/*
 * 作成日 2013/01/01 リポスト試作からの取り入れ
 * 修正日 2013/01/07 適応のための簡単な手直し
 * 修正日 2013/01/11 JPAマージヘルパ追加
 * 修正日 2013/01/19 検索メソッドの直し
 */
package models.reposts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.JPABase;
import play.db.jpa.Model;

import models.*;
import models.items.*;
import models.items.twitter.*;
import models.labels.*;

/**
 * リポストモデル.
 * リポスト関連の統合化されたテーブル作成用。<br>
 * JPA的なメソッドやアノテーションを利用する上で<br>
 * Modelの継承クラスとなっているが、セーブはさせたくない。
 *
 * @author H.Kezuka
 */
@Entity
abstract public class RepostBase extends MySuperModel {

	// =============================================*
	// 定数
	// =============================================*

	// =============================================*
	// コア
	// =============================================*
	// Who
	@Required
	@Valid
	@OneToOne
	public Account contributor;

	// When
	@Required
	public Date repostedAt;

	// -------------------------------------+
	// What
	@Transient
	public ItemBase item;

	// Where
	@Transient
	public LabelBase label;

	/* ************************************************************ */
	/*
	 * コンストラクタ
	 */
	/* ************************************************************ */
	protected RepostBase(Account _contributor) {
		this.contributor = _contributor;
		this.repostedAt = new Date();
	}

	/* ************************************************************ */
	/*
	 * ファクトリー
	 */
	/* ************************************************************ */
	public static class Builder {

		private Account contributor;
		private Date repostedAt;
		private ItemBase item;
		private LabelBase label;

		private ItemType itemType;
		private LabelType labelType;

		// コンストラクタ
		public Builder() {}

		public Builder contributor(Account _val) {
			this.contributor = _val;
			return this;
		}

		public Builder repostedAt(Date _val) {
			this.repostedAt = _val;
			return this;
		}

		public Builder item(ItemBase _val) {
			this.item = _val;
			if (_val instanceof User) {
				itemType = ItemType.USER;
			}
			else if (_val instanceof Tweet) {
				itemType = ItemType.TWEET;
			}
			return this;
		}

		public Builder label(LabelBase _val) {
			this.label = _val;
			if (_val instanceof Category) {
				labelType = LabelType.CATEGORY;
			}
			else if (_val instanceof Tag) {
				labelType = LabelType.TAG;
			}
			return this;
		}

		// TODO 例外処理
		public RepostBase build() {
			if (itemType == ItemType.USER
					&& labelType == LabelType.CATEGORY) {

				return RepostUserCategory.findOrCreateAndSave(
						(User) this.item,
						(Category) this.label,
						this.contributor);
			}
			else if (itemType == ItemType.USER
					&& labelType == LabelType.TAG) {

				return RepostUserTag.findOrCreateAndSave(
						(User) this.item,
						(Tag) this.label,
						this.contributor);
			}
			else if (itemType == ItemType.TWEET
					&& labelType == LabelType.CATEGORY) {

				return RepostTweetCategory.findOrCreateAndSave(
						(Tweet) this.item,
						(Category) this.label,
						this.contributor);
			}
			else if (itemType == ItemType.USER
					&& labelType == LabelType.CATEGORY) {

				return RepostTweetTag.findOrCreateAndSave(
						(Tweet) this.item,
						(Tag) this.label,
						this.contributor);
			}
			return null;
		}
	}

	/* ************************************************************ */
	/*
	 * アクセッサ
	 */
	/* ************************************************************ */
	abstract protected void setItem();

	public ItemBase getItem() {
		setItem();
		return this.item;
	}

	abstract protected void setLabel();

	public LabelBase getLabel() {
		setLabel();
		return this.label;
	}

	/* ************************************************************ */
	/*
	 * JPA関連
	 */
	/* ************************************************************ */
	@Override
	public void mergeSubs() {
		if (this.contributor != null
				&& this.contributor.isPersistent() == false) {
			this.contributor.mergeSubs();
			this.contributor = contributor.merge();
		}
		this.label = null;
		this.item = null;
	}

	/* ************************************************************ */
	/*
	 * 基本メソッド
	 */
	/* ************************************************************ */

	/* ************************************************************ */
	/*
	 * 検索ヘルパ
	 */
	/* ************************************************************ */
	/*
	 * 命名ルール
	 * fetch～：private 汎用的なメソッド、引数も複雑め。
	 * find～ ：public 分岐要素をメソッド名に取り込んだもの。
	 * ................外部クラスからはこちらを使用する。
	 */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 検索ヘルパ用プライベートメソッド
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// SQLにて使用
	private static final String JPQL_BINDKEY_CONTRIBUTOR = "cntr";
	private static final String JPQL_BINDKEY_PRIMARY_ARGS = "args";
//	private static final String JPQL_BINDKEY_SECONDARY_ARGS = "subs";
	// =============================================*
	private static final String CONTORIBUTOR_COL_NAME = "contributor";
	private static final String REPOSTED_AT_COL_NAME = "repostedAt";
	private static final String ITEM_COL_NAME = "item";
	private static final String LABEL_COL_NAME = "label";
	// =============================================*
	private static Map<String, SqlData> ibyiSqls = new HashMap<String, SqlData>();
	private static Map<String, SqlData> lbylSqls = new HashMap<String, SqlData>();
	// -------------------------------------+
	private static Map<String, SqlData> ibylSqls = new HashMap<String, SqlData>();
	private static Map<String, SqlData> lbyiSqls = new HashMap<String, SqlData>();
	// -------------------------------------+
	private static Map<String, SqlData> ibyaSqls = new HashMap<String, SqlData>();
	private static Map<String, SqlData> lbyaSqls = new HashMap<String, SqlData>();
	// -------------------------------------+
	private static Map<String, SqlData> rbyiSqls = new HashMap<String, SqlData>();
	private static Map<String, SqlData> rbylSqls = new HashMap<String, SqlData>();
	private static Map<String, SqlData> rbyaSqls = new HashMap<String, SqlData>();
	// =============================================*
	private static Map<String, SqlCnf> ibyiSqlCnf = new HashMap<String, SqlCnf>();
	private static Map<String, SqlCnf> lbylSqlCnf = new HashMap<String, SqlCnf>();
	// -------------------------------------+
	private static Map<String, SqlCnf> ibylSqlCnf = new HashMap<String, SqlCnf>();
	private static Map<String, SqlCnf> lbyiSqlCnf = new HashMap<String, SqlCnf>();
	// -------------------------------------+
	private static Map<String, SqlCnf> ibyaSqlCnf = new HashMap<String, SqlCnf>();
	private static Map<String, SqlCnf> lbyaSqlCnf = new HashMap<String, SqlCnf>();
	// -------------------------------------+
	private static Map<String, SqlCnf> rbyiSqlCnf = new HashMap<String, SqlCnf>();
	private static Map<String, SqlCnf> rbylSqlCnf = new HashMap<String, SqlCnf>();
	private static Map<String, SqlCnf> rbyaSqlCnf = new HashMap<String, SqlCnf>();
	// =============================================*
	private static final SqlName SQLNAME_REPOSTED_ITEM =
			new SqlName(1, "RepostedItem");
	private static final SqlName SQLNAME_REPOSTED_LABEL =
			new SqlName(2, "RepostedLabel");
	private static final SqlName SQLNAME_ITEM_BY_LABEL =
			new SqlName(3, "ItemByLabel");
	private static final SqlName SQLNAME_LABEL_BY_ITEM =
			new SqlName(4, "LabelByItem");
	private static final SqlName SQLNAME_ITEM_BY_ACCOUNT =
			new SqlName(5, "ItemByAccount");
	private static final SqlName SQLNAME_LABEL_BY_ACCOUNT =
			new SqlName(6, "LabelByAccount");
	private static final SqlName SQLNAME_REPOST_BY_ITEM =
			new SqlName(7, "RepostByItem");
	private static final SqlName SQLNAME_REPOST_BY_LABEL =
			new SqlName(8, "RepostByLabel");
	private static final SqlName SQLNAME_REPOST_BY_ACCOUNT =
			new SqlName(9, "RepostByAccount");

	// TODO 呼び出し考える
	static {
		setSql();
	}

	private static enum FetchTarget {
		REPOST,
		OTHERS,
	}

	private static enum ArgsType {

		NULL(true),
		ENTITY(false),
		STRING(false);

		private boolean isNull;

		ArgsType(boolean _isNull) {
			this.isNull = _isNull;
		}

		private boolean isNull() {
			return this.isNull;
		}

		private boolean isNotNull() {
			return !this.isNull;
		}
	}

	private static class SqlCnf {

		private String name;
		private String index;
//		private IRepostElementType secondaryType;
		private boolean isSingleArg;
		private ArgsType primaryArgsType = ArgsType.NULL;
//		private ArgsType secondaryArgsType = ArgsType.NULL;
		private ArgsType contributorArgsType = ArgsType.NULL;
		private FetchTarget from = FetchTarget.REPOST;
		private FetchTarget to = FetchTarget.REPOST;
		private String resultColName;
		private ColNames primaryColNames = new ColNames();
//		private ColNames secondaryColNames = new ColNames();
		private String pairClassName;
		private String primaryClassColName;

		public String getOrderBySql(IOrderBy _orderBy) {
			StringBuilder sb = new StringBuilder();
			sb.append("ORDER BY r");
			if (isNotBlank(this.resultColName)) {
				sb.append(".");
				sb.append(resultColName);
			}
			sb.append(".");
			sb.append(_orderBy.getSql());
			return sb.toString();
		}

	}

	private static class SqlData {

		String key;
		String index;
		String name;
		String sql;
		String orderBySql;

		private SqlData(
				String _key, String _index,
				String _name, String _sql) {

			this.key = _key;
			this.index = _index;
			this.name = _name;
			this.sql = _sql;
		}

		private SqlData(SqlData _org) {
			this.key = _org.key;
			this.index = _org.index;
			this.name = _org.name;
			this.sql = _org.sql;
		}

		private void printSqlIndex() {
			Logger.debug("SQL:" + index + "@" + name);
		}

		private void printSql() {
			Logger.debug("SQL name:" + name + " " + sql);
		}

	}

	private static class ColNames {
		protected String entity;
		protected String serialCode;
		protected String signature;
	}

	// SQLにて使用
//	private static String accountColName;
//
//	private static void setAccountColName() {
//		Field[] fs = RepostBase.class.getFields();
//		for (Field f : fs) {
//			if (f.getType() == Account.class) {
//				accountColName = f.getName();
//			}
//		}
//	}
//
//	public static String getAccountColName() {
//		if (accountColName == null) {
//			setAccountColName();
//		}
//		return accountColName;
//	}

	// String accountColNameというフィールドがあるのに注意
	private static ColNames accountColNames;

	private static void setSqlCol() {
		if (accountColNames == null) {
			accountColNames = new ColNames();
			accountColNames.entity = CONTORIBUTOR_COL_NAME;
			accountColNames.serialCode = getContributorUniqueColName();
			accountColNames.signature = getContributorSignatureColName();
//			Logger.debug("accountColNames.entity:"+accountColNames.entity);
//			Logger.debug("accountColNames.siqnature:"+accountColNames.siqnature);
		}
	}

	private static String getContributorUniqueColName() {
		return CONTORIBUTOR_COL_NAME
				+ "." + Account.getUniqueColName();
	}

	private static String getContributorSignatureColName() {
		return CONTORIBUTOR_COL_NAME
				+ "." + Account.getSignatureColName();
	}

	public static String getPairClassName(ItemType _i, LabelType _n) {

		if (_i == ItemType.USER && _n == LabelType.CATEGORY) {
			return RepostUserCategory.class.getSimpleName();
		}
		else if (_i == ItemType.USER && _n == LabelType.TAG) {
			return RepostUserTag.class.getSimpleName();
		}
		else if (_i == ItemType.TWEET && _n == LabelType.CATEGORY) {
			return RepostTweetCategory.class.getSimpleName();
		}
		else if (_i == ItemType.TWEET && _n == LabelType.TAG) {
			return RepostTweetTag.class.getSimpleName();
		}

		// 引数にnullが含まれる場合
		return RepostBase.class.getSimpleName();
	}

