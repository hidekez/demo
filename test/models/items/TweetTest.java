package models.items;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import play.Logger;
import play.test.Fixtures;
import utils.MyDateUtils;

import constants.TwitterCountry;
import constants.TwitterLanguage;
import static org.hamcrest.CoreMatchers.*;

import models.TestModels;
import models.items.Tweet;
import models.items.User;
import models.items.multi.Geo;
import models.items.multi.UserName;
import models.items.twitter.ITwitterConstant;
import mytests.UnitTestBase;

/**
 * つぶやきモデル・テスト
 *
 * @author H.Kezuka
 */
public class TweetTest extends UnitTestBase {

	Tweet actual;
	TwitterTweetPOJO expected;

	TestModels tm = new TestModels();

	/* ************************************************************ */

	public void assertModel(
			Tweet _actual, TwitterTweetPOJO _expected) {

		// アイテム共通
		assertEquals("itemId:", _actual.itemId, _expected.base.itemId);// ItemId
		assertEquals("createdAt:", _actual.getCreatedAt(),
				_expected.base.createdAt);// Date
		assertEquals("enteredAt:", _actual.getEnteredAt(),
				_expected.base.enteredAt);// Date
		assertEquals("state:", _actual.state, _expected.base.state);// ItemStateType
		// コア
		assertEquals("statusId_:", _actual.statusId, _expected.statusId);// long
		assertEquals("text_____:", _actual.text, _expected.text);// String
		assertEquals("owner____:", _actual.author, _expected.author);// TwitterUser
		assertEquals("language_:", _actual.getLanguage(), _expected.language);// TwitterLanguage
		assertEquals("country__:", _actual.getCountry(), _expected.country);// TwitterCountry
		assertEquals("isInReply:", _actual.isInReply, _expected.isInReply);// boolean
		assertEquals("hasGeo___:", _actual.hasGeo, _expected.hasGeo);// boolean
		if (_actual.inReplyTo != null) {
			assertEquals("inReplyTo.repStatusId",
					_actual.inReplyTo.repStatusId,
					_expected.inReplyTo.repStatusId);// InReplyTo
			assertEquals("inReplyTo.repUserId", _actual.inReplyTo.repUserId,
					_expected.inReplyTo.repUserId);// InReplyTo
			assertEquals("inReplyTo.repScreenName",
					_actual.inReplyTo.repScreenName,
					_expected.inReplyTo.repScreenName);// InReplyTo
		}
		if (_actual.geo != null) {
			assertThat(_actual.geo.latitude, is(_expected.geo.latitude));// Geo
			assertThat(_actual.geo.longitude, is(_expected.geo.longitude));// Geo
		}
	}

	// 引数なしで、インスタンスフィールドを使用
	public void assertModel() {
		showErrors();
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

		expected = tm.ttPojo01();
		actual = new Tweet.Builder()
				// Sub's fields
				.statusId(1L)
				.text("こんにちは、タローです。")
				.author(tm.getUser(0))
				.language(TwitterLanguage.getEnumByCode("ja"))
				.country(TwitterCountry.getEnumByCode("jp"))
				.isInReply(false)
				.hasGeo(true)
				// .inReplyTo()
				// .geo(35.66746, 139.550375)
				// -------------------------------------+
				// Super's fields
				.itemId(tm.getItemId(1, 0))
				.createdAt(tm.date1())
				.enteredAt(tm.today())
				// -------------------------------------+
				.build();

		assertModel();
	}

	@Test
	public void ビルダー_新規作成_02() {
		printTitle();

		expected = tm.ttPojo02();
		actual = new Tweet.Builder()
				// Sub's fields
				.statusId(2L)
				.text("タローさんへ、ジローより。")
				.author(tm.getUser(1))
				.language(TwitterLanguage.getEnumByCode("ja"))
				.country(TwitterCountry.getEnumByCode("jp"))
				.isInReply(true)
				.hasGeo(true)
				// .inReplyTo(1L, 2L, "jiro")
//				.geo(35.685361, 139.753141)
				// -------------------------------------+
				// Super's fields
				.itemId(tm.getItemId(1, 1))
				.createdAt(tm.date2())
				.enteredAt(tm.today())
				// -------------------------------------+
				.build();

		assertModel();
	}

