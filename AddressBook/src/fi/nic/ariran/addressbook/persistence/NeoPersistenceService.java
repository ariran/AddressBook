package fi.nic.ariran.addressbook.persistence;

import java.util.Collections;
import java.util.LinkedList;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import fi.nic.ariran.addressbook.domain.AddressBookItem;

public class NeoPersistenceService implements PersistenceService {
	
	private GraphDatabaseService graphDb = null;
	
	public NeoPersistenceService(final String path) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("/var/neo4j");
		registerShutdownHook( graphDb );
	}
	
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
				System.out.println("GraphDB was shut down on JVM shutdown.");
			}
		} );
	}
	
	@Override
	public void close() {
		graphDb.shutdown();
		System.out.println("GraphDB was shut down.");
	}

	@Override
	public void insertNewItem(AddressBookItem bean) {

		try ( Transaction tx = graphDb.beginTx() )
		{
			Node addressBookItemNode = graphDb.createNode();
			addressBookItemNode.addLabel(AddressBookLabels.ADDRESSBOOK_ITEM);
			addressBookItemNode.setProperty("uuid", bean.getInternalId());
			addressBookItemNode.setProperty("lastName", bean.getLastName());
			addressBookItemNode.setProperty("firstNames", bean.getFirstNames());
			addressBookItemNode.setProperty("extraInfo", bean.getExtraInfo());

		    tx.success();
		    System.out.println("Successfully inserted: " + bean.getLastName() + " " + bean.getFirstNames());
		}
	}

	@Override
	public Iterable<AddressBookItem> getAllItems() {
		GlobalGraphOperations globalOp = GlobalGraphOperations.at(graphDb);
		LinkedList<AddressBookItem> itemList = new LinkedList<AddressBookItem>();
		
		try ( Transaction tx = graphDb.beginTx() )
		{
			ResourceIterable<Node> allItems = globalOp
					.getAllNodesWithLabel(AddressBookLabels.ADDRESSBOOK_ITEM);
			for (Node node : allItems) {
				AddressBookItem item = new AddressBookItem(
						(String) node.getProperty("uuid"),
						(String) node.getProperty("lastName"),
						(String) node.getProperty("firstNames"));
				item.setExtraInfo((String) node.getProperty("extraInfo")); 
				itemList.add(item);
			}
			tx.success();
		}
		
		Collections.sort(itemList);
		return itemList;
	}

}
