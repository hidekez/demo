package models.items;

import java.util.Date;
import java.util.List;

import constants.TwitterCountry;
import constants.TwitterLanguage;

import models.items.Tweet;
import models.items.User;
import models.items.multi.Geo;
import models.items.twitter.InReplyTo;

/**
 * Twitter投稿(つぶやき)モデル・テスト用POJOクラス.
 *
 * @author H.Kezuka
 */
public class TwitterTweetPOJO {

	// アイテム共通
	public ItemBasePOJO base = new ItemBasePOJO();

	//-------------------------------------+
	// コア
	public long statusId;
	public String text;
	public User author;
	public TwitterLanguage language;
	public TwitterCountry country;
	// フラグ
	public boolean isInReply;
	public boolean hasGeo;
	// キャッシュ
	public InReplyTo inReplyTo = new InReplyTo();
	public Geo geo = new Geo();
	// リスト
	public List<Tweet> mentions;
}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
