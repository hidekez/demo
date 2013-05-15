/*
 * 作成日 2011/10/17
 * 修正日 2012/02/23 命名ルールの変更による
 * 修正日 2012/04/06 static化
 * 修正日 2012/06/05 コメントの変更（WZアウトライン用記号の削除）
 */

package utils;

//import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.time.DateUtils.parseDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

import constants.OutOfSpecificDateRange;

/**
 * 日付関連のユーティリティクラス.
 *
 * @author H.Kezuka
 */
public final class MyDateUtils {

	public static final int ERROR = -1;
	public static final int TRUNCATION_BASE = 1000;
	// -------------------------------------+
	public static final String FORMAT_YMD = "yyyy-MM-dd";
	public static final String FORMAT_YMDHMS = "yyyy-MM-dd\'T\'HH:mm:ss";
	// -------------------------------------+
	public static final String BIRTH_OF_WWW_YMD_STR = "1990-12-25";
	public static final String BIRTH_OF_WWW_YMDHMS_STR = "1990-12-25T00:00:00";
	public static final String BIRTH_OF_MYAPP_YMD_STR = "2011-09-24";
	public static final String BIRTH_OF_MYAPP_YMDHMS_STR = "2011-09-24T00:00:00";
	// -------------------------------------+
	// DateTimeは不変オブジェクトなので
	public static final DateTime BIRTH_OF_WWW_DATETIME = new DateTime(
			BIRTH_OF_WWW_YMDHMS_STR, DateTimeZone.forID("Europe/London"));
	public static final DateTime BIRTH_OF_MYAPP_DATETIME = new DateTime(
			BIRTH_OF_MYAPP_YMDHMS_STR);
	// -------------------------------------+
	public static final long DATE_MIN = BIRTH_OF_WWW_DATETIME.getMillis();
	public static final int INDEX_MIN = truncateCore(DATE_MIN);

	// Twitterに関する日付定数は
	// {models.items.original.twitter.TwitterConstants}

	/* ************************************************************ */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * privateなコンストラクタ.
	 * インスタンス化禁止
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private MyDateUtils() {}

	/* ************************************************************ */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 現在時刻取得.
	 *
	 * @return 現在時刻のDate
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static Date now() {
		// return new Date(System.currentTimeMillis());
		// return Calendar.getInstance().getTime();
		// return new Date();
		return new DateTime().toDate();
	}

