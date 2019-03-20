package ssac.aadl.basicfilter.statistics;


public class TestCorrelationCoefficient
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String[][] testArgsSet = {
		{ "testdata/filter/BasicFilter/Statistics/Correlation/TestDataHand.csv",
			"1", "2-", "false", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultTestDataHand_false_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/TestDataHand.csv",
			"1", "2-", "true", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultTestDataHand_true_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData1.csv",
			"1", "2-", "false", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData1_false_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData1.csv",
			"1", "2-", "true", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData1_true_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData2.csv",
			"1", "2-", "false", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData2_false_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData2.csv",
			"1", "2-", "true", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData2_true_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData2.csv",
			"1", "2-", "false", "true",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData2_false_true.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData2.csv",
			"1", "2-", "true", "true",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData2_true_true.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData3.csv",
			"1", "2-", "false", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData3_false_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData3.csv",
			"1", "2-", "true", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData3_true_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData3.csv",
			"1", "2-", "false", "true",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData3_false_true.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData3.csv",
			"1", "2-", "true", "true",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData3_true_true.csv"},

		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData1nh.csv",
			"0", "2-", "false", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData1nh_false_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData1nh.csv",
			"0", "2-", "true", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData1nh_true_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData2nh.csv",
			"0", "2-", "false", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData2nh_false_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData2nh.csv",
			"0", "2-", "true", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData2nh_true_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData2nh.csv",
			"0", "2-", "false", "true",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData2nh_false_true.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData2nh.csv",
			"0", "2-", "true", "true",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData2nh_true_true.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData3nh.csv",
			"0", "2-", "false", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData3nh_false_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData3nh.csv",
			"0", "2-", "true", "false",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData3nh_true_false.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData3nh.csv",
			"0", "2-", "false", "true",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData3nh_false_true.csv"},
		{ "testdata/filter/BasicFilter/Statistics/Correlation/CorrelTestData3nh.csv",
			"0", "2-", "true", "true",
			"testdata/filter/BasicFilter/Statistics/Correlation/resultCorrelTestData3nh_true_true.csv"},
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("<< Test for CorrelationCoefficient filter >>");
		for (int i = 0; i < testArgsSet.length; i++) {
			String[] testArgs = testArgsSet[i];
			CorrelationCoefficient module = new CorrelationCoefficient();
			int ret = module.aadlRun(testArgs);
			System.out.println("Test[" + i + "] result=" + ret);
		}
		System.out.println("<< finished! >>");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
