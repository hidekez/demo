package models.reposts;

import org.junit.*;

import java.util.*;

import javax.persistence.Query;

import play.Logger;
import play.db.jpa.JPA;
import play.test.*;
import static org.hamcrest.CoreMatchers.*;

import models.*;
import models.items.*;
import models.labels.*;
import models.reposts.*;
import models.reposts.RepostBase.OrderBy;
import mytests.UnitTestBase;
import constants.*;

/*
 * OrderByの機能追加時の、いろいろ実験.
 * DISTINCTをつけていたことが問題であると気づくまでに
 * 結構な時間がかかった・・・Orz
 *
 * @author H.Kezuka
 */
public class RepostOrderByTrialTest extends UnitTestBase {

	TestModels tm = new TestModels();

	User user;
	Tweet tweet;
	Account account;

	Category category;
	Tag tag;

	@Before
	public void setup() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("test_models_ver20130112.yml");
	}

	private void printRepost(RepostBase _repost) {
		Logger.debug("" + _repost);
		Logger.debug("repost.isPersistent=" + _repost.isPersistent());
	}

	private void printRepost(List<RepostBase> _repos) {
		for (RepostBase repo : _repos) {
			Logger.debug("" + repo);
		}
	}

	@Test
	public void 並び替え指定_JPQL_01() {
		List<RepostBase> reposts = RepostBase.find(
				"SELECT r FROM RepostBase r "
						+ "ORDER BY r.id DESC "
				).fetch();
		printRepost(reposts);
	}

	/*
	 * 実行するとエラーが出る
	 * org.h2.jdbc.JdbcSQLException: order by 対象の式 "TWEET1_.TEXT" は、結果リストに含まれる必要があります
	 */
	@Test
	public void 並び替え指定_JPQL_02_NG1() {
		List<RepostBase> reposts = RepostBase.find(
				"SELECT DISTINCT r FROM RepostTweetCategory r "
						+ "ORDER BY r.tweet.text "
				).fetch();
	}

	// OK DISTINCTが原因だった！
	@Test
	public void 並び替え指定_JPQL_02_2_0() {
		List<RepostBase> reposts = RepostBase.find(
				"SELECT r FROM RepostTweetCategory r "
						+ "ORDER BY r.tweet.text "
				).fetch();
	}

	// OK
	@Test
	public void 並び替え指定_JPQL_02_2_1() {
		List<RepostBase> reposts = RepostBase.find(
				"SELECT r FROM RepostTweetCategory r "
						+ "JOIN r.tweet tw "
						+ "ORDER BY tw.text "
				).fetch();
		printRepost(reposts);
	}

	// OK
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

	/*
	 * 実行するとエラーが出る
	 * org.h2.jdbc.JdbcSQLException: order by 対象の式 "TWEET1_.TEXT" は、結果リストに含まれる必要があります
	 */
//	@Test
//	public void 並び替え指定_JPQL_02_NG2() {
//		List<RepostTweetCategory> reposts = RepostTweetCategory.find(
//				"SELECT DISTINCT r FROM RepostTweetCategory r "
//						+ "ORDER BY r.tweet.text "
//				).fetch();
//	}

	/*
	 * 実行するとエラーが出る
	 * org.h2.jdbc.JdbcSQLException: order by 対象の式 "TWEET1_.TEXT" は、結果リストに含まれる必要があります
	 */
//	@Test
//	public void 並び替え指定_JPQL_02_NG3() {
//		List<RepostTweetCategory> reposts = RepostTweetCategory.find(
//				"SELECT DISTINCT r FROM RepostTweetCategory r "
//						+ "JOIN r.tweet tw "
//						+ "ORDER BY tw.text "
//				).fetch();
//	}

	/*
	 * 実行するとエラーが出る
	 * org.h2.jdbc.JdbcSQLException: order by 対象の式 "TWEET1_.TEXT" は、結果リストに含まれる必要があります
	 */
//	@Test
//	public void 並び替え指定_JPQL_02_NG4() {
//		List<RepostTweetCategory> reposts = RepostTweetCategory
//				.find(
//						"SELECT DISTINCT r.contributor,r.repostedAt,r.tweet,r.category FROM RepostTweetCategory r "
//								+ "JOIN r.tweet tw "
//								+ "ORDER BY tw.text "
//				).fetch();
//	}

	@Test
	public void 並び替え指定_JPQL_022() {
//		List<RepostBase> reposts = RepostBase.find(
		List<String> texts = RepostBase.find(
				"SELECT DISTINCT r.tweet.text FROM RepostTweetCategory r "
						+ "ORDER BY r.tweet.text "
				).fetch();
//		printRepost(reposts);
	}

	@Test
	public void 並び替え指定_JPQL_03() {
		List<RepostBase> reposts = RepostBase.find(
				"ORDER BY tweet.text "
				).fetch();
		printRepost(reposts);
	}

	@Test
	public void 並び替え指定_JPQL_04() {
		Query query = JPA.em().createQuery(
				"SELECT contributor FROM RepostBase as r "
				// + "ORDER BY r.tweet.text "
				);
		List<Account> lst = query.getResultList();
//		printRepost(reposts);
	}

	@Test
	public void 並び替え指定_JPQL_05() {
		Query query = JPA.em().createQuery(
				"SELECT contributor FROM RepostBase as r "
				// + "ORDER BY r.tweet.text "
				);
		List<RepostBase> lst = query.getResultList();
//		printRepost(reposts);
	}

	@Test
	public void 並び替え指定_JPQL_06() {
		Query query = JPA.em().createQuery(
				"SELECT contributor FROM RepostBase as r "
						+ "ORDER BY r.tweet.text "
				);
		List<RepostBase> lst = query.getResultList();
		System.out.println(lst);
	}

