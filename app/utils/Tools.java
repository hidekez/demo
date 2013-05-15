/**
 * ユーティリティツール.
 * ちょっとした処理の共有化用クラス
 *
 * @author H.Kezuka
 */

package utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.time.DateUtils;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


import play.Logger;
import constants.WordCharacter;

public final class Tools {

	//Compare Result
	public static final int THIS_IS_FRONT = -1;// This is at front of the other.
	public static final int OTHER_IS_FRONT = 1;// The other is at front of this.
	public static final int NATURAL_ORDER = 0;

	public static final String THIS_IS_BLANK = "This (field) is blank";
	public static final String OTHER_IS_BLANK = "Other (field) is blank";
	public static final String BOTH_ARE_BLANK = "Both (fields) are blank";
	public static final String THIS_IS_NULL = "This (field) is null";
	public static final String OTHER_IS_NULL = "Other (field) is null";
	public static final String BOTH_ARE_NULL = "Both (fields) are null";

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * privateなコンストラクタ インスタンス化禁止
	 */
	private Tools() {}

	/* ************************************************************ */
	/*
	 * 並び替え
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 比較コア
	 * 比較計算で出された、-1～1を超える数値を丸める処理
	 *
	 * @param _diff
	 *            比較時の計算結果
	 * @return 比較対象に対して自分が前方となる場合は-1、
	 *         後方となる場合は1、その他は0。
	 *         this < other : -1 THIS_IS_FRONT (並び){this, other}
	 *         this == other : 0 NATURAL_ORDER
	 *         this > other : 1 OTHER_IS_FRONT (並び){other, this}
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static int compareCore(int _diff) {
		if (_diff < 0) {
			return THIS_IS_FRONT;// -1
		}
		else if (0 < _diff) {
			return OTHER_IS_FRONT;// 1
		}
		else {
			return NATURAL_ORDER;// 0
		}
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * リストをランダムに並び替える
	 * Collections#shuffleを使うべき
	 *
	 * @param <T>
	 * @param list
	 * @return
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Deprecated
	public static <T> List<T> randomOrder(List<T> list) {

		List<T> tmpList = new ArrayList<T>();
		Random random = new Random();

		while (list.size() > 0) {
			int r = random.nextInt(list.size());
			tmpList.add(list.remove(r));
		}

		for (T t : tmpList) {
			list.add(t);
		}

		return list;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * バイト配列を16進数表現で出力する。
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static String dump(String _s) {
		byte[] bytes;
		StringBuilder sb = new StringBuilder("[");
		try {
			bytes = _s.getBytes("UTF-8");
			for (int i = 0; i < bytes.length; i++) {
				sb.append("0x");
				sb.append(String.format("%1$02X", bytes[i]));
				sb.append(" ");
			}
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		sb.append("]");
		return sb.toString();
	}

	/* ************************************************************ */
	/*
	 * 時間
	 */
	/* ************************************************************ */
	// apache.commons.lang3.time.StopWatchを使うことにした
}
/* ************************************************************ */
/*++++++++++++++++++++++++++*+++++++++++++++++++++++++*/
//=============================================*
//-------------------------------------+
