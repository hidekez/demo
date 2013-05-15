/*
 * 作成日 2012/04/10
 * 修正日 2012/06/05 コメントの変更（WZアウトライン用記号の削除）
 * 修正日 2013/01/07 リポスト組み込みに合わせた改造への最適化
 */

package models.items;

import java.util.Iterator;
import java.util.List;

import models.TestModels;
import models.items.*;
//import org.junit.Test;<-このやりかた控えた方がいいかもしれない
import org.junit.*;// Test, Assert
import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;
import play.test.*;// BaseTest, Fixture, UnitTest
import play.Logger;
import mytests.UnitTestBase;

/**
 * アイテム共通ID生成用クラス.
 *
 * @author H.Kezuka
 */
public class ItemIdTest extends UnitTestBase {

	ItemId actual;
	ItemId expected;

	TestModels tm = new TestModels();

	@Ignore("確認済み")
	@Test
	public void 実験_テストのテスト() {
		assertTrue(true);
	}

	@Test
	public void 作成_01() {
		actual = new ItemId();
		assertThat(actual, notNullValue());
		assertThat(actual.id, nullValue());
	}

	@Test
	public void 作成_02() {
		actual = new ItemId();
		actual.saved = actual.validateAndSave();
		assertThat(actual.saved, is(true));
		assertThat(actual.id > 0L, is(true));
	}

	@Test
	public void 検索_findById_01() {
		expected = new ItemId();
		expected.save();

		actual = ItemId.findById(expected.id);

		assertThat(actual.id, is(expected.id));
	}

	@Test
	public void 検索_findAll_01() {
		ItemId iid_1 = new ItemId();
		iid_1.save();
		ItemId iid_2 = new ItemId();
		iid_2.save();
		ItemId iid_3 = new ItemId();
		iid_3.save();

		List<ItemId> list = ItemId.findAll();

		long actualSize = list.size();
		long expectedSize = 3L;

		assertThat(actualSize, is(expectedSize));

		Iterator<ItemId> it = list.iterator();
		for (int i = 0; it.hasNext(); i++) {
			Logger.debug("id%1s:%2s", i, it.next());
		}
	}

	@Test
	public void テスト用ItemId確認() {
		Logger.debug("" + tm.getItemId(0, 0));
	}

	/* ************************************************************ */
	/*
	 * Fixture
	 */
	/* ************************************************************ */
	@Test
	public void Fixture読み込み_01(){
		printTitle();

		Fixtures.deleteDatabase();
		Fixtures.loadModels("test_models_ver20130112.yml");

		assertThat(ItemId.count(), is(19L));
	}
}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
