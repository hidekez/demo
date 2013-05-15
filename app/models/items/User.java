/*
 * 作成日 2011/09/27
 * 修正日 2012/02/23 命名ルールの変更による
 * 修正日 2012/03/08 toStringの{}追加
 * 修正日 2012/04/05 構造改編 TweetUset->User
 * 修正日 2012/04/10 構造変更による（パッケージなど修正）
 * 修正日 2012/04/12 Tweet->Post、つぶやき->投稿(つぶやき)
 * 修正日 2012/04/24 toString,toMap,toJsonの削除、@Hide導入
 * 修正日 2012/04/28 TwitterPost->TwitterTweet
 * 修正日 2012/06/05 コメントの変更（WZアウトライン用記号の削除）
 * 修正日 2012/09/12 ->extends AbstractOriginalItem implements IStandardUser
 * 修正日 2012/09/13 ->extends AbstractOriginalUser implements Comparable<>
 * 修正日 2013/01/01 リポスト試作からの取り入れ
 * 修正日 2013/01/11 JPAマージヘルパ追加
 * 修正日 2013/01/22 検索関連でクラス・フィールド情報ヘルパ整理
 * 修正日 2013/05/02 toString関連でNullPointerExceptionでるの修正
 */

package models.items;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.validation.InFuture;
import play.data.validation.InPast;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.Range;
import play.data.validation.Required;
import play.db.jpa.GenericModel.JPAQuery;
//import play.data.validation.URL;

import validation.MyURL;
import validation.TwitterScreenName;
import validation.TwitterUserName;
import validation.URI;

import constants.ItemStateType;
import constants.ServiceType;
import constants.TwitterCountry;
import constants.TwitterLanguage;
import constants.TwitterTimeZone;
import utils.MyDateUtils;
import utils.MyModelUtils;
import utils.MyStringUtils;

import models.Account;
import models.IOrderBy;
import models.items.multi.UserName;
import models.items.twitter.TwitterAccessToken;
import models.labels.*;
import models.reposts.*;
import models.reposts.RepostBase.FindCondition;

/**
 * ユーザーモデル.
 *
 * @author H.Kezuka
 */
@Entity
public class User extends ItemBase implements IRepostElement {

    // =============================================*
    // 定数
    // =============================================*
    public static final ItemType ITEM_TYPE = ItemType.USER;

    // =============================================*
    // コア
    // =============================================*
    /**
     * ユーザID(id).
     */
    @Required
    @Min(USER_ID_MIN)
    public long userId;

    /**
     * スクリーン名(screen_name).
     */
    // ↓@Required含む(nullはNG)
    @TwitterScreenName
    public String screenName;

    /**
     * ユーザの名前(name).
     */
    // ↓@Required含む(nullはNG)
    @TwitterUserName
    public String userName;

    /**
     * プロフィールアイコンURL(profile_image_url).
     */
    @Required
    @MyURL
    public String profileImageUrl;

    /**
     * プロフィールアイコンURL(profile_image_url_https).
     */
    @MyURL(message = "validation.uri.schemes", schemes = {"https"})
    public String profileImageUrlHttps;

    // =============================================*
    // プロフィール
    // =============================================*
// 親クラスで定義
//	/**
//	 * Twitter入会日時(created_at).
//	 */
//	@Required
//	@InFuture(LIMIT_OF_TWEET_YMD_STR)
//	@InPast
//	private Date createdAt;

    /**
     * 言語.
     */
    @Enumerated(EnumType.STRING)
    private TwitterLanguage language;

    /**
     * 地域（国）.
     */
    @Enumerated(EnumType.STRING)
    private TwitterCountry country;

    /**
     * タイムゾーン(time_zone).
     */
    @Enumerated(EnumType.STRING)
    private TwitterTimeZone timeZone;

    /**
     * UTCとの差(utc_offset).
     * (単位：秒)<br>
     * "null"というデータがあるので、intではなくIntegerで持つ。
     */
    @Range(min = -UTC_OFFSET_RANGE, max = UTC_OFFSET_RANGE)
    public Integer utcOffset;

    /**
     * ユーザの居住地(location).
     */
    @MaxSize(LOCATION_MAX_LENGTH)
    public String location;

    /**
     * ユーザの自己紹介(description).
     */
    @MaxSize(DESCRIPTION_MAX_LENGTH)
    public String description;

    /**
     * ユーザのWebページURL(url).
     */
    @MyURL(nullable = true)
    public String url;

    // =============================================*
    // 数値
    // =============================================*
    /**
     * friend数(friends_count).
     */
    @Min(COUNT_MIN)
    public int friendsCount;

    /**
     * follower数(followers_count).
     */
    @Min(COUNT_MIN)
    public int followersCount;

