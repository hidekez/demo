/*
 * 作成日 2013/02/06
 * 修正日
 */

package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import models.labels.Category;

import play.Logger;
import play.db.jpa.Model;
import play.test.Fixtures;

import com.google.gson.Gson;

/**
 * Javascriptファイル出力クラス.
 *
 * @author H.Kezuka
 */
public class JSGenerator {

	private static void outputJsonJS(String _filePath, String _content) {

		try {
			File file = new File(_filePath);
			if (file.exists() == false ||
					(file.isFile() && file.canWrite() == false)) {

				file.delete();
				file.createNewFile();
//				System.out.println("Can not write into file.");
//				return;
			}

			PrintWriter pw =
					new PrintWriter(
							new BufferedWriter(
									new FileWriter(file)));

			pw.println(_content);
			pw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	// TODO マジックナンバー部分を定数化して外へ
	private static String createCategoryListString() {
		List<Category> cats = Category.findAll();
		Gson gson = new Gson();
		StringBuilder sb = new StringBuilder();
//		sb.append("(function() {");
//		sb.append("MYMY.data.label.category.preset.json = ");
		sb.append(gson.toJson(cats));
//		sb.append(";");
//		sb.append("})();");
		return sb.toString();
	}

	// TODO マジックナンバー部分を定数化して外へ
	public static void generateCategoryListJS(boolean _useFixture) {

		if(_useFixture){
			Fixtures.deleteDatabase();
			Fixtures.loadModels("test_models_ver20130112.yml");
		}
		final String FULL_PATH = "public/js/data/json";
		final String FILE_NAME = "preset-category-list.json";
		String filePath = FULL_PATH + "/" + FILE_NAME;
		String content = createCategoryListString();

		outputJsonJS(filePath, content);

	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
