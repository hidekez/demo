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
