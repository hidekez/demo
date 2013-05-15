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

public class FindItemByLabelTest extends UnitTestBase {

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
	 * 検索ヘルパ・アイテムとラベルのペア検索
	 * Item -> Label
	 * Label -> Item
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * ラッパー
	 * 指定ラベルに関連するアイテムの検索
	 * Label -> Item
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// =============================================*
	/*
	 * ラベルによるアイテムの検索.
	 * Label -> Item
	 */
	// =============================================*
	// -------------------------------------+
	@Test(expected = IllegalArgumentException.class)
	public void findItemsByLabels_不正な引数_例外_01() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		RepostBase.findItemsByLabels(null, LabelType.CATEGORY, cat1).fetch();
	}

	@Test(expected = IllegalArgumentException.class)
	public void findItemsByLabels_不正な引数_例外_02() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		RepostBase.findItemsByLabels(ItemType.USER, null, cat1).fetch();
	}

	@Test(expected = IllegalArgumentException.class)
	public void findItemsByLabels_不正な引数_例外_03() {
		RepostBase.findItemsByLabels(
				ItemType.USER, LabelType.CATEGORY, (Object) null).fetch();
	}

	// -------------------------------------+
	@Test
	public void findItemsByLabels_Entity_単数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY,
						cat1
				)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findItemsByLabels_Entity_複数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY,
						cat1, cat2
				)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findItemsByLabels_String_単数_00() {
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY,
						"cat-biz"
				)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findItemsByLabels_String_複数_00() {
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY,
						"cat-biz", "cat-enta"
				)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findItemsByLabels_Entity_単数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY, cat1
				)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findItemsByLabels_Entity_複数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY,
						cat1, cat2
				)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findItemsByLabels_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY,
						"cat-biz"
				)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findItemsByLabels_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY,
						"cat-biz", "cat-enta"
				)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findItemsByLabels_Entity_単数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY,
						cat1
				)
				.contributor(acnt)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findItemsByLabels_Entity_複数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY,
						cat1, cat2
				)
				.contributor(acnt)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("usr-jiro"));
	}

	@Test
	public void findItemsByLabels_String_単数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY,
						"cat-biz"
				)
				.contributor(acnt)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findItemsByLabels_String_複数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findItemsByLabels(
						ItemType.USER, LabelType.CATEGORY,
						"cat-biz", "cat-enta"
				)
				.contributor(acnt)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("usr-jiro"));
	}

	// =============================================*
	/*
	 * カテゴリーによるリポストされたユーザーの検索.
	 * Category -> User
	 */
	// =============================================*	// -------------------------------------+
	@Test(expected = IllegalArgumentException.class)
	public void findUsersByCategories_不正な引数_例外_01() {
		RepostBase.findUsersByCategories((Object) null).fetch();
	}

	// -------------------------------------+
	@Test
	public void findUsersByCategories_Entity_単数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<ItemBase> lst = RepostBase
				.findUsersByCategories(cat1)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findUsersByCategories_Entity_複数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		List<ItemBase> lst = RepostBase
				.findUsersByCategories(cat1, cat2)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersByCategories_String_単数_00() {
		List<ItemBase> lst = RepostBase
				.findUsersByCategories("cat-biz")
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findUsersByCategories_String_複数_00() {
		List<ItemBase> lst = RepostBase
				.findUsersByCategories("cat-biz", "cat-enta")
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findUsersByCategories_Entity_単数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findUsersByCategories(cat1)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findUsersByCategories_Entity_複数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findUsersByCategories(cat1, cat2)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersByCategories_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findUsersByCategories("cat-biz")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findUsersByCategories_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findUsersByCategories("cat-biz", "cat-enta")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findUsersByCategories_Entity_単数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findUsersByCategories(cat1)
				.contributor(acnt)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findUsersByCategories_Entity_複数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findUsersByCategories(cat1, cat2)
				.contributor(acnt)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("usr-jiro"));
	}

	@Test
	public void findUsersByCategories_String_単数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findUsersByCategories("cat-biz")
				.contributor(acnt)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("usr-goro"));
	}

	@Test
	public void findUsersByCategories_String_複数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<ItemBase> lst = RepostBase
				.findUsersByCategories("cat-biz", "cat-enta")
				.contributor(acnt)
				.orderBy(User.OrderBy.SCREEN_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("usr-jiro"));
	}

	// =============================================*
	/*
	 * タグによるリポストされたユーザーの検索.
	 * Tag -> User
	 */
	// =============================================*
	// findUsersByTags 省略

	// =============================================*
	/*
	 * カテゴリーによるリポストされたつぶやきの検索.
	 * Category -> Tweet
	 */
	// =============================================*
	// findTweetsByTags 省略

	// =============================================*
	/*
	 * タグによるリポストされたつぶやきの検索.
	 * Tag -> Tweet
	 */
	// =============================================*
	// findTweetsByTags 省略

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
