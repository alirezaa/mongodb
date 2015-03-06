package mongodbgroup.mongodbId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class App 
{

	   private static	MongoClient mongoClient;

		public static void main(final String[] args) throws Exception  {

		if (args.length == 0) {
		// connect to the local database server
		mongoClient = new MongoClient();
		} else {
		mongoClient = new MongoClient(new MongoClientURI(args[0]));
		}
		// get handle to the "mydb" database
		DB db = mongoClient.getDB("mydb");
		// get a list of the collections in this database and print them out
		Set<String> collectionNames = db.getCollectionNames();
		for (final String s : collectionNames) {
		System.out.println(s);
		}
		
		
		// get a handle to the "test" collection
		DBCollection collection = db.getCollection("test");
		// drop all the data in it
		collection.drop();
	
		
		// make a document and insert it
		BasicDBObject doc = new BasicDBObject("name", "MongoDB")
		.append("type", "database")
		.append("count", 1)
		.append("info", new BasicDBObject("x", 203).append("y", 102));
		collection.insert(doc);
		// get it (since it's the only one in there since we dropped the rest earlier on)
		DBObject myDoc = collection.findOne();
		System.out.println(myDoc);
		// now, lets add lots of little documents to the collection so we can explore queries and cursors
		List<DBObject> documents = new ArrayList<DBObject>();
		for (int i = 0; i < 100; i++) {
		documents.add(new BasicDBObject().append("i", i));
		}
		collection.insert(documents);
		System.out.println("total # of documents after inserting 100 small ones (should be 101) " + collection.getCount());
		// lets get all the documents in the collection and print them out
		DBCursor cursor = collection.find();
		
		
		try {
		while (cursor.hasNext()) {
		System.out.println(cursor.next());
		}
		} finally {
		cursor.close();
		}
		// now use a query to get 1 document out
		cursor = collection.find(new BasicDBObject("i", 71));
		try {
		while (cursor.hasNext()) {
		System.out.println(cursor.next());
		}
		} finally {
		cursor.close();
		}
		// now use a range query to get a larger subset
		cursor = collection.find(new BasicDBObject("i", new BasicDBObject("$gt", 50)));
		try {
		while (cursor.hasNext()) {
		System.out.println(cursor.next());
		}
		} finally {
		cursor.close();
		}
		// range query with multiple constraints
		cursor = collection.find(new BasicDBObject("i", new BasicDBObject("$gt", 20).append("$lte", 30)));
		try {
		while (cursor.hasNext()) {
		System.out.println(cursor.next());
		}
		} finally {
		cursor.close();
		}
		// create an ascending index on the "i" field
		collection.createIndex(new BasicDBObject("i", 1));
		// list the indexes on the collection
		List<DBObject> list = collection.getIndexInfo();
		for (final DBObject o : list) {
		System.out.println(o);
		}
		// release resources
		mongoClient.close();
		}
		}