    /**
     * つぶやき数(statuses_count).
     */
    @Min(COUNT_MIN)
    public int statusesCount;

    /**
     * favourite数(favourites_count).
     * （米語：favorite）
     */
    @Min(COUNT_MIN)
    public int favouritesCount;

    /**
     * 被list数(listed_count).
     */
    @Min(COUNT_MIN)
    public int listedCount;

    // =============================================*
    // フラグ
    // =============================================*
    /**
     * following中か否か(following).
     */
    public boolean isFollowing;

    /**
     * プロフィールをprotectしているか否か(protected).
     */
    public boolean isProtected;

    /**
     * 認証済みか否か(verified).
     * Twitter側から受け取る情報の中に含まれているデータとして。
     */
    public boolean verified;

    /**
     * 認証済みか否か
     */
    // NOTE オブジェクトのBooleanになってる？
    public Boolean authenticated;

	/*
     * IMを使っているか否か(notifications).
	 */
    // public boolean fNotifications;

    // geo_enabled
    // is_translator
    // contributors_enabled
    // follow_request_sent
    // show_all_inline_media
    // default_profile
    // default_profile_image

    // =============================================*
    // デザイン
    // =============================================*
	/*
	 * <profile_background_color>プロフィールの背景の色
	 * <profile_background_image_url>背景の画像のURL
	 * <profile_background_image_url_https>背景の画像のURL
	 * <profile_background_tile>背景の画像をタイリングするか否か
	 * <profile_use_background_image>背景の画像を使用するか否か
	 * <profile_text_color>プロフィールのテキストの色 <profile_link_color>プロフィールのリンクの色
	 * <profile_sidebar_border_color>サイドバーの border の色
	 * <profile_sidebar_fill_color>サイドバーの背景の色
	 */

    // =============================================*
    // 認証用データ
    // =============================================*
    @Embedded
    public TwitterAccessToken accessToken;

    // =============================================*
    // コレクション
    // =============================================*
    /**
     * 投稿(つぶやき)リスト.
     * 仮保存用(Transient)
     */
    // TODO キャッシュではなく、双方向設定にすべきでは？
    @Transient
//	@OneToMany(mappedBy="author")
    public List<Tweet> tweets;

	/* ************************************************************ */
	/*
	 * コンストラクタ
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

    /**
     * コンストラクタ ビルダー版.
     */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
    public User(Builder _builder) {

        super(_builder);

        // -------------------------------------+
        // コア
        this.userId = _builder.userId;
        this.screenName = _builder.screenName;
        this.userName = _builder.userName;
        this.profileImageUrl = _builder.profileImageUrl;
        this.profileImageUrlHttps = _builder.profileImageUrlHttps;
        // -------------------------------------+
        // プロフィール
        this.language = _builder.language;
        this.country = _builder.country;
        this.timeZone = _builder.timeZone;
        this.utcOffset = _builder.utcOffset;
        this.location = _builder.location;
        this.description = _builder.description;
        this.url = _builder.url;
        // -------------------------------------+
        // 数値
        this.friendsCount = _builder.friendsCount;
        this.followersCount = _builder.followersCount;
        this.statusesCount = _builder.statusesCount;
        this.favouritesCount = _builder.favouritesCount;
        this.listedCount = _builder.listedCount;
        // -------------------------------------+
        // フラグ
        this.isFollowing = _builder.isFollowing;
        this.isProtected = _builder.isProtected;
        this.verified = _builder.verified;
        this.authenticated = _builder.authenticated;
        // -------------------------------------+
        // 認証用データ
        this.accessToken = _builder.accessToken;
        // -------------------------------------+
        this.isNew = _builder.isNew;

    }

	/* ************************************************************ */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

    /**
     * ビルダー(インナークラス)
     */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
    public static class Builder extends ItemBase.AbstractBuilder {

        // =============================================*
        // ローカル変数
        // =============================================*
        // コア
        private long userId;
        private String screenName;
        private String userName;
        private String profileImageUrl;
        private String profileImageUrlHttps;
        // -------------------------------------+
        // プロフィール
//		private Date createdAt;
        private TwitterLanguage language;
        private TwitterCountry country;
        private TwitterTimeZone timeZone;
        private int utcOffset;
        private String location;
        private String description;
        private String url;
        // -------------------------------------+
        // 数値
        private int friendsCount;
        private int followersCount;
        private int statusesCount;
        private int favouritesCount;
        private int listedCount;
        // -------------------------------------+
        // フラグ
        private boolean isFollowing;
        private boolean isProtected;
        private boolean verified;
        private boolean authenticated;
        // -------------------------------------+
        // 認証用データ
        private TwitterAccessToken accessToken;
        private List<Category> val;
        // -------------------------------------+
        private boolean isNew;

