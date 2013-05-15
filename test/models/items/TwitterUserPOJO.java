package models.items;

import java.util.Date;
import java.util.List;

import models.items.ItemBase;
import models.items.Tweet;
import models.items.twitter.TwitterAccessToken;

import constants.TwitterCountry;
import constants.TwitterLanguage;
import constants.TwitterTimeZone;

/**
 * Twitterユーザーモデル・テスト用POJOクラス.
 *
 * @author H.Kezuka
 */
public class TwitterUserPOJO {

	// アイテム共通
	public ItemBasePOJO base = new ItemBasePOJO();

	//-------------------------------------+
	// コア
	public long userId;
	public String screenName;
	public String userName;
	public String profileImageUrl;
	public String profileImageUrlHttps;
	// プロフィール
	public TwitterLanguage language;
	public TwitterCountry country;
	public TwitterTimeZone timeZone;
	public Integer utcOffset;
	public String location;
	public String description;
	public String url;
	// 数値
	public int friendsCount;
	public int followersCount;
	public int statusesCount;
	public int favouritesCount;
	public int listedCount;
	// フラグ
	public boolean isFollowing;
	public boolean isProtected;
	public boolean verified;
	// 認証用データ
	public TwitterAccessToken accessToken;
	// リスト
	public List<Tweet> tweets;
}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
