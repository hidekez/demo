package models.items;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import play.Logger;
import play.data.validation.InFuture;
import play.data.validation.InPast;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.GenericModel.JPAQuery;

import utils.MyDateUtils;
import utils.MyModelUtils;
import utils.MyStringUtils;
import validation.TwitterScreenName;
import constants.ItemStateType;
import constants.ServiceType;
import constants.TwitterCountry;
import constants.TwitterLanguage;

import models.Account;
import models.IOrderBy;
import models.MySuperModel.FindQueryHelper;
import models.items.ItemBase.AbstractBuilder;
import models.items.User.Builder;
import models.items.multi.Geo;
import models.items.twitter.ITwitterConstant;
import models.items.twitter.InReplyTo;
import models.labels.Category;
import models.labels.LabelBase;
import models.labels.LabelType;
import models.labels.Tag;
import models.reposts.IRepostElement;
import models.reposts.NoFittingDataExistException;
import models.reposts.RepostBase;
import models.reposts.RepostTweetCategory;

/**
 * つぶやきモデル.
 *
 * @author H.Kezuka
 */
@Entity
public class Tweet extends ItemBase
		implements Comparable<Tweet>, IRepostElement {

	// =============================================*
	// 定数
	// =============================================*
	public static final ItemType ITEM_TYPE = ItemType.TWEET;

	// =============================================*
	// コア
	// =============================================*
	// 親クラスで定義
//	/**
//	 * 投稿(つぶやき)が作られた日時.
//	 */
//	@Required
//	@InFuture(ITwitterConstant.LIMIT_OF_TWEET_YMD_STR)
//	@InPast
//	private Date createdAt;

	/**
	 * 外部システムにより割り振られているID.
	 * Twitter では StatusId
	 */
	@Required
	@Min(ITwitterConstant.STATUS_ID_MIN)
	public long statusId;

	/**
	 * 投稿(つぶやき)文.
	 */
	@Required
	@MaxSize(ITwitterConstant.TEXT_MAX_LENGTH)
	public String text;

	/**
	 * 所有者.
	 */
	// 必須・null禁止
	// TODO 双方向設定にすべきでは？
	@ManyToOne
	@Required
	@Basic(optional = false)
	@Valid
	public User author;

	/**
	 * 言語.
	 * TwitterのStatusにはない情報であり、TwitterUserから取得している。<br>
	 * 取得は言語コード(2文字)による。
	 */
	@Enumerated(EnumType.STRING)
	private TwitterLanguage language;

	/**
	 * 地域（国）.
	 * TwitterのStatusにはない情報であり、TwitterUserから取得している。<br>
	 * 取得は言語コード(2文字)による。
	 */
	@Enumerated(EnumType.STRING)
	private TwitterCountry country;

	// =============================================*
	// フラグ
	// =============================================*
	/**
	 * リプライ情報フラグ.
	 */
	@Required
	public boolean isInReply;

	/**
	 * 位置情報フラグ.
	 */
	@Required
	public boolean hasGeo;

	// =============================================*
	// キャッシュ
	// =============================================*
	/**
	 * リプライ情報.
	 * (in_reply_to)<br>
	 * status_id, user_id, screen_name
	 */
	@Transient
	public InReplyTo inReplyTo;

	/**
	 * 位置情報(緯度・経度).
	 * latitude, longitude
	 */
	@Transient
	public Geo geo;

	// =============================================*
	// リスト
	// =============================================*
	/**
	 * メンションリスト.
	 */
	@Transient
	// @RelationMaxSize(MENTION_RELATION_MAX_SIZE)
	public List<Tweet> mentions;

	// =============================================*
	// Entity名
	// =============================================*

	/* ************************************************************ */
	/*
	 * コンストラクタ.
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * コンストラクタ（ビルダー版）.
	 *
	 * @param _builder
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public Tweet(Builder _builder) {
		super(_builder);
		this.statusId = _builder.statusId;
		this.text = _builder.text;
		this.author = _builder.author;
		this.language = _builder.language;
		this.country = _builder.country;
		this.isInReply = _builder.isInReply;
		this.hasGeo = _builder.hasGeo;
	}

	/* ************************************************************ */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * ビルダー(インナークラス).
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static class Builder extends ItemBase.AbstractBuilder {

		// =============================================*
		// ローカル変数
		// =============================================*
		private long statusId;
		private String text;
		private User author;
		private TwitterLanguage language;
		private TwitterCountry country;
		private boolean isInReply;
		private boolean hasGeo;

		// =============================================*
		// コンストラクター
		// =============================================*
		public Builder() {}

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
		public Builder statusId(long _val) {
			statusId = _val;
			return this;
		}

		public Builder text(String _val) {
			text = _val;
			return this;
		}

		public Builder author(User _val) {
			author = _val;
			return this;
		}

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

		public Builder isInReply(boolean _val) {
			isInReply = _val;
			return this;
		}

		public Builder hasGeo(boolean _val) {
			hasGeo = _val;
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
		@Override
		public Tweet build() {
			Tweet tweet = new Tweet(this);
			tweet.setupStringFields();
			return tweet;
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
//		this.text = null;
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
				"" + this.statusId);// Long.toString(this.userId)
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
		setupText();
		setupRepScreenName();
	}

	public void setupText() {
		this.text = MyStringUtils.trimToNull(this.text);
	}

	public void setupRepScreenName() {
		if (this.inReplyTo != null) {
			this.inReplyTo.setupRepScreenName();
		}
	}

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

	public void setCountry(String _code) {
		this.country = TwitterCountry.getEnumByCode(_code);
	}

	// =============================================*
	/*
	 * キャッシュリスト
	 */
	@Override
	public List<Category> getCategories() {
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
		return null;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see models.reposts.IRepostElement#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return null;
	}

	/* ************************************************************ */
	/*
	 * JPA関連
	 */
	/* ************************************************************ */
	@Override
	public void mergeSubs() {
		super.mergeSubs();
		if (this.author != null && this.author.isPersistent() == false) {
			this.author.mergeSubs();
			this.author = author.merge();
		}
	}

	/* ************************************************************ */
	/*
	 * 基本メソッド
	 */
	/* ************************************************************ */
	@Override
	public String toString() {
		return "Tweet ["
				+ "id=" + this.id
				+ ", itemId=" + this.itemId
				+ ", author=" + this.author.screenName
				+ ", state=" + this.state
				+ ", text=" + this.text
				+ "]";
	}

	@Override
	public int compareTo(Tweet _other) {
		return (int) (this.id - _other.id);
	}

	/* ************************************************************ */
	/*
	 * クラス情報スタティックヘルパ
	 *
	 * @see models.reposts.IRepostElement#getMergedKeyColName()
	 */
	/* ************************************************************ */
	// RepostBaseからのみ
	private static final String STATUS_ID_COL_NAME = "statusId";
	private static final String TEXT_COL_NAME = "text";
	private static final String AUTHOR_COL_NAME = "author";
	private static final String LANGUAGE_COL_NAME = "language";
	private static final String COUNTRY_COL_NAME = "country";
	private static final String SIGNATURE_COL_NAME = STATUS_ID_COL_NAME;

	public static String getSignatureColName() {
		return SIGNATURE_COL_NAME;
	}

	public static String getDisplayNameColName() {
		return null;
	}

	/* ************************************************************ */
	/*
	 * 遅延生成
	 */
	/* ************************************************************ */
	// FIXME
	public static Tweet findOrCreate(
			// Super's fields
			Date _createdAt,
			// This fields
			String _serialCode,
			long _statusId,
			String _text,
			User _author,
			TwitterLanguage _language,
			TwitterCountry _country,
			boolean _isInReply,
			boolean _hasGeo
			) {

		Tweet item = findUniquely(_serialCode).first();

		if (item == null) {
			ItemId iid = new ItemId();
			iid.save();
			item = new Tweet.Builder()
					// Abstract Builder
					.itemId(iid)
					.createdAt(_createdAt)
					.itemId(iid)
					// This Builder
					.statusId(_statusId)
					.text(_text)
					.author(_author)
					.language(_language)
					.country(_country)
					.isInReply(_isInReply)
					.hasGeo(_hasGeo)
					.build();
			item.save();
		}
		return item;
	}

	// 文字列キーワードをEntityのリストへ
	public static List<Tweet> getMatchedListByName(String... _tweetIds) {
		List<Tweet> items = new ArrayList<Tweet>();
		for (String name : _tweetIds) {
			Tweet tmp = findUniquely(name).first();
			items.add(tmp);
		}
		return items;
	}

	// 文字列キーワードをEntityの配列へ
	public static Tweet[] getMatchedArrayByName(String... _tweetIds) {
		return (Tweet[]) getMatchedListByName(_tweetIds).toArray();
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

	public static JPAQuery findBySignature(String... _params) {
		return find(getSignatureColName(), (Object[]) _params);
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
			public JPAQuery getJPAQuery() throws IllegalStateException {
				if (this.isSignature) {
					return findBySignature(this.signatureParams);
				}
				throw new IllegalStateException();
			}
		};
	}

	// =============================================*
	// 並び替え条件
	public static enum OrderBy implements IOrderBy {

		STATUS_ID_ASC(STATUS_ID_COL_NAME + ASC),
		STATUS_ID_DESC(STATUS_ID_COL_NAME + DESC),
		TEXT_ASC(TEXT_COL_NAME + ASC),
		TEXT_DESC(TEXT_COL_NAME + DESC),
		AUTHOR_ASC(AUTHOR_COL_NAME + ASC),
		AUTHOR_DESC(AUTHOR_COL_NAME + DESC),
		LANGUAGE_ASC(LANGUAGE_COL_NAME + ASC),
		LANGUAGE_DESC(LANGUAGE_COL_NAME + DESC),
		COUNTRY_ASC(COUNTRY_COL_NAME + ASC),
		COUNTRY_DESC(COUNTRY_COL_NAME + DESC),

		AUTHOR_USER_ID_ASC(
				AUTHOR_COL_NAME + "." + User.OrderBy.OUTER_ID_ASC.getSql()),
		AUTHOR_USER_ID_DESC(
				AUTHOR_COL_NAME + "." + User.OrderBy.OUTER_ID_DESC.getSql()),
		AUTHOR_SCREEN_NAME_ASC(
				AUTHOR_COL_NAME + "." + User.OrderBy.SCREEN_NAME_ASC.getSql()),
		AUTHOR_SCREEN_NAME_DESC(
				AUTHOR_COL_NAME + "." + User.OrderBy.SCREEN_NAME_DESC.getSql()),
		AUTHOR_USER_NAME_ASC(
				AUTHOR_COL_NAME + "." + User.OrderBy.USER_NAME_ASC.getSql()),
		AUTHOR_USER_NAME_DESC(
				AUTHOR_COL_NAME + "." + User.OrderBy.USER_NAME_DESC.getSql());

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

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
