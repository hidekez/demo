/**
 * 半角カタカナ変換クラス.
 * ネットから
 * http://www7a.biglobe.ne.jp/~java-master/samples/string/HankakuKatakanaToZenkakuKatakana.html
 * ただし、#mergeChar indexOfの部分でバグ(0<)があったので修正した。
 * またヴを追加し、冗長な名前を簡略化している。
 *
 * @author H.Kezuka
 */

package utils;

public class HanKana {

	private static final char[] HAN_KAKU = { '｡', '｢', '｣', '､', '･',
			'ｦ', 'ｧ', 'ｨ', 'ｩ', 'ｪ', 'ｫ', 'ｬ', 'ｭ', 'ｮ', 'ｯ', 'ｰ', 'ｱ', 'ｲ',
			'ｳ', 'ｴ', 'ｵ', 'ｶ', 'ｷ', 'ｸ', 'ｹ', 'ｺ', 'ｻ', 'ｼ', 'ｽ', 'ｾ', 'ｿ',
			'ﾀ', 'ﾁ', 'ﾂ', 'ﾃ', 'ﾄ', 'ﾅ', 'ﾆ', 'ﾇ', 'ﾈ', 'ﾉ', 'ﾊ', 'ﾋ', 'ﾌ',
			'ﾍ', 'ﾎ', 'ﾏ', 'ﾐ', 'ﾑ', 'ﾒ', 'ﾓ', 'ﾔ', 'ﾕ', 'ﾖ', 'ﾗ', 'ﾘ', 'ﾙ',
			'ﾚ', 'ﾛ', 'ﾜ', 'ﾝ', 'ﾞ', 'ﾟ' };

	private static final char[] ZEN_KAKU = { '。', '「', '」', '、', '・',
			'ヲ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ャ', 'ュ', 'ョ', 'ッ', 'ー', 'ア', 'イ',
			'ウ', 'エ', 'オ', 'カ', 'キ', 'ク', 'ケ', 'コ', 'サ', 'シ', 'ス', 'セ', 'ソ',
			'タ', 'チ', 'ツ', 'テ', 'ト', 'ナ', 'ニ', 'ヌ', 'ネ', 'ノ', 'ハ', 'ヒ', 'フ',
			'ヘ', 'ホ', 'マ', 'ミ', 'ム', 'メ', 'モ', 'ヤ', 'ユ', 'ヨ', 'ラ', 'リ', 'ル',
			'レ', 'ロ', 'ワ', 'ン', '゛', '゜' };

	private static final char HAN_FIRST_CHAR = HAN_KAKU[0];

	private static final char HAN_LAST_CHAR = HAN_KAKU[HAN_KAKU.length - 1];

	/**
	 * 半角カタカナから全角カタカナへ変換します。
	 *
	 * @param c
	 *            変換前の文字
	 * @return 変換後の文字
	 */
	public static char toZenkaku(char c) {
		if (c >= HAN_FIRST_CHAR && c <= HAN_LAST_CHAR) {
			return ZEN_KAKU[c - HAN_FIRST_CHAR];
		}
		else {
			return c;
		}
	}

	/**
	 * 2文字目が濁点・半濁点で、1文字目に加えることができる場合は、合成した文字を返します。
	 * 合成ができないときは、c1を返します。
	 *
	 * @param c1
	 *            変換前の1文字目
	 * @param c2
	 *            変換前の2文字目
	 * @return 変換後の文字
	 */
	public static char mergeChar(char c1, char c2) {
		if (c2 == 'ﾞ') {
			if (0 <= "ｳｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾊﾋﾌﾍﾎ".indexOf(c1)) {
				switch (c1) {
				case 'ｳ':
					return 'ヴ';
				case 'ｶ':
					return 'ガ';
				case 'ｷ':
					return 'ギ';
				case 'ｸ':
					return 'グ';
				case 'ｹ':
					return 'ゲ';
				case 'ｺ':
					return 'ゴ';
				case 'ｻ':
					return 'ザ';
				case 'ｼ':
					return 'ジ';
				case 'ｽ':
					return 'ズ';
				case 'ｾ':
					return 'ゼ';
				case 'ｿ':
					return 'ゾ';
				case 'ﾀ':
					return 'ダ';
				case 'ﾁ':
					return 'ヂ';
				case 'ﾂ':
					return 'ヅ';
				case 'ﾃ':
					return 'デ';
				case 'ﾄ':
					return 'ド';
				case 'ﾊ':
					return 'バ';
				case 'ﾋ':
					return 'ビ';
				case 'ﾌ':
					return 'ブ';
				case 'ﾍ':
					return 'ベ';
				case 'ﾎ':
					return 'ボ';
				}
			}
		}
		else if (c2 == 'ﾟ') {
			if (0 <= "ﾊﾋﾌﾍﾎ".indexOf(c1)) {
				switch (c1) {
				case 'ﾊ':
					return 'パ';
				case 'ﾋ':
					return 'ピ';
				case 'ﾌ':
					return 'プ';
				case 'ﾍ':
					return 'ペ';
				case 'ﾎ':
					return 'ポ';
				}
			}
		}
		return c1;
	}

	/**
	 * 文字列中の半角カタカナを全角カタカナに変換します。
	 *
	 * @param s
	 *            変換前文字列
	 * @return 変換後文字列
	 */
	public static String toZenkaku(String s) {
		if (s.length() == 0) {
			return s;
		}
		else if (s.length() == 1) {
			return toZenkaku(s.charAt(0)) + "";
		}
		else {
			StringBuffer sb = new StringBuffer(s);
			int i = 0;
			for (i = 0; i < sb.length() - 1; i++) {
				char originalChar1 = sb.charAt(i);
				char originalChar2 = sb.charAt(i + 1);
				char margedChar = mergeChar(originalChar1, originalChar2);
				if (margedChar != originalChar1) {
					sb.setCharAt(i, margedChar);
					sb.deleteCharAt(i + 1);
				}
				else {
					char convertedChar = toZenkaku(originalChar1);
					if (convertedChar != originalChar1) {
						sb.setCharAt(i, convertedChar);
					}
				}
			}
			if (i < sb.length()) {
				char originalChar1 = sb.charAt(i);
				char convertedChar = toZenkaku(originalChar1);
				if (convertedChar != originalChar1) {
					sb.setCharAt(i, convertedChar);
				}
			}
			return sb.toString();
		}

	}

	public static void main(String[] args) {
		System.out.println(toZenkaku("Javaﾌﾟﾛｸﾞﾗﾐﾝｸﾞﾗｳﾞﾊﾟ"));
	}

}

/* ************************************************************ */
/*++++++++++++++++++++++++++*+++++++++++++++++++++++++*/
//=============================================*
//-------------------------------------+
