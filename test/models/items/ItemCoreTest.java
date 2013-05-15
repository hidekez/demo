package models.items;

import java.util.Locale;

import models.TestModels;
import models.items.multi.UserName;
import mytests.UnitTestBase;

import org.junit.Ignore;
import org.junit.Test;

import constants.ItemStateType;

import play.Logger;
import static org.hamcrest.CoreMatchers.*;

/**
 * ItemBaseをテストするためのダミークラス.
 *
 * @author H.Kezuka
 */
public class ItemCoreTest extends UnitTestBase {

	ItemBase actual;
	ItemBase expected;

	TestModels tm = new TestModels();

	public void assertModel(
			ItemBase _actual, ItemBasePOJO _expected) {

		// コア
		assertEquals("itemId:", _actual.itemId, _expected.itemId);// ItemId
		assertEquals("serialCode:", _actual.serialCode, _expected.serialCode);// String
		assertEquals("createdAt:", _actual.getCreatedAt(),
				_expected.createdAt);// ★Date
		assertEquals("enteredAt:",
				_actual.getEnteredAt(), _expected.enteredAt);// ★Date
		assertEquals("state:", _actual.state, _expected.state);// ItemStateType

	}

	public void assertModel(
			ItemBase _actual, ItemBase _expected) {

		// コア
		assertEquals("itemId:", _actual.itemId, _expected.itemId);// ItemId
		assertEquals("serialCode:", _actual.serialCode, _expected.serialCode);// String
		assertEquals("createdAt:", _actual.getCreatedAt(),
				_expected.getCreatedAt());// Date
		assertEquals("enteredAt:",
				_actual.getEnteredAt(), _expected.getEnteredAt());// Date
		assertEquals("state:", _actual.state, _expected.state);// ItemStateType

	}

	// 引数なしで、インスタンスフィールドを使用
	public void assertModel() {
		assertModel(actual, expected);
		Logger.debug("actual:%s", actual);
	}

	/* ************************************************************ */
	/*
	 * ビルダーを利用した新規作成
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void ビルダー_新規作成_01() {
		printTitle();

		expected = tm.dummyItem01();
		actual = new ItemCore.Builder()
				// -------------------------------------+
				// コア
				.itemId(tm.getItemId(0, 0))// ItemId
				.createdAt(TestModels.date1())// Date
				.enteredAt(TestModels.date1())// Date
				.state(ItemStateType.OK)// ItemStateType
				// -------------------------------------+
				// ビルド用
				.build();

		assertModel();
	}
}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
