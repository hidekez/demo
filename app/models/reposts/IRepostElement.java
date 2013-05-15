/*
 * 作成日 2013/01/24
 * 修正日
 */

package models.reposts;

/**
 * リポスト対象となる要素用インターフェース.
 *
 * @author H.Kezuka
 */
public interface IRepostElement {

	/**
	 * @return 一意な文字列
	 */
	String getSerialCode();

	/**
	 * @return 保証はされない一意的な文字列
	 */
	String getSignature();

	/**
	 * @return 表示文字列
	 */
	String getDisplayName();

}
