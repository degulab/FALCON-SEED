/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
package exalge2;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import exalge2.ExTransfer.ITransferEntryMap;
import exalge2.ExTransfer.TransferAttribute;

import junit.framework.TestCase;

/**
 * <code>ExTransfer</code> テスト(I/O 以外)
 * @since 0.982
 */
public class ExTransferTest extends TestCase
{
	static protected final String FruitNum		= "果物-*-個-*-*";
	static protected final String ExpFruitNum		= "高級果物-*-個-*-*";
	static protected final String FishAll			= "魚-*-*-*-*";
	static protected final String MeatAll			= "肉類-*-*-*-*";
	static protected final String StationeryAll	= "文具-*-*-*-*";
	static protected final String AppleAll		= "りんご-*-*-*-*";
	static protected final String OrangeAll		= "みかん-*-*-*-*";
	static protected final String BananaAll		= "ばなな-*-*-*-*";
	static protected final String MelonAll		= "メロン-*-*-*-*";
	static protected final String MangoAll		= "マンゴー-*-*-*-*";
	static protected final String TunaAll			= "まぐろ-*-*-*-*";
	static protected final String BonitoAll		= "かつお-*-*-*-*";
	static protected final String SauryAll		= "さんま-*-*-*-*";
	static protected final String SardineAll		= "いわし-*-*-*-*";
	static protected final String BeefAll			= "牛肉-*-*-*-*";
	static protected final String PorkAll			= "豚肉-*-*-*-*";
	static protected final String ChickenAll		= "鶏肉-*-*-*-*";
	static protected final String BallPenYen		= "ボールペン-*-円-*-*";
	static protected final String MechaPenYen		= "シャープペン-*-円-*-*";
	static protected final String PencilYen		= "鉛筆-*-円-*-*";
	static protected final String BallPenNum		= "ボールペン-*-本-*-*";
	static protected final String MechaPenNum		= "シャープペン-*-本-*-*";
	static protected final String PencilNum		= "鉛筆-*-本-*-*";
	static protected final String PrefectureAll	= "*県-*-*-*-*";
	static protected final String JapanAll		= "日本-*-*-*-*";
	static protected final String VegetablesAll	= "野菜-*-*-*-*";
	static protected final String CarrotAll		= "人参-*-*-*-*";
	static protected final String OnionAll		= "玉葱-*-*-*-*";
	static protected final String PotatoAll		= "じゃが芋-*-*-*-*";
	static protected final String Rochin1All		= "労賃１-*-*-*-*";
	static protected final String Rochin2All		= "労賃２-*-*-*-*";
	static protected final String Rochin3All		= "労賃３-*-*-*-*";
	static protected final String NaibuHoryuAll	= "内部保留-*-*-*-*";
	static protected final String HoryuAll		= "保留-*-*-*-*";
	
	static protected final ExBasePattern patFruitNum		= new ExBasePattern(FruitNum);
	static protected final ExBasePattern patExpFruitNum	= new ExBasePattern(ExpFruitNum);
	static protected final ExBasePattern patFishAll		= new ExBasePattern(FishAll);
	static protected final ExBasePattern patMeatAll		= new ExBasePattern(MeatAll);
	static protected final ExBasePattern patStationeryAll	= new ExBasePattern(StationeryAll);
	static protected final ExBasePattern patAppleAll		= new ExBasePattern(AppleAll);
	static protected final ExBasePattern patOrangeAll		= new ExBasePattern(OrangeAll);
	static protected final ExBasePattern patBananaAll		= new ExBasePattern(BananaAll);
	static protected final ExBasePattern patMelonAll		= new ExBasePattern(MelonAll);
	static protected final ExBasePattern patMangoAll		= new ExBasePattern(MangoAll);
	static protected final ExBasePattern patTunaAll		= new ExBasePattern(TunaAll);
	static protected final ExBasePattern patBonitoAll		= new ExBasePattern(BonitoAll);
	static protected final ExBasePattern patSauryAll		= new ExBasePattern(SauryAll);
	static protected final ExBasePattern patSardineAll	= new ExBasePattern(SardineAll);
	static protected final ExBasePattern patBeefAll		= new ExBasePattern(BeefAll);
	static protected final ExBasePattern patPorkAll		= new ExBasePattern(PorkAll);
	static protected final ExBasePattern patChickenAll	= new ExBasePattern(ChickenAll);
	static protected final ExBasePattern patBallPenYen	= new ExBasePattern(BallPenYen);
	static protected final ExBasePattern patMechaPenYen	= new ExBasePattern(MechaPenYen);
	static protected final ExBasePattern patPencilYen		= new ExBasePattern(PencilYen);
	static protected final ExBasePattern patBallPenNum	= new ExBasePattern(BallPenNum);
	static protected final ExBasePattern patMechaPenNum	= new ExBasePattern(MechaPenNum);
	static protected final ExBasePattern patPencilNum		= new ExBasePattern(PencilNum);
	static protected final ExBasePattern patPrefectureAll = new ExBasePattern(PrefectureAll);
	static protected final ExBasePattern patJapanAll		= new ExBasePattern(JapanAll);
	static protected final ExBasePattern patVegetablesAll	= new ExBasePattern(VegetablesAll);
	static protected final ExBasePattern patCarrotAll		= new ExBasePattern(CarrotAll);
	static protected final ExBasePattern patOnionAll		= new ExBasePattern(OnionAll);
	static protected final ExBasePattern patPotatoAll		= new ExBasePattern(PotatoAll);
	static protected final ExBasePattern patRochin1All	= new ExBasePattern(Rochin1All);
	static protected final ExBasePattern patRochin2All	= new ExBasePattern(Rochin2All);
	static protected final ExBasePattern patRochin3All	= new ExBasePattern(Rochin3All);
	static protected final ExBasePattern patNaibuHoryuAll	= new ExBasePattern(NaibuHoryuAll);
	static protected final ExBasePattern patHoryuAll		= new ExBasePattern(HoryuAll);
	
	static protected final ExBase baseFruitNum		= new ExBase("果物-HAT-個-*-*");
	static protected final ExBase baseExpFruitNum		= new ExBase("高級果物-HAT-個-*-*");
	static protected final ExBase baseFishAll			= new ExBase("魚-HAT-*-*-*");
	static protected final ExBase baseMeatAll			= new ExBase("肉類-HAT-*-*-*");
	static protected final ExBase baseStationeryAll	= new ExBase("文具-HAT-*-*-*");
	static protected final ExBase baseAppleAll		= new ExBase("りんご-NO_HAT-*-*-*");
	static protected final ExBase baseOrangeAll		= new ExBase("みかん-NO_HAT-*-*-*");
	static protected final ExBase baseBananaAll		= new ExBase("ばなな-NO_HAT-*-*-*");
	static protected final ExBase baseMelonAll		= new ExBase("メロン-NO_HAT-*-*-*");
	static protected final ExBase baseMangoAll		= new ExBase("マンゴー-NO_HAT-*-*-*");
	static protected final ExBase baseTunaAll			= new ExBase("まぐろ-HAT-*-*-*");
	static protected final ExBase baseBonitoAll		= new ExBase("かつお-HAT-*-*-*");
	static protected final ExBase baseSauryAll		= new ExBase("さんま-HAT-*-*-*");
	static protected final ExBase baseSardineAll		= new ExBase("いわし-HAT-*-*-*");
	static protected final ExBase baseBeefAll			= new ExBase("牛肉-NO_HAT-*-*-*");
	static protected final ExBase basePorkAll			= new ExBase("豚肉-NO_HAT-*-*-*");
	static protected final ExBase baseChickenAll		= new ExBase("鶏肉-NO_HAT-*-*-*");
	static protected final ExBase baseBallPenYen		= new ExBase("ボールペン-HAT-円-*-*");
	static protected final ExBase baseMechaPenYen		= new ExBase("シャープペン-HAT-円-*-*");
	static protected final ExBase basePencilYen		= new ExBase("鉛筆-HAT-円-*-*");
	static protected final ExBase baseBallPenNum		= new ExBase("ボールペン-NO_HAT-本-*-*");
	static protected final ExBase baseMechaPenNum		= new ExBase("シャープペン-NO_HAT-本-*-*");
	static protected final ExBase basePencilNum		= new ExBase("鉛筆-NO_HAT-本-*-*");
	static protected final ExBase basePrefectureAll	= new ExBase("*県-HAT-*-*-*");
	static protected final ExBase baseJapanAll		= new ExBase("日本-HAT-*-*-*");
	static protected final ExBase baseVegetablesAll	= new ExBase("野菜-NO_HAT-*-*-*");
	static protected final ExBase baseCarrotAll		= new ExBase("人参-HAT-*-*-*");
	static protected final ExBase baseOnionAll		= new ExBase("玉葱-HAT-*-*-*");
	static protected final ExBase basePotatoAll		= new ExBase("じゃが芋-HAT-*-*-*");
	static protected final ExBase baseRochin1All		= new ExBase("労賃１-HAT-*-*-*");
	static protected final ExBase baseRochin2All		= new ExBase("労賃２-NO_HAT-*-*-*");
	static protected final ExBase baseRochin3All		= new ExBase("労賃３-HAT-*-*-*");
	static protected final ExBase baseNaibuHoryuAll	= new ExBase("内部保留-HAT-*-*-*");
	static protected final ExBase baseHoryuAll		= new ExBase("保留-HAT-*-*-*");
	
