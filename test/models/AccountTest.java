/*
 * 作成日 2012/04/24
 * 修正日 2012/06/05 コメントの変更（WZアウトライン用記号の削除）
 */
package models;

import models.Account;
import models.items.TwitterUserPOJO;
import models.items.User;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import constants.LifeStateType;

import static org.hamcrest.CoreMatchers.*;

import play.Logger;
import play.test.Fixtures;

import mytests.UnitTestBase;

/**
 * Accountテスト.
 *
 * @author H.Kezuka
 */
public class AccountTest extends UnitTestBase {

	Account actual;
	AccountPOJO expected;

	TestModels tm = new TestModels();

	/* ************************************************************ */

	public void assertModel(
			Account _actual, AccountPOJO _expected) {

		assertSame("loginUser:", _actual.loginUser, _expected.loginUser);
		assertEquals("enteredAt:", _actual.getEnteredAt(), _expected.enteredAt);
		assertEquals("visitedAt:", _actual.getVisitedAt(), _expected.visitedAt);
		assertEquals("life     :", _actual.life, _expected.life);
		assertEquals("tags     :", _actual.tags, _expected.tags);

	}

	// 引数なしで、インスタンスフィールドを使用
	public void assertModel() {
		assertModel(actual, expected);
		Logger.debug("actual:%s", actual);
	}

	/* ************************************************************ */
	@Test
	public void 新規作成_01() {
		printTitle();

		Fixtures.deleteDatabase();

		User user = tm.newTwitterUser01();
		actual = new Account(user);
		actual.save();

		assertThat(Account.count(), is(1L));
		assertThat(actual.loginUser.screenName, is("taro"));
	}

	/*
	 * バリデーションは割愛
	 */

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

		assertThat(Account.count(),is(4L));
	}

}

/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
