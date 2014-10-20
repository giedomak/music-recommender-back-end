package clustering;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

// cluster and labeling songs on title with the lingo algorithm from carrot2
public class TitleClustering {
	final ArrayList<Document> documents = new ArrayList<Document>();
	
	public TitleClustering() throws SQLException {
		
		org.apache.log4j.BasicConfigurator.configure();
		// Get connection with the database 
		Connection connection;
		connection = DriverManager.getConnection("jdbc:mysql://178.62.207.179/2id26?"
				+ "user=root&password=Aarde-Rond-1");
		
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT id, title FROM song");
		
		PreparedStatement insertClusterStatement = connection
		          .prepareStatement("INSERT INTO titlecluster VALUES (?, ?, null)");
		
		PreparedStatement insertClusterMemberStatement = connection
		          .prepareStatement("INSERT INTO titleclustermember VALUES (?, ?)");
		// iterate overall songs
		while (resultSet.next()) {
			int id = resultSet.getInt("id");
			String title = resultSet.getString("title").toLowerCase();
			Document doc = new Document(Integer.toString(id), title);
			documents.add(doc);
			System.out.println("Insert id: " + id + "," + title + " ");
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
		// Print results to the console using existing ConsoleFormatter class
		ConsoleFormatter.displayResults(byTopicClusters);
		
		// save each title cluster and label in the My SQL database 
		for (Cluster cluster : clustersByTopic) {
			System.out.println("Label: " + cluster.getLabel());
			System.out.println("Id: " + cluster.getId());
			
			insertClusterStatement.setInt(1, cluster.getId());
			insertClusterStatement.setString(2, cluster.getLabel());
			insertClusterStatement.execute();
			
			for (Document member : cluster.getDocuments()) {
				insertClusterMemberStatement.setInt(1, Integer.parseInt(member.getTitle()));
				insertClusterMemberStatement.setInt(2, cluster.getId());
				insertClusterMemberStatement.addBatch();
			}
			insertClusterMemberStatement.executeBatch();
		}
	}

	public static void main(String[] args) throws SQLException {
		new TitleClustering();
	}

}