        // =============================================*
        // コンストラクター
        // =============================================*
        public Builder() {
        }

        // =============================================*
        // 親のセッターラッパー
        // =============================================*
        // -------------------------------------+
        // コア
        @Override
        public Builder itemId(ItemId _val) {
            setItemId(_val);
            return this;
        }

        @Override
        public Builder createdAt(Date _val) {
            setCreatedAt(_val);
            return this;
        }

        @Override
        public Builder enteredAt(Date _val) {
            setEnteredAt(_val);
            return this;
        }

        @Override
        public Builder state(ItemStateType _val) {
            setState(_val);
            return this;
        }

        // -------------------------------------+
        // キャッシュ
        @Override
        public <T extends AbstractBuilder> T labels(List<LabelBase> _val) {
            setLabels(_val);
            return (T) this;
        }

        @Override
        public <T extends AbstractBuilder> T categories(List<Category> _val) {
            setCategories(_val);
            return (T) this;
        }

        @Override
        public <T extends AbstractBuilder> T tags(List<Tag> _val) {
            setTags(_val);
            return (T) this;
        }

        // =============================================*
        // セッター
        // =============================================*
        // コア
        public Builder userId(long _val) {
            userId = _val;
            return this;
        }

        public Builder screenName(String _val) {
            screenName = _val;
            return this;
        }

        public Builder userName(String _val) {
            userName = _val;
            return this;
        }

        public Builder profileImageUrl(String _val) {
            profileImageUrl = _val;
            return this;
        }

        public Builder profileImageUrlHttps(String _val) {
            profileImageUrlHttps = _val;
            return this;
        }

        // -------------------------------------+
        // プロフィール
//		public Builder createdAt(Date _val) {
//			createdAt = MyDateUtils.safeCopy(_val);
//			return this;
//		}

        public Builder language(TwitterLanguage _val) {
            language = _val;
            return this;
        }

        public Builder language(String _val) {
            language = TwitterLanguage.getEnumByCode(_val);
            return this;
        }

        public Builder country(TwitterCountry _val) {
            country = _val;
            return this;
        }

        public Builder country(String _val) {
            country = TwitterCountry.getEnumByCode(_val);
            return this;
        }

        public Builder timeZone(TwitterTimeZone _val) {
            timeZone = _val;
            return this;
        }

        public Builder timeZone(String _val) {
            timeZone = TwitterTimeZone.getEnumByName(_val);
            return this;
        }

        public Builder utcOffset(int _val) {
            utcOffset = _val;
            return this;
        }

        public Builder location(String _val) {
            location = _val;
            return this;
        }

        public Builder description(String _val) {
            description = _val;
            return this;
        }

        public Builder url(String _val) {
            url = _val;
            return this;
        }

        // -------------------------------------+
        // 数値
        public Builder friendsCount(int _val) {
            friendsCount = _val;
            return this;
        }

        public Builder followersCount(int _val) {
            followersCount = _val;
            return this;
        }

        public Builder statusesCount(int _val) {
            statusesCount = _val;
            return this;
        }

        public Builder favouritesCount(int _val) {
            favouritesCount = _val;
            return this;
        }

        public Builder listedCount(int _val) {
            listedCount = _val;
            return this;
        }

        // -------------------------------------+
        // フラグ
        public Builder isFollowing(boolean _val) {
            isFollowing = _val;
            return this;
        }

        public Builder isProtected(boolean _val) {
            isProtected = _val;
            return this;
        }

        public Builder verified(boolean _val) {
            verified = _val;
            return this;
        }

        public Builder authenticated(boolean _val) {
            this.authenticated = _val;
            return this;
        }

        // -------------------------------------+
        // 認証用データ
        public Builder accessToken(TwitterAccessToken _val) {
            accessToken = _val;
            return this;
        }

        public Builder accessToken(
                String _accessKey, String _accessKeySecret) {

            return accessToken(new TwitterAccessToken(
                    _accessKey,
                    _accessKeySecret));
        }

        //		public Builder accessKey(String _val) {
//			accessToken.accessKey = _val;
//			return this;
//		}
//
//		public Builder accessKeySecret(String _val) {
//			accessToken.accessKeySecret = _val;
//			return this;
//		}
        // -------------------------------------+
        // 認証用データ
        public Builder isNew() {
            this.isNew = true;
            return this;
        }


        // =============================================*
        // ビルド
        // =============================================*
		/*
		 * (非 Javadoc)
		 *
		 * @see models.items.ItemBase.AbstractBuilder#build()
		 */
        public User build() {
            User user = new User(this);
            user.setupStringFields();
            return user;
        }

    }

