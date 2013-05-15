/*
 * 作成日 2012/04/06
 * 修正日 2012/04/24 toString,toMap,toJsonの削除、@Hide導入
 * 修正日 2012/06/05 コメントの変更（WZアウトライン用記号の削除）
 * 修正日 2012/09/07 tagsの追加
 * 修正日 2012/09/10 ItemBase -> AbstractItem
 * 修正日 2013/01/01 AbstractItem -> ItemBase
 * 修正日 2013/01/01 リポスト試作からの取り入れ
 * 修正日 2013/01/11 JPAマージヘルパ追加
 * 修正日 2013/01/22 UID -> serialCode
 * 修正日 2013/01/22 検索関連でクラス・フィールド情報ヘルパ整理
 */
package models.items;

import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import play.Logger;
import play.data.validation.InFuture;
import play.data.validation.InPast;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.Required;
import play.data.validation.Unique;

import constants.ItemStateType;
import controllers.gson.Hide;
import utils.MyDateUtils;
import utils.MyStringUtils;

import models.Account;
import models.IOrderBy;
import models.MySuperModel;
import models.items.twitter.ITwitterConstant;
import models.labels.Category;
import models.labels.LabelBase;
import models.labels.LabelType;
import models.labels.Tag;
import models.reposts.IRepostElementType;
import models.reposts.RepostBase;
import models.reposts.RepostTweetTag;
import models.reposts.RepostUserTag;

/**
 * アイテム基底抽象クラス
 * .
 * アイテムモデルの基底クラスだが、テーブルは実装サブクラス側で<br>
 * 個別に作られているのに注意。
 *
 * @author H.Kezuka
 */
