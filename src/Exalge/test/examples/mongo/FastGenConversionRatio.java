package examples.mongo;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;

import exalge2.ExBase;
import exalge2.ExBaseSet;
import exalge2.Exalge;
import exalge2.db.mongo.MongoExalge;
import exalge2.util.ExQuarterTimeKey;
import exalge2.util.ExYearTimeKey;
import redundantalge.db.mongo.MongoServerURI;
import redundantalge.db.mongo.MongoSession;

public class FastGenConversionRatio
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private File   FILE_INDATA_SJIS = new File("testdata/MongoExalgeGenConversionRatio/indata_SJIS.csv");
	
	static private String MONGO_COL_RESULT1 = "FastGenConversionRatioResult1";	// 国調労調変換比率の出力先とする、MongoDB コレクション名
	static private String MONGO_COL_RESULT2 = "FastGenConversionRatioResult2";	// 自営業主数（確報）推計値の出力先とする、MongoDB コレクション名

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public static void main(String[] args) {
		MongoSession session = null;
		try {
			// create session
			session = createSession();
			
			// drop existing results
			System.out.println("@@@ Drop existing results...");
			{
				MongoExalge res1 = new MongoExalge(session, MONGO_COL_RESULT1);
				MongoExalge res2 = new MongoExalge(session, MONGO_COL_RESULT2);
				res1.delete();
				res2.delete();
				res1 = null;
				res2 = null;
			}
			System.out.println(".....done.");
			
			// genConversionRatio
			System.out.println("@@@ Start 'FastGenConversionRatio' program...");
			long stime = System.nanoTime();
			int ret = doGenConversionRatio(session, FILE_INDATA_SJIS, MONGO_COL_RESULT1, MONGO_COL_RESULT2);
			long etime = System.nanoTime();
			printNanoDuration(stime, etime);
			System.out.println(".....done(ret=" + String.valueOf(ret) + ").");
			
			/* debug : check equality **
			{
				final String MONGO_COL_ANSWER1 = "GenConversionRatioResult1";
				final String MONGO_COL_ANSWER2 = "GenConversionRatioResult2";
				MongoExalge ansXR  = new MongoExalge(session, MONGO_COL_ANSWER1);
				MongoExalge ansXSE = new MongoExalge(session, MONGO_COL_ANSWER2);
				MongoExalge resXR  = new MongoExalge(session, MONGO_COL_RESULT1);
				MongoExalge resXSE = new MongoExalge(session, MONGO_COL_RESULT2);
				if (resXR.isEqualValues(ansXR)) {
					System.out.println("Check : " + MONGO_COL_RESULT1 + " == " + MONGO_COL_ANSWER1);
				} else {
					System.out.println("Check : " + MONGO_COL_RESULT1 + " != " + MONGO_COL_ANSWER1);
				}
				if (resXSE.isEqualValues(ansXSE)) {
					System.out.println("Check : " + MONGO_COL_RESULT2 + " == " + MONGO_COL_ANSWER2);
				} else {
					System.out.println("Check : " + MONGO_COL_RESULT2 + " != " + MONGO_COL_ANSWER2);
				}
			}
			/* end of debug */
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
				//session.closeSession();
				System.out.println(".....done.");
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static private void printNanoDuration(long stime, long etime) {
		double ddur = (double)(etime - stime) / 1000000000.0;
		System.out.printf("=== Duration: %g", ddur).println();
	}
	
	static private MongoSession createSession() {
		System.out.println("@@@ Create MongoExalgeSession with MongoExalgeURI...");
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
		session.ensureConnection();// 接続
		//session = new MongoExalgeSession(uri);
		long etime = System.nanoTime();
		printNanoDuration(stime, etime);
		System.out.println(".....done!");
		return session;
	}
	
	static private String TBq3_by(String tby) {
		String retTime;
		retTime = tby + "Q3";
		return retTime;
	}
	
	static private String TBby_q(String tq) {
	    ExYearTimeKey ytk = new ExYearTimeKey();
	    ExQuarterTimeKey qtk = new ExQuarterTimeKey(tq);
	    if (qtk.getYear() <= 1980)
	      ytk.setYear(1975);
	    else if (qtk.getYear() <= 1985)
	      ytk.setYear(1980);
	    else if (qtk.getYear() <= 1990)
	      ytk.setYear(1985);
	    else if (qtk.getYear() <= 1995)
	      ytk.setYear(1990);
	    else if (qtk.getYear() <= 2000)
	      ytk.setYear(1995);
	    else
	      ytk.setYear(2000);
		
	    return ytk.toString();
	}
	
	/**
	 ** 国勢調整ベースの自営業主数四半期値の推計を行う。
	 ** (1) 国勢調査自営業主数（基準暦年値）と労働力調査自営業主数（基準年７～９月期）を
	 **     用いて、国調労調変換比率を算出する。
	 ** (2) 労働力調査自営業主数に国調労調変換比率を乗じて、自営業主数（確報）を推計する。
	 * @param session	MongoDB セッション
	 * @param fInData			入力ファイル(原データ)
	 * @param colNameResult1	国調労調変換比率の出力先とする、MongoDB コレクション名
	 * @param colNameResult2	自営業主数（確報）推計値の出力先とする、MongoDB コレクション名
	 * @return	終了コード
	 **/
	static public int doGenConversionRatio(MongoSession session, File fInData, String colNameResult1, String colNameResult2) {
		// 産業大分類 ΛInd
		ArrayList<String> lambdaInd = new ArrayList<String>(Arrays.asList(
										"鉱業", "建設業", "製造業（除く内職）", "卸小売業（除飲食店）",
										"不動産業", "運輸通信業", "サービス業（含飲食店，除く内職）",
										"内職者（製造業，サービス業）"));
		// 基準暦年 ΛTby
		ArrayList<String> lambdaTby = new ArrayList<String>(Arrays.asList(
										"Y1975", "Y1980", "Y1985", "Y1990", "Y1995", "Y2000"));
		// 四半期 ΛTq
		ArrayList<String> lambdaTq = new ArrayList<String>(Arrays.asList(
										"Y1975Q1", "Y1975Q2", "Y1975Q3", "Y1975Q4",
										"Y1980Q1", "Y1980Q2", "Y1980Q3", "Y1980Q4",
										"Y1981Q1", "Y1981Q2", "Y1981Q3", "Y1981Q4",
										"Y1982Q1", "Y1982Q2", "Y1982Q3", "Y1982Q4",
										"Y1983Q1", "Y1983Q2", "Y1983Q3", "Y1983Q4",
										"Y1984Q1", "Y1984Q2", "Y1984Q3", "Y1984Q4",
										"Y1985Q1", "Y1985Q2", "Y1985Q3", "Y1985Q4",
										"Y1986Q1", "Y1986Q2", "Y1986Q3", "Y1986Q4",
										"Y1987Q1", "Y1987Q2", "Y1987Q3", "Y1987Q4",
										"Y1988Q1", "Y1988Q2", "Y1988Q3", "Y1988Q4",
										"Y1989Q1", "Y1989Q2", "Y1989Q3", "Y1989Q4",
										"Y1990Q1", "Y1990Q2", "Y1990Q3", "Y1990Q4",
										"Y1991Q1", "Y1991Q2", "Y1991Q3", "Y1991Q4",
										"Y1992Q1", "Y1992Q2", "Y1992Q3", "Y1992Q4",
										"Y1993Q1", "Y1993Q2", "Y1993Q3", "Y1993Q4",
										"Y1994Q1", "Y1994Q2", "Y1994Q3", "Y1994Q4",
										"Y1995Q1", "Y1995Q2", "Y1995Q3", "Y1995Q4",
										"Y1996Q1", "Y1996Q2", "Y1996Q3", "Y1996Q4",
										"Y1997Q1", "Y1997Q2", "Y1997Q3", "Y1997Q4",
										"Y1998Q1", "Y1998Q2", "Y1998Q3", "Y1998Q4",
										"Y1999Q1", "Y1999Q2", "Y1999Q3", "Y1999Q4",
										"Y2000Q1", "Y2000Q2", "Y2000Q3", "Y2000Q4",
										"Y2001Q1", "Y2001Q2", "Y2001Q3", "Y2001Q4",
										"Y2002Q1", "Y2002Q2", "Y2002Q3", "Y2002Q4",
										"Y2003Q1", "Y2003Q2", "Y2003Q3", "Y2003Q4",
										"Y2004Q1", "Y2004Q2", "Y2004Q3", "Y2004Q4",
										"Y2005Q1", "Y2005Q2", "Y2005Q3", "Y2005Q4",
										"Y2006Q1", "Y2006Q2", "Y2006Q3", "Y2006Q4"));
		
		// read from File
		Exalge fileData;
		try {
			fileData = Exalge.fromCSV(fInData, "MS932");
		}
		catch (Exception ex) {
			throw new RuntimeException("Failed to open input file : \"" + fInData.toString() + "\"", ex);
		}
		MongoExalge xData = new MongoExalge(session);
		xData.add(fileData);

		// 国勢調査自営業主数
		///MongoExAlgeSet xPCSet = new MongoExAlgeSet(session);
		///for (String t : lambdaTby) {
		///	for (String i : lambdaInd) {
		///		MongoExalge v = xData.projection(new ExBase("自営業主数", ExBase.NO_HAT, "人", t, i));
		///		xPCSet.add(v);
		///	}
		///}
		///MongoExalge xPC = xPCSet.sum();
		ExBaseSet basesPCSet = new ExBaseSet();
		for (String t : lambdaTby) {
			for (String i : lambdaInd) {
				basesPCSet.add(new ExBase("自営業主数", ExBase.NO_HAT, "人", t, i));
			}
		}
		MongoExalge xPC = xData.projection(basesPCSet);
		basesPCSet = null;
		///if (xPCSet.isEmpty())
		///	return 1;
		///else if (xPC.isEmpty())
		///	return 1;
		if (xPC.isEmpty())
			return 1;

		// 労働力調査自営業主数
		///MongoExAlgeSet xLFSSet = new MongoExAlgeSet(session);
		///for (String t : lambdaTq) {
		///	for (String i : lambdaInd) {
		///		MongoExalge v = xData.projection(new ExBase("自営業主数",ExBase.NO_HAT, "千人",t,i));
		///		xLFSSet.add(v);
		///	}
		///}
		///MongoExalge xLFS = xLFSSet.sum();
		ExBaseSet basesLFSSet = new ExBaseSet();
		for (String t : lambdaTq) {
			for (String i : lambdaInd) {
				basesLFSSet.add(new ExBase("自営業主数",ExBase.NO_HAT, "千人",t,i));
			}
		}
		MongoExalge xLFS = xData.projection(basesLFSSet);
		basesLFSSet = null;

		// 国調労調変換比率の算出
		///MongoExAlgeSet xrSet = new MongoExAlgeSet(session);
		///for (String t : lambdaTby) {
		///	for (String i : lambdaInd) {
		///		MongoExalge a = null;
		///		MongoExalge b = null;
		///		try {
		///			a = xPC.projection(new ExBase("自営業主数",ExBase.NO_HAT,"人",t, i));
		///			b = xLFS.projection(new ExBase("自営業主数", ExBase.NO_HAT,"千人", TBq3_by(t), i));
		///			BigDecimal na = a.norm();
		///			BigDecimal nb = b.norm();
		///			if (!a.isEmpty() && !b.isEmpty() && na.compareTo(BigDecimal.ZERO) != 0 && nb.compareTo(BigDecimal.ZERO) != 0) {
		///				Exalge r = new Exalge(new ExBase("国調労調変換比率", ExBase.NO_HAT, "#", t, i), na.divide(nb, MathContext.DECIMAL128));
		///				xrSet.add(r);
		///			}
		///		}
		///		finally {
		///			if (a != null)
		///				a.delete();
		///			if (b != null)
		///				b.delete();
		///		}
		///	}
		///}
		///MongoExalge xr = xrSet.sum();
		MongoExalge xr = new MongoExalge(session);
		for (String t : lambdaTby) {
			for (String i : lambdaInd) {
				BigDecimal na = xPC.get(new ExBase("自営業主数",ExBase.NO_HAT,"人",t, i));
				BigDecimal nb = xLFS.get(new ExBase("自営業主数", ExBase.NO_HAT,"千人", TBq3_by(t), i));
				if (na.compareTo(BigDecimal.ZERO) != 0 && nb.compareTo(BigDecimal.ZERO) != 0) {
					Exalge r = new Exalge(new ExBase("国調労調変換比率", ExBase.NO_HAT, "#", t, i), na.divide(nb, MathContext.DECIMAL128));
					xr.add(r);
				}
			}
		}

		// 自営業主数（確報）の推計
		///MongoExAlgeSet xSESet = new MongoExAlgeSet(session);
		///for (String t : lambdaTq) {
		///	for (String i : lambdaInd) {
		///		MongoExalge a = null;
		///		MongoExalge b = null;
		///		try {
		///			a = xLFS.projection(new ExBase("自営業主数", ExBase.NO_HAT, "千人", t, i));
		///			b = xr.projection(new ExBase("国調労調変換比率", ExBase.NO_HAT, "#", TBby_q(t), i));
		///			if (!a.isEmpty() && !b.isEmpty()) {
		///				Exalge r = new Exalge(new ExBase("自営業主数（確報）", ExBase.NO_HAT, "人", t, i), a.norm().multiply(b.norm()));
		///				xSESet.add(r);
		///			}
		///		}
		///		finally {
		///			if (a != null)
		///				a.delete();
		///			if (b != null)
		///				b.delete();
		///		}
		///	}
		///}
		///MongoExalge xSE = xSESet.sum();
		MongoExalge xSE = new MongoExalge(session);
		for (String t : lambdaTq) {
			for (String i : lambdaInd) {
				ExBase ba = new ExBase("自営業主数", ExBase.NO_HAT, "千人", t, i);
				ExBase bb = new ExBase("国調労調変換比率", ExBase.NO_HAT, "#", TBby_q(t), i);
				if (xLFS.containsBase(ba) && xr.containsBase(bb)) {
					BigDecimal na = xLFS.get(ba);
					BigDecimal nb = xr.get(bb);
					Exalge r = new Exalge(new ExBase("自営業主数（確報）", ExBase.NO_HAT, "人", t, i), na.multiply(nb));
					xSE.add(r);
				}
			}
		}

		// resultは、ファイル出力
		xr.toPersistent(colNameResult1);	// 国調労調変換比率
		xSE.toPersistent(colNameResult2);	// 自営業主数（確報）推計値
		return 0;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
