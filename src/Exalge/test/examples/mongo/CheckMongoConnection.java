package examples.mongo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ServerSettings;
import com.mongodb.connection.ServerSettings.Builder;
import com.mongodb.event.ServerClosedEvent;
import com.mongodb.event.ServerDescriptionChangedEvent;
import com.mongodb.event.ServerListener;
import com.mongodb.event.ServerOpeningEvent;

public class CheckMongoConnection
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** PING コマンド **/
	static protected final BasicDBObject	_cmdPing = new BasicDBObject("ping", "1");

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/*
	 * テストまとめ：
	 * MongoClients.create() は、接続を行わないので、例外は出力しない。
	 * MongoClient#getDatabase() ならびに MongoDatabase#getCollection() は、存在しないDB名やコレクション名を指定してもエラーとはならない。
	 * コマンド実行時にデータベースに接続しに行くので、処理が行わえない場合に例外がスローされる。
	 * サーバーの状態は、ServerListener を登録することにより取得できる。サーバー状態のモニタリングは別スレッドが自動的に生成され監視が実行される。
	 * ServerListener#serverDescriptionChanged の引数である ServerDescriptionChangedEvent から、
	 * getNewDescrioption() を取得し、そのインスタンスに対して isOk() メソッドを呼び出すことで、サーバーが有効かどうかは判定できる。
	 * また、その時点での例外も getException() メソッドの呼び出しで取得できる。
	 */
	
	static private class MongoStatusListener implements ServerListener
	{
		@Override
		public void serverOpening(ServerOpeningEvent event) {
			System.out.flush();
			System.err.flush();
			System.out.println("### Called serverOpening(event):");
			System.out.println("event=" + String.valueOf(event));
			System.out.flush();
		}

		@Override
		public void serverClosed(ServerClosedEvent event) {
			System.out.flush();
			System.err.flush();
			System.out.println("### Called serverClosed(event):");
			System.out.println("event=" + String.valueOf(event));
			System.out.flush();
		}

		@Override
		public void serverDescriptionChanged(ServerDescriptionChangedEvent event) {
			System.out.flush();
			System.err.flush();
			System.out.println("### Called serverDescriptionChanged(event):");
			System.out.println("event=" + String.valueOf(event));
			System.out.println("event.getNewDescription().isOk()="+String.valueOf(event.getNewDescription().isOk()));
			System.out.println("event.getNewDescription().getException()="+String.valueOf(event.getNewDescription().getException()));
			System.out.println("event.getNewDescription().equals(event.getPreviousDescription())="+String.valueOf(event.getNewDescription().equals(event.getPreviousDescription())));
			System.out.flush();
		}
	}
	static private final MongoStatusListener	_serverListener = new MongoStatusListener();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Entry method
	//------------------------------------------------------------

	public static void main(String[] args) {
		// Create connection string
		ConnectionString mcs = makeConnectionString();
		
		// Check MongoDB
		MongoClient mclient = null;
		try {
			mclient = doCheckMongoDB(mcs);
		}
		catch (Throwable ex) {
			System.out.flush();
			System.err.flush();
			System.err.println("@@@ Caught unknown exception:");
			ex.printStackTrace();
			System.err.flush();
		}
		finally {
			if (mclient != null) {
				// wait
				try {
					Thread.sleep(1000L);
				} catch (Throwable ignoreEx) {}
				// close
				System.out.flush();
				System.err.flush();
				System.out.println("@@@ Close connection from \"" + mcs.toString() + "\"...");
				try {
					mclient.close();
					System.out.println(".....done.");
					System.out.flush();
				} catch (Throwable ex) {
					System.err.flush();
					ex.printStackTrace();
					System.err.flush();
				}
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static private MongoClient doCheckMongoDB(final ConnectionString mcs) {
		// connect
		MongoClient mclient = connect(mcs);
		if (mclient == null)
			return null;
		
		// wait
		try {
			Thread.sleep(1000L);
		} catch (Throwable ignoreEx) {}
		
		// print databases
		printDatabases(mclient);
		
		// wait
		try {
			Thread.sleep(1000L);
		} catch (Throwable ignoreEx) {}
		
		// get unknown database
		MongoDatabase mdb;
		mdb = getDatabase(mclient, "Hoge");
		// wait
		try {
			Thread.sleep(1000L);
		} catch (Throwable ignoreEx) {}
		// check connection
		checkConnection(mdb);
		
		// get ExalgeTest database
		mdb = getDatabase(mclient, MongoConnectionConstants.MONGO_DB_NAME);
		if (mdb == null)
			return mclient;
		
		// wait
		try {
			Thread.sleep(1000L);
		} catch (Throwable ignoreEx) {}
		
		// check connection
		checkConnection(mdb);
		
		// wait
		try {
			Thread.sleep(1000L);
		} catch (Throwable ignoreEx) {}
		
		// print collections
		printCollections(mdb);
		
		// wait
		try {
			Thread.sleep(1000L);
		} catch (Throwable ignoreEx) {}
		
		// get unknown collection
		MongoCollection<Document> mcol;
		/*
		mcol = getCollection(mdb, "Unknown");
		if (mcol != null) {
			printCollections(mdb);
			// コレクションのインスタンスは作成されるが、この時点ではコレクションの実体は作成されていない。
			throw new RuntimeException("database[" + MONGO_DB_NAME + "].collection[Unknown] was created!");
		}
		/**/
		
		// get Exalge collection
		mcol = getCollection(mdb, "Exalge");
		if (mcol == null)
			return mclient;
		
		// wait
		try {
			Thread.sleep(1000L);
		} catch (Throwable ignoreEx) {}
		
		// check input
		System.out.flush();
		System.err.flush();
		System.out.print("@@@ Input: ");
		System.out.flush();
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		try {
			br.readLine();
		} catch (Throwable ex) {
			System.out.println();
			System.out.flush();
			System.err.flush();
			ex.printStackTrace();
			System.err.flush();
		}
		
		// wait
		try {
			Thread.sleep(1000L);
		} catch (Throwable ignoreEx) {}
		
		// check connection
		checkConnection(mdb);
		
		// finish
		return mclient;
	}
	
	static private ConnectionString makeConnectionString() {
		System.out.flush();
		System.err.flush();
		System.out.println("@@@ makeConnectionString()...");
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
		System.out.println("ConnectionString mcs = new ConnectionString(\"" + cs + "\");");
		ConnectionString mcs = new ConnectionString(cs);
		System.out.println("mcs.getHosts()   =" + String.valueOf(mcs.getHosts()));
		System.out.println("mcs.getDatabase()=" + String.valueOf(mcs.getDatabase()));
		System.out.println("mcs.getCollection()=" + String.valueOf(mcs.getCollection()));
		System.out.println("mcs.getCredential().getSource()=" + String.valueOf(mcs.getCredential().getSource()));
		System.out.println("mcs.getUsername()=" + String.valueOf(mcs.getUsername()));
		System.out.println("mcs.getPassword()=" + String.valueOf(mcs.getPassword()));
		System.out.flush();
		return mcs;
	}
	
	static private MongoClient connect(final ConnectionString mcs) {
		System.out.flush();
		System.err.flush();
		System.out.println("@@@ Connecting to [" + mcs.toString() + "]...");
		try {
			MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder();
			settingsBuilder.applyConnectionString(mcs);
			settingsBuilder.applyToServerSettings(new Block<ServerSettings.Builder>() {
				@Override
				public void apply(Builder t) {
					t.addServerListener(_serverListener);
				}
			});
			MongoClient mclient = MongoClients.create(settingsBuilder.build());
			System.out.println(".....done!");
			System.out.flush();
			return mclient;
		}
		catch (Throwable ex) {
			System.out.flush();
			System.err.flush();
			System.err.println("@@@ Caught exception in connect():");
			ex.printStackTrace();
			System.err.flush();
			return null;
		}
	}
	
	static private MongoDatabase getDatabase(final MongoClient mclient, final String dbName) {
		System.out.flush();
		System.err.flush();
		System.out.println("@@@ Get database[" + String.valueOf(dbName) + "]...");
		try {
			MongoDatabase mdb = mclient.getDatabase(dbName);
			System.out.println(".....done.");
			System.out.flush();
			return mdb;
		}
		catch (Throwable ex) {
			System.out.flush();
			System.err.flush();
			System.err.println("@@@ Caught exception in getDatabase(mclient, " + dbName + "):");
			ex.printStackTrace();
			System.err.flush();
			return null;
		}
	}
	
	static private boolean checkConnection(final MongoDatabase mdb) {
		System.out.flush();
		System.err.flush();
		System.out.println("@@@ Ping to Database[" + mdb.getName() + "]...");
		try {
			Document doc = mdb.runCommand(_cmdPing);
			System.out.println(doc.toJson());
			System.out.println(".....done:");
			System.out.flush();
			return true;
		}
		catch (Throwable ex) {
			System.out.flush();
			System.err.flush();
			System.err.println("@@@ Caught exception in checkConnection:");
			ex.printStackTrace();
			System.err.flush();
			return false;
		}
	}
	
	static private boolean printDatabases(final MongoClient mclient) {
		System.out.flush();
		System.err.flush();
		System.out.println("@@@ Print databases...");
		try {
			List<String> list = new ArrayList<String>();
			MongoCursor<String> cursor = mclient.listDatabaseNames().iterator();
			try {
				for (; cursor.hasNext(); ) {
					list.add(cursor.next());
				}
			}
			finally {
				cursor.close();
			}
			System.out.println("  database names: (" + String.valueOf(list.size()) + ")");
			for (String name : list) {
				System.out.println("    " + name);
			}
			System.out.flush();
			return true;
		}
		catch (Throwable ex) {
			System.out.flush();
			System.err.flush();
			System.err.println("@@@ Caught exception in printDatabases:");
			ex.printStackTrace();
			System.err.flush();
			return false;
		}
	}
	
	static private boolean printCollections(final MongoDatabase mdb) {
		System.out.flush();
		System.err.flush();
		System.out.println("@@@ Print collections in database[" + mdb.getName() + "]...");
		try {
			MongoCursor<String> cursor = mdb.listCollectionNames().iterator();
			try {
				List<String> list = new ArrayList<String>();
				for (; cursor.hasNext(); ) {
					list.add(cursor.next());
				}
				System.out.println("  collections: (" + String.valueOf(list.size()) + ")");
				for (String name : list) {
					System.out.println("    " + name);
				}
				System.out.flush();
			}
			finally {
				cursor.close();
			}
			return true;
		}
		catch (Throwable ex) {
			System.out.flush();
			System.err.flush();
			System.err.println("@@@ Caught exception in printCollections:");
			ex.printStackTrace();
			System.err.flush();
			return false;
		}
	}
	
	static private MongoCollection<Document> getCollection(final MongoDatabase mdb, final String cName) {
		System.out.flush();
		System.err.flush();
		System.out.println("@@@ Get collection[" + cName + "] from database[" + mdb.getName() + "]...");
		try {
			MongoCollection<Document> mc = mdb.getCollection(cName);
			System.out.println(".....done.");
			System.out.flush();
			return mc;
		}
		catch (Throwable ex) {
			System.out.flush();
			System.err.flush();
			System.err.println("@@@ Caught exception in getCollection(" + mdb.getName() + ", " + cName + "):");
			ex.printStackTrace();
			System.err.flush();
			return null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