@MappedSuperclass
abstract public class ItemBase extends MySuperModel
		implements ITwitterConstant {

	// =============================================*
	// 定数
	// =============================================*
	// public は外部から参照できるように
	public static final int SERIAL_CODE_NAME_MAX_LENGTH = 25;// Long19桁+6

	// =============================================*
	// コア
	// =============================================*
	/**
	 * アイテム全体のID生成クラス.
	 */
	// ItemBaseは固有のテーブルを持たないので、双方向設定はできない
	@OneToOne
	@Required
	@Unique
	@Basic(optional = false)
	public ItemId itemId;

	/**
	 * シリアルコード.
	 * オリジナルの外部IDとアイテム種類を組み合わせたもの。<br>
	 * アイテムの一意な検索(findUniquely)、<br>
	 * リクエストパラメーターなどで利用される。
	 */
	@Required
	@Unique
	@Basic(optional = false)
	public String serialCode;

	/**
	 * 作成日時.
	 */
	@Required
//	@InFuture(MyDateUtils.BIRTH_OF_WWW_YMD_STR)
	@Basic(optional = false)
	@InFuture(LIMIT_OF_TWEET_YMD_STR)
	@InPast
	private Date createdAt;

	/**
	 * 登録日時.
	 */
	// TODO DateIndex化する？
	// 必須・null禁止
	@Required
	@Basic(optional = false)
	@InFuture(MyDateUtils.BIRTH_OF_MYAPP_YMD_STR)
	@InPast
	private Date enteredAt;

	/**
	 * 状態.
	 */
	// 必須・null禁止
	@Required
	@Basic(optional = false)
	@Enumerated(EnumType.STRING)
	public ItemStateType state = ItemStateType.OK;

	// =============================================*
	// キャッシュ
	// =============================================*
	/**
	 * 関連付けされたラベルセット.
	 */
	@Transient
	public List<LabelBase> labels;

	/**
	 * 関連付けされたカテゴリーセット.
	 */
	@Transient
	public List<Category> categories;

	/**
	 * 関連付けされたタグセット.
	 */
	@Transient
	public List<Tag> tags;

	// =============================================*
	// イニシャライザ
	// =============================================*
	{
		this.labels = new ArrayList<LabelBase>();
		this.categories = new ArrayList<Category>();
		this.tags = new ArrayList<Tag>();
	}

	/* ************************************************************ */
	/*
	 * コンストラクタ
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * コンストラクタ.
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// 必須
	protected ItemBase() {
	}

	// Builderを通して生成すること
//	public ItemBase(ItemId _itemId) {
//		// コア
//		this.itemId = _itemId;
//		this.createdAt = MyDateUtils.now();
//		this.enteredAt = MyDateUtils.now();
//		this.serialCode = createSerialCode();
//	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * コンストラクタ（ビルダー版）.
	 *
	 * @param _builder
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public ItemBase(AbstractBuilder _builder) {

		// コア
		this.itemId = _builder.itemId;
		this.createdAt = _builder.createdAt;
		this.enteredAt = _builder.enteredAt;
		this.state = _builder.state;
		// -------------------------------------+
		// キャッシュ
		this.labels = _builder.labels;
		this.categories = _builder.categories;
		this.tags = _builder.tags;
		// -------------------------------------+
		this.serialCode = createSerialCode();
	}

	/* ************************************************************ */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * ビルダー(インナークラス).
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	abstract protected static class AbstractBuilder {

		// =============================================*
		// ローカル変数
		// =============================================*
		// -------------------------------------+
		// コア
		protected ItemId itemId;
//		protected String serialCode;
		protected Date createdAt = MyDateUtils.now();
		protected Date enteredAt = MyDateUtils.now();
		protected ItemStateType state = ItemStateType.OK;
		// -------------------------------------+
		// キャッシュ
		protected List<LabelBase> labels;
		protected List<Category> categories;
		protected List<Tag> tags;

		// =============================================*
		// コンストラクター
		// =============================================*
		protected AbstractBuilder() {}

		// =============================================*
		// セッター
		// =============================================*
		// -------------------------------------+
		// コア
		protected void setItemId(ItemId _val) {
			this.itemId = _val;
		}

//		protected void setUid(String _val) {
//			this.serialCode = _val;
//		}

		protected void setCreatedAt(Date _val) {
			this.createdAt = _val;
		}

		protected void setEnteredAt(Date _val) {
			this.enteredAt = _val;
		}

		protected void setState(ItemStateType _val) {
			this.state = _val;
		}

		// -------------------------------------+
		// キャッシュ
		protected void setLabels(List<LabelBase> _val) {
			this.labels = _val;
		}

		protected void setCategories(List<Category> _val) {
			this.categories = _val;
		}

		protected void setTags(List<Tag> _val) {
			this.tags = _val;
		}

		// =============================================*
		// セッター
		// =============================================*
		// -------------------------------------+
		// コア
		abstract protected <T extends AbstractBuilder> T itemId(ItemId _val);

//		protected AbstractBuilder serialCode(String _val);

		abstract protected <T extends AbstractBuilder> T createdAt(Date _val);

		abstract protected <T extends AbstractBuilder> T enteredAt(Date _val);

		abstract protected <T extends AbstractBuilder> T state(
				ItemStateType _val);

		// -------------------------------------+
		// キャッシュ
		abstract protected <T extends AbstractBuilder> T labels(
				List<LabelBase> _val);

		abstract protected <T extends AbstractBuilder> T categories(
				List<Category> _val);

		abstract protected <T extends AbstractBuilder> T tags(
				List<Tag> _val);

		// =============================================*
		// ビルド
		// =============================================*
		abstract protected <T extends ItemBase> T build();

	}

	/* ************************************************************ */
	/*
	 * クラス情報スタティックヘルパ
	 *
	 * @see models.reposts.IRepostElement#getMergedKeyColName()
	 */
	/* ************************************************************ */
	// SQLにて使用
	public static final String SERIAL_CODE_COL_NAME = "serialCode";
	public static final String ITEMID_COL_NAME = "itemId";
	public static final String CREATED_AT_COL_NAME = "createdAt";
	public static final String ENTERED_AT_COL_NAME = "enteredAt";

	public static String getSerialCodeColName() {
		return SERIAL_CODE_COL_NAME;
	}

	/* ************************************************************ */
	/*
	 * プロパティ初期化.
	 */
	/* ************************************************************ */
	public void initWhole() {
		initDates();
		initState();
		initSubs();
	}

	public void initDates() {
		Date now = new Date();
		setCreatedAt(now);
		setEnteredAt(now);
	}

	public void initState() {
		this.state = ItemStateType.OK;
	}

	abstract public void initSubs();

	// -------------------------------------+
	/**
	 * シリアルコードの作成.
	 */
	abstract protected String createSerialCode();

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
		setupSubs();
	}

	abstract public void setupSubs();

	/* ************************************************************ */
	/*
	 * アクセッサ
	 */
	/* ************************************************************ */
	// キャッシュ
	abstract public List<Category> getCategories();

	abstract public void setCategories(List<Category> _categories);

	abstract public List<Tag> getTags();

	abstract public void setTags(List<Tag> _tags);

	// =============================================*
	/*
	 * 必須項目であり、nullは禁止の要素だが、例外の送出はしない。<br>
	 * バリデーションで一括処理できるよう、ここでは受け入れている。
	 */
	public void setCreatedAt(Date _date) {
		this.createdAt = MyDateUtils.safeCopyOrNull(_date);
	}

	public Date getCreatedAt() {
		return MyDateUtils.safeCopyOrNull(this.createdAt);
	}

	// =============================================*
	public void setEnteredAt(Date _date) {
		this.enteredAt = MyDateUtils.safeCopyOrNull(_date);
	}

	public Date getEnteredAt() {
		return MyDateUtils.safeCopyOrNull(this.enteredAt);
	}

	/* ************************************************************ */
	/*
	 * JPA関連
	 */
	/* ************************************************************ */
	@Override
	public void mergeSubs() {
		if (this.itemId != null && this.itemId.isPersistent() == false) {
			this.itemId.mergeSubs();
			this.itemId = itemId.merge();
		}
		this.labels = null;
		this.categories = null;
		this.tags = null;
	}

	/* ************************************************************ */
	/*
	 * 基本メソッド
	 */
	/* ************************************************************ */
	@Override
	public String toString() {
		return "[id=" + id
				+ ", itemId=" + itemId
				+ ", serialCode=" + serialCode
				+ "]";
	}

	/* ************************************************************ */
	/*
	 * 遅延生成
	 */
	/* ************************************************************ */
	// なし

	/* ************************************************************ */
	/*
	 * リレーショナルなフィールド関連
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * エンティティ型フィールドの永続化セット.
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public void setupSubsPersistent() {
		Logger.debug(">>ItemBase.setupSubsPersistent");
		this.isSubsPersistent = checkSubsPersistent();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * エンティティ型フィールドの永続化チェック.
	 *
	 * @return 永続化してるならTrue
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// TODO 見直し
	// authorどうする？
	private boolean checkSubsPersistent() {
		Logger.debug(">>ItemBase.checkSubsPersistent");

		if (this.itemId == null) {
//			|| this.contributors == null
//				|| this.categories == null) {

			return false;
		}

		// ItemId
		if (!checkPersistent(this.itemId)) {
			return false;
		}

		// categories
//		for (Category ctg : this.categories) {
//			if (!checkPersistent(ctg)) {
//				return false;
//			}
//		}

		return true;
	}

	private static boolean checkPersistent(MySuperModel _model) {
		if (_model == null) {
			return false;// 念のため
		}
		if (_model.isPersistent() == false && _model.validateAndSave() == false) {
			return false;
		}
		return true;
	}

	/* ************************************************************ */
	/*
	 * 検索ヘルパ
	 */
	/* ************************************************************ */
	// アイテム横断検索
	// Item種類の数だけ検索を実行してリストに追加する処理
	//
	// 挙動は確認しているが、コード自体は作りかけ
	public static List<ItemBase> fetchNamedItems(String... _words) {

		List<User> users = RepostUserTag.find(
				"SELECT r.user FROM RepostUserTag r "
						+ "WHERE r.tag.name = 'Red' "// TODO 引数の適用
		).fetch();
//		Logger.debug("users.size:" + users.size());

		List<Tweet> tweets = RepostTweetTag.find(
				"SELECT r.tweet FROM RepostTweetTag r "
						+ "WHERE r.tag.name = 'Red' "
				).fetch();
//		Logger.debug("tweets.size:" + tweets.size());

		// -------------------
		List<ItemBase> items = new ArrayList<ItemBase>();

		items.addAll(users);
		items.addAll(tweets);

		// TODO 並び替え実装

		return items;
	}

	public static enum OrderBy implements IOrderBy {

		ID_ASC("id" + ASC),
		ID_DESC("id" + DESC),
		SERIAL_CODE_ASC(SERIAL_CODE_COL_NAME + ASC),
		SERIAL_CODE_DESC(SERIAL_CODE_COL_NAME + DESC),
		ITEMID_ASC(ITEMID_COL_NAME + ASC),
		ITEMID_DESC(ITEMID_COL_NAME + DESC),
		ITEMID_ID_ASC(ITEMID_COL_NAME + ".id" + ASC),
		ITEMID_ID_DESC(ITEMID_COL_NAME + ".id" + DESC),
		DATE_OF_CREATION_ASC(CREATED_AT_COL_NAME + ASC),
		DATE_OF_CREATION_DESC(CREATED_AT_COL_NAME + DESC),
		DATE_OF_THE_ENTRY_ASC(ENTERED_AT_COL_NAME + ASC),
		DATE_OF_THE_ENTRY_DESC(ENTERED_AT_COL_NAME + DESC);

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
	 * インスタンスごとの、関連ラベルの収集
	 */
	/* ************************************************************ */
	abstract protected ItemType getItemType();

	abstract protected <T extends ItemBase> T getSelf();

	// =============================================*
	// TEMP
	public void fetchThisCategories() {
		this.categories = (List<Category>) RepostBase.findLabelsByItems(
				LabelType.CATEGORY,
				getItemType(),
				getSelf()
				);
	}

	public void fetchThisTags() {
		this.tags = (List<Tag>) RepostBase.findLabelsByItems(
				LabelType.TAG,
				getItemType(),
				getSelf()
				);
	}

	/* ************************************************************ */
	/*
	 * 関連ラベルの追加・削除
	 */
	/* ************************************************************ */
	// TEMP
	// 追加
	public <T extends ItemBase> T attachLabels(
			Account _contributor, LabelBase... _labels) {
		return (T) RepostBase.attachLabels(this, _contributor, _labels);
	}

	// 削除
	public <T extends ItemBase> T detachLabels(
			Account _contributor, LabelBase... _labels) {
		return (T) RepostBase.detachLabels(this, _contributor, _labels);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 文字列によるカテゴリーの追加.
	 *
	 * @param _contributor
	 * @param _names
	 * @return カテゴリー追加後のアイテム
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// TEMP
	public <T extends ItemBase> T attachCategories(
			Account _contributor, String... _names) {

		return attachCategories(
				_contributor, Category.getMatchedArrayByName(_names));
	}

	public <T extends ItemBase> T attachCategories(
			Account _contributor, Category... _categories) {

		return (T) this.attachLabels(_contributor, _categories);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 文字列によるタグの追加.
	 *
	 * @param _contributor
	 * @param _names
	 * @return タグ追加後のアイテム
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// TEMP
	public <T extends ItemBase> T attachTags(
			Account _contributor, String... _names) {

		return attachTags(
				_contributor,
				Tag.getMatchedArrayByName(_contributor, _names));
	}

	public <T extends ItemBase> T attachTags(
			Account _contributor, Tag... _tags) {

		return (T) this.attachLabels(_contributor, _tags);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 文字列によるカテゴリーの削除.
	 *
	 * @param _contributor
	 * @param _names
	 * @return カテゴリー削除後のアイテム
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// TEMP
	public <T extends ItemBase> T detachCategories(
			Account _contributor, String... _names) {

		return detachCategories(
				_contributor, Category.getMatchedArrayByName(_names));
	}

	public <T extends ItemBase> T detachCategories(
			Account _contributor, Category... _categories) {

		return (T) this.detachLabels(_contributor, _categories);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 文字列によるタグの削除.
	 *
	 * @param _contributor
	 * @param _names
	 * @return タグ削除後のアイテム
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// TEMP
	public <T extends ItemBase> T detachTags(
			Account _contributor, String... _names) {

		return detachTags(
				_contributor,
				Tag.getMatchedArrayByName(_contributor, _names));
	}

	public <T extends ItemBase> T detachTags(
			Account _contributor, Tag... _tags) {

		return (T) this.detachLabels(_contributor, _tags);
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+

