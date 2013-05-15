/* ************************************************************ */
// Kuromoji.java  <Utility>
/* ************************************************************ */
/**
 * 日本語形態素解析（Japanese Morphological Analysis）
 *
 * @author H.Kezuka
 * 作成日 2012/05/03
 * 修正日 2012/06/05 コメントの変更（WZアウトライン用記号の削除）
 */
/* ************************************************************ */
/*++++++++++++++++++++++++++*+++++++++++++++++++++++++*/
//=============================================*
//-------------------------------------+

package utils;

//import java.util.*;
import play.Logger;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

public final class Kuromoji {

	public static void trial(String _arg) {
		if (isBlank(_arg)) {
			throw new IllegalArgumentException();
		}
		Tokenizer tokenizer = Tokenizer.builder().build();
		for (Token token : tokenizer.tokenize(_arg)) {
//			Logger.debug("-------------------------");
//			Logger.debug(token.getSurfaceForm() + "\t" + token.getAllFeatures());
			System.out.println("-------------------------");
			System.out.println(token.getSurfaceForm() + "\t" + token.getAllFeatures());
		}
	}
}
