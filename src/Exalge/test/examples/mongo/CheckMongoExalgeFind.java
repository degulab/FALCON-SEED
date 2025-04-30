package examples.mongo;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;

import redundantalge.util.UUIDGenerator;

public class CheckMongoExalgeFind
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private String MONGO_COL_EXALGE = "Exalge";
	
	/** PING コマンド **/
	static protected final BasicDBObject	_cmdPing = new BasicDBObject("ping", "1");

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Entry method
	//------------------------------------------------------------

	public static void main(String[] args) {
		// Create connection string
		String cs = "mongodb://";
		//--- user
		cs += MongoConnectionConstants.MONGO_AUTH_USER;
		//--- password
		cs = cs + ":" + MongoConnectionConstants.MONGO_AUTH_PASS + "@";
		//--- host
		cs += MongoConnectionConstants.MONGO_SERVER_HOST;
		//--- port
		cs = cs + ":" + String.valueOf(MongoConnectionConstants.MONGO_SERVER_PORT);
		//--- target DB
		cs = cs + "/" + "ExalgeTest";
		//--- target Collection
		cs = cs + "." + "Exalge";
		//--- auth DB
		cs = cs + "?authSource=" + MongoConnectionConstants.MONGO_AUTH_DB;
		ConnectionString mcs = new ConnectionString(cs);
		
		MongoClient mclient = null;
		MongoDatabase mdb = null;
		try {
			// connection
			mclient = MongoClients.create(mcs);
			mdb = mclient.getDatabase(MongoConnectionConstants.MONGO_DB_NAME);
			MongoCollection<Document> mcol = mdb.getCollection(MONGO_COL_EXALGE);
			
			// find without _id
			findWithoutId(mdb, mcol);
			
			// Duplicate collection
			MongoCollection<Document> dupcol = duplicateCollection(mdb, mcol);
			findAll(mdb, dupcol);
			
			// Rename collection
			dupcol = renameCollection(mdb, dupcol, "TempExalge");
			findAll(mdb, dupcol);
			
			// delete collection
			//deleteCollection(mdb, dupcol);
		}
		finally {
			System.out.flush();
			System.err.flush();
			if (mclient != null) {
				//--- wait
				try {
					Thread.sleep(1000L);
				} catch (Throwable ignoreEx) {}
				//--- close
				try {
					System.out.println("@@@ close connection...");
					mclient.close();
					System.out.println(".....done.");
				}
				catch (Throwable ignoreEx) {}
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public void printNanoDuration(long stime, long etime) {
		double ddur = (double)(etime - stime) / 1000000000.0;
		System.out.printf("=== Duration: %g", ddur).println();
	}
	
	static public void findAll(MongoDatabase mdb, MongoCollection<? extends Document> mcol) {
		System.out.println("@@@ find({})...");
		long stime = System.nanoTime();
		Consumer<Document> printBlock = new Consumer<Document>() {
			@Override
			public void accept(Document t) {
				System.out.println("  " + t.toJson());
			}
		};
		mcol.find().forEach(printBlock);
		long etime = System.nanoTime();
		printNanoDuration(stime, etime);
		System.out.println(".....done.");
	}
	
	static public void findWithoutId(MongoDatabase mdb, MongoCollection<? extends Document> mcol) {
		System.out.println("@@@ find({},{\"_id\" : 0})...");
		long stime = System.nanoTime();
		Consumer<Document> printBlock = new Consumer<Document>() {
			@Override
			public void accept(Document t) {
				System.out.println("  " + t.toJson());
			}
		};
		mcol.find().projection(new Document("_id", 0)).forEach(printBlock);
		long etime = System.nanoTime();
		printNanoDuration(stime, etime);
		System.out.println(".....done.");
	}
	
	static public MongoCollection<Document> duplicateCollection(MongoDatabase mdb, MongoCollection<? extends Document> mcol) {
		//String newColName = MongoUtil.makeUniqueCollectionName();
		String newColName = UUIDGenerator.genHexUUID();
		System.out.println("@@@ duplicate collection [" + mcol.getNamespace().toString() + "] -> [" + newColName + "]...");
		Consumer<Document> printBlock = new Consumer<Document>() {
			@Override
			public void accept(Document t) {
				System.out.println("  " + t.toJson());
			}
		};
		ArrayList<Bson> list = new ArrayList<Bson>();
		list.add(Aggregates.project(new Document("_id", 0)));
		list.add(Aggregates.out(newColName));
		long stime = System.nanoTime();
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = mcol.aggregate(list).iterator();
			while (cursor.hasNext()) {
				System.out.println("  " + cursor.next().toJson());
			}
		}
		finally {
			cursor.close();
		}
		//mcol.aggregate(Arrays.asList(
		//		Aggregates.out(newColName)
		//));
		long etime = System.nanoTime();
		printNanoDuration(stime, etime);
		System.out.println(".....done.");
		return mdb.getCollection(newColName);
	}
	
	static public MongoCollection<Document> renameCollection(MongoDatabase mdb, MongoCollection<? extends Document> mcol, String newName) {
		System.out.println("@@@ rename collection " + mcol.getNamespace().toString() + " -> [" + newName + "]...");
		long stime = System.nanoTime();
		try {
			MongoNamespace newNamespace = new MongoNamespace(mdb.getName(), newName);
			mcol.renameCollection(newNamespace);
		} catch (Throwable ex) {
			throw new RuntimeException("Failed to rename: ", ex);
		}
		long etime = System.nanoTime();
		printNanoDuration(stime, etime);
		System.out.println(".....done.");
		return mdb.getCollection(newName);
	}
	
	static public void deleteCollection(MongoDatabase mdb, MongoCollection<? extends Document> mcol) {
		System.out.println("@@@ delete collection " + mcol.getNamespace().toString() + "...");
		long stime = System.nanoTime();
		mcol.drop();
		long etime = System.nanoTime();
		printNanoDuration(stime, etime);
		System.out.println(".....done.");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