    /* ************************************************************ */
	/*
	 * プロパティ初期化.
	 */
	/* ************************************************************ */
    @Override
    public void initSubs() {
        // 必要なら作る
    }

    // =============================================*

    /**
     * serialCodeの生成.
     */
    @Override
    public String createSerialCode() {
        return MyModelUtils.createRepostElementSerialCode(
                ITEM_TYPE,
                itemId.id,
                "" + userId
        );// Long.toString(this.userId)
    }

	/* ************************************************************ */
	/*
	 * データ整形
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

    /**
     * 文字列フィールドセットアップ.
     * 空白や空文字だった場合には、nullに置き換える。
     */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
    public void setupStringFields() {
        setupUserName();
        setupScreenName();
        setupLocation();
        setupDescription();
        setupProfileImageUrl();
        setupProfileImageUrlHttps();
        setupUrl();
    }

    public void setupUserName() {
        this.userName = MyStringUtils.trimToNull(this.userName);
    }

    public void setupScreenName() {
        this.screenName = MyStringUtils.trimToNull(this.screenName);
    }

    public void setupLocation() {
        this.location = MyStringUtils.trimToNull(this.location);
    }

    public void setupDescription() {
        this.description = MyStringUtils.trimToNull(this.description);
    }

    public void setupProfileImageUrl() {
        this.profileImageUrl = MyStringUtils.trimToNull(this.profileImageUrl);
    }

    public void setupProfileImageUrlHttps() {
        this.profileImageUrlHttps = MyStringUtils
                .trimToNull(this.profileImageUrlHttps);
    }

    public void setupUrl() {
        this.url = MyStringUtils.trimToNull(this.url);
    }

//	public void setupStringFields(){
//		this.userName = StringUtils.trimToNull(this.userName);
//		this.screenName = StringUtils.trimToNull(this.screenName);
//		this.location = StringUtils.trimToNull(this.location);
//		this.description = StringUtils.trimToNull(this.description);
//		this.profileImageUrl = StringUtils.trimToNull(this.profileImageUrl);
//		this.profileImageUrlHttps = StringUtils.trimToNull(this.profileImageUrlHttps);
//		this.url = StringUtils.trimToNull(this.url);
//	}

    // =============================================*
	/*
	 * (非 Javadoc)
	 *
	 * @see models.items.ItemBase#setupSubs()
	 */
    @Override
    public void setupSubs() {
        setupStringFields();
    }

    /* ************************************************************ */
	/*
	 * アクセッサ
	 */
	/* ************************************************************ */
    // =============================================*
	/*
	 * Enumのフィールド
	 */
    public TwitterLanguage getLanguage() {
        return language;
    }

    public void setLanguage(TwitterLanguage language) {
        this.language = language;
    }

    // Overload
    public void setLanguage(String _code) {
        this.language = TwitterLanguage.getEnumByCode(_code);
    }

    // =============================================*
	/*
	 * Enumのフィールド
	 */
    public TwitterCountry getCountry() {
        return country;
    }

    public void setCountry(TwitterCountry country) {
        this.country = country;
    }

    // Overload
    public void setCountry(String _code) {
        this.country = TwitterCountry.getEnumByCode(_code);
    }

