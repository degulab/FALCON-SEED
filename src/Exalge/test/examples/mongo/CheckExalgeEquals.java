package examples.mongo;

import java.math.BigDecimal;

import org.bson.Document;
import org.bson.codecs.BigDecimalCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;

import exalge2.ExBase;
import exalge2.db.mongo.MongoDelegateDocExBase;
import exalge2.db.mongo.MongoDelegateDocExalgeElem;

public class CheckExalgeEquals
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private String MONGO_COL_EXBASESET1 = "TestExBaseSet1";
	static private String MONGO_COL_EXBASESET2 = "TestExBaseSet2";
	
	static private String MONGO_COL_EXALGE1 = "TestExalge1";
	static private String MONGO_COL_EXALGE2 = "TestExalge2";
	
	static private String MONGO_COL_EXALGESET1 = "TestExAlgeSet1";
	static private String MONGO_COL_EXALGESET2 = "TestExAlgeSet2";
	
	/** PING コマンド **/
	static protected final BasicDBObject	_cmdPing = new BasicDBObject("ping", "1");
	
	static public final ExBase[] test_bases = new ExBase[] {
		new ExBase("基底1", ExBase.NO_HAT, "円", "", ""),	
		new ExBase("基底2", ExBase.NO_HAT, "円", "", ""),	
		new ExBase("基底3", ExBase.NO_HAT, "円", "", ""),	
		new ExBase("基底4", ExBase.NO_HAT, "円", "", ""),	
		new ExBase("基底5", ExBase.NO_HAT, "円", "", ""),	
		new ExBase("基底1", ExBase.HAT, "円", "", ""),	
		new ExBase("基底2", ExBase.NO_HAT, "円", "", ""),	
		new ExBase("基底3", ExBase.HAT, "円", "", ""),	
		new ExBase("基底4", ExBase.NO_HAT, "円", "", ""),	
		new ExBase("基底5", ExBase.HAT, "円", "", ""),	
	};
	
	static public class ExalgeElem {
		public ExBase base;
		public BigDecimal value;
		
		public ExalgeElem(ExBase inBase, BigDecimal inValue) {
			base = inBase;
			value = inValue;
		}
	}
	
	static public final ExalgeElem[] test_alges = new ExalgeElem[] {
		new ExalgeElem(new ExBase("基底1", ExBase.NO_HAT, "円", "", ""), new BigDecimal("10")),	
		new ExalgeElem(new ExBase("基底2", ExBase.NO_HAT, "円", "", ""), new BigDecimal("20")),	
		new ExalgeElem(new ExBase("基底3", ExBase.NO_HAT, "円", "", ""),	new BigDecimal("0.0100")),
		new ExalgeElem(new ExBase("基底4", ExBase.NO_HAT, "円", "", ""),	new BigDecimal("40")),
		new ExalgeElem(new ExBase("基底5", ExBase.NO_HAT, "円", "", ""),	new BigDecimal("50")),
		new ExalgeElem(new ExBase("基底1", ExBase.HAT, "円", "", ""),	new BigDecimal("60")),
		new ExalgeElem(new ExBase("基底2", ExBase.NO_HAT, "円", "", ""),	new BigDecimal("70")),
		new ExalgeElem(new ExBase("基底3", ExBase.HAT, "円", "", ""),	new BigDecimal("0.100")),
		new ExalgeElem(new ExBase("基底4", ExBase.NO_HAT, "円", "", ""),	null),
		new ExalgeElem(new ExBase("基底5", ExBase.HAT, "円", "", ""),	null),
	};

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
			CodecRegistry codecreg = CodecRegistries.fromRegistries(
					CodecRegistries.fromCodecs(new BigDecimalCodec()),
					MongoClientSettings.getDefaultCodecRegistry());
			mdb.withCodecRegistry(codecreg);
			MongoCollection<Document> bases1 = mdb.getCollection(MONGO_COL_EXBASESET1);
			MongoCollection<Document> bases2 = mdb.getCollection(MONGO_COL_EXBASESET2);
			
			// create data
			upsetAllExBase(mdb, bases1, false);
			upsetAllExBase(mdb, bases2, true);
			
			// equals data
			equalsAllElements(mdb, bases1, bases2);
			
			// upset all exalge
			MongoCollection<Document> alge1 = mdb.getCollection(MONGO_COL_EXALGE1);
			MongoCollection<Document> alge2 = mdb.getCollection(MONGO_COL_EXALGE2);
			upsetAllExalge(mdb, alge1, false);
			upsetAllExalge(mdb, alge2, false);
			
			// equals data
			equalsAllElements(mdb, alge1, alge2);
			
			// clear collection
			clearCollection(mdb, alge1);
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
	
	static public void insertAllExBase(MongoDatabase mdb, MongoCollection<Document> mcol, boolean reverse) {
		System.out.println("@@@ Insert all testdata of ExBase (reverse=" + String.valueOf(reverse) + ") into " + mcol.getNamespace().toString() + "...");
		long stime = System.nanoTime();
		try {
			if (reverse) {
				for (int i = test_bases.length-1; i >= 0; i--) {
					mcol.insertOne(MongoDelegateDocExBase.makeExBaseDocument(test_bases[i]));
				}
			} else {
				for (int i = 0; i < test_bases.length; i++) {
					mcol.insertOne(MongoDelegateDocExBase.makeExBaseDocument(test_bases[i]));
				}
			}
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to insert data into collection:", ex);
		}
		long etime = System.nanoTime();
		CheckMongoExalgeFind.printNanoDuration(stime, etime);
		System.out.println(".....done.");
	}
	
	static public void upsetAllExBase(MongoDatabase mdb, MongoCollection<Document> mcol, boolean reverse) {
		System.out.println("@@@ Upset(insert or update) all testdata of ExBase (reverse=" + String.valueOf(reverse) + ") into " + mcol.getNamespace().toString() + "...");
		long stime = System.nanoTime();
		int inserted = 0;
		try {
			if (reverse) {
				for (int i = test_bases.length-1; i >= 0; i--) {
					Document doc = MongoDelegateDocExBase.makeExBaseDocument(test_bases[i]);
					if (mcol.countDocuments(doc) == 0L) {
						mcol.insertOne(doc);
						inserted++;
					}
				}
			} else {
				for (int i = 0; i < test_bases.length; i++) {
					Document doc = MongoDelegateDocExBase.makeExBaseDocument(test_bases[i]);
					if (mcol.countDocuments(doc) == 0L) {
						mcol.insertOne(doc);
						inserted++;
					}
				}
			}
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to insert data into collection:", ex);
		}
		long etime = System.nanoTime();
		CheckMongoExalgeFind.printNanoDuration(stime, etime);
		System.out.println(".....done(inserted:" + String.valueOf(inserted) + " / elements:" + String.valueOf(test_bases.length) + ").");
	}
	
	static private void clearCollection(MongoDatabase mdb, MongoCollection<? extends Document> mcol) {
		long numBefore = 0L;
		long numDeleted = 0L;
		long numAfter  = 0L;
		long stime = System.nanoTime();
		try {
			numBefore = mcol.countDocuments();
			numDeleted = mcol.deleteMany(new Document()).getDeletedCount();
			numAfter = mcol.countDocuments();
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to clear collection:", ex);
		}
		long etime = System.nanoTime();
		CheckMongoExalgeFind.printNanoDuration(stime, etime);
		System.out.println(".....done(deleted: " + String.valueOf(numDeleted) + " / before:" + String.valueOf(numBefore) + " / after:" + String.valueOf(numAfter) + ").");
	}
	
	static private void equalsAllElements(MongoDatabase mdb, MongoCollection<? extends Document> mcol1, MongoCollection<? extends Document> mcol2) {
		System.out.println("@@@ Equals all elements [" + mcol1.getNamespace().toString() + "] & [" + mcol2.getNamespace().toString() + "]...");
		try {
			boolean result = false;
			long stime = System.nanoTime();
			long numCol1 = mcol1.countDocuments();
			long numCol2 = mcol2.countDocuments();
			if (numCol1 == numCol2) {
				MongoCursor<? extends Document> cursor = mcol1.find().projection(new Document("_id", 0)).iterator();
				try {
					long matched = 0L;
					while (cursor.hasNext()) {
						if (mcol2.countDocuments(cursor.next()) > 0L) {
							matched++;
						}
						else {
							break;
						}
					}
					result = (matched == numCol1);
				}
				finally {
					cursor.close();
				}
			}
			long etime = System.nanoTime();
			CheckMongoExalgeFind.printNanoDuration(stime, etime);
			System.out.println(".....done(" + String.valueOf(result) + ")...");
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to equals two collections:", ex);
		}
	}
	
	static public void upsetAllExalge(MongoDatabase mdb, MongoCollection<Document> mcol, boolean reverse) {
		System.out.println("@@@ Upset(insert or update) all testdata of Exalge (reverse=" + String.valueOf(reverse) + ") into " + mcol.getNamespace().toString() + "...");
		long stime = System.nanoTime();
		long num = 0;
		try {
			FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
			options.upsert(true);
			if (reverse) {
				for (int i = test_alges.length-1; i >= 0; i--) {
					Document doc = MongoDelegateDocExalgeElem.makeExalgeElemDocument(test_alges[i].base, test_alges[i].value);
					Document filter = MongoDelegateDocExalgeElem.makeFilterByExBase(doc);
					mcol.findOneAndReplace(filter, doc, options);
				}
			} else {
				for (int i = 0; i < test_alges.length; i++) {
					Document doc = MongoDelegateDocExalgeElem.makeExalgeElemDocument(test_alges[i].base, test_alges[i].value);
					Document filter = MongoDelegateDocExalgeElem.makeFilterByExBase(doc);
					mcol.findOneAndReplace(filter, doc, options);
				}
			}
			num = mcol.countDocuments();
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to upsetAllExalge into collection:", ex);
		}
		long etime = System.nanoTime();
		CheckMongoExalgeFind.printNanoDuration(stime, etime);
		System.out.println(".....done(updated:" + String.valueOf(num) + " / elements:" + String.valueOf(test_alges.length) + ").");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