	@Test
	public void ビルダー_新規作成_03() {
		printTitle();

		expected = tm.ttPojo03();
		actual = new Tweet.Builder()
				// Sub's fields
				.statusId(3L)
				.text("Hello world!")
				.author(tm.getUser(2))
				.language(TwitterLanguage.getEnumByCode("ja"))
				.country(TwitterCountry.getEnumByCode("jp"))
				.isInReply(false)
				.hasGeo(false)
				// .inReplyTo()
				// .geo()
				// -------------------------------------+
				// Super's fields
				.itemId(tm.getItemId(1, 2))
				.createdAt(tm.date3())
				.enteredAt(tm.today())
				// -------------------------------------+
				.build();

		assertModel();
	}

	@Test
	public void ビルダー_新規作成_検証パターン2() {
		printTitle();

		actual = tm.newTwitterTweet01();
//		TwitterUser user = TestModels.tu01();
//		user.save();
		expected = new TwitterTweetPOJO();
		expected.statusId = 1L;
		expected.author = actual.author;
		expected.text = "こんにちは、タローです。";
		expected.language = TwitterLanguage.getEnumByCode("ja");
		expected.country = TwitterCountry.getEnumByCode("jp");
		expected.isInReply = false;
		expected.hasGeo = true;
		// expected.geo = new Geo(35.66746, 139.550375);

		expected.base.itemId = tm.getItemId(1, 0);
		expected.base.createdAt = tm.date1();
		expected.base.enteredAt = tm.today();

		assertModel();
	}

	/* ************************************************************ */
	/*
	 * 保存
	 */
	/* ************************************************************ */
	@Test
	public void 保存_save_01() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.save();

