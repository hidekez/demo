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

public class RepostedCategoryTest extends UnitTestBase {

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
	 * あるアイテム種類に関連するラベルのリスト取得
	 * 引数なし：すべて
	 * 引数あり：条件にマッチするもの
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

	@Test
	public void データベースチェック_01() {
		assertThat(RepostBase.count(), is(33L));
		assertThat(RepostUserCategory.count(), is(3L));
		assertThat(RepostTweetCategory.count(), is(7L));
	}

	// ラベルの検索 Category
	// =============================================*
	/*
	 * リポストされたカテゴリーの検索.
	 * Category -> Category
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findRepostedCategories_Category_単数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostedCategories_Category_単数_01() {
		// リポストと関連のないカテゴリー
		Category cat1 = Category.findBySerialCode("cat-no-repo").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1).fetch();
		assertThat(lst.size(), is(0));
//		assertThat(lst.get(0).serialCode, is());
	}

	@Test
	public void findRepostedCategories_Category_複数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1, cat2)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedCategories_Category_複数_01() {
		// リポストと関連のないカテゴリー
		Category cat1 = Category.findBySerialCode("cat-eco").first();
		Category cat2 = Category.findBySerialCode("cat-no-repo").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1, cat2)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-eco"));
	}

	@Test
	public void findRepostedCategories_Category_単数_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1)
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostedCategories_Category_複数_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1, cat2)
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-eco"));// ★
	}

	@Test
	public void findRepostedCategories_Category_単数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostedCategories_Category_単数_投稿者_01() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedCategories_Category_単数_投稿者_02() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostedCategories_Category_複数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1, cat2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedCategories_Category_複数_投稿者_01() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1, cat2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedCategories_Category_複数_投稿者_02() {
		Category cat1 = Category.findBySerialCode("cat-enta").first();
		Category cat2 = Category.findBySerialCode("cat-hello").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1, cat2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedCategories_Category_単数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1)
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostedCategories_Category_複数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		List<Category> lst = RepostBase.findRepostedCategories(cat1, cat2)
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-enta"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findRepostedCategories_String_単数_00() {
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-biz").fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findRepostedCategories_String_単数_01() {
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-no-repo").fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedCategories_String_複数_00() {
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-biz", "cat-enta").fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedCategories_String_複数_01() {
		// リポストと関連のないカテゴリー
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-enta", "cat-no-repo").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-enta"));
	}

	@Test
	public void findRepostedCategories_String_単数_降順_00() {
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-biz")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostedCategories_String_複数_降順_00() {
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-biz", "cat-enta")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-enta"));// ★
	}

	@Test
	public void findRepostedCategories_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-biz")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostedCategories_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findRepostedCategories("cat-biz")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedCategories_String_単数_投稿者_02() {
		List<Category> lst = RepostBase.findRepostedCategories("cat-biz")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostedCategories_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-biz", "cat-enta")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedCategories_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-biz", "cat-enta")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-enta"));
	}

	@Test
	public void findRepostedCategories_String_複数_投稿者_02() {
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-biz", "cat-enta")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedCategories_String_単数_投稿者_降順_00() {
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-biz")
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostedCategories_String_複数_投稿者_降順_00() {
		List<Category> lst = RepostBase
				.findRepostedCategories("cat-biz", "cat-enta")
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-enta"));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findRepostedCategories_NoArg_00() {
		List<Category> lst = RepostBase.findRepostedCategories().fetch();
		assertThat(lst.size(), is(4));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedCategories_NoArg_降順_00() {
		List<Category> lst = RepostBase.findRepostedCategories()
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(4));
		assertThat(lst.get(0).serialCode, is("cat-hello"));
	}

	@Test
	public void findRepostedCategories_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase.findRepostedCategories()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedCategories_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findRepostedCategories()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedCategories_NoArg_投稿者_02() {
		List<Category> lst = RepostBase.findRepostedCategories()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedCategories_NoArg_投稿者_降順_00() {
		List<Category> lst = RepostBase.findRepostedCategories()
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-hello"));
	}

	// =============================================*
	/*
	 * ユーザーに付けされているカテゴリーの検索.
	 * Category -> Category
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findUsersCategories_Category_単数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_Category_単数_01() {
		// リポストと関連のないカテゴリー
		Category cat1 = Category.findBySerialCode("cat-no-repo").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1).fetch();
		assertThat(lst.size(), is(0));
//		assertThat(lst.get(0).serialCode, is());
	}

	@Test
	public void findUsersCategories_Category_複数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1, cat2).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_Category_複数_01() {
		// リポストと関連のないカテゴリー
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-no-repo").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1, cat2).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_Category_単数_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1)
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_Category_複数_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1, cat2)
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-enta"));// ★
	}

	@Test
	public void findUsersCategories_Category_単数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_Category_単数_投稿者_01() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findUsersCategories_Category_単数_投稿者_02() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_Category_複数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1, cat2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_Category_複数_投稿者_01() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1, cat2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findUsersCategories_Category_複数_投稿者_02() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1, cat2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_Category_単数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1)
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_Category_複数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		List<Category> lst = RepostBase.findUsersCategories(cat1, cat2)
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-enta"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findUsersCategories_String_単数_00() {
		List<Category> lst = RepostBase.findUsersCategories("cat-biz").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_String_単数_01() {
		List<Category> lst = RepostBase.findUsersCategories("cat-no-repo")
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findUsersCategories_String_複数_00() {
		List<Category> lst = RepostBase
				.findUsersCategories("cat-biz", "cat-enta").fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersCategories_String_複数_01() {
		// リポストと関連のないカテゴリー
		List<Category> lst = RepostBase.
				findUsersCategories("cat-enta", "cat-no-repo").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-enta"));
	}

	@Test
	public void findUsersCategories_String_単数_降順_00() {
		List<Category> lst = RepostBase.findUsersCategories("cat-biz")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_String_複数_降順_00() {
		List<Category> lst = RepostBase
				.findUsersCategories("cat-biz", "cat-enta")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-enta"));// ★
	}

	@Test
	public void findUsersCategories_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase.findUsersCategories("cat-biz")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findUsersCategories("cat-biz")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findUsersCategories_String_単数_投稿者_02() {
		List<Category> lst = RepostBase.findUsersCategories("cat-biz")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase
				.findUsersCategories("cat-biz", "cat-enta")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersCategories_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase
				.findUsersCategories("cat-biz", "cat-enta")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findUsersCategories_String_複数_投稿者_02() {
		List<Category> lst = RepostBase
				.findUsersCategories("cat-biz", "cat-enta")// 文字列による
				.contributor("usr-goro")
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersCategories_String_単数_投稿者_降順_00() {
		List<Category> lst = RepostBase.findUsersCategories("cat-biz")
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findUsersCategories_String_複数_投稿者_降順_00() {
		List<Category> lst = RepostBase
				.findUsersCategories("cat-biz", "cat-enta")
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-enta"));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findUsersCategories_NoArg_00() {
		List<Category> lst = RepostBase.findUsersCategories().fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersCategories_NoArg_降順_00() {
		List<Category> lst = RepostBase.findUsersCategories()
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-hello"));
	}

	@Test
	public void findUsersCategories_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase.findUsersCategories()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersCategories_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findUsersCategories()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findUsersCategories_NoArg_投稿者_02() {
		List<Category> lst = RepostBase.findUsersCategories()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersCategories_NoArg_投稿者_降順_00() {
		List<Category> lst = RepostBase.findUsersCategories()
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-hello"));
	}

	// =============================================*
	/*
	 * つぶやきに付けされているカテゴリーの検索.
	 * Category -> Category
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findTweetsCategories_Category_単数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_Category_単数_01() {
		// リポストと関連のないカテゴリー
		Category cat1 = Category.findBySerialCode("cat-no-repo").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1).fetch();
		assertThat(lst.size(), is(0));
//		assertThat(lst.get(0).serialCode, is());
	}

	@Test
	public void findTweetsCategories_Category_複数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1, cat2)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsCategories_Category_複数_01() {
		// リポストと関連のないカテゴリー
		Category cat1 = Category.findBySerialCode("cat-eco").first();
		Category cat2 = Category.findBySerialCode("cat-no-repo").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1, cat2)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-eco"));
	}

	@Test
	public void findTweetsCategories_Category_単数_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1)
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_Category_複数_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1, cat2)
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-eco"));// ★
	}

	@Test
	public void findTweetsCategories_Category_単数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_Category_単数_投稿者_01() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTweetsCategories_Category_単数_投稿者_02() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_Category_複数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1, cat2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_Category_複数_投稿者_01() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1, cat2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTweetsCategories_Category_複数_投稿者_02() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-eco").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1, cat2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_Category_単数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1)
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_Category_複数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-hello").first();
		List<Category> lst = RepostBase.findTweetsCategories(cat1, cat2)
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-hello"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findTweetsCategories_String_単数_00() {
		List<Category> lst = RepostBase
				.findTweetsCategories("cat-biz").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_String_単数_01() {
		List<Category> lst = RepostBase
				.findTweetsCategories("cat-no-repo").fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTweetsCategories_String_複数_00() {
		List<Category> lst = RepostBase
				.findTweetsCategories("cat-biz", "cat-enta").fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsCategories_String_複数_01() {
		// リポストと関連のないカテゴリー
		List<Category> lst = RepostBase
				.findTweetsCategories("cat-enta", "cat-no-repo").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-enta"));
	}

	@Test
	public void findTweetsCategories_String_単数_降順_00() {
		List<Category> lst = RepostBase
				.findTweetsCategories("cat-biz")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_String_複数_降順_00() {
		List<Category> lst = RepostBase
				.findTweetsCategories("cat-biz", "cat-enta")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-enta"));// ★
	}

	@Test
	public void findTweetsCategories_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase.findTweetsCategories("cat-biz")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findTweetsCategories("cat-biz")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTweetsCategories_String_単数_投稿者_02() {
		List<Category> lst = RepostBase.findTweetsCategories("cat-biz")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase
				.findTweetsCategories("cat-biz", "cat-enta")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsCategories_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase
				.findTweetsCategories("cat-biz", "cat-enta")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-enta"));
	}

	@Test
	public void findTweetsCategories_String_複数_投稿者_02() {
		List<Category> lst = RepostBase
				.findTweetsCategories("cat-biz", "cat-enta")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsCategories_String_単数_投稿者_降順_00() {
		List<Category> lst = RepostBase.findTweetsCategories("cat-biz")
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findTweetsCategories_String_複数_投稿者_降順_00() {
		List<Category> lst = RepostBase
				.findTweetsCategories("cat-biz", "cat-enta")
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findTweetsCategories_NoArg_00() {
		List<Category> lst = RepostBase.findTweetsCategories().fetch();
		assertThat(lst.size(), is(4));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsCategories_NoArg_降順_00() {
		List<Category> lst = RepostBase.findTweetsCategories()
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(4));
		assertThat(lst.get(0).serialCode, is("cat-hello"));
	}

	@Test
	public void findTweetsCategories_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Category> lst = RepostBase.findTweetsCategories()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsCategories_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Category> lst = RepostBase.findTweetsCategories()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsCategories_NoArg_投稿者_02() {
		List<Category> lst = RepostBase.findTweetsCategories()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsCategories_NoArg_投稿者_降順_00() {
		List<Category> lst = RepostBase.findTweetsCategories()
				.contributor("usr-goro")
				.orderBy(Category.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("cat-hello"));
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
