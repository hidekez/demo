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

public class RepostTest extends UnitTestBase {

	TestModels tm = new TestModels();

	User user;
	Tweet tweet;
	Account account;

	Category category;
	Tag tag;

	private RepostBase newInstance() {

		// Playが起動した後にセットしなくてはいけない
//		user = tm.getUser(3);
		tweet = tm.getTweet(3);
		account = tm.getAccount(3);

		category = tm.getCategory(3);
//		tag = tm.getTag(3);

//		System.out.println(user);
//		System.out.println(tweet);
//		System.out.println(account);
//		System.out.println(category);
//		System.out.println(tag);

		return new RepostBase.Builder()
				.contributor(account)
				.item(tweet)
				.label(category)
				.build();
	}

	// =============================================*
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("test_models_ver20130112.yml");
	}

	// =============================================*
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

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void データベースチェック_01() {
		assertThat(RepostBase.count(), is(33L));
	}

	@Test
	public void データベースチェック_02() {

		Fixtures.deleteDatabase();

		RepostBase repost = newInstance();

		printRepost(repost);
		assertThat(RepostTweetCategory.count(), is(1L));
		assertThat(RepostBase.count(), is(1L));

	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void リポストの作成と取得() {

		Fixtures.deleteDatabase();

		RepostBase repost = newInstance();

		Tweet twt = (Tweet) repost.item;
		Category cat = (Category) repost.label;

		printRepost(repost);
		assertThat(RepostBase.count(), is(1L));

		assertThat(repost, is(notNullValue()));
		assertThat(twt.text, is("東京は一日晴れ模様"));
		assertThat(twt.author.screenName, is("goro_san"));
		assertThat(twt.author.userName, is("田中五郎"));
		assertThat(cat.displayName, is("ビジネス"));
		assertThat(repost.contributor, sameInstance(account));

//		assertThat(repost.item.text, is("Hello World"));// item型なので☓
		assertThat(repost.label.displayName, is("ビジネス"));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void つぶやきリストの取得_01() {

		Fixtures.deleteDatabase();

		RepostBase repost = newInstance();
		printRepost(repost);

		List<Tweet> tweets =
				RepostBase.findTweetsByCategories(category).fetch();

		Logger.debug("repost.category.id:" + repost.label.id);
		Logger.debug("category.id:" + category.id);
		assertThat(tweets, is(notNullValue()));
		assertThat(tweets.size(), is(1));
		assertThat(tweets.get(0).text, is("東京は一日晴れ模様"));
	}

	@Test
	public void つぶやきリストの取得_02() {

		Fixtures.deleteDatabase();

		newInstance();

		List<Tweet> tweets = RepostBase
				.findRepostedTweets()
				.contributor(account)
				.fetch();

		assertThat(tweets, is(notNullValue()));
		assertThat(tweets.size(), is(1));
		assertThat(tweets.get(0).text, is("東京は一日晴れ模様"));
	}

	@Test
	public void つぶやきリストの取得_03() {

		Fixtures.deleteDatabase();

		newInstance();
		user = tm.getUser(3);

		List<Tweet> tweets = Tweet.find("author", user).fetch();

		assertThat(tweets, is(notNullValue()));
		assertThat(tweets.size(), is(1));
		assertThat(tweets.get(0).text, is("東京は一日晴れ模様"));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void Fixture読み込みテスト_01() {

		List<RepostBase> repos = RepostBase.findAll();
		printReposts(repos);

		assertThat(RepostBase.count(), is(33L));
	}

	@Test
	public void Fixture読み込みテスト_021() {

		Category cat = Category.find("displayName", "ビジネス").first();
		assertThat(cat, is(notNullValue()));

		List<RepostBase> repos =
				RepostBase.findRepostByCategories(cat).fetch();
		printReposts(repos);

		assertThat(repos.size(), is(2));
	}

	@Test
	public void Fixture読み込みテスト_022() {

		Category cat = Category.find("displayName", "経済").first();
		assertThat(cat, is(notNullValue()));

		List<RepostBase> repos =
				RepostBase.findRepostByCategories(cat).fetch();
		assertThat(repos, is(notNullValue()));
		assertThat(repos.size(), is(2));
		assertThat(repos.get(0), is(notNullValue()));
		assertThat(repos.get(0).contributor.loginUser.screenName,
				is("hana_chan"));
	}

	@Test
	public void Fixture読み込みテスト_023() {

		Category cat = Category.find("displayName", "経済").first();
		List<RepostBase> repos =
				RepostBase.findRepostByCategories(cat).fetch();
		RepostTweetCategory rtc = (RepostTweetCategory) repos.get(0);
//		Logger.debug("rtc:"+rtc);
//		Logger.debug("cat.name:"+rtc.category.name);
		assertThat(rtc.category.displayName, is("経済"));
	}

//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	@Test
//	public void タグ_単体() {
//		// TODO ↓本来２つあるが、おそらくfindUniqulyあたりで１つに絞られている？
////		assertThat(
////				RepostBase.findTweetsByTags("Hello").fetch().size(), is(1));
////		assertThat(
////				RepostBase.findTweetsByTags("赤").fetch().size(), is(2));
////		assertThat(
////				RepostBase.findTweetsByTags("青").fetch().size(), is(2));
////		assertThat(
////				RepostBase.findTweetsByTags("緑").fetch().size(), is(4));
//		printTweets(RepostBase.findTweetsByTags("金").fetch());
//		printTweets(RepostBase.findTweetsByTags("金")
//				.orderBy(Tweet.OrderBy.TEXT_ASC).fetch());
//		assertThat(
//				RepostBase.findTweetsByTags("金").fetch().size(), is(2));
////		assertThat(
////				RepostBase.findTweetsByTags("ピンク").fetch().size(), is(0));
//	}
//
//	@Test
//	public void タグ_単体_02() {
//		Set<Tweet> tweets = new LinkedHashSet<Tweet>(
//				RepostBase.findTweetsByTags("金").fetch());
//		printTweetsSet(tweets);
//	}
//
//	// NG ←DBをH2からMySQLに変えたらエラーでなくなった！
//	@Test
//	public void タグ_単体_03() {
//		List<Tweet> tweets = RepostBase.find(
//				"SELECT DISTINCT r.tweet FROM RepostTweetTag r "
//						+ "WHERE r.tag.displayName = :args "
//						+ "ORDER BY r.tweet.text ASC"
//				).bind("args", "金")
//				.fetch();
//		printTweets(tweets);
//	}
//
//	@Test
//	public void タグ_単体_03_2() {
//		List<Tweet> tweets = RepostBase
//				.find(
//						"SELECT DISTINCT tw FROM RepostTweetTag r "
//								+ "JOIN r.tweet tw "
//								+ "WHERE r.tag.displayName = :args "
//								+ "ORDER BY r.tweet.text ASC"
//				).bind("args", "金")
//				.fetch();
//		printTweets(tweets);
//	}
//
////	@Test
////	public void タグ_単体_03() {
////		List<Tweet> tweets = RepostTweetTag.find(
////						"SELECT DISTINCT r FROM RepostTweetTag r "
////						+"JOIN r.tweet tw "
////						+"WHERE r.tag.displayName = "
////						).fetch();
////		printTweetsSet(tweets);
////	}
//
//	@Test
//	public void タグ_複数() {
//
//		// 現状はOR状態
//		Logger.debug(""
//				+ RepostBase.findTweetsByTags("Hello", "赤").fetch().size());
//		Logger.debug("" + RepostBase.findTweetsByTags("赤", "青").fetch().size());
//		Logger.debug("" + RepostBase.findTweetsByTags("赤", "緑").fetch().size());
//		Logger.debug("" + RepostBase.findTweetsByTags("赤", "青", "緑").fetch()
//				.size());
//		Logger.debug(""
//				+ RepostBase.findTweetsByTags("赤", "ピンク").fetch().size());
//
//		assertThat(RepostBase.findTweetsByTags("Hello", "赤").fetch().size(),
//				is(2));
//		assertThat(RepostBase.findTweetsByTags("赤", "青").fetch().size(),
//				is(4));
//		assertThat(RepostBase.findTweetsByTags("赤", "緑").fetch().size(),
//				is(6));
//		assertThat(RepostBase.findTweetsByTags("赤", "青", "緑").fetch().size(),
//				is(7));
//		assertThat(RepostBase.findTweetsByTags("赤", "ピンク").fetch().size(),
//				is(2));
//	}
//
////DISTINCTなし→NG Helloと赤とが同じつぶやきについているため
//	@Test
//	public void タグ_複数_01() {
//
//		// 現状はOR状態
//		List<Tweet> list = RepostBase.findTweetsByTags("Hello", "赤").fetch();
//		Logger.debug("" + list.size());
//		assertThat(list.size(), is(2));
//	}
//
////DISTINCTなし→OK
//	@Test
//	public void タグ_複数_02() {
//
//		// 現状はOR状態
//		Logger.debug("" + RepostBase.findTweetsByTags("赤", "青").fetch().size());
//
//		assertThat(RepostBase.findTweetsByTags("赤", "青").fetch().size(),
//				is(4));
//	}
//
////DISTINCTなし→OK
//	@Test
//	public void タグ_複数_03() {
//
//		// 現状はOR状態
//		Logger.debug("" + RepostBase.findTweetsByTags("赤", "緑").fetch().size());
//
//		assertThat(RepostBase.findTweetsByTags("赤", "緑").fetch().size(),
//				is(6));
//	}
//
//// DISTINCTなし→NG 青と緑が同じつぶやきについているため
//	@Test
//	public void タグ_複数_04() {
//
//		// 現状はOR状態
//		Logger.debug("" + RepostBase.findTweetsByTags("赤", "青", "緑").fetch()
//				.size());
//
//		assertThat(RepostBase.findTweetsByTags("赤", "青", "緑").fetch().size(),
//				is(7));
//	}
//
////DISTINCTなし→OK
//	@Test
//	public void タグ_複数_05() {
//
//		// 現状はOR状態
//		Logger.debug(""
//				+ RepostBase.findTweetsByTags("赤", "ピンク").fetch().size());
//
//		assertThat(RepostBase.findTweetsByTags("赤", "ピンク").fetch().size(),
//				is(2));
//	}
//
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void タグ_単体() {

		assertThat(RepostBase.findTweetsByTags("tag-goro-hello")
				.fetch().size(), is(1));
		assertThat(RepostBase.findTweetsByTags("tag-goro-red")
				.fetch().size(), is(2));
		assertThat(RepostBase.findTweetsByTags("tag-goro-blue")
				.fetch().size(), is(2));
		assertThat(RepostBase.findTweetsByTags("tag-goro-green")
				.fetch().size(), is(4));
		assertThat(RepostBase.findTweetsByTags("tag-goro-gold")
				.fetch().size(), is(1));
		assertThat(RepostBase.findTweetsByTags("tag-no-pink")
				.fetch().size(), is(0));

//		printTweets(RepostBase.findTweetsByTags("tag-goro-gold").fetch());
//		printTweets(RepostBase.findTweetsByTags("tag-goro-gold")
//				.orderBy(Tweet.OrderBy.TEXT_ASC).fetch());
	}

	@Test
	public void タグ_単体_02() {
		Set<Tweet> tweets = new LinkedHashSet<Tweet>(
				RepostBase.findTweetsByTags("tag-goro-gold").fetch());
		printTweetsSet(tweets);
	}

	// NG ←DBをH2からMySQLに変えたらエラーでなくなった！
	@Test
	public void タグ_単体_03() {
		List<Tweet> tweets = RepostBase.find(
				"SELECT DISTINCT r.tweet FROM RepostTweetTag r "
						+ "WHERE r.tag.displayName = :args "
						+ "ORDER BY r.tweet.text ASC"
				).bind("args", "金")
				.fetch();
		printTweets(tweets);
	}

	@Test
	public void タグ_単体_03_2() {
		List<Tweet> tweets = RepostBase
				.find(
						"SELECT DISTINCT tw FROM RepostTweetTag r "
								+ "JOIN r.tweet tw "
								+ "WHERE r.tag.displayName = :args "
								+ "ORDER BY r.tweet.text ASC"
				).bind("args", "金")
				.fetch();
		printTweets(tweets);
	}

	@Test
	public void タグ_複数() {

		// 現状はOR状態
		Logger.debug("" + RepostBase.findTweetsByTags(
				"tag-goro-hello", "tag-goro-red").fetch().size());
		Logger.debug("" + RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-goro-blue").fetch().size());
		Logger.debug("" + RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-goro-green").fetch().size());
		Logger.debug(""
				+ RepostBase.findTweetsByTags(
						"tag-goro-red", "tag-goro-blue", "tag-goro-green")
						.fetch().size());
		Logger.debug("" + RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-no-pink").fetch().size());

		assertThat(RepostBase.findTweetsByTags(
				"tag-goro-hello", "tag-goro-red").fetch().size(), is(2));
		assertThat(RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-goro-blue").fetch().size(), is(4));
		assertThat(RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-goro-green").fetch().size(), is(6));
		assertThat(RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-goro-blue", "tag-goro-green").fetch()
				.size(), is(7));
		assertThat(RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-no-pink").fetch().size(), is(2));
	}

//DISTINCTなし→NG tag-goro-helloと赤とが同じつぶやきについているため
	@Test
	public void タグ_複数_01() {

		// 現状はOR状態
		List<Tweet> list = RepostBase.findTweetsByTags(
				"tag-goro-hello", "tag-goro-red").fetch();
		Logger.debug("" + list.size());
		assertThat(list.size(), is(2));
	}

//DISTINCTなし→OK
	@Test
	public void タグ_複数_02() {

		// 現状はOR状態
		Logger.debug("" + RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-goro-blue").fetch().size());

		assertThat(RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-goro-blue").fetch().size(), is(4));
	}

//DISTINCTなし→OK
	@Test
	public void タグ_複数_03() {

		// 現状はOR状態
		Logger.debug("" + RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-goro-green").fetch().size());

		assertThat(RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-goro-green").fetch().size(), is(6));
	}

// DISTINCTなし→NG tag-goro-blueとtag-goro-greenが同じつぶやきについているため
	@Test
	public void タグ_複数_04() {

		// 現状はOR状態
		Logger.debug("" + RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-goro-blue", "tag-goro-green").fetch()
				.size());

		assertThat(RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-goro-blue", "tag-goro-green").fetch()
				.size(),
				is(7));
	}

//DISTINCTなし→OK
	@Test
	public void タグ_複数_05() {

		// 現状はOR状態
		Logger.debug("" + RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-no-pink").fetch().size());

		assertThat(RepostBase.findTweetsByTags(
				"tag-goro-red", "tag-no-pink").fetch().size(),
				is(2));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void 横断的なアイテム検索_Playクエリ_01() {

		category = Category.find("displayName", "ビジネス").first();
		assertThat(category, notNullValue());
		Logger.debug("category:%s", category);

		List<RepostBase> reposts =
				RepostBase.find("category", category).fetch();

		printReposts(reposts);
		for (RepostBase repo : reposts) {
			Logger.debug("item:" + repo.getItem());
		}

		assertThat(reposts.size(), is(2));
	}

	@Test
	public void 横断的なアイテム検索_Playクエリ_02() {

		category = Category.find("displayName", "ビジネス").first();
		account = Account.find("loginUser.screenName", "goro_san").first();

//		List<RepostBase> reposts = RepostBase
//				.find("ByCategoryAndContributor", category, account).fetch();
		List<RepostBase> reposts = RepostBase
				.find("SELECT DISTINCT r FROM RepostBase r "
						+ "WHERE r.category = :cat "
						+ "AND r.contributor = :acnt")
				.bind("cat", category)
				.bind("acnt", account)
				.fetch();

		printReposts(reposts);
		assertThat(reposts.size(), is(2));
	}

	@Test
	public void 横断的なアイテム検索_ラッパー_01() {
//		List<ItemBase> items =
//				RepostBase.findItemsByLabels(ItemType, _labelType, _primaryArgs)
		List<RepostBase> reposts =
				RepostBase.findRepostByCategories("cat-biz").fetch();

		printReposts(reposts);
		assertThat(reposts.size(), is(2));
	}

	@Test
	public void 横断的なアイテム検索_ラッパー_02() {
		List<RepostBase> reposts =RepostBase
						.findRepostByCategories("cat-biz")
						.contributor("usr-goro")
						.fetch();

		printReposts(reposts);
		assertThat(reposts.size(), is(2));
	}

	@Test
	public void 横断的なラベル検索_Playクエリ_01() {

		user = User.find("screenName", "goro_san").first();
		assertThat(user, notNullValue());
		Logger.debug("user:%s", user);

		// 田中五郎につけられた全種類合わせたラベル数
		List<RepostBase> reposts =
				RepostBase.find("user", user).fetch();

		printReposts(reposts);
		printRepostLabel(reposts);

		assertThat(reposts.size(), is(4));
	}

	// user指定の時点で一意になってるに決まってる。
	// 設定が変なテスト
	@Test
	public void 横断的なラベル検索_Playクエリ_02() {

		user = User.find("screenName", "goro_san").first();
		category = Category.find("displayName", "ビジネス").first();

		List<RepostBase> reposts =
				RepostBase.find("byUserAndCategory", user, category).fetch();

		printReposts(reposts);
		printRepostLabel(reposts);

		assertThat(reposts.size(), is(1));
	}

	@Test
	public void 横断的なラベル検索_ラッパー01() {

		List<RepostBase> reposts =
				RepostBase.findRepostByCategories("cat-biz").fetch();

		printReposts(reposts);
		printRepostLabel(reposts);

		assertThat(reposts.size(), is(2));
	}

	@Test
	public void 横断的なラベル検索_ラッパー02() {

		category = Category.find("displayName", "ビジネス").first();
		account = Account.find("loginUser.screenName", "goro_san").first();

		List<RepostBase> reposts =
				RepostBase.findRepostByCategories(category)
						.contributor(account)
						.fetch();

		printReposts(reposts);
		printRepostLabel(reposts);

		assertThat(reposts.size(), is(2));
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Test
	public void 並び替え指定_JPQL_01() {
		List<RepostBase> reposts = RepostBase.find(
				"SELECT r FROM RepostBase r "
						+ "ORDER BY r.id DESC "
				).fetch();
		printReposts(reposts);
	}

	@Test
	public void 並び替え指定_JPQL_02_2_1() {
		List<RepostBase> reposts = RepostBase.find(
				"SELECT r FROM RepostTweetCategory r "
						+ "JOIN r.tweet tw "
						+ "ORDER BY tw.text "
				).fetch();
		printReposts(reposts);
	}

	@Test
	public void 並び替え指定_JPQL_02_2_2() {
		List<RepostBase> reposts = RepostBase.find(
				"SELECT r FROM RepostTweetCategory r "
						+ "JOIN r.tweet tw "
						+ "WHERE tw.text LIKE 'Hello%' "
						+ "ORDER BY tw.text "
				).fetch();
		Logger.debug("reposts.size:" + reposts.size());
	}

	@Test
	public void 並び替え指定_ラッパー_01() {
		List<Tweet> tweets =
				RepostBase.findRepostedTweets()
						.orderBy(Tweet.OrderBy.TEXT_ASC)
						.fetch();
		Logger.debug("tweets.size:" + tweets.size());
		Logger.debug("tweets.get(0).text:%s", tweets.get(0).text);// everything gonna be alright.
		Logger.debug("tweets.get(1).text:%s", tweets.get(1).text);// Hello world!
//		printTweets(tweets);

		/*
		 * より小さい数が前になる（昇順）
		 * 文字列ならUnicodeで小さい値の方が前。
		 * 式：this - other
		 */
//		System.out.println("everything".compareTo("Hello"));// 29
//		System.out.println("e".compareTo("H"));// 29
//		System.out.println("e".compareTo("h"));// -3
//		System.out.println("1".compareTo("2"));// -1

		/*
		 * ↓この検査はやめたほうがよいみたい。
		 * DBのorder by とJavaの並び替えとではアルゴリズムが違うから
		 */
//		assertThat(tweets.get(0).text.compareTo(tweets.get(1).text),
//				is(greaterThan(0)));

		assertThat(tweets.get(0).text, is("everything gonna be alright."));
	}

	@Test
	public void 並び替え指定_ラッパー_02() {
		List<Tweet> tweets =
				RepostBase.findRepostedTweets()
						.orderBy(Tweet.OrderBy.TEXT_DESC)
						.fetch();

		Logger.debug("tweets.get(0).text:%s", tweets.get(0).text);// everything gonna be alright.
		Logger.debug("tweets.get(1).text:%s", tweets.get(1).text);// Hello world!
		/*
		 * ↓この検査はやめたほうがよいみたい。
		 * DBのorder by とJavaの並び替えとではアルゴリズムが違うから
		 */
//		 assertThat(tweets.get(0).text.compareTo(tweets.get(1).text),
//				is(greaterThan(0)));

		assertThat(tweets.get(0).text, is("花子とか━━"));
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
