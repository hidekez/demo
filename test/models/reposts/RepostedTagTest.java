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

public class RepostedTagTest extends UnitTestBase {

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
		assertThat(RepostUserTag.count(), is(7L));
		assertThat(RepostTweetTag.count(), is(16L));
	}

	// ラベルの検索 Tag
	// =============================================*
	/*
	 * リポストされたタグの検索.
	 * Tag -> Tag
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findRepostedTags_Tag_単数_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostedTags_Tag_単数_01() {
		// リポストと関連のないタグ
		Tag tag1 = Tag.findBySerialCode("tag-bob-no-repo").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1).fetch();
		assertThat(lst.size(), is(0));
//		assertThat(lst.get(0).serialCode, is());
	}

	@Test
	public void findRepostedTags_Tag_複数_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-jiro-hello").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1, tag2)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTags_Tag_複数_01() {
		// リポストと関連のないタグ
		Tag tag1 = Tag.findBySerialCode("tag-jiro-hello").first();
		Tag tag2 = Tag.findBySerialCode("tag-bob-no-repo").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1, tag2)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));
	}

	@Test
	public void findRepostedTags_Tag_単数_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostedTags_Tag_複数_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-jiro-hello").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1, tag2)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));// ★
	}

	@Test
	public void findRepostedTags_Tag_単数_投稿者_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostedTags_Tag_単数_投稿者_01() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTags_Tag_単数_投稿者_02() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostedTags_Tag_複数_投稿者_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-hello").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1, tag2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTags_Tag_複数_投稿者_01() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-hello").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1, tag2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTags_Tag_複数_投稿者_02() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-blue").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-gold").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1, tag2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTags_Tag_単数_投稿者_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1)
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostedTags_Tag_複数_投稿者_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-blue").first();
		List<Tag> lst = RepostBase.findRepostedTags(tag1, tag2)
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findRepostedTags_String_単数_00() {
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-goro-red").fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findRepostedTags_String_単数_01() {
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-bob-no-repo").fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTags_String_複数_00() {
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-goro-red", "tag-jiro-hello").fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTags_String_複数_01() {
		// リポストと関連のないタグ
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-jiro-hello", "tag-no-repo").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));
	}

	@Test
	public void findRepostedTags_String_単数_降順_00() {
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-goro-red")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostedTags_String_複数_降順_00() {
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-goro-red", "tag-jiro-hello")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));// ★
	}

	@Test
	public void findRepostedTags_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-goro-red")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostedTags_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findRepostedTags("tag-goro-red")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findRepostedTags_String_単数_投稿者_02() {
		List<Tag> lst = RepostBase.findRepostedTags("tag-goro-red")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostedTags_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-goro-red", "tag-jiro-hello")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostedTags_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-goro-red", "tag-jiro-hello")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));
	}

	@Test
	public void findRepostedTags_String_複数_投稿者_02() {
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-goro-red", "tag-goro-blue")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTags_String_単数_投稿者_降順_00() {
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-goro-red")
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostedTags_String_複数_投稿者_降順_00() {
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-goro-red", "tag-jiro-hello")
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findRepostedTags_String_複数_投稿者_降順_01() {
		List<Tag> lst = RepostBase
				.findRepostedTags("tag-goro-red", "tag-goro-blue")
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findRepostedTags_NoArg_00() {
		List<Tag> lst = RepostBase.findRepostedTags().fetch();
		assertThat(lst.size(), is(12));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTags_NoArg_降順_00() {
		List<Tag> lst = RepostBase.findRepostedTags()
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(12));
		assertThat(lst.get(0).serialCode, is("tag-jiro-ohayo"));
	}

	@Test
	public void findRepostedTags_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase.findRepostedTags()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTags_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findRepostedTags()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(3));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTags_NoArg_投稿者_02() {
		List<Tag> lst = RepostBase.findRepostedTags()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findRepostedTags_NoArg_投稿者_降順_00() {
		List<Tag> lst = RepostBase.findRepostedTags()
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(5));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	// =============================================*
	/*
	 * ユーザーに付けされているタグの検索.
	 * Tag -> Tag
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findUsersTags_Tag_単数_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_Tag_単数_01() {
		// リポストと関連のないタグ
		Tag tag1 = Tag.findBySerialCode("tag-bob-no-repo").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1).fetch();
		assertThat(lst.size(), is(0));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersTags_Tag_複数_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-jiro-hello").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1, tag2).fetch();
		assertThat(lst.size(), is(2));
//		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_Tag_複数_01() {
		// リポストと関連のないタグ
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-bob-no-repo").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1, tag2).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_Tag_複数_02() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-blue").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1, tag2).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_Tag_単数_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_Tag_複数_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-blue").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1, tag2)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));// ★
	}

	@Test
	public void findUsersTags_Tag_単数_投稿者_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_Tag_単数_投稿者_01() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findUsersTags_Tag_単数_投稿者_02() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_Tag_複数_投稿者_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-jiro-goodbye").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1, tag2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_Tag_複数_投稿者_01() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-jiro-goodbye").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1, tag2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-jiro-goodbye"));
	}

	@Test
	public void findUsersTags_Tag_複数_投稿者_02() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-jiro-goodbye").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1, tag2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_Tag_単数_投稿者_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1)
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_Tag_複数_投稿者_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-blue").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1, tag2)
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_Tag_複数_投稿者_降順_01() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-gold").first();
		List<Tag> lst = RepostBase.findUsersTags(tag1, tag2)
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findUsersTags_String_単数_00() {
		List<Tag> lst = RepostBase.findUsersTags("tag-goro-red").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_String_単数_01() {
		List<Tag> lst = RepostBase.findUsersTags("tag-no-repo")
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findUsersTags_String_複数_00() {
		List<Tag> lst = RepostBase
				.findUsersTags("tag-goro-red", "tag-jiro-hello").fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersTags_String_複数_01() {
		// リポストと関連のないタグ
		List<Tag> lst = RepostBase.
				findUsersTags("tag-jiro-hello", "tag-no-repo").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));
	}

	@Test
	public void findUsersTags_String_単数_降順_00() {
		List<Tag> lst = RepostBase.findUsersTags("tag-goro-red")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_String_複数_降順_00() {
		List<Tag> lst = RepostBase
				.findUsersTags("tag-goro-red", "tag-jiro-hello")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));// ★
	}

	@Test
	public void findUsersTags_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase.findUsersTags("tag-goro-red")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findUsersTags("tag-goro-red")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findUsersTags_String_単数_投稿者_02() {
		List<Tag> lst = RepostBase.findUsersTags("tag-goro-red")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase
				.findUsersTags("tag-goro-red", "tag-jiro-hello")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersTags_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase
				.findUsersTags("tag-goro-red", "tag-jiro-hello")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
	}

	@Test
	public void findUsersTags_String_複数_投稿者_02() {
		List<Tag> lst = RepostBase
				.findUsersTags("tag-goro-red", "tag-jiro-hello")// 文字列による
				.contributor("usr-goro")
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersTags_String_複数_投稿者_03() {
		List<Tag> lst = RepostBase
				.findUsersTags("tag-goro-red", "tag-goro-gold")// 文字列による
				.contributor("usr-goro")
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersTags_String_単数_投稿者_降順_00() {
		List<Tag> lst = RepostBase.findUsersTags("tag-goro-red")
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findUsersTags_String_複数_投稿者_降順_00() {
		List<Tag> lst = RepostBase
				.findUsersTags("tag-goro-red", "tag-jiro-hello")
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findUsersTags_NoArg_00() {
		List<Tag> lst = RepostBase.findUsersTags().fetch();
		assertThat(lst.size(), is(7));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersTags_NoArg_降順_00() {
		List<Tag> lst = RepostBase.findUsersTags()
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(7));
		assertThat(lst.get(0).serialCode, is("tag-jiro-ohayo"));
	}

	@Test
	public void findUsersTags_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase.findUsersTags()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersTags_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findUsersTags()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(3));
	}

	@Test
	public void findUsersTags_NoArg_投稿者_02() {
		List<Tag> lst = RepostBase.findUsersTags()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findUsersTags_NoArg_投稿者_降順_00() {
		List<Tag> lst = RepostBase.findUsersTags()
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	// =============================================*
	/*
	 * つぶやきに付けされているタグの検索.
	 * Tag -> Tag
	 */
	// =============================================*
	// 引数：エンティティ
	@Test
	public void findTweetsTags_Tag_単数_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1).fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTweetsTags_Tag_単数_01() {
		// リポストと関連のないタグ
		Tag tag1 = Tag.findBySerialCode("tag-bob-no-repo").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1).fetch();
		assertThat(lst.size(), is(0));