//	@Test
//	public void 並び替え指定_JPQL_07() {
//		Query query = JPA
//				.em()
//				.createQuery(
//						"SELECT r.contributor,r.repostedAt,r.tweet,r.category FROM RepostTweetCategory as r "
//								+ "ORDER BY r.tweet.text "
//				);
//		List<RepostTweetCategory> lst = query.getResultList();
//		System.out.println(lst);
//		for (Object obj : lst) {
//			RepostTweetCategory rtc = (RepostTweetCategory) obj;
//			System.out.println(rtc.tweet.text);
//		}
//	}
//	@Test
//	public void 並び替え指定_JPQL_08() {
//		Query query = JPA.em().createNativeQuery(
////				"SELECT * FROM RepostBase AS rb "
////					+ "JOIN tweet AS tw ON rb.tweet_id = tw.id "
//				"SELECT * FROM repostbase LEFT JOIN tweet ON repostbase.tweet_id  = tweet.id "
//				);
//		List<RepostBase> lst = query.getResultList();
//		System.out.println(lst);
//	}
	@Test
	public void 並び替え指定_JPQL_09() {
		Query query = JPA
				.em()
				.createQuery(
						// "SELECT * FROM RepostBase AS rb "
//					+ "JOIN tweet AS tw ON rb.tweet_id = tw.id "
						"SELECT  r.contributor,r.repostedAt,r.tweet,r.category FROM RepostBase AS r "// JOIN
// tweet
				);
		List<RepostBase> lst = query.getResultList();
		System.out.println(lst);
	}

	@Test
	public void 並び替え指定_ラッパー_01() {
		List<Tweet> tweets =
				RepostBase.findRepostedTweets()
						// .orderBy(Tweet.OrderBy.AUTHOR_SCREEN_NAME_ASC)
						.orderBy(Tweet.OrderBy.TEXT_ASC)
						.fetch();
		Logger.debug("tweets.size:" + tweets.size());
	}

	/*
	 * (*)が問題らしい
	 * org.hibernate.MappingException: No Dialect mapping for JDBC type: 16
	 */
//	@Test
//	public void ネイティブ実験_01() {
//		Query query = JPA.em().createNativeQuery(
//				"SELECT * FROM tweet"
//				);
//		List<Tweet> lst = query.getResultList();
//		System.out.println(lst);
////		System.out.println(lst.get(0).getClass().getSimpleName());
//	}

	@Test
	public void ネイティブ実験_02() {
		Query query = JPA.em().createNativeQuery(
				"SELECT id,serialCode,displayName,openlevel,author_id FROM LabelBase"
				);
		List<LabelBase> lst = query.getResultList();
		System.out.println(lst);
	}

	/*
	 * java.lang.ClassCastException
	 */
//	@Test
//	public void ネイティブ実験_02_2() {
//		Query query = JPA.em().createNativeQuery(
//				"SELECT id,serialCode,displayName,openlevel,author_id FROM LabelBase"
//				);
//		List<LabelBase> lst = query.getResultList();
//		for (LabelBase lb : lst) {
//			System.out.println(lb);
//		}
//	}

	/*
	 * java.lang.ClassCastException
	 */
//	@Test
//	public void ネイティブ実験_02_2_2() {
//		Query query = JPA.em().createNativeQuery(
//				"SELECT id,serialCode,displayName,openlevel,author_id FROM LabelBase"
//				);
//		List<LabelBase> lst = (List<LabelBase>) query.getResultList();
//		for (LabelBase lb : lst) {
//			System.out.println(lb);
//		}
//	}

	/*
	 * java.lang.ClassCastException
	 */
//	@Test
//	public void ネイティブ実験_02_3() {
//		Query query = JPA.em().createNativeQuery(
//				"SELECT id,serialCode,displayName,openlevel,author_id FROM LabelBase"
//				);
//		List<LabelBase> lst = (List<LabelBase>) query.getResultList();
//		for (int i = 0; i < lst.size(); i++) {
//			LabelBase lb = (LabelBase) lst.get(i);
//			System.out.println(lb);
//		}
//	}

	/*
	 * java.lang.ClassCastException
	 */
//	@Test
//	public void ネイティブ実験_03() {
//		Query query = JPA.em().createNativeQuery(
//				"SELECT id,enteredat,life,visitedat,loginuser_id FROM account"
//				);
//		List<Account> lst = (List<Account>) query.getResultList();
//		for (int i = 0; i < lst.size(); i++) {
//			Account a = (Account) lst.get(i);
//			System.out.println(a);
//		}
//	}
}

