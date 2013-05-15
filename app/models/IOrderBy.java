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
