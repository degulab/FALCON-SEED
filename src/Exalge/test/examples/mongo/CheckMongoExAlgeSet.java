package examples.mongo;

import java.math.BigDecimal;

import exalge2.ExBase;
import exalge2.Exalge;
import exalge2.db.mongo.MongoExAlgeSet;
import examples.mongo.CheckExalgeEquals.ExalgeElem;
import redundantalge.db.mongo.MongoServerURI;
import redundantalge.db.mongo.MongoSession;

public class CheckMongoExAlgeSet
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private String MONGO_COL_EXALGESET1 = "TestMongoExAlgeSet1";
	static private String MONGO_COL_EXALGESET2 = "TestMongoExAlgeSet2";
	
	static private String MONGO_COL_EXBASESET1 = "TestResultExBaseSet1";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static public final ExalgeElem[] test_alges = new ExalgeElem[] {
		new ExalgeElem(new ExBase("基底1", ExBase.NO_HAT, "円", "", ""), new BigDecimal("10")),	
		new ExalgeElem(new ExBase("基底2", ExBase.NO_HAT, "円", "", ""), new BigDecimal("20")),	
		new ExalgeElem(new ExBase("基底3", ExBase.NO_HAT, "円", "", ""),	new BigDecimal("0.0100")),
		new ExalgeElem(new ExBase("基底4", ExBase.NO_HAT, "円", "", ""),	new BigDecimal("40")),
		new ExalgeElem(new ExBase("基底5", ExBase.NO_HAT, "円", "", ""),	new BigDecimal("50")),
		new ExalgeElem(new ExBase("基底6", ExBase.NO_HAT, "円", "", ""),	new BigDecimal("0.000")),
		new ExalgeElem(new ExBase("基底7", ExBase.NO_HAT, "円", "", ""),	BigDecimal.ZERO),
		new ExalgeElem(new ExBase("基底1", ExBase.HAT, "円", "", ""),	new BigDecimal("60")),
		new ExalgeElem(new ExBase("基底2", ExBase.NO_HAT, "円", "", ""),	new BigDecimal("70")),
		new ExalgeElem(new ExBase("基底3", ExBase.HAT, "円", "", ""),	new BigDecimal("0.100")),
		new ExalgeElem(new ExBase("基底4", ExBase.NO_HAT, "円", "", ""),	null),
		new ExalgeElem(new ExBase("基底5", ExBase.HAT, "円", "", ""),	null),
		new ExalgeElem(new ExBase("基底6", ExBase.HAT, "円", "", ""),	new BigDecimal("0.000000")),
	};

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Entry method
	//------------------------------------------------------------

	public static void main(String[] args) {
		MongoSession session = null;
		try {
			// create session
			session = createSession();
			
			// setupExAlgeSetCollection
			setupExAlgeSetCollection(session);
		}
		finally {
			System.out.flush();
			System.err.flush();
			if (session != null) {
				//--- wait
				try {
					Thread.sleep(1000L);
				} catch (Throwable ignoreEx) {}
				System.out.println("@@@ Closing [" + session.getStorageUriString() + "]...");
				MongoSession.cleanupAllSessions();
				System.out.println(".....done.");
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public MongoSession createSession() {
		System.out.println("@@@ Create MongoSession with MongoServerURI...");
		long stime = System.nanoTime();
		MongoSession session = null;
		//--- make URI
		MongoServerURI uri = new MongoServerURI(MongoConnectionConstants.MONGO_SERVER_HOST,
												MongoConnectionConstants.MONGO_SERVER_PORT,
												MongoConnectionConstants.MONGO_AUTH_DB,
												MongoConnectionConstants.MONGO_AUTH_USER,
												MongoConnectionConstants.MONGO_AUTH_PASS,
												MongoConnectionConstants.MONGO_DB_NAME);
		session = MongoSession.getOrNewSession(uri);
		//session = new MongoExalgeSession(uri);
		long etime = System.nanoTime();
		CheckMongoExalgeFind.printNanoDuration(stime, etime);
		System.out.println(".....done!");
		return session;
	}
	
	static public MongoExAlgeSet setupExAlgeSetCollection(MongoSession session) {
		System.out.println("@@@ Create temporary collection for MongoExAlgeSet...");
		long numSrc = test_alges.length;
		long numDst = 0L;
		long numElms = 0L;
		long stime = System.nanoTime();
		//--- create instance
		MongoExAlgeSet newSet = new MongoExAlgeSet(session);
		//--- add data
		Exalge newAlge = null;
		for (int i = 0; i < test_alges.length;) {
			newAlge = new Exalge();
			newAlge.add(test_alges[i].base, test_alges[i].value);
			numDst++;
			i++;
			//---
			if (i >= test_alges.length) {
				break;
			}
			newAlge.add(test_alges[i].base, test_alges[i].value);
			numDst++;
			i++;
			//---
			if (i >= test_alges.length)
				break;
			newAlge.add(test_alges[i].base, test_alges[i].value);
			numDst++;
			i++;
			//--- add into MongoExAlgeSet
			newSet.add(newAlge);
			newAlge = null;
		}
		if (newAlge != null) {
			newSet.add(newAlge);
		}
		numElms = newSet.size();
		long etime = System.nanoTime();
		CheckMongoExalgeFind.printNanoDuration(stime, etime);
		System.out.println(".....done(elem:" + String.valueOf(numElms) + " / dst:" + String.valueOf(numDst) + " / src:" +String.valueOf(numSrc) + ")");
		return newSet;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
