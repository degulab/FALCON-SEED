/*
 * @(#)CheckMongoURI.java	0.991	2019/02/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package examples.mongo;

import com.mongodb.ConnectionString;

/**
 * MongoDB URI をチェックするためのテスト。
 * 
 * @version 0.991
 * @since 0.991
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class CheckMongoURI
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Main method
	//------------------------------------------------------------

	public static void main(String[] args) {
		System.out.println("<<< Started --- CheckMongoURI >>>");
		try {
			checkMongoConnectionStrings();
		}
		finally {
			System.out.println("<<< Finished --- CheckMongoURI >>>");
			System.out.flush();
			try {
				Thread.sleep(1000);
			} catch (Throwable ex) {}
		}
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static private void checkMongoConnectionStringEquals() {
		System.out.println("### (checkMongoConnectionStrings) Started...");
		
		System.out.println("### (checkMongoConnectionStrings) .....done.");
	}
	
	static private void checkMongoConnectionStrings() {
		System.out.println("### (checkMongoConnectionStrings) Started...");
		
		checkConnectionString("mongodb://user:pass@mhost1:8765");
		checkConnectionString("mongodb://user:pass@mhost1:8765/dbname1");
		checkConnectionString("mongodb://user:pass@mhost1:8765/dbname1/dbcol1");
		checkConnectionString("mongodb://user:pass@mhost1:8765/dbname1.dbcol1");
		
		System.out.println("### (checkMongoConnectionStrings) .....done.");
	}
	
	static private void checkConnectionString(String cs) {
		System.out.println("  @@@ \"" + cs + "\"...");
		try {
			ConnectionString mcs = new ConnectionString(cs);
			printConnectionString(mcs);
		}
		catch (Throwable ex) {
			System.err.println("  Failed to create ConnectionString from String-URI : " + ex);
			ex.printStackTrace();
		}
	}
	
	static public void printConnectionString(ConnectionString mcs) {
		System.out.println("  ConnectionString.getApplicationName()        = " + String.valueOf(mcs.getApplicationName()));
		System.out.println("  ConnectionString.getHosts()                  = " + String.valueOf(mcs.getHosts()));
		System.out.println("  ConnectionString.getCredential()             = " + String.valueOf(mcs.getCredential()));
		System.out.println("  ConnectionString.getUsername()               = " + String.valueOf(mcs.getUsername()));
		System.out.println("  ConnectionString.getPassword()               = " + String.valueOf(mcs.getPassword()));
		System.out.println("  ConnectionString.getDatabase()               = " + String.valueOf(mcs.getDatabase()));
		System.out.println("  ConnectionString.getCollection()             = " + String.valueOf(mcs.getCollection()));
		System.out.println("  ConnectionString.getCompressorList()         = " + String.valueOf(mcs.getCompressorList()));
		System.out.println("  ConnectionString.getConnectTimeout()         = " + String.valueOf(mcs.getConnectTimeout()));
		System.out.println("  ConnectionString.getHeartbeatFrequency()     = " + String.valueOf(mcs.getHeartbeatFrequency()));
		System.out.println("  ConnectionString.getLocalThreshold()         = " + String.valueOf(mcs.getLocalThreshold()));
		System.out.println("  ConnectionString.getRequiredReplicaSetName() = " + String.valueOf(mcs.getRequiredReplicaSetName()));
		System.out.println("  ConnectionString.getServerSelectionTimeout() = " + String.valueOf(mcs.getServerSelectionTimeout()));
		System.out.println("  ConnectionString.getSocketTimeout()          = " + String.valueOf(mcs.getSocketTimeout()));
		System.out.println("  ConnectionString.getSslEnabled()             = " + String.valueOf(mcs.getSslEnabled()));
		System.out.println("  ConnectionString.getStreamType()             = " + String.valueOf(mcs.getStreamType()));
		System.out.println("  ConnectionString.getConnectionString()       = " + String.valueOf(mcs.getConnectionString()));
		System.out.println("  ConnectionString.getURI()                    = " + String.valueOf(mcs.getURI()));
		System.out.println("  ConnectionString.toString()                  = " + String.valueOf(mcs.toString()));
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
