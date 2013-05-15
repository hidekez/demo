/*
 * 作成日 2013/01/20
 * 修正日
 */

package models;

/**
 * 並び替え情報Enum用基底インターフェース.
 *
 * @author H.Kezuka
 */
public interface IOrderBy {
	String ASC = " ASC ";
	String DESC = " DESC ";
	String getSql();
}
