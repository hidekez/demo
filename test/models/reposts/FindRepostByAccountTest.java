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

public class FindRepostByAccountTest extends UnitTestBase {

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
	 * 検索ヘルパ・投稿者による検索
	 */
	/* ************************************************************ */
	// アイテムの検索 User
	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたユーザーの検索.
	 * Account -> User
	 */
	// =============================================*
	@Test
	public void findUsersByAccounts_Entity_単数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<User> lst = RepostBase
				.findUsersByAccounts(acnt1).fetch();// エンティティ
		assertThat(lst.size(), is(2));
	}

	@Test
	public void findUsersByAccounts_String_単数_00() {
		List<User> lst = RepostBase
				.findUsersByAccounts("usr-goro").fetch();// 文字列
		assertThat(lst.size(), is(2));
	}

	@Test
	public void findUsersByAccounts_Entity_複数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<User> lst = RepostBase
				.findUsersByAccounts(acnt1, acnt2).fetch();// エンティティ
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersByAccounts_String_複数_00() {
		List<User> lst = RepostBase
				.findUsersByAccounts("usr-goro", "usr-jiro").fetch();// 文字列
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findUsersByAccounts_Entity_単数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<User> lst = RepostBase
				.findUsersByAccounts(acnt1)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC).fetch();// エンティティ
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findUsersByAccounts_String_単数_降順_00() {
		List<User> lst = RepostBase
				.findUsersByAccounts("usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC).fetch();// 文字列
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findUsersByAccounts_Entity_複数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<User> lst = RepostBase
				.findUsersByAccounts(acnt1, acnt2)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findUsersByAccounts_String_複数_降順_00() {
		List<User> lst = RepostBase
				.findUsersByAccounts("usr-goro", "usr-jiro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	// -------------------------------------+
	@Test
	public void findUsersByAccounts_Entity_ラベル指定_単数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<User> lst = RepostBase
				.findUsersByAccounts(LabelType.CATEGORY, acnt1).fetch(); // エンティティ
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersByAccounts_String_ラベル指定_単数_00() {
		List<User> lst = RepostBase
				.findUsersByAccounts(LabelType.CATEGORY, "usr-goro").fetch(); // 文字列
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersByAccounts_Entity_ラベル指定_複数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<User> lst = RepostBase
				.findUsersByAccounts(LabelType.CATEGORY, acnt1, acnt2).fetch(); // エンティティ
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersByAccounts_String_ラベル指定_複数_00() {
		List<User> lst = RepostBase
				.findUsersByAccounts(LabelType.CATEGORY, "usr-goro", "usr-jiro")
				.fetch(); // 文字列
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findUsersByAccounts_Entity_ラベル指定_単数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<User> lst = RepostBase
				.findUsersByAccounts(LabelType.CATEGORY, acnt1)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findUsersByAccounts_String_ラベル指定_単数_降順_00() {
		List<User> lst = RepostBase
				.findUsersByAccounts(LabelType.CATEGORY, "usr-goro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findUsersByAccounts_Entity_ラベル指定_複数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<User> lst = RepostBase
				.findUsersByAccounts(LabelType.CATEGORY, acnt1, acnt2)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	@Test
	public void findUsersByAccounts_String_ラベル指定_複数_降順_00() {
		List<User> lst = RepostBase
				.findUsersByAccounts(LabelType.CATEGORY, "usr-goro", "usr-jiro")
				.orderBy(User.OrderBy.SCREEN_NAME_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).screenName, is("jiro_san"));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// アイテムの検索 Tweet
	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたつぶやきの検索.
	 * Account -> Tweet
	 */
	// =============================================*
	@Test
	public void findTweetsByAccounts_Entity_単数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts(acnt1).fetch();// エンティティ
		assertThat(lst.size(), is(8));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsByAccounts_String_単数_00() {
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts("usr-goro").fetch();// 文字列
		assertThat(lst.size(), is(8));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsByAccounts_Entity_複数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts(acnt1, acnt2).fetch();// エンティティ
		assertThat(lst.size(), is(11));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsByAccounts_String_複数_00() {
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts("usr-goro", "usr-jiro").fetch();// 文字列
		assertThat(lst.size(), is(11));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findTweetsByAccounts_Entity_単数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts(acnt1)
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC).fetch();// エンティティ
		assertThat(lst.size(), is(8));
		assertThat(lst.get(0).serialCode, is("twt-hanako5"));
	}

	@Test
	public void findTweetsByAccounts_String_単数_降順_00() {
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts("usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC).fetch();// 文字列
		assertThat(lst.size(), is(8));
		assertThat(lst.get(0).serialCode, is("twt-hanako5"));
	}

	@Test
	public void findTweetsByAccounts_Entity_複数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts(acnt1, acnt2)
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(11));
		assertThat(lst.get(0).serialCode, is("twt-hanako5"));
	}

	@Test
	public void findTweetsByAccounts_String_複数_降順_00() {
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts("usr-goro", "usr-jiro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(11));
		assertThat(lst.get(0).serialCode, is("twt-hanako5"));
	}

	// -------------------------------------+
	@Test
	public void findTweetsByAccounts_Entity_ラベル指定_単数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts(LabelType.CATEGORY, acnt1).fetch(); // エンティティ
		assertThat(lst.size(), is(1));//同じつぶやきに２つリポスト
	}

	@Test
	public void findTweetsByAccounts_String_ラベル指定_単数_00() {
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts(LabelType.CATEGORY, "usr-goro").fetch(); // 文字列
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findTweetsByAccounts_Entity_ラベル指定_複数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts(LabelType.CATEGORY, acnt1, acnt2).fetch(); // エンティティ
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsByAccounts_String_ラベル指定_複数_00() {
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts(LabelType.CATEGORY, "usr-goro",
						"usr-jiro").fetch(); // 文字列
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findTweetsByAccounts_Entity_ラベル指定_単数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts(LabelType.CATEGORY, acnt1)
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findTweetsByAccounts_String_ラベル指定_単数_降順_00() {
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts(LabelType.CATEGORY, "usr-goro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findTweetsByAccounts_Entity_ラベル指定_複数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Tweet> lst = RepostBase
				.findTweetsByAccounts(LabelType.CATEGORY, acnt1, acnt2)
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	@Test
	public void findTweetsByAccounts_String_ラベル指定_複数_降順_00() {
		List<Tweet> lst = RepostBase.findTweetsByAccounts(
				LabelType.CATEGORY, "usr-goro","usr-jiro")
				.orderBy(Tweet.OrderBy.STATUS_ID_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("twt-jiro1"));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// ラベルの検索 Category
	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたカテゴリーの検索.
	 * Account -> Category
	 */
	// =============================================*
	@Test
	public void findCategoriesByAccounts_Entity_単数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(acnt1).fetch();// エンティティ
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategoriesByAccounts_String_単数_00() {
		List<Category> lst = RepostBase
				.findCategoriesByAccounts("usr-goro").fetch();// 文字列
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategoriesByAccounts_Entity_複数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(acnt1, acnt2).fetch();// エンティティ
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategoriesByAccounts_String_複数_00() {
		List<Category> lst = RepostBase
				.findCategoriesByAccounts("usr-goro", "usr-jiro").fetch();// 文字列
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findCategoriesByAccounts_Entity_単数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(acnt1)
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC).fetch();// エンティティ
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findCategoriesByAccounts_String_単数_降順_00() {
		List<Category> lst = RepostBase
				.findCategoriesByAccounts("usr-goro")
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC).fetch();// 文字列
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findCategoriesByAccounts_Entity_複数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(acnt1, acnt2)
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findCategoriesByAccounts_String_複数_降順_00() {
		List<Category> lst = RepostBase
				.findCategoriesByAccounts("usr-goro", "usr-jiro")
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	// -------------------------------------+
	@Test
	public void findCategoriesByAccounts_Entity_アイテム指定_単数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(ItemType.USER, acnt1).fetch(); // エンティティ
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategoriesByAccounts_String_アイテム指定_単数_00() {
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(ItemType.USER, "usr-goro").fetch(); // 文字列
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategoriesByAccounts_Entity_アイテム指定_複数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(ItemType.USER, acnt1, acnt2).fetch(); // エンティティ
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findCategoriesByAccounts_String_アイテム指定_複数_00() {
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(ItemType.USER, "usr-goro", "usr-jiro")
				.fetch(); // 文字列
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findCategoriesByAccounts_Entity_アイテム指定_単数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(ItemType.USER, acnt1)
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findCategoriesByAccounts_String_アイテム指定_単数_降順_00() {
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(ItemType.USER, "usr-goro")
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findCategoriesByAccounts_Entity_アイテム指定_複数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(ItemType.USER, acnt1, acnt2)
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findCategoriesByAccounts_String_アイテム指定_複数_降順_00() {
		List<Category> lst = RepostBase
				.findCategoriesByAccounts(ItemType.USER, "usr-goro", "usr-jiro")
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// ラベルの検索 Tag
	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたタグの検索.
	 * Account -> Tag
	 */
	// =============================================*
	@Test
	public void findTagsByAccounts_Entity_単数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase
				.findTagsByAccounts(acnt1).fetch();// エンティティ
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByAccounts_String_単数_00() {
		List<Tag> lst = RepostBase
				.findTagsByAccounts("usr-goro").fetch();// 文字列
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByAccounts_Entity_複数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase
				.findTagsByAccounts(acnt1, acnt2).fetch();// エンティティ
		assertThat(lst.size(), is(8));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByAccounts_String_複数_00() {
		List<Tag> lst = RepostBase
				.findTagsByAccounts("usr-goro", "usr-jiro").fetch();// 文字列
		assertThat(lst.size(), is(8));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findTagsByAccounts_Entity_単数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase
				.findTagsByAccounts(acnt1)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC).fetch();// エンティティ
		assertThat(lst.size(), is(5));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTagsByAccounts_String_単数_降順_00() {
		List<Tag> lst = RepostBase
				.findTagsByAccounts("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC).fetch();// 文字列
		assertThat(lst.size(), is(5));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTagsByAccounts_Entity_複数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase
				.findTagsByAccounts(acnt1, acnt2)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(8));
		assertThat(lst.get(0).serialCode, is("tag-jiro-ohayo"));
	}

	@Test
	public void findTagsByAccounts_String_複数_降順_00() {
		List<Tag> lst = RepostBase
				.findTagsByAccounts("usr-goro", "usr-jiro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(8));
		assertThat(lst.get(0).serialCode, is("tag-jiro-ohayo"));
	}

	// -------------------------------------+
	@Test
	public void findTagsByAccounts_Entity_アイテム指定_単数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase
				.findTagsByAccounts(ItemType.USER, acnt1).fetch(); // エンティティ
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByAccounts_String_アイテム指定_単数_00() {
		List<Tag> lst = RepostBase
				.findTagsByAccounts(ItemType.USER, "usr-goro").fetch(); // 文字列
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByAccounts_Entity_アイテム指定_複数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase
				.findTagsByAccounts(ItemType.USER, acnt1, acnt2).fetch(); // エンティティ
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByAccounts_String_アイテム指定_複数_00() {
		List<Tag> lst = RepostBase
				.findTagsByAccounts(ItemType.USER, "usr-goro", "usr-jiro")
				.fetch(); // 文字列
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findTagsByAccounts_Entity_アイテム指定_単数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase
				.findTagsByAccounts(ItemType.USER, acnt1)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTagsByAccounts_String_アイテム指定_単数_降順_00() {
		List<Tag> lst = RepostBase
				.findTagsByAccounts(ItemType.USER, "usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTagsByAccounts_Entity_アイテム指定_複数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase
				.findTagsByAccounts(ItemType.USER, acnt1, acnt2)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC).fetch(); // エンティティ
		assertThat(lst.size(), is(5));
		assertThat(lst.get(0).serialCode, is("tag-jiro-ohayo"));
	}

	@Test
	public void findTagsByAccounts_String_アイテム指定_複数_降順_00() {
		List<Tag> lst = RepostBase
				.findTagsByAccounts(ItemType.USER, "usr-goro", "usr-jiro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC).fetch(); // 文字列
		assertThat(lst.size(), is(5));
		assertThat(lst.get(0).serialCode, is("tag-jiro-ohayo"));
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
