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

public class FindRepostByLabelTest extends UnitTestBase {

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
	 * 検索ヘルパ・リポストの検索
	 */
	/* ************************************************************ */
	// =============================================*
	/*
	 * ユーザーによるリポストの検索.
	 * User -> Repost
	 */
	// =============================================*
	// 問題なし
	@Test
	public void findRepostByUsers_例外_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByUsers((Object) null).fetch();
		assertThat(lst.size(), is(10));
	}

	// -------------------------------------+
	@Test
	public void findRepostByUsers_Entity_単数_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		List<RepostBase> lst = RepostBase
				.findRepostByUsers(usr1).fetch();
		assertThat(lst.size(), is(4));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByUsers_String_単数_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByUsers("usr-goro").fetch();
		assertThat(lst.size(), is(4));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByUsers_Entity_複数_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		List<RepostBase> lst = RepostBase
				.findRepostByUsers(usr1, usr2).fetch();
		assertThat(lst.size(), is(9));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByUsers_String_複数_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByUsers("usr-goro", "usr-jiro").fetch();
		assertThat(lst.size(), is(9));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findRepostByUsers_Entity_単数_投稿者_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByUsers(usr1)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByUsers_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByUsers("usr-goro")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByUsers_Entity_複数_投稿者_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByUsers(usr1, usr2)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByUsers_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByUsers("usr-goro", "usr-jiro")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findRepostByUsers_Entity_単数_投稿者_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByUsers(usr1)
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).getItem().serialCode, is("usr-goro"));
	}

	@Test
	public void findRepostByUsers_String_単数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByUsers("usr-goro")
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).getItem().serialCode, is("usr-goro"));
	}

	@Test
	public void findRepostByUsers_Entity_複数_投稿者_降順_00() {
		User usr1 = User.findBySerialCode("usr-goro").first();
		User usr2 = User.findBySerialCode("usr-jiro").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByUsers(usr1, usr2)
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(5));
		assertThat(lst.get(0).getItem().serialCode, is("usr-jiro"));
	}

	@Test
	public void findRepostByUsers_String_複数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByUsers("usr-goro", "usr-jiro")
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(5));
		assertThat(lst.get(0).getItem().serialCode, is("usr-jiro"));
	}

	// =============================================*
	/*
	 * つぶやきによるリポストの検索.
	 * Tweet -> Repost
	 */
	// =============================================*
	// 問題なし
	@Test
	public void findRepostByTweets_例外_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByTweets((Object) null).fetch();
		assertThat(lst.size(), is(23));
	}

	// -------------------------------------+
	@Test
	public void findRepostByTweets_Entity_単数_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTweets(twt1).fetch();
		assertThat(lst.size(), is(4));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTweets_String_単数_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByTweets("twt-goro2").fetch();
		assertThat(lst.size(), is(4));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTweets_Entity_複数_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTweets(twt1, twt2).fetch();
		assertThat(lst.size(), is(7));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTweets_String_複数_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByTweets("twt-goro2", "twt-jiro1").fetch();
		assertThat(lst.size(), is(7));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findRepostByTweets_Entity_単数_投稿者_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTweets(twt1)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTweets_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTweets("twt-goro2")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTweets_Entity_複数_投稿者_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTweets(twt1, twt2)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTweets_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTweets("twt-goro2", "twt-jiro1")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findRepostByTweets_Entity_単数_投稿者_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTweets(twt1)
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).getItem().serialCode, is("twt-goro2"));
	}

	@Test
	public void findRepostByTweets_String_単数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTweets("twt-goro2")
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).getItem().serialCode, is("twt-goro2"));
	}

	@Test
	public void findRepostByTweets_Entity_複数_投稿者_降順_00() {
		Tweet twt1 = Tweet.findBySerialCode("twt-goro2").first();
		Tweet twt2 = Tweet.findBySerialCode("twt-jiro1").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTweets(twt1, twt2)
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(5));
		assertThat(lst.get(0).getItem().serialCode, is("twt-jiro1"));
	}

	@Test
	public void findRepostByTweets_String_複数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTweets("twt-goro2", "twt-jiro1")
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(5));
		assertThat(lst.get(0).getItem().serialCode, is("twt-jiro1"));
	}

	// =============================================*
	/*
	 * カテゴリーによるリポストの検索.
	 * Category -> Repost
	 */
	// =============================================*
	// 問題なし
	@Test
	public void findRepostByCategories_例外_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByCategories((Object) null).fetch();
		assertThat(lst.size(), is(10));
	}

	// -------------------------------------+
	@Test
	public void findRepostByCategories_Entity_単数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		List<RepostBase> lst = RepostBase
				.findRepostByCategories(cat1).fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByCategories_String_単数_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByCategories("cat-biz").fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByCategories_Entity_複数_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		List<RepostBase> lst = RepostBase
				.findRepostByCategories(cat1, cat2).fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByCategories_String_複数_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByCategories("cat-biz", "cat-enta").fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findRepostByCategories_Entity_単数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByCategories(cat1)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByCategories_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByCategories("cat-biz")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByCategories_Entity_複数_投稿者_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByCategories(cat1, cat2)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByCategories_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByCategories("cat-biz", "cat-enta")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findRepostByCategories_Entity_単数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByCategories(cat1)
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).getLabel().serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostByCategories_String_単数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByCategories("cat-biz")
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).getLabel().serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostByCategories_Entity_複数_投稿者_降順_00() {
		Category cat1 = Category.findBySerialCode("cat-biz").first();
		Category cat2 = Category.findBySerialCode("cat-enta").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByCategories(cat1, cat2)
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).getLabel().serialCode, is("cat-biz"));
	}

	@Test
	public void findRepostByCategories_String_複数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByCategories("cat-biz", "cat-enta")
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).getLabel().serialCode, is("cat-biz"));
	}

	// =============================================*
	/*
	 * タグによるリポストの検索.
	 * Tag -> Repost
	 */
	// =============================================*
	// 問題なし
	@Test
	public void findRepostByTags_例外_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByTags((Object) null).fetch();
		assertThat(lst.size(), is(23));
	}

	// -------------------------------------+
	@Test
	public void findRepostByTags_Entity_単数_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTags(tag1).fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTags_String_単数_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByTags("tag-goro-red").fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTags_Entity_複数_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-jiro-hello").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTags(tag1, tag2).fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTags_String_複数_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByTags("tag-goro-red", "tag-jiro-hello").fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findRepostByTags_Entity_単数_投稿者_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTags(tag1)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTags_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTags("tag-goro-red")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTags_Entity_複数_投稿者_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-jiro-hello").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTags(tag1, tag2)
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByTags_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTags("tag-goro-red", "tag-jiro-hello")
				.contributor(acnt)
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findRepostByTags_Entity_単数_投稿者_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTags(tag1)
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).getLabel().serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostByTags_String_単数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTags("tag-goro-red")
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).getLabel().serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostByTags_Entity_複数_投稿者_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-jiro-hello").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTags(tag1, tag2)
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).getLabel().serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostByTags_String_複数_投稿者_降順_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByTags("tag-goro-red", "tag-jiro-hello")
				.contributor(acnt)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(3));
		assertThat(lst.get(0).getLabel().serialCode, is("tag-goro-red"));
	}

	// =============================================*
	/*
	 * アカウントによるリポストの検索.
	 * Account -> Repost
	 */
	// =============================================*
	@Test(expected = IllegalArgumentException.class)
	public void findRepostByAccounts_例外_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByAccounts((Object) null).fetch();
	}

	// NGな用法 contributorは呼び出してはいけない