	/* ************************************************************ */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 防御的コピー.
	 *
	 * @param _date
	 *            入力日付
	 * @return 安全なDateインスタンス
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static Date safeCopy(Date _date) {
		// return new Date(_date.getTime());
		return new DateTime(_date.getTime()).toDate();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 防御的コピー・登録時用・nullなし.
	 * nullだった場合には現在日時を入れる
	 *
	 * @param _date
	 *            入力日付
	 * @return 安全なDateインスタンス
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static Date safeCopyNotNull(Date _date) {
		if (_date != null) {
			return safeCopy(_date);
		}
		return now();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 防御的コピー・登録時用・nullあり.
	 *
	 * @param _date
	 *            入力日付
	 * @return 安全なDateインスタンス
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static Date safeCopyOrNull(Date _date) {
		if (_date == null) {
			return null;
		}
		else {
			return MyDateUtils.safeCopy(_date);
		}
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 検査された防御的コピー.
	 *
	 * @param _date
	 *            入力日付
	 * @return 安全なDateインスタンス
	 * @throws OutOfSpecificDateRange
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static
			Date validAndSafeCopy(Date _date)
												throws OutOfSpecificDateRange {
		if (valid(_date) == false) {
			throw new OutOfSpecificDateRange();
		}
		return safeCopy(_date);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 検査された防御的コピー・登録時用.
	 * nullだった場合には現在日時を入れる
	 *
	 * @param _date
	 *            入力日付
	 * @return 安全なDateインスタンス
	 * @throws OutOfSpecificDateRange
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static Date
			validAndSafeCopyNotNull(Date _date)
												throws OutOfSpecificDateRange {
		if (valid(_date) == false) {
			throw new OutOfSpecificDateRange();
		}
		return safeCopyNotNull(_date);
	}

	/* ************************************************************ */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 日付のバリデーション.
	 *
	 * @param _date
	 *            入力日付
	 * @return 有効範囲内かどうか
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static boolean valid(Date _date) {
		if (_date.getTime() < DATE_MIN
				|| now().getTime() < _date.getTime()) {
			return false;
		}
		return true;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 日付インデックス値のバリデーション.
	 *
	 * @param _index
	 *            入力日付インデックス値
	 * @return 有効範囲内かどうか
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static boolean valid(int _index) {
		if (_index < INDEX_MIN || truncateNowIndex() < _index) {
			return false;
		}
		return true;
	}

	/* ************************************************************ */

//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * Dateインデックス用.
//	 *
//	 * @param _now
//	 *            現在の日付
//	 * @return 日付インデックス値(int) 引数が不正な場合は INDEX_MIN。
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	@Deprecated
//	public static int get(Date _now) {
//		if (_now == null) {
//			return ERROR;
//		}
//		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_DATETIME);
//		Date base = new Date();
//		try {
//			base = sdf.parse(BEGINNING_YMDT);
//		}
//		catch (ParseException e) {}
//		int def = (int) ((_now.getTime() - base.getTime()) / 1000);
//		return (INDEX_MIN < def) ? def : INDEX_MIN;
//	}
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * DATE_YMDインデックス用.
//	 *
//	 * @param _now
//	 *            現在の日付
//	 * @return 日付YMD型インデックス値(int)
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	@Deprecated
//	public static int getYMD(Date _now) {
//		if (_now != null) {
//			SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_YMD);
//			Date now = Tools.getSafetyDate(_now);// 防御的コピー
//			try {
//				return (int) Integer.parseInt(sdf.format(now));
//			}
//			catch (Exception e) {}
//		}
//		return ERROR;// _now != null、もしくはエラー時
//	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * Dateからint値作成.
	 * DateのgetTime値(long)を切り落としてintに変換
	 *
	 * @param _date
	 *            現在の日付
	 * @return 日付インデックス値(int)
	 *         引数が不正な場合は ERROR
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static int truncate(Date _date)
											throws OutOfSpecificDateRange,
											NullPointerException {
		if (_date == null) {
			throw new NullPointerException();
		}

		int index = truncateCore(_date.getTime());

		if (valid(index) == false) {
			throw new OutOfSpecificDateRange();
		}
		return index;
	}

	/**
	 * @param _date
	 * @return 日付インデックス値(int)
	 */
	static int truncateCore(long _milliTime) {
		return (int) (_milliTime / TRUNCATION_BASE);
	}

	/**
	 * @return 日付インデックス値(int)
	 */
	static int truncateNowIndex() {
		return (int) (now().getTime() / TRUNCATION_BASE);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * int値からDate作成.
	 * 切り落としたintからDateに変換する処理
	 *
	 * @param _index
	 *            現在の日付インデックス値
	 * @return 日付
	 *         引数が不正な場合は null
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static
			Date toDateWithTruncated(int _index)
												throws OutOfSpecificDateRange {
		if (valid(_index) == false) {
			throw new OutOfSpecificDateRange();
		}
		return new DateTime((long) _index * TRUNCATION_BASE).toDate();
	}

	/* ************************************************************ */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 日付変換.
	 * JodaTimeの薄いラッパー<br>
	 *
	 * @param _str
	 *            日付文字列
	 * @return 変換された日付データ。
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static Date toDate(String _str) {
		return new DateTime(_str).toDate();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 日付変換コア.
	 *
	 * @param _str
	 *            日付文字列
	 * @return 変換された日付データ。
	 * @throws ParseException
	 *             , NullPointerException
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static Date parseCore(String _str)
												throws ParseException,
												NullPointerException,
												IllegalArgumentException {
		// return SDF_DT.parse(_str);
		// return DateUtils.parseDate(_str, ALL_PATTERNS);
		return toDate(_str);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 検査されない日付変換.
	 * 例外を握りつぶしているので、例外が発生し得る場合の利用には注意すること。<br>
	 *
	 * @param _str
	 *            日付文字列
	 * @return 変換された日付データ、もしくはnull。
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static Date noCheckedParse(String _str) {
		Date date = null;
		try {
			// date = SDF_DT.parse(_str);
			date = parseCore(_str);
		}
		catch (Exception e) {
			// e.printStackTrace();
			// 例外は意図的に無視している。
		}
		return date;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 検査された日付変換.
	 * パターンをタイム入りにした場合、入力情報にタイム部分(00:00:00.0)がないと<br>
	 * ParseException となるので注意。
	 *
	 * @param _str
	 *            日付文字列
	 * @return 変換された日付データ、もしくは現在の日付データ。
	 * @throws ParseException
	 * @throws OutOfSpecificDateRange
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static Date parseAndValid(String _str)
													throws ParseException,
													IllegalArgumentException,
													OutOfSpecificDateRange {
		// Date date = SDF_DT.parse(_str);
		Date date = parseCore(_str);
		if (valid(date) == false) {
			throw new OutOfSpecificDateRange();
		}
		return date;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 日付変換（nullの場合あり）.
	 * 例外を握りつぶしているので、例外が発生し得る場合の利用には注意すること。<br>
	 * {MyDateUtils#noCheckedParse}とは、validが間に実行されている点で異なるのに注意。
	 *
	 * @see MyDateUtils#noCheckedParse(String)
	 *
	 * @param _str
	 *            日付文字列
	 * @return 変換された日付データ、もしくはnull。
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static Date parseOrNull(String _str) {
		try {
			return parseAndValid(_str);
		}
		catch (Exception e) {
			// e.printStackTrace();
			// 例外は意図的に無視している。
		}
		return null;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 日付変換（newの場合あり）.
	 * 例外を握りつぶしているので、例外が発生し得る場合の利用には注意すること。<br>
	 *
	 * @param _str
	 *            日付文字列
	 * @return 変換された日付データ、もしくは現在の日付データ。
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static Date parseOrNew(String _str) {
		try {
			return parseAndValid(_str);
		}
		catch (Exception e) {
			// e.printStackTrace();
			// 例外は意図的に無視している。
		}
		return new Date(System.currentTimeMillis());
	}

	/* ************************************************************ */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * メイン（確認用）
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static void main(String args[]) {
		System.out.println("Now(System)__:" + System.currentTimeMillis());
		System.out.println("Now(Date)____:" + new Date());
		System.out.println("Now(Calendar):" + Calendar.getInstance());
		System.out.println("Now(DateTime):" + now());
		// -------------------------------------+
		System.out.println("ERROR:" + ERROR);
		System.out.println("TRUNCATION_BASE:" + TRUNCATION_BASE);
		// -------------------------------------+
		System.out.println("DATE_MIN_:" + DATE_MIN);
		System.out.println("INDEX_MIN:" + INDEX_MIN);
		// -------------------------------------+
		System.out.println("BIRTH_OF_WWW__:" + BIRTH_OF_WWW_DATETIME);
//		System.out.println("BIRTH_OF_TWEET:" + BIRTH_OF_TWEET_DATETIME);
//		System.out.println("LIMIT_OF_TWEET:" + LIMIT_OF_TWEET_DATETIME);
		System.out.println("BIRTH_OF_MYAPP:" + BIRTH_OF_MYAPP_DATETIME);
		String str = "2011-10-01T12:34:56";
		System.out.println(str);
		System.out.println(noCheckedParse(str));
		// -------------------------------------+
		System.out.println(BIRTH_OF_WWW_DATETIME);
		System.out.println(BIRTH_OF_WWW_DATETIME.toString(ISODateTimeFormat
				.date()));
		System.out.println(BIRTH_OF_WWW_DATETIME.toString("yyyy-MM-dd"));
		System.out.println(BIRTH_OF_WWW_DATETIME.toDateTimeISO().toString());
		// -------------------------------------+
		// ↓この書式ではNG
//		System.out.println(toDate("2013-01-12 00:00:00 GMT+09:00"));
//		System.out.println(toDate("2010-09-24 00:00:00 GMT+09:00"));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 防御的コピー
	 *
	 * @see safeCopy
	 *
	 * @param _date
	 *            入力日付
	 * @return 安全なDateインスタンス
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Deprecated
	public static Date getSafetyDate(Date _date) {
		return new Date(_date.getTime());
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 防御的コピー・登録時用
	 * nullだった場合には現在日時を入れる
	 *
	 * @see safeCopyNotNull
	 *
	 * @param _date
	 *            入力日付
	 * @return 安全なDateインスタンス
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Deprecated
	public static Date getSafetyEntryDate(Date _date) {
		if (_date == null) {
			return new Date(System.currentTimeMillis());
		}
		return new Date(_date.getTime());
	}
}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
//public static final int NETLIMIT_YEAR = 1990;
//public static final int BEGINNING_YEAR = 2005;
//public static final int BEGINNING_MONTH = 1;
//public static final int BEGINNING_DAY = 1;
//public static final int BEGINNING_HOUR = 0;
//public static final int BEGINNING_MINUTE = 0;
//public static final int BEGINNING_SECOND = 0;
// -------------------------------------+
//public static final String BEGINNING_YMD = "2005-01-01";
//public static final String BEGINNING_YMDT = "2005-01-01T00:00:00";//Date'T'Time
//public static final String THIS_SERVICE_BEGINNING_YMD = "2011-10-01";// Validation
//public static final String THIS_SERVICE_BEGINNING_YMDT = "2011-10-01T00:00:00";//Date'T'Time
// -------------------------------------+
//public static final String DATE_PATTERN = "yyyy-MM-dd";
//public static final String DATE_PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss.S";
//public static final String DATE_PATTERN_YMD = "yyyyMMdd";
// -------------------------------------+
//public static final String[] ISO_PATTERNS = new String[6];
//public static final List<String> PATTERNS = new ArrayList<>();
//public static final String[] ALL_PATTERNS;
// -------------------------------------+
//public static final SimpleDateFormat SDF_DT = new SimpleDateFormat(
//		DATE_PATTERN);
//public static final SimpleDateFormat SDF_DTTM = new SimpleDateFormat(
//		DATE_PATTERN_DATETIME);
//public static final SimpleDateFormat SDF_YMD = new SimpleDateFormat(
//		DATE_PATTERN_YMD);
//// -------------------------------------+
//private static final Date DATE_OF_BEGINNING_YMD;
//private static final Date DATE_OF_BEGINNING_YMDT;
//private static final Date DATE_OF_THIS_SERVICE_BEGINNING_YMD;
//private static final Date DATE_OF_THIS_SERVICE_BEGINNING_YMDT;

//// yyyy-MM-dd
//ISO_PATTERNS[0] = DateFormatUtils.ISO_DATE_FORMAT.getPattern();
//// yyyy-MM-ddZZ
//ISO_PATTERNS[1] = DateFormatUtils.ISO_DATE_TIME_ZONE_FORMAT
//		.getPattern();
//// yyyy-MM-dd'T'HH:mm:ss
//ISO_PATTERNS[2] = DateFormatUtils.ISO_DATETIME_FORMAT.getPattern();
//// yyyy-MM-dd'T'HH:mm:ssZZ
//ISO_PATTERNS[3] = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT
//		.getPattern();
//ISO_PATTERNS[4] = "yyyy-MM-dd\'T\'HH:mm:ss.S";
//ISO_PATTERNS[5] = "yyyy-MM-dd\'T\'HH:mm:ss.SZZ";
//// -------------------------------------+
//for (String str : ISO_PATTERNS) {
//	PATTERNS.add(str);
//	PATTERNS.add(str.replaceAll("-", "_"));
//	PATTERNS.add(str.replaceAll("-", "/"));
//	PATTERNS.add(str.replaceAll("-", "."));
//	PATTERNS.add(str.replaceAll("-", ","));
//	PATTERNS.add(str.replaceAll("-", " "));
//}
//// ALL_PATTERNS = (String[])(PATTERNS.toArray(new String[0]));
//ALL_PATTERNS = PATTERNS.toArray(new String[0]);
//// -------------------------------------+
//DATE_OF_BEGINNING_YMD = noCheckedParse(BEGINNING_YMD);
//DATE_OF_BEGINNING_YMDT = noCheckedParse(BEGINNING_YMDT);
//DATE_OF_THIS_SERVICE_BEGINNING_YMD = noCheckedParse(THIS_SERVICE_BEGINNING_YMD);
//DATE_OF_THIS_SERVICE_BEGINNING_YMDT = noCheckedParse(THIS_SERVICE_BEGINNING_YMDT);

//INDEX_MIN = truncate(noCheckedParse(BEGINNING_YMD));

//String s = DateFormatUtils.ISO_DATE_FORMAT.getPattern();

///* ************************************************************ */
///*
// * ゲッター
// */
//public static final Date getDateOfBeginningYMD() {
//	return safeCopy(DATE_OF_BEGINNING_YMD);
//}
//
//public static final Date getDateOfBeginningYMDT() {
//	return safeCopy(DATE_OF_BEGINNING_YMDT);
//}
//
//public static final Date getDateOfThisServiceBeginningYMD() {
//	return safeCopy(DATE_OF_THIS_SERVICE_BEGINNING_YMD);
//}
//
//public static final Date getDateOfThisServiceBeginningYMDT() {
//	return safeCopy(DATE_OF_THIS_SERVICE_BEGINNING_YMDT);
//}

//System.out.println("NETLIMIT_YEAR:" + NETLIMIT_YEAR);
//// -------------------------------------+
//System.out.println("BEGINNING_YEAR:" + BEGINNING_YEAR);
//System.out.println("BEGINNING_MONTH:" + BEGINNING_MONTH);
//System.out.println("BEGINNING_DAY:" + BEGINNING_DAY);
//System.out.println("BEGINNING_YMD:" + BEGINNING_YMD);
//System.out.println("BEGINNING_YMDT:" + BEGINNING_YMDT);
//System.out.println("THIS_SERVICE_BEGINNING_YMD:"
//		+ THIS_SERVICE_BEGINNING_YMD);
//System.out.println("THIS_SERVICE_BEGINNING_YMDT:"
//		+ THIS_SERVICE_BEGINNING_YMDT);
//// -------------------------------------+
////System.out.println(DATE_PATTERN);
////System.out.println(DATE_PATTERN_DATETIME);
//System.out.println("DATE_PATTERN_YMD:" + DATE_PATTERN_YMD);

//// -------------------------------------+
//for (String str : ISO_PATTERNS) {
//	System.out.println("ISO_PATTERNS:" + str);
//}
//// -------------------------------------+
//for (String str : PATTERNS) {
//	System.out.println("PATTERNS:" + str);
//}
// -------------------------------------+
//System.out.println("DATE_OF_BEGINNING_YMD:" + DATE_OF_BEGINNING_YMD);
//System.out.println("DATE_OF_BEGINNING_YMDT:" + DATE_OF_BEGINNING_YMDT);
//System.out.println("DATE_OF_THIS_SERVICE_BEGINNING_YMD:"
//		+ DATE_OF_THIS_SERVICE_BEGINNING_YMD);
//System.out.println("DATE_OF_THIS_SERVICE_BEGINNING_YMDT:"
//		+ DATE_OF_THIS_SERVICE_BEGINNING_YMDT);
//System.out.println("DATE_OF_BEGINNING_YMD:" + getDateOfBeginningYMD());
//System.out.println("DATE_OF_BEGINNING_YMDT:" + getDateOfBeginningYMDT());
//System.out.println("DATE_OF_THIS_SERVICE_BEGINNING_YMD:" + getDateOfThisServiceBeginningYMD());
//System.out.println("DATE_OF_THIS_SERVICE_BEGINNING_YMDT:" + getDateOfThisServiceBeginningYMDT());

//System.out.println(THIS_SERVICE_BEGINNING_YMD);
//System.out.println(noCheckedParse(THIS_SERVICE_BEGINNING_YMD));
//System.out.println(THIS_SERVICE_BEGINNING_YMDT);
//System.out.println(noCheckedParse(THIS_SERVICE_BEGINNING_YMDT));
//String str = "2011-10-01T12:34:56";
//System.out.println(str);
//System.out.println(noCheckedParse(str));
