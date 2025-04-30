package examples.mongo;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.ConnectionString;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class CheckBasicMongClient
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

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
		System.out.println("mcs.getHosts()   =" + String.valueOf(mcs.getHosts()));
		System.out.println("mcs.getDatabase()=" + String.valueOf(mcs.getDatabase()));
		System.out.println("mcs.getCollection()=" + String.valueOf(mcs.getCollection()));
		System.out.println("mcs.getCredential().getSource()=" + String.valueOf(mcs.getCredential().getSource()));
		System.out.println("mcs.getUsername()=" + String.valueOf(mcs.getUsername()));
		System.out.println("mcs.getPassword()=" + String.valueOf(mcs.getPassword()));
		
		// Connection with authentication
		MongoClient mclient = null;
		MongoDatabase mdb = null;
		try {
			//--- connect
			System.out.println("Connecting to [" + mcs.toString() + "]...");
			mclient = MongoClients.create(mcs);
			System.out.println(".....done.");
			//--- select database
			System.out.println("Get database with [" + MongoConnectionConstants.MONGO_DB_NAME + "]...");
			mdb = mclient.getDatabase(MongoConnectionConstants.MONGO_DB_NAME);
			System.out.println(".....done.");
			//--- Print collection
			MongoCollection<Document> mcol = mdb.getCollection("Exalge");
			Bson filter = Filters.eq("base.name", "現金");
			FindIterable<Document> fi = mcol.find(filter);
			MongoCursor<Document> cursor = fi.iterator();
			System.out.println("Extract by \"base.name\" with \"現金\" in Collection[Exalge]...");
			try {
				while (cursor.hasNext()) {
					System.out.println(cursor.next().toJson());
				}
			}
			finally {
				cursor.close();
			}
			long countAll = mcol.countDocuments();
			long countCursor = mcol.countDocuments(filter);
			System.out.println(".....done(" + String.valueOf(countCursor) + "/" + String.valueOf(countAll) + ").");
			//--- Print collection
			mcol = mdb.getCollection("ExalgeArray");
			filter = Filters.elemMatch("exalge", Filters.eq("base.name", "現金"));
			fi = mcol.find(filter);
			cursor = fi.iterator();
			System.out.println("Extract by \"exalge.base.name\" with \"現金\" in Collection[ExalgeArray]...");
			try {
				while (cursor.hasNext()) {
					System.out.println(cursor.next().toJson());
				}
			}
			finally {
				cursor.close();
			}
			countAll = mcol.countDocuments();
			countCursor = mcol.countDocuments(filter);
			System.out.println(".....done(" + String.valueOf(countCursor) + "/" + String.valueOf(countAll) + ").");
			// MapReduce
			System.out.println("MapReduce for Sum in Collection[Exalge]...");
			mcol = mdb.getCollection("Exalge");
			//--- Map function
			String map = "function() {"+
					"  if ( this.value != null ) {"+
					"    emit( {base_hat:this.base.hat, base_name:this.base.name, base_unit:this.base.unit}, this.value);"+
					"  }"+
					"};";
			//--- Reduce function
			String reduce = "function(key,values) {"+
					"  var sumval = 0;"+
					"  values.forEach(function(value){"+
					"    sumval += value;"+
					"  });"+
					"  return sumval;"+
					"};";
			//--- Finalize
			String project = "function(key, reducedVal) {"+
					"  return {"+
					"    \"base\" : {"+
					"      \"hat\" : key.base_hat,"+
					"      \"name\" : key.base_name,"+
					"      \"unit\" : key.base_unit,"+
					"      \"time\" : \"\","+
					"      \"subject\" : \"\""+
					"    },"+
					"    \"value\" : reducedVal"+
					"  };"+
					"};";
			//--- Make command
			//MapReduceCommand cmd = new MapReduceCommand(mcol, map, reduce, null, MapReduceCommand.OutputType.INLINE, null);
			//---> Legacy driver functions
			MapReduceIterable<Document> mi = mcol.mapReduce(map, reduce);
			mi = mi.finalizeFunction(project);
			countCursor = 0L;
			cursor = mi.iterator();
			try {
				while (cursor.hasNext()) {
					System.out.println(cursor.next().toJson());
					countCursor++;
				}
			}
			finally {
				cursor.close();
			}
			countAll = mcol.countDocuments();
			System.out.println(".....done(" + String.valueOf(countCursor) + "/" + String.valueOf(countAll) + ").");
		}
		finally {
			//--- close connection
			if (mclient != null) {
				// Close database
				System.out.println("Close Connection(" + mcs.toString() + ")...");
				mclient.close();
				System.out.println(".....done.");
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