//		assertThat(lst.get(0).serialCode, is());
	}

	@Test
	public void findTweetsTags_Tag_複数_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-jiro-hello").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1, tag2)
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsTags_Tag_複数_01() {
		// リポストと関連のないタグ
		Tag tag1 = Tag.findBySerialCode("tag-jiro-hello").first();
		Tag tag2 = Tag.findBySerialCode("tag-bob-no-repo").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1, tag2)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));
	}

	@Test
	public void findTweetsTags_Tag_単数_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTweetsTags_Tag_複数_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-jiro-hello").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1, tag2)
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));// ★
	}

	@Test
	public void findTweetsTags_Tag_単数_投稿者_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTweetsTags_Tag_単数_投稿者_01() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTweetsTags_Tag_単数_投稿者_02() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTweetsTags_Tag_複数_投稿者_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-blue").first();
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1, tag2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsTags_Tag_複数_投稿者_01() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-blue").first();
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1, tag2)
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTweetsTags_Tag_複数_投稿者_02() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-blue").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1, tag2)
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsTags_Tag_単数_投稿者_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1)
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTweetsTags_Tag_複数_投稿者_降順_00() {
		Tag tag1 = Tag.findBySerialCode("tag-goro-red").first();
		Tag tag2 = Tag.findBySerialCode("tag-goro-gold").first();
		List<Tag> lst = RepostBase.findTweetsTags(tag1, tag2)
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	// -------------------------------------+
	// 引数：文字列
	@Test
	public void findTweetsTags_String_単数_00() {
		List<Tag> lst = RepostBase
				.findTweetsTags("tag-goro-red").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTweetsTags_String_単数_01() {
		List<Tag> lst = RepostBase
				.findTweetsTags("tag-no-repo").fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTweetsTags_String_複数_00() {
		List<Tag> lst = RepostBase
				.findTweetsTags("tag-goro-red", "tag-jiro-hello").fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsTags_String_複数_01() {
		// リポストと関連のないタグ
		List<Tag> lst = RepostBase
				.findTweetsTags("tag-jiro-hello", "tag-no-repo").fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));
	}

	@Test
	public void findTweetsTags_String_単数_降順_00() {
		List<Tag> lst = RepostBase
				.findTweetsTags("tag-goro-red")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTweetsTags_String_複数_降順_00() {
		List<Tag> lst = RepostBase
				.findTweetsTags("tag-goro-red", "tag-jiro-hello")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));// ★
	}

	@Test
	public void findTweetsTags_String_単数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase.findTweetsTags("tag-goro-red")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTweetsTags_String_単数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findTweetsTags("tag-goro-red")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(0));
	}

	@Test
	public void findTweetsTags_String_単数_投稿者_02() {
		List<Tag> lst = RepostBase.findTweetsTags("tag-goro-red")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTweetsTags_String_複数_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase
				.findTweetsTags("tag-goro-red", "tag-jiro-hello")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsTags_String_複数_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase
				.findTweetsTags("tag-goro-red", "tag-jiro-hello")
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-jiro-hello"));
	}

	@Test
	public void findTweetsTags_String_複数_投稿者_02() {
		List<Tag> lst = RepostBase
				.findTweetsTags("tag-goro-red", "tag-jiro-hello")
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(1));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsTags_String_単数_投稿者_降順_00() {
		List<Tag> lst = RepostBase.findTweetsTags("tag-goro-red")
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	@Test
	public void findTweetsTags_String_複数_投稿者_降順_00() {
		List<Tag> lst = RepostBase
				.findTweetsTags("tag-goro-red", "tag-jiro-hello")
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(1));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}
//FIXME
	@Test
	public void findTweetsTags_String_複数_投稿者_降順_01() {
		List<Tag> lst = RepostBase
				.findTweetsTags("tag-goro-red", "tag-goro-gold")
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(2));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

	// -------------------------------------+
	// 引数なし
	@Test
	public void findTweetsTags_NoArg_00() {
		List<Tag> lst = RepostBase.findTweetsTags().fetch();
		assertThat(lst.size(), is(11));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsTags_NoArg_降順_00() {
		List<Tag> lst = RepostBase.findTweetsTags()
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(11));
		assertThat(lst.get(0).serialCode, is("tag-jiro-ohayo"));
	}

	@Test
	public void findTweetsTags_NoArg_投稿者_00() {
		Account acnt = Account.findByLoginName("goro_san").first();
		List<Tag> lst = RepostBase.findTweetsTags()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsTags_NoArg_投稿者_01() {
		Account acnt = Account.findByLoginName("jiro_san").first();
		List<Tag> lst = RepostBase.findTweetsTags()
				.contributor(acnt) // エンティティによる
				.fetch();
		assertThat(lst.size(), is(2));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsTags_NoArg_投稿者_02() {
		List<Tag> lst = RepostBase.findTweetsTags()
				.contributor("usr-goro")// 文字列による
				.fetch();
		assertThat(lst.size(), is(5));
		// DBからの取得リストの並び保証なし
	}

	@Test
	public void findTweetsTags_NoArg_投稿者_降順_00() {
		List<Tag> lst = RepostBase.findTweetsTags()
				.contributor("usr-goro")
				.orderBy(LabelBase.OrderBy.SERIAL_CODE_DESC)
				.fetch();
		assertThat(lst.size(), is(5));
		assertThat(lst.get(0).serialCode, is("tag-goro-red"));
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