		assertThat(actual.isPersistent(), is(true));
	}

	@Test
	public void 保存_save_02() {
		printTitle();

		actual = tm.newTwitterTweet02();
		actual.save();

		assertThat(actual.isPersistent(), is(true));
	}

	@Test
	public void 保存_save_03() {
		printTitle();

		actual = tm.newTwitterTweet03();
		actual.save();

		assertThat(actual.isPersistent(), is(true));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void 保存_valid_01() {
		printTitle();

		actual = tm.newTwitterTweet01();
		boolean result = actual.valid();

		showErrors();

		assertThat(result, is(true));
	}

	@Test
	public void 保存_valid_02() {
		printTitle();

		actual = tm.newTwitterTweet02();
		boolean result = actual.valid();

		assertThat(result, is(true));
	}

	@Test
	public void 保存_valid_03() {
		printTitle();

		actual = tm.newTwitterTweet03();
		boolean result = actual.valid();

		assertThat(result, is(true));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void 保存_validAndSave_01() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.saved = actual.validateAndSave();

		assertThat(actual.isPersistent(), is(true));
		assertThat(actual.saved, is(true));
	}

	@Test
	public void 保存_validAndSave_02() {
		printTitle();

		actual = tm.newTwitterTweet02();
		actual.saved = actual.validateAndSave();

		assertThat(actual.isPersistent(), is(true));
		assertThat(actual.saved, is(true));
	}

	@Test
	public void 保存_validAndSave_03() {
		printTitle();

		actual = tm.newTwitterTweet03();
		actual.saved = actual.validateAndSave();

		assertThat(actual.isPersistent(), is(true));
		assertThat(actual.saved, is(true));
	}

	/* ************************************************************ */
	/*
	 * バリデーション
	 */
	/* ************************************************************ */
	/*
	 * Geoのテストは{models.items.multi.StubOfMultisTest}内にて確認済み。
	 */

	private void assertValidation(boolean _expected) {
		boolean result = actual.valid();
		showErrors();
		assertThat(result, is(_expected));
	}

	// =============================================*
	@Test
	public void バリデーション_createdAt_Null() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.setCreatedAt(null);

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_createdAt_Ancient_遠い過去() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.setCreatedAt(tm.ancient());// @InFuture(BIRTH_OF_TWEET)による

		Logger.debug("TestModels.ancient():%s", tm.ancient());
		Logger.debug("actual.getCreatedAt()___:%s", actual.getCreatedAt());

		assertValidation(false);
	}

	@Test
	public void バリデーション_createdAt_Future_遠い未来() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.setCreatedAt(tm.future());// @InPastによる

		Logger.debug("TestModels.future():%s", tm.future());
		Logger.debug("actual.getCreatedAt()__:%s", actual.getCreatedAt());

		assertValidation(false);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_createdAt_未来_境界値_現在時刻() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.setCreatedAt(MyDateUtils.now());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());

		assertValidation(true);
	}

	// -------------------------------------+
	@Test
	public void バリデーション_createdAt_過去_境界値() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.setCreatedAt(ITwitterConstant.LIMIT_OF_TWEET_DATETIME.toDate());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());
		Logger.debug("actual.getCreatedAt().time__:%s", actual.getCreatedAt()
				.getTime());
		Logger.debug("LIMIT_OF_TWEET_DATETIME.time:%s",
				ITwitterConstant.LIMIT_OF_TWEET_DATETIME.toDate().getTime());

		assertValidation(false);// 同値だとエラー扱いなので注意
	}

	@Test
	public void バリデーション_createdAt_過去_境界値_ちょい足し() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.setCreatedAt(ITwitterConstant.LIMIT_OF_TWEET_DATETIME
				.plusMillis(1).toDate());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());
		Logger.debug("actual.getCreatedAt().time__:%s", actual.getCreatedAt()
				.getTime());
		Logger.debug("LIMIT_OF_TWEET_DATETIME.time:%s",
				ITwitterConstant.LIMIT_OF_TWEET_DATETIME.toDate().getTime());

		assertValidation(true);// １ミリセカンドでも進んでいればOK
	}

	@Test
	public void バリデーション_createdAt_過去_境界値_圏内_秒() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.setCreatedAt(ITwitterConstant.LIMIT_OF_TWEET_DATETIME
				.plusSeconds(1)
				.toDate());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());

		assertValidation(true);
	}

	@Test
	public void バリデーション_createdAt_過去_境界値_圏外_日() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.setCreatedAt(ITwitterConstant.LIMIT_OF_TWEET_DATETIME.minusDays(
				1)
				.toDate());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());

		assertValidation(false);
	}

	// 日付の変化がポイント
	@Test
	public void バリデーション_createdAt_過去_境界値_圏外_秒() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.setCreatedAt(ITwitterConstant.LIMIT_OF_TWEET_DATETIME
				.minusSeconds(1)
				.toDate());

		Logger.debug("actual.getCreatedAt():%s", actual.getCreatedAt());

		assertValidation(false);
	}

	// =============================================*
	@Test
	public void バリデーション_statusId_境界値() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.statusId = ITwitterConstant.STATUS_ID_MIN;

		assertValidation(true);
	}

	@Test
	public void バリデーション_statusId_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.statusId = ITwitterConstant.STATUS_ID_MIN + 1;

		assertValidation(true);
	}

	@Test
	public void バリデーション_statusId_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.statusId = ITwitterConstant.STATUS_ID_MIN - 1;

		assertValidation(false);

		actual.save();// セーブはできてしまうという確認
		assertThat(actual.isPersistent(), is(true));
	}

	// =============================================*
	@Test
	public void バリデーション_text_Null() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.text = null;

		assertValidation(false);// nullはNG
	}

	@Test
	public void バリデーション_text_Empty() {
		printTitle();

		actual = tm.newTwitterTweet01();
		actual.text = "";// 空文字

		assertValidation(false);// 空文字はNG
	}

	// -------------------------------------+
	@Test
	public void バリデーション_text_境界値() {
		printTitle();

		actual = tm.newTwitterTweet01();
		int max = ITwitterConstant.TEXT_MAX_LENGTH;
		actual.text = StringUtils.repeat('a', max);

		assertValidation(true);
	}

	@Test
	public void バリデーション_text_境界値_圏内() {
		printTitle();

		actual = tm.newTwitterTweet01();
		int max = ITwitterConstant.TEXT_MAX_LENGTH;
		actual.text = StringUtils.repeat('a', max - 1);

		assertValidation(true);
	}

	@Test
	public void バリデーション_text_境界値_圏外() {
		printTitle();

		actual = tm.newTwitterTweet01();
		int max = ITwitterConstant.TEXT_MAX_LENGTH;
		actual.text = StringUtils.repeat('a', max + 1);// 字数オーバー

		assertValidation(false);
	}

	/* ************************************************************ */
	/*
	 * Fixture
	 */
	/* ************************************************************ */
	@Test
	public void Fixture読み込み_01() {
		printTitle();

		Fixtures.deleteDatabase();
		Fixtures.loadModels("test_models_ver20130112.yml");

		assertThat(Tweet.count(), is(15L));
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