	static protected final BigDecimal int5 = new BigDecimal(5);
	static protected final BigDecimal int3 = new BigDecimal(3);
	static protected final BigDecimal int2 = new BigDecimal(2);
	static protected final BigDecimal int1 = new BigDecimal(1);
	static protected final BigDecimal f005 = new BigDecimal("0.005");
	static protected final BigDecimal f004 = new BigDecimal("0.004");
	static protected final BigDecimal f0125 = new BigDecimal("0.0125");
	static protected final TransferEntry[] data1 = makeTransferEntry(new Object[][]{
			{patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patOrangeAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patBananaAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patMelonAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, int5},
			{patFishAll, patBonitoAll, ExTransfer.ATTR_RATIO, int3},
			{patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, int1},
			{patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int1},
			{patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int2},
			{patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int1},
			{patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int1},
			{patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005},
			{patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004},
			{patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f0125},
			{patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, null},
			{patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
			{patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
	});
	static protected final TransferEntry[] data10 = makeTransferEntry(new Object[][]{
			{patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patOrangeAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patBananaAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patMelonAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, new BigDecimal("5.000")},
			{patFishAll, patBonitoAll, ExTransfer.ATTR_RATIO, new BigDecimal("3.0")},
			{patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, new BigDecimal("1.0000")},
			{patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, new BigDecimal("1.00000")},
			{patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, new BigDecimal("002.00")},
			{patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, new BigDecimal("1.000000")},
			{patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, new BigDecimal("1.0")},
			{patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, new BigDecimal("0.0050")},
			{patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, new BigDecimal("0.00400")},
			{patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, new BigDecimal("0.0125000")},
			{patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, null},
			{patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
			{patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
	});
	static protected final TransferEntry[] data2 = makeTransferEntry(new Object[][]{
			{patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patOrangeAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patBananaAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patMelonAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, int5},
			{patFishAll, patBonitoAll, ExTransfer.ATTR_RATIO, int3},
			{patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, int5},
			{patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int3},
			{patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int5},
			{patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int5},
			{patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int5},
			{patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005},
			{patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f0125},
			{patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f004},
			{patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, null},
			{patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
			{patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
	});
	static protected final TransferEntry[] data3 = makeTransferEntry(new Object[][]{
			{patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patOrangeAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patBananaAll, patBeefAll, ExTransfer.ATTR_RATIO, int1},
			{patMelonAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patFishAll, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f004},
			{patFishAll, patTunaAll, ExTransfer.ATTR_MULTIPLY, f004},
			{patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int5},
			{patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int5},
			{patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int5},
			{patBallPenYen, patBeefAll, ExTransfer.ATTR_RATIO, int1},
			{patBallPenYen, patBallPenNum, ExTransfer.ATTR_RATIO, int1},
			{patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f0125},
			{patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f004},
			{patPrefectureAll, patJapanAll, ExTransfer.ATTR_HAT, null},
			{patRochin3All, patHoryuAll, ExTransfer.ATTR_AGGRE, null},
	});
	static protected final TransferEntry[] data4 = makeTransferEntry(new Object[][]{
			{patAppleAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patOrangeAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patBananaAll, patBeefAll, ExTransfer.ATTR_RATIO, int1},
			{patFishAll, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f004},
			{patFishAll, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004},
			{patFruitNum, patAppleAll, ExTransfer.ATTR_RATIO, int1},
			{patFruitNum, patOrangeAll, ExTransfer.ATTR_RATIO, int1},
			{patFruitNum, patBananaAll, ExTransfer.ATTR_RATIO, int1},
			{patBallPenYen, patBeefAll, ExTransfer.ATTR_RATIO, int1},
			{patBallPenYen, patPorkAll, ExTransfer.ATTR_RATIO, int1},
			{patBallPenYen, patChickenAll, ExTransfer.ATTR_RATIO, int1},
			{patMechaPenNum, patMechaPenYen, ExTransfer.ATTR_MULTIPLY, f0125},
			{patPencilNum, patPencilYen, ExTransfer.ATTR_MULTIPLY, f004},
			{patRochin3All, patHoryuAll, ExTransfer.ATTR_HAT, null},
	});
	static protected final TransferEntry[] data5 = makeTransferEntry(new Object[][]{
			{patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patOrangeAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patBananaAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patMelonAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, int5},
			{patFishAll, patBonitoAll, ExTransfer.ATTR_RATIO, int3},
			{patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, int1},
			{patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int1},
			{patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int2},
			{patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int1},
			{patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int1},
			{patVegetablesAll, patCarrotAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
			{patVegetablesAll, patOnionAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
			{patVegetablesAll, patPotatoAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
			{patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005},
			{patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004},
			{patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f0125},
			{patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, null},
			{patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
			{patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
	});
	
	static protected final ExBase nFruitNum = new ExBase("果物-NO_HAT-個-#-#");
	static protected final ExBase hFruitNum = new ExBase("果物-HAT-個-#-#");
	static protected final ExBase nExpFruitNum = new ExBase("高級果物-NO_HAT-個-#-#");
	static protected final ExBase hExpFruitNum = new ExBase("高級果物-HAT-個-#-#");
	static protected final ExBase nFisheryYen = new ExBase("魚介類-NO_HAT-円-#-#");
	static protected final ExBase hFisheryYen = new ExBase("魚介類-HAT-円-#-#");
	static protected final ExBase nMuttonYen = new ExBase("羊肉-NO_HAT-円-#-#");
	static protected final ExBase hMuttonYen = new ExBase("羊肉-HAT-円-#-#");
	static protected final ExBase nBallPenNum = new ExBase("ボールペン-NO_HAT-本-#-#");
	static protected final ExBase hBallPenNum = new ExBase("ボールペン-HAT-本-#-#");
	static protected final ExBase nMechaPenNum = new ExBase("シャープペン-NO_HAT-本-#-#");
	static protected final ExBase hMechaPenNum = new ExBase("シャープペン-HAT-本-#-#");
	static protected final ExBase nPencilNum = new ExBase("鉛筆-NO_HAT-本-#-#");
	static protected final ExBase hPencilNum = new ExBase("鉛筆-HAT-本-#-#");
	static protected final ExBase nEraserNum = new ExBase("消しゴム-NO_HAT-個-#-#");
	static protected final ExBase hEraserNum = new ExBase("消しゴム-HAT-個-#-#");
	static protected final ExBase nStationeryNum = new ExBase("文具-NO_HAT-個-#-#");
	static protected final ExBase hStationeryNum = new ExBase("文具-HAT-個-#-#");
	static protected final ExBase nAppleYen = new ExBase("りんご-NO_HAT-円-#-#");
	static protected final ExBase hAppleYen = new ExBase("りんご-HAT-円-#-#");
	static protected final ExBase nOrangeYen = new ExBase("みかん-NO_HAT-円-#-#");
	static protected final ExBase hOrangeYen = new ExBase("みかん-HAT-円-#-#");
	static protected final ExBase nBananaYen = new ExBase("ばなな-NO_HAT-円-#-#");
	static protected final ExBase hBananaYen = new ExBase("ばなな-HAT-円-#-#");
	static protected final ExBase nMelonYen = new ExBase("メロン-NO_HAT-円-#-#");
	static protected final ExBase hMelonYen = new ExBase("メロン-HAT-円-#-#");
	static protected final ExBase nMangoYen = new ExBase("マンゴー-NO_HAT-円-#-#");
	static protected final ExBase hMangoYen = new ExBase("マンゴー-HAT-円-#-#");
	static protected final ExBase nGrapefruitYen = new ExBase("グレープフルーツ-NO_HAT-円-#-#");
	static protected final ExBase hGrapefruitYen = new ExBase("グレープフルーツ-HAT-円-#-#");
	static protected final ExBase nFishYen = new ExBase("魚-NO_HAT-円-#-#");
	static protected final ExBase hFishYen = new ExBase("魚-HAT-円-#-#");
	static protected final ExBase nMeatYen = new ExBase("肉類-NO_HAT-円-#-#");
	static protected final ExBase hMeatYen = new ExBase("肉類-HAT-円-#-#");
	static protected final ExBase nBallPenYen = new ExBase("ボールペン-NO_HAT-円-#-#");
	static protected final ExBase hBallPenYen = new ExBase("ボールペン-HAT-円-#-#");
	static protected final ExBase nMechaPenYen = new ExBase("シャープペン-NO_HAT-円-#-#");
	static protected final ExBase hMechaPenYen = new ExBase("シャープペン-HAT-円-#-#");
	static protected final ExBase nPencilYen = new ExBase("鉛筆-NO_HAT-円-#-#");
	static protected final ExBase hPencilYen = new ExBase("鉛筆-HAT-円-#-#");
	static protected final ExBase nEraserYen = new ExBase("消しゴム-NO_HAT-円-#-#");
	static protected final ExBase hEraserYen = new ExBase("消しゴム-HAT-円-#-#");
	static protected final ExBase nStationeryYen = new ExBase("文具-NO_HAT-円-#-#");
	static protected final ExBase hStationeryYen = new ExBase("文具-HAT-円-#-#");
	static protected final ExBase nTunaYen = new ExBase("まぐろ-NO_HAT-円-#-#");
	static protected final ExBase hTunaYen = new ExBase("まぐろ-HAT-円-#-#");
	static protected final ExBase nBonitoYen = new ExBase("かつお-NO_HAT-円-#-#");
	static protected final ExBase hBonitoYen = new ExBase("かつお-HAT-円-#-#");
	static protected final ExBase nSauryYen = new ExBase("さんま-NO_HAT-円-#-#");
	static protected final ExBase hSauryYen = new ExBase("さんま-HAT-円-#-#");
	static protected final ExBase nSardineYen = new ExBase("いわし-NO_HAT-円-#-#");
	static protected final ExBase hSardineYen = new ExBase("いわし-HAT-円-#-#");
	static protected final ExBase nBeefYen = new ExBase("牛肉-NO_HAT-円-#-#");
	static protected final ExBase hBeefYen = new ExBase("牛肉-HAT-円-#-#");
	static protected final ExBase nPorkYen = new ExBase("豚肉-NO_HAT-円-#-#");
	static protected final ExBase hPorkYen = new ExBase("豚肉-HAT-円-#-#");
	static protected final ExBase nChickenYen = new ExBase("鶏肉-NO_HAT-円-#-#");
	static protected final ExBase hChickenYen = new ExBase("鶏肉-HAT-円-#-#");
	static protected final ExBase nPrefChibaNum = new ExBase("千葉県-NO_HAT-人-#-#");
	static protected final ExBase hPrefChibaNum = new ExBase("千葉県-HAT-人-#-#");
	static protected final ExBase nPrefTokyoNum = new ExBase("東京都-NO_HAT-人-#-#");
	static protected final ExBase hPrefTokyoNum = new ExBase("東京都-HAT-人-#-#");
	static protected final ExBase nPrefKanagawaNum = new ExBase("神奈川県-NO_HAT-人-#-#");
	static protected final ExBase hPrefKanagawaNum = new ExBase("神奈川県-HAT-人-#-#");
	static protected final ExBase nPrefSaitamaNum = new ExBase("埼玉県-NO_HAT-人-#-#");
	static protected final ExBase hPrefSaitamaNum = new ExBase("埼玉県-HAT-人-#-#");
	static protected final ExBase nPrefTochigiNum = new ExBase("栃木県-NO_HAT-人-#-#");
	static protected final ExBase hPrefTochigiNum = new ExBase("栃木県-HAT-人-#-#");
	static protected final ExBase nPrefGunmaNum = new ExBase("群馬県-NO_HAT-人-#-#");
	static protected final ExBase hPrefGunmaNum = new ExBase("群馬県-HAT-人-#-#");
	static protected final ExBase nPrefIbarakiNum = new ExBase("茨城県-NO_HAT-人-#-#");
	static protected final ExBase hPrefIbarakiNum = new ExBase("茨城県-HAT-人-#-#");
	static protected final ExBase nJapanNum = new ExBase("日本-NO_HAT-人-#-#");
	static protected final ExBase hJapanNum = new ExBase("日本-HAT-人-#-#");
	static protected final ExBase nVegetablesYen = new ExBase("野菜-NO_HAT-円-#-#");
	static protected final ExBase hVegetablesYen = new ExBase("野菜-HAT-円-#-#");
	static protected final ExBase nCarrotYen = new ExBase("人参-NO_HAT-円-#-#");
	static protected final ExBase hCarrotYen = new ExBase("人参-HAT-円-#-#");
	static protected final ExBase nOnionYen = new ExBase("玉葱-NO_HAT-円-#-#");
	static protected final ExBase hOnionYen = new ExBase("玉葱-HAT-円-#-#");
	static protected final ExBase nPotatoYen = new ExBase("じゃが芋-NO_HAT-円-#-#");
	static protected final ExBase hPotatoYen = new ExBase("じゃが芋-HAT-円-#-#");
	static protected final ExBase nRochin1Yen = new ExBase("労賃１-NO_HAT-円-#-#");
	static protected final ExBase hRochin1Yen = new ExBase("労賃１-HAT-円-#-#");
	static protected final ExBase nRochin2Yen = new ExBase("労賃２-NO_HAT-円-#-#");
	static protected final ExBase hRochin2Yen = new ExBase("労賃２-HAT-円-#-#");
	static protected final ExBase nRochin3Yen = new ExBase("労賃３-NO_HAT-円-#-#");
	static protected final ExBase hRochin3Yen = new ExBase("労賃３-HAT-円-#-#");
	static protected final ExBase nNaibuHoryuYen = new ExBase("内部保留-NO_HAT-円-#-#");
	static protected final ExBase hNaibuHoryuYen = new ExBase("内部保留-HAT-円-#-#");
	static protected final ExBase nHoryuYen = new ExBase("保留-NO_HAT-円-#-#");
	static protected final ExBase hHoryuYen = new ExBase("保留-HAT-円-#-#");
	
	static class TransferEntry {
		static private final ExTransfer fTrans = new ExTransfer();
		private final ExBasePattern patFrom;
		private final ExBasePattern patTo;
		private final TransferAttribute attr;
		
		public TransferEntry(ExBasePattern from, ExBasePattern to, String attrName, BigDecimal attrValue) {
			this.patFrom = from;
			this.patTo   = to;
			this.attr    = fTrans.makeAttribute(attrName, attrValue);
		}
		
		public ExBasePattern from() {
			return patFrom;
		}
		
		public ExBasePattern to() {
			return patTo;
		}
		
		public TransferAttribute attr() {
			return attr;
		}
		
		public String attrName() {
			return attr.getName();
		}
		
		public BigDecimal attrValue() {
			return attr.getValue();
		}
	}
	
	static TransferEntry[] makeTransferEntry(Object[][] args) {
		TransferEntry[] entries = new TransferEntry[args.length];

		for (int i = 0; i < args.length; i++) {
			Object[] params = args[i];
			if (params.length != 4)
				throw new IllegalArgumentException("Illegal parameter count at [" + i + "] : " + params.length);
			TransferEntry entry = new TransferEntry((ExBasePattern)params[0], (ExBasePattern)params[1], (String)params[2], (BigDecimal)params[3]);
			entries[i] = entry;
		}
		
		return entries;
	}
	
	static ExTransfer makeTransfer(boolean useIndex, TransferEntry[] entries) {
		ExTransfer trans = new ExTransfer(useIndex);
		if (entries.length > 0) {
			for (TransferEntry entry : entries) {
				trans.put(entry.from(), entry.to(), entry.attr());
			}
		}
		return trans;
	}
	
	static void putAllTransfer(ExTransfer transfer, TransferEntry[] entries) {
		for (TransferEntry entry : entries) {
			transfer.put(entry.from(), entry.to(), entry.attr());
		}
	}
	
	static void setAllTransfer(ExTransfer transfer, TransferEntry[] entries) {
		for (TransferEntry entry : entries) {
			transfer.set(entry.from(), entry.to(), entry.attr());
		}
	}
	
	static int calcHashCode(TransferAttribute attr) {
		if (attr == null) {
			return 0;
		} else {
			BigDecimal val = attr.getValue();
			int hn = attr.getName().hashCode();
			int hv = 0;
			if (val != null) {
				if (BigDecimal.ZERO.compareTo(val) == 0) {
					hv = BigDecimal.ZERO.hashCode();
				} else {
					hv = val.stripTrailingZeros().hashCode();
				}
			}
			return (hn ^ hv);
		}
	}
	
	static int calcHashCode(TransferEntry[] entries) {
		LinkedHashMap<ExBasePattern, Integer> map = new LinkedHashMap<ExBasePattern,Integer>();
		for (TransferEntry entry : entries) {
			Integer hash = map.get(entry.from());
			int ehash = entry.to().hashCode() ^ calcHashCode(entry.attr());
			if (hash == null) {
				hash = new Integer(ehash);
			} else {
				hash = new Integer(hash.intValue() + ehash);
			}
			map.put(entry.from(), hash);
		}
		return map.hashCode();
	}
	
	static void assertEqualTransfer(ExTransfer transfer, TransferEntry[] entries) {
		assertEquals(transfer.numElements, entries.length);
		if (entries.length > 0) {
			assertFalse(transfer.map.isEmpty());
			assertFalse(transfer.backmap.isEmpty());
			if (transfer.patIndex != null) {
				assertFalse(transfer.patIndex.isEmpty());
			}
		} else {
			assertTrue(transfer.map.isEmpty());
			assertTrue(transfer.backmap.isEmpty());
			if (transfer.patIndex != null) {
				assertTrue(transfer.patIndex.isEmpty());
			}
		}
		
		HashMap<ExBasePattern, Integer> nums = new HashMap<ExBasePattern,Integer>();
		ExTransfer.ExBasePatternMultiMap checkBackMap = new ExTransfer.ExBasePatternMultiMap();
		
		for (int i = 0; i < entries.length; i++) {
			TransferEntry entry = entries[i];
			Integer cnt = nums.get(entry.from());
			if (cnt == null)
				nums.put(entry.from(), Integer.valueOf(1));
			else
				nums.put(entry.from(), Integer.valueOf(cnt.intValue() + 1));
			
			ITransferEntryMap entrymap = transfer.map.get(entry.from());
			assertNotNull("TransferEntry[" + i + "].from[" + String.valueOf(entry.from()) + "] not found in ExTransfer.", entrymap);
			TransferAttribute attr = entrymap.get(entry.to());
			assertNotNull("TransferEntry[" + i + "].to[" + String.valueOf(entry.to()) + "] not found in ExTransfer.", attr);
			assertEquals("TransferEntry[" + i + "].attr[" + String.valueOf(entry.attr()) + "] not equal ExTransfer.attr[" + String.valueOf(attr) + "]", entry.attr(), attr);
			
			checkBackMap.put(entry.to(), entry.from());
		}
		
		//--- check num
		int tfNum = transfer.map.size();
		int efNum = nums.size();
		assertEquals("TransferEntry's from(" + efNum + ") not equal ExTransfer's from(" + tfNum +")", efNum, tfNum);
		assertEquals("TransferEntry's from entries not equal ExTransfer's from entries.", nums.keySet(), transfer.map.keySet());
		for (ExBasePattern pat : nums.keySet()) {
			int num = nums.get(pat).intValue();
			ITransferEntryMap entrymap = transfer.map.get(pat);
			int tnum = entrymap.size();
			assertEquals("from[" + pat + "] pattern map elements not equals : TransferEntries(" + num + ") ExTransfer(" + tnum + ")", num, tnum);
			if (transfer.patIndex != null) {
				assertTrue("from[" + pat + "] pattern not exist in Pattern indices of ExTransfer.", ExBasePatternIndexSetTest.contains(transfer.patIndex, pat));
			}
		}
		
		//--- check backmap
		assertEquals(checkBackMap, transfer.backmap);
	}
	
	static void printException(String methodName, Throwable ex, Object...args) {
		StringBuilder sb = new StringBuilder();
		sb.append("[Check] ");
		sb.append(methodName);
		sb.append("(");
		if (args.length > 0) {
			if (args[0] instanceof BigDecimal)
				sb.append(((BigDecimal)args[0]).toPlainString());
			else
				sb.append(args[0]);
			for (int i = 1; i < args.length; i++) {
				sb.append(",");
				if (args[i] instanceof BigDecimal)
					sb.append(((BigDecimal)args[i]).toPlainString());
				else
					sb.append(args[i]);
			}
		}
		sb.append(") : ");
		sb.append(ex);
		
		System.out.println(sb.toString());
	}
	
	static ExBase toBase(String hatKey, ExBasePattern pattern) {
		if (pattern == null)
			return null;
		return new ExBase(pattern.getNameKey(), hatKey, pattern.getUnitKey(), pattern.getTimeKey(), pattern.getSubjectKey());
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * {@link exalge2.ExTransfer#ExTransfer()} のためのテスト・メソッド。
	 */
	public void testExTransfer() {
		ExTransfer t = new ExTransfer();
		assertTrue(t.map.isEmpty());
		assertTrue(t.backmap.isEmpty());
		assertTrue(t.numElements == 0);
		if (t.patIndex != null) {
			assertTrue(t.patIndex.isEmpty());
		}
	}

	/**
	 * {@link exalge2.ExTransfer#ExTransfer(int)} のためのテスト・メソッド。
	 */
	public void testExTransferInt() {
		ExTransfer t = new ExTransfer(10);
		assertTrue(t.map.isEmpty());
		assertTrue(t.backmap.isEmpty());
		assertTrue(t.numElements == 0);
		if (t.patIndex != null) {
			assertTrue(t.patIndex.isEmpty());
		}
	}

	/**
	 * {@link exalge2.ExTransfer#ExTransfer(int, float)} のためのテスト・メソッド。
	 */
	public void testExTransferIntFloat() {
		ExTransfer t = new ExTransfer(10, 0.75f);
		assertTrue(t.map.isEmpty());
		assertTrue(t.backmap.isEmpty());
		assertTrue(t.numElements == 0);
		if (t.patIndex != null) {
			assertTrue(t.patIndex.isEmpty());
		}
	}
	
	/**
	 * {@link exalge2.ExTransfer#ExTransfer(boolean)} のためのテスト・メソッド。
	 */
	public void testExTransferBoolean() {
		ExTransfer t1 = new ExTransfer(false);
		assertTrue(t1.map.isEmpty());
		assertTrue(t1.backmap.isEmpty());
		assertTrue(t1.numElements == 0);
		assertTrue(null == t1.patIndex);
		
		ExTransfer t2 = new ExTransfer(true);
		assertTrue(t2.map.isEmpty());
		assertTrue(t2.backmap.isEmpty());
		assertTrue(t2.numElements == 0);
		assertTrue(null != t2.patIndex);
		assertTrue(t2.patIndex.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer#normalizeAttributeName(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNormalizeAttributeName() {
		ExTransfer trans = new ExTransfer();
		String attr;
		
		//--- null
		attr = trans.normalizeAttributeName(null);
		assertSame(ExTransfer.ATTR_AGGRE, attr);
		//--- empty
		attr = trans.normalizeAttributeName("");
		assertSame(ExTransfer.ATTR_AGGRE, attr);
		
		//--- AGGRE
		attr = trans.normalizeAttributeName(ExTransfer.ATTR_AGGRE);
		assertSame(ExTransfer.ATTR_AGGRE, attr);
		attr = trans.normalizeAttributeName("aggre");
		assertSame(ExTransfer.ATTR_AGGRE, attr);
		attr = trans.normalizeAttributeName("AGGRE");
		assertSame(ExTransfer.ATTR_AGGRE, attr);
		attr = trans.normalizeAttributeName("AgGrE");
		assertSame(ExTransfer.ATTR_AGGRE, attr);
		
		//--- HAT
		attr = trans.normalizeAttributeName(ExTransfer.ATTR_HAT);
		assertSame(ExTransfer.ATTR_HAT, attr);
		attr = trans.normalizeAttributeName("hat");
		assertSame(ExTransfer.ATTR_HAT, attr);
		attr = trans.normalizeAttributeName("HAT");
		assertSame(ExTransfer.ATTR_HAT, attr);
		attr = trans.normalizeAttributeName("HaT");
		assertSame(ExTransfer.ATTR_HAT, attr);
		
		//--- RATIO
		attr = trans.normalizeAttributeName(ExTransfer.ATTR_RATIO);
		assertSame(ExTransfer.ATTR_RATIO, attr);
		attr = trans.normalizeAttributeName("ratio");
		assertSame(ExTransfer.ATTR_RATIO, attr);
		attr = trans.normalizeAttributeName("RATIO");
		assertSame(ExTransfer.ATTR_RATIO, attr);
		attr = trans.normalizeAttributeName("rAtIo");
		assertSame(ExTransfer.ATTR_RATIO, attr);
		
		//--- MULTIPLY
		attr = trans.normalizeAttributeName(ExTransfer.ATTR_MULTIPLY);
		assertSame(ExTransfer.ATTR_MULTIPLY, attr);
		attr = trans.normalizeAttributeName("multiply");
		assertSame(ExTransfer.ATTR_MULTIPLY, attr);
		attr = trans.normalizeAttributeName("MULTIPLY");
		assertSame(ExTransfer.ATTR_MULTIPLY, attr);
		attr = trans.normalizeAttributeName("MuLtIpLy");
		assertSame(ExTransfer.ATTR_MULTIPLY, attr);
		
		//--- Error
		attr = trans.normalizeAttributeName("agre");
		assertNull(attr);
		attr = trans.normalizeAttributeName("hato");
		assertNull(attr);
		attr = trans.normalizeAttributeName("rate");
		assertNull(attr);
		attr = trans.normalizeAttributeName("multiple");
		assertNull(attr);
		attr = trans.normalizeAttributeName(" ");
		assertNull(attr);
	}

	/**
	 * {@link exalge2.ExTransfer#makeAttribute(java.lang.String, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testMakeAttribute() {
		ExTransfer trans = new ExTransfer();
		TransferAttribute attr;
		boolean coughtException;
		final BigDecimal vZERO = BigDecimal.ZERO;
		final BigDecimal vPONE = new BigDecimal(1);
		final BigDecimal vMONE = new BigDecimal(-1);
		final BigDecimal vPPONE = new BigDecimal("0.1");
		final BigDecimal vMPONE = new BigDecimal("-0.1");
		
		//--- null
		attr = trans.makeAttribute(null, null);
		assertSame(TransferAttribute.AGGREGATE, attr);
		attr = trans.makeAttribute(null, vZERO);
		assertSame(TransferAttribute.AGGREGATE, attr);
		attr = trans.makeAttribute(null, vPPONE);
		assertSame(TransferAttribute.AGGREGATE, attr);
		attr = trans.makeAttribute(null, vMPONE);
		assertSame(TransferAttribute.AGGREGATE, attr);
		
		//--- empty
		attr = trans.makeAttribute("", null);
		assertSame(TransferAttribute.AGGREGATE, attr);
		attr = trans.makeAttribute("", vZERO);
		assertSame(TransferAttribute.AGGREGATE, attr);
		attr = trans.makeAttribute("", vPPONE);
		assertSame(TransferAttribute.AGGREGATE, attr);
		attr = trans.makeAttribute("", vMPONE);
		assertSame(TransferAttribute.AGGREGATE, attr);
		
		//--- aggre
		attr = trans.makeAttribute("AGGRE", null);
		assertSame(TransferAttribute.AGGREGATE, attr);
		attr = trans.makeAttribute("AGGRE", vZERO);
		assertSame(TransferAttribute.AGGREGATE, attr);
		attr = trans.makeAttribute("AGGRE", vPPONE);
		assertSame(TransferAttribute.AGGREGATE, attr);
		attr = trans.makeAttribute("AGGRE", vMPONE);
		assertSame(TransferAttribute.AGGREGATE, attr);
		
		//--- hat
		attr = trans.makeAttribute("HAT", null);
		assertSame(TransferAttribute.HAT_AGGREGATE, attr);
		attr = trans.makeAttribute("HAT", vZERO);
		assertSame(TransferAttribute.HAT_AGGREGATE, attr);
		attr = trans.makeAttribute("HAT", vPPONE);
		assertSame(TransferAttribute.HAT_AGGREGATE, attr);
		attr = trans.makeAttribute("HAT", vMPONE);
		assertSame(TransferAttribute.HAT_AGGREGATE, attr);
		
		//--- ratio(normal)
		attr = trans.makeAttribute("RATIO", vPPONE);
		assertSame(ExTransfer.ATTR_RATIO, attr.getName());
		assertEquals(vPPONE, attr.getValue());
		attr = trans.makeAttribute("RATIO", vPONE);
		assertSame(ExTransfer.ATTR_RATIO, attr.getName());
		assertEquals(vPONE, attr.getValue());
		attr = trans.makeAttribute("RATIO", vZERO);
		assertSame(ExTransfer.ATTR_RATIO, attr.getName());
		assertEquals(vZERO, attr.getValue());
		//--- ratio(error)
		try {
			attr = trans.makeAttribute("RATIO", vMONE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testMakeAttribute(\"RATIO\"," + vMONE.toPlainString() + ") : " + ex.toString());
		}
		assertTrue(coughtException);
		try {
			attr = trans.makeAttribute("RATIO", vMPONE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testMakeAttribute(\"RATIO\"," + vMPONE.toPlainString() + ") : " + ex.toString());
		}
		assertTrue(coughtException);
		
		//--- multiply
		attr = trans.makeAttribute("MULTIPLY", vZERO);
		assertSame(ExTransfer.ATTR_MULTIPLY, attr.getName());
		assertEquals(vZERO, attr.getValue());
		attr = trans.makeAttribute("MULTIPLY", vPONE);
		assertSame(ExTransfer.ATTR_MULTIPLY, attr.getName());
		assertEquals(vPONE, attr.getValue());
		attr = trans.makeAttribute("MULTIPLY", vMONE);
		assertSame(ExTransfer.ATTR_MULTIPLY, attr.getName());
		assertEquals(vMONE, attr.getValue());
		attr = trans.makeAttribute("MULTIPLY", vPPONE);
		assertSame(ExTransfer.ATTR_MULTIPLY, attr.getName());
		assertEquals(vPPONE, attr.getValue());
		attr = trans.makeAttribute("MULTIPLY", vMPONE);
		assertSame(ExTransfer.ATTR_MULTIPLY, attr.getName());
		assertEquals(vMPONE, attr.getValue());
		
		//--- error
		try {
			attr = trans.makeAttribute(ExTransfer.ATTR_RATIO, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testMakeAttribute(\"" + ExTransfer.ATTR_RATIO + "\",null) : " + ex.toString());
		}
		assertTrue(coughtException);
		try {
			attr = trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testMakeAttribute(\"" + ExTransfer.ATTR_MULTIPLY + "\",null) : " + ex.toString());
		}
		assertTrue(coughtException);
		try {
			attr = trans.makeAttribute("agre", vPONE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testMakeAttribute(\"agre\"," + vPONE.toPlainString() + ") : " + ex.toString());
		}
		assertTrue(coughtException);
		try {
			attr = trans.makeAttribute("hato", vPONE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testMakeAttribute(\"hato\"," + vPONE.toPlainString() + ") : " + ex.toString());
		}
		assertTrue(coughtException);
		try {
			attr = trans.makeAttribute("rate", vPONE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testMakeAttribute(\"rate\"," + vPONE.toPlainString() + ") : " + ex.toString());
		}
		assertTrue(coughtException);
		try {
			attr = trans.makeAttribute("multiple", vPONE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testMakeAttribute(\"multiple\"," + vPONE.toPlainString() + ") : " + ex.toString());
		}
		assertTrue(coughtException);
		try {
			attr = trans.makeAttribute(" ", vPONE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testMakeAttribute(\" \"," + vPONE.toPlainString() + ") : " + ex.toString());
		}
		assertTrue(coughtException);
	}

	/**
	 * {@link exalge2.ExTransfer#put(exalge2.ExBasePattern, exalge2.ExBasePattern, exalge2.ExTransfer.TransferAttribute)} のためのテスト・メソッド。
	 */
	public void testPutExBasePatternExBasePatternTransferAttribute() {
		final BigDecimal int5 = new BigDecimal(5);
		final BigDecimal int3 = new BigDecimal(3);
		final BigDecimal int2 = new BigDecimal(2);
		final BigDecimal int1 = new BigDecimal(1);
		final BigDecimal f005 = new BigDecimal("0.005");
		final BigDecimal f004 = new BigDecimal("0.004");
		final BigDecimal f0125 = new BigDecimal("0.0125");
		
		//----------
		// no index
		//----------
		ITransferEntryMap entrymap;
		ExTransfer trans = new ExTransfer();
		trans.put(patAppleAll, patFruitNum, TransferAttribute.AGGREGATE);
		trans.put(patOrangeAll, patFruitNum, TransferAttribute.AGGREGATE);
		trans.put(patBananaAll, patFruitNum, TransferAttribute.AGGREGATE);
		trans.put(patMelonAll, patExpFruitNum, TransferAttribute.AGGREGATE);
		trans.put(patMangoAll, patExpFruitNum, TransferAttribute.AGGREGATE);
		trans.put(patFishAll, patTunaAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		trans.put(patFishAll, patBonitoAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		trans.put(patFishAll, patSauryAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		trans.put(patFishAll, patSardineAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		trans.put(patMeatAll, patBeefAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int2));
		trans.put(patMeatAll, patPorkAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int1));
		trans.put(patMeatAll, patChickenAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int1));
		trans.put(patBallPenYen, patBallPenNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f005));
		trans.put(patMechaPenYen, patMechaPenNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		trans.put(patPencilYen, patPencilNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f0125));
		trans.put(patPrefectureAll, patJapanAll, TransferAttribute.AGGREGATE);
		trans.put(patRochin1All, patNaibuHoryuAll, TransferAttribute.HAT_AGGREGATE);
		trans.put(patRochin2All, patNaibuHoryuAll, TransferAttribute.HAT_AGGREGATE);
		
		//--- check from
		assertEquals(trans.numElements, 18);
		assertEquals(trans.map.size(), 13);
		assertEquals(trans.backmap.size(), 14);
		assertTrue(trans.map.containsKey(patAppleAll));
		assertTrue(trans.map.containsKey(patOrangeAll));
		assertTrue(trans.map.containsKey(patBananaAll));
		assertTrue(trans.map.containsKey(patMelonAll));
		assertTrue(trans.map.containsKey(patMangoAll));
		assertTrue(trans.map.containsKey(patFishAll));
		assertTrue(trans.map.containsKey(patMeatAll));
		assertTrue(trans.map.containsKey(patBallPenYen));
		assertTrue(trans.map.containsKey(patMechaPenYen));
		assertTrue(trans.map.containsKey(patPencilYen));
		assertTrue(trans.map.containsKey(patPrefectureAll));
		assertTrue(trans.map.containsKey(patRochin1All));
		assertTrue(trans.map.containsKey(patRochin2All));
		//--- aggre
		entrymap = trans.map.get(patAppleAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patOrangeAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patBananaAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patMelonAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patExpFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patMangoAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patExpFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patPrefectureAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patJapanAll), TransferAttribute.AGGREGATE);
		//--- hat
		entrymap = trans.map.get(patRochin1All);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patNaibuHoryuAll), TransferAttribute.HAT_AGGREGATE);
		entrymap = trans.map.get(patRochin2All);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patNaibuHoryuAll), TransferAttribute.HAT_AGGREGATE);
		//--- ratio
		entrymap = trans.map.get(patFishAll);
		assertEquals(entrymap.size(), 4);
		assertEquals(entrymap.get(patTunaAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		assertEquals(entrymap.get(patBonitoAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		assertEquals(entrymap.get(patSauryAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		assertEquals(entrymap.get(patSardineAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		//--- multiply
		entrymap = trans.map.get(patMeatAll);
		assertEquals(entrymap.size(), 3);
		assertEquals(entrymap.get(patBeefAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int2));
		assertEquals(entrymap.get(patPorkAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int1));
		assertEquals(entrymap.get(patChickenAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int1));
		entrymap = trans.map.get(patBallPenYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patBallPenNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f005));
		entrymap = trans.map.get(patMechaPenYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patMechaPenNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		entrymap = trans.map.get(patPencilYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patPencilNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f0125));
		//--- backmap
		assertTrue(trans.backmap.contains(patFruitNum, patAppleAll));
		assertTrue(trans.backmap.contains(patFruitNum, patOrangeAll));
		assertTrue(trans.backmap.contains(patFruitNum, patBananaAll));
		assertTrue(trans.backmap.contains(patExpFruitNum, patMelonAll));
		assertTrue(trans.backmap.contains(patExpFruitNum, patMangoAll));
		assertTrue(trans.backmap.contains(patTunaAll, patFishAll));
		assertTrue(trans.backmap.contains(patBonitoAll, patFishAll));
		assertTrue(trans.backmap.contains(patSauryAll, patFishAll));
		assertTrue(trans.backmap.contains(patSardineAll, patFishAll));
		assertTrue(trans.backmap.contains(patBeefAll, patMeatAll));
		assertTrue(trans.backmap.contains(patPorkAll, patMeatAll));
		assertTrue(trans.backmap.contains(patChickenAll, patMeatAll));
		assertTrue(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertTrue(trans.backmap.contains(patMechaPenNum, patMechaPenYen));
		assertTrue(trans.backmap.contains(patPencilNum, patPencilYen));
		assertTrue(trans.backmap.contains(patJapanAll, patPrefectureAll));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin1All));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		
		//--- overwrite
		trans.put(patFishAll, patSauryAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		trans.put(patFishAll, patSardineAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		trans.put(patMeatAll, patBeefAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.put(patMeatAll, patPorkAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.put(patMeatAll, patChickenAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.put(patMechaPenYen, patMechaPenNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f0125));
		trans.put(patPencilYen, patPencilNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		//--- check
		assertEquals(trans.numElements, 18);
		assertEquals(trans.map.size(), 13);
		assertEquals(trans.backmap.size(), 14);
		assertTrue(trans.map.containsKey(patAppleAll));
		assertTrue(trans.map.containsKey(patOrangeAll));
		assertTrue(trans.map.containsKey(patBananaAll));
		assertTrue(trans.map.containsKey(patMelonAll));
		assertTrue(trans.map.containsKey(patMangoAll));
		assertTrue(trans.map.containsKey(patFishAll));
		assertTrue(trans.map.containsKey(patMeatAll));
		assertTrue(trans.map.containsKey(patBallPenYen));
		assertTrue(trans.map.containsKey(patMechaPenYen));
		assertTrue(trans.map.containsKey(patPencilYen));
		assertTrue(trans.map.containsKey(patPrefectureAll));
		assertTrue(trans.map.containsKey(patRochin1All));
		assertTrue(trans.map.containsKey(patRochin2All));
		//--- aggre
		entrymap = trans.map.get(patAppleAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patOrangeAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patBananaAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patMelonAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patExpFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patMangoAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patExpFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patPrefectureAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patJapanAll), TransferAttribute.AGGREGATE);
		//--- hat
		entrymap = trans.map.get(patRochin1All);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patNaibuHoryuAll), TransferAttribute.HAT_AGGREGATE);
		entrymap = trans.map.get(patRochin2All);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patNaibuHoryuAll), TransferAttribute.HAT_AGGREGATE);
		//--- ratio
		entrymap = trans.map.get(patFishAll);
		assertEquals(entrymap.size(), 4);
		assertEquals(entrymap.get(patTunaAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		assertEquals(entrymap.get(patBonitoAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		assertEquals(entrymap.get(patSauryAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		assertEquals(entrymap.get(patSardineAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		//--- multiply
		entrymap = trans.map.get(patMeatAll);
		assertEquals(entrymap.size(), 3);
		assertEquals(entrymap.get(patBeefAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		assertEquals(entrymap.get(patPorkAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		assertEquals(entrymap.get(patChickenAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		entrymap = trans.map.get(patBallPenYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patBallPenNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f005));
		entrymap = trans.map.get(patMechaPenYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patMechaPenNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f0125));
		entrymap = trans.map.get(patPencilYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patPencilNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		//--- backmap
		assertTrue(trans.backmap.contains(patFruitNum, patAppleAll));
		assertTrue(trans.backmap.contains(patFruitNum, patOrangeAll));
		assertTrue(trans.backmap.contains(patFruitNum, patBananaAll));
		assertTrue(trans.backmap.contains(patExpFruitNum, patMelonAll));
		assertTrue(trans.backmap.contains(patExpFruitNum, patMangoAll));
		assertTrue(trans.backmap.contains(patTunaAll, patFishAll));
		assertTrue(trans.backmap.contains(patBonitoAll, patFishAll));
		assertTrue(trans.backmap.contains(patSauryAll, patFishAll));
		assertTrue(trans.backmap.contains(patSardineAll, patFishAll));
		assertTrue(trans.backmap.contains(patBeefAll, patMeatAll));
		assertTrue(trans.backmap.contains(patPorkAll, patMeatAll));
		assertTrue(trans.backmap.contains(patChickenAll, patMeatAll));
		assertTrue(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertTrue(trans.backmap.contains(patMechaPenNum, patMechaPenYen));
		assertTrue(trans.backmap.contains(patPencilNum, patPencilYen));
		assertTrue(trans.backmap.contains(patJapanAll, patPrefectureAll));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin1All));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		
		//----------
		// with Index
		//----------
		trans = new ExTransfer(true);
		trans.put(patAppleAll, patFruitNum, TransferAttribute.AGGREGATE);
		trans.put(patOrangeAll, patFruitNum, TransferAttribute.AGGREGATE);
		trans.put(patBananaAll, patFruitNum, TransferAttribute.AGGREGATE);
		trans.put(patMelonAll, patExpFruitNum, TransferAttribute.AGGREGATE);
		trans.put(patMangoAll, patExpFruitNum, TransferAttribute.AGGREGATE);
		trans.put(patFishAll, patTunaAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		trans.put(patFishAll, patBonitoAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		trans.put(patFishAll, patSauryAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		trans.put(patFishAll, patSardineAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		trans.put(patMeatAll, patBeefAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int2));
		trans.put(patMeatAll, patPorkAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int1));
		trans.put(patMeatAll, patChickenAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int1));
		trans.put(patBallPenYen, patBallPenNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f005));
		trans.put(patMechaPenYen, patMechaPenNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		trans.put(patPencilYen, patPencilNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f0125));
		trans.put(patPrefectureAll, patJapanAll, TransferAttribute.AGGREGATE);
		trans.put(patRochin1All, patNaibuHoryuAll, TransferAttribute.HAT_AGGREGATE);
		trans.put(patRochin2All, patNaibuHoryuAll, TransferAttribute.HAT_AGGREGATE);
		
		//--- check from
		assertEquals(trans.numElements, 18);
		assertEquals(trans.map.size(), 13);
		assertEquals(trans.backmap.size(), 14);
		assertFalse(trans.patIndex.isEmpty());
		assertTrue(trans.map.containsKey(patAppleAll));
		assertTrue(trans.map.containsKey(patOrangeAll));
		assertTrue(trans.map.containsKey(patBananaAll));
		assertTrue(trans.map.containsKey(patMelonAll));
		assertTrue(trans.map.containsKey(patMangoAll));
		assertTrue(trans.map.containsKey(patFishAll));
		assertTrue(trans.map.containsKey(patMeatAll));
		assertTrue(trans.map.containsKey(patBallPenYen));
		assertTrue(trans.map.containsKey(patMechaPenYen));
		assertTrue(trans.map.containsKey(patPencilYen));
		assertTrue(trans.map.containsKey(patPrefectureAll));
		assertTrue(trans.map.containsKey(patRochin1All));
		assertTrue(trans.map.containsKey(patRochin2All));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patAppleAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patOrangeAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patBananaAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patMelonAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patMangoAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patFishAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patMeatAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patBallPenYen));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patMechaPenYen));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patPencilYen));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patPrefectureAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patRochin1All));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patRochin2All));
		//--- aggre
		entrymap = trans.map.get(patAppleAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patOrangeAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patBananaAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patMelonAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patExpFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patMangoAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patExpFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patPrefectureAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patJapanAll), TransferAttribute.AGGREGATE);
		//--- hat
		entrymap = trans.map.get(patRochin1All);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patNaibuHoryuAll), TransferAttribute.HAT_AGGREGATE);
		entrymap = trans.map.get(patRochin2All);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patNaibuHoryuAll), TransferAttribute.HAT_AGGREGATE);
		//--- ratio
		entrymap = trans.map.get(patFishAll);
		assertEquals(entrymap.size(), 4);
		assertEquals(entrymap.get(patTunaAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		assertEquals(entrymap.get(patBonitoAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		assertEquals(entrymap.get(patSauryAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		assertEquals(entrymap.get(patSardineAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		//--- multiply
		entrymap = trans.map.get(patMeatAll);
		assertEquals(entrymap.size(), 3);
		assertEquals(entrymap.get(patBeefAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int2));
		assertEquals(entrymap.get(patPorkAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int1));
		assertEquals(entrymap.get(patChickenAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int1));
		entrymap = trans.map.get(patBallPenYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patBallPenNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f005));
		entrymap = trans.map.get(patMechaPenYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patMechaPenNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		entrymap = trans.map.get(patPencilYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patPencilNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f0125));
		//--- backmap
		assertTrue(trans.backmap.contains(patFruitNum, patAppleAll));
		assertTrue(trans.backmap.contains(patFruitNum, patOrangeAll));
		assertTrue(trans.backmap.contains(patFruitNum, patBananaAll));
		assertTrue(trans.backmap.contains(patExpFruitNum, patMelonAll));
		assertTrue(trans.backmap.contains(patExpFruitNum, patMangoAll));
		assertTrue(trans.backmap.contains(patTunaAll, patFishAll));
		assertTrue(trans.backmap.contains(patBonitoAll, patFishAll));
		assertTrue(trans.backmap.contains(patSauryAll, patFishAll));
		assertTrue(trans.backmap.contains(patSardineAll, patFishAll));
		assertTrue(trans.backmap.contains(patBeefAll, patMeatAll));
		assertTrue(trans.backmap.contains(patPorkAll, patMeatAll));
		assertTrue(trans.backmap.contains(patChickenAll, patMeatAll));
		assertTrue(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertTrue(trans.backmap.contains(patMechaPenNum, patMechaPenYen));
		assertTrue(trans.backmap.contains(patPencilNum, patPencilYen));
		assertTrue(trans.backmap.contains(patJapanAll, patPrefectureAll));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin1All));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		
		//--- overwrite
		trans.put(patFishAll, patSauryAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		trans.put(patFishAll, patSardineAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		trans.put(patMeatAll, patBeefAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.put(patMeatAll, patPorkAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.put(patMeatAll, patChickenAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.put(patMechaPenYen, patMechaPenNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f0125));
		trans.put(patPencilYen, patPencilNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		//--- check
		assertEquals(trans.numElements, 18);
		assertEquals(trans.map.size(), 13);
		assertEquals(trans.backmap.size(), 14);
		assertFalse(trans.patIndex.isEmpty());
		assertTrue(trans.map.containsKey(patAppleAll));
		assertTrue(trans.map.containsKey(patOrangeAll));
		assertTrue(trans.map.containsKey(patBananaAll));
		assertTrue(trans.map.containsKey(patMelonAll));
		assertTrue(trans.map.containsKey(patMangoAll));
		assertTrue(trans.map.containsKey(patFishAll));
		assertTrue(trans.map.containsKey(patMeatAll));
		assertTrue(trans.map.containsKey(patBallPenYen));
		assertTrue(trans.map.containsKey(patMechaPenYen));
		assertTrue(trans.map.containsKey(patPencilYen));
		assertTrue(trans.map.containsKey(patPrefectureAll));
		assertTrue(trans.map.containsKey(patRochin1All));
		assertTrue(trans.map.containsKey(patRochin2All));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patAppleAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patOrangeAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patBananaAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patMelonAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patMangoAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patFishAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patMeatAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patBallPenYen));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patMechaPenYen));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patPencilYen));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patPrefectureAll));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patRochin1All));
		assertTrue(ExBasePatternIndexSetTest.contains(trans.patIndex, patRochin2All));
		//--- aggre
		entrymap = trans.map.get(patAppleAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patOrangeAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patBananaAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patMelonAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patExpFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patMangoAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patExpFruitNum), TransferAttribute.AGGREGATE);
		entrymap = trans.map.get(patPrefectureAll);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patJapanAll), TransferAttribute.AGGREGATE);
		//--- hat
		entrymap = trans.map.get(patRochin1All);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patNaibuHoryuAll), TransferAttribute.HAT_AGGREGATE);
		entrymap = trans.map.get(patRochin2All);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patNaibuHoryuAll), TransferAttribute.HAT_AGGREGATE);
		//--- ratio
		entrymap = trans.map.get(patFishAll);
		assertEquals(entrymap.size(), 4);
		assertEquals(entrymap.get(patTunaAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		assertEquals(entrymap.get(patBonitoAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		assertEquals(entrymap.get(patSauryAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		assertEquals(entrymap.get(patSardineAll), trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		//--- multiply
		entrymap = trans.map.get(patMeatAll);
		assertEquals(entrymap.size(), 3);
		assertEquals(entrymap.get(patBeefAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		assertEquals(entrymap.get(patPorkAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		assertEquals(entrymap.get(patChickenAll), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		entrymap = trans.map.get(patBallPenYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patBallPenNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f005));
		entrymap = trans.map.get(patMechaPenYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patMechaPenNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f0125));
		entrymap = trans.map.get(patPencilYen);
		assertEquals(entrymap.size(), 1);
		assertEquals(entrymap.get(patPencilNum), trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		//--- backmap
		assertTrue(trans.backmap.contains(patFruitNum, patAppleAll));
		assertTrue(trans.backmap.contains(patFruitNum, patOrangeAll));
		assertTrue(trans.backmap.contains(patFruitNum, patBananaAll));
		assertTrue(trans.backmap.contains(patExpFruitNum, patMelonAll));
		assertTrue(trans.backmap.contains(patExpFruitNum, patMangoAll));
		assertTrue(trans.backmap.contains(patTunaAll, patFishAll));
		assertTrue(trans.backmap.contains(patBonitoAll, patFishAll));
		assertTrue(trans.backmap.contains(patSauryAll, patFishAll));
		assertTrue(trans.backmap.contains(patSardineAll, patFishAll));
		assertTrue(trans.backmap.contains(patBeefAll, patMeatAll));
		assertTrue(trans.backmap.contains(patPorkAll, patMeatAll));
		assertTrue(trans.backmap.contains(patChickenAll, patMeatAll));
		assertTrue(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertTrue(trans.backmap.contains(patMechaPenNum, patMechaPenYen));
		assertTrue(trans.backmap.contains(patPencilNum, patPencilYen));
		assertTrue(trans.backmap.contains(patJapanAll, patPrefectureAll));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin1All));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		
		// error check
		boolean coughtException;
		//--- aggre pattern
		try {
			trans.put(patAppleAll, patFruitNum, TransferAttribute.AGGREGATE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patAppleAll + "," + patFruitNum + "," + TransferAttribute.AGGREGATE + ") : " + ex);
		}
		assertTrue(coughtException);
		try {
			trans.put(patOrangeAll, patExpFruitNum, TransferAttribute.AGGREGATE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patOrangeAll + "," + patExpFruitNum + "," + TransferAttribute.AGGREGATE + ") : " + ex);
		}
		assertTrue(coughtException);
		try {
			trans.put(patRochin3All, patExpFruitNum, TransferAttribute.HAT_AGGREGATE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patRochin3All + "," + patExpFruitNum + "," + TransferAttribute.HAT_AGGREGATE + ") : " + ex);
		}
		assertTrue(coughtException);
		TransferAttribute attr1 = trans.makeAttribute(ExTransfer.ATTR_RATIO, int1);
		try {
			trans.put(patBananaAll, patBeefAll, attr1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patBananaAll + "," + patBeefAll + "," + attr1 + ") : " + ex);
		}
		assertTrue(coughtException);
		//--- hat pattern
		try {
			trans.put(patRochin1All, patNaibuHoryuAll, TransferAttribute.HAT_AGGREGATE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patRochin1All + "," + patNaibuHoryuAll + "," + TransferAttribute.HAT_AGGREGATE + ") : " + ex);
		}
		assertTrue(coughtException);
		try {
			trans.put(patRochin2All, patHoryuAll, TransferAttribute.HAT_AGGREGATE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patRochin2All + "," + patHoryuAll + "," + TransferAttribute.HAT_AGGREGATE + ") : " + ex);
		}
		assertTrue(coughtException);
		try {
			trans.put(patRochin3All, patNaibuHoryuAll, TransferAttribute.AGGREGATE);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patRochin3All + "," + patNaibuHoryuAll + "," + TransferAttribute.AGGREGATE + ") : " + ex);
		}
		assertTrue(coughtException);
		try {
			trans.put(patRochin1All, patBeefAll, attr1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patRochin1All + "," + patBeefAll + "," + attr1 + ") : " + ex);
		}
		assertTrue(coughtException);
		//--- not same attr
		try {
			trans.put(patBallPenYen, patBeefAll, attr1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patBallPenYen + "," + patBeefAll + "," + attr1 + ") : " + ex);
		}
		assertTrue(coughtException);
		try {
			trans.put(patBallPenYen, patBallPenNum, attr1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patBallPenYen + "," + patBallPenNum + "," + attr1 + ") : " + ex);
		}
		assertTrue(coughtException);
		attr1 = trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004);
		try {
			trans.put(patFishAll, patBallPenNum, attr1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patFishAll + "," + patBallPenNum + "," + attr1 + ") : " + ex);
		}
		assertTrue(coughtException);
		try {
			trans.put(patFishAll, patTunaAll, attr1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			System.out.println("[Check] ExTransferTest:testPutExBasePatternExBasePatternTransferAttribute(" + patFishAll + "," + patTunaAll + "," + attr1 + ") : " + ex);
		}
		assertTrue(coughtException);
		
		//----------
		// validation method test
		//----------
		final Object[][] data = new Object[][]{
				{patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
				{patOrangeAll, patFruitNum, null, null},
				{patBananaAll, patFruitNum, "", null},
				{patMelonAll, patExpFruitNum, "AGGRE", null},
				{patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
				{patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, int5},
				{patFishAll, patBonitoAll, "RATIO", int3},
				{patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, int1},
				{patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int1},
				{patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int2},
				{patMeatAll, patPorkAll, "MULTIPLY", int1},
				{patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int1},
				{patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005},
				{patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004},
				{patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f0125},
				{patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, null},
				{patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
				{patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
		};
		TransferEntry[] entries = makeTransferEntry(data);
		ExTransfer test1 = makeTransfer(false, entries);
		assertEqualTransfer(test1, entries);
		ExTransfer test2 = makeTransfer(true, entries);
		assertEqualTransfer(test2, entries);
		ExTransfer test3 = new ExTransfer(false);
		putAllTransfer(test3, entries);
		assertEqualTransfer(test3, entries);
		ExTransfer test4 = new ExTransfer(true);
		putAllTransfer(test4, entries);
		assertEqualTransfer(test4, entries);
		ExTransfer test5 = new ExTransfer(false);
		setAllTransfer(test5, entries);
		assertEqualTransfer(test5, entries);
		ExTransfer test6 = new ExTransfer(true);
		setAllTransfer(test6, entries);
		assertEqualTransfer(test6, entries);
	}

	/**
	 * {@link exalge2.ExTransfer#set(exalge2.ExBasePattern, exalge2.ExBasePattern, exalge2.ExTransfer.TransferAttribute)} のためのテスト・メソッド。
	 */
	public void testSetExBasePatternExBasePatternTransferAttribute() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		for (TransferEntry entry : data1) {
			trans.set(entry.from(), entry.to(), entry.attr());
		}
		assertEqualTransfer(trans, data1);
		
		//--- overwrite
		trans.set(patFishAll, patSauryAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		trans.set(patFishAll, patSardineAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		trans.set(patMeatAll, patBeefAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.set(patMeatAll, patPorkAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.set(patMeatAll, patChickenAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.set(patMechaPenYen, patMechaPenNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f0125));
		trans.set(patPencilYen, patPencilNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		assertEqualTransfer(trans, data2);
		
		//--- put method illegal case
		trans.set(patAppleAll, patFruitNum, TransferAttribute.AGGREGATE);
		trans.set(patOrangeAll, patExpFruitNum, TransferAttribute.AGGREGATE);
		trans.set(patBananaAll, patBeefAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		trans.set(patBallPenYen, patBeefAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		trans.set(patBallPenYen, patBallPenNum, trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		trans.set(patFishAll, patBallPenNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		trans.set(patFishAll, patTunaAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		trans.set(patPrefectureAll, patJapanAll, TransferAttribute.HAT_AGGREGATE);
		trans.set(patRochin1All, patHoryuAll, TransferAttribute.AGGREGATE);
		trans.set(patRochin2All, patHoryuAll, TransferAttribute.HAT_AGGREGATE);
		trans.set(patRochin3All, patHoryuAll, TransferAttribute.AGGREGATE);
		assertEqualTransfer(trans, data3);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		for (TransferEntry entry : data1) {
			trans.set(entry.from(), entry.to(), entry.attr());
		}
		assertEqualTransfer(trans, data1);
		
		//--- overwrite
		trans.set(patFishAll, patSauryAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int5));
		trans.set(patFishAll, patSardineAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int3));
		trans.set(patMeatAll, patBeefAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.set(patMeatAll, patPorkAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.set(patMeatAll, patChickenAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, int5));
		trans.set(patMechaPenYen, patMechaPenNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f0125));
		trans.set(patPencilYen, patPencilNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		assertEqualTransfer(trans, data2);
		
		//--- put method illegal case
		trans.set(patAppleAll, patFruitNum, TransferAttribute.AGGREGATE);
		trans.set(patOrangeAll, patExpFruitNum, TransferAttribute.AGGREGATE);
		trans.set(patBananaAll, patBeefAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		trans.set(patBallPenYen, patBeefAll, trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		trans.set(patBallPenYen, patBallPenNum, trans.makeAttribute(ExTransfer.ATTR_RATIO, int1));
		trans.set(patFishAll, patBallPenNum, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		trans.set(patFishAll, patTunaAll, trans.makeAttribute(ExTransfer.ATTR_MULTIPLY, f004));
		trans.set(patPrefectureAll, patJapanAll, TransferAttribute.HAT_AGGREGATE);
		trans.set(patRochin1All, patHoryuAll, TransferAttribute.AGGREGATE);
		trans.set(patRochin2All, patHoryuAll, TransferAttribute.HAT_AGGREGATE);
		trans.set(patRochin3All, patHoryuAll, TransferAttribute.AGGREGATE);
		assertEqualTransfer(trans, data3);
	}

	/**
	 * {@link exalge2.ExTransfer#hashCode()} のためのテスト・メソッド。
	 */
	public void testHashCode() {
		
		final int hash1 = calcHashCode(data1);
		final int hash2 = calcHashCode(data10);
		final int hash3 = calcHashCode(data3);
		
		//----------
		// no index
		//----------
		ExTransfer trans1 = makeTransfer(false, data1);
		ExTransfer trans2 = makeTransfer(false, data10);
		ExTransfer trans3 = makeTransfer(false, data3);
		assertEquals(hash1, trans1.hashCode());
		assertEquals(hash2, trans2.hashCode());
		assertEquals(hash3, trans3.hashCode());
		assertTrue(hash1 == hash2);
		assertTrue(hash1 != hash3);
		
		//----------
		// with index
		//----------
		trans1 = makeTransfer(true, data1);
		trans2 = makeTransfer(true, data10);
		trans3 = makeTransfer(true, data3);
		assertEquals(hash1, trans1.hashCode());
		assertEquals(hash2, trans2.hashCode());
		assertEquals(hash3, trans3.hashCode());
		assertTrue(hash1 == hash2);
		assertTrue(hash1 != hash3);
	}

	/**
	 * {@link exalge2.ExTransfer#equals(java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testEqualsObject() {
		//----------
		// no index
		//----------
		ExTransfer trans1 = new ExTransfer(false);
		ExTransfer trans2 = new ExTransfer(false);
		ExTransfer trans3 = new ExTransfer(false);
		putAllTransfer(trans1, data1);
		putAllTransfer(trans2, data10);
		putAllTransfer(trans3, data3);
		assertEqualTransfer(trans1, data1);
		assertEqualTransfer(trans2, data10);
		assertEqualTransfer(trans3, data3);
		assertTrue(trans1.equals(trans2));
		assertFalse(trans1.equals(trans3));
		assertFalse(trans2.equals(trans3));
		assertTrue(trans1.hashCode() == trans2.hashCode());
		assertTrue(trans1.hashCode() != trans3.hashCode());
		
		//----------
		// with index
		//----------
		trans1 = new ExTransfer(true);
		trans2 = new ExTransfer(true);
		trans3 = new ExTransfer(true);
		putAllTransfer(trans1, data1);
		putAllTransfer(trans2, data10);
		putAllTransfer(trans3, data3);
		assertEqualTransfer(trans1, data1);
		assertEqualTransfer(trans2, data10);
		assertEqualTransfer(trans3, data3);
		assertTrue(trans1.equals(trans2));
		assertFalse(trans1.equals(trans3));
		assertFalse(trans2.equals(trans3));
		assertTrue(trans1.hashCode() == trans2.hashCode());
		assertTrue(trans1.hashCode() != trans3.hashCode());
	}

	/**
	 * {@link exalge2.ExTransfer#toString()} のためのテスト・メソッド。
	 */
	public void testToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[\n");
		for (TransferEntry entry : data1) {
			sb.append(entry.from());
			sb.append(" -> ");
			sb.append(entry.to());
			sb.append(" , ");
			sb.append(entry.attrName());
			sb.append(":");
			sb.append(entry.attrValue().toPlainString());
			sb.append("\n");
		}
		sb.append("]");
		final String strData1 = sb.toString();
		final String strEmpty = "[]";
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertEquals(strEmpty, trans.toString());
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertEquals(strData1, trans.toString());
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertEquals(strEmpty, trans.toString());
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertEquals(strData1, trans.toString());
	}

	/**
	 * {@link exalge2.ExTransfer#clear()} のためのテスト・メソッド。
	 */
	public void testClear() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		
		putAllTransfer(trans, data1);
		assertFalse(trans.numElements == 0);
		assertFalse(trans.map.isEmpty());
		assertFalse(trans.backmap.isEmpty());
		assertEqualTransfer(trans, data1);
		
		trans.clear();
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(trans.patIndex.isEmpty());
		
		putAllTransfer(trans, data1);
		assertFalse(trans.numElements == 0);
		assertFalse(trans.map.isEmpty());
		assertFalse(trans.backmap.isEmpty());
		assertFalse(trans.patIndex.isEmpty());
		assertEqualTransfer(trans, data1);
		
		trans.clear();
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(trans.patIndex.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer#isEmpty()} のためのテスト・メソッド。
	 */
	public void testIsEmpty() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(trans.isEmpty());
		
		putAllTransfer(trans, data1);
		assertFalse(trans.numElements == 0);
		assertFalse(trans.map.isEmpty());
		assertFalse(trans.backmap.isEmpty());
		assertEqualTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		
		trans.clear();
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(trans.isEmpty());
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(trans.patIndex.isEmpty());
		assertTrue(trans.isEmpty());
		
		putAllTransfer(trans, data1);
		assertFalse(trans.numElements == 0);
		assertFalse(trans.map.isEmpty());
		assertFalse(trans.backmap.isEmpty());
		assertFalse(trans.patIndex.isEmpty());
		assertEqualTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		
		trans.clear();
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(trans.patIndex.isEmpty());
		assertTrue(trans.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer#size()} のためのテスト・メソッド。
	 */
	public void testSize() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertEquals(trans.size(), 0);
		
		putAllTransfer(trans, data1);
		assertFalse(trans.numElements == 0);
		assertFalse(trans.map.isEmpty());
		assertFalse(trans.backmap.isEmpty());
		assertEqualTransfer(trans, data1);
		assertEquals(trans.size(), data1.length);
		
		trans.clear();
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertEquals(trans.size(), 0);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(trans.patIndex.isEmpty());
		assertEquals(trans.size(), 0);
		
		putAllTransfer(trans, data1);
		assertFalse(trans.numElements == 0);
		assertFalse(trans.map.isEmpty());
		assertFalse(trans.backmap.isEmpty());
		assertFalse(trans.patIndex.isEmpty());
		assertEqualTransfer(trans, data1);
		assertEquals(trans.size(), data1.length);
		
		trans.clear();
		assertTrue(trans.numElements == 0);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(trans.patIndex.isEmpty());
		assertEquals(trans.size(), 0);
	}

	/**
	 * {@link exalge2.ExTransfer#contains(exalge2.ExBasePattern, exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testContainsExBasePatternExBasePattern() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertFalse(trans.contains(patAppleAll, patFruitNum));
		assertFalse(trans.contains(patFishAll, patBonitoAll));
		assertFalse(trans.contains(patBallPenYen, patBallPenNum));
		assertFalse(trans.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.contains(patStationeryAll, patBallPenNum));
		assertFalse(trans.contains(patAppleAll, null));
		assertFalse(trans.contains(null, patFruitNum));
		assertFalse(trans.contains((ExBasePattern)null, (ExBasePattern)null));
		//---
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.backmap.contains(patFruitNum, patAppleAll));
		assertFalse(trans.backmap.contains(patBonitoAll, patFishAll));
		assertFalse(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertFalse(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		assertFalse(trans.backmap.contains(patBallPenNum, patStationeryAll));
		assertFalse(trans.backmap.contains(patAppleAll, patFruitNum));
		assertFalse(trans.backmap.contains(patFishAll, patBonitoAll));
		assertFalse(trans.backmap.contains(patBallPenYen, patBallPenNum));
		assertFalse(trans.backmap.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.backmap.contains(null, patAppleAll));
		assertFalse(trans.backmap.contains(patFruitNum, null));
		assertFalse(trans.backmap.contains(null, null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.contains(patAppleAll, patFruitNum));
		assertTrue(trans.contains(patFishAll, patBonitoAll));
		assertTrue(trans.contains(patBallPenYen, patBallPenNum));
		assertTrue(trans.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.contains(patStationeryAll, patBallPenNum));
		assertFalse(trans.contains(patAppleAll, null));
		assertFalse(trans.contains(null, patFruitNum));
		assertFalse(trans.contains((ExBasePattern)null, (ExBasePattern)null));
		//---
		assertFalse(trans.backmap.isEmpty());
		assertTrue(trans.backmap.contains(patFruitNum, patAppleAll));
		assertTrue(trans.backmap.contains(patBonitoAll, patFishAll));
		assertTrue(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		assertFalse(trans.backmap.contains(patBallPenNum, patStationeryAll));
		assertFalse(trans.backmap.contains(patAppleAll, patFruitNum));
		assertFalse(trans.backmap.contains(patFishAll, patBonitoAll));
		assertFalse(trans.backmap.contains(patBallPenYen, patBallPenNum));
		assertFalse(trans.backmap.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.backmap.contains(null, patAppleAll));
		assertFalse(trans.backmap.contains(patFruitNum, null));
		assertFalse(trans.backmap.contains(null, null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertFalse(trans.contains(patAppleAll, patFruitNum));
		assertFalse(trans.contains(patFishAll, patBonitoAll));
		assertFalse(trans.contains(patBallPenYen, patBallPenNum));
		assertFalse(trans.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.contains(patStationeryAll, patBallPenNum));
		assertFalse(trans.contains(patAppleAll, null));
		assertFalse(trans.contains(null, patFruitNum));
		assertFalse(trans.contains((ExBasePattern)null, (ExBasePattern)null));
		//---
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.backmap.contains(patFruitNum, patAppleAll));
		assertFalse(trans.backmap.contains(patBonitoAll, patFishAll));
		assertFalse(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertFalse(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		assertFalse(trans.backmap.contains(patBallPenNum, patStationeryAll));
		assertFalse(trans.backmap.contains(patAppleAll, patFruitNum));
		assertFalse(trans.backmap.contains(patFishAll, patBonitoAll));
		assertFalse(trans.backmap.contains(patBallPenYen, patBallPenNum));
		assertFalse(trans.backmap.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.backmap.contains(null, patAppleAll));
		assertFalse(trans.backmap.contains(patFruitNum, null));
		assertFalse(trans.backmap.contains(null, null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.contains(patAppleAll, patFruitNum));
		assertTrue(trans.contains(patFishAll, patBonitoAll));
		assertTrue(trans.contains(patBallPenYen, patBallPenNum));
		assertTrue(trans.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.contains(patStationeryAll, patBallPenNum));
		assertFalse(trans.contains(patAppleAll, null));
		assertFalse(trans.contains(null, patFruitNum));
		assertFalse(trans.contains((ExBasePattern)null, (ExBasePattern)null));
		//---
		assertFalse(trans.backmap.isEmpty());
		assertTrue(trans.backmap.contains(patFruitNum, patAppleAll));
		assertTrue(trans.backmap.contains(patBonitoAll, patFishAll));
		assertTrue(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		assertFalse(trans.backmap.contains(patBallPenNum, patStationeryAll));
		assertFalse(trans.backmap.contains(patAppleAll, patFruitNum));
		assertFalse(trans.backmap.contains(patFishAll, patBonitoAll));
		assertFalse(trans.backmap.contains(patBallPenYen, patBallPenNum));
		assertFalse(trans.backmap.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.backmap.contains(null, patAppleAll));
		assertFalse(trans.backmap.contains(patFruitNum, null));
		assertFalse(trans.backmap.contains(null, null));
	}

	/**
	 * {@link exalge2.ExTransfer#contains(exalge2.ExBase, exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testContainsExBaseExBase() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertFalse(trans.contains(baseAppleAll, baseFruitNum));
		assertFalse(trans.contains(baseFishAll, baseBonitoAll));
		assertFalse(trans.contains(baseBallPenYen, baseBallPenNum));
		assertFalse(trans.contains(baseRochin1All, baseNaibuHoryuAll));
		assertFalse(trans.contains(baseStationeryAll, baseBallPenNum));
		assertFalse(trans.contains(baseAppleAll, null));
		assertFalse(trans.contains(null, baseFruitNum));
		assertFalse(trans.contains((ExBase)null, (ExBase)null));
		//---
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.backmap.contains(patFruitNum, patAppleAll));
		assertFalse(trans.backmap.contains(patBonitoAll, patFishAll));
		assertFalse(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertFalse(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		assertFalse(trans.backmap.contains(patBallPenNum, patStationeryAll));
		assertFalse(trans.backmap.contains(patAppleAll, patFruitNum));
		assertFalse(trans.backmap.contains(patFishAll, patBonitoAll));
		assertFalse(trans.backmap.contains(patBallPenYen, patBallPenNum));
		assertFalse(trans.backmap.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.backmap.contains(null, patAppleAll));
		assertFalse(trans.backmap.contains(patFruitNum, null));
		assertFalse(trans.backmap.contains(null, null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.contains(baseAppleAll, baseFruitNum));
		assertTrue(trans.contains(baseFishAll, baseBonitoAll));
		assertTrue(trans.contains(baseBallPenYen, baseBallPenNum));
		assertTrue(trans.contains(baseRochin1All, baseNaibuHoryuAll));
		assertFalse(trans.contains(baseStationeryAll, baseBallPenNum));
		assertFalse(trans.contains(baseAppleAll, null));
		assertFalse(trans.contains(null, baseFruitNum));
		assertFalse(trans.contains((ExBase)null, (ExBase)null));
		//---
		assertFalse(trans.backmap.isEmpty());
		assertTrue(trans.backmap.contains(patFruitNum, patAppleAll));
		assertTrue(trans.backmap.contains(patBonitoAll, patFishAll));
		assertTrue(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		assertFalse(trans.backmap.contains(patBallPenNum, patStationeryAll));
		assertFalse(trans.backmap.contains(patAppleAll, patFruitNum));
		assertFalse(trans.backmap.contains(patFishAll, patBonitoAll));
		assertFalse(trans.backmap.contains(patBallPenYen, patBallPenNum));
		assertFalse(trans.backmap.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.backmap.contains(null, patAppleAll));
		assertFalse(trans.backmap.contains(patFruitNum, null));
		assertFalse(trans.backmap.contains(null, null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertFalse(trans.contains(baseAppleAll, baseFruitNum));
		assertFalse(trans.contains(baseFishAll, baseBonitoAll));
		assertFalse(trans.contains(baseBallPenYen, baseBallPenNum));
		assertFalse(trans.contains(baseRochin1All, baseNaibuHoryuAll));
		assertFalse(trans.contains(baseStationeryAll, baseBallPenNum));
		assertFalse(trans.contains(baseAppleAll, null));
		assertFalse(trans.contains(null, baseFruitNum));
		assertFalse(trans.contains((ExBase)null, (ExBase)null));
		//---
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.backmap.contains(patFruitNum, patAppleAll));
		assertFalse(trans.backmap.contains(patBonitoAll, patFishAll));
		assertFalse(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertFalse(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		assertFalse(trans.backmap.contains(patBallPenNum, patStationeryAll));
		assertFalse(trans.backmap.contains(patAppleAll, patFruitNum));
		assertFalse(trans.backmap.contains(patFishAll, patBonitoAll));
		assertFalse(trans.backmap.contains(patBallPenYen, patBallPenNum));
		assertFalse(trans.backmap.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.backmap.contains(null, patAppleAll));
		assertFalse(trans.backmap.contains(patFruitNum, null));
		assertFalse(trans.backmap.contains(null, null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.contains(baseAppleAll, baseFruitNum));
		assertTrue(trans.contains(baseFishAll, baseBonitoAll));
		assertTrue(trans.contains(baseBallPenYen, baseBallPenNum));
		assertTrue(trans.contains(baseRochin1All, baseNaibuHoryuAll));
		assertFalse(trans.contains(baseStationeryAll, baseBallPenNum));
		assertFalse(trans.contains(baseAppleAll, null));
		assertFalse(trans.contains(null, baseFruitNum));
		assertFalse(trans.contains((ExBase)null, (ExBase)null));
		//---
		assertFalse(trans.backmap.isEmpty());
		assertTrue(trans.backmap.contains(patFruitNum, patAppleAll));
		assertTrue(trans.backmap.contains(patBonitoAll, patFishAll));
		assertTrue(trans.backmap.contains(patBallPenNum, patBallPenYen));
		assertTrue(trans.backmap.contains(patNaibuHoryuAll, patRochin2All));
		assertFalse(trans.backmap.contains(patBallPenNum, patStationeryAll));
		assertFalse(trans.backmap.contains(patAppleAll, patFruitNum));
		assertFalse(trans.backmap.contains(patFishAll, patBonitoAll));
		assertFalse(trans.backmap.contains(patBallPenYen, patBallPenNum));
		assertFalse(trans.backmap.contains(patRochin2All, patNaibuHoryuAll));
		assertFalse(trans.backmap.contains(null, patAppleAll));
		assertFalse(trans.backmap.contains(patFruitNum, null));
		assertFalse(trans.backmap.contains(null, null));
	}

	/**
	 * {@link exalge2.ExTransfer#containsFrom(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testContainsFromExBasePattern() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertFalse(trans.containsFrom(patAppleAll));
		assertFalse(trans.containsFrom(patFishAll));
		assertFalse(trans.containsFrom(patBallPenYen));
		assertFalse(trans.containsFrom(patRochin2All));
		assertFalse(trans.containsFrom(patStationeryAll));
		assertFalse(trans.containsFrom((ExBasePattern)null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.containsFrom(patAppleAll));
		assertTrue(trans.containsFrom(patFishAll));
		assertTrue(trans.containsFrom(patBallPenYen));
		assertTrue(trans.containsFrom(patRochin2All));
		assertFalse(trans.containsFrom(patStationeryAll));
		assertFalse(trans.containsFrom((ExBasePattern)null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertFalse(trans.containsFrom(patAppleAll));
		assertFalse(trans.containsFrom(patFishAll));
		assertFalse(trans.containsFrom(patBallPenYen));
		assertFalse(trans.containsFrom(patRochin2All));
		assertFalse(trans.containsFrom(patStationeryAll));
		assertFalse(trans.containsFrom((ExBasePattern)null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.containsFrom(patAppleAll));
		assertTrue(trans.containsFrom(patFishAll));
		assertTrue(trans.containsFrom(patBallPenYen));
		assertTrue(trans.containsFrom(patRochin2All));
		assertFalse(trans.containsFrom(patStationeryAll));
		assertFalse(trans.containsFrom((ExBasePattern)null));
	}

	/**
	 * {@link exalge2.ExTransfer#containsFrom(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testContainsFromExBase() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertFalse(trans.containsFrom(baseAppleAll));
		assertFalse(trans.containsFrom(baseFishAll));
		assertFalse(trans.containsFrom(baseBallPenYen));
		assertFalse(trans.containsFrom(baseRochin2All));
		assertFalse(trans.containsFrom(baseStationeryAll));
		assertFalse(trans.containsFrom((ExBase)null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.containsFrom(baseAppleAll));
		assertTrue(trans.containsFrom(baseFishAll));
		assertTrue(trans.containsFrom(baseBallPenYen));
		assertTrue(trans.containsFrom(baseRochin2All));
		assertFalse(trans.containsFrom(baseStationeryAll));
		assertFalse(trans.containsFrom((ExBase)null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertFalse(trans.containsFrom(baseAppleAll));
		assertFalse(trans.containsFrom(baseFishAll));
		assertFalse(trans.containsFrom(baseBallPenYen));
		assertFalse(trans.containsFrom(baseRochin2All));
		assertFalse(trans.containsFrom(baseStationeryAll));
		assertFalse(trans.containsFrom((ExBase)null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.containsFrom(baseAppleAll));
		assertTrue(trans.containsFrom(baseFishAll));
		assertTrue(trans.containsFrom(baseBallPenYen));
		assertTrue(trans.containsFrom(baseRochin2All));
		assertFalse(trans.containsFrom(baseStationeryAll));
		assertFalse(trans.containsFrom((ExBase)null));
	}

	/**
	 * {@link exalge2.ExTransfer#containsTo(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testContainsToExBasePattern() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertFalse(trans.containsTo(patFruitNum));
		assertFalse(trans.containsTo(patBonitoAll));
		assertFalse(trans.containsTo(patBallPenNum));
		assertFalse(trans.containsTo(patNaibuHoryuAll));
		assertFalse(trans.containsTo(patStationeryAll));
		assertFalse(trans.containsTo((ExBasePattern)null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.containsTo(patFruitNum));
		assertTrue(trans.containsTo(patBonitoAll));
		assertTrue(trans.containsTo(patBallPenNum));
		assertTrue(trans.containsTo(patNaibuHoryuAll));
		assertFalse(trans.containsTo(patStationeryAll));
		assertFalse(trans.containsTo((ExBasePattern)null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertFalse(trans.containsTo(patFruitNum));
		assertFalse(trans.containsTo(patBonitoAll));
		assertFalse(trans.containsTo(patBallPenNum));
		assertFalse(trans.containsTo(patNaibuHoryuAll));
		assertFalse(trans.containsTo(patStationeryAll));
		assertFalse(trans.containsTo((ExBasePattern)null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.containsTo(patFruitNum));
		assertTrue(trans.containsTo(patBonitoAll));
		assertTrue(trans.containsTo(patBallPenNum));
		assertTrue(trans.containsTo(patNaibuHoryuAll));
		assertFalse(trans.containsTo(patStationeryAll));
		assertFalse(trans.containsTo((ExBasePattern)null));
	}

	/**
	 * {@link exalge2.ExTransfer#containsTo(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testContainsToExBase() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertFalse(trans.containsTo(baseFruitNum));
		assertFalse(trans.containsTo(baseBonitoAll));
		assertFalse(trans.containsTo(baseBallPenNum));
		assertFalse(trans.containsTo(baseNaibuHoryuAll));
		assertFalse(trans.containsTo(baseStationeryAll));
		assertFalse(trans.containsTo((ExBase)null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.containsTo(baseFruitNum));
		assertTrue(trans.containsTo(baseBonitoAll));
		assertTrue(trans.containsTo(baseBallPenNum));
		assertTrue(trans.containsTo(baseNaibuHoryuAll));
		assertFalse(trans.containsTo(baseStationeryAll));
		assertFalse(trans.containsTo((ExBase)null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertFalse(trans.containsTo(baseFruitNum));
		assertFalse(trans.containsTo(baseBonitoAll));
		assertFalse(trans.containsTo(baseBallPenNum));
		assertFalse(trans.containsTo(baseNaibuHoryuAll));
		assertFalse(trans.containsTo(baseStationeryAll));
		assertFalse(trans.containsTo((ExBase)null));
		
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertTrue(trans.containsTo(baseFruitNum));
		assertTrue(trans.containsTo(baseBonitoAll));
		assertTrue(trans.containsTo(baseBallPenNum));
		assertTrue(trans.containsTo(baseNaibuHoryuAll));
		assertFalse(trans.containsTo(baseStationeryAll));
		assertFalse(trans.containsTo((ExBase)null));
	}

	/**
	 * {@link exalge2.ExTransfer#fromPatterns()} のためのテスト・メソッド。
	 */
	public void testFromPatterns() {
		ExBasePatternSet testSet1 = new ExBasePatternSet();
		for (TransferEntry entry : data1) {
			testSet1.add(entry.from());
		}
		ExBasePatternSet testSet3 = new ExBasePatternSet();
		for (TransferEntry entry : data3) {
			testSet3.add(entry.from());
		}
		ExBasePatternSet ret;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		ret = trans.fromPatterns();
		assertTrue(ret.isEmpty());
		//---
		putAllTransfer(trans, data1);
		ret = trans.fromPatterns();
		assertEquals(testSet1, ret);
		//---
		trans.clear();
		putAllTransfer(trans, data3);
		ret = trans.fromPatterns();
		assertEquals(testSet3, ret);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		ret = trans.fromPatterns();
		assertTrue(ret.isEmpty());
		//---
		putAllTransfer(trans, data1);
		ret = trans.fromPatterns();
		assertEquals(testSet1, ret);
		//---
		trans.clear();
		putAllTransfer(trans, data3);
		ret = trans.fromPatterns();
		assertEquals(testSet3, ret);
	}

	/**
	 * {@link exalge2.ExTransfer#toPatterns()} のためのテスト・メソッド。
	 */
	public void testToPatterns() {
		ExBasePatternSet testSet1 = new ExBasePatternSet();
		for (TransferEntry entry : data1) {
			testSet1.add(entry.to());
		}
		ExBasePatternSet testSet3 = new ExBasePatternSet();
		for (TransferEntry entry : data3) {
			testSet3.add(entry.to());
		}
		ExBasePatternSet ret;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		ret = trans.toPatterns();
		assertTrue(ret.isEmpty());
		//---
		putAllTransfer(trans, data1);
		ret = trans.toPatterns();
		assertEquals(testSet1, ret);
		//---
		trans.clear();
		putAllTransfer(trans, data3);
		ret = trans.toPatterns();
		assertEquals(testSet3, ret);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		ret = trans.toPatterns();
		assertTrue(ret.isEmpty());
		//---
		putAllTransfer(trans, data1);
		ret = trans.toPatterns();
		assertEquals(testSet1, ret);
		//---
		trans.clear();
		putAllTransfer(trans, data3);
		ret = trans.toPatterns();
		assertEquals(testSet3, ret);
	}

	/**
	 * {@link exalge2.ExTransfer#toPatterns(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testToPatternsExBasePattern() {
		HashMap<ExBasePattern, ExBasePatternSet> map1 = new HashMap<ExBasePattern,ExBasePatternSet>();
		for (TransferEntry entry : data1) {
			ExBasePattern pf = entry.from();
			ExBasePattern pt = entry.to();
			ExBasePatternSet set = map1.get(pf);
			if (set == null) {
				set = new ExBasePatternSet();
				map1.put(pf, set);
			}
			set.add(pt);
		}
		ExBasePatternSet ans;
		ExBasePatternSet ret;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (ExBasePattern pf : map1.keySet()) {
			ret = trans.toPatterns(pf);
			assertTrue(ret.isEmpty());
		}
		assertTrue(trans.toPatterns(null).isEmpty());
		//---
		putAllTransfer(trans, data1);
		assertTrue(trans.map.keySet().equals(map1.keySet()));
		for (ExBasePattern pf : map1.keySet()) {
			ans = map1.get(pf);
			ret = trans.toPatterns(pf);
			assertEquals(ans, ret);
		}
		assertTrue(trans.toPatterns(null).isEmpty());
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (ExBasePattern pf : map1.keySet()) {
			ret = trans.toPatterns(pf);
			assertTrue(ret.isEmpty());
		}
		assertTrue(trans.toPatterns(null).isEmpty());
		//---
		putAllTransfer(trans, data1);
		assertTrue(trans.map.keySet().equals(map1.keySet()));
		for (ExBasePattern pf : map1.keySet()) {
			ans = map1.get(pf);
			ret = trans.toPatterns(pf);
			assertEquals(ans, ret);
		}
		assertTrue(trans.toPatterns(null).isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer#getTransferAttribute(exalge2.ExBasePattern, exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testGetTransferAttribute() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (TransferEntry entry : data1) {
			assertNull(trans.getTransferAttribute(entry.from(), entry.to()));
		}
		assertNull(trans.getTransferAttribute(patStationeryAll, patBallPenYen));
		assertNull(trans.getTransferAttribute(patAppleAll, null));
		assertNull(trans.getTransferAttribute(null, patFruitNum));
		assertNull(trans.getTransferAttribute(null, null));
		//---
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		for (TransferEntry entry : data1) {
			TransferAttribute attr = trans.getTransferAttribute(entry.from(), entry.to());
			assertEquals(entry.attr(), attr);
		}
		assertNull(trans.getTransferAttribute(patStationeryAll, patBallPenYen));
		assertNull(trans.getTransferAttribute(patAppleAll, null));
		assertNull(trans.getTransferAttribute(null, patFruitNum));
		assertNull(trans.getTransferAttribute(null, null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (TransferEntry entry : data1) {
			assertNull(trans.getTransferAttribute(entry.from(), entry.to()));
		}
		assertNull(trans.getTransferAttribute(patStationeryAll, patBallPenYen));
		assertNull(trans.getTransferAttribute(patAppleAll, null));
		assertNull(trans.getTransferAttribute(null, patFruitNum));
		assertNull(trans.getTransferAttribute(null, null));
		//---
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		for (TransferEntry entry : data1) {
			TransferAttribute attr = trans.getTransferAttribute(entry.from(), entry.to());
			assertEquals(entry.attr(), attr);
		}
		assertNull(trans.getTransferAttribute(patStationeryAll, patBallPenYen));
		assertNull(trans.getTransferAttribute(patAppleAll, null));
		assertNull(trans.getTransferAttribute(null, patFruitNum));
		assertNull(trans.getTransferAttribute(null, null));
	}

	/**
	 * {@link exalge2.ExTransfer#getAttribute(exalge2.ExBasePattern, exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testGetAttributeExBasePatternExBasePattern() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (TransferEntry entry : data1) {
			assertNull(trans.getAttribute(entry.from(), entry.to()));
		}
		for (TransferEntry entry : data4) {
			assertNull(trans.getAttribute(entry.from(), entry.to()));
		}
		assertNull(trans.getAttribute(patAppleAll, null));
		assertNull(trans.getAttribute(null, patFruitNum));
		assertNull(trans.getAttribute((ExBasePattern)null, (ExBasePattern)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertEquals(data1.length, trans.size());
		for (TransferEntry entry : data1) {
			String attrName = trans.getAttribute(entry.from(), entry.to());
			assertEquals(entry.attrName(), attrName);
		}
		for (TransferEntry entry : data4) {
			assertNull(trans.getAttribute(entry.from(), entry.to()));
		}
		assertNull(trans.getAttribute(patAppleAll, null));
		assertNull(trans.getAttribute(null, patFruitNum));
		assertNull(trans.getAttribute((ExBasePattern)null, (ExBasePattern)null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (TransferEntry entry : data1) {
			assertNull(trans.getAttribute(entry.from(), entry.to()));
		}
		for (TransferEntry entry : data4) {
			assertNull(trans.getAttribute(entry.from(), entry.to()));
		}
		assertNull(trans.getAttribute(patAppleAll, null));
		assertNull(trans.getAttribute(null, patFruitNum));
		assertNull(trans.getAttribute((ExBasePattern)null, (ExBasePattern)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertEquals(data1.length, trans.size());
		for (TransferEntry entry : data1) {
			String attrName = trans.getAttribute(entry.from(), entry.to());
			assertEquals(entry.attrName(), attrName);
		}
		for (TransferEntry entry : data4) {
			assertNull(trans.getAttribute(entry.from(), entry.to()));
		}
		assertNull(trans.getAttribute(patAppleAll, null));
		assertNull(trans.getAttribute(null, patFruitNum));
		assertNull(trans.getAttribute((ExBasePattern)null, (ExBasePattern)null));
	}

	/**
	 * {@link exalge2.ExTransfer#getAttribute(exalge2.ExBase, exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testGetAttributeExBaseExBase() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (TransferEntry entry : data1) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getAttribute(bf, bt));
		}
		for (TransferEntry entry : data4) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getAttribute(bf, bt));
		}
		assertNull(trans.getAttribute(baseAppleAll, null));
		assertNull(trans.getAttribute(null, baseFruitNum));
		assertNull(trans.getAttribute((ExBase)null, (ExBase)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertEquals(data1.length, trans.size());
		for (TransferEntry entry : data1) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			String attrName = trans.getAttribute(bf, bt);
			assertEquals(entry.attrName(), attrName);
		}
		for (TransferEntry entry : data4) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getAttribute(bf, bt));
		}
		assertNull(trans.getAttribute(baseAppleAll, null));
		assertNull(trans.getAttribute(null, baseFruitNum));
		assertNull(trans.getAttribute((ExBase)null, (ExBase)null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (TransferEntry entry : data1) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getAttribute(bf, bt));
		}
		for (TransferEntry entry : data4) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getAttribute(bf, bt));
		}
		assertNull(trans.getAttribute(baseAppleAll, null));
		assertNull(trans.getAttribute(null, baseFruitNum));
		assertNull(trans.getAttribute((ExBase)null, (ExBase)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertEquals(data1.length, trans.size());
		for (TransferEntry entry : data1) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			String attrName = trans.getAttribute(bf, bt);
			assertEquals(entry.attrName(), attrName);
		}
		for (TransferEntry entry : data4) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getAttribute(bf, bt));
		}
		assertNull(trans.getAttribute(baseAppleAll, null));
		assertNull(trans.getAttribute(null, baseFruitNum));
		assertNull(trans.getAttribute((ExBase)null, (ExBase)null));
	}

	/**
	 * {@link exalge2.ExTransfer#getValue(exalge2.ExBasePattern, exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testGetValueExBasePatternExBasePattern() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (TransferEntry entry : data1) {
			assertNull(trans.getValue(entry.from(), entry.to()));
		}
		for (TransferEntry entry : data4) {
			assertNull(trans.getValue(entry.from(), entry.to()));
		}
		assertNull(trans.getValue(patAppleAll, null));
		assertNull(trans.getValue(null, patFruitNum));
		assertNull(trans.getValue((ExBasePattern)null, (ExBasePattern)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertEquals(data1.length, trans.size());
		for (TransferEntry entry : data1) {
			BigDecimal attrValue = trans.getValue(entry.from(), entry.to());
			assertTrue(entry.attrValue().compareTo(attrValue) == 0);
		}
		for (TransferEntry entry : data4) {
			assertNull(trans.getValue(entry.from(), entry.to()));
		}
		assertNull(trans.getValue(patAppleAll, null));
		assertNull(trans.getValue(null, patFruitNum));
		assertNull(trans.getValue((ExBasePattern)null, (ExBasePattern)null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (TransferEntry entry : data1) {
			assertNull(trans.getValue(entry.from(), entry.to()));
		}
		for (TransferEntry entry : data4) {
			assertNull(trans.getValue(entry.from(), entry.to()));
		}
		assertNull(trans.getValue(patAppleAll, null));
		assertNull(trans.getValue(null, patFruitNum));
		assertNull(trans.getValue((ExBasePattern)null, (ExBasePattern)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertEquals(data1.length, trans.size());
		for (TransferEntry entry : data1) {
			BigDecimal attrValue = trans.getValue(entry.from(), entry.to());
			assertTrue(entry.attrValue().compareTo(attrValue) == 0);
		}
		for (TransferEntry entry : data4) {
			assertNull(trans.getValue(entry.from(), entry.to()));
		}
		assertNull(trans.getValue(patAppleAll, null));
		assertNull(trans.getValue(null, patFruitNum));
		assertNull(trans.getValue((ExBasePattern)null, (ExBasePattern)null));
	}

	/**
	 * {@link exalge2.ExTransfer#getValue(exalge2.ExBase, exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testGetValueExBaseExBase() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (TransferEntry entry : data1) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getValue(bf, bt));
		}
		for (TransferEntry entry : data4) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getValue(bf, bt));
		}
		assertNull(trans.getValue(baseAppleAll, null));
		assertNull(trans.getValue(null, baseFruitNum));
		assertNull(trans.getValue((ExBase)null, (ExBase)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertEquals(data1.length, trans.size());
		for (TransferEntry entry : data1) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			BigDecimal attrValue = trans.getValue(bf, bt);
			assertTrue(entry.attrValue().compareTo(attrValue) == 0);
		}
		for (TransferEntry entry : data4) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getValue(bf, bt));
		}
		assertNull(trans.getValue(baseAppleAll, null));
		assertNull(trans.getValue(null, baseFruitNum));
		assertNull(trans.getValue((ExBase)null, (ExBase)null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (TransferEntry entry : data1) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getValue(bf, bt));
		}
		for (TransferEntry entry : data4) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getValue(bf, bt));
		}
		assertNull(trans.getValue(baseAppleAll, null));
		assertNull(trans.getValue(null, baseFruitNum));
		assertNull(trans.getValue((ExBase)null, (ExBase)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertEquals(data1.length, trans.size());
		for (TransferEntry entry : data1) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			BigDecimal attrValue = trans.getValue(bf, bt);
			assertTrue(entry.attrValue().compareTo(attrValue) == 0);
		}
		for (TransferEntry entry : data4) {
			ExBase bf = new ExBase(entry.from().getNameKey(), ExBase.HAT, entry.from().getUnitKey(), entry.from().getTimeKey(), entry.from().getSubjectKey());
			ExBase bt = new ExBase(entry.to().getNameKey(), ExBase.HAT, entry.to().getUnitKey(), entry.to().getTimeKey(), entry.to().getSubjectKey());
			assertNull(trans.getValue(bf, bt));
		}
		assertNull(trans.getValue(baseAppleAll, null));
		assertNull(trans.getValue(null, baseFruitNum));
		assertNull(trans.getValue((ExBase)null, (ExBase)null));
	}

	/**
	 * {@link exalge2.ExTransfer#getTotalValue(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testGetTotalValueExBasePattern() {
		BigDecimal totalAppleAll = BigDecimal.ONE;
		BigDecimal totalRochinAll = BigDecimal.ONE;
		BigDecimal totalFishAll  = int5.add(int3).add(int1).add(int1);
		BigDecimal totalMeatAll  = int2.add(int1).add(int1);
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertNull(trans.getTotalValue(patAppleAll));
		assertNull(trans.getTotalValue(patFishAll));
		assertNull(trans.getTotalValue(patMeatAll));
		assertNull(trans.getTotalValue(patRochin1All));
		assertNull(trans.getTotalValue(patStationeryAll));
		assertNull(trans.getTotalValue((ExBasePattern)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertTrue(totalAppleAll.compareTo(trans.getTotalValue(patAppleAll)) == 0);
		assertTrue(totalFishAll.compareTo(trans.getTotalValue(patFishAll)) == 0);
		assertTrue(totalMeatAll.compareTo(trans.getTotalValue(patMeatAll)) == 0);
		assertTrue(totalRochinAll.compareTo(trans.getTotalValue(patRochin1All)) == 0);
		assertNull(trans.getTotalValue(patStationeryAll));
		assertNull(trans.getTotalValue((ExBasePattern)null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertNull(trans.getTotalValue(patAppleAll));
		assertNull(trans.getTotalValue(patFishAll));
		assertNull(trans.getTotalValue(patMeatAll));
		assertNull(trans.getTotalValue(patRochin1All));
		assertNull(trans.getTotalValue(patStationeryAll));
		assertNull(trans.getTotalValue((ExBasePattern)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertTrue(totalAppleAll.compareTo(trans.getTotalValue(patAppleAll)) == 0);
		assertTrue(totalFishAll.compareTo(trans.getTotalValue(patFishAll)) == 0);
		assertTrue(totalMeatAll.compareTo(trans.getTotalValue(patMeatAll)) == 0);
		assertTrue(totalRochinAll.compareTo(trans.getTotalValue(patRochin1All)) == 0);
		assertNull(trans.getTotalValue(patStationeryAll));
		assertNull(trans.getTotalValue((ExBasePattern)null));
	}

	/**
	 * {@link exalge2.ExTransfer#getTotalValue(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testGetTotalValueExBase() {
		BigDecimal totalAppleAll = BigDecimal.ONE;
		BigDecimal totalRochinAll = BigDecimal.ONE;
		BigDecimal totalFishAll  = int5.add(int3).add(int1).add(int1);
		BigDecimal totalMeatAll  = int2.add(int1).add(int1);
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertNull(trans.getTotalValue(baseAppleAll));
		assertNull(trans.getTotalValue(baseFishAll));
		assertNull(trans.getTotalValue(baseMeatAll));
		assertNull(trans.getTotalValue(baseRochin1All));
		assertNull(trans.getTotalValue(baseStationeryAll));
		assertNull(trans.getTotalValue((ExBase)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertTrue(totalAppleAll.compareTo(trans.getTotalValue(baseAppleAll)) == 0);
		assertTrue(totalFishAll.compareTo(trans.getTotalValue(baseFishAll)) == 0);
		assertTrue(totalMeatAll.compareTo(trans.getTotalValue(baseMeatAll)) == 0);
		assertTrue(totalRochinAll.compareTo(trans.getTotalValue(baseRochin1All)) == 0);
		assertNull(trans.getTotalValue(baseStationeryAll));
		assertNull(trans.getTotalValue((ExBase)null));
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertNull(trans.getTotalValue(baseAppleAll));
		assertNull(trans.getTotalValue(baseFishAll));
		assertNull(trans.getTotalValue(baseMeatAll));
		assertNull(trans.getTotalValue(baseRochin1All));
		assertNull(trans.getTotalValue(baseStationeryAll));
		assertNull(trans.getTotalValue((ExBase)null));
		//---
		putAllTransfer(trans, data1);
		assertFalse(trans.isEmpty());
		assertTrue(totalAppleAll.compareTo(trans.getTotalValue(baseAppleAll)) == 0);
		assertTrue(totalFishAll.compareTo(trans.getTotalValue(baseFishAll)) == 0);
		assertTrue(totalMeatAll.compareTo(trans.getTotalValue(baseMeatAll)) == 0);
		assertTrue(totalRochinAll.compareTo(trans.getTotalValue(baseRochin1All)) == 0);
		assertNull(trans.getTotalValue(baseStationeryAll));
		assertNull(trans.getTotalValue((ExBase)null));
	}

	/**
	 * {@link exalge2.ExTransfer#put(exalge2.ExBasePattern, exalge2.ExBasePattern, java.lang.String, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testPutExBasePatternExBasePatternStringBigDecimal() {
		final String methodName = "ExTransferTest:testPutExBasePatternExBasePatternStringBigDecimal";
		boolean coughtException;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//--- NullPointerException
		try {
			trans.put(null, patFruitNum, "AGGRE", BigDecimal.ONE);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(patFishAll, null, "RATIO", int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put((ExBasePattern)null, (ExBasePattern)null, "MULTIPLY", f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(patFishAll, patTunaAll, "RATIO", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(patMeatAll, patBeefAll, "MULTIPLY", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.put(patAppleAll, patFruitNum, null, null);
		trans.put(patOrangeAll, patFruitNum, null, int5);
		trans.put(patBananaAll, patFruitNum, "AGGRE", null);
		trans.put(patMelonAll, patExpFruitNum, "aGgRe", int5);
		trans.put(patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.put(patFishAll, patTunaAll, "RATIO", int5);
		trans.put(patFishAll, patBonitoAll, "RaTiO", int3);
		trans.put(patFishAll, patSauryAll, "ratio", int1);
		trans.put(patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int1);
		trans.put(patMeatAll, patBeefAll, "MULTIPLY", int2);
		trans.put(patMeatAll, patPorkAll, "mUlTiPlY", int1);
		trans.put(patMeatAll, patChickenAll, "multiply", int1);
		trans.put(patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005);
		trans.put(patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.put(patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.put(patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, int1);
		trans.put(patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int2);
		trans.put(patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int3);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.put(patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, int5);
		trans.put(patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int3);
		trans.put(patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.put(patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f004);
		assertEqualTransfer(trans, data2);
		//--- illegal arguments
		//--- (aggre)
		try {
			trans.put(patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patOrangeAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patOrangeAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patBananaAll, patBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBananaAll, patBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.put(patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patRochin2All, patHoryuAll, ExTransfer.ATTR_HAT, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patRochin2All, patHoryuAll, ExTransfer.ATTR_HAT, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patRochin1All, patBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patRochin1All, patBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		//--- not same attr
		try {
			trans.put(patBallPenYen, patBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBallPenYen, patBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patBallPenYen, patBallPenNum, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBallPenYen, patBallPenNum, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patFishAll, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		}
		assertTrue(coughtException);
		try {
			trans.put(patFishAll, patTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
		}
		assertTrue(coughtException);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//--- NullPointerException
		try {
			trans.put(null, patFruitNum, "AGGRE", BigDecimal.ONE);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(patFishAll, null, "RATIO", int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put((ExBasePattern)null, (ExBasePattern)null, "MULTIPLY", f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(patFishAll, patTunaAll, "RATIO", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(patMeatAll, patBeefAll, "MULTIPLY", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.put(patAppleAll, patFruitNum, null, null);
		trans.put(patOrangeAll, patFruitNum, null, int5);
		trans.put(patBananaAll, patFruitNum, "AGGRE", null);
		trans.put(patMelonAll, patExpFruitNum, "aGgRe", int5);
		trans.put(patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.put(patFishAll, patTunaAll, "RATIO", int5);
		trans.put(patFishAll, patBonitoAll, "RaTiO", int3);
		trans.put(patFishAll, patSauryAll, "ratio", int1);
		trans.put(patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int1);
		trans.put(patMeatAll, patBeefAll, "MULTIPLY", int2);
		trans.put(patMeatAll, patPorkAll, "mUlTiPlY", int1);
		trans.put(patMeatAll, patChickenAll, "multiply", int1);
		trans.put(patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005);
		trans.put(patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.put(patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.put(patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, int1);
		trans.put(patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int2);
		trans.put(patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int3);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.put(patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, int5);
		trans.put(patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int3);
		trans.put(patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.put(patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f004);
		assertEqualTransfer(trans, data2);
		//--- illegal arguments
		//--- (aggre)
		try {
			trans.put(patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patOrangeAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patOrangeAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patBananaAll, patBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBananaAll, patBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.put(patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patRochin2All, patHoryuAll, ExTransfer.ATTR_HAT, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patRochin2All, patHoryuAll, ExTransfer.ATTR_HAT, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patRochin1All, patBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patRochin1All, patBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		//--- not same attr
		try {
			trans.put(patBallPenYen, patBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBallPenYen, patBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patBallPenYen, patBallPenNum, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBallPenYen, patBallPenNum, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(patFishAll, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		}
		assertTrue(coughtException);
		try {
			trans.put(patFishAll, patTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
		}
		assertTrue(coughtException);
	}

	/**
	 * {@link exalge2.ExTransfer#put(exalge2.ExBase, exalge2.ExBase, java.lang.String, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testPutExBaseExBaseStringBigDecimal() {
		final String methodName = "ExTransferTest:testPutExBaseExBaseStringBigDecimal";
		boolean coughtException;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//--- NullPointerException
		try {
			trans.put(null, baseFruitNum, "AGGRE", BigDecimal.ONE);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(baseFishAll, null, "RATIO", int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put((ExBase)null, (ExBase)null, "MULTIPLY", f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(baseFishAll, baseTunaAll, "RATIO", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(baseMeatAll, baseBeefAll, "MULTIPLY", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.put(baseAppleAll, baseFruitNum, null, null);
		trans.put(baseOrangeAll, baseFruitNum, null, int5);
		trans.put(baseBananaAll, baseFruitNum, "AGGRE", null);
		trans.put(baseMelonAll, baseExpFruitNum, "aGgRe", int5);
		trans.put(baseMangoAll, baseExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.put(baseFishAll, baseTunaAll, "RATIO", int5);
		trans.put(baseFishAll, baseBonitoAll, "RaTiO", int3);
		trans.put(baseFishAll, baseSauryAll, "ratio", int1);
		trans.put(baseFishAll, baseSardineAll, ExTransfer.ATTR_RATIO, int1);
		trans.put(baseMeatAll, baseBeefAll, "MULTIPLY", int2);
		trans.put(baseMeatAll, basePorkAll, "mUlTiPlY", int1);
		trans.put(baseMeatAll, baseChickenAll, "multiply", int1);
		trans.put(baseBallPenYen, baseBallPenNum, ExTransfer.ATTR_MULTIPLY, f005);
		trans.put(baseMechaPenYen, baseMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.put(basePencilYen, basePencilNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.put(basePrefectureAll, baseJapanAll, ExTransfer.ATTR_AGGRE, int1);
		trans.put(baseRochin1All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int2);
		trans.put(baseRochin2All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int3);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.put(baseFishAll, baseSauryAll, ExTransfer.ATTR_RATIO, int5);
		trans.put(baseFishAll, baseSardineAll, ExTransfer.ATTR_RATIO, int3);
		trans.put(baseMeatAll, baseBeefAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(baseMeatAll, basePorkAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(baseMeatAll, baseChickenAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(baseMechaPenYen, baseMechaPenNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.put(basePencilYen, basePencilNum, ExTransfer.ATTR_MULTIPLY, f004);
		assertEqualTransfer(trans, data2);
		//--- illegal arguments
		//--- (aggre)
		try {
			trans.put(baseAppleAll, baseFruitNum, ExTransfer.ATTR_AGGRE, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseAppleAll, baseFruitNum, ExTransfer.ATTR_AGGRE, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseOrangeAll, baseExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseOrangeAll, baseExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseBananaAll, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBananaAll, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.put(baseRochin1All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseRochin1All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseRochin2All, baseHoryuAll, ExTransfer.ATTR_HAT, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseRochin2All, baseHoryuAll, ExTransfer.ATTR_HAT, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseRochin1All, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseRochin1All, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		//--- not same attr
		try {
			trans.put(baseBallPenYen, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBallPenYen, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseBallPenYen, baseBallPenNum, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBallPenYen, baseBallPenNum, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseFishAll, baseBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseFishAll, baseTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
		}
		assertTrue(coughtException);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//--- NullPointerException
		try {
			trans.put(null, baseFruitNum, "AGGRE", BigDecimal.ONE);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(baseFishAll, null, "RATIO", int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put((ExBase)null, (ExBase)null, "MULTIPLY", f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(baseFishAll, baseTunaAll, "RATIO", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.put(baseMeatAll, baseBeefAll, "MULTIPLY", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.put(baseAppleAll, baseFruitNum, null, null);
		trans.put(baseOrangeAll, baseFruitNum, null, int5);
		trans.put(baseBananaAll, baseFruitNum, "AGGRE", null);
		trans.put(baseMelonAll, baseExpFruitNum, "aGgRe", int5);
		trans.put(baseMangoAll, baseExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.put(baseFishAll, baseTunaAll, "RATIO", int5);
		trans.put(baseFishAll, baseBonitoAll, "RaTiO", int3);
		trans.put(baseFishAll, baseSauryAll, "ratio", int1);
		trans.put(baseFishAll, baseSardineAll, ExTransfer.ATTR_RATIO, int1);
		trans.put(baseMeatAll, baseBeefAll, "MULTIPLY", int2);
		trans.put(baseMeatAll, basePorkAll, "mUlTiPlY", int1);
		trans.put(baseMeatAll, baseChickenAll, "multiply", int1);
		trans.put(baseBallPenYen, baseBallPenNum, ExTransfer.ATTR_MULTIPLY, f005);
		trans.put(baseMechaPenYen, baseMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.put(basePencilYen, basePencilNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.put(basePrefectureAll, baseJapanAll, ExTransfer.ATTR_AGGRE, int1);
		trans.put(baseRochin1All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int2);
		trans.put(baseRochin2All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int3);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.put(baseFishAll, baseSauryAll, ExTransfer.ATTR_RATIO, int5);
		trans.put(baseFishAll, baseSardineAll, ExTransfer.ATTR_RATIO, int3);
		trans.put(baseMeatAll, baseBeefAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(baseMeatAll, basePorkAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(baseMeatAll, baseChickenAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.put(baseMechaPenYen, baseMechaPenNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.put(basePencilYen, basePencilNum, ExTransfer.ATTR_MULTIPLY, f004);
		assertEqualTransfer(trans, data2);
		//--- illegal arguments
		//--- (aggre)
		try {
			trans.put(baseAppleAll, baseFruitNum, ExTransfer.ATTR_AGGRE, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseAppleAll, baseFruitNum, ExTransfer.ATTR_AGGRE, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseOrangeAll, baseExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseOrangeAll, baseExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseBananaAll, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBananaAll, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.put(baseRochin1All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseRochin1All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseRochin2All, baseHoryuAll, ExTransfer.ATTR_HAT, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseRochin2All, baseHoryuAll, ExTransfer.ATTR_HAT, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseRochin1All, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseRochin1All, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		//--- not same attr
		try {
			trans.put(baseBallPenYen, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBallPenYen, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseBallPenYen, baseBallPenNum, ExTransfer.ATTR_RATIO, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBallPenYen, baseBallPenNum, ExTransfer.ATTR_RATIO, int1);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseFishAll, baseBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		}
		assertTrue(coughtException);
		try {
			trans.put(baseFishAll, baseTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
		}
		assertTrue(coughtException);
	}

	/**
	 * {@link exalge2.ExTransfer#putAggregate(ExBasePattern, ExBasePattern)}、
	 * {@link exalge2.ExTransfer#putHatAggregate(ExBasePattern, ExBasePattern)}、
	 * {@link exalge2.ExTransfer#putRatio(exalge2.ExBasePattern, exalge2.ExBasePattern, java.math.BigDecimal)}、
	 * {@link exalge2.ExTransfer#putMultiply(ExBasePattern, ExBasePattern, BigDecimal)} のためのテスト・メソッド。
	 */
	public void testPutXXXXXExBasePatternExBasePatternBigDecimal() {
		final String methodName = "ExTransferTest:testPutXXXXXExBasePatternExBasePatternBigDecimal";
		boolean coughtException;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//--- NullPointerException
		//--- (aggre)
		try {
			trans.putAggregate(null, patFruitNum);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate(patAppleAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.putHatAggregate(null, patNaibuHoryuAll);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate(patRochin1All, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (ratio)
		try {
			trans.putRatio(patFishAll, null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(null, patTunaAll, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio((ExBasePattern)null, (ExBasePattern)null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(patFishAll, patTunaAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (multiply)
		try {
			trans.putMultiply(patMeatAll, null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(null, patBeefAll, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply((ExBasePattern)null, (ExBasePattern)null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(patMeatAll, patBeefAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.putAggregate(patAppleAll, patFruitNum);
		trans.putAggregate(patOrangeAll, patFruitNum);
		trans.putAggregate(patBananaAll, patFruitNum);
		trans.putAggregate(patMelonAll, patExpFruitNum);
		trans.putAggregate(patMangoAll, patExpFruitNum);
		trans.putRatio(patFishAll, patTunaAll, int5);
		trans.putRatio(patFishAll, patBonitoAll, int3);
		trans.putRatio(patFishAll, patSauryAll, int1);
		trans.putRatio(patFishAll, patSardineAll, int1);
		trans.putMultiply(patMeatAll, patBeefAll, int2);
		trans.putMultiply(patMeatAll, patPorkAll, int1);
		trans.putMultiply(patMeatAll, patChickenAll, int1);
		trans.putMultiply(patBallPenYen, patBallPenNum, f005);
		trans.putMultiply(patMechaPenYen, patMechaPenNum, f004);
		trans.putMultiply(patPencilYen, patPencilNum, f0125);
		trans.putAggregate(patPrefectureAll, patJapanAll);
		trans.putHatAggregate(patRochin1All, patNaibuHoryuAll);
		trans.putHatAggregate(patRochin2All, patNaibuHoryuAll);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.putRatio(patFishAll, patSauryAll, int5);
		trans.putRatio(patFishAll, patSardineAll, int3);
		trans.putMultiply(patMeatAll, patBeefAll, int5);
		trans.putMultiply(patMeatAll, patPorkAll, int5);
		trans.putMultiply(patMeatAll, patChickenAll, int5);
		trans.putMultiply(patMechaPenYen, patMechaPenNum, f0125);
		trans.putMultiply(patPencilYen, patPencilNum, f004);
		assertEqualTransfer(trans, data2);
		//--- illegal arguments
		//--- (aggre)
		try {
			trans.putAggregate(patAppleAll, patFruitNum);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patAppleAll, patFruitNum);
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate(patOrangeAll, patExpFruitNum);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patOrangeAll, patExpFruitNum);
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.putHatAggregate(patRochin1All, patNaibuHoryuAll);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patRochin1All, patNaibuHoryuAll);
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate(patRochin2All, patHoryuAll);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patRochin2All, patHoryuAll);
		}
		assertTrue(coughtException);
		//--- not same attr
		try {
			trans.putRatio(patBananaAll, patBeefAll, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBananaAll, patBeefAll, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(patBallPenYen, patBeefAll, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBallPenYen, patBeefAll, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(patBallPenYen, patBallPenNum, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBallPenYen, patBallPenNum, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(patFishAll, patBallPenNum, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patBallPenNum, f004);
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(patFishAll, patTunaAll, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patTunaAll, f004);
		}
		assertTrue(coughtException);
		//--- illegal value
		try {
			trans.putRatio(patFishAll, patTunaAll, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patTunaAll, new BigDecimal("-1"));
		}
		assertTrue(coughtException);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//--- NullPointerException
		//--- (aggre)
		try {
			trans.putAggregate(null, patFruitNum);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate(patAppleAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.putHatAggregate(null, patNaibuHoryuAll);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate(patRochin1All, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (ratio)
		try {
			trans.putRatio(patFishAll, null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(null, patTunaAll, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio((ExBasePattern)null, (ExBasePattern)null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(patFishAll, patTunaAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (multiply)
		try {
			trans.putMultiply(patMeatAll, null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(null, patBeefAll, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply((ExBasePattern)null, (ExBasePattern)null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(patMeatAll, patBeefAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.putAggregate(patAppleAll, patFruitNum);
		trans.putAggregate(patOrangeAll, patFruitNum);
		trans.putAggregate(patBananaAll, patFruitNum);
		trans.putAggregate(patMelonAll, patExpFruitNum);
		trans.putAggregate(patMangoAll, patExpFruitNum);
		trans.putRatio(patFishAll, patTunaAll, int5);
		trans.putRatio(patFishAll, patBonitoAll, int3);
		trans.putRatio(patFishAll, patSauryAll, int1);
		trans.putRatio(patFishAll, patSardineAll, int1);
		trans.putMultiply(patMeatAll, patBeefAll, int2);
		trans.putMultiply(patMeatAll, patPorkAll, int1);
		trans.putMultiply(patMeatAll, patChickenAll, int1);
		trans.putMultiply(patBallPenYen, patBallPenNum, f005);
		trans.putMultiply(patMechaPenYen, patMechaPenNum, f004);
		trans.putMultiply(patPencilYen, patPencilNum, f0125);
		trans.putAggregate(patPrefectureAll, patJapanAll);
		trans.putHatAggregate(patRochin1All, patNaibuHoryuAll);
		trans.putHatAggregate(patRochin2All, patNaibuHoryuAll);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.putRatio(patFishAll, patSauryAll, int5);
		trans.putRatio(patFishAll, patSardineAll, int3);
		trans.putMultiply(patMeatAll, patBeefAll, int5);
		trans.putMultiply(patMeatAll, patPorkAll, int5);
		trans.putMultiply(patMeatAll, patChickenAll, int5);
		trans.putMultiply(patMechaPenYen, patMechaPenNum, f0125);
		trans.putMultiply(patPencilYen, patPencilNum, f004);
		assertEqualTransfer(trans, data2);
		//--- illegal arguments
		//--- (aggre)
		try {
			trans.putAggregate(patAppleAll, patFruitNum);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patAppleAll, patFruitNum);
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate(patOrangeAll, patExpFruitNum);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patOrangeAll, patExpFruitNum);
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.putHatAggregate(patRochin1All, patNaibuHoryuAll);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patRochin1All, patNaibuHoryuAll);
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate(patRochin2All, patHoryuAll);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patRochin2All, patHoryuAll);
		}
		assertTrue(coughtException);
		//--- not same attr
		try {
			trans.putRatio(patBananaAll, patBeefAll, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBananaAll, patBeefAll, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(patBallPenYen, patBeefAll, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBallPenYen, patBeefAll, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(patBallPenYen, patBallPenNum, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patBallPenYen, patBallPenNum, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(patFishAll, patBallPenNum, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patBallPenNum, f004);
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(patFishAll, patTunaAll, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patTunaAll, f004);
		}
		assertTrue(coughtException);
		//--- illegal value
		try {
			trans.putRatio(patFishAll, patTunaAll, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patTunaAll, new BigDecimal("-1"));
		}
		assertTrue(coughtException);
	}

	/**
	 * {@link exalge2.ExTransfer#putRatio(exalge2.ExBase, exalge2.ExBase, java.math.BigDecimal)} のためのテスト・メソッド。
	 * {@link exalge2.ExTransfer#putAggregate(ExBase, ExBase)}、
	 * {@link exalge2.ExTransfer#putHatAggregate(ExBase, ExBase)}、
	 * {@link exalge2.ExTransfer#putRatio(exalge2.ExBase, exalge2.ExBase, java.math.BigDecimal)}、
	 * {@link exalge2.ExTransfer#putMultiply(ExBase, ExBase, BigDecimal)} のためのテスト・メソッド。
	 */
	public void testPutXXXXXExBaseExBaseBigDecimal() {
		final String methodName = "ExTransferTest:testPutXXXXXExBaseExBaseBigDecimal";
		boolean coughtException;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//--- NullPointerException
		//--- (aggre)
		try {
			trans.putAggregate(null, baseFruitNum);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate(baseAppleAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.putHatAggregate(null, baseNaibuHoryuAll);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate(baseRochin1All, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (ratio)
		try {
			trans.putRatio(baseFishAll, null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(null, baseTunaAll, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio((ExBase)null, (ExBase)null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(baseFishAll, baseTunaAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (multiply)
		try {
			trans.putMultiply(baseMeatAll, null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(null, baseBeefAll, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply((ExBase)null, (ExBase)null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(baseMeatAll, baseBeefAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.putAggregate(baseAppleAll, baseFruitNum);
		trans.putAggregate(baseOrangeAll, baseFruitNum);
		trans.putAggregate(baseBananaAll, baseFruitNum);
		trans.putAggregate(baseMelonAll, baseExpFruitNum);
		trans.putAggregate(baseMangoAll, baseExpFruitNum);
		trans.putRatio(baseFishAll, baseTunaAll, int5);
		trans.putRatio(baseFishAll, baseBonitoAll, int3);
		trans.putRatio(baseFishAll, baseSauryAll, int1);
		trans.putRatio(baseFishAll, baseSardineAll, int1);
		trans.putMultiply(baseMeatAll, baseBeefAll, int2);
		trans.putMultiply(baseMeatAll, basePorkAll, int1);
		trans.putMultiply(baseMeatAll, baseChickenAll, int1);
		trans.putMultiply(baseBallPenYen, baseBallPenNum, f005);
		trans.putMultiply(baseMechaPenYen, baseMechaPenNum, f004);
		trans.putMultiply(basePencilYen, basePencilNum, f0125);
		trans.putAggregate(basePrefectureAll, baseJapanAll);
		trans.putHatAggregate(baseRochin1All, baseNaibuHoryuAll);
		trans.putHatAggregate(baseRochin2All, baseNaibuHoryuAll);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.putRatio(baseFishAll, baseSauryAll, int5);
		trans.putRatio(baseFishAll, baseSardineAll, int3);
		trans.putMultiply(baseMeatAll, baseBeefAll, int5);
		trans.putMultiply(baseMeatAll, basePorkAll, int5);
		trans.putMultiply(baseMeatAll, baseChickenAll, int5);
		trans.putMultiply(baseMechaPenYen, baseMechaPenNum, f0125);
		trans.putMultiply(basePencilYen, basePencilNum, f004);
		assertEqualTransfer(trans, data2);
		//--- illegal arguments
		//--- (aggre)
		try {
			trans.putAggregate(baseAppleAll, baseFruitNum);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseAppleAll, baseFruitNum);
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate(baseOrangeAll, baseExpFruitNum);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseOrangeAll, baseExpFruitNum);
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.putHatAggregate(baseRochin1All, baseNaibuHoryuAll);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseRochin1All, baseNaibuHoryuAll);
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate(baseRochin2All, baseHoryuAll);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseRochin2All, baseHoryuAll);
		}
		assertTrue(coughtException);
		//--- not same attr
		try {
			trans.putRatio(baseBananaAll, baseBeefAll, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBananaAll, baseBeefAll, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(baseBallPenYen, baseBeefAll, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBallPenYen, baseBeefAll, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(baseBallPenYen, baseBallPenNum, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBallPenYen, baseBallPenNum, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(baseFishAll, baseBallPenNum, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseBallPenNum, f004);
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(baseFishAll, baseTunaAll, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseTunaAll, f004);
		}
		assertTrue(coughtException);
		//--- illegal value
		try {
			trans.putRatio(baseFishAll, baseTunaAll, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseTunaAll, new BigDecimal("-1"));
		}
		assertTrue(coughtException);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//--- NullPointerException
		//--- (aggre)
		try {
			trans.putAggregate(null, baseFruitNum);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate(baseAppleAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.putHatAggregate(null, baseNaibuHoryuAll);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate(baseRochin1All, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (ratio)
		try {
			trans.putRatio(baseFishAll, null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(null, baseTunaAll, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio((ExBase)null, (ExBase)null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(baseFishAll, baseTunaAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (multiply)
		try {
			trans.putMultiply(baseMeatAll, null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(null, baseBeefAll, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply((ExBase)null, (ExBase)null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(baseMeatAll, baseBeefAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.putAggregate(baseAppleAll, baseFruitNum);
		trans.putAggregate(baseOrangeAll, baseFruitNum);
		trans.putAggregate(baseBananaAll, baseFruitNum);
		trans.putAggregate(baseMelonAll, baseExpFruitNum);
		trans.putAggregate(baseMangoAll, baseExpFruitNum);
		trans.putRatio(baseFishAll, baseTunaAll, int5);
		trans.putRatio(baseFishAll, baseBonitoAll, int3);
		trans.putRatio(baseFishAll, baseSauryAll, int1);
		trans.putRatio(baseFishAll, baseSardineAll, int1);
		trans.putMultiply(baseMeatAll, baseBeefAll, int2);
		trans.putMultiply(baseMeatAll, basePorkAll, int1);
		trans.putMultiply(baseMeatAll, baseChickenAll, int1);
		trans.putMultiply(baseBallPenYen, baseBallPenNum, f005);
		trans.putMultiply(baseMechaPenYen, baseMechaPenNum, f004);
		trans.putMultiply(basePencilYen, basePencilNum, f0125);
		trans.putAggregate(basePrefectureAll, baseJapanAll);
		trans.putHatAggregate(baseRochin1All, baseNaibuHoryuAll);
		trans.putHatAggregate(baseRochin2All, baseNaibuHoryuAll);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.putRatio(baseFishAll, baseSauryAll, int5);
		trans.putRatio(baseFishAll, baseSardineAll, int3);
		trans.putMultiply(baseMeatAll, baseBeefAll, int5);
		trans.putMultiply(baseMeatAll, basePorkAll, int5);
		trans.putMultiply(baseMeatAll, baseChickenAll, int5);
		trans.putMultiply(baseMechaPenYen, baseMechaPenNum, f0125);
		trans.putMultiply(basePencilYen, basePencilNum, f004);
		assertEqualTransfer(trans, data2);
		//--- illegal arguments
		//--- (aggre)
		try {
			trans.putAggregate(baseAppleAll, baseFruitNum);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseAppleAll, baseFruitNum);
		}
		assertTrue(coughtException);
		try {
			trans.putAggregate(baseOrangeAll, baseExpFruitNum);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseOrangeAll, baseExpFruitNum);
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.putHatAggregate(baseRochin1All, baseNaibuHoryuAll);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseRochin1All, baseNaibuHoryuAll);
		}
		assertTrue(coughtException);
		try {
			trans.putHatAggregate(baseRochin2All, baseHoryuAll);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseRochin2All, baseHoryuAll);
		}
		assertTrue(coughtException);
		//--- not same attr
		try {
			trans.putRatio(baseBananaAll, baseBeefAll, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBananaAll, baseBeefAll, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(baseBallPenYen, baseBeefAll, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBallPenYen, baseBeefAll, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putRatio(baseBallPenYen, baseBallPenNum, int1);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseBallPenYen, baseBallPenNum, int1);
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(baseFishAll, baseBallPenNum, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseBallPenNum, f004);
		}
		assertTrue(coughtException);
		try {
			trans.putMultiply(baseFishAll, baseTunaAll, f004);
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseTunaAll, f004);
		}
		assertTrue(coughtException);
		//--- illegal value
		try {
			trans.putRatio(baseFishAll, baseTunaAll, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseTunaAll, new BigDecimal("-1"));
		}
		assertTrue(coughtException);
	}

	/**
	 * {@link exalge2.ExTransfer#set(exalge2.ExBasePattern, exalge2.ExBasePattern, java.lang.String, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testSetExBasePatternExBasePatternStringBigDecimal() {
		final String methodName = "ExTransferTest:testSetExBasePatternExBasePatternStringBigDecimal";
		boolean coughtException;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//--- NullPointerException
		try {
			trans.set(null, patFruitNum, "AGGRE", BigDecimal.ONE);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(patFishAll, null, "RATIO", int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set((ExBasePattern)null, (ExBasePattern)null, "MULTIPLY", f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(patFishAll, patTunaAll, "RATIO", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(patMeatAll, patBeefAll, "MULTIPLY", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.set(patAppleAll, patFruitNum, null, null);
		trans.set(patOrangeAll, patFruitNum, null, int5);
		trans.set(patBananaAll, patFruitNum, "AGGRE", null);
		trans.set(patMelonAll, patExpFruitNum, "aGgRe", int5);
		trans.set(patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(patFishAll, patTunaAll, "RATIO", int5);
		trans.set(patFishAll, patBonitoAll, "RaTiO", int3);
		trans.set(patFishAll, patSauryAll, "ratio", int1);
		trans.set(patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(patMeatAll, patBeefAll, "MULTIPLY", int2);
		trans.set(patMeatAll, patPorkAll, "mUlTiPlY", int1);
		trans.set(patMeatAll, patChickenAll, "multiply", int1);
		trans.set(patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005);
		trans.set(patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.set(patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, int1);
		trans.set(patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int2);
		trans.set(patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int3);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.set(patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, int5);
		trans.set(patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int3);
		trans.set(patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.set(patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f004);
		assertEqualTransfer(trans, data2);
		//--- put method illegal case
		trans.set(patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(patOrangeAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(patBananaAll, patBeefAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(patBallPenYen, patBeefAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(patBallPenYen, patBallPenNum, ExTransfer.ATTR_RATIO, int1);
		trans.set(patFishAll, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(patFishAll, patTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(patPrefectureAll, patJapanAll, ExTransfer.ATTR_HAT, int5);
		trans.set(patRochin1All, patHoryuAll, ExTransfer.ATTR_AGGRE, null);
		trans.set(patRochin2All, patHoryuAll, ExTransfer.ATTR_HAT, null);
		trans.set(patRochin3All, patHoryuAll, ExTransfer.ATTR_AGGRE, null);
		assertEqualTransfer(trans, data3);
		//--- illegal arguments
		try {
			trans.set(patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, new BigDecimal("-1"));
		}
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//--- NullPointerException
		try {
			trans.set(null, patFruitNum, "AGGRE", BigDecimal.ONE);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(patFishAll, null, "RATIO", int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set((ExBasePattern)null, (ExBasePattern)null, "MULTIPLY", f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(patFishAll, patTunaAll, "RATIO", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(patMeatAll, patBeefAll, "MULTIPLY", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.set(patAppleAll, patFruitNum, null, null);
		trans.set(patOrangeAll, patFruitNum, null, int5);
		trans.set(patBananaAll, patFruitNum, "AGGRE", null);
		trans.set(patMelonAll, patExpFruitNum, "aGgRe", int5);
		trans.set(patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(patFishAll, patTunaAll, "RATIO", int5);
		trans.set(patFishAll, patBonitoAll, "RaTiO", int3);
		trans.set(patFishAll, patSauryAll, "ratio", int1);
		trans.set(patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(patMeatAll, patBeefAll, "MULTIPLY", int2);
		trans.set(patMeatAll, patPorkAll, "mUlTiPlY", int1);
		trans.set(patMeatAll, patChickenAll, "multiply", int1);
		trans.set(patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005);
		trans.set(patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.set(patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, int1);
		trans.set(patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int2);
		trans.set(patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, int3);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.set(patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, int5);
		trans.set(patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int3);
		trans.set(patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.set(patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f004);
		assertEqualTransfer(trans, data2);
		//--- put method illegal case
		trans.set(patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(patOrangeAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(patBananaAll, patBeefAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(patBallPenYen, patBeefAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(patBallPenYen, patBallPenNum, ExTransfer.ATTR_RATIO, int1);
		trans.set(patFishAll, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(patFishAll, patTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(patPrefectureAll, patJapanAll, ExTransfer.ATTR_HAT, int5);
		trans.set(patRochin1All, patHoryuAll, ExTransfer.ATTR_AGGRE, null);
		trans.set(patRochin2All, patHoryuAll, ExTransfer.ATTR_HAT, null);
		trans.set(patRochin3All, patHoryuAll, ExTransfer.ATTR_AGGRE, null);
		assertEqualTransfer(trans, data3);
		//--- illegal arguments
		try {
			trans.set(patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, new BigDecimal("-1"));
		}
	}

	/**
	 * {@link exalge2.ExTransfer#set(exalge2.ExBase, exalge2.ExBase, java.lang.String, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testSetExBaseExBaseStringBigDecimal() {
		final String methodName = "ExTransferTest:testSetExBaseExBaseStringBigDecimal";
		boolean coughtException;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//--- NullPointerException
		try {
			trans.set(null, baseFruitNum, "AGGRE", BigDecimal.ONE);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(baseFishAll, null, "RATIO", int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set((ExBase)null, (ExBase)null, "MULTIPLY", f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(baseFishAll, baseTunaAll, "RATIO", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(baseMeatAll, baseBeefAll, "MULTIPLY", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.set(baseAppleAll, baseFruitNum, null, null);
		trans.set(baseOrangeAll, baseFruitNum, null, int5);
		trans.set(baseBananaAll, baseFruitNum, "AGGRE", null);
		trans.set(baseMelonAll, baseExpFruitNum, "aGgRe", int5);
		trans.set(baseMangoAll, baseExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(baseFishAll, baseTunaAll, "RATIO", int5);
		trans.set(baseFishAll, baseBonitoAll, "RaTiO", int3);
		trans.set(baseFishAll, baseSauryAll, "ratio", int1);
		trans.set(baseFishAll, baseSardineAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(baseMeatAll, baseBeefAll, "MULTIPLY", int2);
		trans.set(baseMeatAll, basePorkAll, "mUlTiPlY", int1);
		trans.set(baseMeatAll, baseChickenAll, "multiply", int1);
		trans.set(baseBallPenYen, baseBallPenNum, ExTransfer.ATTR_MULTIPLY, f005);
		trans.set(baseMechaPenYen, baseMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(basePencilYen, basePencilNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.set(basePrefectureAll, baseJapanAll, ExTransfer.ATTR_AGGRE, int1);
		trans.set(baseRochin1All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int2);
		trans.set(baseRochin2All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int3);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.set(baseFishAll, baseSauryAll, ExTransfer.ATTR_RATIO, int5);
		trans.set(baseFishAll, baseSardineAll, ExTransfer.ATTR_RATIO, int3);
		trans.set(baseMeatAll, baseBeefAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(baseMeatAll, basePorkAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(baseMeatAll, baseChickenAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(baseMechaPenYen, baseMechaPenNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.set(basePencilYen, basePencilNum, ExTransfer.ATTR_MULTIPLY, f004);
		assertEqualTransfer(trans, data2);
		//--- put method illegal case
		trans.set(baseAppleAll, baseFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(baseOrangeAll, baseExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(baseBananaAll, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(baseBallPenYen, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(baseBallPenYen, baseBallPenNum, ExTransfer.ATTR_RATIO, int1);
		trans.set(baseFishAll, baseBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(baseFishAll, baseTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(basePrefectureAll, baseJapanAll, ExTransfer.ATTR_HAT, int5);
		trans.set(baseRochin1All, baseHoryuAll, ExTransfer.ATTR_AGGRE, null);
		trans.set(baseRochin2All, baseHoryuAll, ExTransfer.ATTR_HAT, null);
		trans.set(baseRochin3All, baseHoryuAll, ExTransfer.ATTR_AGGRE, null);
		assertEqualTransfer(trans, data3);
		//--- illegal arguments
		try {
			trans.set(baseFishAll, baseTunaAll, ExTransfer.ATTR_RATIO, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseTunaAll, ExTransfer.ATTR_RATIO, new BigDecimal("-1"));
		}
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//--- NullPointerException
		try {
			trans.set(null, baseFruitNum, "AGGRE", BigDecimal.ONE);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(baseFishAll, null, "RATIO", int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set((ExBase)null, (ExBase)null, "MULTIPLY", f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(baseFishAll, baseTunaAll, "RATIO", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.set(baseMeatAll, baseBeefAll, "MULTIPLY", null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.set(baseAppleAll, baseFruitNum, null, null);
		trans.set(baseOrangeAll, baseFruitNum, null, int5);
		trans.set(baseBananaAll, baseFruitNum, "AGGRE", null);
		trans.set(baseMelonAll, baseExpFruitNum, "aGgRe", int5);
		trans.set(baseMangoAll, baseExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(baseFishAll, baseTunaAll, "RATIO", int5);
		trans.set(baseFishAll, baseBonitoAll, "RaTiO", int3);
		trans.set(baseFishAll, baseSauryAll, "ratio", int1);
		trans.set(baseFishAll, baseSardineAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(baseMeatAll, baseBeefAll, "MULTIPLY", int2);
		trans.set(baseMeatAll, basePorkAll, "mUlTiPlY", int1);
		trans.set(baseMeatAll, baseChickenAll, "multiply", int1);
		trans.set(baseBallPenYen, baseBallPenNum, ExTransfer.ATTR_MULTIPLY, f005);
		trans.set(baseMechaPenYen, baseMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(basePencilYen, basePencilNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.set(basePrefectureAll, baseJapanAll, ExTransfer.ATTR_AGGRE, int1);
		trans.set(baseRochin1All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int2);
		trans.set(baseRochin2All, baseNaibuHoryuAll, ExTransfer.ATTR_HAT, int3);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.set(baseFishAll, baseSauryAll, ExTransfer.ATTR_RATIO, int5);
		trans.set(baseFishAll, baseSardineAll, ExTransfer.ATTR_RATIO, int3);
		trans.set(baseMeatAll, baseBeefAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(baseMeatAll, basePorkAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(baseMeatAll, baseChickenAll, ExTransfer.ATTR_MULTIPLY, int5);
		trans.set(baseMechaPenYen, baseMechaPenNum, ExTransfer.ATTR_MULTIPLY, f0125);
		trans.set(basePencilYen, basePencilNum, ExTransfer.ATTR_MULTIPLY, f004);
		assertEqualTransfer(trans, data2);
		//--- put method illegal case
		trans.set(baseAppleAll, baseFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(baseOrangeAll, baseExpFruitNum, ExTransfer.ATTR_AGGRE, int1);
		trans.set(baseBananaAll, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(baseBallPenYen, baseBeefAll, ExTransfer.ATTR_RATIO, int1);
		trans.set(baseBallPenYen, baseBallPenNum, ExTransfer.ATTR_RATIO, int1);
		trans.set(baseFishAll, baseBallPenNum, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(baseFishAll, baseTunaAll, ExTransfer.ATTR_MULTIPLY, f004);
		trans.set(basePrefectureAll, baseJapanAll, ExTransfer.ATTR_HAT, int5);
		trans.set(baseRochin1All, baseHoryuAll, ExTransfer.ATTR_AGGRE, null);
		trans.set(baseRochin2All, baseHoryuAll, ExTransfer.ATTR_HAT, null);
		trans.set(baseRochin3All, baseHoryuAll, ExTransfer.ATTR_AGGRE, null);
		assertEqualTransfer(trans, data3);
		//--- illegal arguments
		try {
			trans.set(baseFishAll, baseTunaAll, ExTransfer.ATTR_RATIO, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseTunaAll, ExTransfer.ATTR_RATIO, new BigDecimal("-1"));
		}
	}

	/**
	 * {@link exalge2.ExTransfer#setAggregate(ExBasePattern, ExBasePattern)}、
	 * {@link exalge2.ExTransfer#setHatAggregate(ExBasePattern, ExBasePattern)}、
	 * {@link exalge2.ExTransfer#setRatio(exalge2.ExBasePattern, exalge2.ExBasePattern, java.math.BigDecimal)}、
	 * {@link exalge2.ExTransfer#setMultiply(ExBasePattern, ExBasePattern, BigDecimal)} のためのテスト・メソッド。
	 */
	public void testSetXXXXExBasePatternExBasePatternBigDecimal() {
		final String methodName = "ExTransferTest:testSetXXXXXExBasePatternExBasePatternBigDecimal";
		boolean coughtException;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//--- NullPointerException
		//--- (aggre)
		try {
			trans.setAggregate(null, patFruitNum);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setAggregate(patAppleAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setAggregate((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.setHatAggregate(null, patNaibuHoryuAll);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setHatAggregate(patRochin1All, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setHatAggregate((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (ratio)
		try {
			trans.setRatio(patFishAll, null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio(null, patTunaAll, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio((ExBasePattern)null, (ExBasePattern)null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio(patFishAll, patTunaAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (multiply)
		try {
			trans.setMultiply(patMeatAll, null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply(null, patBeefAll, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply((ExBasePattern)null, (ExBasePattern)null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply(patMeatAll, patBeefAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.setAggregate(patAppleAll, patFruitNum);
		trans.setAggregate(patOrangeAll, patFruitNum);
		trans.setAggregate(patBananaAll, patFruitNum);
		trans.setAggregate(patMelonAll, patExpFruitNum);
		trans.setAggregate(patMangoAll, patExpFruitNum);
		trans.setRatio(patFishAll, patTunaAll, int5);
		trans.setRatio(patFishAll, patBonitoAll, int3);
		trans.setRatio(patFishAll, patSauryAll, int1);
		trans.setRatio(patFishAll, patSardineAll, int1);
		trans.setMultiply(patMeatAll, patBeefAll, int2);
		trans.setMultiply(patMeatAll, patPorkAll, int1);
		trans.setMultiply(patMeatAll, patChickenAll, int1);
		trans.setMultiply(patBallPenYen, patBallPenNum, f005);
		trans.setMultiply(patMechaPenYen, patMechaPenNum, f004);
		trans.setMultiply(patPencilYen, patPencilNum, f0125);
		trans.setAggregate(patPrefectureAll, patJapanAll);
		trans.setHatAggregate(patRochin1All, patNaibuHoryuAll);
		trans.setHatAggregate(patRochin2All, patNaibuHoryuAll);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.setRatio(patFishAll, patSauryAll, int5);
		trans.setRatio(patFishAll, patSardineAll, int3);
		trans.setMultiply(patMeatAll, patBeefAll, int5);
		trans.setMultiply(patMeatAll, patPorkAll, int5);
		trans.setMultiply(patMeatAll, patChickenAll, int5);
		trans.setMultiply(patMechaPenYen, patMechaPenNum, f0125);
		trans.setMultiply(patPencilYen, patPencilNum, f004);
		assertEqualTransfer(trans, data2);
		//--- put method illegal case
		trans.setAggregate(patAppleAll, patFruitNum);
		trans.setAggregate(patOrangeAll, patExpFruitNum);
		trans.setRatio(patBananaAll, patBeefAll, int1);
		trans.setRatio(patBallPenYen, patBeefAll, int1);
		trans.setRatio(patBallPenYen, patBallPenNum, int1);
		trans.setMultiply(patFishAll, patBallPenNum, f004);
		trans.setMultiply(patFishAll, patTunaAll, f004);
		trans.setHatAggregate(patPrefectureAll, patJapanAll);
		trans.setAggregate(patRochin1All, patHoryuAll);
		trans.setHatAggregate(patRochin2All, patHoryuAll);
		trans.setAggregate(patRochin3All, patHoryuAll);
		assertEqualTransfer(trans, data3);
		//--- illegal arguments
		try {
			trans.setRatio(patFishAll, patTunaAll, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patTunaAll, new BigDecimal("-1"));
		}
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//--- NullPointerException
		//--- (aggre)
		try {
			trans.setAggregate(null, patFruitNum);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setAggregate(patAppleAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setAggregate((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.setHatAggregate(null, patNaibuHoryuAll);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setHatAggregate(patRochin1All, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setHatAggregate((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (ratio)
		try {
			trans.setRatio(patFishAll, null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio(null, patTunaAll, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio((ExBasePattern)null, (ExBasePattern)null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio(patFishAll, patTunaAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (multiply)
		try {
			trans.setMultiply(patMeatAll, null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply(null, patBeefAll, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply((ExBasePattern)null, (ExBasePattern)null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply(patMeatAll, patBeefAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.setAggregate(patAppleAll, patFruitNum);
		trans.setAggregate(patOrangeAll, patFruitNum);
		trans.setAggregate(patBananaAll, patFruitNum);
		trans.setAggregate(patMelonAll, patExpFruitNum);
		trans.setAggregate(patMangoAll, patExpFruitNum);
		trans.setRatio(patFishAll, patTunaAll, int5);
		trans.setRatio(patFishAll, patBonitoAll, int3);
		trans.setRatio(patFishAll, patSauryAll, int1);
		trans.setRatio(patFishAll, patSardineAll, int1);
		trans.setMultiply(patMeatAll, patBeefAll, int2);
		trans.setMultiply(patMeatAll, patPorkAll, int1);
		trans.setMultiply(patMeatAll, patChickenAll, int1);
		trans.setMultiply(patBallPenYen, patBallPenNum, f005);
		trans.setMultiply(patMechaPenYen, patMechaPenNum, f004);
		trans.setMultiply(patPencilYen, patPencilNum, f0125);
		trans.setAggregate(patPrefectureAll, patJapanAll);
		trans.setHatAggregate(patRochin1All, patNaibuHoryuAll);
		trans.setHatAggregate(patRochin2All, patNaibuHoryuAll);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.setRatio(patFishAll, patSauryAll, int5);
		trans.setRatio(patFishAll, patSardineAll, int3);
		trans.setMultiply(patMeatAll, patBeefAll, int5);
		trans.setMultiply(patMeatAll, patPorkAll, int5);
		trans.setMultiply(patMeatAll, patChickenAll, int5);
		trans.setMultiply(patMechaPenYen, patMechaPenNum, f0125);
		trans.setMultiply(patPencilYen, patPencilNum, f004);
		assertEqualTransfer(trans, data2);
		//--- put method illegal case
		trans.setAggregate(patAppleAll, patFruitNum);
		trans.setAggregate(patOrangeAll, patExpFruitNum);
		trans.setRatio(patBananaAll, patBeefAll, int1);
		trans.setRatio(patBallPenYen, patBeefAll, int1);
		trans.setRatio(patBallPenYen, patBallPenNum, int1);
		trans.setMultiply(patFishAll, patBallPenNum, f004);
		trans.setMultiply(patFishAll, patTunaAll, f004);
		trans.setHatAggregate(patPrefectureAll, patJapanAll);
		trans.setAggregate(patRochin1All, patHoryuAll);
		trans.setHatAggregate(patRochin2All, patHoryuAll);
		trans.setAggregate(patRochin3All, patHoryuAll);
		assertEqualTransfer(trans, data3);
		//--- illegal arguments
		try {
			trans.setRatio(patFishAll, patTunaAll, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, patFishAll, patTunaAll, new BigDecimal("-1"));
		}
	}

	/**
	 * {@link exalge2.ExTransfer#setAggregate(ExBase, ExBase)}、
	 * {@link exalge2.ExTransfer#setHatAggregate(ExBase, ExBase)}、
	 * {@link exalge2.ExTransfer#setRatio(exalge2.ExBase, exalge2.ExBase, java.math.BigDecimal)}、
	 * {@link exalge2.ExTransfer#setMultiply(ExBase, ExBase, BigDecimal)} のためのテスト・メソッド。
	 */
	public void testSetXXXXXExBaseExBaseBigDecimal() {
		final String methodName = "ExTransferTest:testSetXXXXXExBaseExBaseBigDecimal";
		boolean coughtException;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//--- NullPointerException
		//--- (aggre)
		try {
			trans.setAggregate(null, baseFruitNum);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setAggregate(baseAppleAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setAggregate((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.setHatAggregate(null, baseNaibuHoryuAll);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setHatAggregate(baseRochin1All, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setHatAggregate((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (ratio)
		try {
			trans.setRatio(baseFishAll, null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio(null, baseTunaAll, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio((ExBase)null, (ExBase)null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio(baseFishAll, baseTunaAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (multiply)
		try {
			trans.setMultiply(baseMeatAll, null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply(null, baseBeefAll, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply((ExBase)null, (ExBase)null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply(baseMeatAll, baseBeefAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.setAggregate(baseAppleAll, baseFruitNum);
		trans.setAggregate(baseOrangeAll, baseFruitNum);
		trans.setAggregate(baseBananaAll, baseFruitNum);
		trans.setAggregate(baseMelonAll, baseExpFruitNum);
		trans.setAggregate(baseMangoAll, baseExpFruitNum);
		trans.setRatio(baseFishAll, baseTunaAll, int5);
		trans.setRatio(baseFishAll, baseBonitoAll, int3);
		trans.setRatio(baseFishAll, baseSauryAll, int1);
		trans.setRatio(baseFishAll, baseSardineAll, int1);
		trans.setMultiply(baseMeatAll, baseBeefAll, int2);
		trans.setMultiply(baseMeatAll, basePorkAll, int1);
		trans.setMultiply(baseMeatAll, baseChickenAll, int1);
		trans.setMultiply(baseBallPenYen, baseBallPenNum, f005);
		trans.setMultiply(baseMechaPenYen, baseMechaPenNum, f004);
		trans.setMultiply(basePencilYen, basePencilNum, f0125);
		trans.setAggregate(basePrefectureAll, baseJapanAll);
		trans.setHatAggregate(baseRochin1All, baseNaibuHoryuAll);
		trans.setHatAggregate(baseRochin2All, baseNaibuHoryuAll);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.setRatio(baseFishAll, baseSauryAll, int5);
		trans.setRatio(baseFishAll, baseSardineAll, int3);
		trans.setMultiply(baseMeatAll, baseBeefAll, int5);
		trans.setMultiply(baseMeatAll, basePorkAll, int5);
		trans.setMultiply(baseMeatAll, baseChickenAll, int5);
		trans.setMultiply(baseMechaPenYen, baseMechaPenNum, f0125);
		trans.setMultiply(basePencilYen, basePencilNum, f004);
		assertEqualTransfer(trans, data2);
		//--- put method illegal case
		trans.setAggregate(baseAppleAll, baseFruitNum);
		trans.setAggregate(baseOrangeAll, baseExpFruitNum);
		trans.setRatio(baseBananaAll, baseBeefAll, int1);
		trans.setRatio(baseBallPenYen, baseBeefAll, int1);
		trans.setRatio(baseBallPenYen, baseBallPenNum, int1);
		trans.setMultiply(baseFishAll, baseBallPenNum, f004);
		trans.setMultiply(baseFishAll, baseTunaAll, f004);
		trans.setHatAggregate(basePrefectureAll, baseJapanAll);
		trans.setAggregate(baseRochin1All, baseHoryuAll);
		trans.setHatAggregate(baseRochin2All, baseHoryuAll);
		trans.setAggregate(baseRochin3All, baseHoryuAll);
		assertEqualTransfer(trans, data3);
		//--- illegal arguments
		try {
			trans.setRatio(baseFishAll, baseTunaAll, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseTunaAll, new BigDecimal("-1"));
		}
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//--- NullPointerException
		//--- (aggre)
		try {
			trans.setAggregate(null, baseFruitNum);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setAggregate(baseAppleAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setAggregate((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (hat)
		try {
			trans.setHatAggregate(null, baseNaibuHoryuAll);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setHatAggregate(baseRochin1All, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setHatAggregate((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (ratio)
		try {
			trans.setRatio(baseFishAll, null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio(null, baseTunaAll, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio((ExBase)null, (ExBase)null, int5);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setRatio(baseFishAll, baseTunaAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- (multiply)
		try {
			trans.setMultiply(baseMeatAll, null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply(null, baseBeefAll, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply((ExBase)null, (ExBase)null, f005);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.setMultiply(baseMeatAll, baseBeefAll, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//--- normal pattern
		trans.setAggregate(baseAppleAll, baseFruitNum);
		trans.setAggregate(baseOrangeAll, baseFruitNum);
		trans.setAggregate(baseBananaAll, baseFruitNum);
		trans.setAggregate(baseMelonAll, baseExpFruitNum);
		trans.setAggregate(baseMangoAll, baseExpFruitNum);
		trans.setRatio(baseFishAll, baseTunaAll, int5);
		trans.setRatio(baseFishAll, baseBonitoAll, int3);
		trans.setRatio(baseFishAll, baseSauryAll, int1);
		trans.setRatio(baseFishAll, baseSardineAll, int1);
		trans.setMultiply(baseMeatAll, baseBeefAll, int2);
		trans.setMultiply(baseMeatAll, basePorkAll, int1);
		trans.setMultiply(baseMeatAll, baseChickenAll, int1);
		trans.setMultiply(baseBallPenYen, baseBallPenNum, f005);
		trans.setMultiply(baseMechaPenYen, baseMechaPenNum, f004);
		trans.setMultiply(basePencilYen, basePencilNum, f0125);
		trans.setAggregate(basePrefectureAll, baseJapanAll);
		trans.setHatAggregate(baseRochin1All, baseNaibuHoryuAll);
		trans.setHatAggregate(baseRochin2All, baseNaibuHoryuAll);
		assertEqualTransfer(trans, data1);
		//--- overwrite
		trans.setRatio(baseFishAll, baseSauryAll, int5);
		trans.setRatio(baseFishAll, baseSardineAll, int3);
		trans.setMultiply(baseMeatAll, baseBeefAll, int5);
		trans.setMultiply(baseMeatAll, basePorkAll, int5);
		trans.setMultiply(baseMeatAll, baseChickenAll, int5);
		trans.setMultiply(baseMechaPenYen, baseMechaPenNum, f0125);
		trans.setMultiply(basePencilYen, basePencilNum, f004);
		assertEqualTransfer(trans, data2);
		//--- put method illegal case
		trans.setAggregate(baseAppleAll, baseFruitNum);
		trans.setAggregate(baseOrangeAll, baseExpFruitNum);
		trans.setRatio(baseBananaAll, baseBeefAll, int1);
		trans.setRatio(baseBallPenYen, baseBeefAll, int1);
		trans.setRatio(baseBallPenYen, baseBallPenNum, int1);
		trans.setMultiply(baseFishAll, baseBallPenNum, f004);
		trans.setMultiply(baseFishAll, baseTunaAll, f004);
		trans.setHatAggregate(basePrefectureAll, baseJapanAll);
		trans.setAggregate(baseRochin1All, baseHoryuAll);
		trans.setHatAggregate(baseRochin2All, baseHoryuAll);
		trans.setAggregate(baseRochin3All, baseHoryuAll);
		assertEqualTransfer(trans, data3);
		//--- illegal arguments
		try {
			trans.setRatio(baseFishAll, baseTunaAll, new BigDecimal("-1"));
			coughtException = false;
		} catch (IllegalArgumentException ex) {
			coughtException = true;
			printException(methodName, ex, baseFishAll, baseTunaAll, new BigDecimal("-1"));
		}
	}

	/**
	 * {@link exalge2.ExTransfer#remove(exalge2.ExBasePattern, exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testRemoveExBasePatternExBasePattern() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//---
		assertTrue(trans.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.remove(patAppleAll, null));
		assertFalse(trans.remove(null, patFruitNum));
		assertFalse(trans.remove((ExBasePattern)null, (ExBasePattern)null));
		for (TransferEntry entry : data1) {
			assertFalse(trans.remove(entry.from(), entry.to()));
		}
		//---
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertFalse(trans.remove(patAppleAll, null));
		assertFalse(trans.remove(null, patFruitNum));
		assertFalse(trans.remove((ExBasePattern)null, (ExBasePattern)null));
		for (TransferEntry entry : data1) {
			assertTrue(trans.remove(entry.from(), entry.to()));
		}
		assertTrue(trans.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(0 == trans.numElements);
		assertTrue(trans.map.isEmpty());
		assertTrue(null == trans.patIndex);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//---
		assertTrue(trans.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.remove(patAppleAll, null));
		assertFalse(trans.remove(null, patFruitNum));
		assertFalse(trans.remove((ExBasePattern)null, (ExBasePattern)null));
		for (TransferEntry entry : data1) {
			assertFalse(trans.remove(entry.from(), entry.to()));
		}
		//---
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertFalse(trans.remove(patAppleAll, null));
		assertFalse(trans.remove(null, patFruitNum));
		assertFalse(trans.remove((ExBasePattern)null, (ExBasePattern)null));
		for (TransferEntry entry : data1) {
			assertTrue(trans.remove(entry.from(), entry.to()));
		}
		assertTrue(trans.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(0 == trans.numElements);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.patIndex.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer#remove(exalge2.ExBase, exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testRemoveExBaseExBase() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//---
		assertTrue(trans.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.remove(baseAppleAll, null));
		assertFalse(trans.remove(null, baseFruitNum));
		assertFalse(trans.remove((ExBase)null, (ExBase)null));
		for (TransferEntry entry : data1) {
			ExBase bf = toBase(ExBase.HAT, entry.from());
			ExBase bt = toBase(ExBase.HAT, entry.to());
			assertFalse(trans.remove(bf, bt));
		}
		//---
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertFalse(trans.remove(baseAppleAll, null));
		assertFalse(trans.remove(null, baseFruitNum));
		assertFalse(trans.remove((ExBase)null, (ExBase)null));
		for (TransferEntry entry : data1) {
			ExBase bf = toBase(ExBase.HAT, entry.from());
			ExBase bt = toBase(ExBase.HAT, entry.to());
			assertTrue(trans.remove(bf, bt));
		}
		assertTrue(trans.isEmpty());
		assertTrue(0 == trans.numElements);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(null == trans.patIndex);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//---
		assertTrue(trans.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.remove(baseAppleAll, null));
		assertFalse(trans.remove(null, baseFruitNum));
		assertFalse(trans.remove((ExBase)null, (ExBase)null));
		for (TransferEntry entry : data1) {
			ExBase bf = toBase(ExBase.HAT, entry.from());
			ExBase bt = toBase(ExBase.HAT, entry.to());
			assertFalse(trans.remove(bf, bt));
		}
		//---
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertFalse(trans.remove(baseAppleAll, null));
		assertFalse(trans.remove(null, baseFruitNum));
		assertFalse(trans.remove((ExBase)null, (ExBase)null));
		for (TransferEntry entry : data1) {
			ExBase bf = toBase(ExBase.HAT, entry.from());
			ExBase bt = toBase(ExBase.HAT, entry.to());
			assertTrue(trans.remove(bf, bt));
		}
		assertTrue(trans.isEmpty());
		assertTrue(0 == trans.numElements);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(trans.patIndex.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer#removeFrom(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testRemoveFromExBasePattern() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//---
		assertTrue(trans.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.removeFrom((ExBasePattern)null));
		assertFalse(trans.removeFrom(patStationeryAll));
		assertFalse(trans.removeFrom(patAppleAll));
		assertFalse(trans.removeFrom(patOrangeAll));
		assertFalse(trans.removeFrom(patBananaAll));
		assertFalse(trans.removeFrom(patMelonAll));
		assertFalse(trans.removeFrom(patMangoAll));
		assertFalse(trans.removeFrom(patFishAll));
		assertFalse(trans.removeFrom(patMeatAll));
		assertFalse(trans.removeFrom(patBallPenYen));
		assertFalse(trans.removeFrom(patMechaPenYen));
		assertFalse(trans.removeFrom(patPencilYen));
		assertFalse(trans.removeFrom(patPrefectureAll));
		assertFalse(trans.removeFrom(patRochin1All));
		assertFalse(trans.removeFrom(patRochin2All));
		//---
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertFalse(trans.removeFrom((ExBasePattern)null));
		assertFalse(trans.removeFrom(patStationeryAll));
		assertTrue(trans.removeFrom(patAppleAll));
		assertTrue(trans.removeFrom(patOrangeAll));
		assertTrue(trans.removeFrom(patBananaAll));
		assertTrue(trans.removeFrom(patMelonAll));
		assertTrue(trans.removeFrom(patMangoAll));
		assertTrue(trans.removeFrom(patFishAll));
		assertTrue(trans.removeFrom(patMeatAll));
		assertTrue(trans.removeFrom(patBallPenYen));
		assertTrue(trans.removeFrom(patMechaPenYen));
		assertTrue(trans.removeFrom(patPencilYen));
		assertTrue(trans.removeFrom(patPrefectureAll));
		assertTrue(trans.removeFrom(patRochin1All));
		assertTrue(trans.removeFrom(patRochin2All));
		assertTrue(trans.isEmpty());
		assertTrue(0 == trans.numElements);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(null == trans.patIndex);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//---
		assertTrue(trans.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.removeFrom((ExBasePattern)null));
		assertFalse(trans.removeFrom(patStationeryAll));
		assertFalse(trans.removeFrom(patAppleAll));
		assertFalse(trans.removeFrom(patOrangeAll));
		assertFalse(trans.removeFrom(patBananaAll));
		assertFalse(trans.removeFrom(patMelonAll));
		assertFalse(trans.removeFrom(patMangoAll));
		assertFalse(trans.removeFrom(patFishAll));
		assertFalse(trans.removeFrom(patMeatAll));
		assertFalse(trans.removeFrom(patBallPenYen));
		assertFalse(trans.removeFrom(patMechaPenYen));
		assertFalse(trans.removeFrom(patPencilYen));
		assertFalse(trans.removeFrom(patPrefectureAll));
		assertFalse(trans.removeFrom(patRochin1All));
		assertFalse(trans.removeFrom(patRochin2All));
		//---
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertFalse(trans.removeFrom((ExBasePattern)null));
		assertFalse(trans.removeFrom(patStationeryAll));
		assertTrue(trans.removeFrom(patAppleAll));
		assertTrue(trans.removeFrom(patOrangeAll));
		assertTrue(trans.removeFrom(patBananaAll));
		assertTrue(trans.removeFrom(patMelonAll));
		assertTrue(trans.removeFrom(patMangoAll));
		assertTrue(trans.removeFrom(patFishAll));
		assertTrue(trans.removeFrom(patMeatAll));
		assertTrue(trans.removeFrom(patBallPenYen));
		assertTrue(trans.removeFrom(patMechaPenYen));
		assertTrue(trans.removeFrom(patPencilYen));
		assertTrue(trans.removeFrom(patPrefectureAll));
		assertTrue(trans.removeFrom(patRochin1All));
		assertTrue(trans.removeFrom(patRochin2All));
		assertTrue(trans.isEmpty());
		assertTrue(0 == trans.numElements);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(trans.patIndex.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer#removeFrom(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testRemoveFromExBase() {
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		//---
		assertTrue(trans.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.removeFrom((ExBase)null));
		assertFalse(trans.removeFrom(baseStationeryAll));
		assertFalse(trans.removeFrom(baseAppleAll));
		assertFalse(trans.removeFrom(baseOrangeAll));
		assertFalse(trans.removeFrom(baseBananaAll));
		assertFalse(trans.removeFrom(baseMelonAll));
		assertFalse(trans.removeFrom(baseMangoAll));
		assertFalse(trans.removeFrom(baseFishAll));
		assertFalse(trans.removeFrom(baseMeatAll));
		assertFalse(trans.removeFrom(baseBallPenYen));
		assertFalse(trans.removeFrom(baseMechaPenYen));
		assertFalse(trans.removeFrom(basePencilYen));
		assertFalse(trans.removeFrom(basePrefectureAll));
		assertFalse(trans.removeFrom(baseRochin1All));
		assertFalse(trans.removeFrom(baseRochin2All));
		//---
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertFalse(trans.removeFrom((ExBase)null));
		assertFalse(trans.removeFrom(baseStationeryAll));
		assertTrue(trans.removeFrom(baseAppleAll));
		assertTrue(trans.removeFrom(baseOrangeAll));
		assertTrue(trans.removeFrom(baseBananaAll));
		assertTrue(trans.removeFrom(baseMelonAll));
		assertTrue(trans.removeFrom(baseMangoAll));
		assertTrue(trans.removeFrom(baseFishAll));
		assertTrue(trans.removeFrom(baseMeatAll));
		assertTrue(trans.removeFrom(baseBallPenYen));
		assertTrue(trans.removeFrom(baseMechaPenYen));
		assertTrue(trans.removeFrom(basePencilYen));
		assertTrue(trans.removeFrom(basePrefectureAll));
		assertTrue(trans.removeFrom(baseRochin1All));
		assertTrue(trans.removeFrom(baseRochin2All));
		assertTrue(trans.isEmpty());
		assertTrue(0 == trans.numElements);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(null == trans.patIndex);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		//---
		assertTrue(trans.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertFalse(trans.removeFrom((ExBase)null));
		assertFalse(trans.removeFrom(baseStationeryAll));
		assertFalse(trans.removeFrom(baseAppleAll));
		assertFalse(trans.removeFrom(baseOrangeAll));
		assertFalse(trans.removeFrom(baseBananaAll));
		assertFalse(trans.removeFrom(baseMelonAll));
		assertFalse(trans.removeFrom(baseMangoAll));
		assertFalse(trans.removeFrom(baseFishAll));
		assertFalse(trans.removeFrom(baseMeatAll));
		assertFalse(trans.removeFrom(baseBallPenYen));
		assertFalse(trans.removeFrom(baseMechaPenYen));
		assertFalse(trans.removeFrom(basePencilYen));
		assertFalse(trans.removeFrom(basePrefectureAll));
		assertFalse(trans.removeFrom(baseRochin1All));
		assertFalse(trans.removeFrom(baseRochin2All));
		//---
		putAllTransfer(trans, data1);
		assertEqualTransfer(trans, data1);
		assertFalse(trans.removeFrom((ExBase)null));
		assertFalse(trans.removeFrom(baseStationeryAll));
		assertTrue(trans.removeFrom(baseAppleAll));
		assertTrue(trans.removeFrom(baseOrangeAll));
		assertTrue(trans.removeFrom(baseBananaAll));
		assertTrue(trans.removeFrom(baseMelonAll));
		assertTrue(trans.removeFrom(baseMangoAll));
		assertTrue(trans.removeFrom(baseFishAll));
		assertTrue(trans.removeFrom(baseMeatAll));
		assertTrue(trans.removeFrom(baseBallPenYen));
		assertTrue(trans.removeFrom(baseMechaPenYen));
		assertTrue(trans.removeFrom(basePencilYen));
		assertTrue(trans.removeFrom(basePrefectureAll));
		assertTrue(trans.removeFrom(baseRochin1All));
		assertTrue(trans.removeFrom(baseRochin2All));
		assertTrue(trans.isEmpty());
		assertTrue(0 == trans.numElements);
		assertTrue(trans.map.isEmpty());
		assertTrue(trans.backmap.isEmpty());
		assertTrue(trans.patIndex.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer#matchesFrom(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testMatchesFrom() {
		HashMap<ExBase,ExBasePattern> mmap = new HashMap<ExBase,ExBasePattern>();
		mmap.put(nAppleYen, patAppleAll);
		mmap.put(nOrangeYen, patOrangeAll);
		mmap.put(nBananaYen, patBananaAll);
		mmap.put(nMelonYen, patMelonAll);
		mmap.put(nMangoYen, patMangoAll);
		mmap.put(nFishYen, patFishAll);
		mmap.put(nMeatYen, patMeatAll);
		mmap.put(nBallPenYen, patBallPenYen);
		mmap.put(nMechaPenYen, patMechaPenYen);
		mmap.put(nPencilYen, patPencilYen);
		mmap.put(nPrefChibaNum, patPrefectureAll);
		mmap.put(nPrefKanagawaNum, patPrefectureAll);
		mmap.put(nRochin1Yen, patRochin1All);
		mmap.put(nRochin2Yen, patRochin2All);
		mmap.put(nFruitNum, null);
		mmap.put(nEraserNum, null);
		mmap.put(nPrefTokyoNum, null);
		mmap.put(nRochin3Yen, null);
		mmap.put(nNaibuHoryuYen, null);
		mmap.put(hAppleYen, patAppleAll);
		mmap.put(hOrangeYen, patOrangeAll);
		mmap.put(hBananaYen, patBananaAll);
		mmap.put(hMelonYen, patMelonAll);
		mmap.put(hMangoYen, patMangoAll);
		mmap.put(hFishYen, patFishAll);
		mmap.put(hMeatYen, patMeatAll);
		mmap.put(hBallPenYen, patBallPenYen);
		mmap.put(hMechaPenYen, patMechaPenYen);
		mmap.put(hPencilYen, patPencilYen);
		mmap.put(hPrefChibaNum, patPrefectureAll);
		mmap.put(hPrefKanagawaNum, patPrefectureAll);
		mmap.put(hRochin1Yen, patRochin1All);
		mmap.put(hRochin2Yen, patRochin2All);
		mmap.put(hFruitNum, null);
		mmap.put(hEraserNum, null);
		mmap.put(hPrefTokyoNum, null);
		mmap.put(hRochin3Yen, null);
		mmap.put(hNaibuHoryuYen, null);
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (Map.Entry<ExBase,ExBasePattern> entry : mmap.entrySet()) {
			assertNull(trans.matchesFrom(entry.getKey()));
		}
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (Map.Entry<ExBase,ExBasePattern> entry : mmap.entrySet()) {
			assertEquals(entry.getValue(), trans.matchesFrom(entry.getKey()));
		}
		//---
		trans.clear();
		assertTrue(trans.isEmpty());
		for (Map.Entry<ExBase,ExBasePattern> entry : mmap.entrySet()) {
			assertNull(trans.matchesFrom(entry.getKey()));
		}
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (Map.Entry<ExBase,ExBasePattern> entry : mmap.entrySet()) {
			assertNull(trans.matchesFrom(entry.getKey()));
		}
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (Map.Entry<ExBase,ExBasePattern> entry : mmap.entrySet()) {
			assertEquals(entry.getValue(), trans.matchesFrom(entry.getKey()));
		}
		//---
		trans.clear();
		assertTrue(trans.isEmpty());
		for (Map.Entry<ExBase,ExBasePattern> entry : mmap.entrySet()) {
			assertNull(trans.matchesFrom(entry.getKey()));
		}
	}

	/**
	 * {@link exalge2.ExTransfer#transform(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testTransformExBase() {
		HashMap<ExBase,ExBaseSet> ans = new HashMap<ExBase,ExBaseSet>();
		ans.put(hAppleYen, new ExBaseSet(Arrays.asList(hFruitNum)));
		ans.put(hMelonYen, new ExBaseSet(Arrays.asList(hExpFruitNum)));
		ans.put(hFishYen, new ExBaseSet(Arrays.asList(hTunaYen, hBonitoYen, hSauryYen, hSardineYen)));
		ans.put(nMeatYen, new ExBaseSet(Arrays.asList(nBeefYen, nPorkYen, nChickenYen)));
		ans.put(nPrefChibaNum, new ExBaseSet(Arrays.asList(nJapanNum)));
		ans.put(hPrefKanagawaNum, new ExBaseSet(Arrays.asList(hJapanNum)));
		ans.put(hFruitNum, new ExBaseSet(Arrays.asList(hFruitNum)));
		ans.put(hPorkYen, new ExBaseSet(Arrays.asList(hPorkYen)));
		ans.put(nStationeryNum, new ExBaseSet(Arrays.asList(nStationeryNum)));
		ans.put(nPrefTokyoNum, new ExBaseSet(Arrays.asList(nPrefTokyoNum)));
		ans.put(hRochin1Yen, new ExBaseSet(Arrays.asList(nNaibuHoryuYen)));
		ans.put(nRochin2Yen, new ExBaseSet(Arrays.asList(hNaibuHoryuYen)));
		ans.put(nNaibuHoryuYen, new ExBaseSet(Arrays.asList(nNaibuHoryuYen)));
		
		boolean coughtException;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (ExBase base : ans.keySet()) {
			ExBaseSet ret = trans.transform(base);
			assertTrue(1 == ret.size());
			assertTrue(ret.contains(base));
		}
		try {
			trans.transform((ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (Map.Entry<ExBase, ExBaseSet> entry : ans.entrySet()) {
			ExBaseSet ret = trans.transform(entry.getKey());
			assertEquals(entry.getValue(), ret);
		}
		try {
			trans.transform((ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		trans.clear();
		assertTrue(trans.isEmpty());
		for (ExBase base : ans.keySet()) {
			ExBaseSet ret = trans.transform(base);
			assertTrue(1 == ret.size());
			assertTrue(ret.contains(base));
		}
		try {
			trans.transform((ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (ExBase base : ans.keySet()) {
			ExBaseSet ret = trans.transform(base);
			assertTrue(1 == ret.size());
			assertTrue(ret.contains(base));
		}
		try {
			trans.transform((ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (Map.Entry<ExBase, ExBaseSet> entry : ans.entrySet()) {
			ExBaseSet ret = trans.transform(entry.getKey());
			assertEquals(entry.getValue(), ret);
		}
		try {
			trans.transform((ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		trans.clear();
		assertTrue(trans.isEmpty());
		for (ExBase base : ans.keySet()) {
			ExBaseSet ret = trans.transform(base);
			assertTrue(1 == ret.size());
			assertTrue(ret.contains(base));
		}
		try {
			trans.transform((ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
	}

	/**
	 * {@link exalge2.ExTransfer#transform(exalge2.ExBaseSet)} のためのテスト・メソッド。
	 */
	public void testTransformExBaseSet() {
		ExBaseSet src = new ExBaseSet(Arrays.asList(
				hAppleYen,hMelonYen,hFishYen,nMeatYen,
				nPrefChibaNum,hPrefKanagawaNum,hFruitNum,
				hPorkYen,nStationeryNum,nPrefTokyoNum,
				hRochin1Yen, nRochin2Yen, nHoryuYen
		));
		ExBaseSet ans = new ExBaseSet(Arrays.asList(
				hFruitNum,
				hExpFruitNum,
				hTunaYen, hBonitoYen, hSauryYen, hSardineYen,
				nBeefYen, nPorkYen, nChickenYen,
				nJapanNum,
				hJapanNum,
				hFruitNum,
				hPorkYen,
				nStationeryNum,
				nPrefTokyoNum,
				hNaibuHoryuYen, nNaibuHoryuYen, nHoryuYen
		));
		
		boolean coughtException;
		ExBaseSet ret;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		ret = trans.transform(src);
		assertEquals(src, ret);
		try {
			trans.transform((ExBaseSet)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		ret = trans.transform(src);
		assertEquals(ans, ret);
		try {
			trans.transform((ExBaseSet)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		trans.clear();
		assertTrue(trans.isEmpty());
		ret = trans.transform(src);
		assertEquals(src, ret);
		try {
			trans.transform((ExBaseSet)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		ret = trans.transform(src);
		assertEquals(src, ret);
		try {
			trans.transform((ExBaseSet)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		ret = trans.transform(src);
		assertEquals(ans, ret);
		try {
			trans.transform((ExBaseSet)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		trans.clear();
		assertTrue(trans.isEmpty());
		ret = trans.transform(src);
		assertEquals(src, ret);
		try {
			trans.transform((ExBaseSet)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
	}

	/**
	 * {@link exalge2.ExTransfer#transfer(exalge2.ExBase, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testTransferExBaseBigDecimal() {
		HashMap<Exalge,Exalge> testdata = new HashMap<Exalge,Exalge>();
		testdata.put(new Exalge(nFruitNum, new BigDecimal(10+20+30)), null);
		testdata.put(new Exalge(nExpFruitNum, new BigDecimal(40+50)), null);
		testdata.put(new Exalge(hFisheryYen, new BigDecimal(50+30+10+10)), null);
		testdata.put(new Exalge(nMuttonYen, new BigDecimal(20)), null);
		testdata.put(new Exalge(nBallPenNum, new BigDecimal(200*5)), null);
		testdata.put(new Exalge(hMechaPenNum, new BigDecimal(250*4)), null);
		testdata.put(new Exalge(nEraserNum, new BigDecimal(50*2)), null);
		testdata.put(new Exalge(hPrefTokyoNum, new BigDecimal(100)), null);
		testdata.put(new Exalge(nAppleYen, new BigDecimal(10)), new Exalge(nFruitNum, new BigDecimal(10)));
		testdata.put(new Exalge(hMelonYen, new BigDecimal(40)), new Exalge(hExpFruitNum, new BigDecimal(40)));
		testdata.put(new Exalge(nFishYen, new BigDecimal(50+30+10+10)), new Exalge(new Object[]{
				new ExBase(nTunaYen), new BigDecimal(50),
				new ExBase(nBonitoYen), new BigDecimal(30),
				new ExBase(nSauryYen), new BigDecimal(10),
				new ExBase(nSardineYen), new BigDecimal(10),
		}));
		testdata.put(new Exalge(hMeatYen, new BigDecimal(20)), new Exalge(new Object[]{
				new ExBase(hBeefYen), new BigDecimal(40),
				new ExBase(hPorkYen), new BigDecimal(20),
				new ExBase(hChickenYen), new BigDecimal(20),
		}));
		testdata.put(new Exalge(nBallPenYen, new BigDecimal(200*5)), new Exalge(nBallPenNum, new BigDecimal(5)));
		testdata.put(new Exalge(hPrefChibaNum, new BigDecimal(111)), new Exalge(hJapanNum, new BigDecimal(111)));
		testdata.put(new Exalge(nPrefSaitamaNum, new BigDecimal(222)), new Exalge(nJapanNum, new BigDecimal(222)));
		testdata.put(new Exalge(hRochin1Yen, new BigDecimal(123)), new Exalge(nNaibuHoryuYen, new BigDecimal(123)));
		testdata.put(new Exalge(nRochin2Yen, new BigDecimal(456)), new Exalge(hNaibuHoryuYen, new BigDecimal(456)));
		
		boolean coughtException;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (Exalge alge : testdata.keySet()) {
			Exalge ret = trans.transfer(alge.getOneBase(), alge.get(alge.getOneBase()));
			assertNull(ret);
		}
		assertNull(trans.transfer(nVegetablesYen, int5));
		try {
			trans.transfer(nAppleYen, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, new BigDecimal(10));
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (Map.Entry<Exalge, Exalge> entry : testdata.entrySet()) {
			ExBase base = entry.getKey().getOneBase();
			BigDecimal val = entry.getKey().get(base);
			Exalge ret = trans.transfer(base, val);
			assertEquals(entry.getValue(), ret);
		}
		try {
			trans.transfer(nAppleYen, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, new BigDecimal(10));
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(nVegetablesYen, int5);
			coughtException = false;
		} catch (ArithmeticException ex) {
			coughtException = true;
			printException("ExTransferTest:testTransferExBaseBigDecimal", ex, nVegetablesYen, int5);
		}
		assertTrue(coughtException);
		//---
		trans.clear();
		assertTrue(trans.isEmpty());
		for (Exalge alge : testdata.keySet()) {
			Exalge ret = trans.transfer(alge.getOneBase(), alge.get(alge.getOneBase()));
			assertNull(ret);
		}
		assertNull(trans.transfer(nVegetablesYen, int5));
		try {
			trans.transfer(nAppleYen, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, new BigDecimal(10));
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (Exalge alge : testdata.keySet()) {
			Exalge ret = trans.transfer(alge.getOneBase(), alge.get(alge.getOneBase()));
			assertNull(ret);
		}
		assertNull(trans.transfer(nVegetablesYen, int5));
		try {
			trans.transfer(nAppleYen, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, new BigDecimal(10));
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (Map.Entry<Exalge, Exalge> entry : testdata.entrySet()) {
			ExBase base = entry.getKey().getOneBase();
			BigDecimal val = entry.getKey().get(base);
			Exalge ret = trans.transfer(base, val);
			assertEquals(entry.getValue(), ret);
		}
		try {
			trans.transfer(nAppleYen, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, new BigDecimal(10));
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(nVegetablesYen, int5);
			coughtException = false;
		} catch (ArithmeticException ex) {
			coughtException = true;
			printException("ExTransferTest:testTransferExBaseBigDecimal", ex, nVegetablesYen, int5);
		}
		assertTrue(coughtException);
		//---
		trans.clear();
		assertTrue(trans.isEmpty());
		for (Exalge alge : testdata.keySet()) {
			Exalge ret = trans.transfer(alge.getOneBase(), alge.get(alge.getOneBase()));
			assertNull(ret);
		}
		assertNull(trans.transfer(nVegetablesYen, int5));
		try {
			trans.transfer(nAppleYen, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, new BigDecimal(10));
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(null, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
	}

	/**
	 * {@link exalge2.ExTransfer#transfer(exalge2.Exalge)} のためのテスト・メソッド。
	 */
	public void testTransferExalge() {
		Exalge data0 = new Exalge(new Object[]{
				nFruitNum, new BigDecimal(10+20+30),
				nExpFruitNum, new BigDecimal(40+50),
				hFisheryYen, new BigDecimal(50+30+10+10),
				nMuttonYen, new BigDecimal(20),
				nBallPenNum, new BigDecimal(200*5),
				hMechaPenNum, new BigDecimal(250*4),
				nEraserNum, new BigDecimal(50*2),
				hPrefTokyoNum, new BigDecimal(100),
				nHoryuYen, new BigDecimal(123),
		});
		Exalge data1 = new Exalge(new Object[]{
				nAppleYen,		new BigDecimal(10),
				nOrangeYen,		new BigDecimal(20),
				nBananaYen,		new BigDecimal(30),
				hMelonYen,		new BigDecimal(40),
				hMangoYen,		new BigDecimal(50),
				hGrapefruitYen,	new BigDecimal(60),
				
				nFishYen, new BigDecimal(50+30+10+10),
				
				hMeatYen, new BigDecimal(20),
				
				nBallPenYen,	new BigDecimal(200*5),
				hMechaPenYen,	new BigDecimal(250*4),
				nPencilYen,		new BigDecimal(80*3),
				hEraserYen,		new BigDecimal(50*2),

				nPrefChibaNum,		new BigDecimal(11),
				nPrefKanagawaNum,	new BigDecimal(22),
				nPrefSaitamaNum,	new BigDecimal(33),
				nPrefTokyoNum,		new BigDecimal(44),
				
				hRochin1Yen,	new BigDecimal(123),
				nRochin2Yen,	new BigDecimal(456),
		});
		Exalge data2 = new Exalge(nVegetablesYen, int5);
		Exalge ans1 = new Exalge(new Object[]{
				nAppleYen,		new BigDecimal(10),
				nOrangeYen,		new BigDecimal(20),
				nBananaYen,		new BigDecimal(30),
				hMelonYen,		new BigDecimal(40),
				hMangoYen,		new BigDecimal(50),
				hGrapefruitYen,	new BigDecimal(60),
				
				nFishYen, new BigDecimal(50+30+10+10),
				
				hMeatYen, new BigDecimal(20),
				
				nBallPenYen,	new BigDecimal(200*5),
				hMechaPenYen,	new BigDecimal(250*4),
				nPencilYen,		new BigDecimal(80*3),
				hEraserYen,		new BigDecimal(50*2),

				nPrefChibaNum,		new BigDecimal(11),
				nPrefKanagawaNum,	new BigDecimal(22),
				nPrefSaitamaNum,	new BigDecimal(33),
				nPrefTokyoNum,		new BigDecimal(44),
				
				hRochin1Yen,	new BigDecimal(123),
				nRochin2Yen,	new BigDecimal(456),

				hAppleYen,		new BigDecimal(10),
				nFruitNum,		new BigDecimal(10),
				hOrangeYen,		new BigDecimal(20),
				nFruitNum,		new BigDecimal(20),
				hBananaYen,		new BigDecimal(30),
				nFruitNum,		new BigDecimal(30),
				nMelonYen,		new BigDecimal(40),
				hExpFruitNum,	new BigDecimal(40),
				nMangoYen,		new BigDecimal(50),
				hExpFruitNum,	new BigDecimal(50),
				
				hFishYen,		new BigDecimal(50+30+10+10),
				nTunaYen,		new BigDecimal(50),
				nBonitoYen,		new BigDecimal(30),
				nSauryYen,		new BigDecimal(10),
				nSardineYen,	new BigDecimal(10),
				
				nMeatYen,		new BigDecimal(20),
				hBeefYen,		new BigDecimal(40),
				hPorkYen,		new BigDecimal(20),
				hChickenYen,	new BigDecimal(20),
				
				hBallPenYen,	new BigDecimal(200*5),
				nBallPenNum,	new BigDecimal(5),
				nMechaPenYen,	new BigDecimal(250*4),
				hMechaPenNum,	new BigDecimal(4),
				hPencilYen,		new BigDecimal(80*3),
				nPencilNum,		new BigDecimal(3),

				hPrefChibaNum,		new BigDecimal(11),
				nJapanNum,			new BigDecimal(11),
				hPrefKanagawaNum,	new BigDecimal(22),
				nJapanNum,			new BigDecimal(22),
				hPrefSaitamaNum,	new BigDecimal(33),
				nJapanNum,			new BigDecimal(33),
				
				nRochin1Yen,	new BigDecimal(123),
				nNaibuHoryuYen,	new BigDecimal(123),
				hRochin2Yen,	new BigDecimal(456),
				hNaibuHoryuYen,	new BigDecimal(456),
		});
		
		boolean coughtException;
		Exalge ret;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		ret = trans.transfer(data0);
		assertEquals(data0, ret);
		ret = trans.transfer(data1);
		assertEquals(data1, ret);
		ret = trans.transfer(data2);
		assertEquals(data2, ret);
		try {
			trans.transfer(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		ret = trans.transfer(data0);
		assertEquals(data0, ret);
		ret = trans.transfer(data1);
		assertEquals(ans1, ret);
		try {
			trans.transfer(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(data2);
			coughtException = false;
		} catch (ArithmeticException ex) {
			coughtException = true;
			printException("ExTransferTest:testTransferExalge", ex, data2);
		}
		assertTrue(coughtException);
		//---
		trans.clear();
		assertTrue(trans.isEmpty());
		ret = trans.transfer(data0);
		assertEquals(data0, ret);
		ret = trans.transfer(data1);
		assertEquals(data1, ret);
		ret = trans.transfer(data2);
		assertEquals(data2, ret);
		try {
			trans.transfer(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		ret = trans.transfer(data0);
		assertEquals(data0, ret);
		ret = trans.transfer(data1);
		assertEquals(data1, ret);
		ret = trans.transfer(data2);
		assertEquals(data2, ret);
		try {
			trans.transfer(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		ret = trans.transfer(data0);
		assertEquals(data0, ret);
		ret = trans.transfer(data1);
		assertEquals(ans1, ret);
		try {
			trans.transfer(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			trans.transfer(data2);
			coughtException = false;
		} catch (ArithmeticException ex) {
			coughtException = true;
			printException("ExTransferTest:testTransferExalge", ex, data2);
		}
		assertTrue(coughtException);
		//---
		trans.clear();
		assertTrue(trans.isEmpty());
		ret = trans.transfer(data0);
		assertEquals(data0, ret);
		ret = trans.transfer(data1);
		assertEquals(data1, ret);
		ret = trans.transfer(data2);
		assertEquals(data2, ret);
		try {
			trans.transfer(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
	}

	/**
	 * {@link exalge2.ExTransfer#lookup(ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testLookupExBasePattern() {
		HashMap<ExBasePattern, ExBasePatternSet> map1 = new HashMap<ExBasePattern,ExBasePatternSet>();
		for (TransferEntry entry : data5) {
			ExBasePattern pf = entry.from();
			ExBasePattern pt = entry.to();
			ExBasePatternSet set = map1.get(pf);
			if (set == null) {
				set = new ExBasePatternSet();
				map1.put(pf, set);
			}
			set.add(pt);
		}
		ExBasePatternSet ans;
		ExBasePatternSet ret;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (ExBasePattern pf : map1.keySet()) {
			ret = trans.lookup(pf);
			assertTrue(ret.isEmpty());
		}
		assertTrue(trans.lookup((ExBasePattern)null).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		assertTrue(trans.map.keySet().equals(map1.keySet()));
		for (ExBasePattern pf : map1.keySet()) {
			ans = map1.get(pf);
			ret = trans.lookup(pf);
			assertEquals(ans, ret);
			ret.clear();
		}
		assertTrue(trans.lookup((ExBasePattern)null).isEmpty());
		assertEqualTransfer(trans, data5);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (ExBasePattern pf : map1.keySet()) {
			ret = trans.lookup(pf);
			assertTrue(ret.isEmpty());
		}
		assertTrue(trans.lookup((ExBasePattern)null).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		assertTrue(trans.map.keySet().equals(map1.keySet()));
		for (ExBasePattern pf : map1.keySet()) {
			ans = map1.get(pf);
			ret = trans.lookup(pf);
			assertEquals(ans, ret);
			ret.clear();
		}
		assertTrue(trans.lookup((ExBasePattern)null).isEmpty());
		assertEqualTransfer(trans, data5);
	}
	
	/**
	 * {@link exalge2.ExTransfer#lookup(ExBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testLookupExBasePatternSet() {
		ExBasePatternSet set0 = new ExBasePatternSet();
		ExBasePatternSet set1 = new ExBasePatternSet(Arrays.asList(
				patFruitNum, patExpFruitNum,
				patTunaAll, patBonitoAll, patSauryAll, patSardineAll,
				patBeefAll, patPorkAll, patChickenAll,
				patCarrotAll, patOnionAll, patPotatoAll,
				patBallPenNum, patMechaPenNum, patPencilNum,
				patJapanAll,
				patNaibuHoryuAll
		));
		ExBasePatternSet set2 = new ExBasePatternSet(Arrays.asList(
				patOrangeAll, patMelonAll, patMangoAll,
				patExpFruitNum, patNaibuHoryuAll,
				patMeatAll, patVegetablesAll,
				patPrefectureAll, patRochin2All
		));
		ExBasePatternSet ans2 = new ExBasePatternSet(Arrays.asList(
				patFruitNum, patExpFruitNum,
				patBeefAll, patPorkAll, patChickenAll,
				patCarrotAll, patOnionAll, patPotatoAll,
				patJapanAll, patNaibuHoryuAll
		));
		ExBasePatternSet ret;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertTrue(trans.lookup((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.lookup(set0).isEmpty());
		assertTrue(trans.lookup(set1).isEmpty());
		assertTrue(trans.lookup(set2).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		assertTrue(trans.lookup((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.lookup(set0).isEmpty());
		assertTrue(trans.lookup(set1).isEmpty());
		ret = trans.lookup(set2);
		assertEquals(ret, ans2);
		ret.clear();
		assertEqualTransfer(trans, data5);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertTrue(trans.lookup((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.lookup(set0).isEmpty());
		assertTrue(trans.lookup(set1).isEmpty());
		assertTrue(trans.lookup(set2).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		assertTrue(trans.lookup((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.lookup(set0).isEmpty());
		assertTrue(trans.lookup(set1).isEmpty());
		ret = trans.lookup(set2);
		assertEquals(ret, ans2);
		ret.clear();
		assertEqualTransfer(trans, data5);
	}
	
	/**
	 * {@link exalge2.ExTransfer#inverseLookup(ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testInverseLookupExBasePattern() {
		HashMap<ExBasePattern, ExBasePatternSet> map1 = new HashMap<ExBasePattern,ExBasePatternSet>();
		for (TransferEntry entry : data5) {
			ExBasePattern pf = entry.from();
			ExBasePattern pt = entry.to();
			ExBasePatternSet set = map1.get(pt);
			if (set == null) {
				set = new ExBasePatternSet();
				map1.put(pt, set);
			}
			set.add(pf);
		}
		ExBasePatternSet ans;
		ExBasePatternSet ret;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (ExBasePattern pt : map1.keySet()) {
			ret = trans.inverseLookup(pt);
			assertTrue(ret.isEmpty());
		}
		assertTrue(trans.inverseLookup((ExBasePattern)null).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (ExBasePattern pt : map1.keySet()) {
			ans = map1.get(pt);
			ret = trans.inverseLookup(pt);
			assertEquals(ans, ret);
			ret.clear();
		}
		assertTrue(trans.inverseLookup((ExBasePattern)null).isEmpty());
		assertEqualTransfer(trans, data5);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (ExBasePattern pt : map1.keySet()) {
			ret = trans.inverseLookup(pt);
			assertTrue(ret.isEmpty());
		}
		assertTrue(trans.inverseLookup((ExBasePattern)null).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (ExBasePattern pt : map1.keySet()) {
			ans = map1.get(pt);
			ret = trans.inverseLookup(pt);
			assertEquals(ans, ret);
			ret.clear();
		}
		assertTrue(trans.inverseLookup((ExBasePattern)null).isEmpty());
		assertEqualTransfer(trans, data5);
	}
	
	/**
	 * {@link exalge2.ExTransfer#inverseLookup(ExBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testInverseLookupExBasePatternSet() {
		ExBasePatternSet set0 = new ExBasePatternSet();
		ExBasePatternSet set1 = new ExBasePatternSet(Arrays.asList(
				patAppleAll, patOrangeAll, patBananaAll,
				patMelonAll, patMangoAll,
				patFishAll,
				patMeatAll,
				patVegetablesAll,
				patBallPenYen, patMechaPenYen, patPencilYen,
				patPrefectureAll, patRochin1All, patRochin2All
		));
		ExBasePatternSet set2 = new ExBasePatternSet(Arrays.asList(
				patExpFruitNum,
				patMeatAll, patVegetablesAll,
				patBonitoAll, patSardineAll,
				patBallPenNum, patMechaPenNum,
				patNaibuHoryuAll
		));
		ExBasePatternSet ans2 = new ExBasePatternSet(Arrays.asList(
				patMelonAll, patMangoAll,
				patFishAll,
				patBallPenYen, patMechaPenYen,
				patRochin1All, patRochin2All
		));
		ExBasePatternSet ret;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertTrue(trans.inverseLookup((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.inverseLookup(set0).isEmpty());
		assertTrue(trans.inverseLookup(set1).isEmpty());
		assertTrue(trans.inverseLookup(set2).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		assertTrue(trans.inverseLookup((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.inverseLookup(set0).isEmpty());
		assertTrue(trans.inverseLookup(set1).isEmpty());
		ret = trans.inverseLookup(set2);
		assertEquals(ret, ans2);
		ret.clear();
		assertEqualTransfer(trans, data5);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertTrue(trans.inverseLookup((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.inverseLookup(set0).isEmpty());
		assertTrue(trans.inverseLookup(set1).isEmpty());
		assertTrue(trans.inverseLookup(set2).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		assertTrue(trans.inverseLookup((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.inverseLookup(set0).isEmpty());
		assertTrue(trans.inverseLookup(set1).isEmpty());
		ret = trans.inverseLookup(set2);
		assertEquals(ret, ans2);
		ret.clear();
		assertEqualTransfer(trans, data5);
	}
	
	/**
	 * {@link exalge2.ExTransfer#projection(ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testProjectionExBasePattern() {
		HashMap<ExBasePattern,Object> map1 = new HashMap<ExBasePattern,Object>();
		map1.put(patAppleAll, makeTransferEntry(new Object[][]{
				{patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
		}));
		map1.put(patOrangeAll, makeTransferEntry(new Object[][]{
				{patOrangeAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
		}));
		map1.put(patBananaAll, makeTransferEntry(new Object[][]{
				{patBananaAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
		}));
		map1.put(patMelonAll, makeTransferEntry(new Object[][]{
				{patMelonAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
		}));
		map1.put(patMangoAll, makeTransferEntry(new Object[][]{
				{patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
		}));
		map1.put(patFishAll, makeTransferEntry(new Object[][]{
				{patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, int5},
				{patFishAll, patBonitoAll, ExTransfer.ATTR_RATIO, int3},
				{patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, int1},
				{patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int1},
		}));
		map1.put(patMeatAll, makeTransferEntry(new Object[][]{
				{patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int2},
				{patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int1},
				{patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int1},
		}));
		map1.put(patVegetablesAll, makeTransferEntry(new Object[][]{
				{patVegetablesAll, patCarrotAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
				{patVegetablesAll, patOnionAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
				{patVegetablesAll, patPotatoAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
		}));
		map1.put(patBallPenYen, makeTransferEntry(new Object[][]{
				{patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005},
		}));
		map1.put(patMechaPenYen, makeTransferEntry(new Object[][]{
				{patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004},
		}));
		map1.put(patPencilYen, makeTransferEntry(new Object[][]{
				{patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f0125},
		}));
		map1.put(patPrefectureAll, makeTransferEntry(new Object[][]{
				{patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, null},
		}));
		map1.put(patRochin1All, makeTransferEntry(new Object[][]{
				{patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
		}));
		map1.put(patRochin2All, makeTransferEntry(new Object[][]{
				{patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
		}));
		map1.put(patFruitNum, null);
		map1.put(patTunaAll, null);
		map1.put(patBeefAll, null);
		map1.put(patJapanAll, null);
		map1.put(patNaibuHoryuAll, null);
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (ExBasePattern pf : map1.keySet()) {
			ExTransfer subtrans = trans.projection(pf);
			assertTrue(subtrans.isEmpty());
		}
		assertTrue(trans.projection((ExBasePattern)null).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (ExBasePattern pf : map1.keySet()) {
			ExTransfer anstrans = new ExTransfer(trans.patIndex!=null);
			TransferEntry[] ansentry = (TransferEntry[])map1.get(pf);
			if (ansentry != null) {
				putAllTransfer(anstrans, ansentry);
				assertEqualTransfer(anstrans, ansentry);
			}
			ExTransfer subtrans = trans.projection(pf);
			assertEquals(subtrans, anstrans);
			subtrans.clear();
		}
		assertTrue(trans.projection((ExBasePattern)null).isEmpty());
		assertEqualTransfer(trans, data5);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (ExBasePattern pf : map1.keySet()) {
			ExTransfer subtrans = trans.projection(pf);
			assertTrue(subtrans.isEmpty());
		}
		assertTrue(trans.projection((ExBasePattern)null).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (ExBasePattern pf : map1.keySet()) {
			ExTransfer anstrans = new ExTransfer(trans.patIndex!=null);
			TransferEntry[] ansentry = (TransferEntry[])map1.get(pf);
			if (ansentry != null) {
				putAllTransfer(anstrans, ansentry);
				assertEqualTransfer(anstrans, ansentry);
			}
			ExTransfer subtrans = trans.projection(pf);
			assertEquals(subtrans, anstrans);
			subtrans.clear();
		}
		assertTrue(trans.projection((ExBasePattern)null).isEmpty());
		assertEqualTransfer(trans, data5);
	}
	
	/**
	 * {@link exalge2.ExTransfer#projection(ExBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testProjectionExBasePatternSet() {
		ExBasePatternSet set0 = new ExBasePatternSet();
		ExBasePatternSet set1 = new ExBasePatternSet(Arrays.asList(
				patFruitNum, patExpFruitNum,
				patTunaAll, patBonitoAll, patSauryAll, patSardineAll,
				patBeefAll, patPorkAll, patChickenAll,
				patCarrotAll, patOnionAll, patPotatoAll,
				patBallPenNum, patMechaPenNum, patPencilNum,
				patJapanAll,
				patNaibuHoryuAll
		));
		ExBasePatternSet set2 = new ExBasePatternSet(Arrays.asList(
				patOrangeAll, patMelonAll, patMangoAll,
				patExpFruitNum, patNaibuHoryuAll,
				patMeatAll, patVegetablesAll,
				patPrefectureAll, patRochin2All
		));
		TransferEntry[] ans2 = makeTransferEntry(new Object[][]{
				{patOrangeAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
				{patMelonAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
				{patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
				{patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int2},
				{patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int1},
				{patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int1},
				{patVegetablesAll, patCarrotAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
				{patVegetablesAll, patOnionAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
				{patVegetablesAll, patPotatoAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
				{patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, null},
				{patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
		});
		ExTransfer ret;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertTrue(trans.projection((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.projection(set0).isEmpty());
		assertTrue(trans.projection(set1).isEmpty());
		assertTrue(trans.projection(set2).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		assertTrue(trans.projection((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.projection(set0).isEmpty());
		assertTrue(trans.projection(set1).isEmpty());
		ret = trans.projection(set2);
		assertEqualTransfer(ret, ans2);
		ret.clear();
		assertEqualTransfer(trans, data5);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertTrue(trans.projection((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.projection(set0).isEmpty());
		assertTrue(trans.projection(set1).isEmpty());
		assertTrue(trans.projection(set2).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		assertTrue(trans.projection((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.projection(set0).isEmpty());
		assertTrue(trans.projection(set1).isEmpty());
		ret = trans.projection(set2);
		assertEqualTransfer(ret, ans2);
		ret.clear();
		assertEqualTransfer(trans, data5);
	}
	
	/**
	 * {@link exalge2.ExTransfer#inverseProjection(ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testInverseProjectionExBasePattern() {
		HashMap<ExBasePattern,Object> map1 = new HashMap<ExBasePattern,Object>();
		map1.put(patFruitNum, makeTransferEntry(new Object[][]{
				{patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
				{patOrangeAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
				{patBananaAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
		}));
		map1.put(patExpFruitNum, makeTransferEntry(new Object[][]{
				{patMelonAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
				{patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
		}));
		map1.put(patTunaAll, makeTransferEntry(new Object[][]{
				{patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, int5},
		}));
		map1.put(patBonitoAll, makeTransferEntry(new Object[][]{
				{patFishAll, patBonitoAll, ExTransfer.ATTR_RATIO, int3},
		}));
		map1.put(patSauryAll, makeTransferEntry(new Object[][]{
				{patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, int1},
		}));
		map1.put(patSardineAll, makeTransferEntry(new Object[][]{
				{patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int1},
		}));
		map1.put(patBeefAll, makeTransferEntry(new Object[][]{
				{patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int2},
		}));
		map1.put(patPorkAll, makeTransferEntry(new Object[][]{
				{patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int1},
		}));
		map1.put(patChickenAll, makeTransferEntry(new Object[][]{
				{patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int1},
		}));
		map1.put(patCarrotAll, makeTransferEntry(new Object[][]{
				{patVegetablesAll, patCarrotAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
		}));
		map1.put(patOnionAll, makeTransferEntry(new Object[][]{
				{patVegetablesAll, patOnionAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
		}));
		map1.put(patPotatoAll, makeTransferEntry(new Object[][]{
				{patVegetablesAll, patPotatoAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
		}));
		map1.put(patBallPenNum, makeTransferEntry(new Object[][]{
				{patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005},
		}));
		map1.put(patMechaPenNum, makeTransferEntry(new Object[][]{
				{patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004},
		}));
		map1.put(patPencilNum, makeTransferEntry(new Object[][]{
				{patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f0125},
		}));
		map1.put(patJapanAll, makeTransferEntry(new Object[][]{
				{patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, null},
		}));
		map1.put(patNaibuHoryuAll, makeTransferEntry(new Object[][]{
				{patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
				{patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
		}));
		map1.put(patBananaAll, null);
		map1.put(patFishAll, null);
		map1.put(patMeatAll, null);
		map1.put(patPrefectureAll, null);
		map1.put(patRochin2All, null);
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		for (ExBasePattern pf : map1.keySet()) {
			ExTransfer subtrans = trans.inverseProjection(pf);
			assertTrue(subtrans.isEmpty());
		}
		assertTrue(trans.inverseProjection((ExBasePattern)null).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (ExBasePattern pf : map1.keySet()) {
			ExTransfer anstrans = new ExTransfer(trans.patIndex!=null);
			TransferEntry[] ansentry = (TransferEntry[])map1.get(pf);
			if (ansentry != null) {
				putAllTransfer(anstrans, ansentry);
				assertEqualTransfer(anstrans, ansentry);
			}
			ExTransfer subtrans = trans.inverseProjection(pf);
			assertEquals(subtrans, anstrans);
			subtrans.clear();
		}
		assertTrue(trans.inverseProjection((ExBasePattern)null).isEmpty());
		assertEqualTransfer(trans, data5);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		for (ExBasePattern pf : map1.keySet()) {
			ExTransfer subtrans = trans.inverseProjection(pf);
			assertTrue(subtrans.isEmpty());
		}
		assertTrue(trans.inverseProjection((ExBasePattern)null).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		for (ExBasePattern pf : map1.keySet()) {
			ExTransfer anstrans = new ExTransfer(trans.patIndex!=null);
			TransferEntry[] ansentry = (TransferEntry[])map1.get(pf);
			if (ansentry != null) {
				putAllTransfer(anstrans, ansentry);
				assertEqualTransfer(anstrans, ansentry);
			}
			ExTransfer subtrans = trans.inverseProjection(pf);
			assertEquals(subtrans, anstrans);
			subtrans.clear();
		}
		assertTrue(trans.inverseProjection((ExBasePattern)null).isEmpty());
		assertEqualTransfer(trans, data5);
	}
	static protected final TransferEntry[] data6 = makeTransferEntry(new Object[][]{
			{patAppleAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patOrangeAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patBananaAll, patFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patMelonAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
			{patFishAll, patTunaAll, ExTransfer.ATTR_RATIO, int5},
			{patFishAll, patBonitoAll, ExTransfer.ATTR_RATIO, int3},
			{patFishAll, patSauryAll, ExTransfer.ATTR_RATIO, int1},
			{patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int1},
			{patMeatAll, patBeefAll, ExTransfer.ATTR_MULTIPLY, int2},
			{patMeatAll, patPorkAll, ExTransfer.ATTR_MULTIPLY, int1},
			{patMeatAll, patChickenAll, ExTransfer.ATTR_MULTIPLY, int1},
			{patVegetablesAll, patCarrotAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
			{patVegetablesAll, patOnionAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
			{patVegetablesAll, patPotatoAll, ExTransfer.ATTR_RATIO, BigDecimal.ZERO},
			{patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005},
			{patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004},
			{patPencilYen, patPencilNum, ExTransfer.ATTR_MULTIPLY, f0125},
			{patPrefectureAll, patJapanAll, ExTransfer.ATTR_AGGRE, null},
			{patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
			{patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
	});
	
	/**
	 * {@link exalge2.ExTransfer#inverseProjection(ExBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testInverseProjectionExBasePatternSet() {
		ExBasePatternSet set0 = new ExBasePatternSet();
		ExBasePatternSet set1 = new ExBasePatternSet(Arrays.asList(
				patAppleAll, patOrangeAll, patBananaAll,
				patMelonAll, patMangoAll,
				patFishAll,
				patMeatAll,
				patVegetablesAll,
				patBallPenYen, patMechaPenYen, patPencilYen,
				patPrefectureAll, patRochin1All, patRochin2All
		));
		ExBasePatternSet set2 = new ExBasePatternSet(Arrays.asList(
				patExpFruitNum,
				patMeatAll, patVegetablesAll,
				patBonitoAll, patSardineAll,
				patBallPenNum, patMechaPenNum,
				patNaibuHoryuAll
		));
		TransferEntry[] ans2 = makeTransferEntry(new Object[][]{
				{patMelonAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
				{patMangoAll, patExpFruitNum, ExTransfer.ATTR_AGGRE, null},
				{patFishAll, patBonitoAll, ExTransfer.ATTR_RATIO, int3},
				{patFishAll, patSardineAll, ExTransfer.ATTR_RATIO, int1},
				{patBallPenYen, patBallPenNum, ExTransfer.ATTR_MULTIPLY, f005},
				{patMechaPenYen, patMechaPenNum, ExTransfer.ATTR_MULTIPLY, f004},
				{patRochin1All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
				{patRochin2All, patNaibuHoryuAll, ExTransfer.ATTR_HAT, null},
		});
		ExTransfer ret;
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		assertTrue(trans.isEmpty());
		assertTrue(trans.inverseProjection((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.inverseProjection(set0).isEmpty());
		assertTrue(trans.inverseProjection(set1).isEmpty());
		assertTrue(trans.inverseProjection(set2).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		assertTrue(trans.inverseProjection((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.inverseProjection(set0).isEmpty());
		assertTrue(trans.inverseProjection(set1).isEmpty());
		ret = trans.inverseProjection(set2);
		assertEqualTransfer(ret, ans2);
		ret.clear();
		assertEqualTransfer(trans, data5);
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		assertTrue(trans.isEmpty());
		assertTrue(trans.inverseProjection((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.inverseProjection(set0).isEmpty());
		assertTrue(trans.inverseProjection(set1).isEmpty());
		assertTrue(trans.inverseProjection(set2).isEmpty());
		//---
		putAllTransfer(trans, data5);
		assertEqualTransfer(trans, data5);
		assertTrue(trans.inverseProjection((ExBasePatternSet)null).isEmpty());
		assertTrue(trans.inverseProjection(set0).isEmpty());
		assertTrue(trans.inverseProjection(set1).isEmpty());
		ret = trans.inverseProjection(set2);
		assertEqualTransfer(ret, ans2);
		ret.clear();
		assertEqualTransfer(trans, data5);
	}
	
	/**
	 * {@link exalge2.ExTransfer#clone()} のためのテスト・メソッド。
	 */
	public void testClone() {
		ExTransfer trans0, trans1, trans2, trans3, trans4, trans5;
		ExTransfer clone0, clone1, clone2, clone3, clone4, clone5;
		
		//----------
		// no index
		//----------
		trans0 = new ExTransfer(false);
		assertTrue(trans0.isEmpty());
		trans1 = makeTransfer(false, data1);
		assertEqualTransfer(trans1, data1);
		trans2 = makeTransfer(false, data2);
		assertEqualTransfer(trans2, data2);
		trans3 = makeTransfer(false, data3);
		assertEqualTransfer(trans3, data3);
		trans4 = makeTransfer(false, data4);
		assertEqualTransfer(trans4, data4);
		trans5 = makeTransfer(false, data5);
		assertEqualTransfer(trans5, data5);
		//---
		clone0 = trans0.clone();
		assertNotSame(clone0, trans0);
		assertEquals(clone0, trans0);
		assertTrue(trans0.isEmpty());
		clone1 = trans1.clone();
		assertNotSame(clone1, trans1);
		assertEquals(clone1, trans1);
		assertEqualTransfer(clone1, data1);
		clone2 = trans2.clone();
		assertNotSame(clone2, trans2);
		assertEquals(clone2, trans2);
		assertEqualTransfer(clone2, data2);
		clone3 = trans3.clone();
		assertNotSame(clone3, trans3);
		assertEquals(clone3, trans3);
		assertEqualTransfer(clone3, data3);
		clone4 = trans4.clone();
		assertNotSame(clone4, trans4);
		assertEquals(clone4, trans4);
		assertEqualTransfer(clone4, data4);
		clone5 = trans5.clone();
		assertNotSame(clone5, trans5);
		assertEquals(clone5, trans5);
		assertEqualTransfer(clone5, data5);
		//---
		clone0.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		clone1.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		clone2.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		clone3.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		clone4.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		clone5.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		//---
		assertNotSame(clone0, trans0);
		assertFalse(clone0.equals(trans0));
		assertTrue(trans0.isEmpty());
		assertEquals(clone0.size()-1, trans0.size());
		assertNotSame(clone1, trans1);
		assertFalse(clone1.equals(trans1));
		assertEqualTransfer(trans1, data1);
		assertEquals(clone1.size()-1, trans1.size());
		assertNotSame(clone2, trans2);
		assertFalse(clone2.equals(trans2));
		assertEqualTransfer(trans2, data2);
		assertEquals(clone2.size()-1, trans2.size());
		assertNotSame(clone3, trans3);
		assertFalse(clone3.equals(trans3));
		assertEqualTransfer(trans3, data3);
		assertEquals(clone3.size()-1, trans3.size());
		assertNotSame(clone4, trans4);
		assertFalse(clone4.equals(trans4));
		assertEqualTransfer(trans4, data4);
		assertEquals(clone4.size()-1, trans4.size());
		assertNotSame(clone5, trans5);
		assertFalse(clone5.equals(trans5));
		assertEqualTransfer(trans5, data5);
		assertEquals(clone5.size()-1, trans5.size());
		
		//----------
		// with index
		//----------
		trans0 = new ExTransfer(true);
		assertTrue(trans0.isEmpty());
		trans1 = makeTransfer(true, data1);
		assertEqualTransfer(trans1, data1);
		trans2 = makeTransfer(true, data2);
		assertEqualTransfer(trans2, data2);
		trans3 = makeTransfer(true, data3);
		assertEqualTransfer(trans3, data3);
		trans4 = makeTransfer(true, data4);
		assertEqualTransfer(trans4, data4);
		trans5 = makeTransfer(true, data5);
		assertEqualTransfer(trans5, data5);
		//---
		clone0 = trans0.clone();
		assertNotSame(clone0, trans0);
		assertEquals(clone0, trans0);
		assertTrue(trans0.isEmpty());
		clone1 = trans1.clone();
		assertNotSame(clone1, trans1);
		assertEquals(clone1, trans1);
		assertEqualTransfer(clone1, data1);
		clone2 = trans2.clone();
		assertNotSame(clone2, trans2);
		assertEquals(clone2, trans2);
		assertEqualTransfer(clone2, data2);
		clone3 = trans3.clone();
		assertNotSame(clone3, trans3);
		assertEquals(clone3, trans3);
		assertEqualTransfer(clone3, data3);
		clone4 = trans4.clone();
		assertNotSame(clone4, trans4);
		assertEquals(clone4, trans4);
		assertEqualTransfer(clone4, data4);
		clone5 = trans5.clone();
		assertNotSame(clone5, trans5);
		assertEquals(clone5, trans5);
		assertEqualTransfer(clone5, data5);
		//---
		clone0.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		clone1.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		clone2.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		clone3.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		clone4.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		clone5.put(patHoryuAll, patRochin3All, ExTransfer.ATTR_MULTIPLY, BigDecimal.ONE);
		//---
		assertNotSame(clone0, trans0);
		assertFalse(clone0.equals(trans0));
		assertTrue(trans0.isEmpty());
		assertEquals(clone0.size()-1, trans0.size());
		assertNotSame(clone1, trans1);
		assertFalse(clone1.equals(trans1));
		assertEqualTransfer(trans1, data1);
		assertEquals(clone1.size()-1, trans1.size());
		assertNotSame(clone2, trans2);
		assertFalse(clone2.equals(trans2));
		assertEqualTransfer(trans2, data2);
		assertEquals(clone2.size()-1, trans2.size());
		assertNotSame(clone3, trans3);
		assertFalse(clone3.equals(trans3));
		assertEqualTransfer(trans3, data3);
		assertEquals(clone3.size()-1, trans3.size());
		assertNotSame(clone4, trans4);
		assertFalse(clone4.equals(trans4));
		assertEqualTransfer(trans4, data4);
		assertEquals(clone4.size()-1, trans4.size());
		assertNotSame(clone5, trans5);
		assertFalse(clone5.equals(trans5));
		assertEqualTransfer(trans5, data5);
		assertEquals(clone5.size()-1, trans5.size());
	}
}
