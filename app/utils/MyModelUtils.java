/*
 * 作成日 2012/07/22
 * 修正日 2012/09/14 アイテムコード生成メソッド追加
 */
package utils;

import static utils.Tools.NATURAL_ORDER;
import static utils.Tools.OTHER_IS_FRONT;
import static utils.Tools.THIS_IS_FRONT;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import models.items.ItemType;
import models.labels.categories.Word;

import play.db.jpa.Model;
import java.util.Comparator;

import constants.ServiceType;

import org.apache.commons.codec.binary.Base64;

/**
 * モデルユーティリティクラス
 *
 * @author H.Kezuka
 */
public class MyModelUtils {

//	public static <T extends Comparable<? super T>> void sortById(List<T> _models){
	public static <T extends Model> void sortById(List<T> _models) {
		Collections.sort(_models, new Comparator<T>() {
			@Override
			public int compare(T _t1, T _t2) {
				if (_t1 == null && _t2 == null) {
					return NATURAL_ORDER;
				}
				else if (_t1 == null) {
					return OTHER_IS_FRONT;
				}
				else if (_t2 == null) {
					return THIS_IS_FRONT;
				}
				return _t1.id.compareTo(_t2.id);
			}
		});
	}

	/**
	 * アイテムUID生成
	 *
	 * @param _itemType
	 * @param _itemId
	 * @param _outerId
	 * @return アイテムの文字列コード
	 */
	public static String createRepostElementSerialCode(
			ItemType _itemType,
			long _itemId,
			String _outerId) {
		StringBuilder sb = new StringBuilder();
		sb.append(_itemType.getPrefix());
		sb.append(_itemId);
		sb.append(".");
		sb.append(_outerId);
		return sb.toString();
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
