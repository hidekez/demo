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

public class RepostedUserTest extends UnitTestBase {

	@Before
	public void setup() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("test_models_ver20130112.yml");
	}

//	private void printRepost(RepostBase _repost) {
//		Logger.debug("" + _repost);
//		Logger.debug("repost.isPersistent=" + _repost.isPersistent());
//	}
//
//	private void printReposts(List<RepostBase> _reposts) {
//		for (RepostBase repo : _reposts) {
//			Logger.debug("" + repo);
//		}
//	}
//
//	private void printRepostLabel(List<RepostBase> _reposts) {
//		for (RepostBase repo : _reposts) {
//			Logger.debug("label:" + repo.getLabel());
//		}
//	}
//
//	private void printTweets(List<Tweet> _tweets) {
//		for (Tweet twt : _tweets) {
//			Logger.debug("" + twt);
//		}
//	}
//
//	private void printTweetsSet(Set<Tweet> _tweets) {
//		for (Tweet twt : _tweets) {
//			Logger.debug("" + twt);
//		}
//	}

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

	// アイテムの検索 User
	// =============================================*
	/*
	 * リポストされたユーザーの検索.
	 * User -> User
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findRepostedUsers_User_単数_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findRepostedUsers(usr1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_User_単数_01() {
		// リポストと関連のないユーザー
		User usr1 = User.findBySerialCode("usr-bob").first();
		List<User> lst = RepostBase.findRepostedUsers(usr1).fetch();
		assertThat(lst.size(), is(0));
//		assertThat(lst.get(0).screenName, is());
	}

	@Test
	public void findRepostedUsers_User_複数_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findRepostedUsers(usr1, usr2).fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedUsers_User_複数_01() {
		// リポストと関連のないユーザー
		User usr1 = User.findBySerialCode("usr-jiro").first();
		User usr2 = User.findBySerialCode("usr-bob").first();
		List<User> lst = RepostBase.findRepostedUsers(usr1, usr2).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findRepostedUsers_User_単数_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findRepostedUsers(usr1)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_User_複数_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findRepostedUsers(usr1, usr2)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));// ★
	}

	@Test
	public void findRepostedUsers_User_単数_投稿者_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findRepostedUsers(usr1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_User_単数_投稿者_01() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findRepostedUsers(usr1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_User_単数_投稿者_02() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findRepostedUsers(usr1)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_User_複数_投稿者_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findRepostedUsers(usr1, usr2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedUsers_User_複数_投稿者_01() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findRepostedUsers(usr1, usr2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
//		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_User_複数_投稿者_02() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findRepostedUsers(usr1, usr2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedUsers_User_単数_投稿者_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findRepostedUsers(usr1)
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_User_複数_投稿者_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findRepostedUsers(usr1, usr2)
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findRepostedUsers_String_単数_00() {
		List<User> lst = RepostBase.findRepostedUsers("usr-goro").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_String_単数_01() {
		List<User> lst = RepostBase.findRepostedUsers("usr-bob").fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedUsers_String_複数_00() {
		List<User> lst = RepostBase.findRepostedUsers("usr-goro", "usr-jiro")
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedUsers_String_複数_01() {
		// リポストと関連のないユーザー
		List<User> lst = RepostBase.findRepostedUsers("usr-jiro", "usr-bob")
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findRepostedUsers_String_単数_降順_00() {
		List<User> lst = RepostBase.findRepostedUsers("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_String_複数_降順_00() {
		List<User> lst = RepostBase.findRepostedUsers("usr-goro", "usr-jiro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));// ★
	}

	@Test
	public void findRepostedUsers_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findRepostedUsers("usr-goro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findRepostedUsers("usr-goro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_String_単数_投稿者_02() {
		List<User> lst = RepostBase.findRepostedUsers("usr-goro")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findRepostedUsers("usr-goro", "usr-jiro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedUsers_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findRepostedUsers("usr-goro", "usr-jiro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_String_複数_投稿者_02() {
		List<User> lst = RepostBase.findRepostedUsers("usr-goro", "usr-jiro")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedUsers_String_単数_投稿者_降順_00() {
		List<User> lst = RepostBase.findRepostedUsers("usr-goro")
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findRepostedUsers_String_複数_投稿者_降順_00() {
		List<User> lst = RepostBase.findRepostedUsers("usr-goro", "usr-jiro")
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findRepostedUsers_NoArg_00() {
		List<User> lst = RepostBase.findRepostedUsers().fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedUsers_NoArg_降順_00() {
		List<User> lst = RepostBase.findRepostedUsers()
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findRepostedUsers_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findRepostedUsers()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedUsers_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findRepostedUsers()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedUsers_NoArg_投稿者_02() {
		List<User> lst = RepostBase.findRepostedUsers()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedUsers_NoArg_投稿者_降順_00() {
		List<User> lst = RepostBase.findRepostedUsers()
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	// =============================================*
	/*
	 * カテゴリー付けされているユーザーの検索.
	 * User -> User
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findCategorizedUsers_User_単数_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findCategorizedUsers(usr1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findCategorizedUsers_User_単数_01() {
		// リポストと関連のないユーザー
		User usr1 = User.findBySerialCode("usr-bob").first();
		List<User> lst = RepostBase.findCategorizedUsers(usr1).fetch();
		assertThat(lst.size(), is(0));
//		assertThat(lst.get(0).screenName, is());
	}

	@Test
	public void findCategorizedUsers_User_複数_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findCategorizedUsers(usr1, usr2).fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedUsers_User_複数_01() {
		// リポストと関連のないユーザー
		User usr1 = User.findBySerialCode("usr-jiro").first();
		User usr2 = User.findBySerialCode("usr-bob").first();
		List<User> lst = RepostBase.findCategorizedUsers(usr1, usr2).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findCategorizedUsers_User_単数_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findCategorizedUsers(usr1)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findCategorizedUsers_User_複数_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findCategorizedUsers(usr1, usr2)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));// ★
	}

	@Test
	public void findCategorizedUsers_User_単数_投稿者_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findCategorizedUsers(usr1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findCategorizedUsers_User_単数_投稿者_01() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findCategorizedUsers(usr1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findCategorizedUsers_User_単数_投稿者_02() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findCategorizedUsers(usr1)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findCategorizedUsers_User_複数_投稿者_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findCategorizedUsers(usr1, usr2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedUsers_User_複数_投稿者_01() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findCategorizedUsers(usr1, usr2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findCategorizedUsers_User_複数_投稿者_02() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findCategorizedUsers(usr1, usr2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedUsers_User_単数_投稿者_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findCategorizedUsers(usr1)
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findCategorizedUsers_User_複数_投稿者_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findCategorizedUsers(usr1, usr2)
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findCategorizedUsers_String_単数_00() {
		List<User> lst = RepostBase.findCategorizedUsers("usr-goro").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findCategorizedUsers_String_単数_01() {
		List<User> lst = RepostBase.findCategorizedUsers("usr-bob").fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findCategorizedUsers_String_複数_00() {
		List<User> lst = RepostBase
				.findCategorizedUsers("usr-goro", "usr-jiro").fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedUsers_String_複数_01() {
		// リポストと関連のないユーザー
		List<User> lst = RepostBase.findCategorizedUsers("usr-jiro", "usr-bob")
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findCategorizedUsers_String_単数_降順_00() {
		List<User> lst = RepostBase.findCategorizedUsers("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findCategorizedUsers_String_複数_降順_00() {
		List<User> lst = RepostBase
				.findCategorizedUsers("usr-goro", "usr-jiro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));// ★
	}

	@Test
	public void findCategorizedUsers_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findCategorizedUsers("usr-goro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findCategorizedUsers_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findCategorizedUsers("usr-goro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findCategorizedUsers_String_単数_投稿者_02() {
		List<User> lst = RepostBase.findCategorizedUsers("usr-goro")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findCategorizedUsers_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase
				.findCategorizedUsers("usr-goro", "usr-jiro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedUsers_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase
				.findCategorizedUsers("usr-goro", "usr-jiro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findCategorizedUsers_String_複数_投稿者_02() {
		List<User> lst = RepostBase
				.findCategorizedUsers("usr-goro", "usr-jiro")// 文字列による
				.contributor("usr-goro")
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedUsers_String_単数_投稿者_降順_00() {
		List<User> lst = RepostBase.findCategorizedUsers("usr-goro")
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findCategorizedUsers_String_複数_投稿者_降順_00() {
		List<User> lst = RepostBase
				.findCategorizedUsers("usr-goro", "usr-jiro")
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findCategorizedUsers_NoArg_00() {
		List<User> lst = RepostBase.findCategorizedUsers().fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedUsers_NoArg_降順_00() {
		List<User> lst = RepostBase.findCategorizedUsers()
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findCategorizedUsers_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findCategorizedUsers()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedUsers_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findCategorizedUsers()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findCategorizedUsers_NoArg_投稿者_02() {
		List<User> lst = RepostBase.findCategorizedUsers()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategorizedUsers_NoArg_投稿者_降順_00() {
		List<User> lst = RepostBase.findCategorizedUsers()
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	// =============================================*
	/*
	 * タグ付けされているユーザーの検索.
	 * User -> User
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findTaggedUsers_User_単数_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findTaggedUsers(usr1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_User_単数_01() {
		// リポストと関連のないユーザー
		User usr1 = User.findBySerialCode("usr-bob").first();
		List<User> lst = RepostBase.findTaggedUsers(usr1).fetch();
		assertThat(lst.size(), is(0));
//		assertThat(lst.get(0).screenName, is());
	}

	@Test
	public void findTaggedUsers_User_複数_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findTaggedUsers(usr1, usr2).fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedUsers_User_複数_01() {
		// リポストと関連のないユーザー
		User usr1 = User.findBySerialCode("usr-jiro").first();
		User usr2 = User.findBySerialCode("usr-bob").first();
		List<User> lst = RepostBase.findTaggedUsers(usr1, usr2).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findTaggedUsers_User_単数_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findTaggedUsers(usr1)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_User_複数_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findTaggedUsers(usr1, usr2)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));// ★
	}

	@Test
	public void findTaggedUsers_User_単数_投稿者_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findTaggedUsers(usr1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_User_単数_投稿者_01() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findTaggedUsers(usr1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_User_単数_投稿者_02() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findTaggedUsers(usr1)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_User_複数_投稿者_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findTaggedUsers(usr1, usr2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedUsers_User_複数_投稿者_01() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findTaggedUsers(usr1, usr2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
//		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_User_複数_投稿者_02() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findTaggedUsers(usr1, usr2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedUsers_User_単数_投稿者_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<User> lst = RepostBase.findTaggedUsers(usr1)
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_User_複数_投稿者_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<User> lst = RepostBase.findTaggedUsers(usr1, usr2)
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findTaggedUsers_String_単数_00() {
		List<User> lst = RepostBase.findTaggedUsers("usr-goro").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_String_単数_01() {
		List<User> lst = RepostBase.findTaggedUsers("usr-bob").fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTaggedUsers_String_複数_00() {
		List<User> lst = RepostBase.findTaggedUsers("usr-goro", "usr-jiro")
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedUsers_String_複数_01() {
		// リポストと関連のないユーザー
		List<User> lst = RepostBase.findTaggedUsers("usr-jiro", "usr-bob")
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findTaggedUsers_String_単数_降順_00() {
		List<User> lst = RepostBase.findTaggedUsers("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_String_複数_降順_00() {
		List<User> lst = RepostBase.findTaggedUsers("usr-goro", "usr-jiro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));// ★
	}

	@Test
	public void findTaggedUsers_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findTaggedUsers("usr-goro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findTaggedUsers("usr-goro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_String_単数_投稿者_02() {
		List<User> lst = RepostBase.findTaggedUsers("usr-goro")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findTaggedUsers("usr-goro", "usr-jiro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedUsers_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findTaggedUsers("usr-goro", "usr-jiro")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_String_複数_投稿者_02() {
		List<User> lst = RepostBase.findTaggedUsers("usr-goro", "usr-jiro")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedUsers_String_単数_投稿者_降順_00() {
		List<User> lst = RepostBase.findTaggedUsers("usr-goro")
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).screenName, is("goro_san"));
	}

	@Test
	public void findTaggedUsers_String_複数_投稿者_降順_00() {
		List<User> lst = RepostBase.findTaggedUsers("usr-goro", "usr-jiro")
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findTaggedUsers_NoArg_00() {
		List<User> lst = RepostBase.findTaggedUsers().fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedUsers_NoArg_降順_00() {
		List<User> lst = RepostBase.findTaggedUsers()
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findTaggedUsers_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();// 五郎
		List<User> lst = RepostBase.findTaggedUsers()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedUsers_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();// 次郎
		List<User> lst = RepostBase.findTaggedUsers()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedUsers_NoArg_投稿者_02() {
		List<User> lst = RepostBase.findTaggedUsers()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTaggedUsers_NoArg_投稿者_降順_00() {
		List<User> lst = RepostBase.findTaggedUsers()
				.contributor("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
