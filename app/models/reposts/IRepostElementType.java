/*
 * 作成日 2013/01/23
 * 修正日
 */

package models.reposts;

/**
 * リポスト対象となる要素タイプEnum用インターフェース.
 *
 * @author H.Kezuka
 */
public interface IRepostElementType {

	String getLowerClassName();

	String getSerialCodeColName() throws NoFittingDataExistException;

	String getSignatureColName() throws NoFittingDataExistException;

	String getDisplayNameColName() throws NoFittingDataExistException;

	String getMergedSerialCodeColName() throws NoFittingDataExistException;

	String getMergedSignatureColName() throws NoFittingDataExistException;

	String getMergedDisplayNameColName() throws NoFittingDataExistException;

}