//	@Test(expected = IllegalArgumentException.class)
	@Test(expected = DeprecatedMethodUseException.class)
	public void findRepostByAccounts_例外_01() throws Exception {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByAccounts("usr-goro")
				.contributor(acnt)
				.fetch();
	}

//	@Test(expected = IllegalArgumentException.class)
	@Test(expected = DeprecatedMethodUseException.class)
	public void findRepostByAccounts_例外_02() throws Exception {
		List<RepostBase> lst = RepostBase
				.findRepostByAccounts("usr-goro")
				.contributor("goro_san")
				.fetch();
	}

	// -------------------------------------+
	@Test
	public void findRepostByAccounts_Entity_単数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByAccounts(acnt1).fetch();
		assertThat(lst.size(), is(17));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByAccounts_String_単数_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByAccounts("usr-goro").fetch();
		assertThat(lst.size(), is(17));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByAccounts_Entity_複数_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByAccounts(acnt1, acnt2).fetch();
		assertThat(lst.size(), is(24));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostByAccounts_String_複数_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByAccounts("usr-goro", "usr-jiro").fetch();
		assertThat(lst.size(), is(24));
		// DBからの取得リストの並び保証なし
	}

	// -------------------------------------+
	@Test
	public void findRepostByAccounts_Entity_単数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByAccounts(acnt1)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(17));
		assertThat(lst.get(0).contributor.loginUser.screenName, is("goro_san"));
	}

	@Test
	public void findRepostByAccounts_String_単数_降順_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByAccounts("usr-goro")
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(17));
		assertThat(lst.get(0).contributor.loginUser.screenName, is("goro_san"));
	}

	@Test
	public void findRepostByAccounts_Entity_複数_降順_00() {
		Account acnt1 = Account.findByLoginName("goro_san").first();
		Account acnt2 = Account.findByLoginName("jiro_san").first();
		List<RepostBase> lst = RepostBase
				.findRepostByAccounts(acnt1, acnt2)
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(24));
		assertThat(lst.get(0).contributor.loginUser.screenName, is("goro_san"));
	}

	@Test
	public void findRepostByAccounts_String_複数_降順_00() {
		List<RepostBase> lst = RepostBase
				.findRepostByAccounts("usr-goro", "usr-jiro")
				.orderBy(RepostBase.OrderBy.DATE_OF_REPOST_DESC)
				.fetch();
		assertThat(lst.size(), is(24));
		assertThat(lst.get(0).contributor.loginUser.screenName, is("goro_san"));
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