    // =============================================*
	/*
	 * Enumのフィールド
	 */
    public TwitterTimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TwitterTimeZone timeZone) {
        this.timeZone = timeZone;
    }

    // Overload
    public void setTimeZone(String _name) {
        this.timeZone = TwitterTimeZone.getEnumByName(_name);
    }

    // =============================================*
	/*
	 * キャッシュリスト
	 */
    @Override
    public List<Category> getCategories() {
//		if(this.categories == null){
//		this.categories = Repost.findCategoriesByUsers(this);
//	}
        return this.categories;
    }

    @Override
    public void setCategories(List<Category> _categories) {
        this.categories = _categories;
    }

    // =============================================*
	/*
	 * キャッシュリスト
	 */
    @Override
    public List<Tag> getTags() {
//		if(this.tags == null){
//		this.tags = Repost.findTagsByUsers(this);
//	}
        return this.tags;
    }

    @Override
    public void setTags(List<Tag> _tags) {
        this.tags = _tags;
    }

    // =============================================*
	/*
	 * リポスト要素共通情報
	 */
	/*
	 * (非 Javadoc)
	 *
	 * @see models.reposts.IRepostElement#getSerialCode()
	 */
    @Override
    public String getSerialCode() {
        return this.serialCode;
    }

    /*
     * (非 Javadoc)
     *
     * @see models.reposts.IRepostElement#getSignature()
     */
    @Override
    public String getSignature() {
        return this.screenName;
    }

    /*
     * (非 Javadoc)
     *
     * @see models.reposts.IRepostElement#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return this.userName;
    }

    /* ************************************************************ */
	/*
	 * JPA関連
	 */
	/* ************************************************************ */
    @Override
    public void mergeSubs() {
        super.mergeSubs();
        this.tweets = null;
    }

	/* ************************************************************ */
	/*
	 * 基本メソッド
	 */
	/* ************************************************************ */

    /* ************************************************************ */
	/*
	 * クラス情報スタティックヘルパ
	 *
	 * @see models.reposts.IRepostElement#getMergedKeyColName()
	 */
	/* ************************************************************ */
    // SQLにて使用
    private static final String OUTER_ID_COL_NAME = "userId";
    private static final String SCREEN_NAME_COL_NAME = "screenName";
    private static final String USER_NAME_COL_NAME = "userName";
    private static final String SIGNATURE_COL_NAME = SCREEN_NAME_COL_NAME;

    public static String getOuterIdColName() {
        return OUTER_ID_COL_NAME;
    }

    public static String getSignatureColName() {
        return SIGNATURE_COL_NAME;
    }

    public static String getDisplayNameColName() {
        return USER_NAME_COL_NAME;
    }

    /* ************************************************************ */
	/*
	 * 遅延生成
	 */
	/* ************************************************************ */
    public static User findOrCreate(
            // Super's fields
            Date _createdAt,
            // This fields
            // コア
            String _serialCode,
            long _userId,
            String _screenName,
            String _userName,
            String _profileImageUrl,
            String _profileImageUrlHttps,
            // プロフィール
            TwitterLanguage _language,
            TwitterCountry _country,
            TwitterTimeZone _timeZone,
            int _utcOffset,
            String _location,
            String _description,
            String _url
    ) {

        User user = findUniquely(_serialCode).first();

        // FIXME
        if (user == null) {
            ItemId iid = new ItemId();
            iid.save();
            user = new User.Builder()
                    // Abstract Builder
                    .itemId(iid)
                    .createdAt(_createdAt)
                            // This Builder
                    .userId(_userId)
                    .screenName(_screenName)
                    .userName(_userName)
                    .profileImageUrl(_profileImageUrl)
                    .profileImageUrlHttps(_profileImageUrlHttps)
                    .language(_language)
                    .country(_country)
                    .timeZone(_timeZone)
                    .utcOffset(_utcOffset)
                    .location(_location)
                    .description(_description)
                    .url(_url)
                    .build();
            user.save();
        }
        return user;
    }

    // 文字列キーワードをEntityのリストへ
    public static List<User> getMatchedListByName(String... _screenNames) {
        List<User> items = new ArrayList<User>();
        for (String name : _screenNames) {
            User tmp = findUniquely(name).first();
            items.add(tmp);
        }
        return items;
    }

    // 文字列キーワードをEntityの配列へ
    public static User[] getMatchedArrayByName(String... _screenNames) {
        return (User[]) getMatchedListByName(_screenNames).toArray();
    }

    /* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * twitter4j.Userからの遅延生成
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
    public static User findOrCreate(twitter4j.User _tu)
            throws IllegalArgumentException {

        if (_tu == null) {
            throw new IllegalArgumentException();
        }

        User user = findByOuterId(_tu.getId()).first();
        if (user != null) {
            return user;
        }

        ItemId itemId = new ItemId();
        itemId.save();

        return new Builder()
                // 親のラッパー
                .itemId(itemId)
                .createdAt(_tu.getCreatedAt())
                        // -------------------------------------+
                        // コア
                .userId(_tu.getId())
                .screenName(_tu.getScreenName())
                .userName(_tu.getName())
                .profileImageUrl(_tu.getProfileImageURL())
                .profileImageUrlHttps(_tu.getProfileImageURLHttps())
                        // -------------------------------------+
                        // プロフィール
                .language(TwitterLanguage.getEnumByName(_tu.getLang()))
                        // .country(null)// TwitterCountry.getEnumByName(_tu.get)//FIXME
                .timeZone(TwitterTimeZone.getEnumByName(_tu.getTimeZone()))
                .utcOffset(_tu.getUtcOffset())
                .location(_tu.getLocation())
                .description(_tu.getDescription())
                .url(_tu.getURL())
                        // -------------------------------------+
                        // 数値
                .friendsCount(_tu.getFriendsCount())
                .followersCount(_tu.getFollowersCount())
                .statusesCount(_tu.getStatusesCount())
                .favouritesCount(_tu.getFavouritesCount())
                .listedCount(_tu.getListedCount())
                        // -------------------------------------+
                        // フラグ
                .isFollowing(false)// 要検討
                .isProtected(_tu.isProtected())
                .verified(_tu.isVerified())
                .authenticated(false)// 要検討
                        // -------------------------------------+
                        // 認証用データ
                .accessToken(null)// FIXME
                        // -------------------------------------+
                .isNew()
                .build();

    }

    /* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * twitter4j.Userからの遅延生成
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
    public static User findAndUpdateOrCreate(twitter4j.User _tu)
            throws IllegalArgumentException {

        User user = findOrCreate(_tu);
        if (user != null && user.isNew == false) {
            user.copy(_tu);
        }
        return user;
    }

    /* ************************************************************ */
	/*
	 * インスタンスごとの、関連ネーミングの収集
	 *
	 * @see models.items.ItemBase
	 */
	/* ************************************************************ */
    @Override
    protected ItemType getItemType() {
        return ITEM_TYPE;
    }

    @Override
    protected <T extends ItemBase> T getSelf() {
        return (T) this;
    }

    /* ************************************************************ */
	/*
	 * 検索ヘルパ
	 * リポスト関連はRepostBaseからのみ
	 */
	/* ************************************************************ */
    // =============================================*
    // カラム文字列隠蔽のためのラッパー
    public static JPAQuery findBySerialCode(String... _params) {
        return find(getSerialCodeColName(), (Object[]) _params);
    }

    // このエンティティの場合は screenName
    public static JPAQuery findBySignature(String... _params) {
        return find(getSignatureColName(), (Object[]) _params);
    }

    // このエンティティの場合は userName
    public static JPAQuery findByDisplayName(String... _params) {
        return find(getDisplayNameColName(), (Object[]) _params);
    }

    // 引数をStringにする必要ありか？
    // このエンティティの場合は userId
    public static JPAQuery findByOuterId(Long... _params) {
        return find(getOuterIdColName(), (Object[]) _params);
    }

    // =============================================*
    // 特定的な取得
    public static JPAQuery findUniquely(String _serialCode) {
        return findBySerialCode(_serialCode);
    }

    // 近似的な取得
    public static FindQueryHelper findLikely() {
        return new FindQueryHelper() {
            @Override
            public JPAQuery getJPAQuery() {
                if (this.isSignature) {
                    return findBySignature(this.signatureParams);
                }
                return findByDisplayName(this.displayNameParams);
            }
        };
    }

    // =============================================*
    // 並び替え条件
    public static enum OrderBy implements IOrderBy {

        OUTER_ID_ASC(OUTER_ID_COL_NAME + ASC),
        OUTER_ID_DESC(OUTER_ID_COL_NAME + DESC),
        SCREEN_NAME_ASC(SCREEN_NAME_COL_NAME + ASC),
        SCREEN_NAME_DESC(SCREEN_NAME_COL_NAME + DESC),
        USER_NAME_ASC(USER_NAME_COL_NAME + ASC),
        USER_NAME_DESC(USER_NAME_COL_NAME + DESC);

        private String sql;

        private OrderBy(String _sql) {
            this.sql = _sql;
        }

        public String getSql() {
            return this.sql;
        }
    }

	/* ************************************************************ */
	/*
	 * 追加・削除
	 */
	/* ************************************************************ */
	/*
	 * ItemBase#attachCategories
	 * ItemBase#attachTags
	 * ItemBase#detachCategories
	 * ItemBase#detachTags
	 */

