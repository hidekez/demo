package utils;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.commons.lang3.StringUtils;

import com.ibm.icu.text.Transliterator;

/**
 * 文字列のユーティリティー.
 * @author H.Kezuka
 */
public class MyStringUtils {

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 文字列のnull/空チェック
	 *
	 * @see org.apache.commons.lang3.StringUtils
	 *
	 * @param _str
	 *            検査する文字列
	 * @return nullか空("")であればtrue、その他はfalse
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Deprecated
	public static boolean isNullEmpty(String _str) {
		return _str == null || _str.trim().length() == 0;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 文字列がnull/空でないチェック
	 *
	 * Commons-lang3のStingUtils#isBlank/isEmptyを利用すべき
	 *
	 * @see org.apache.commons.lang3.StringUtils
	 *
	 * @param _str
	 *            検査する文字列
	 * @return nullか空("")であればtrue、その他はfalse
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Deprecated
	public static boolean isNotNullEmpty(String _str) {
		return _str != null && 0 < _str.trim().length();
	}

	/* ************************************************************ */
	/*
	 * 文字判別
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 半角の判定
	 * 半角カナは含まれない
	 *
	 * @param (char)_c
	 * @return 半角ならtrue
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static boolean isHankaku(char _c) {
		return String.valueOf(_c).getBytes().length < 2;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 全角の判定
	 *
	 * @param (char)_c
	 * @return 全角ならtrue
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static boolean isZenkaku(char _c) {
		return !isHankaku(_c);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * ひらがなの判定
	 *
	 * @param (char)_c
	 * @return ひらがなならtrue
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static boolean isHiragana(char _c) {
		return ('\u3040' <= _c && _c <= '\u309F');
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * カタカナの判定
	 *
	 * @param (char)_c
	 * @return カタカナならtrue
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static boolean isKatakana(char _c) {
		return ('\u30A0' <= _c && _c <= '\u30FF');
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 半角カタカナ（半角カナ）の判定
	 *
	 * @param (char)_c
	 * @return 半角カタカナならtrue
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static boolean isHanKatakana(char _c) {
		return ('\uFF61' <= _c && _c <= '\uFF9F');
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 漢字の判定
	 *
	 * @param (char)_c
	 * @return 漢字ならtrue
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static boolean isKanji(char _c) {
		return ('\u4E00' <= _c && _c <= '\u9FCF');
	}

	/* ************************************************************ */
	/*
	 * 特定の文字種の含有・日本語(String)
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * ひらがなを含むか
	 *
	 * @param _str
	 * @return ひらがなを含むか否か
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static boolean hasHiragana(String _str) {
		if (isBlank(_str)) {
			return false;
		}
		StringBuffer sb = new StringBuffer(_str);
		int len = sb.length();
		for (int i = 0; i < len; i++) {
			char c = sb.charAt(i);
			if (isHiragana(c)) {
				return true;
			}
		}
		return false;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * カタカナを含むか
	 *
	 * @param _str
	 * @return カタカナを含むか否か
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static boolean hasKatakana(String _str) {
		if (isBlank(_str)) {
			return false;
		}
		StringBuffer sb = new StringBuffer(_str);
		int len = sb.length();
		for (int i = 0; i < len; i++) {
			char c = sb.charAt(i);
			if (isKatakana(c)) {
				return true;
			}
		}
		return false;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 漢字を含むか
	 *
	 * @param _str
	 * @return 漢字を含むか否か
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static boolean hasKanji(String _str) {
		if (isBlank(_str)) {
			return false;
		}
		StringBuffer sb = new StringBuffer(_str);
		int len = sb.length();
		for (int i = 0; i < len; i++) {
			char c = sb.charAt(i);
			if (isKanji(c)) {
				return true;
			}
		}
		return false;
	}

	/* ************************************************************ */
	/*
	 * 日本語変換(String)
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * ひらがなからカタカナに変換
	 *
	 * @param _str
	 * @return カタカナに変換された文字列
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String hiraToKata(String _str) {
		if (isBlank(_str)) {
			return _str;
		}
		StringBuffer sb = new StringBuffer(_str);
		int len = sb.length();
		for (int i = 0; i < len; i++) {
			char c = sb.charAt(i);
			if ('ぁ' <= c && c <= 'ん') {
				sb.setCharAt(i, (char) (c - 'ぁ' + 'ァ'));
			}
			else if (c == 'ゔ') {
				sb.setCharAt(i, 'ヴ');
			}
			else if (c == '゛') {
				if (0 < i && sb.charAt(i - 1) == 'ウ') {
					sb.setCharAt(i - 1, 'ヴ');
					sb.deleteCharAt(i);
					len--;
					i--;
				}
			}
		}
		return sb.toString();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * カタカナからひらがなに変換
	 *
	 * @param _str
	 * @return ひらがなに変換された文字列
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String kataToHira(String _str) {
		if (isBlank(_str)) {
			return _str;
		}
		StringBuffer sb = new StringBuffer(_str);
		int len = sb.length();
		for (int i = 0; i < len; i++) {
			char c = sb.charAt(i);
			if ('ァ' <= c && c <= 'ン') {
				sb.setCharAt(i, (char) (c - 'ァ' + 'ぁ'));
			}
			else if (c == 'ヵ') {
				sb.setCharAt(i, 'か');
			}
			else if (c == 'ヶ') {
				sb.setCharAt(i, 'け');
			}
			else if (c == 'ヴ') {
				// sb.setCharAt(i, 'う');
				// sb.insert(i + 1, '゛');
				// len++;
				// i++;
				sb.setCharAt(i, 'ゔ');
			}
		}
		return sb.toString();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 半角カタカナから全角カタカナに変換
	 *
	 * @param _str
	 * @return カタカナに変換された文字列
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String hanKataToZenKata(String _str) {
		if (isBlank(_str)) {
			return _str;
		}
		return HanKana.toZenkaku(_str);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * カタカナからローマ字に変換
	 *
	 * @param _str
	 * @return ローマ字に変換された文字列
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String kataToRoman(String _str) {
		if (isBlank(_str)) {
			return _str;
		}
		return Transliterator.getInstance("Katakana-Latin")
				.transliterate(_str);
	}

	public static String replaceBadRoman(String _str, boolean _hasDouble) {
		if (isBlank(_str)) {
			return _str;
		}
		StringBuilder sb = new StringBuilder(_str);
		int len = sb.length();
		for (int i = 0; i < len; i++) {
			char c = sb.charAt(i);
			if (c == '~') {
				sb.deleteCharAt(i);
				i--;
				len--;
			}
			if (c == 'ā') {
				sb.setCharAt(i, 'a');
				if (_hasDouble) {
					sb.insert(i + 1, 'a');
					i++;
					len++;
				}
			}
			else if (c == 'ī') {
				sb.setCharAt(i, 'i');
				if (_hasDouble) {
					sb.insert(i + 1, 'i');
					i++;
					len++;
				}
			}
			else if (c == 'ū') {
				sb.setCharAt(i, 'u');
				if (_hasDouble) {
					sb.insert(i + 1, 'i');
					i++;
					len++;
				}
			}
			else if (c == 'ē') {
				sb.setCharAt(i, 'e');
				if (_hasDouble) {
					sb.insert(i + 1, 'e');
					i++;
					len++;
				}
			}
			else if (c == 'ō') {
				sb.setCharAt(i, 'o');
				if (_hasDouble) {
					sb.insert(i + 1, 'o');
					i++;
					len++;
				}
			}
		}
		return sb.toString();
	}

	/* ************************************************************ */
	/*
	 * 日本語変換(char)
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * ひらがなからカタカナに変換
	 *
	 * @param _c
	 * @return カタカナに変換されたchar
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static char hiraToKata(char _c) {
		if ('ぁ' <= _c && _c <= 'ん') {
			return (char) (_c - 'ぁ' + 'ァ');
		}
		else if (_c == 'ゔ') {
			return 'ヴ';
		}
		return _c;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * カタカナからひらがなに変換
	 *
	 * @param _c
	 * @return ひらがなに変換されたchar
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static char kataToHira(char _c) {
		if ('ァ' <= _c && _c <= 'ン') {
			return (char) (_c - 'ァ' + 'ぁ');
		}
		else if (_c == 'ヵ') {
			return 'か';
		}
		else if (_c == 'ヶ') {
			return 'け';
		}
		else if (_c == 'ヴ') {
			return 'ゔ';
		}
		return _c;
	}

	/* ************************************************************ */
	/*
	 * 全角半角変換(String)
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 文字列の半角化
	 *
	 * @param _str
	 * @return 半角変換された文字列
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String toHalf(String _str) {
		if (isBlank(_str)) {
			return null;
		}
		return Transliterator.getInstance("Fullwidth-Halfwidth")
				.transliterate(_str);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 文字列の全角化
	 *
	 * @param _str
	 * @return 全角変換された文字列
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String toFull(String _str) {
		if (isBlank(_str)) {
			return null;
		}
		return Transliterator.getInstance("Halfwidth-Fullwidth")
				.transliterate(_str);
	}

	/* ************************************************************ */
	/*
	 * 大文字小文字変換(String)
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 文字列の大文字化
	 *
	 * @param _str
	 * @return 大文字変換された文字列
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String toUpper(String _str) {
		return StringUtils.upperCase(_str);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 文字列の小文字化
	 *
	 * @param _str
	 * @return 小文字変換された文字列
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String toLower(String _str) {
		return StringUtils.lowerCase(_str);
	}

	/* ************************************************************ */

	/**
	 * intへのパース
	 *
	 * @param _num
	 * @return 入力文字列を数値にしたもの、もしくは0。
	 */
	public static int parseInt(String _num) {
		if (StringUtils.isNumeric(_num)) {
			return Integer.parseInt(_num);
		}
		return 0;
	}

	/* ************************************************************ */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 文字列のトリミング処理、上限付き
	 *
	 * @param _org
	 * @param _len
	 * @return トリミング処理した文字列
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String trimAsLength(String _org, int _len) {
		if (_org == null || isBlank(_org.trim())) {
			return null;
		}
		_org = _org.trim();
		if (_len < _org.length()) {
			return _org.substring(0, _len);
		}
		return _org;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 汎用文字列クリーナー
	 *
	 * 【矯正ルール】
	 * ・プリンタブルでない文字列（改行コードなど）を削除
	 * ・全角空白は半角空白に
	 * ・前後の空白は削除
	 *
	 * @param _s
	 *            クリーニングする文字列
	 * @return クリーニング後の文字列、もしくはnull
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String stringCleaner(String _s) {
		if (_s == null || isBlank(_s.trim())) {
			return null;
		}
		StringBuilder sb = new StringBuilder(_s.trim());
		int len = sb.length();
		for (int i = 0; i < len; i++) {
			char c = sb.charAt(i);
			if (Character.isISOControl(c)) {
				sb.deleteCharAt(i);
				len--;
				i--;
			}
			else if (c == ' ' || c == '　') {
				if (0 < i) {
					sb.setCharAt(i, ' ');// 半角化
					if (sb.charAt(i - 1) == ' ' || sb.charAt(i - 1) == '　') {
						sb.deleteCharAt(i - 1);
						len--;
						i--;
					}
				}
			}
		}
		return sb.toString().trim();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 汎用文字列クリーナー・上限トリミング付き
	 *
	 * 【矯正ルール】
	 * ・プリンタブルでない文字列（改行コードなど）を削除
	 * ・全角空白は半角空白に
	 * ・前後の空白は削除
	 *
	 * @param _s
	 *            クリーニングする文字列
	 * @param _len
	 *            文字列の上限サイズ
	 * @return クリーニング後の文字列、もしくはnull
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String stringCleanerWithTrim(String _s, int _len) {
		return trimAsLength(stringCleaner(_s), _len);
	}

	/* ************************************************************ */
	public static String trimToNull(String _org) {
		if (_org == null) {
			return null;
		}
		int len;
		do {
			len = _org.length();
			_org = _org.trim();
			if (0 < _org.length()) {
				if ("　".equals(StringUtils.left(_org, 1))) {
					_org = _org.substring(1);
				}
			}
			if (0 < _org.length()) {
				if ("　".equals(StringUtils.right(_org, 1))) {
					_org = _org.substring(0, _org.length() - 1);
				}
			}
		} while (len != _org.length() && 0 < _org.length());
		if (_org.length()==0) {
			return null;
		}
		return _org;
	}
}
