/*
 * 作成日 2013/01/24
 * 修正日
 */

package models.reposts;

/**
 * 非推奨のメソッドを利用している場合の例外.
 * 構造上、使用しても意味のないメソッドを誤って呼び出していることを<br>
 * 知らせるためのもの。
 *
 * @author H.Kezuka
 */
public class DeprecatedMethodUseException extends IllegalArgumentException {

}