//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * 言語セットアップ.
//	 * コンストラクタでの取得はコードのみ
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	// TODO 手直しする
//	public void putLanguage() {
//		String code = this.languageCode;
//		// if (code == null || code.equals("")) return;
//		TwitterLanguage eTemp = TwitterLanguage.getEnumByCode(code);
//		if (eTemp != null) {
//			this.language = eTemp;
//		}
//		else if (this.language != null) {
//			this.languageCode = this.language.getCode();
//		}
//	}
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * 国家セットアップ.
//	 * コンストラクタでの取得はコードのみ
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	// TODO 手直しする
//	public void putCountry() {
//		String code = this.countryCode;
//		// if (code == null || code.equals("")) return;
//		// Logger.debug("code:"+code);
//		TwitterCountry eTemp = TwitterCountry.getEnumByCode(code);
//		if (eTemp != null) {
//			// Logger.debug("eTemp not null");
//			this.country = eTemp;
//		}
//		else if (this.country != null) {
//			// Logger.debug("country not null");
//			this.countryCode = this.country.getCode();
//		}
//	}
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * タイムゾーン セットアップ コンストラクタでの取得はコードのみ (void method)
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public void putTimeZone() {
//		String name = this.timeZoneName;
//		if (isNotBlank(name)) {
//			TwitterTimeZone eTemp = TwitterTimeZone.getEnumByName(name);
//			if (eTemp != null) {
//				this.timeZone = eTemp;
//			}
//		}
//	}

	/* ************************************************************ */
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * PrePersist (void method)
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	@PrePersist
//	public void prePersist() {
//		Logger.debug(">>User.PrePersist");
//		putLanguage();
//		putCountry();
//		putTimeZone();
//	}
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * PreUpdate (void method)
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	@PreUpdate
//	public void preUpdate() {
//		Logger.debug(">>User.PreUpdate");
//		putLanguage();
//		putCountry();
//		putTimeZone();
//	}
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * PostLoad (void method)
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	@PostLoad
//	public void postLoad() {
//		Logger.debug(">>User.PostLoad");
//
//		// Language
//		if (this.language != null) {
//			this.languageCode = this.language.getCode();
//		}
//
//		// Country
//		if (this.country != null) {
//			this.countryCode = this.country.getCode();
//		}
//
//		// TimeZone
//		if (this.timeZone != null) {
//			this.timeZoneName = this.timeZone.getName();
//			// this.timeZoneData = this.timeZone.getName();
//		}
//	}

	/* ************************************************************ */

    // FIXME 以下、tolist時のソース、一時的にコメントアウトしている

//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * このユーザーによる投稿(つぶやき)を収拾
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public List<TwitterTweet> fetchPosts() {
//		return TwitterTweet.find(
//				"SELECT twt " + "FROM TwitterTweet AS twt "
//						+ "WHERE pst.owner.id = ? ", this.id).fetch();
//	}
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * 投稿(つぶやき)リスト作成して格納.
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public void setPosts() {
//		this.tweets = fetchPosts();
//	}
//
//	/* ************************************************************ */
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * 検索.
//	 *
//	 * @param _serviceType
//	 * @param _userId
//	 * @return 検索結果のインスタンス
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public static User findOnly(ServiceType _serviceType, long _userId) {
//		if (_serviceType == null || _userId < 0)
//			return null;
//		return User
//				.find("byServiceTypeAndUserId", _serviceType, _userId).first();
//	}
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public static User findOnly(User _owner) {
//		if (_owner == null)
//			return null;
//		if (!_owner.valid())
//			return null;
//		return findOnly(_owner.serviceType, _owner.userId);
//	}
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public static User findOnlyNoValidate(User _owner) {
//		if (_owner == null)
//			return null;
//		return findOnly(_owner.serviceType, _owner.userId);
//	}
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	// 条件付き生成処理は「まとめられない」ので注意
//	// オブジェクト版で引数をそのまま代入していたり、saveを条件内で行ったりしているため
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/*
//	 * 条件付き生成処理（遅延生成のようなもの） 生データ指定版
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	// 作らない
//	// 引数でNewするための情報をすべて渡す必要があるが、要素が多すぎる
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * 条件付き生成処理
//	 * （遅延生成のようなもの）<br>
//	 * オブジェクト指定版
//	 *
//	 * @param _owner
//	 *            Userオブジェクト
//	 * @return 検索結果、もしくは新規インスタンス
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public static User findOrCreate(User _owner) {
//		if (_owner == null || _owner.valid() == false) {
//			return null;
//		}
//		User owner = findOnlyNoValidate(_owner);
//		// Find
//		if (owner != null)
//			return owner;
//		// New Create
//		return _owner;
//	}
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * 条件付き生成処理.
//	 * （遅延生成のようなもの）<br>
//	 * オブジェクト指定・セーブあり版
//	 *
//	 * @param _owner
//	 *            Userオブジェクト
//	 * @return 検索結果、もしくは新規インスタンス
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public static User findOrCreateAndSave(User _owner) {
//		if (_owner == null || _owner.valid() == false) {
//			return null;
//		}
//		User owner = findOnlyNoValidate(_owner);
//		// ↑ここまでfindOrCreateと同じだがまとめてはいけない。return nullするため
//		// Find
//		if (owner != null) {
//			owner.save();// ★要検討
//			return owner;
//		}
//		// New Create
//		_owner.save();
//		return _owner;
//	}
//
	/* ************************************************************ */

//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * 比較更新処理.
//	 *
//	 * @param _otherUser
//	 *            Userオブジェクト
//	 * @return 更新の有無
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public boolean checkAndUpdate(twitter4j.User _otherUser) {
//		Logger.debug("checkAndUpdate");
//
//		int debugNum = 0;
//
//		// -------------------------------------+
//		if (this.getCreatedAt() != _otherUser.getCreatedAt()) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			// そもそも別のユーザー
//			return false;
//		}
//
//		// -------------------------------------+
//		// コア
//		if (this.userId != _otherUser.getId()) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			// そもそも別のユーザー
//			return false;
//		}
//
//		if (_otherUser.getScreenName().equals(this.screenName) == false) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.screenName = _otherUser.getScreenName();
//			return true;
//		}
//		if (_otherUser.getName().equals(this.userName) == false) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.userName = _otherUser.getName();
//			return true;
//		}
//		if (_otherUser.getProfileImageURL().toString()
//				.equals(this.profileImageUrl)==false) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.profileImageUrl = _otherUser.getProfileImageURL().toString();
//			return true;
//		}
//		if (_otherUser.getProfileImageUrlHttps().toString()
//				.equals(this.profileImageUrlHttps)== false) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.profileImageUrlHttps = _otherUser.getProfileImageUrlHttps().toString();
//			return true;
//		}
//
//		// -------------------------------------+
//		// TODO ここの処理、重いんじゃないかな。要検討
//		// 必要性
//		// -------------------------------------+
//		// プロフィール
//		if (this.language != _otherUser.language) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.language = _otherUser.language;
//			return true;
//		}
//
//		if (this.country != _otherUser.country) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//
//			// このへん、エラー出るよ。PrePersistとかで入れてるのを、ここでセットする必要あり
//
//			this.country = _otherUser.country;
//			return true;
//		}
//
//		if (this.timeZone != _otherUser.timeZone) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.timeZone = _otherUser.timeZone;
//			return true;
//		}
//
//		if (this.utcOffset != _otherUser.utcOffset) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.utcOffset = _otherUser.utcOffset;
//			return true;
//		}
//		if (!this.description.equals(_otherUser.description)) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.description = _otherUser.description;
//			return true;
//		}
//		if (!this.url.equals(_otherUser.url)) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.url = _otherUser.url;
//			return true;
//		}
//		if (!this.location.equals(_otherUser.location)) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.location = _otherUser.location;
//			return true;
//		}
//		// -------------------------------------+
//		// 数値
//		if (this.friendsCount != _otherUser.friendsCount) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.friendsCount = _otherUser.friendsCount;
//			return true;
//		}
//		if (this.followersCount != _otherUser.followersCount) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.followersCount = _otherUser.followersCount;
//			return true;
//		}
//		if (this.statusesCount != _otherUser.statusesCount) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.statusesCount = _otherUser.statusesCount;
//			return true;
//		}
//		if (this.favouritesCount != _otherUser.favouritesCount) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.favouritesCount = _otherUser.favouritesCount;
//			return true;
//		}
//		if (this.listedCount != _otherUser.listedCount) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.listedCount = _otherUser.listedCount;
//			return true;
//		}
//		// -------------------------------------+ //フラグ
//		if (this.isFollowing != _otherUser.isFollowing) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.isFollowing = _otherUser.isFollowing;
//			return true;
//		}
//		if (this.isProtected != _otherUser.isProtected) {
//			Logger.debug("checkAndUpdate check" + debugNum++);
//			this.isProtected = _otherUser.isProtected;
//			return true;
//		}
//		if (this.verified != _otherUser.verified) {
//			Logger.debug("checkAndUpdate check" +
//					debugNum++);
//			this.verified = _otherUser.verified;
//			return true;
//		}
//
//		// return fHasChange;
//		Logger.debug("Same User");
//		return false;
//	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

    /**
     * コピー.
     *
     * @param _otherUser
     */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
    public void copy(twitter4j.User _otherUser) {

        this.setCreatedAt(_otherUser.getCreatedAt());
        // -------------------------------------+
        // コア
        this.userId = _otherUser.getId();
        this.screenName = _otherUser.getScreenName();
        this.userName = _otherUser.getName();
        this.profileImageUrl = _otherUser.getProfileImageURL();
        this.profileImageUrlHttps = _otherUser.getProfileImageURLHttps();
        this.language = TwitterLanguage.getEnumByName(_otherUser.getLang());
        //this.country = _otherUser.getCountry();
        this.timeZone = TwitterTimeZone.getEnumByName(_otherUser.getTimeZone());
        this.utcOffset = _otherUser.getUtcOffset();
        this.description = _otherUser.getDescription();
        this.url = _otherUser.getURL();
        this.location = _otherUser.getLocation();
        this.friendsCount = _otherUser.getFriendsCount();
        this.followersCount = _otherUser.getFollowersCount();
        this.statusesCount = _otherUser.getStatusesCount();
        this.favouritesCount = _otherUser.getFavouritesCount();
        this.listedCount = _otherUser.getListedCount();
        this.isFollowing = false;
        this.isProtected = _otherUser.isProtected();
        this.verified = _otherUser.isVerified();
    }

//	public static void main(String[] args) {
//		User tu = new User.Builder().build();
//	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
