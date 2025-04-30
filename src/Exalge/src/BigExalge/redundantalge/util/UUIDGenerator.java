/*
 * @(#)UUIDGenerator.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.util;

import java.util.Random;
import java.util.UUID;

/**
 * UUID 生成ユーティリティ。
 * 
 * @version 0.990
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Li Hou(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class UUIDGenerator
{
	/*
	 * 以下のページを参考：
	 * http://www.famkruithof.net/guid-uuid-timebased.html
	 * http://argius.hatenablog.jp/entry/20120607/1339082250
	 */
	
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ノードID (6 バイト) **/
	static protected final byte[]	_nodeid;
	/** プロセス ID **/
	static protected final long		_procid;
	/** 初期化時のエポック時刻からの経過ミリ秒 **/
	static protected final long 	_startMilliTime;
	/** 初期化時のナノ秒 **/
	static protected final long 	_startNanoTime;

	/** 100ナノ秒単位での 1582年10月15日からの経過時間 **/
	static protected long	_lastTimestamp;
	/** 内部制御用クロックID **/
	static protected int	_clockid;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static {
		// first setup
		_startMilliTime = System.currentTimeMillis();
		_startNanoTime  = System.nanoTime();
		_procid         = ProcessUtilities.getCurrentProcessID();
		
		long rseed = (_procid==0L ? _startMilliTime : _procid);
		
		//Random rdm = new Random(_startMilliTime);
		Random rdm = new Random(rseed);
		_clockid = (int)(rdm.nextDouble() * 0x4000);
		if (_clockid >= 0x4000)
			_clockid = 1;
		
		_lastTimestamp = 0L;
		
		_nodeid = new byte[6];
		byte[] addr = NetUtilities.getFirstPhysicalNicHardwareAddr();
		_nodeid[0] = addr[0];
		_nodeid[1] = addr[1];
		_nodeid[2] = addr[2];
		_nodeid[3] = addr[3];
		_nodeid[4] = addr[4];
		_nodeid[5] = addr[5];
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクト初期化時の、ミリ秒で測定した UTC 1970 年 1 月 1 日午前 0 時との差を返す。
	 * @return	初期化時の、ミリ秒で測定した UTC 1970 年 1 月 1 日午前 0 時との差
	 */
	static public long getInitialTimeMillis() {
		return _startMilliTime;
	}

	/**
	 * このオブジェクト初期化時の、システムタイマーの値(ナノ秒単位)を返す。
	 * @return	初期化時の、システムタイマーの値(ナノ秒単位)
	 */
	static public long getInitialTimeNanos() {
		return _startNanoTime;
	}

	/**
	 * 現在時刻を表す100ナノ秒単位の時間を取得する。
	 * この値は、1582年10月15日からの経過時間を表す。
	 * @return	100 ナノ秒単位の経過時間
	 */
	static public long getTimestamp() {
		return _startMilliTime * 10000L + ((System.nanoTime() - _startNanoTime) / 100L) + 122192928000000000L;
	}

	/**
	 * UUID を生成する。
	 * @return	生成された UUID
	 */
	static public UUID genUUID() {
		return bytesToUUID(genTimeUuidBytes(getTimestamp()));
	}

	/**
	 * UUID をバイト配列で生成する。
	 * @return	UUID のバイト配列
	 */
	static public byte[] genBytesUUID() {
		return genTimeUuidBytes(getTimestamp());
	}

	/**
	 * 数値のみの UUID 文字列を生成する。
	 * @return	区切り記号を含まない、16進数のみの UUID を示す文字列
	 */
	static public String genHexUUID() {
		byte[] uuidbytes = genTimeUuidBytes(getTimestamp());
		return String.format("%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X",
				uuidbytes[0], uuidbytes[1], uuidbytes[2], uuidbytes[3],
				uuidbytes[4], uuidbytes[5], uuidbytes[6], uuidbytes[7],
				uuidbytes[8], uuidbytes[9], uuidbytes[10], uuidbytes[11],
				uuidbytes[12], uuidbytes[13], uuidbytes[14], uuidbytes[15]);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 16 バイトの UUID バイト配列から、UUID オブジェクトを生成する。
	 * @param uuidbytes	UUID バイト配列
	 * @return	UUID オブジェクト
	 */
	static protected UUID bytesToUUID(byte[] uuidbytes) {
		long mostSigBits;
		mostSigBits = uuidbytes[0] & 0xFF;
		mostSigBits <<= 8;
		mostSigBits |= uuidbytes[1] & 0xFF;
		mostSigBits <<= 8;
		mostSigBits |= uuidbytes[2] & 0xFF;
		mostSigBits <<= 8;
		mostSigBits |= uuidbytes[3] & 0xFF;
		mostSigBits <<= 8;
		mostSigBits |= uuidbytes[4] & 0xFF;
		mostSigBits <<= 8;
		mostSigBits |= uuidbytes[5] & 0xFF;
		mostSigBits <<= 8;
		mostSigBits |= uuidbytes[6] & 0xFF;
		mostSigBits <<= 8;
		mostSigBits |= uuidbytes[7] & 0xFF;
		
		long leastSigBits;
		leastSigBits = uuidbytes[8] & 0xFF;
		leastSigBits <<= 8;
		leastSigBits |= uuidbytes[9] & 0xFF;
		leastSigBits <<= 8;
		leastSigBits |= uuidbytes[10] & 0xFF;
		leastSigBits <<= 8;
		leastSigBits |= uuidbytes[11] & 0xFF;
		leastSigBits <<= 8;
		leastSigBits |= uuidbytes[12] & 0xFF;
		leastSigBits <<= 8;
		leastSigBits |= uuidbytes[13] & 0xFF;
		leastSigBits <<= 8;
		leastSigBits |= uuidbytes[14] & 0xFF;
		leastSigBits <<= 8;
		leastSigBits |= uuidbytes[15] & 0xFF;
		
		return new UUID(mostSigBits, leastSigBits);
	}

	/**
	 * 時刻ベースの UUID を生成する。
	 * @param timestamp	100ナノ秒単位の経過時間
	 * @return	生成された UUID を示す 16 バイトの配列
	 */
	static protected synchronized byte[] genTimeUuidBytes(final long timestamp) {
		byte[] uuidbytes = new byte[16];
		
		// get Timestamp & clock ID
		final int clockid;
		if (timestamp == _lastTimestamp) {
			// same timestamp, increment clock ID
			clockid = getNextClockID();
		} else {
			// save new timestamp
			_lastTimestamp = timestamp;
			clockid = _clockid;
		}
		
		// set MSB
		//--- time low
		uuidbytes[0] |= timestamp >> 24;
		uuidbytes[1] |= timestamp >> 16;
		uuidbytes[2] |= timestamp >> 8;
		uuidbytes[3] |= timestamp;
		//--- time mid
		uuidbytes[4] |= timestamp >> 40;
		uuidbytes[5] |= timestamp >> 32;
		//--- version
		uuidbytes[6] = 0x10;	// version-1
		//--- time hi
		uuidbytes[6] |= (timestamp >> 56) & 0x0F;
		uuidbytes[7] |= timestamp >> 48;
		
		// set LSB
		//--- clock ID
		uuidbytes[8] = 0;
		uuidbytes[8] |= (clockid >> 8) & 0x3F;
		uuidbytes[9] |= clockid;
		//--- variant
		uuidbytes[8] |= 0x80;
		//--- node
		uuidbytes[10] = _nodeid[0];
		uuidbytes[11] = _nodeid[1];
		uuidbytes[12] = _nodeid[2];
		uuidbytes[13] = _nodeid[3];
		uuidbytes[14] = _nodeid[4];
		uuidbytes[15] = _nodeid[5];
		
		return uuidbytes;
	}

	/**
	 * 現在のクロックID を 1 進めたものを取得する。
	 * 1 進めた値が 0x3FFF より大きい場合は、1 とする。
	 * @return	次のクロックID
	 */
	static protected int getNextClockID() {
		int newID = _clockid + 1;
		if (newID >= 0x4000)
			newID = 1;
		_clockid = newID;
		return newID;
	}
	
	static protected int getCurrentClockID() {
		return _clockid;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
