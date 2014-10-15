package termclustering;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;

public class LyricClustering {
	final ArrayList<Document> documents = new ArrayList<Document>();
	
	public LyricClustering() throws SQLException {
		
		org.apache.log4j.BasicConfigurator.configure();
		
		Connection connection;
		connection = DriverManager.getConnection("jdbc:mysql://178.62.207.179/2id26?"
				+ "user=root&password=Aarde-Rond-1");
		
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT id, text FROM lyric WHERE source='wikia' LIMIT 1000");
		
		while (resultSet.next()) {
			int lyricId = resultSet.getInt("id");
			String text = resultSet.getString("text").toLowerCase();
			Document doc = new Document(Integer.toString(lyricId), text);
			documents.add(doc);
			System.out.println("Insert id: " + lyricId + ", text.length(): " + text.length());
		}
		
		/* A controller to manage the processing pipeline. */
		final Controller controller = ControllerFactory.createSimple();
		 
		/*
		* Perform clustering by topic using the Lingo algorithm. Lingo can
		* take advantage of the original query, so we provide it along with the documents.
		*/
		final ProcessingResult byTopicClusters = controller.process(documents, null,
		LingoClusteringAlgorithm.class);
		final List<Cluster> clustersByTopic = byTopicClusters.getClusters();
		
		ConsoleFormatter.displayResults(byTopicClusters);
		
		for (Cluster cluster : clustersByTopic) {
			//cluster.
		}
	}

	public static void main(String[] args) throws SQLException {
		new LyricClustering();
	}

}
