package models.reposts;

import org.junit.*;

import java.util.*;

import javax.persistence.Query;

import play.Logger;
import play.db.jpa.JPA;
import play.test.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.*;
//import static org.junit.matchers.JUnitMatchers.*;

import models.*;
import models.items.*;
import models.labels.*;
import models.reposts.*;
import models.reposts.RepostBase.OrderBy;
import mytests.UnitTestBase;
import constants.*;

public class RepostedTweetTest extends UnitTestBase {

	@Before
	public void setup() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("test_models_ver20130112.yml");
	}

	private void printRepost(RepostBase _repost) {
		Logger.debug("" + _repost);
		Logger.debug("repost.isPersistent=" + _repost.isPersistent());
	}

	private void printReposts(List<RepostBase> _reposts) {
		for (RepostBase repo : _reposts) {
			Logger.debug("" + repo);
		}
	}

	private void printRepostLabel(List<RepostBase> _reposts) {
		for (RepostBase repo : _reposts) {
			Logger.debug("label:" + repo.getLabel());
		}
	}

	private void printTweets(List<Tweet> _tweets) {
		for (Tweet twt : _tweets) {
			Logger.debug("" + twt);
		}
	}

	private void printTweetsSet(Set<Tweet> _tweets) {
		for (Tweet twt : _tweets) {
			Logger.debug("" + twt);
		}
	}

	/* ************************************************************ */
	/*
	 * 検索ヘルパ・同種類からの検索
	 * Item -> Item
	 * Label -> Label
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * ラッパー
	 * あるラベル種類に関連するアイテムのリスト取得
	 * 引数なし：すべて
	 * 引数あり：条件にマッチするもの
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

	@Test
	public void データベースチェック_01() {
		assertThat(RepostBase.count(), is(33L));
		assertThat(RepostTweetCategory.count(), is(7L));
		assertThat(RepostTweetTag.count(), is(16L));
	}

	// アイテムの検索 Tweet
	// =============================================*
	/*
	 * リポストされているつぶやきの検索.
	 * Tweet -> Tweet
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findRepostedTweets_Tweet_単数_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findRepostedTweets_Tweet_単数_01() {
		// リポストと関連のないつぶやき
		Tweet twt1 = Tweet.findBySerialCode("twt-bob1").first();// Bobのつぶやき
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1).fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTweets_Tweet_複数_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1, twt2).fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTweets_Tweet_複数_01() {
		// リポストと関連のないユーザー
		Tweet twt1 = Tweet.findBySerialCode("twt-jiro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-bob1").first();// Bobのつぶやき
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1, twt2).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findRepostedTweets_Tweet_単数_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1)
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findRepostedTweets_Tweet_複数_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1, twt2)
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));// ★
	}

	@Test
	public void findRepostedTweets_Tweet_複数_降順_01() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1, twt2)
				.orderBy(ItemBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findRepostedTweets_Tweet_単数_投稿者_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTweets_Tweet_単数_投稿者_01() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findRepostedTweets_Tweet_単数_投稿者_02() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTweets_Tweet_複数_投稿者_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1, twt2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findRepostedTweets_Tweet_複数_投稿者_01() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1, twt2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findRepostedTweets_Tweet_複数_投稿者_02() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1, twt2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findRepostedTweets_Tweet_複数_投稿者_03() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1, twt2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTweets_Tweet_単数_投稿者_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1)
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTweets_Tweet_複数_投稿者_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findRepostedTweets(twt1, twt2)
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findRepostedTweets_String_単数_00() {
		List<Tweet> lst = RepostBase.findRepostedTweets("twt-goro1").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findRepostedTweets_String_単数_01() {
		List<Tweet> lst = RepostBase.findRepostedTweets("twt-bob1").fetch();// Bobのつぶやき
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTweets_String_複数_00() {
		List<Tweet> lst = RepostBase.findRepostedTweets("twt-goro1",
				"twt-jiro1").fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTweets_String_複数_01() {
		// リポストと関連のないユーザー
		List<Tweet> lst = RepostBase
				.findRepostedTweets("twt-jiro1", "twt-bob1").fetch();// Bobのつぶやき
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findRepostedTweets_String_単数_降順_00() {
		List<Tweet> lst = RepostBase.findRepostedTweets("twt-goro1")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findRepostedTweets_String_複数_降順_00() {
		List<Tweet> lst = RepostBase
				.findRepostedTweets("twt-goro1", "twt-jiro1")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));// ★
	}

	@Test
	public void findRepostedTweets_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findRepostedTweets("twt-goro1")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTweets_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findRepostedTweets("twt-goro1")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findRepostedTweets_String_単数_投稿者_02() {
		List<Tweet> lst = RepostBase.findRepostedTweets("twt-goro1")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTweets_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase
				.findRepostedTweets("twt-goro1", "twt-jiro1")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findRepostedTweets_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase
				.findRepostedTweets("twt-goro1", "twt-jiro1")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findRepostedTweets_String_複数_投稿者_02() {
		List<Tweet> lst = RepostBase
				.findRepostedTweets("twt-goro1", "twt-jiro1")
				.contributor("usr-jiro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findRepostedTweets_String_単数_投稿者_降順_00() {
		List<Tweet> lst = RepostBase.findRepostedTweets("twt-goro1")
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTweets_String_単数_投稿者_降順_01() {
		List<Tweet> lst = RepostBase.findRepostedTweets("twt-goro1")
				.contributor("usr-jiro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findRepostedTweets_String_複数_投稿者_降順_00() {
		List<Tweet> lst = RepostBase
				.findRepostedTweets("twt-goro1", "twt-jiro1")
				.contributor("usr-jiro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findRepostedTweets_NoArg_00() {
		List<Tweet> lst = RepostBase.findRepostedTweets().fetch();
		assertThat(lst.size(), is(12));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTweets_NoArg_降順_00() {
		List<Tweet> lst = RepostBase.findRepostedTweets()
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(12));
		assertThat(lst.get(0).serialCode, is("twt-hanako5"));
	}

	@Test
	public void findRepostedTweets_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findRepostedTweets()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(8));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTweets_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findRepostedTweets()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(4));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTweets_NoArg_投稿者_02() {
		List<Tweet> lst = RepostBase.findRepostedTweets()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(8));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTweets_NoArg_投稿者_降順_00() {
		List<Tweet> lst = RepostBase.findRepostedTweets()
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(8));
		assertThat(lst.get(0).serialCode, is("twt-hanako5"));
	}

	// =============================================*
	/*
	 * カテゴリー付けされているつぶやきの検索.
	 * Tweet -> Tweet
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findCategorizedTweets_Tweet_単数_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findCategorizedTweets_Tweet_単数_01() {
		// リポストと関連のないユーザー
		Tweet twt1 = Tweet.findBySerialCode("twt-bob1").first();// Bobのつぶやき
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1).fetch();
		assertThat(lst.size(), is(0));
//		assertThat(lst.get(0).serialCode, is());
	}

	@Test
	public void findCategorizedTweets_Tweet_複数_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1, twt2).fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedTweets_Tweet_複数_01() {
		// リポストと関連のないユーザー
		Tweet twt1 = Tweet.findBySerialCode("twt-jiro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-bob1").first();// Bobのつぶやき
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1, twt2).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findCategorizedTweets_Tweet_単数_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1)
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findCategorizedTweets_Tweet_複数_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1, twt2)
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));// ★
	}

	@Test
	public void findCategorizedTweets_Tweet_単数_投稿者_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findCategorizedTweets_Tweet_単数_投稿者_01() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findCategorizedTweets_Tweet_単数_投稿者_02() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1)
				.contributor("usr-jiro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findCategorizedTweets_Tweet_複数_投稿者_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1, twt2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedTweets_Tweet_複数_投稿者_01() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1, twt2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findCategorizedTweets_Tweet_複数_投稿者_02() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1, twt2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedTweets_Tweet_単数_投稿者_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1)
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findCategorizedTweets_Tweet_単数_投稿者_降順_01() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1)
				.contributor("usr-jiro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findCategorizedTweets_Tweet_複数_投稿者_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();// タグで
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();// カテゴリーで
		List<Tweet> lst = RepostBase.findCategorizedTweets(twt1, twt2)
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findCategorizedTweets_String_単数_00() {
		List<Tweet> lst = RepostBase.findCategorizedTweets("twt-goro1").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findCategorizedTweets_String_単数_01() {
		List<Tweet> lst = RepostBase.findCategorizedTweets("twt-bob1").fetch();// Bobのつぶやき
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findCategorizedTweets_String_複数_00() {
		List<Tweet> lst = RepostBase.findCategorizedTweets("twt-goro1",
				"twt-jiro1").fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedTweets_String_複数_01() {
		// リポストと関連のないユーザー
		List<Tweet> lst = RepostBase.findCategorizedTweets("twt-jiro1",
				"twt-bob1").fetch();// Bobのつぶやき
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findCategorizedTweets_String_単数_降順_00() {
		List<Tweet> lst = RepostBase.findCategorizedTweets("twt-goro1")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findCategorizedTweets_String_複数_降順_00() {
		List<Tweet> lst = RepostBase
				.findCategorizedTweets("twt-goro1", "twt-jiro1")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));// ★
	}

	@Test
	public void findCategorizedTweets_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findCategorizedTweets("twt-goro1")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findCategorizedTweets_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findCategorizedTweets("twt-goro1")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findCategorizedTweets_String_単数_投稿者_02() {
		List<Tweet> lst = RepostBase.findCategorizedTweets("twt-goro1")
				.contributor("usr-jiro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findCategorizedTweets_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase
				.findCategorizedTweets("twt-goro1", "twt-jiro1")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedTweets_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase
				.findCategorizedTweets("twt-goro1", "twt-jiro1")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findCategorizedTweets_String_複数_投稿者_02() {
		List<Tweet> lst = RepostBase
				.findCategorizedTweets("twt-goro1", "twt-jiro1")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedTweets_String_単数_投稿者_降順_00() {
		List<Tweet> lst = RepostBase.findCategorizedTweets("twt-goro1")
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findCategorizedTweets_String_複数_投稿者_降順_00() {
		List<Tweet> lst = RepostBase
				.findCategorizedTweets("twt-goro2", "twt-jiro2")
				.contributor("usr-hanako")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("twt-jiro2"));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findCategorizedTweets_NoArg_00() {
		List<Tweet> lst = RepostBase.findCategorizedTweets().fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedTweets_NoArg_降順_00() {
		List<Tweet> lst = RepostBase.findCategorizedTweets()
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(5));
		assertThat(lst.get(0).serialCode, is("twt-jiro2"));
	}

	@Test
	public void findCategorizedTweets_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findCategorizedTweets()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findCategorizedTweets_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findCategorizedTweets()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedTweets_NoArg_投稿者_02() {
		List<Tweet> lst = RepostBase.findCategorizedTweets()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findCategorizedTweets_NoArg_投稿者_降順_00() {
		List<Tweet> lst = RepostBase.findCategorizedTweets()
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	// =============================================*
	/*
	 * タグ付けされているつぶやきの検索.
	 * Tweet -> Tweet
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findTaggedTweets_Tweet_単数_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();// カテゴリーと関連
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1).fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTaggedTweets_Tweet_単数_01() {
		// リポストと関連のないユーザー
		Tweet twt1 = Tweet.findBySerialCode("twt-bob1").first();// Bobのつぶやき
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1).fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTaggedTweets_Tweet_単数_02() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();// カテゴリーと関連
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro2"));
	}

	@Test
	public void findTaggedTweets_Tweet_複数_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();// タグと関連なし
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1, twt2).fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedTweets_Tweet_複数_01() {
		// リポストと関連のないユーザー
		Tweet twt1 = Tweet.findBySerialCode("twt-jiro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-bob1").first();// Bobのつぶやき
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1, twt2).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findTaggedTweets_Tweet_単数_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();// カテゴリーと関連
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1)
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(0));
//		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findTaggedTweets_Tweet_複数_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();// カテゴリーと関連
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1, twt2)
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));// ★
	}

	@Test
	public void findTaggedTweets_Tweet_単数_投稿者_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();// タグと関連なし
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
//		assertThat(lst.get(0).serialCode, is("twt-goro1"));
	}

	@Test
	public void findTaggedTweets_Tweet_単数_投稿者_01() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();// カテゴリーと関連
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTaggedTweets_Tweet_単数_投稿者_02() {
		Tweet twt1 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findTaggedTweets_Tweet_複数_投稿者_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1, twt2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedTweets_Tweet_複数_投稿者_01() {
		Tweet twt1 = Tweet.findBySerialCode("twt-hanako1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1, twt2)
				.contributor(acnt) // エンティティによる
				.fetch();
//		Logger.debug("twt1:%s", twt1);
//		Logger.debug("twt2:%s", twt2);
//		Logger.debug("acnt:%s", acnt);

		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedTweets_Tweet_複数_投稿者_02() {
		Tweet twt1 = Tweet.findBySerialCode("twt-hanako1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1, twt2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedTweets_Tweet_単数_投稿者_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1)
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findTaggedTweets_Tweet_複数_投稿者_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro1").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<Tweet> lst = RepostBase.findTaggedTweets(twt1, twt2)
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findTaggedTweets_String_単数_00() {
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro1").fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTaggedTweets_String_単数_01() {
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro2").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro2"));
	}

	@Test
	public void findTaggedTweets_String_単数_02() {
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-bob1").fetch();// Bobのつぶやき
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTaggedTweets_String_複数_00() {
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro1", "twt-jiro1")
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedTweets_String_複数_01() {
		// リポストと関連のないユーザー
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-jiro1", "twt-bob1")
				.fetch();// Bobのつぶやき
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findTaggedTweets_String_単数_降順_00() {
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro2")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro2"));
	}

	@Test
	public void findTaggedTweets_String_複数_降順_00() {
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro2", "twt-jiro1")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findTaggedTweets_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro1")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTaggedTweets_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("goro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro2")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro2"));
	}

	@Test
	public void findTaggedTweets_String_単数_投稿者_02() {
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro2")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro2"));
	}

	@Test
	public void findTaggedTweets_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro2", "twt-jiro1")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedTweets_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro3", "twt-jiro1")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro3"));
	}

	@Test
	public void findTaggedTweets_String_複数_投稿者_02() {
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro2", "twt-jiro1")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedTweets_String_単数_投稿者_降順_00() {
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro2")
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("twt-goro2"));
	}

	@Test
	public void findTaggedTweets_String_複数_投稿者_降順_00() {
		List<Tweet> lst = RepostBase.findTaggedTweets("twt-goro2", "twt-jiro1")
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findTaggedTweets_NoArg_00() {
		List<Tweet> lst = RepostBase.findTaggedTweets().fetch();
		assertThat(lst.size(), is(10));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedTweets_NoArg_降順_00() {
		List<Tweet> lst = RepostBase.findTaggedTweets()
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(10));
		assertThat(lst.get(0).serialCode, is("twt-hanako5"));
	}

	@Test
	public void findTaggedTweets_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<Tweet> lst = RepostBase.findTaggedTweets()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(8));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedTweets_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<Tweet> lst = RepostBase.findTaggedTweets()
				.contributor(acnt) // エンティティによる
				.fetch();
		printTweets(lst);
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	//Fixtureのデータエラーだった。。。
//	@Test
//	public void checkHanakosTweets() {
//		List<Tweet> lst = Tweet.find("author.screenName", "hana_chan").fetch();
//		printTweets(lst);
//		Logger.debug("----------------");
//		Logger.debug("RepostTweetTag.count:"+RepostTweetTag.count());
//		printTweets(lst);
//		Logger.debug("----------------");
////		lst = RepostBase.findRepostedTweets().fetch();
//		lst = RepostBase.findTaggedTweets().fetch();
//		printTweets(lst);
//		Logger.debug("----------------");
//		List<RepostTweetTag> rtts = RepostTweetTag.find(
//				"SELECT r FROM RepostTweetTag r "
//						+ "WHERE r.tweet.text = :txt ")
//				.bind("txt", "ビジュアル系ってどうしてるんだろう？")
//				.fetch();
//		Logger.debug("" + rtts);
//		Logger.debug("" + rtts.size());
//		Logger.debug("----------------");
//		RepostTweetTag rtt = RepostTweetTag.find(
//				"SELECT r FROM RepostTweetTag r "
//						+ "WHERE r.contributor.loginUser.screenName = :sn ")
//				.bind("sn", "jiro_san")
//				.first();
//		Logger.debug("" + rtt);
////		Logger.debug("----------------");
////		RepostTweetTag rtt = RepostTweetTag.find(
////				"SELECT r FROM RepostTweetTag r "
////						+ "WHERE r.tweet.text = :txt "
////						+ "AND r.contributor.loginUser.screenName = :sn")
////				.bind("txt", "ビジュアル系ってどうしてるんだろう？")
////				.bind("sn", "jiro_san").first();
////		Logger.debug("" + rtt);
//
//	}

	@Test
	public void findTaggedTweets_NoArg_投稿者_02() {
		List<Tweet> lst = RepostBase.findTaggedTweets()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(8));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedTweets_NoArg_投稿者_降順_00() {
		List<Tweet> lst = RepostBase.findTaggedTweets()
				.contributor("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC)
				.fetch();
		assertThat(lst.size(), is(8));
		assertThat(lst.get(0).serialCode, is("twt-hanako5"));
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
