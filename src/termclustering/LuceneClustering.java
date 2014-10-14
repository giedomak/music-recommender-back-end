package termclustering;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

public class LuceneClustering {

	public LuceneClustering() throws SQLException, IOException, ParseException {
		// TODO Auto-generated constructor stub
		
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_1);
		Directory index = new RAMDirectory();

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);

		Connection connection;
		connection = DriverManager.getConnection("jdbc:mysql://178.62.207.179/2id26?"
				+ "user=root&password=Aarde-Rond-1");
		
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT id, text FROM lyric LIMIT 1000");
		
		IndexWriter w = new IndexWriter(index, config);
		
		while (resultSet.next()) {
			int lyricId = resultSet.getInt("id");
			String text = resultSet.getString("text").toLowerCase();
			addLyric(w, text, lyricId);
			System.out.println("Insert id " + lyricId + " text: " + text.length());
		}
		//addLyric(w, "Lucene in Action", "193398817");
		//addLyric(w, "Lucene for Dummies", "55320055Z");
		//addLyric(w, "Managing Gigabytes", "55063554A");
		//addLyric(w, "The Art of Computer Science", "9900333X");
		w.close();
		
	    // 2. query
	    String querystr = "love";

	    // the "title" arg specifies the default field to use
	    // when no field is explicitly specified in the query.
	    Query q = new QueryParser(Version.LUCENE_4_10_1, "lyric", analyzer).parse(querystr);

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

	public static void main(String[] args) throws SQLException, IOException, ParseException {
		// TODO Auto-generated method stub
		new LuceneClustering();
	}
	
	private static void addLyric(IndexWriter w, String text, int lyricId) throws IOException {
		  Document doc = new Document();
		  doc.add(new TextField("lyric", text, Field.Store.YES));
		  doc.add(new IntField("id", lyricId, Field.Store.YES));
		  w.addDocument(doc);
	}
}
