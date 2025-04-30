package examples.mongo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.codecs.BigDecimalCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;

import exalge2.ExBase;
import exalge2.db.mongo.MongoDelegateDocExAlgeSetElem;
import exalge2.db.mongo.MongoDelegateDocExBase;
import exalge2.db.mongo.MongoDelegateDocExalgeElem;
import examples.mongo.CheckExalgeEquals.ExalgeElem;
import redundantalge.db.mongo.MongoUtil;

public class CheckExAlgeSet
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private String MONGO_COL_EXALGESET1 = "TestExAlgeSet1";
	static private String MONGO_COL_EXALGESET2 = "TestExAlgeSet2";
	
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
			MongoCollection<Document> aset1 = mdb.getCollection(MONGO_COL_EXALGESET1);
			MongoCollection<Document> aset2 = mdb.getCollection(MONGO_COL_EXALGESET2);

			//--- clear
			/**/
			System.out.println("@@@ clear " + aset1.getNamespace().toString() + "...");
			MongoUtil.clearAllDocuments(aset1);
			System.out.println(".....done.");
			System.out.println("@@@ clear " + aset2.getNamespace().toString() + "...");
			MongoUtil.clearAllDocuments(aset2);
			System.out.println(".....done.");
			/**/
			
			//--- insert
			/**/
			insertExAlgeSetData(mdb, aset1, false);
			insertExAlgeSetData(mdb, aset2, false);
			/**/
			
			//--- equals with sort
			equalsWithSort(aset1, aset2);
			
			//--- upsert documents
			UpdateResult updateResult;
			System.out.println("@@@ upset into " + aset1.getNamespace().toString() + "...");
			Document data = MongoDelegateDocExalgeElem.makeExalgeElemDocument(test_alges[3].base, new BigDecimal("1.11111"));
			Document where = MongoDelegateDocExalgeElem.makeFilterByExBase(data);
			updateResult = MongoUtil.updateDocument(aset1, where, data, true, false);
			System.out.println(".....done(matched=" + String.valueOf(updateResult.getMatchedCount())
								+ " / modified=" + String.valueOf(updateResult.getModifiedCount())
								+ " / upsertedId=" + String.valueOf(updateResult.getUpsertedId()) + ").");
			
			//--- upsert new documents
			System.out.println("@@@ upset new document into " + aset1.getNamespace().toString() + "...");
			data = MongoDelegateDocExalgeElem.makeExalgeElemDocument(new ExBase("ほげ", ExBase.NO_HAT, "円", null, null), new BigDecimal("99.9999"));
			where = MongoDelegateDocExalgeElem.makeFilterByExBase(data);
			updateResult = MongoUtil.updateDocument(aset1, where, data, false, true);
			System.out.println(".....done(matched=" + String.valueOf(updateResult.getMatchedCount())
			+ " / modified=" + String.valueOf(updateResult.getModifiedCount())
			+ " / upsertedId=" + String.valueOf(updateResult.getUpsertedId()) + ").");
			
			// getBases
			getBases(mdb, aset1, MONGO_COL_EXBASESET1);
			
			// distinct by base
			distinctByBase(aset2);
			distinctFirstAlgeByBase(aset2);
			
			// find by value
			findByBase(aset2, test_alges[4].base, false);
			findByBase(aset2, test_alges[4].base, true);
			findByValue(aset2, null);
			findByValue(aset2, BigDecimal.ZERO);
			findByValue(aset2, new BigDecimal("0.000"));
			findByValue(aset2, new BigDecimal("0.1"));
			
			findByValues(aset2, Arrays.asList(BigDecimal.ZERO, new BigDecimal("0.1"), (BigDecimal)null));
			findWithoutValues(aset2, Arrays.asList(BigDecimal.ZERO, new BigDecimal("0.1"), (BigDecimal)null));
			
			findByBases(aset2, Arrays.asList(test_alges[0].base, test_alges[2].base, test_alges[4].base), false);
			findByBases(aset2, Arrays.asList(test_alges[0].base, test_alges[2].base, test_alges[4].base), true);
			
			// count distinct documents
			countExAlgeSetElements(mdb, aset2);
			checkExAlgeSetElementSetID(aset2);
			
			// delete by ObjectID
			deleteExAlgeSetElementsByObjectId(aset1);
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
	
	static public long deleteExAlgeSetElementsByObjectId(MongoCollection<? extends Document> mcol) {
		System.out.println("@@@ delete document by ObjectID from ExAlgeSet" + mcol.getNamespace().toString() + "...");
		long numDocs = 0L;
		long numElems = 0L;
		long stime = System.nanoTime();
		try {
			numDocs = mcol.countDocuments();
			System.out.println("  Results:");
			MongoCursor<? extends Document> cursor = mcol.find().iterator();
			try {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					Object objid = doc.get("_id");
					System.out.println("    removing document by " + String.valueOf(objid) + "...");
					long deleted = mcol.deleteOne(new Document("_id", objid)).getDeletedCount();
					System.out.println("    .....done(" + String.valueOf(deleted) + ")");
					numElems += deleted;
				}
			}
			finally {
				cursor.close();
			}
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to delete elements of ExAlgeSet:", ex);
		}
		long etime = System.nanoTime();
		CheckMongoExalgeFind.printNanoDuration(stime, etime);
		System.out.println(".....done(deleted:" + String.valueOf(numElems) + " / documents:" + String.valueOf(numDocs) + ").");
		return numElems;
	}
	
	static public void checkExAlgeSetElementSetID(MongoCollection<? extends Document> mcol) {
		System.out.println("@@@ print aggregated elem_id of ExAlgeSet" + mcol.getNamespace().toString() + "...");
		ArrayList<Bson> MONGO_AGGRE_ELEM_ITERABLE = new ArrayList<Bson>();
		MONGO_AGGRE_ELEM_ITERABLE.add(Aggregates.group("$" + MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID));
		MONGO_AGGRE_ELEM_ITERABLE.add(Aggregates.project(new Document(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, "$_id").append("_id", 0)));
		MONGO_AGGRE_ELEM_ITERABLE.add(Aggregates.sort(Sorts.ascending(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID)));
		long numDocs = 0L;
		long numElems = 0L;
		long stime = System.nanoTime();
		try {
			numDocs = mcol.countDocuments();
			System.out.println("  Results:");
			MongoCursor<? extends Document> cursor = mcol.aggregate(MONGO_AGGRE_ELEM_ITERABLE).iterator();
			try {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					System.out.println("    " + doc.toJson());
					numElems++;
				}
			}
			finally {
				cursor.close();
			}
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to count elements of ExAlgeSet:", ex);
		}
		long etime = System.nanoTime();
		CheckMongoExalgeFind.printNanoDuration(stime, etime);
		System.out.println(".....done(elements:" + String.valueOf(numElems) + " / documents:" + String.valueOf(numDocs) + ").");
	}
	
	static public long countExAlgeSetElements(MongoDatabase mdb, MongoCollection<? extends Document> mcol) {
		System.out.println("@@@ Count elements of ExAlgeSet" + mcol.getNamespace().toString() + "...");
		long numDocs = 0L;
		long numElems = 0L;
		long stime = System.nanoTime();
		try {
			ArrayList<Bson> list = new ArrayList<Bson>();
			list.add(Aggregates.group("$elem_id"));
			list.add(Aggregates.count("elements"));
			numDocs = mcol.countDocuments();
			System.out.println("  Results:");
			MongoCursor<? extends Document> cursor = mcol.aggregate(list).iterator();
			try {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					System.out.println("    " + doc.toJson());
					if (doc.containsKey("elements")) {
						numElems = doc.get("elements", Number.class).longValue();
					}
				}
			}
			finally {
				cursor.close();
			}
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to count elements of ExAlgeSet:", ex);
		}
		long etime = System.nanoTime();
		CheckMongoExalgeFind.printNanoDuration(stime, etime);
		System.out.println(".....done(elements:" + String.valueOf(numElems) + " / documents:" + String.valueOf(numDocs) + ").");
		return numElems;
	}
	
	static public void insertExAlgeSetData(MongoDatabase mdb, MongoCollection<Document> mcol, boolean reverse) {
		System.out.println("@@@ insert all testdata of ExAlgeSet (reverse=" + String.valueOf(reverse) + ") into " + mcol.getNamespace().toString() + "...");
		long stime = System.nanoTime();
		long num = 0;
		try {
			if (reverse) {
				for (int i = test_alges.length-1; i >= 0;) {
					Document doc;
					String str = MongoUtil.makeUniqueAlgeSetElemID();
					//--- 1st
					doc = MongoDelegateDocExAlgeSetElem.makeExAlgeSetElemDocument(str, test_alges[i].base, test_alges[i].value);
					mcol.insertOne(doc);
					i--;
					//--- 2nd
					if (i < 0)
						break;
					doc = MongoDelegateDocExAlgeSetElem.makeExAlgeSetElemDocument(str, test_alges[i].base, test_alges[i].value);
					mcol.insertOne(doc);
					i--;
					//--- 3rd
					if (i < 0)
						break;
					doc = MongoDelegateDocExAlgeSetElem.makeExAlgeSetElemDocument(str, test_alges[i].base, test_alges[i].value);
					mcol.insertOne(doc);
					i--;
				}
			} else {
				for (int i = 0; i < test_alges.length;) {
					Document doc;
					String str = MongoUtil.makeUniqueAlgeSetElemID();
					//--- 1st
					doc = MongoDelegateDocExAlgeSetElem.makeExAlgeSetElemDocument(str, test_alges[i].base, test_alges[i].value);
					mcol.insertOne(doc);
					i++;
					//--- 2nd
					if (i >= test_alges.length)
						break;
					doc = MongoDelegateDocExAlgeSetElem.makeExAlgeSetElemDocument(str, test_alges[i].base, test_alges[i].value);
					mcol.insertOne(doc);
					i++;
					//--- 3rd
					if (i >= test_alges.length)
						break;
					doc = MongoDelegateDocExAlgeSetElem.makeExAlgeSetElemDocument(str, test_alges[i].base, test_alges[i].value);
					mcol.insertOne(doc);
					i++;
				}
			}
			num = mcol.countDocuments();
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to upsetAllExalge into collection:", ex);
		}
		long etime = System.nanoTime();
		CheckMongoExalgeFind.printNanoDuration(stime, etime);
		System.out.println(".....done(inserted:" + String.valueOf(num) + " / elements:" + String.valueOf(test_alges.length) + ").");
	}
	
	static public boolean equalsWithSort(MongoCollection<? extends Document> mcol1, MongoCollection<? extends Document> mcol2) {
		System.out.println("@@@ Equals all elements with sort [" + mcol1.getNamespace().toString() + "] & [" + mcol2.getNamespace().toString() + "]...");
		try {
			boolean result = false;
			long stime = System.nanoTime();
			long numCol1 = mcol1.countDocuments();
			long numCol2 = mcol2.countDocuments();
			if (numCol1 == 0L && numCol2 == 0L) {
				result = true;
			}
			else if (numCol1 == numCol2) {
				MongoCursor<? extends Document> cursor1 = null;
				MongoCursor<? extends Document> cursor2 = null;
				try {
					cursor1 = mcol1.find().sort(MongoDelegateDocExAlgeSetElem.MONGO_SORT_EXALGESET_ELEM_ALL).iterator();
					cursor2 = mcol2.find().sort(MongoDelegateDocExAlgeSetElem.MONGO_SORT_EXALGESET_ELEM_ALL).iterator();
					long idxCol1 = -1L;
					long idxCol2 = -1L;
					result = true;
					String strLastSetID1 = null;
					String strLastSetID2 = null;
					while (cursor1.hasNext()) {
						Document doc1 = cursor1.next();
						Document doc2 = cursor2.next();
						String strCurSetID1 = MongoDelegateDocExAlgeSetElem.getExAlgeSetIDfromDocument(doc1);
						String strCurSetID2 = MongoDelegateDocExAlgeSetElem.getExAlgeSetIDfromDocument(doc2);
						if (!Objects.equals(strCurSetID1, strLastSetID1)) {
							idxCol1++;
							strLastSetID1 = strCurSetID1;
						}
						if (!Objects.equals(strCurSetID2, strLastSetID2)) {
							idxCol2++;
							strLastSetID2 = strCurSetID2;
						}
						//--- index
						if (idxCol1 != idxCol2) {
							result = false;
							break;
						}
						//--- element
						if (!MongoDelegateDocExAlgeSetElem.equalsExalgeElemDocument(doc1, doc2)) {
							result = false;
							break;
						}
					}
				}
				finally {
					if (cursor1 != null) {
						cursor1.close();
					}
					if (cursor2 != null) {
						cursor2.close();
					}
				}
			}
			long etime = System.nanoTime();
			CheckMongoExalgeFind.printNanoDuration(stime, etime);
			System.out.println(".....done(" + String.valueOf(result) + ")...");
			return result;
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to equals with sort for two collections:", ex);
		}
	}
	
	static public long getBases(MongoDatabase mdb, MongoCollection<? extends Document> mcol, String newCollectionName) {
		System.out.println("@@@ getBases from ExAlgeSet" + mcol.getNamespace().toString() + " to [" + newCollectionName + "]...");
		try {
			Document key = mcol.find().first();
			String firstSetID = key.getString("elem_id");
			ArrayList<Bson> list = new ArrayList<Bson>();
			list.add(Aggregates.project(new Document("_id", 0)));
			//list.add(Aggregates.match(new Document("elem_id", firstSetID)));
			list.add(Aggregates.match(Filters.regex("base.name", Pattern.compile("基底[123].*", Pattern.DOTALL))));
			list.add(Aggregates.group("$base",
					Accumulators.sum("count", 1)
			));
			list.add(Aggregates.project(new Document()
					.append("_id",  0)
					.append("hat", "$_id.hat")
					.append("name", "$_id.name")
					.append("unit", "$_id.unit")
					.append("time", "$_id.time")
					.append("subject", "$_id.subject")
			));
			list.add(Aggregates.out(newCollectionName));
			long orgNum = mcol.countDocuments();
			long stime = System.nanoTime();
			AggregateIterable<? extends Document> result = mcol.aggregate(list);
			result.toCollection();
			/*
			MongoCursor<? extends Document> cursor = result.iterator();
			try {
				System.out.println("  results:");
				while (cursor.hasNext()) {
					System.out.println("    " + cursor.next().toJson());
				}
			}
			finally {
				cursor.close();
			}
			/**/
			long etime = System.nanoTime();
			CheckMongoExalgeFind.printNanoDuration(stime, etime);
			long retNum = mdb.getCollection(newCollectionName).countDocuments();
			System.out.println(".....done(result=" + String.valueOf(retNum) + " / source=" + String.valueOf(orgNum) + ").");
			return retNum;
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to getBases :", ex);
		}
	}
	
	static public long distinctByBase(MongoCollection<? extends Document> mcol) {
		System.out.println("@@@ distinct by Base in " + mcol.getNamespace().toString() + "...");
		try {
			long orgNum = mcol.countDocuments();
			long retNum = 0L;
			long stime = System.nanoTime();
			MongoCursor<? extends Document> cursor = mcol.distinct("base", Document.class).iterator();
			try {
				System.out.println("  Results:");
				while (cursor.hasNext()) {
					System.out.println("    " + cursor.next().toJson());
					retNum++;
				}
			}
			finally {
				cursor.close();
			}
			long etime = System.nanoTime();
			CheckMongoExalgeFind.printNanoDuration(stime, etime);
			System.out.println(".....done(result=" + String.valueOf(retNum) + " / source=" + String.valueOf(orgNum) + ").");
			return retNum;
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to distinct by ExBase :", ex);
		}
	}
	
	static public long distinctFirstAlgeByBase(MongoCollection<? extends Document> mcol) {
		System.out.println("@@@ distinct first Exalge by Base in ExAlgeSet" + mcol.getNamespace().toString() + "...");
		try {
			long orgNum = mcol.countDocuments();
			long retNum = 0L;
			long stime = System.nanoTime();
			String strSetID = null;
			Document firstDoc = mcol.find().first();
			if (firstDoc != null) {
				strSetID = firstDoc.getString("elem_id");
			}
			MongoCursor<? extends Document> cursor;
			if (strSetID != null) {
				//--- ok
				System.out.println("*** elem_id=" + strSetID);
				//cursor = mcol.distinct("base", new Document("elem_id", strSetID), Document.class).iterator();
				cursor = mcol.distinct("base", Filters.eq("elem_id", strSetID), Document.class).iterator();
			} else {
				cursor = mcol.distinct("base", Document.class).iterator();
			}
			try {
				System.out.println("  Results:");
				while (cursor.hasNext()) {
					System.out.println("    " + cursor.next().toJson());
					retNum++;
				}
			}
			finally {
				cursor.close();
			}
			long etime = System.nanoTime();
			CheckMongoExalgeFind.printNanoDuration(stime, etime);
			System.out.println(".....done(result=" + String.valueOf(retNum) + " / source=" + String.valueOf(orgNum) + ").");
			return retNum;
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to distinct first alge by ExBase :", ex);
		}
	}
	
	static public long findByBase(MongoCollection<? extends Document> mcol, ExBase base, boolean onlyFirstAlge) {
		System.out.println("@@@ Find by filter with ExBase in " + mcol.getNamespace().toString() + " with elem_id(" + String.valueOf(onlyFirstAlge) + ")...");
		try {
			long orgNum = mcol.countDocuments();
			long retNum = 0L;
			long stime = System.nanoTime();
			String strSetID = null;
			if (onlyFirstAlge) {
				Document firstDoc = mcol.find().first();
				if (firstDoc != null) {
					strSetID = firstDoc.getString("elem_id");
				}
			}
			Bson filter;
			if (strSetID != null) {
				filter = Filters.and(
							Filters.eq("elem_id", strSetID),
							Filters.eq("base", MongoDelegateDocExBase.makeExBaseDocument(base))
						);
			}
			else {
				filter = Filters.eq("base", MongoDelegateDocExBase.makeExBaseDocument(base));
			}
			MongoCursor<? extends Document> cursor = mcol.find(filter).projection(new Document("_id", 0)).iterator();
			try {
				System.out.println("  Results:");
				while (cursor.hasNext()) {
					System.out.println("    " + cursor.next().toJson());
					retNum++;
				}
			}
			finally {
				cursor.close();
			}
			long etime = System.nanoTime();
			CheckMongoExalgeFind.printNanoDuration(stime, etime);
			System.out.println(".....done(result=" + String.valueOf(retNum) + " / source=" + String.valueOf(orgNum) + ").");
			return retNum;
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to find by filter with value :", ex);
		}
	}
	
	static public long findByBases(MongoCollection<? extends Document> mcol, Collection<? extends ExBase> bases, boolean onlyFirstAlge) {
		System.out.println("@@@ Find by filter with multiple ExBase in " + mcol.getNamespace().toString() + " with elem_id(" + String.valueOf(onlyFirstAlge) + ")...");
		try {
			long orgNum = mcol.countDocuments();
			long retNum = 0L;
			long stime = System.nanoTime();
			String strSetID = null;
			if (onlyFirstAlge) {
				Document firstDoc = mcol.find().first();
				if (firstDoc != null) {
					strSetID = firstDoc.getString("elem_id");
				}
			}
			ArrayList<Document> aryBaseDocs = new ArrayList<Document>(bases.size());
			for (ExBase base : bases) {
				aryBaseDocs.add(MongoDelegateDocExBase.makeExBaseDocument(base));
			}
			Bson filter;
			if (strSetID != null) {
				filter = Filters.and(
							Filters.eq("elem_id", strSetID),
							Filters.in("base", aryBaseDocs)
						);
			}
			else {
				filter = Filters.in("base", aryBaseDocs);
			}
			MongoCursor<? extends Document> cursor = mcol.find(filter).projection(new Document("_id", 0)).iterator();
			try {
				System.out.println("  Results:");
				while (cursor.hasNext()) {
					System.out.println("    " + cursor.next().toJson());
					retNum++;
				}
			}
			finally {
				cursor.close();
			}
			long etime = System.nanoTime();
			CheckMongoExalgeFind.printNanoDuration(stime, etime);
			System.out.println(".....done(result=" + String.valueOf(retNum) + " / source=" + String.valueOf(orgNum) + ").");
			return retNum;
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to find by multiple ExBase with value :", ex);
		}
	}
	
	static public long findByValue(MongoCollection<? extends Document> mcol, BigDecimal value) {
		System.out.println("@@@ Find by filter with value in " + mcol.getNamespace().toString() + " with value(" + String.valueOf(value) + ")...");
		try {
			long orgNum = mcol.countDocuments();
			long retNum = 0L;
			long stime = System.nanoTime();
			MongoCursor<? extends Document> cursor = mcol.find(Filters.eq("value", value)).projection(new Document("_id", 0)).iterator();
			try {
				System.out.println("  Results:");
				while (cursor.hasNext()) {
					System.out.println("    " + cursor.next().toJson());
					retNum++;
				}
			}
			finally {
				cursor.close();
			}
			long etime = System.nanoTime();
			CheckMongoExalgeFind.printNanoDuration(stime, etime);
			System.out.println(".....done(result=" + String.valueOf(retNum) + " / source=" + String.valueOf(orgNum) + ").");
			return retNum;
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to find by filter with value :", ex);
		}
	}
	
	static public long findByValues(MongoCollection<? extends Document> mcol, Collection<? extends BigDecimal> values) {
		System.out.println("@@@ Find by filter with multiple values in " + mcol.getNamespace().toString() + " with value(" + String.valueOf(values) + ")...");
		try {
			long orgNum = mcol.countDocuments();
			long retNum = 0L;
			long stime = System.nanoTime();
			MongoCursor<? extends Document> cursor = mcol.find(Filters.in("value", values)).projection(new Document("_id", 0)).iterator();
			try {
				System.out.println("  Results:");
				while (cursor.hasNext()) {
					System.out.println("    " + cursor.next().toJson());
					retNum++;
				}
			}
			finally {
				cursor.close();
			}
			long etime = System.nanoTime();
			CheckMongoExalgeFind.printNanoDuration(stime, etime);
			System.out.println(".....done(result=" + String.valueOf(retNum) + " / source=" + String.valueOf(orgNum) + ").");
			return retNum;
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to find by multiple values with value :", ex);
		}
	}
	
	static public long findWithoutValues(MongoCollection<? extends Document> mcol, Collection<? extends BigDecimal> values) {
		System.out.println("@@@ Find by filter without multiple values in " + mcol.getNamespace().toString() + " with value(" + String.valueOf(values) + ")...");
		try {
			long orgNum = mcol.countDocuments();
			long retNum = 0L;
			long stime = System.nanoTime();
			MongoCursor<? extends Document> cursor = mcol.find(Filters.nin("value", values)).projection(new Document("_id", 0)).iterator();
			try {
				System.out.println("  Results:");
				while (cursor.hasNext()) {
					System.out.println("    " + cursor.next().toJson());
					retNum++;
				}
			}
			finally {
				cursor.close();
			}
			long etime = System.nanoTime();
			CheckMongoExalgeFind.printNanoDuration(stime, etime);
			System.out.println(".....done(result=" + String.valueOf(retNum) + " / source=" + String.valueOf(orgNum) + ").");
			return retNum;
		}
		catch (Throwable ex) {
			throw new RuntimeException("!!! Failed to find without multiple values with value :", ex);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
