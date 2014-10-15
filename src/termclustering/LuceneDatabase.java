package termclustering;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.ProcessingComponentConfiguration;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.source.lucene.LuceneDocumentSource;
import org.carrot2.source.lucene.LuceneDocumentSourceDescriptor;
import org.carrot2.source.lucene.SimpleFieldMapperDescriptor;

import com.google.common.collect.Maps;

public class LuceneDatabase {
	// based on http://www.lucenetutorial.com/lucene-in-5-minutes.html
	// based on https://github.com/carrot2/carrot2/blob/master/applications/carrot2-examples/examples/org/carrot2/examples/clustering/ClusteringDataFromLucene.java
	
	public Directory index;
	public IndexWriterConfig config;
	public StandardAnalyzer analyzer;
	
	public LuceneDatabase() throws SQLException, IOException, ParseException {	
		analyzer = new StandardAnalyzer();
		index = new RAMDirectory();
		config = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);
	}

	public static void main(String[] args) throws SQLException, IOException, ParseException {
		LuceneDatabase database = new LuceneDatabase();
	
		database.fillFromMysql();
		
	    // Search query
	    String querystr = "love";

	    Query q = new QueryParser("lyric", database.analyzer).parse(querystr);
	    //database.search(q);
	    
	    org.apache.log4j.BasicConfigurator.configure();
	    
	    database.clusterLyrics();
	}

	private void clusterLyrics() {
		final Controller controller = ControllerFactory.createPooling();
		
        final Map<String, Object> luceneGlobalAttributes = new HashMap<String, Object>();

        LuceneDocumentSourceDescriptor
            .attributeBuilder(luceneGlobalAttributes)
            .directory(index)
            .analyzer(analyzer);

        /*
         * Specify fields providing data inside your Lucene index.
         */
        SimpleFieldMapperDescriptor
            .attributeBuilder(luceneGlobalAttributes)
            .titleField("id")
            //.titleField("title") // TODO: add to index
            .contentField("lyric");
            //.searchFields(Arrays.asList(new String [] {"titleField", "fullContent"}));
		
        controller.init(new HashMap<String, Object>(),
            new ProcessingComponentConfiguration(LuceneDocumentSource.class, "lucene",
                luceneGlobalAttributes));

        /*
         * Perform processing.
         */
        //String query = "mining";
        String query = "me";
        final Map<String, Object> processingAttributes = Maps.newHashMap();
        CommonAttributesDescriptor.attributeBuilder(processingAttributes).query(query).results(1000);

        /*
         * We need to refer to the Lucene component by its identifier we set during
         * initialization. As we've not assigned any identifier to the
         * LingoClusteringAlgorithm we want to use, we can its fully qualified class name.
         */
        ProcessingResult process = controller.process(processingAttributes, "lucene",
            LingoClusteringAlgorithm.class);
        
        ConsoleFormatter.displayResults(process);
	}

	public void search(Query q)
			throws IOException {
		// 3. search
	    int hitsPerPage = 10;
	    IndexReader reader = DirectoryReader.open(index);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
	    searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    
	    // 4. display results
	    System.out.println("Found " + hits.length + " hits.");
	    for(int i=0;i<hits.length;++i) {
	      int docId = hits[i].doc;
	      Document d = searcher.doc(docId);
	      System.out.println((i + 1) + ". " + d.get("id") + "\t" + d.get("lyric"));
	    }

	    // reader can only be closed when there
	    // is no need to access the documents any more.
	    reader.close();
	}

	public void fillFromMysql()
			throws SQLException, IOException {
		Connection connection;
		connection = DriverManager.getConnection("jdbc:mysql://178.62.207.179/2id26?"
				+ "user=root&password=Aarde-Rond-1");
		
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT id, text FROM lyric WHERE source='wikia' LIMIT 1000");
		
		IndexWriter w = new IndexWriter(index, config);
		
		while (resultSet.next()) {
			int lyricId = resultSet.getInt("id");
			String text = resultSet.getString("text").toLowerCase();
			addLyric(w, text, lyricId);
			System.out.println("Insert id: " + lyricId + ", text.length(): " + text.length());
		}
		w.close();
	}
	
	private static void addLyric(IndexWriter w, String text, int lyricId) throws IOException {
		  Document doc = new Document();
		  doc.add(new TextField("lyric", text, Field.Store.YES));
		  doc.add(new IntField("id", lyricId, Field.Store.YES));
		  w.addDocument(doc);
	}
}
