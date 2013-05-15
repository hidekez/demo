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

public class FindLabelByItemTest extends UnitTestBase {

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
	 * 指定アイテムに関連するラベルの検索
	 * Item -> Label
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// =============================================*
	/*
	 * アイテムによるラベルの検索.
	 * Item -> Label
	 */
	// =============================================*
	// -------------------------------------+
	@Test(expected = IllegalArgumentException.class)
	public void findLabelsByItems_不正な引数_例外_02() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		RepostBase.findLabelsByItems(null, ItemType.USER, usr1).fetch();
	}

	@Test(expected = IllegalArgumentException.class)
	public void findLabelsByItems_不正な引数_例外_01() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		RepostBase.findLabelsByItems(LabelType.CATEGORY, null, usr1).fetch();
	}

	@Test(expected = IllegalArgumentException.class)
	public void findLabelsByItems_不正な引数_例外_03() {
		RepostBase.findLabelsByItems(
				LabelType.CATEGORY, ItemType.USER, (Object) null).fetch();
	}

	// -------------------------------------+
	@Test
	public void findLabelsByItems_Entity_単数_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						usr1
				)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findLabelsByItems_Entity_複数_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						usr1, usr2
				)
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findLabelsByItems_String_単数_00() {
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						"usr-goro"
				)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findLabelsByItems_String_複数_00() {
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						"usr-goro", "usr-jiro"
				)
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findLabelsByItems_Entity_単数_投稿者_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						usr1
				)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findLabelsByItems_Entity_複数_投稿者_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						usr1, usr2
				)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findLabelsByItems_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						"usr-goro"
				)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findLabelsByItems_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						"usr-goro", "usr-jiro"
				)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findLabelsByItems_Entity_単数_投稿者_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						usr1
				)
				.contributor(acnt)
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findLabelsByItems_Entity_複数_投稿者_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						usr1, usr2
				)
				.contributor(acnt)
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findLabelsByItems_String_単数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						"usr-goro"
				)
				.contributor(acnt)
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}

	@Test
	public void findLabelsByItems_String_複数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findLabelsByItems(
						LabelType.CATEGORY, ItemType.USER,
						"usr-goro", "usr-jiro"
				)
				.contributor(acnt)
				.orderBy(Category.OrderBy.DISPLAY_NAME_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).serialCode, is("cat-biz"));
	}
	// =============================================*
	/*
	 * ユーザーによるリポストされたカテゴリーの検索.
	 * User -> Category
	 */
	// =============================================*
	// findCategoriesByUsers 省略

	// =============================================*
	/*
	 * つぶやきによるリポストされたカテゴリーの検索.
	 * Tweet -> Category
	 */
	// =============================================*
	// findCategoriesByTweets 省略

	// =============================================*
	/*
	 * つぶやきによるリポストされたタグの検索.
	 * User -> Tag
	 */
	// =============================================*
	// findTagsByUsers 省略

	// =============================================*
	/*
	 * つぶやきによるリポストされたタグの検索.
	 * Tweet -> Tag
	 */
	// =============================================*
	// =============================================*
	/*
	 * つぶやきによるリポストされたタグの検索.
	 * Tweet -> Tag
	 */
	// =============================================*
	// -------------------------------------+
	@Test(expected = IllegalArgumentException.class)
	public void findTagsByTweets_不正な引数_例外_03() {
		RepostBase.findTagsByTweets((Object) null).fetch();
	}

	// -------------------------------------+
	@Test
	public void findTagsByTweets_Entity_単数_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		List<LabelBase> lst = RepostBase
				.findTagsByTweets(twt1)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByTweets_Entity_複数_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-hanako3").first();
		List<LabelBase> lst = RepostBase
				.findTagsByTweets(twt1, twt2)
				.fetch();
		assertThat(lst.size(), is(4));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByTweets_String_単数_00() {
		List<LabelBase> lst = RepostBase
				.findTagsByTweets("twt-goro2")
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByTweets_String_複数_00() {
		List<LabelBase> lst = RepostBase
				.findTagsByTweets("twt-goro2", "twt-hanako3")
				.fetch();
		assertThat(lst.size(), is(4));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findTagsByTweets_Entity_単数_投稿者_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findTagsByTweets(twt1)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByTweets_Entity_複数_投稿者_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-hanako3").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findTagsByTweets(twt1, twt2)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));//アカウント縛りによる減少
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByTweets_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findTagsByTweets("twt-goro2")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTagsByTweets_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findTagsByTweets("twt-goro2", "twt-hanako3")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));//アカウント縛りによる減少
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findTagsByTweets_Entity_単数_投稿者_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findTagsByTweets(twt1)
				.contributor(acnt)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTagsByTweets_Entity_複数_投稿者_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-hanako3").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findTagsByTweets(twt1, twt2)
				.contributor(acnt)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));//アカウント縛りによる減少
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTagsByTweets_String_単数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findTagsByTweets("twt-goro2")
				.contributor(acnt)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTagsByTweets_String_複数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<LabelBase> lst = RepostBase
				.findTagsByTweets("twt-goro2", "twt-hanako3")
				.contributor(acnt)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));//アカウント縛りによる減少
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