	public static String getPrimaryClassName(IRepostElementType _t) {

		if (_t == ItemType.USER) {
			return ItemType.USER.getLowerClassName();
		}
		else if (_t == ItemType.TWEET) {
			return ItemType.TWEET.getLowerClassName();
		}
		else if (_t == LabelType.CATEGORY) {
			return LabelType.CATEGORY.getLowerClassName();
		}
		else if (_t == LabelType.TAG) {
			return LabelType.TAG.getLowerClassName();
		}

		return null;
	}

//	private static void printSqlCnf(SqlCnf _cnf) {
//		Logger.debug("name" + _cnf.name);
//		Logger.debug("primaryArgsType         :" + _cnf.primaryArgsType);
//		Logger.debug("fetchType        :" + _cnf.fetchType);
//		Logger.debug("resultColName    :" + _cnf.resultColName);
//		Logger.debug("instanceColName  :" + _cnf.primaryColNames.entity);
//		Logger.debug("stringColName    :" + _cnf.primaryColNames.siqnature);
//		Logger.debug("additionalSql:" + _cnf.additionalSql);
//		Logger.debug("pairClassName    :" + _cnf.pairClassName);
//		Logger.debug("withAccount      :" + _cnf.withAccount);
//	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 《privateメソッド》
	 * SQL文字列作成コア
	 *
	 * @param _cnf
	 * 文字列作成用の情報集積オブジェクト
	 *
	 * @return SQL文字列
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static String createSqlCore(SqlCnf _cnf) {

//		printSqlCnf(_cnf);
		setSqlCol();

		StringBuilder sb = new StringBuilder();

		/*
		 * MySQLなら入っていて問題なし
		 * また、検索キーが単数でも結果内に同じデータが重複する
		 * こともあるので、そもそもこの分岐は意味ない。
		 * distinct = (_cnf.isSingleArg) ? "" : "DISTINCT"
		 */
		String distinct = "DISTINCT";

		if (_cnf.to == FetchTarget.REPOST) {
			sb.append("SELECT ");
			sb.append(distinct);
			sb.append(" r ");
		}
		else {
			sb.append("SELECT ");
			sb.append(distinct);
			sb.append(" r.");
			sb.append(_cnf.resultColName);
		}
		if (_cnf.from == FetchTarget.REPOST) {
			sb.append(" FROM ");
			sb.append(RepostBase.class.getSimpleName());
			sb.append(" r ");
		}
		else {
			sb.append(" FROM ");
			sb.append(_cnf.pairClassName);// RepostBaseの場合あり
			sb.append(" r ");
		}

//		boolean hasCondition = false;
		String joint = "WHERE";

		switch (_cnf.primaryArgsType) {
		case NULL:
			break;
		case ENTITY:
			sb.append(joint);// WHERE
			sb.append(" r.");
			sb.append(_cnf.primaryColNames.entity);
			if (_cnf.isSingleArg) {
				sb.append(" = :");
				sb.append(JPQL_BINDKEY_PRIMARY_ARGS);
				sb.append(" ");
			}
			else {
				sb.append(" in (:");
				sb.append(JPQL_BINDKEY_PRIMARY_ARGS);
				sb.append(") ");
			}
			joint = "AND";
			break;
		case STRING:
			sb.append(joint);// WHERE
			sb.append(" r.");
			sb.append(_cnf.primaryColNames.serialCode);
			if (_cnf.isSingleArg) {
				sb.append(" = :");
				sb.append(JPQL_BINDKEY_PRIMARY_ARGS);
				sb.append(" ");
			}
			else {
				sb.append(" in (:");
				sb.append(JPQL_BINDKEY_PRIMARY_ARGS);
				sb.append(") ");
			}
			joint = "AND";
			break;
		}
//FIXME
		if (isNotBlank(_cnf.primaryClassColName)) {
			sb.append(joint);// WHERE or AND
			sb.append(" r.");
			sb.append(_cnf.primaryClassColName);
			sb.append(" IS NOT NULL ");
			joint = "AND";
		}

//		// primaryArgsがない場合はsecondaryも無効
//		if (_cnf.secondaryArgsType != null &&
//				_cnf.secondaryArgsType != ArgsType.NULL &&
//			_cnf.primaryArgsType != ArgsType.NULL) {
//
//			sb.append((hasCondition) ? "AND " : "WHERE ");
//			sb.append(" r.");
//
//			if (_cnf.secondaryArgsType == ArgsType.ENTITY) {
//				sb.append(_cnf.secondaryColNames.entity);
//			}
//			else {
//				sb.append(_cnf.secondaryColNames.serialCode);
//			}
//			sb.append(" = :");
//			sb.append(JPQL_BINDKEY_SECONDARY_ARGS);
//			sb.append(" ");
//
//			hasCondition = true;
//		}

		// contributorは引数を単数のみとする。
		// （複数のcontributor情報での検索はなし）
		if (_cnf.contributorArgsType != null &&
				_cnf.contributorArgsType != ArgsType.NULL) {

			sb.append(joint);// WHERE or AND
			sb.append(" r.");

			if (_cnf.contributorArgsType == ArgsType.ENTITY) {
				sb.append(accountColNames.entity);
			}
			else {
				sb.append(accountColNames.serialCode);
			}
			sb.append(" = :");
			sb.append(JPQL_BINDKEY_CONTRIBUTOR);
			sb.append(" ");
//			joint = "AND";
		}

//		sb.append("GROUP BY r.id ");

		// デバッグプリント
		System.out.println(
				"sql@" + _cnf.index + "::"
						+ _cnf.name + ">>" + sb.toString());

		return sb.toString();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

	// =============================================*
	/*
	 * 全体に共通
	 *
	 * [SAMPLE]
	 * ◇(追加)アカウント指定あり
	 * + "AND r.contributor = :cntr "
	 * + "AND r.contributor.loginUser.screenName = :cntr "
	 *
	 * ◇(追加)追加SQL文字列あり
	 * + "<追加要素のためのSQL文字列>"
	 */
	// =============================================*

	// Item -> Item
	// =============================================*
	/*
	 * [SAMPLE]
	 * ◇検索キーがない場合
	 * ・LabelType指定なし
	 * "SELECT r.tweet FROM RepostBase r "
	 *
	 * ・LabelType指定あり
	 * "SELECT r.tweet FROM RepostTweetCategory r "
	 *
	 * ◇検索キーがある場合
	 * ・Entity
	 * "SELECT r.tweet FROM RepostTweetCategory r "
	 * + "WHERE r.tweet in (:args)"
	 *
	 * ・String
	 * "SELECT r.tweet FROM RepostTweetCategory r "
	 * + "WHERE r.tweet.statusId in (:args)"
	 */
	// =============================================*
	/*
	 * 《privateメソッド》
	 * SQL作成・リポストされたアイテム検索用.
	 *
	 * @param _itemType
	 * 抽出したいアイテム種類
	 *
	 * @param _labelType
	 * 関連するラベル種類、省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _isSingleArg
	 * 検索キーが単数か否か
	 *
	 * @param _primaryArgsType
	 * 検索キーがエンティティか文字列かの情報
	 *
	 * @param _contributorArgsType
	 * 投稿者キーがエンティティか文字列かの情報
	 *
	 * @return SQL文字列
	 */
	private static SqlCnf createSqlCnfForRepostedItem(
			String _index, String _name,
			ItemType _itemType, LabelType _labelType,
			// IRepostElementType _secondaryType,
			boolean _isSingleArg,
			ArgsType _primaryArgsType,
			// ArgsType _secondaryArgsType,
			ArgsType _contributorArgsType) {

		SqlCnf cnf = new SqlCnf();

		cnf.index = _index;
		cnf.name = _name;
//		cnf.secondaryType = _secondaryType;
		cnf.isSingleArg = _isSingleArg;
		cnf.primaryArgsType = _primaryArgsType;
//		cnf.secondaryArgsType = _secondaryArgsType;
		cnf.contributorArgsType = _contributorArgsType;
		cnf.resultColName = _itemType.getLowerClassName();
		cnf.primaryColNames.entity = _itemType.getLowerClassName();
		cnf.primaryColNames.serialCode = _itemType.getMergedSerialCodeColName();
		cnf.primaryColNames.signature = _itemType.getMergedSignatureColName();
		cnf.pairClassName = getPairClassName(_itemType, _labelType);
		cnf.primaryClassColName = getPrimaryClassName(_itemType);
		cnf.from = getFetchTagget(cnf.pairClassName);
		cnf.to = FetchTarget.OTHERS;

		return cnf;
	}

	private static String createSqlForRepostedItem(SqlCnf _cnf) {
		return createSqlCore(_cnf);
	}

	// Label -> Label
	// =============================================*
	/*
	 * [SAMPLE]
	 * - ALL -
	 * "SELECT r.category FROM RepostTweetCategory r "
	 *
	 * - ENTITIES -
	 * "SELECT r.category FROM RepostTweetCategory r "
	 * + "WHERE r.category in (:args)"
	 *
	 * - STRINGS -
	 * "SELECT r.category FROM RepostTweetCategory r "
	 * + "WHERE r.category.displayName in (:args)"
	 */
	// =============================================*
	/*
	 * 《privateメソッド》
	 * SQL作成・リポストされたラベル検索用.
	 *
	 * @param _labelType
	 * 抽出したいラベル種類
	 *
	 * @param _itemType
	 * 関連するアイテム種類、省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _isSingleArg
	 * 検索キーが単数か否か
	 *
	 * @param _primaryArgsType
	 * 検索キーがエンティティか文字列かの情報
	 *
	 * @param _contributorArgsType
	 * 投稿者キーがエンティティか文字列かの情報
	 *
	 * @return SQL文字列
	 */
	private static SqlCnf createSqlCnfForRepostedLabel(
			String _index, String _name,
			LabelType _labelType, ItemType _itemType,
			// IRepostElementType _secondaryType,
			boolean _isSingleArg,
			ArgsType _primaryArgsType,
			// ArgsType _secondaryArgsType,
			ArgsType _contributorArgsType) {

		SqlCnf cnf = new SqlCnf();

		cnf.name = _name;
		cnf.index = _index;
//		cnf.secondaryType = _secondaryType;
		cnf.isSingleArg = _isSingleArg;
		cnf.primaryArgsType = _primaryArgsType;
//		cnf.secondaryArgsType = _secondaryArgsType;
		cnf.contributorArgsType = _contributorArgsType;
		cnf.resultColName = _labelType.getLowerClassName();
		cnf.primaryColNames.entity = _labelType.getLowerClassName();
		cnf.primaryColNames.serialCode = _labelType
				.getMergedSerialCodeColName();
		cnf.primaryColNames.signature = _labelType.getMergedSignatureColName();
		cnf.pairClassName = getPairClassName(_itemType, _labelType);
		cnf.primaryClassColName = getPrimaryClassName(_labelType);
		cnf.from = getFetchTagget(cnf.pairClassName);
		cnf.to = FetchTarget.OTHERS;

		return cnf;
	}

	private static String createSqlForRepostedLabel(SqlCnf _cnf) {
		return createSqlCore(_cnf);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

	// =============================================*
	/*
	 * [SAMPLE]
	 * <ItemsByLabels>
	 * - ENTITIES -
	 * "SELECT r.tweet FROM RepostTweetCategory r "
	 * + "WHERE r.category in (:args)"
	 *
	 * - STRINGS -
	 * "SELECT r.tweet FROM RepostTweetCategory r "
	 * + "WHERE r.category.displayName in (:args)"
	 */
	// =============================================*
	/*
	 * 《privateメソッド》
	 * SQL作成・リポストされたアイテム検索用.
	 * ラベルを引数に関連のあるアイテムを抽出する。
	 *
	 * @param _itemType
	 * 抽出したいアイテム種類
	 *
	 * @param _labelType
	 * 関連するラベル種類、省略不可
	 *
	 * @param _isSingleArg
	 * 検索キーが単数か否か
	 *
	 * @param _primaryArgsType
	 * 検索キーがエンティティか文字列かの情報
	 *
	 * @param _contributorArgsType
	 * 投稿者キーがエンティティか文字列かの情報
	 *
	 * @return SQL文字列
	 */
	private static SqlCnf createSqlCnfFindItemByLabel(
			String _index, String _name,
			ItemType _itemType, LabelType _labelType,
			// IRepostElementType _secondaryType,
			boolean _isSingleArg,
			ArgsType _primaryArgsType,
			// ArgsType _secondaryArgsType,
			ArgsType _contributorArgsType) {

		SqlCnf cnf = new SqlCnf();

		cnf.name = _name;
		cnf.index = _index;
//		cnf.secondaryType = _secondaryType;
		cnf.isSingleArg = _isSingleArg;
		cnf.primaryArgsType = _primaryArgsType;
//		cnf.secondaryArgsType = _secondaryArgsType;
		cnf.contributorArgsType = _contributorArgsType;
		cnf.resultColName = _itemType.getLowerClassName();
		cnf.primaryColNames.entity = _labelType.getLowerClassName();
		cnf.primaryColNames.serialCode = _labelType
				.getMergedSerialCodeColName();
		cnf.primaryColNames.signature = _labelType.getMergedSignatureColName();
		cnf.pairClassName = getPairClassName(_itemType, _labelType);
		cnf.primaryClassColName = getPrimaryClassName(_itemType);
		cnf.from = FetchTarget.OTHERS;
		cnf.to = FetchTarget.OTHERS;

		return cnf;
	}

	private static String createSqlFindItemByLabel(SqlCnf _cnf) {
		return createSqlCore(_cnf);
	}

	// =============================================*
	/*
	 * [SAMPLE]
	 * <LabelsByItems>
	 * - ENTITIES -
	 * "SELECT r.category FROM RepostTweetCategory r "
	 * + "WHERE r.tweet in (:args)"
	 *
	 * - STRINGS -
	 * "SELECT r.category FROM RepostTweetCategory r "
	 * + "WHERE r.tweet.statusId in (:args)"
	 */
	// =============================================*
	/*
	 * 《privateメソッド》
	 * SQL作成・リポストされたラベル検索用.
	 * アイテムを引数に関連のあるラベルを抽出する。
	 *
	 * @param _labelType
	 * 抽出したいラベル種類
	 *
	 * @param _itemType
	 * 関連するアイテム種類、省略不可
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _isSingleArg
	 * 検索キーが単数か否か
	 *
	 * @param _primaryArgsType
	 * 検索キーがエンティティか文字列かの情報
	 *
	 * @param _contributorArgsType
	 * 投稿者キーがエンティティか文字列かの情報
	 *
	 * @return SQL文字列
	 */
	private static SqlCnf createSqlCnfFindLabelByItem(
			String _index, String _name,
			LabelType _labelType, ItemType _itemType,
			// IRepostElementType _secondaryType,
			boolean _isSingleArg,
			ArgsType _primaryArgsType,
			// ArgsType _secondaryArgsType,
			ArgsType _contributorArgsType) {

		SqlCnf cnf = new SqlCnf();

		cnf.name = _name;
		cnf.index = _index;
//		cnf.secondaryType = _secondaryType;
		cnf.isSingleArg = _isSingleArg;
		cnf.primaryArgsType = _primaryArgsType;
//		cnf.secondaryArgsType = _secondaryArgsType;
		cnf.contributorArgsType = _contributorArgsType;
		cnf.resultColName = _labelType.getLowerClassName();
		cnf.primaryColNames.entity = _itemType.getLowerClassName();
		cnf.primaryColNames.serialCode = _itemType.getMergedSerialCodeColName();
		cnf.primaryColNames.signature = _itemType.getMergedSignatureColName();
		cnf.pairClassName = getPairClassName(_itemType, _labelType);
		cnf.primaryClassColName = getPrimaryClassName(_labelType);
		cnf.from = FetchTarget.OTHERS;
		cnf.to = FetchTarget.OTHERS;

		return cnf;
	}

	private static String createSqlFindLabelByItem(SqlCnf _cnf) {
		return createSqlCore(_cnf);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static FetchTarget getFetchTagget(String _pairClassName) {
		if (RepostBase.class.getSimpleName().equals(_pairClassName)) {
			return FetchTarget.REPOST;
		}
		return FetchTarget.OTHERS;
	}

	// Account -> Item
	// =============================================*
	/*
	 * [SAMPLE]
	 * <ItemsByAccounts>
	 * - ENTITIES -
	 * "SELECT r.tweet FROM RepostBase r "
	 * + "WHERE r.contributor in (:args)"
	 *
	 * - STRINGS -
	 * "SELECT r.tweet FROM RepostBase r "
	 * + "WHERE r.contributor.loginUser.screenName in (:args)"
	 */
	// =============================================*
	/*
	 * 《privateメソッド》
	 * SQL作成・リポストされたアイテム検索用.
	 * 投稿者を引数に関連のあるアイテムを抽出する。<br>
	 * 複数のアカウントをキーに検索する場合のもので、<br>
	 * 他でも使われている追加条件としてのcontributorは<br>
	 * 単数のみの検索であり、このSQLでは使われない。
	 *
	 * @param _itemType
	 * 抽出したいアイテム種類
	 *
	 * @param _labelType
	 * 絞り込み要素<br>
	 * 関連するラベル種類、省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _isSingleArg
	 * 検索キーが単数か否か
	 *
	 * @param _primaryArgsType
	 * 検索キーがエンティティか文字列かの情報
	 *
	 * @return SQL文字列
	 */
	private static SqlCnf createSqlCnfFindItemByAccount(
			String _index, String _name,
			ItemType _itemType, LabelType _labelType,
			// IRepostElementType _secondaryType,
			boolean _isSingleArg,
			ArgsType _primaryArgsType) {
//			ArgsType _secondaryArgsType) {

		SqlCnf cnf = new SqlCnf();

		cnf.name = _name;
		cnf.index = _index;
//		cnf.secondaryType = _secondaryType;
		cnf.isSingleArg = _isSingleArg;
		cnf.primaryArgsType = _primaryArgsType;
//		cnf.secondaryArgsType = _secondaryArgsType;
		cnf.contributorArgsType = ArgsType.NULL;
		cnf.resultColName = _itemType.getLowerClassName();
		cnf.primaryColNames.entity = accountColNames.entity;
		cnf.primaryColNames.serialCode = accountColNames.serialCode;
		cnf.primaryColNames.signature = accountColNames.signature;
		cnf.pairClassName = getPairClassName(_itemType, _labelType);
		cnf.primaryClassColName = getPrimaryClassName(_itemType);
		cnf.from = getFetchTagget(cnf.pairClassName);
		cnf.to = FetchTarget.OTHERS;

		return cnf;
	}

	private static String createSqlFindItemByAccount(SqlCnf _cnf) {
		return createSqlCore(_cnf);
	}

	// Account -> Label
	// =============================================*
	/*
	 * [SAMPLE]
	 * <LabelsByAccounts>
	 * - ENTITIES -
	 * "SELECT r.category FROM RepostBase r "
	 * + "WHERE r.contributor in (:args)"
	 *
	 * - STRINGS -
	 * "SELECT r.category FROM RepostBase r "
	 * + "WHERE r.contributor.loginUser.screenName in (:args)"
	 */
	// =============================================*
	/*
	 * 《privateメソッド》
	 * SQL作成・リポストされたラベル検索用.
	 * 投稿者を引数に関連のあるラベルを抽出する。<br>
	 * 複数のアカウントをキーに検索する場合のもので、<br>
	 * 他でも使われている追加条件としてのcontributorは<br>
	 * 単数のみの検索であり、このSQLでは使われない。
	 *
	 * @param _labelType
	 * 抽出したいラベル種類
	 *
	 * @param _itemType
	 * 絞り込み要素<br>
	 * 関連するアイテム種類、省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _isSingleArg
	 * 検索キーが単数か否か
	 *
	 * @param _primaryArgsType
	 * 検索キーがエンティティか文字列かの情報
	 *
	 * @return SQL文字列
	 */
	private static SqlCnf createSqlCnfFindLabelByAccount(
			String _index, String _name,
			LabelType _labelType, ItemType _itemType,
			// IRepostElementType _secondaryType,
			boolean _isSingleArg,
			ArgsType _primaryArgsType) {
//			ArgsType _secondaryArgsType) {

		SqlCnf cnf = new SqlCnf();

		cnf.name = _name;
		cnf.index = _index;
//		cnf.secondaryType = _secondaryType;
		cnf.isSingleArg = _isSingleArg;
		cnf.primaryArgsType = _primaryArgsType;
//		cnf.secondaryArgsType = _secondaryArgsType;
		cnf.contributorArgsType = ArgsType.NULL;
		cnf.resultColName = _labelType.getLowerClassName();
		cnf.primaryColNames.entity = accountColNames.entity;
		cnf.primaryColNames.serialCode = accountColNames.serialCode;
		cnf.primaryColNames.signature = accountColNames.signature;
		cnf.pairClassName = getPairClassName(_itemType, _labelType);
		cnf.primaryClassColName = getPrimaryClassName(_labelType);
		cnf.from = getFetchTagget(cnf.pairClassName);
		cnf.to = FetchTarget.OTHERS;

		return cnf;
	}

	private static String createSqlFindLabelByAccount(SqlCnf _cnf) {
		return createSqlCore(_cnf);
	}

	// Item -> Repost
	// =============================================*
	/*
	 * [SAMPLE]
	 * - ENTITIES -
	 * "SELECT r FROM RepostBase r "
	 * "SELECT r FROM RepostTweetCategory r "
	 * + "WHERE r.tweet in (:args)"
	 *
	 * - STRINGS -
	 * "SELECT r FROM RepostBase r "
	 * "SELECT r FROM RepostTweetCategory r "
	 * + "WHERE r.tweet.statusId in (:args)"
	 */
	// =============================================*
	/*
	 * 《privateメソッド》
	 * SQL作成・アイテムによるリポストの検索用.
	 *
	 * @param _itemType
	 * 関連するアイテム種類、省略不可
	 *
	 * @param _labelType
	 * 絞り込み要素<br>
	 * 関連するラベル種類、省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _isSingleArg
	 * 検索キーが単数か否か
	 *
	 * @param _primaryArgsType
	 * 検索キーがエンティティか文字列かの情報
	 *
	 * @param _contributorArgsType
	 * 投稿者キーがエンティティか文字列かの情報
	 *
	 * @return SQL文字列
	 */
	private static SqlCnf createSqlCnfFindRepostByItem(
			String _index, String _name,
			ItemType _itemType, LabelType _labelType,
			// IRepostElementType _secondaryType,
			boolean _isSingleArg,
			ArgsType _primaryArgsType,
			// ArgsType _secondaryArgsType,
			ArgsType _contributorArgsType) {

		SqlCnf cnf = new SqlCnf();

		cnf.name = _name;
		cnf.index = _index;
//		cnf.secondaryType = _secondaryType;
		cnf.isSingleArg = _isSingleArg;
		cnf.primaryArgsType = _primaryArgsType;
//		cnf.secondaryArgsType = _secondaryArgsType;
		cnf.contributorArgsType = _contributorArgsType;
		cnf.resultColName = "";
		cnf.primaryColNames.entity = _itemType.getLowerClassName();
		cnf.primaryColNames.serialCode = _itemType.getMergedSerialCodeColName();
		cnf.primaryColNames.signature = _itemType.getMergedSignatureColName();
		cnf.pairClassName = getPairClassName(_itemType, _labelType);
		cnf.primaryClassColName = getPrimaryClassName(_itemType);
		cnf.from = getFetchTagget(cnf.pairClassName);
		cnf.to = FetchTarget.REPOST;

		return cnf;
	}

	private static String createSqlFindRepostByItem(SqlCnf _cnf) {
		return createSqlCore(_cnf);
	}

	// Label -> Repost
	// =============================================*
	/*
	 * [SAMPLE]
	 * - ENTITIES -
	 * "SELECT r FROM RepostBase r "
	 * "SELECT r FROM RepostTweetCategory r "
	 * + "WHERE r.category in (:args)"
	 *
	 * - STRINGS -
	 * "SELECT r FROM RepostBase r "
	 * "SELECT r FROM RepostTweetCategory r "
	 * + "WHERE r.category.displayName in (:args)"
	 */
	// =============================================*
	/*
	 * 《privateメソッド》
	 * SQL作成・ラベルによるリポストの検索用.
	 *
	 * @param _labelType
	 * 関連するラベル種類、省略不可
	 *
	 * @param _itemType
	 * 絞り込み要素<br>
	 * 関連するアイテム種類、省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _isSingleArg
	 * 検索キーが単数か否か
	 *
	 * @param _primaryArgsType
	 * 検索キーがエンティティか文字列かの情報
	 *
	 * @param _contributorArgsType
	 * 投稿者キーがエンティティか文字列かの情報
	 *
	 * @return SQL文字列
	 */
	private static SqlCnf createSqlCnfFindRepostByLabel(
			String _index, String _name,
			LabelType _labelType, ItemType _itemType,
			// IRepostElementType _secondaryType,
			boolean _isSingleArg,
			ArgsType _primaryArgsType,
			// ArgsType _secondaryArgsType,
			ArgsType _contributorArgsType) {

		SqlCnf cnf = new SqlCnf();

		cnf.name = _name;
		cnf.index = _index;
//		cnf.secondaryType = _secondaryType;
		cnf.isSingleArg = _isSingleArg;
		cnf.primaryArgsType = _primaryArgsType;
//		cnf.secondaryArgsType = _secondaryArgsType;
		cnf.contributorArgsType = _contributorArgsType;
		cnf.resultColName = "";
		cnf.primaryColNames.entity = _labelType.getLowerClassName();
		cnf.primaryColNames.serialCode = _labelType
				.getMergedSerialCodeColName();
		cnf.primaryColNames.signature = _labelType.getMergedSignatureColName();
		cnf.pairClassName = getPairClassName(_itemType, _labelType);
		cnf.primaryClassColName = getPrimaryClassName(_labelType);
		cnf.from = getFetchTagget(cnf.pairClassName);
		cnf.to = FetchTarget.REPOST;

		return cnf;
	}

	private static String createSqlFindRepostByLabel(SqlCnf _cnf) {
		return createSqlCore(_cnf);
	}

	// Account -> Repost
	// =============================================*
	/*
	 * [SAMPLE]
	 * - ENTITIES -
	 * "SELECT r FROM RepostBase r "
	 * "SELECT r FROM RepostTweetCategory r "
	 * + "WHERE r.contributor in (:args)"
	 *
	 * - STRINGS -
	 * "SELECT r FROM RepostBase r "
	 * "SELECT r FROM RepostTweetCategory r "
	 * + "WHERE r.contributor.loginUser.screenName in (:args)"
	 */
	// =============================================*
	/*
	 * 《privateメソッド》
	 * SQL作成・投稿者によるリポストの検索用.
	 *
	 * @param _labelType
	 * 絞り込み要素<br>
	 * 関連するラベル種類、省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _itemType
	 * 絞り込み要素<br>
	 * 関連するアイテム種類、省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _isSingleArg
	 * 検索キーが単数か否か
	 *
	 * @param _primaryArgsType
	 * 検索キーがエンティティか文字列かの情報
	 *
	 * @return SQL文字列
	 */
	private static SqlCnf createSqlCnfFindRepostByAccount(
			String _index, String _name,
			ItemType _itemType, LabelType _labelType,
			// IRepostElementType _secondaryType,
			boolean _isSingleArg,
			ArgsType _primaryArgsType) {
//			ArgsType _secondaryArgsType) {

		SqlCnf cnf = new SqlCnf();

		cnf.name = _name;
		cnf.index = _index;
//		cnf.secondaryType = _secondaryType;
		cnf.isSingleArg = _isSingleArg;
		cnf.primaryArgsType = _primaryArgsType;
//		cnf.secondaryArgsType = _secondaryArgsType;
		cnf.contributorArgsType = ArgsType.NULL;
		cnf.resultColName = "";
		cnf.primaryColNames.entity = accountColNames.entity;
		cnf.primaryColNames.serialCode = accountColNames.serialCode;
		cnf.primaryColNames.signature = accountColNames.signature;
		cnf.pairClassName = getPairClassName(_itemType, _labelType);
		cnf.primaryClassColName = getPrimaryClassName(null);
		cnf.from = getFetchTagget(cnf.pairClassName);
		cnf.to = FetchTarget.REPOST;

		return cnf;
	}

	private static String createSqlFindRepostByAccount(SqlCnf _cnf) {
		return createSqlCore(_cnf);
	}

//	private static void printIndex(int _i, int _j, int _k, int _m) {
//		System.out.print(_i + "." + _j + "." + _k + "." + _m + "@");
//	}

//	private static void printIndex(String _index) {
//		System.out.print(_index + "@");
//	}

	private static String createSqlIndex(
			int _sqlNumber, int _itm, int _lbl, int _sgl, int _prm, int _cntr) {

		return _sqlNumber
				+ "." + (++_itm)
				+ "." + (++_lbl)
				+ "." + (++_sgl)
				+ "." + (++_prm)
				+ "." + (++_cntr);
	}

	private static class SqlName {
		int number;
		String name;

		private SqlName(int _number, String _name) {
			this.number = _number;
			this.name = _name;
		}
	}

	private static void setSql() {

		String index = "";
		String name = "";
		String key = "";
		String sql = "";
		SqlCnf cnf;
		boolean isSingle = false;

		for (int itm = 0; itm < ItemType.values().length; itm++) {
			for (int lbl = 0; lbl < LabelType.values().length; lbl++) {
				for (int sgl = 0; sgl <= 1; sgl++) {
					isSingle = (sgl == 0) ? false : true;
					for (int prm = 0; prm < ArgsType.values().length; prm++) {
						for (int cntr = 0; cntr < ArgsType.values().length; cntr++) {

							index = createSqlIndex(
									SQLNAME_REPOSTED_ITEM.number,
									itm, lbl, sgl, prm, cntr);
							name = SQLNAME_REPOSTED_ITEM.name;
							key = keyStr(
									SQLNAME_REPOSTED_ITEM.number,
									ItemType.values()[itm],
									LabelType.values()[lbl],
									isSingle,
									ArgsType.values()[prm],
									ArgsType.values()[cntr]
									);
							cnf = createSqlCnfForRepostedItem(
									index,
									name,
									ItemType.values()[itm],
									LabelType.values()[lbl],
									isSingle,
									ArgsType.values()[prm],
									ArgsType.values()[cntr]
									);
							sql = createSqlForRepostedItem(cnf);

							ibyiSqlCnf.put(key, cnf);

							ibyiSqls.put(key,
									new SqlData(key, index, name, sql));

							index = createSqlIndex(
									SQLNAME_REPOSTED_LABEL.number,
									itm, lbl, sgl, prm, cntr);
							name = SQLNAME_REPOSTED_LABEL.name;
							key = keyStr(
									SQLNAME_REPOSTED_LABEL.number,
									LabelType.values()[lbl],
									ItemType.values()[itm],
									isSingle,
									ArgsType.values()[prm],
									ArgsType.values()[cntr]
									);
							cnf = createSqlCnfForRepostedLabel(
									index,
									name,
									LabelType.values()[lbl],
									ItemType.values()[itm],
									isSingle,
									ArgsType.values()[prm],
									ArgsType.values()[cntr]
									);
							sql = createSqlForRepostedLabel(cnf);

							lbylSqlCnf.put(key, cnf);

							lbylSqls.put(key,
									new SqlData(key, index, name, sql));

							// -------------------------------------+
							index = createSqlIndex(
									SQLNAME_REPOST_BY_ITEM.number,
									itm, lbl, sgl, prm, cntr);
							name = SQLNAME_REPOST_BY_ITEM.name;
							key = keyStr(
									SQLNAME_REPOST_BY_ITEM.number,
									ItemType.values()[itm],
									LabelType.values()[lbl],
									isSingle,
									ArgsType.values()[prm],
									ArgsType.values()[cntr]
									);
							cnf = createSqlCnfFindRepostByItem(
									index,
									name,
									ItemType.values()[itm],
									LabelType.values()[lbl],
									isSingle,
									ArgsType.values()[prm],
									ArgsType.values()[cntr]
									);
							sql = createSqlFindRepostByItem(cnf);

							rbyiSqlCnf.put(key, cnf);

							rbyiSqls.put(key,
									new SqlData(key, index, name, sql));

							index = createSqlIndex(
									SQLNAME_REPOST_BY_LABEL.number,
									itm, lbl, sgl, prm, cntr);
							name = SQLNAME_REPOST_BY_LABEL.name;
							key = keyStr(
									SQLNAME_REPOST_BY_LABEL.number,
									LabelType.values()[lbl],
									ItemType.values()[itm],
									isSingle,
									ArgsType.values()[prm],
									ArgsType.values()[cntr]
									);
							cnf = createSqlCnfFindRepostByLabel(
									index,
									name,
									LabelType.values()[lbl],
									ItemType.values()[itm],
									isSingle,
									ArgsType.values()[prm],
									ArgsType.values()[cntr]
									);
							sql = createSqlFindRepostByLabel(cnf);

							rbylSqlCnf.put(key, cnf);

							rbylSqls.put(key,
									new SqlData(key, index, name, sql));

							// -------------------------------------+
							if (ArgsType.values()[prm].isNotNull()) {

								index = createSqlIndex(
										SQLNAME_ITEM_BY_LABEL.number,
										itm, lbl, sgl, prm, cntr);
								name = SQLNAME_ITEM_BY_LABEL.name;
								key = keyStr(
										SQLNAME_ITEM_BY_LABEL.number,
										ItemType.values()[itm],
										LabelType.values()[lbl],
										isSingle,
										ArgsType.values()[prm],
										ArgsType.values()[cntr]
										);
								cnf = createSqlCnfFindItemByLabel(
										index,
										name,
										ItemType.values()[itm],
										LabelType.values()[lbl],
										isSingle,
										ArgsType.values()[prm],
										ArgsType.values()[cntr]
										);
								sql = createSqlFindItemByLabel(cnf);

								ibylSqlCnf.put(key, cnf);

								ibylSqls.put(key,
										new SqlData(key, index, name, sql));

								index = createSqlIndex(
										SQLNAME_LABEL_BY_ITEM.number,
										itm, lbl, sgl, prm, cntr);
								name = SQLNAME_LABEL_BY_ITEM.name;
								key = keyStr(
										SQLNAME_LABEL_BY_ITEM.number,
										LabelType.values()[lbl],
										ItemType.values()[itm],
										isSingle,
										ArgsType.values()[prm],
										ArgsType.values()[cntr]
										);
								cnf = createSqlCnfFindLabelByItem(
										index,
										name,
										LabelType.values()[lbl],
										ItemType.values()[itm],
										isSingle,
										ArgsType.values()[prm],
										ArgsType.values()[cntr]
										);
								sql = createSqlFindLabelByItem(cnf);

								lbyiSqlCnf.put(key, cnf);

								lbyiSqls.put(key,
										new SqlData(key, index, name, sql));

								// -------------------------------------+
								if (ArgsType.values()[cntr].isNull()) {
									index = createSqlIndex(
											SQLNAME_ITEM_BY_ACCOUNT.number,
											itm, lbl, sgl, prm, -1);
									name = SQLNAME_ITEM_BY_ACCOUNT.name;
									key = keyStr(
											SQLNAME_ITEM_BY_ACCOUNT.number,
											ItemType.values()[itm],
											LabelType.values()[lbl],
											isSingle,
											ArgsType.values()[prm],
											null
											);
									cnf = createSqlCnfFindItemByAccount(
											index,
											name,
											ItemType.values()[itm],
											LabelType.values()[lbl],
											isSingle,
											ArgsType.values()[prm]
											);
									sql = createSqlFindItemByAccount(cnf);

									ibyaSqlCnf.put(key, cnf);

									ibyaSqls.put(key, new SqlData(key, index,
											name,
											sql));

									index = createSqlIndex(
											SQLNAME_LABEL_BY_ACCOUNT.number,
											itm, lbl, sgl, prm, -1);
									name = SQLNAME_LABEL_BY_ACCOUNT.name;
									key = keyStr(
											SQLNAME_LABEL_BY_ACCOUNT.number,
											LabelType.values()[lbl],
											ItemType.values()[itm],
											isSingle,
											ArgsType.values()[prm],
											null
											);
									cnf = createSqlCnfFindLabelByAccount(
											index,
											name,
											LabelType.values()[lbl],
											ItemType.values()[itm],
											isSingle,
											ArgsType.values()[prm]
											);
									sql = createSqlFindLabelByAccount(cnf);

									lbyaSqlCnf.put(key, cnf);

									lbyaSqls.put(key, new SqlData(key, index,
											name,
											sql));

									// -------------------------------------+
									index = createSqlIndex(
											SQLNAME_REPOST_BY_ACCOUNT.number,
											itm, lbl, sgl, prm, cntr);
									name = SQLNAME_REPOST_BY_ACCOUNT.name;
									key = keyStr(
											SQLNAME_REPOST_BY_ACCOUNT.number,
											ItemType.values()[itm],
											LabelType.values()[lbl],
											isSingle,
											ArgsType.values()[prm],
											null
											);
//									Logger.debug("key:%s", key);
									cnf = createSqlCnfFindRepostByAccount(
											index,
											name,
											ItemType.values()[itm],
											LabelType.values()[lbl],
											isSingle,
											ArgsType.values()[prm]
											);
									sql = createSqlFindRepostByAccount(cnf);

									rbyaSqlCnf.put(key, cnf);

									rbyaSqls.put(key, new SqlData(key, index,
											name,
											sql));
								}
							}
						}
					}
				}
			}
		}

		// =============================================*
		// ItemTypeだけのループ
		for (int itm = 0; itm < ItemType.values().length; itm++) {
			for (int sgl = 0; sgl <= 1; sgl++) {
				isSingle = (sgl == 0) ? false : true;
				for (int prm = 0; prm < ArgsType.values().length; prm++) {
					for (int cntr = 0; cntr < ArgsType.values().length; cntr++) {

						index = createSqlIndex(
								SQLNAME_REPOSTED_ITEM.number,
								itm, -1, sgl, prm, cntr);
						name = SQLNAME_REPOSTED_ITEM.name;
						key = keyStr(
								SQLNAME_REPOSTED_ITEM.number,
								ItemType.values()[itm],
								null,
								isSingle,
								ArgsType.values()[prm],
								ArgsType.values()[cntr]
								);
						cnf = createSqlCnfForRepostedItem(
								index,
								name,
								ItemType.values()[itm],
								null,
								isSingle,
								ArgsType.values()[prm],
								ArgsType.values()[cntr]
								);
						sql = createSqlForRepostedItem(cnf);

						ibyiSqlCnf.put(key, cnf);

						ibyiSqls.put(key, new SqlData(key, index, name, sql));

						// -------------------------------------+
						index = createSqlIndex(
								SQLNAME_REPOST_BY_ITEM.number,
								itm, -1, sgl, prm, cntr);
						name = SQLNAME_REPOST_BY_ITEM.name;
						key = keyStr(
								SQLNAME_REPOST_BY_ITEM.number,
								ItemType.values()[itm],
								null,
								isSingle,
								ArgsType.values()[prm],
								ArgsType.values()[cntr]
								);
						cnf = createSqlCnfFindRepostByItem(
								index,
								name,
								ItemType.values()[itm],
								null,
								isSingle,
								ArgsType.values()[prm],
								ArgsType.values()[cntr]
								);
						sql = createSqlFindRepostByItem(cnf);

						rbyiSqlCnf.put(key, cnf);

						rbyiSqls.put(key,
								new SqlData(key, index, name, sql));
						// -------------------------------------+
						if (ArgsType.values()[prm].isNotNull()) {
							if (ArgsType.values()[cntr].isNull()) {

								index = createSqlIndex(
										SQLNAME_ITEM_BY_ACCOUNT.number,
										itm, -1, sgl, prm, -1);
								name = SQLNAME_ITEM_BY_ACCOUNT.name;
								key = keyStr(
										SQLNAME_ITEM_BY_ACCOUNT.number,
										ItemType.values()[itm],
										null,
										isSingle,
										ArgsType.values()[prm],
										null
										);
								cnf = createSqlCnfFindItemByAccount(
										index,
										name,
										ItemType.values()[itm],
										null,
										isSingle,
										ArgsType.values()[prm]
										);
								sql = createSqlFindItemByAccount(cnf);
//Logger.debug("key:%s", key);
								ibyaSqlCnf.put(key, cnf);

								ibyaSqls.put(key,
										new SqlData(key, index, name, sql));
//								// -------------------------------------+
//
//								index = createSqlIndex(
//										SQLNAME_REPOST_BY_ACCOUNT.number,
//										itm, -1, sgl, prm, -1);
//								name = SQLNAME_REPOST_BY_ACCOUNT.name;
//								key = keyStr(
//										SQLNAME_REPOST_BY_ACCOUNT.number,
//										ItemType.values()[itm],
//										null,
//										isSingle,
//										ArgsType.values()[prm],
//										null
//										);
//
////								Logger.debug("key:%s", key);
//
//								cnf = createSqlCnfFindRepostByAccount(
//										index,
//										name,
//										ItemType.values()[itm],
//										null,
//										isSingle,
//										ArgsType.values()[prm]
//										);
//								sql = createSqlFindRepostByAccount(cnf);
//
//								rbyaSqlCnf.put(key, cnf);
//
//								rbyaSqls.put(key, new SqlData(key, index,
//										name,
//										sql));
							}
						}
					}
				}
			}
		}
		// =============================================*
		// LabelTypeだけのループ
		for (int lbl = 0; lbl < LabelType.values().length; lbl++) {
			for (int sgl = 0; sgl <= 1; sgl++) {
				isSingle = (sgl == 0) ? false : true;
				for (int prm = 0; prm < ArgsType.values().length; prm++) {
					for (int cntr = 0; cntr < ArgsType.values().length; cntr++) {

						index = createSqlIndex(
								SQLNAME_REPOSTED_LABEL.number,
								-1, lbl, sgl, prm, cntr);
						name = SQLNAME_REPOSTED_LABEL.name;
						key = keyStr(
								SQLNAME_REPOSTED_LABEL.number,
								LabelType.values()[lbl],
								null,
								isSingle,
								ArgsType.values()[prm],
								ArgsType.values()[cntr]
								);
						cnf = createSqlCnfForRepostedLabel(
								index,
								name,
								LabelType.values()[lbl],
								null,
								isSingle,
								ArgsType.values()[prm],
								ArgsType.values()[cntr]
								);
						sql = createSqlForRepostedLabel(cnf);

						lbylSqlCnf.put(key, cnf);

						lbylSqls.put(key, new SqlData(key, index, name, sql));

						// -------------------------------------+
						index = createSqlIndex(
								SQLNAME_REPOST_BY_LABEL.number,
								-1, lbl, sgl, prm, cntr);
						name = SQLNAME_REPOST_BY_LABEL.name;
						key = keyStr(
								SQLNAME_REPOST_BY_LABEL.number,
								LabelType.values()[lbl],
								null,
								isSingle,
								ArgsType.values()[prm],
								ArgsType.values()[cntr]
								);
						cnf = createSqlCnfFindRepostByLabel(
								index,
								name,
								LabelType.values()[lbl],
								null,
								isSingle,
								ArgsType.values()[prm],
								ArgsType.values()[cntr]
								);
						sql = createSqlFindRepostByLabel(cnf);

						rbylSqlCnf.put(key, cnf);

						rbylSqls.put(key,
								new SqlData(key, index, name, sql));

						// -------------------------------------+
						if (ArgsType.values()[prm].isNotNull()) {
							if (ArgsType.values()[cntr].isNull()) {
								index = createSqlIndex(
										SQLNAME_LABEL_BY_ACCOUNT.number,
										-1, lbl, sgl, prm, -1);
								name = SQLNAME_LABEL_BY_ACCOUNT.name;
								key = keyStr(
										SQLNAME_LABEL_BY_ACCOUNT.number,
										LabelType.values()[lbl],
										null,
										isSingle,
										ArgsType.values()[prm],
										null
										);
								cnf = createSqlCnfFindLabelByAccount(
										index,
										name,
										LabelType.values()[lbl],
										null,
										isSingle,
										ArgsType.values()[prm]
										);
								sql = createSqlFindLabelByAccount(cnf);

								lbyaSqlCnf.put(key, cnf);

								lbyaSqls.put(key,
										new SqlData(key, index, name, sql));

//								// -------------------------------------+
//								index = createSqlIndex(
//										SQLNAME_REPOST_BY_ACCOUNT.number,
//										-1, lbl, sgl, prm, -1);
//								name = SQLNAME_REPOST_BY_ACCOUNT.name;
//								key = keyStr(
//										SQLNAME_REPOST_BY_ACCOUNT.number,
//										null,
//										LabelType.values()[lbl],
//										isSingle,
//										ArgsType.values()[prm],
//										null
//										);
//
////								Logger.debug("key:%s", key);
//
//								cnf = createSqlCnfFindRepostByAccount(
//										index,
//										name,
//										null,
//										LabelType.values()[lbl],
//										isSingle,
//										ArgsType.values()[prm]
//										);
//								sql = createSqlFindRepostByAccount(cnf);
//
//								rbyaSqlCnf.put(key, cnf);
//
//								rbyaSqls.put(key, new SqlData(key, index,
//										name,
//										sql));
							}
						}
					}
				}
			}
		}
		// =============================================*
		// ItemType、LabelTypeなしのループ
		for (int sgl = 0; sgl <= 1; sgl++) {
			isSingle = (sgl == 0) ? false : true;
			for (int prm = 0; prm < ArgsType.values().length; prm++) {

				if (ArgsType.values()[prm].isNotNull()) {

					index = createSqlIndex(
							SQLNAME_REPOST_BY_ACCOUNT.number,
							-1, -1, sgl, prm, -1);
					name = SQLNAME_REPOST_BY_ACCOUNT.name;
					key = keyStr(
							SQLNAME_REPOST_BY_ACCOUNT.number,
							null,
							null,
							isSingle,
							ArgsType.values()[prm],
							null
							);

//					Logger.debug("key:%s", key);

					cnf = createSqlCnfFindRepostByAccount(
							index,
							name,
							null,
							null,
							isSingle,
							ArgsType.values()[prm]
							);
					sql = createSqlFindRepostByAccount(cnf);

					rbyaSqlCnf.put(key, cnf);

					rbyaSqls.put(key, new SqlData(key, index,
							name,
							sql));
				}
			}
		}
	}

	/*
	 * 《privateメソッド》
	 * SQL文字列格納マップのキー文字列作成メソッド.
	 *
	 * @param _sqlNumber
	 * SQL番号
	 *
	 * @param _type1
	 * ItemType、もしくは、LabelType
	 *
	 * @param _type2
	 * ItemType、もしくは、LabelType
	 *
	 * @param _primaryArgsType
	 * 検索キーがエンティティか文字列かの情報<br>
	 * キー作成用の文字列として
	 *
	 * @param _contributorArgsType
	 * 投稿者キーがエンティティか文字列かの情報<br>
	 * キー作成用の文字列として
	 *
	 * @return 引数を組み合わせたキー文字列
	 */
	private static String keyStr(
			int _sqlNumber,
			Object _type1,
			Object _type2,
			boolean _isSingleArg,
			ArgsType _primaryArgsType,
			// ArgsType _secondaryArgsType,
			ArgsType _contributorArgsType) {

//		if (!(_type1 instanceof ItemType) &&
//				!(_type1 instanceof LabelType)) {
//			throw new IllegalArgumentException();
//		}
//		if (!(_type2 instanceof ItemType) &&
//				!(_type2 instanceof LabelType)) {
//			throw new IllegalArgumentException();
//		}
//		if (_type1.getClass() == _type2.getClass()) {
//			throw new IllegalArgumentException();
//		}

		StringBuilder sb = new StringBuilder();
		sb.append(_sqlNumber);
		sb.append("_");
		sb.append((_type1 == null) ? null : _type1.toString().toLowerCase());
		sb.append("_");
		sb.append((_type2 == null) ? null : _type2.toString().toLowerCase());
		sb.append("_");
		sb.append(_isSingleArg);
		sb.append("_");
		sb.append((_primaryArgsType == null)
				? null
				: _primaryArgsType.toString().toLowerCase());
		sb.append("_");
		// + "_" + _secondaryArgsType
		sb.append((_contributorArgsType == null)
				? null
				: _contributorArgsType.toString().toLowerCase());

		return sb.toString();
	}

	private static ArgsType getContributorArgsType(Object _arg) {

		ArgsType argsMode = ArgsType.NULL;

		if (_arg instanceof Account) {
			argsMode = ArgsType.ENTITY;
		}
		else if (_arg instanceof String) {
			argsMode = ArgsType.STRING;
		}
		return argsMode;
	}

	private static ArgsType getPrimaryArgsType(Object _arg) {

		ArgsType argsMode = ArgsType.NULL;

		if (_arg instanceof MySuperModel) {
			argsMode = ArgsType.ENTITY;
		}
		else if (_arg instanceof String) {
			argsMode = ArgsType.STRING;
		}
		return argsMode;
	}

	public static enum OrderBy implements IOrderBy {

		ID_ASC("id" + ASC),
		ID_DESC("id" + DESC),
		DATE_OF_REPOST_ASC(REPOSTED_AT_COL_NAME + ASC),
		DATE_OF_REPOST_DESC(REPOSTED_AT_COL_NAME + DESC),
		CONTRIBUTORS_ID_ASC(CONTORIBUTOR_COL_NAME + ".id" + ASC),
		CONTRIBUTORS_ID_DESC(CONTORIBUTOR_COL_NAME + ".id" + DESC),
		CONTRIBUTORS_NAME_ASC(CONTORIBUTOR_COL_NAME + ASC),
		CONTRIBUTORS_NAME_DESC(CONTORIBUTOR_COL_NAME + DESC);

		private String sql;

		private OrderBy(String _sql) {
			this.sql = _sql;
		}

		public String getSql() {
			return this.sql;
		}

//		private static String repostedAtColName() {
//			return REPOSTED_AT_COL_NAME;
//		}
//
//		private static String contributorIdColName() {
//			return CONTORIBUTOR_COL_NAME + ".id";
//		}
//
//		private static String contributorNameColName() {
//			return getContributorSignatureColName();
//		}
//
//		private static String labelIdColName() {
//			return LABEL_COL_NAME + ".id";
//		}
//
//		private static String labelSerialCodeColName() {
//			return LabelBase.SERIAL_CODE_COL_NAME;
//		}
//
//		private static String labelDisplayNameColName() {
//			return LabelBase.DISPLAY_NAME_COL_NAME;
//		}
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 検索条件クラス.
	 * 検索メソッドをメソッドチェーンさせるためのもの
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	abstract public static class FindCondition {

		protected ItemType itemType;
		protected LabelType labelType;
		protected List<Object> primaryArgs;
//		protected Object secondary;
		protected Object contributor;// 共通で使われる投稿者条件用
		protected IOrderBy orderBy;
		protected String additionalSql;

		public FindCondition() {}

		public FindCondition itemType(ItemType _itemType) {
			this.itemType = _itemType;
			return this;
		}

		public FindCondition labelType(LabelType _labelType) {
			this.labelType = _labelType;
			return this;
		}

		public FindCondition additionalSql(String _additionalSql) {
			this.additionalSql = _additionalSql;
			return this;
		}

		public FindCondition contributor(
				Object _contributor
				) throws IllegalArgumentException {

			if (!(_contributor instanceof String) &&
					!(_contributor instanceof Account)) {
				throw new IllegalArgumentException();
			}

			this.contributor = _contributor;
			return this;
		}

		// 検索キーがnullの場合ありえるので注意
		public FindCondition primaryArgs(Object... _primaryArgs) {
			if (_primaryArgs == null) {
				return this;
			}
			Logger.debug("_primaryArgs.length:" + _primaryArgs.length);
			/*
			 * 可変長引数の関係で？、配列にしないとチェックにかからない
			 * ○_primaryArgs[0] instanceof String[]
			 * ×_primaryArgs[0] instanceof String
			 */
			if (0 <= _primaryArgs.length) {
				boolean ok = false;
				if (_primaryArgs[0] instanceof String) {
					Logger.debug("_primaryArgs[0] instanceof String");
					ok = true;
				}
				else if (_primaryArgs[0] instanceof String[]) {
					Logger.debug("_primaryArgs[0] instanceof String[]");
					ok = true;
				}
				else if (_primaryArgs[0] instanceof ItemBase) {
					Logger.debug("_primaryArgs[0] instanceof ItemBase");
					ok = true;
				}
				else if (_primaryArgs[0] instanceof ItemBase[]) {
					Logger.debug("_primaryArgs[0] instanceof ItemBase[]");
					ok = true;
				}
				else if (_primaryArgs[0] instanceof LabelBase) {
					Logger.debug("_primaryArgs[0] instanceof LabelBase");
					ok = true;
				}
				else if (_primaryArgs[0] instanceof LabelBase[]) {
					Logger.debug("_primaryArgs[0] instanceof LabelBase[]");
					ok = true;
				}
				else if (_primaryArgs[0] instanceof Account) {
					Logger.debug("_primaryArgs[0] instanceof Account");
					ok = true;
				}
				else if (_primaryArgs[0] instanceof Account[]) {
					Logger.debug("_primaryArgs[0] instanceof Account[]");
					ok = true;
				}
				if (ok) {
					this.primaryArgs = Arrays.asList(_primaryArgs);
				}
			}
			return this;
		}

//		public FindCondition secondary(
//		Object _secondary
//		) throws IllegalArgumentException {
//
//	if (!(_secondary instanceof String) &&
//			!(_secondary instanceof MySuperModel)) {
//		throw new IllegalArgumentException();
//	}
//
//	this.secondary = _secondary;
//	return this;
//}

		public FindCondition orderBy(IOrderBy _orderBy) {
			this.orderBy = _orderBy;
			return this;
		}

		public FindCondition build() {
			return this;
		}

		abstract public List fetch();
	}

	/* ************************************************************ */
	/*
	 * 検索ヘルパ・コア
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 《privateメソッド》
	 * リポストされたアイテムの検索.
	 *
	 * @param _itemType
	 * 検索情報としてのアイテム種類
	 *
	 * @param _labelType
	 * 検索情報としてのラベル種類
	 *
	 * @param _contributor
	 * リポスト投稿者、省略可能
	 *
	 * @param _orderBy
	 * 並び替え指定、省略可能
	 *
	 * @param _additionalSql
	 * 追加SQL文字列
	 *
	 * @param _primaryArgs
	 * 検索キー、省略可能
	 *
	 * @param _sql
	 * 各メソッドごとの検索クエリ
	 *
	 * @param _primaryArgsType
	 * 検索キーのインスタンスのタイプ
	 *
	 * @param _contributorArgsType
	 * 投稿者キーのインスタンスのタイプ
	 *
	 * @return 条件で取得されたリポスト済みのモデルリスト
	 *
	 * @throws IllegalArgumentException
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static List fetchCore(
			ItemType _itemType, LabelType _labelType,
			Object _contributor,
			String _additionalSql,
			List<Object> _primaryArgs,
			SqlData _sqlData,
			ArgsType _primaryArgsType,
			ArgsType _contributorArgsType)
											throws IllegalArgumentException {

		_sqlData.printSqlIndex();

		if (isNotBlank(_additionalSql)) {
			_sqlData.sql += _additionalSql;
		}

		if (isNotBlank(_sqlData.orderBySql)) {
			_sqlData.sql += _sqlData.orderBySql;
		}
//		System.out.println(_sqlData.sql);
		_sqlData.printSql();
//		throw new IllegalArgumentException();// Exception

		boolean hasPrimary = _primaryArgsType.isNotNull();
		boolean hasContributor = _contributorArgsType.isNotNull();

		if (hasPrimary && hasContributor) {
			return RepostBase.find(_sqlData.sql)
					.bind(JPQL_BINDKEY_PRIMARY_ARGS, _primaryArgs)
					.bind(JPQL_BINDKEY_CONTRIBUTOR, _contributor)
					.fetch();
		}
		if (hasPrimary && !hasContributor) {
			return RepostBase.find(_sqlData.sql)
					.bind(JPQL_BINDKEY_PRIMARY_ARGS, _primaryArgs)
					.fetch();
		}
		if (!hasPrimary && hasContributor) {
			return RepostBase.find(_sqlData.sql)
					.bind(JPQL_BINDKEY_CONTRIBUTOR, _contributor)
					.fetch();
		}

		return RepostBase.find(_sqlData.sql).fetch();

	}

	private static boolean isSingle(List<Object> _args) {
		if (_args != null) {
			return _args.size() == 1;
		}
		return true;// Oの場合も
	}

	// TODO List<Object>がNGなら配列に変換すること
	private static ArgsType setPrimaryArgsType(List<Object> _primaryArgs) {
		if (_primaryArgs != null) {
			return getPrimaryArgsType(_primaryArgs.get(0));
		}
		return ArgsType.NULL;
	}

	private static ArgsType contributorArgsType(Object _contributor) {
		if (_contributor != null) {
			return getContributorArgsType(_contributor);
		}
		return ArgsType.NULL;
	}

	/* ************************************************************ */
	/*
	 * 検索ヘルパ・同種類からの検索
	 * Item -> Item
	 * Label -> Label
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 《privateメソッド》
	 * リポストされたアイテムの検索.
	 * Item -> Item
	 *
	 * @param _itemType
	 * 抽出したいアイテム種類、省略可能
	 *
	 * @param _labelType
	 * 関連するラベル種類、省略可能
	 * 省略の場合はRepost全体での検索
	 *
	 * @param _contributor
	 * リポスト投稿者、省略可能
	 *
	 * @param _orderBy
	 * 並び替え指定、省略可能
	 *
	 * @param _additionalSql
	 * 追加SQL文字列
	 *
	 * @param _primaryArgs
	 * 検索キーとなるアイテム、省略可能
	 *
	 * @return 条件で取得されたリポスト済みのアイテムリスト
	 *
	 * @throws IllegalArgumentException
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static List<? extends ItemBase> fetchItems(
			ItemType _itemType, LabelType _labelType,
			Object _contributor, IOrderBy _orderBy,
			String _additionalSql,
			List<Object> _primaryArgs)
										throws IllegalArgumentException {

		ArgsType primaryArgsType = setPrimaryArgsType(_primaryArgs);
		ArgsType contributorArgsType = contributorArgsType(_contributor);

		String key = keyStr(SQLNAME_REPOSTED_ITEM.number,
				_itemType,
				_labelType,
				isSingle(_primaryArgs),
				primaryArgsType,
				contributorArgsType);

		Logger.debug("key:%s", key);

		SqlData sqlData = new SqlData(ibyiSqls.get(key));

		if (_orderBy != null) {
			SqlCnf cnf;
			cnf = ibyiSqlCnf.get(key);
			sqlData.orderBySql = cnf.getOrderBySql(_orderBy);
		}

		return (List<? extends ItemBase>) fetchCore(
				_itemType, _labelType,
				_contributor, _additionalSql,
				_primaryArgs,
				sqlData,
				primaryArgsType,
				contributorArgsType);

	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 《privateメソッド》
	 * リポストされたラベルの検索.
	 * Label -> Label
	 *
	 * @param _labelType
	 * 抽出したいラベル種類、省略可能
	 *
	 * @param _itemType
	 * 関連するアイテム種類、省略可能
	 * 省略の場合はRepost全体での検索
	 *
	 * @param _contributor
	 * リポスト投稿者、省略可能
	 *
	 * @param _orderBy
	 * 並び替え指定、省略可能
	 *
	 * @param _additionalSql
	 * 追加SQL文字列
	 *
	 * @param _primaryArgs
	 * 検索キーとなるラベル、省略可能
	 *
	 * @return 条件で取得されたリポスト済みのラベルリスト
	 *
	 * @throws IllegalArgumentException
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static List<? extends LabelBase> fetchLabels(
			LabelType _labelType, ItemType _itemType,
			Object _contributor, IOrderBy _orderBy,
			String _additionalSql,
			List<Object> _primaryArgs)
										throws IllegalArgumentException {

		ArgsType primaryArgsType = setPrimaryArgsType(_primaryArgs);
		ArgsType contributorArgsType = contributorArgsType(_contributor);

		String key = keyStr(SQLNAME_REPOSTED_LABEL.number,
				_labelType,
				_itemType,
				isSingle(_primaryArgs),
				primaryArgsType,
				contributorArgsType);

		Logger.debug("key:%s", key);

		SqlData sqlData = new SqlData(lbylSqls.get(key));

		if (_orderBy != null) {
			SqlCnf cnf;
			cnf = lbylSqlCnf.get(key);
			sqlData.orderBySql = cnf.getOrderBySql(_orderBy);
		}

		return (List<? extends LabelBase>) fetchCore(
				_itemType, _labelType,
				_contributor, _additionalSql,
				_primaryArgs,
				sqlData,
				primaryArgsType,
				contributorArgsType);

	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * ラッパー
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * ラッパー
	 * あるラベル種類に関連するアイテムのリスト取得
	 * 引数なし：すべて
	 * 引数あり：条件にマッチするもの
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// アイテムの検索 User
	// =============================================*
	/*
	 * 《privateメソッド》
	 * リポストされたユーザーの検索.
	 * User -> User
	 *
	 * @param _primaryArgs 検索キーとなるユーザー、省略可能
	 *
	 * @return 条件で抽出されたユーザーのリスト
	 */
	// =============================================*
	private static FindCondition fetchRepostedUsers(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<User> fetch() {
				return (List<User>) RepostBase.fetchItems(
						ItemType.USER,
						null,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findRepostedUsers(User... _entities) {
		return fetchRepostedUsers(_entities);
	}

	public static FindCondition findRepostedUsers(String... _siqnatures) {
		return fetchRepostedUsers(_siqnatures);
	}

	public static FindCondition findRepostedUsers() {
		return fetchRepostedUsers(null);
	}

	// =============================================*
	/*
	 * 《privateメソッド》
	 * カテゴリー付けされているユーザーの検索.
	 * User -> User
	 *
	 * @param _primaryArgs 検索キーとなるユーザー、省略可能
	 *
	 * @return 条件で抽出されたユーザーのリスト
	 */
	// =============================================*
	private static FindCondition fetchCategorizedUsers(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<User> fetch() {
				return (List<User>) RepostBase.fetchItems(
						ItemType.USER,
						LabelType.CATEGORY,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findCategorizedUsers(User... _entities) {
		return fetchCategorizedUsers(_entities);
	}

	public static FindCondition findCategorizedUsers(String... _siqnatures) {
		return fetchCategorizedUsers(_siqnatures);
	}

	public static FindCondition findCategorizedUsers() {
		return fetchCategorizedUsers(null);
	}

	// =============================================*
	/*
	 * 《privateメソッド》
	 * タグ付けされているユーザーの検索.
	 * User -> User
	 *
	 * @param _primaryArgs 検索キーとなるユーザー、省略可能
	 *
	 * @return 条件で抽出されたユーザーのリスト
	 */
	// =============================================*
	private static FindCondition fetchTaggedUsers(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<User> fetch() {
				return (List<User>) RepostBase.fetchItems(
						ItemType.USER,
						LabelType.TAG,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findTaggedUsers(User... _entities) {
		return fetchTaggedUsers(_entities);
	}

	public static FindCondition findTaggedUsers(String... _siqnatures) {
		return fetchTaggedUsers(_siqnatures);
	}

	public static FindCondition findTaggedUsers() {
		return fetchTaggedUsers(null);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// アイテムの検索 Tweet
	// =============================================*
	/*
	 * 《privateメソッド》
	 * リポストされているつぶやきの検索.
	 * Tweet -> Tweet
	 *
	 * @param _primaryArgs 検索キーとなるつぶやき、省略可能
	 *
	 * @return 条件で抽出されたつぶやきのリスト
	 */
	// =============================================*
	private static FindCondition fetchRepostedTweets(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Tweet> fetch() {
				return (List<Tweet>) RepostBase.fetchItems(
						ItemType.TWEET,
						null,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findRepostedTweets(Tweet... _entities) {
		return fetchRepostedTweets(_entities);
	}

	public static FindCondition findRepostedTweets(String... _siqnatures) {
		return fetchRepostedTweets(_siqnatures);
	}

	public static FindCondition findRepostedTweets() {
		return fetchRepostedTweets(null);
	}

	// =============================================*
	/*
	 * 《privateメソッド》
	 * カテゴリー付けされているつぶやきの検索.
	 * Tweet -> Tweet
	 *
	 * @param _primaryArgs 検索キーとなるつぶやき、省略可能
	 *
	 * @return 条件で抽出されたつぶやきのリスト
	 */
	// =============================================*
	private static FindCondition fetchCategorizedTweets(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Tweet> fetch() {
				return (List<Tweet>) RepostBase.fetchItems(
						ItemType.TWEET,
						LabelType.CATEGORY,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findCategorizedTweets(Tweet... _entities) {
		return fetchCategorizedTweets(_entities);
	}

	public static FindCondition findCategorizedTweets(String... _siqnatures) {
		return fetchCategorizedTweets(_siqnatures);
	}

	public static FindCondition findCategorizedTweets() {
		return fetchCategorizedTweets(null);
	}

	// =============================================*
	/*
	 * 《privateメソッド》
	 * タグ付けされているつぶやきの検索.
	 * Tweet -> Tweet
	 *
	 * @param _primaryArgs 検索キーとなるつぶやき、省略可能
	 *
	 * @return 条件で抽出されたつぶやきのリスト
	 */
	// =============================================*
	private static FindCondition fetchTaggedTweets(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Tweet> fetch() {
				return (List<Tweet>) RepostBase.fetchItems(
						ItemType.TWEET,
						LabelType.TAG,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findTaggedTweets(Tweet... _entities) {
		return fetchTaggedTweets(_entities);
	}

	public static FindCondition findTaggedTweets(String... _siqnatures) {
		return fetchTaggedTweets(_siqnatures);
	}

	public static FindCondition findTaggedTweets() {
		return fetchTaggedTweets(null);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * ラッパー
	 * あるアイテム種類に関連するラベルのリスト取得
	 * 引数なし：すべて
	 * 引数あり：条件にマッチするもの
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// ラベルの検索 Category
	// =============================================*
	/*
	 * 《privateメソッド》
	 * リポストされたカテゴリーの検索.
	 * Category -> Category
	 *
	 * @param _primaryArgs 検索キーとなるカテゴリー、省略可能
	 *
	 * @return 条件で抽出されたカテゴリーのリスト
	 */
	// =============================================*
	private static FindCondition fetchRepostedCategories(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Category> fetch() {
				return (List<Category>) RepostBase.fetchLabels(
						LabelType.CATEGORY,
						null,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findRepostedCategories(Category... _entities) {
		return fetchRepostedCategories(_entities);
	}

	public static FindCondition findRepostedCategories(String... _siqnatures) {
		return fetchRepostedCategories(_siqnatures);
	}

	public static FindCondition findRepostedCategories() {
		return fetchRepostedCategories(null);
	}

	// =============================================*
	/*
	 * 《privateメソッド》
	 * ユーザーに付けられたカテゴリーの検索.
	 * Category -> Category
	 *
	 * @param _primaryArgs 検索キーとなるカテゴリー、省略可能
	 *
	 * @return 条件で抽出されたカテゴリーのリスト
	 */
	// =============================================*
	private static FindCondition fetchUsersCategories(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Category> fetch() {
				return (List<Category>) RepostBase.fetchLabels(
						LabelType.CATEGORY,
						ItemType.USER,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findUsersCategories(Category... _entities) {
		return fetchUsersCategories(_entities);
	}

	public static FindCondition findUsersCategories(String... _siqnatures) {
		return fetchUsersCategories(_siqnatures);
	}

	public static FindCondition findUsersCategories() {
		return fetchUsersCategories(null);
	}

	// =============================================*
	/*
	 * 《privateメソッド》
	 * つぶやきに付けられたカテゴリーの検索.
	 * Category -> Category
	 *
	 * @param _primaryArgs 検索キーとなるカテゴリー、省略可能
	 *
	 * @return 条件で抽出されたカテゴリーのリスト
	 */
	// =============================================*
	private static FindCondition fetchTweetsCategories(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Category> fetch() {
				return (List<Category>) RepostBase.fetchLabels(
						LabelType.CATEGORY,
						ItemType.TWEET,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findTweetsCategories(Category... _entities) {
		return fetchTweetsCategories(_entities);
	}

	public static FindCondition findTweetsCategories(String... _siqnatures) {
		return fetchTweetsCategories(_siqnatures);
	}

	public static FindCondition findTweetsCategories() {
		return fetchTweetsCategories(null);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// ラベルの検索 Tag
	// =============================================*
	/*
	 * 《privateメソッド》
	 * リポストされたタグの検索.
	 * Tag -> Tag
	 *
	 * @param _primaryArgs 検索キーとなるタグ、省略可能
	 *
	 * @return 条件で抽出されたタグのリスト
	 */
	// =============================================*
	private static FindCondition fetchRepostedTags(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Tag> fetch() {
				return (List<Tag>) RepostBase.fetchLabels(
						LabelType.TAG,
						null,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findRepostedTags(Tag... _entities) {
		return fetchRepostedTags(_entities);
	}

	public static FindCondition findRepostedTags(String... _siqnatures) {
		return fetchRepostedTags(_siqnatures);
	}

	public static FindCondition findRepostedTags() {
		return fetchRepostedTags(null);
	}

	// =============================================*
	/*
	 * 《privateメソッド》
	 * ユーザーに付けられたタグの検索.
	 * Tag -> Tag
	 *
	 * @param _primaryArgs 検索キーとなるタグ、省略可能
	 *
	 * @return 条件で抽出されたタグのリスト
	 */
	// =============================================*
	private static FindCondition fetchUsersTags(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Tag> fetch() {
				return (List<Tag>) RepostBase.fetchLabels(
						LabelType.TAG,
						ItemType.USER,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findUsersTags(Tag... _entities) {
		return fetchUsersTags(_entities);
	}

	public static FindCondition findUsersTags(String... _siqnatures) {
		return fetchUsersTags(_siqnatures);
	}

	public static FindCondition findUsersTags() {
		return fetchUsersTags(null);
	}

	// =============================================*
	/*
	 * 《privateメソッド》
	 * つぶやきに付けられたタグの検索.
	 * Tag -> Tag
	 *
	 * @param _primaryArgs 検索キーとなるタグ、省略可能
	 *
	 * @return 条件で抽出されたタグのリスト
	 */
	// =============================================*
	private static FindCondition fetchTweetsTags(
			Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Tag> fetch() {
				return (List<Tag>) RepostBase.fetchLabels(
						LabelType.TAG,
						ItemType.TWEET,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	public static FindCondition findTweetsTags(Tag... _entities) {
		return fetchTweetsTags(_entities);
	}

	public static FindCondition findTweetsTags(String... _siqnatures) {
		return fetchTweetsTags(_siqnatures);
	}

	public static FindCondition findTweetsTags() {
		return fetchTweetsTags(null);
	}

	/* ************************************************************ */
	/*
	 * 検索ヘルパ・投稿者による検索
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 《privateメソッド》
	 * リポスト投稿者によるリポストされたアイテムの検索.
	 * Account -> Item
	 *
	 * 複数のアカウントでの検索用。<br>
	 * 他でも使われている追加条件であるcontributorは単数のみ。
	 *
	 * @param _itemType
	 * 抽出したいアイテム種類
	 *
	 * @param _labelType
	 * 省略の場合はRepost全体での検索
	 *
	 * @param _additionalSql
	 * 追加SQL文字列
	 *
	 * @param _primaryArgs
	 * 検索キーとなるAccount、省略不可
	 *
	 * @return 指定アカウントにより投稿されたリポストリスト
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static List<? extends ItemBase> fetchItemsByAccounts(
			ItemType _itemType, LabelType _labelType,
			IOrderBy _orderBy,
			String _additionalSql,
			List<Object> _primaryArgs) {

		if (_primaryArgs == null) {
			throw new IllegalArgumentException();
		}
		// TODO List<Object>がNGなら配列に変換すること
		ArgsType primaryArgsType = getPrimaryArgsType(_primaryArgs.get(0));

		String key = keyStr(SQLNAME_ITEM_BY_ACCOUNT.number,
				_itemType,
				_labelType,
				isSingle(_primaryArgs),
				primaryArgsType,
				null);

		Logger.debug("key:%s", key);

		SqlData sqlData = new SqlData(ibyaSqls.get(key));

		if (isNotBlank(_additionalSql)) {
			sqlData.sql += _additionalSql;
		}

		if (_orderBy != null) {
			SqlCnf cnf;
			cnf = ibyaSqlCnf.get(key);
			sqlData.orderBySql = cnf.getOrderBySql(_orderBy);
			sqlData.sql += sqlData.orderBySql;
		}

		sqlData.printSqlIndex();

		return RepostBase.find(sqlData.sql)
				.bind(JPQL_BINDKEY_PRIMARY_ARGS, _primaryArgs)
				.fetch();

	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 《privateメソッド》
	 * リポスト投稿者によるリポストされたラベルの検索.
	 * Account -> Label
	 *
	 * 複数のアカウントでの検索用。<br>
	 * 他でも使われている追加条件であるcontributorは単数のみ。
	 *
	 * @param _labelType
	 * 抽出したいラベル種類
	 *
	 * @param _itemType
	 * 省略の場合はRepost全体での検索
	 *
	 * @param _additionalSql
	 * 追加SQL文字列
	 *
	 * @param _primaryArgs
	 * 検索キーとなるAccount、省略不可
	 *
	 * @return 指定アカウントにより投稿されたリポストリスト
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static List<? extends LabelBase> fetchLabelsByAccounts(
			LabelType _labelType, ItemType _itemType,
			IOrderBy _orderBy,
			String _additionalSql,
			List<Object> _primaryArgs) {

		if (_primaryArgs == null) {
			throw new IllegalArgumentException();
		}
		// TODO List<Object>がNGなら配列に変換すること
		ArgsType primaryArgsType = getPrimaryArgsType(_primaryArgs.get(0));

		String key = keyStr(SQLNAME_LABEL_BY_ACCOUNT.number,
				_labelType,
				_itemType,
				isSingle(_primaryArgs),
				primaryArgsType,
				null);

		Logger.debug("key:%s", key);

		SqlData sqlData = new SqlData(lbyaSqls.get(key));

		if (isNotBlank(_additionalSql)) {
			sqlData.sql += _additionalSql;
		}

		if (_orderBy != null) {
			SqlCnf cnf;
			cnf = lbyaSqlCnf.get(key);
			sqlData.orderBySql = cnf.getOrderBySql(_orderBy);
			sqlData.sql += sqlData.orderBySql;
		}

		sqlData.printSqlIndex();

		return RepostBase.find(sqlData.sql)
				.bind(JPQL_BINDKEY_PRIMARY_ARGS, _primaryArgs)
				.fetch();

	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * ラッパー
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// アイテムの検索 User
	// =============================================*
	/*
	 * LabelTypeのない場合に、nullが可変長引数と混じってしまう。
	 * そのクッションとしての役割あり。リファクタリングしないこと。
	 */
	private static FindCondition fetchUsersByAccounts(
			LabelType _labelType, Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<User> fetch() {
				return (List<User>) RepostBase.fetchItemsByAccounts(
						ItemType.USER,
						this.labelType,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs)
				.labelType(_labelType)
				.build();
	}

	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたユーザーの検索.
	 * Account -> User
	 *
	 * 複数のアカウントでの検索用。<br>
	 * 他でも使われている追加条件であるcontributorは単数のみ。
	 *
	 * @param _labelType
	 *            絞り込み要素としてのラベルタイプ
	 * @param _primaryArgs
	 *            検索キーとなるAccount、省略不可
	 *
	 * @return 指定アカウントにより投稿されたリポストリスト
	 */
	// =============================================*
	public static FindCondition findUsersByAccounts(
			LabelType _labelType, Object... _primaryArgs) {

		return fetchUsersByAccounts(_labelType, _primaryArgs);
	}

	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたカテゴリーの検索.
	 * Account -> User
	 *
	 * 複数のアカウントでの検索用。<br>
	 * 他でも使われている追加条件であるcontributorは単数のみ。
	 *
	 * @param _primaryArgs
	 *            検索キーとなるAccount、省略不可
	 *
	 * @return 指定アカウントにより投稿されたリポストリスト
	 */
	// =============================================*
	public static FindCondition findUsersByAccounts(
			Object... _primaryArgs) {

		return fetchUsersByAccounts(null, _primaryArgs);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// アイテムの検索 Tweet
	// =============================================*
	/*
	 * LabelTypeのない場合に、nullが可変長引数と混じってしまう。
	 * そのクッションとしての役割あり。リファクタリングしないこと。
	 */
	private static FindCondition fetchTweetsByAccounts(
			LabelType _labelType, Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Tweet> fetch() {
				return (List<Tweet>) RepostBase.fetchItemsByAccounts(
						ItemType.TWEET,
						this.labelType,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs)
				.labelType(_labelType)
				.build();
	}

	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたつぶやきの検索.
	 * Account -> Tweet
	 *
	 * 複数のアカウントでの検索用。<br>
	 * 他でも使われている追加条件であるcontributorは単数のみ。
	 *
	 * @param _labelType
	 *            絞り込み要素としてのラベルタイプ
	 * @param _primaryArgs
	 *            検索キーとなるAccount、省略不可
	 *
	 * @return 指定アカウントにより投稿されたリポストリスト
	 */
	// =============================================*
	public static FindCondition findTweetsByAccounts(
			LabelType _labelType, Object... _primaryArgs) {

		return fetchTweetsByAccounts(_labelType, _primaryArgs);
	}

	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたつぶやきの検索.
	 * Account -> Tweet
	 *
	 * 複数のアカウントでの検索用。<br>
	 * 他でも使われている追加条件であるcontributorは単数のみ。
	 *
	 * @param _primaryArgs
	 *            検索キーとなるAccount、省略不可
	 * @return 指定アカウントにより投稿されたリポストリスト
	 */
	// =============================================*
	public static FindCondition findTweetsByAccounts(
			Object... _primaryArgs) {

		return fetchTweetsByAccounts(null, _primaryArgs);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// ラベルの検索 Category
	// =============================================*
	/*
	 * LabelTypeのない場合に、nullが可変長引数と混じってしまう。
	 * そのクッションとしての役割あり。リファクタリングしないこと。
	 */
	private static FindCondition fetchCategoriesByAccounts(
			ItemType _itemType, Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Category> fetch() {
				return (List<Category>) RepostBase.fetchLabelsByAccounts(
						LabelType.CATEGORY,
						this.itemType,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs)
				.itemType(_itemType)
				.build();
	}

	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたカテゴリーの検索.
	 * Account -> Category
	 *
	 * 複数のアカウントでの検索用。<br>
	 * 他でも使われている追加条件であるcontributorは単数のみ。
	 *
	 * @param _itemType
	 *            絞り込み要素としてのアイテムタイプ
	 * @param _primaryArgs
	 *            検索キーとなるAccount、省略不可
	 * @return 指定アカウントにより投稿されたリポストリスト
	 */
	// =============================================*
	public static FindCondition findCategoriesByAccounts(
			ItemType _itemType, Object... _primaryArgs) {

		return fetchCategoriesByAccounts(_itemType, _primaryArgs);
	}

	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたカテゴリーの検索.
	 * Account -> Category
	 *
	 * 複数のアカウントでの検索用。<br>
	 * 他でも使われている追加条件であるcontributorは単数のみ。
	 *
	 * @param _primaryArgs
	 *            検索キーとなるAccount、省略不可
	 * @return 指定アカウントにより投稿されたリポストリスト
	 */
	// =============================================*
	public static FindCondition findCategoriesByAccounts(
			Object... _primaryArgs) {

		return fetchCategoriesByAccounts(null, _primaryArgs);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// ラベルの検索 Tag
	// =============================================*
	/*
	 * LabelTypeのない場合に、nullが可変長引数と混じってしまう。
	 * そのクッションとしての役割あり。リファクタリングしないこと。
	 */
	private static FindCondition fetchTagsByAccounts(
			ItemType _itemType, Object[] _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<Tag> fetch() {
				return (List<Tag>) RepostBase.fetchLabelsByAccounts(
						LabelType.TAG,
						this.itemType,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs)
				.itemType(_itemType)
				.build();
	}

	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたタグの検索.
	 * Account -> Tag
	 *
	 * 複数のアカウントでの検索用。<br>
	 * 他でも使われている追加条件であるcontributorは単数のみ。
	 *
	 * @param _itemType
	 *            絞り込み要素としてのアイテムタイプ
	 * @param _primaryArgs
	 *            検索キーとなるAccount、省略不可
	 * @return 指定アカウントにより投稿されたリポストリスト
	 */
	// =============================================*
	public static FindCondition findTagsByAccounts(
			ItemType _itemType, Object... _primaryArgs) {

		return fetchTagsByAccounts(_itemType, _primaryArgs);
	}

	// =============================================*
	/**
	 * リポスト投稿者によるリポストされたタグの検索.
	 * Account -> Tag
	 *
	 * 複数のアカウントでの検索用。<br>
	 * 他でも使われている追加条件であるcontributorは単数のみ。
	 *
	 * @param _primaryArgs
	 *            検索キーとなるAccount、省略不可
	 * @return 指定アカウントにより投稿されたリポストリスト
	 */
	// =============================================*
	public static FindCondition findTagsByAccounts(
			Object... _primaryArgs) {

		return fetchTagsByAccounts(null, _primaryArgs);
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
	 * 《privateメソッド》
	 * ラベルによるアイテムの検索
	 * Item -> Label
	 *
	 * @param _itemType
	 * 抽出したいアイテム種類、省略不可
	 *
	 * @param _labelType
	 * 条件としての関連ラベル種類、省略不可
	 *
	 * @param _contributor
	 * リポスト投稿者、省略可能
	 *
	 * @param _orderBy
	 * 並び替え指定、省略可能
	 *
	 * @param _additionalSql
	 * 追加SQL文字列
	 *
	 * @param _primaryArgs
	 * 検索キーとなるラベル、省略不可
	 *
	 * @return 条件で取得されたリポスト済みのアイテムリスト
	 *
	 * @throws IllegalArgumentException
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static List<? extends ItemBase> fetchItemsByLabels(
			ItemType _itemType, LabelType _labelType,
			Object _contributor, IOrderBy _orderBy,
			String _additionalSql,
			List<Object> _primaryArgs)
										throws IllegalArgumentException {

		if (_primaryArgs == null ||
				_itemType == null || _labelType == null) {

			throw new IllegalArgumentException();
		}

		ArgsType primaryArgsType = setPrimaryArgsType(_primaryArgs);
		ArgsType contributorArgsType = contributorArgsType(_contributor);

		String key = keyStr(SQLNAME_ITEM_BY_LABEL.number,
				_itemType,
				_labelType,
				isSingle(_primaryArgs),
				primaryArgsType,
				contributorArgsType);

		Logger.debug("key:%s", key);

		SqlData sqlData = new SqlData(ibylSqls.get(key));

		if (_orderBy != null) {
			SqlCnf cnf;
			cnf = ibylSqlCnf.get(key);
			sqlData.orderBySql = cnf.getOrderBySql(_orderBy);
		}

		return (List<? extends ItemBase>) fetchCore(
				_itemType, _labelType,
				_contributor, _additionalSql,
				_primaryArgs,
				sqlData,
				primaryArgsType,
				contributorArgsType);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 《privateメソッド》
	 * アイテムによるラベルの検索
	 * Item -> Label
	 *
	 * @param _labelType
	 * 抽出したいラベル種類、省略不可
	 *
	 * @param _itemType
	 * 条件としての関連ラベル種類、省略不可
	 *
	 * @param _contributor
	 * リポスト投稿者、省略可能
	 *
	 * @param _orderBy
	 * 並び替え指定、省略可能
	 *
	 * @param _additionalSql
	 * 追加SQL文字列
	 *
	 * @param _primaryArgs
	 * 検索キーとなるアイテム、省略不可
	 *
	 * @return 条件で取得されたリポスト済みのラベルリスト
	 *
	 * @throws IllegalArgumentException
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static List<? extends LabelBase> fetchLabelsByItems(
			LabelType _labelType, ItemType _itemType,
			Object _contributor, IOrderBy _orderBy,
			String _additionalSql,
			List<Object> _primaryArgs)
										throws IllegalArgumentException {

		if (_primaryArgs == null ||
				_labelType == null || _itemType == null) {

			throw new IllegalArgumentException();
		}

		ArgsType primaryArgsType = setPrimaryArgsType(_primaryArgs);
		ArgsType contributorArgsType = contributorArgsType(_contributor);

		String key = keyStr(SQLNAME_LABEL_BY_ITEM.number,
				_labelType,
				_itemType,
				isSingle(_primaryArgs),
				primaryArgsType,
				contributorArgsType);

		Logger.debug("key:%s", key);

		SqlData sqlData = new SqlData(lbyiSqls.get(key));

		if (_orderBy != null) {
			SqlCnf cnf;
			cnf = lbyiSqlCnf.get(key);
			sqlData.orderBySql = cnf.getOrderBySql(_orderBy);
		}

		return (List<? extends LabelBase>) fetchCore(
				_itemType, _labelType,
				_contributor, _additionalSql,
				_primaryArgs,
				sqlData,
				primaryArgsType,
				contributorArgsType);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * ラッパー
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * ラッパー
	 * 指定ラベルに関連するアイテムの検索
	 * Label -> Item
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// =============================================*
	/**
	 * ラベルによるアイテムの検索.
	 * Label -> Item
	 *
	 * @param _itemType
	 *            抽出したいアイテム種類
	 *
	 * @param _labelType
	 *            条件としての関連ラベル種類、省略不可
	 *
	 * @param _primaryArgs
	 *            検索キーとなるラベル、省略不可
	 *
	 * @return 条件で取得されたリポスト済みのアイテムリスト
	 *
	 * @throws IllegalArgumentException
	 */
	// =============================================*
	public static FindCondition findItemsByLabels(
			ItemType _itemType, LabelType _labelType,
			Object... _primaryArgs) throws IllegalArgumentException {

		if (_itemType == ItemType.USER && _labelType == LabelType.CATEGORY) {
			return findUsersByCategories(_primaryArgs);
		}
		else if (_itemType == ItemType.USER && _labelType == LabelType.TAG) {
			return findUsersByTags(_primaryArgs);
		}
		else if (_itemType == ItemType.TWEET
				&& _labelType == LabelType.CATEGORY) {
			return findTweetsByCategories(_primaryArgs);
		}
		else if (_itemType == ItemType.TWEET && _labelType == LabelType.TAG) {
			return findTweetsByTags(_primaryArgs);
		}

		throw new IllegalArgumentException();
	}

	// =============================================*
	/**
	 * カテゴリーによるリポストされたユーザーの検索.
	 * Category -> User
	 *
	 * @param _primaryArgs
	 *            検索キーとなるカテゴリーモデル、省略不可
	 * @return 条件で取得されたリポスト済みのユーザーリスト
	 */
	// =============================================*
	public static FindCondition findUsersByCategories(
			Object... _primaryArgs) throws IllegalArgumentException {

		return new FindCondition() {
			@Override
			public List<User> fetch() {
				return (List<User>) RepostBase.fetchItemsByLabels(
						ItemType.USER,
						LabelType.CATEGORY,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	// =============================================*
	/**
	 * タグによるリポストされたユーザーの検索.
	 * Tag -> User
	 *
	 * @param _primaryArgs
	 *            検索キーとなるタグモデル、省略不可
	 * @return 条件で取得されたリポスト済みのユーザーリスト
	 */
	// =============================================*
	public static FindCondition findUsersByTags(
			Object... _primaryArgs) throws IllegalArgumentException {

		return new FindCondition() {
			@Override
			public List<User> fetch() {
				return (List<User>) RepostBase.fetchItemsByLabels(
						ItemType.USER,
						LabelType.TAG,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	// =============================================*
	/**
	 * カテゴリーによるリポストされたつぶやきの検索.
	 * Category -> Tweet
	 *
	 * @param _primaryArgs
	 *            検索キーとなるカテゴリーモデル、省略不可
	 * @return 条件で取得されたリポスト済みのつぶやきリスト
	 */
	// =============================================*
	public static FindCondition findTweetsByCategories(
			Object... _primaryArgs) throws IllegalArgumentException {

		return new FindCondition() {
			@Override
			public List<Tweet> fetch() {
				return (List<Tweet>) RepostBase.fetchItemsByLabels(
						ItemType.TWEET,
						LabelType.CATEGORY,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	// =============================================*
	/**
	 * タグによるリポストされたつぶやきの検索.
	 * Tag -> Tweet
	 *
	 * @param _primaryArgs
	 *            検索キーとなるタグモデル、省略不可
	 * @return 条件で取得されたリポスト済みのつぶやきリスト
	 */
	// =============================================*
	public static FindCondition findTweetsByTags(
			Object... _primaryArgs) throws IllegalArgumentException {

		return new FindCondition() {
			@Override
			public List<Tweet> fetch() {
				return (List<Tweet>) RepostBase.fetchItemsByLabels(
						ItemType.TWEET,
						LabelType.TAG,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * ラッパー
	 * 指定アイテムに関連するラベルの検索
	 * Item -> Label
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// =============================================*
	/*
	 * ラッパー
	 * アイテムによるラベルの検索
	 * Item -> Label
	 */
	// =============================================*
	// =============================================*
	/**
	 * アイテムによるラベルの検索.
	 * Item -> Label
	 *
	 * @param _labelType
	 *            抽出したいラベル種類
	 *
	 * @param _itemType
	 *            条件としての関連アイテム種類、省略不可
	 *
	 * @param _primaryArgs
	 *            検索キーとなるアイテム、省略不可
	 *
	 * @return 条件で取得されたリポスト済みのラベルリスト
	 *
	 * @throws IllegalArgumentException
	 */
	// =============================================*
	public static FindCondition findLabelsByItems(
			LabelType _labelType, ItemType _itemType,
			Object... _primaryArgs) throws IllegalArgumentException {

		if (_itemType == ItemType.USER && _labelType == LabelType.CATEGORY) {
			return findCategoriesByUsers(_primaryArgs);
		}
		else if (_itemType == ItemType.USER && _labelType == LabelType.TAG) {
			return findTagsByUsers(_primaryArgs);
		}
		else if (_itemType == ItemType.TWEET
				&& _labelType == LabelType.CATEGORY) {
			return findCategoriesByTweets(_primaryArgs);
		}
		else if (_itemType == ItemType.TWEET && _labelType == LabelType.TAG) {
			return findTagsByTweets(_primaryArgs);
		}

		throw new IllegalArgumentException();
	}

	// =============================================*
	/**
	 * ユーザーによるリポストされたカテゴリーの検索.
	 * User -> Category
	 *
	 * @param _primaryArgs
	 *            検索キーとなるユーザーモデル、省略不可
	 * @return 条件で取得されたリポスト済みのカテゴリーリスト
	 */
	// =============================================*
	public static FindCondition findCategoriesByUsers(
			Object... _primaryArgs) throws IllegalArgumentException {

		return new FindCondition() {
			@Override
			public List<Category> fetch() {
				return (List<Category>) RepostBase.fetchLabelsByItems(
						LabelType.CATEGORY,
						ItemType.USER,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	// =============================================*
	/**
	 * つぶやきによるリポストされたカテゴリーの検索.
	 * Tweet -> Category
	 *
	 * @param _primaryArgs
	 *            検索キーとなるつぶやきモデル、省略不可
	 * @return 条件で取得されたリポスト済みのカテゴリーリスト
	 */
	// =============================================*
	public static FindCondition findCategoriesByTweets(
			Object... _primaryArgs) throws IllegalArgumentException {

		return new FindCondition() {
			@Override
			public List<Category> fetch() {
				return (List<Category>) RepostBase.fetchLabelsByItems(
						LabelType.CATEGORY,
						ItemType.TWEET,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	// =============================================*
	/**
	 * つぶやきによるリポストされたタグの検索.
	 * User -> Tag
	 *
	 * @param _primaryArgs
	 *            検索キーとなるつぶやきモデル、省略不可
	 * @return 条件で取得されたリポスト済みのタグリスト
	 */
	// =============================================*
	public static FindCondition findTagsByUsers(
			Object... _primaryArgs) throws IllegalArgumentException {

		return new FindCondition() {
			@Override
			public List<Tag> fetch() {
				return (List<Tag>) RepostBase.fetchLabelsByItems(
						LabelType.TAG,
						ItemType.USER,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	// =============================================*
	/**
	 * つぶやきによるリポストされたタグの検索.
	 * Tweet -> Tag
	 *
	 * @param _primaryArgs
	 *            検索キーとなるつぶやきモデル、省略不可
	 * @return 条件で取得されたリポスト済みのタグリスト
	 */
	// =============================================*
	public static FindCondition findTagsByTweets(
			Object... _primaryArgs) throws IllegalArgumentException {

		return new FindCondition() {
			@Override
			public List<Tag> fetch() {
				return (List<Tag>) RepostBase.fetchLabelsByItems(
						LabelType.TAG,
						ItemType.TWEET,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	/* ************************************************************ */
	/*
	 * 検索ヘルパ・リポストの検索
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 《privateメソッド》
	 * アイテムによるリポストの検索.
	 * Item -> Repost
	 *
	 * @param _itemType
	 * 検索情報としてのアイテム種類、省略不可
	 *
	 * @param _labelType
	 * 検索情報としてのラベル種類、省略可能<br>
	 * 省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _contributor
	 * リポスト投稿者、省略可能
	 *
	 * @param _orderBy
	 * 並び替え指定、省略可能
	 *
	 * @param _additionalSql
	 * 追加SQL文字列
	 *
	 * @param _primaryArgs
	 * 検索キー、省略可能
	 *
	 * @return 条件で取得されたリポストモデルリスト
	 *
	 * @throws IllegalArgumentException
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static List<RepostBase> fetchRepostByItems(
			ItemType _itemType, LabelType _labelType,
			Object _contributor, IOrderBy _orderBy,
			String _additionalSql,
			List<Object> _primaryArgs)
										throws IllegalArgumentException {

		if (_itemType == null) {
			throw new IllegalArgumentException();
		}

		ArgsType primaryArgsType = setPrimaryArgsType(_primaryArgs);
		ArgsType contributorArgsType = contributorArgsType(_contributor);

		String key = keyStr(SQLNAME_REPOST_BY_ITEM.number,
				_itemType,
				_labelType,
				isSingle(_primaryArgs),
				primaryArgsType,
				contributorArgsType);

		Logger.debug("key:%s", key);

		SqlData sqlData = new SqlData(rbyiSqls.get(key));

		if (_orderBy != null) {
			SqlCnf cnf;
			cnf = rbyiSqlCnf.get(key);
			sqlData.orderBySql = cnf.getOrderBySql(_orderBy);
		}

		return (List<RepostBase>) fetchCore(
				_itemType, _labelType,
				_contributor, _additionalSql,
				_primaryArgs,
				sqlData,
				primaryArgsType,
				contributorArgsType);

	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 《privateメソッド》
	 * ラベルによるリポストの検索.
	 * Label -> Repost
	 *
	 * @param _labelType
	 * 検索情報としてのラベル種類、省略不可
	 *
	 * @param _itemType
	 * 検索情報としてのアイテム種類、省略可能<br>
	 * 省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _contributor
	 * リポスト投稿者、省略可能
	 *
	 * @param _orderBy
	 * 並び替え指定、省略可能
	 *
	 * @param _additionalSql
	 * 追加SQL文字列
	 *
	 * @param _primaryArgs
	 * 検索キー、省略可能
	 *
	 * @return 条件で取得されたリポストモデルリスト
	 *
	 * @throws IllegalArgumentException
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static List<RepostBase> fetchRepostByLabels(
			LabelType _labelType, ItemType _itemType,
			Object _contributor, IOrderBy _orderBy,
			String _additionalSql,
			List<Object> _primaryArgs)
										throws IllegalArgumentException {

		if (_labelType == null) {
			throw new IllegalArgumentException();
		}

		ArgsType primaryArgsType = setPrimaryArgsType(_primaryArgs);
		ArgsType contributorArgsType = contributorArgsType(_contributor);

		String key = keyStr(SQLNAME_REPOST_BY_LABEL.number,
				_labelType,
				_itemType,
				isSingle(_primaryArgs),
				primaryArgsType,
				contributorArgsType);

		Logger.debug("key:%s", key);

		SqlData sqlData = new SqlData(rbylSqls.get(key));

		if (_orderBy != null) {
			SqlCnf cnf;
			cnf = rbylSqlCnf.get(key);
			sqlData.orderBySql = cnf.getOrderBySql(_orderBy);
		}

		return (List<RepostBase>) fetchCore(
				_itemType, _labelType,
				_contributor, _additionalSql,
				_primaryArgs,
				sqlData,
				primaryArgsType,
				contributorArgsType);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * 《privateメソッド》
	 * アカウントによるリポストの検索.
	 * Account -> Repost
	 *
	 * @param _labelType
	 * 検索情報としてのラベル種類、省略可能<br>
	 * 省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _itemType
	 * 検索情報としてのアイテム種類、省略可能<br>
	 * 省略の場合はRepost全体での検索<br>
	 * リポスト組み合わせのサブクラス取得用情報
	 *
	 * @param _additionalSql
	 * 追加SQL文字列
	 *
	 * @param _primaryArgs
	 * 検索キー、省略不可
	 *
	 * @return 条件で取得されたリポストモデルリスト
	 *
	 * @throws IllegalArgumentException
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private static List<RepostBase> fetchRepostByAccounts(
			ItemType _itemType, LabelType _labelType,
			IOrderBy _orderBy,
			String _additionalSql,
			List<Object> _primaryArgs)
										throws IllegalArgumentException {

		if (_primaryArgs == null) {
			throw new IllegalArgumentException();
		}

		ArgsType primaryArgsType = setPrimaryArgsType(_primaryArgs);

		ItemType itemTypeTemp = _itemType;
		LabelType labelTypeTemp = _labelType;
		if (_itemType == null || _labelType == null) {
			itemTypeTemp = null;
			labelTypeTemp = null;
		}

		String key = keyStr(SQLNAME_REPOST_BY_ACCOUNT.number,
				itemTypeTemp,
				labelTypeTemp,
				isSingle(_primaryArgs),
				primaryArgsType,
				null);

		Logger.debug("key:%s", key);

		SqlData sqlData = new SqlData(rbyaSqls.get(key));

		if (_orderBy != null) {
			SqlCnf cnf;
			cnf = rbyaSqlCnf.get(key);
			sqlData.orderBySql = cnf.getOrderBySql(_orderBy);
		}

		return (List<RepostBase>) fetchCore(
				_itemType, _labelType,
				null, _additionalSql,
				_primaryArgs,
				sqlData,
				primaryArgsType,
				ArgsType.NULL);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * ラッパー
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// =============================================*
	/**
	 * ユーザーによるリポストの検索.
	 * User -> Repost
	 *
	 * @param _primaryArgs
	 *            検索キーとなるユーザーモデル、省略不可
	 * @return 条件で取得されたリポストのリスト
	 */
	// =============================================*
	public static FindCondition findRepostByUsers(
			Object... _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<RepostBase> fetch() {
				return RepostBase.fetchRepostByItems(
						ItemType.USER,
						this.labelType,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	// =============================================*
	/**
	 * つぶやきによるリポストの検索.
	 * Tweet -> Repost
	 *
	 * @param _primaryArgs
	 *            検索キーとなるつぶやきモデル、省略不可
	 * @return 条件で取得されたリポストのリスト
	 */
	// =============================================*
	public static FindCondition findRepostByTweets(
			Object... _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<RepostBase> fetch() {
				return RepostBase.fetchRepostByItems(
						ItemType.TWEET,
						this.labelType,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	// =============================================*
	/**
	 * カテゴリーによるリポストの検索.
	 * Category -> Repost
	 *
	 * @param _primaryArgs
	 *            検索キーとなるカテゴリーモデル、省略不可
	 * @return 条件で取得されたリポストのリスト
	 */
	// =============================================*
	public static FindCondition findRepostByCategories(
			Object... _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<RepostBase> fetch() {
				return RepostBase.fetchRepostByLabels(
						LabelType.CATEGORY,
						this.itemType,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	// =============================================*
	/**
	 * タグによるリポストの検索.
	 * Tag -> Repost
	 *
	 * @param _primaryArgs
	 *            検索キーとなるタグモデル、省略不可
	 * @return 条件で取得されたリポストのリスト
	 */
	// =============================================*
	public static FindCondition findRepostByTags(
			Object... _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<RepostBase> fetch() {
				return RepostBase.fetchRepostByLabels(
						LabelType.TAG,
						this.itemType,
						this.contributor,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}
		}.primaryArgs(_primaryArgs).build();
	}

	// =============================================*
	/**
	 * アカウントによるリポストの検索.
	 * Account -> Repost
	 *
	 * @param _primaryArgs
	 *            検索キーとなるアカウントモデル、省略不可
	 * @return 条件で取得されたリポストのリスト
	 */
	// =============================================*
	public static FindCondition findRepostByAccounts(
			Object... _primaryArgs) {

		return new FindCondition() {
			@Override
			public List<RepostBase> fetch() {
				return RepostBase.fetchRepostByAccounts(
						this.itemType,
						this.labelType,
						this.orderBy,
						this.additionalSql,
						this.primaryArgs
						);
			}

			@Override
			public FindCondition contributor(
					Object _contributor
					)
						throws DeprecatedMethodUseException {

				throw new DeprecatedMethodUseException();
			}
//			@Override
//			public FindCondition contributor(
//					Object _contributor
//					) throws IllegalArgumentException {
//				throw new IllegalArgumentException();
//			}
//			@Override
//			@Deprecated
//			public FindCondition contributor(
//					Object _contributor
//					) throws IllegalArgumentException {
//				return this;
//			}

		}.primaryArgs(_primaryArgs).build();
	}

	/* ************************************************************ */
	/*
	 * 追加・削除ヘルパ
	 */
	/* ************************************************************ */
	public static enum ActionMode {
		ATTACH,
		DETACH
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * アイテムへのラベルの追加.
	 *
	 * @param _item
	 * @param _contributor
	 * @param _entities
	 * @return ラベルを追加した状態のアイテム
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// TODO 投稿者(contibutor)が違う場合はどうするのか？
	public static <T extends ItemBase> T attachLabels(
			T _item,
			Account _contributor,
			LabelBase... _labels) {

		return modifyLabels(
				ActionMode.ATTACH, _item, _contributor, _labels);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * アイテムへのラベルの削除.
	 *
	 * @param _item
	 * @param _contributor
	 * @param _labels
	 * @return ラベルを削除した状態のアイテム
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// TODO 投稿者(contibutor)が違う場合はどうするのか？
	public static <T extends ItemBase> T detachLabels(
			T _item,
			Account _contributor,
			LabelBase... _labels) {

		return modifyLabels(
				ActionMode.DETACH, _item, _contributor, _labels);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * アイテムへのラベル追加／削除処理.
	 *
	 * @param _item
	 * @param _contributor
	 * @param _labels
	 * @return ラベルを変更した状態のアイテム
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	// TODO 投稿者(contibutor)が違う場合はどうするのか？
	private static <T extends ItemBase> T modifyLabels(
			ActionMode _mode,
			T _item,
			Account _contributor,
			LabelBase... _labels) {

		ItemType itemType = ItemType.USER;
		if (_item instanceof Tweet) {
			itemType = ItemType.TWEET;
		}

		LabelType labelType = LabelType.CATEGORY;
		if (_labels[0] instanceof Tag) {
			labelType = LabelType.TAG;
		}

		List<? extends LabelBase> requests = Arrays.asList(_labels);

		List<? extends LabelBase> labels =
				(List<? extends LabelBase>) findLabelsByItems(
						labelType, itemType, _item).fetch();

		Logger.debug("labels.size:%s", labels.size());

		if (labelType == LabelType.CATEGORY) {
			_item.setCategories((List<Category>) labels);
		}
		else {
			_item.setTags((List<Tag>) labels);
		}

//		if (labels.containsAll(requests)) {
//			return _item;
//		}
		for (LabelBase req : requests) {
			if (labels.contains(req) == false) {
				if (_mode == ActionMode.ATTACH) {
					attach(_item, req, _contributor, itemType, labelType);
					if (labelType == LabelType.CATEGORY) {
						_item.getCategories().add((Category) req);
					}
					else {
						_item.getTags().add((Tag) req);
					}
				}
			}
			else if (_mode == ActionMode.DETACH) {
				req.delete();
//				LabelBase nm = LabelBase.findById(req.id);
//				nm.delete();
			}
		}
		return _item;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * ラベルへのアイテムの追加.
	 *
	 * @param _label
	 * @param _contributor
	 * @param _items
	 * @return ラベルを追加した状態のアイテム
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static <T extends LabelBase> T attachItems(
			T _label,
			Account _contributor,
			ItemBase... _items) {

		return modifyItems(ActionMode.ATTACH, _label, _contributor, _items);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * ラベルへのアイテムの削除.
	 *
	 * @param _label
	 * @param _contributor
	 * @param _items
	 * @return ラベルを削除した状態のアイテム
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static <T extends LabelBase> T detachItems(
			T _label,
			Account _contributor,
			ItemBase... _items) {

		return modifyItems(ActionMode.DETACH, _label, _contributor, _items);
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * ラベルへのアイテム追加／削除処理.
	 *
	 * @param _label
	 * @param _contributor
	 * @param _items
	 * @return ラベルを変更した状態のアイテム
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static <T extends LabelBase> T modifyItems(
			ActionMode _mode,
			T _label,
			Account _contributor,
			ItemBase... _items) {

		LabelType labelType = LabelType.CATEGORY;
		if (_label instanceof Tag) {
			labelType = LabelType.TAG;
		}

		ItemType itemType = ItemType.USER;
		if (_items[0] instanceof Tweet) {
			itemType = ItemType.TWEET;
		}

		List<? extends ItemBase> requests = Arrays.asList(_items);

		List<? extends ItemBase> items =
				(List<? extends ItemBase>) findItemsByLabels(
						itemType, labelType, _label).fetch();

		Logger.debug("items.size:%s", items.size());

		if (itemType == ItemType.USER) {
			_label.namedUsers = (List<User>) items;
		}
		else {
			_label.namedTweets = (List<Tweet>) items;
		}

		for (ItemBase req : requests) {
			if (items.contains(req) == false) {
				if (_mode == ActionMode.ATTACH) {
					attach(req, _label, _contributor, itemType, labelType);
					if (itemType == ItemType.USER) {
						_label.namedUsers.add((User) req);
					}
					else {
						_label.namedTweets.add((Tweet) req);
					}
				}
			}
			else if (_mode == ActionMode.DETACH) {
				req.delete();
			}
		}
		return _label;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * アイテムとラベルの関連付け追加処理.
	 *
	 * @param _item
	 * @param _label
	 * @param _contributor
	 * @param _itemClass
	 * @param _labelClass
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static void attach(
			ItemBase _item,
			LabelBase _label,
			Account _contributor,
			ItemType _itemClass,
			LabelType _labelClass) {

		if (_itemClass == ItemType.USER) {
			User user = (User) _item;
			if (_labelClass == LabelType.CATEGORY) {
				new RepostUserCategory(
						user, (Category) _label, _contributor).save();
			}
			else {
				new RepostUserTag(
						user, (Tag) _label, _contributor).save();
			}
		}
		else {
			Tweet tweet = (Tweet) _item;
			if (_labelClass == LabelType.CATEGORY) {
				new RepostTweetCategory(
						tweet, (Category) _label, _contributor).save();
			}
			else {
				new RepostTweetTag(
						tweet, (Tag) _label, _contributor).save();
			}

		}
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
