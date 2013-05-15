/*
 * 作成日 2012/04/12
 * 修正日 2012/06/05 コメントの変更（WZアウトライン用記号の削除）
 * 修正日 2012/10/23 Account -> MasterUser
 * 修正日 2013/01/01 MasterUser -> Account
 * 修正日 2013/01/01 リポスト試作からの取り入れ
 * 修正日 2013/01/11 JPAマージヘルパ追加
 */

package models;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.*;// Logger
import play.data.validation.InFuture;
import play.data.validation.InPast;
import play.data.validation.Required;
import play.data.validation.Valid;

import twitter4j.auth.AccessToken;
import utils.MyDateUtils;
import constants.LifeStateType;

import models.items.User;
import models.labels.Tag;

/**
 * アカウントモデル.
 *
 * @author H.Kezuka
 */
@Entity
public class Account extends MySuperModel {

	// =============================================*
	// コア
	// =============================================*
//	/**
//	 * アカウントユーザーセット.
//	 * <ユーザー、所持者同一性認証フラグ>
//	 */
//	@Required
//	@ElementCollection
//	public Map<User, Boolean> users;
//
//	/**
//	 * カレントユーザー.
//	 */
//	@Transient
//	public User currentUser;

	/**
	 * ログインユーザー.
	 */
	@Required
	@Basic(optional = false)
	@Valid
	@OneToOne
	public User loginUser;

	/**
	 * 登録日時.
	 */
	// TODO DateIndex化する？
	@Required
	@Basic(optional = false)
	@InFuture(MyDateUtils.BIRTH_OF_MYAPP_YMD_STR)
	@InPast
	private Date enteredAt;

	/**
	 * 最終訪問日時.
	 */
	@Required
	@Basic(optional = false)
	@InFuture(MyDateUtils.BIRTH_OF_MYAPP_YMD_STR)
	@InPast
	private Date visitedAt;

	/**
	 * 状態.
	 */
	// Basic(optional=false) -> null禁止
	@Required
	@Basic(optional = false)
	@Enumerated(EnumType.STRING)
	public LifeStateType life = LifeStateType.ALIVE;

//	// =============================================*
//	// 数値
//	// =============================================*
//	/**
//	 * ユーザー数.
//	 * マルチアカウントとして登録されているユーザー数 users.sizeに等しい。
//	 */
//	@Transient
//	public int userSize;
//
//	/**
//	 * 訪問総数.
//	 * マルチアカウントとして登録されているユーザーの、全訪問回数合計。
//	 */
//	@Transient
//	public int wholeVisitCount;

	// =============================================*
	// 双方向リレーション
	// =============================================*
	@OneToMany(mappedBy = "author")
	public List<Tag> tags;

	{
		this.tags = new ArrayList<Tag>();
	}

	// =============================================*
	// キャッシュ
	// =============================================*
	@Transient
	public twitter4j.auth.AccessToken accessToken;

	/* ************************************************************ */
	/*
	 * コンストラクタ
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * コンストラクタ
	 *
	 * @param _loginUser
	 *            ログインユーザー
	 * @param _enteredAt
	 *            登録日
	 * @param _visitedAt
	 *            最新訪問日
	 * @param _life
	 *            状態
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public Account(
			User _loginUser,
			Date _enteredAt,
			Date _visitedAt,
			LifeStateType _life) {

		this.loginUser = _loginUser;
		setEnteredAt(_enteredAt);
		setVisitedAt(_visitedAt);
		this.life = _life;
		// -------------------------------------+
		// findAndAddUserCommon(_users);//Validation含む
	}

	public Account(User _loginUser) {
		this.loginUser = _loginUser;
		initWhole();
	}

	/* ************************************************************ */
	/*
	 * プロパティ初期化.
	 */
	/* ************************************************************ */
	public void initWhole() {
		initDates();
		initState();
	}

	// =============================================*
	public void initDates() {
		Date now = new Date();
		setEnteredAt(now);
		setVisitedAt(now);
	}

	// =============================================*
	public void initState() {
		this.life = LifeStateType.ALIVE;
	}

	/* ************************************************************ */
	/*
	 * セットアップ.
	 */
	/* ************************************************************ */
	public void copyAccessToken() {
		this.loginUser.accessToken.accessKey = accessToken.getToken();
		this.loginUser.accessToken.accessKeySecret = accessToken
				.getTokenSecret();
		this.loginUser.authenticated = true;
	}

    public void copyAccessTokenFromUser() {
        this.accessToken = new AccessToken(
                this.loginUser.accessToken.accessKey,
                this.loginUser.accessToken.accessKeySecret);
    }

    /* ************************************************************ */
	/*
	 * アクセッサ
	 */
	/* ************************************************************ */
	// enteredAt
	public void setEnteredAt(Date _date) {
		this.enteredAt = MyDateUtils.safeCopyOrNull(_date);
	}

	public Date getEnteredAt() {
		return MyDateUtils.safeCopyOrNull(this.enteredAt);
	}

	// =============================================*
	// visitedAt
	public void setVisitedAt(Date _date) {
		this.visitedAt = MyDateUtils.safeCopyOrNull(_date);
	}

	public void setVisitedAt(){
		this.visitedAt = MyDateUtils.now();
	}

	public Date getVisitedAt() {
		return MyDateUtils.safeCopyOrNull(this.visitedAt);
	}



	/* ************************************************************ */
	/*
	 * JPA関連
	 */
	/* ************************************************************ */
	@Override
	public void mergeSubs() {
		if (this.loginUser != null && this.loginUser.isPersistent() == false) {
			this.loginUser.mergeSubs();
			this.loginUser = loginUser.merge();
		}
		this.tags = null;
	}

	/* ************************************************************ */
	/*
	 * 基本メソッド
	 */
	/* ************************************************************ */
	/*
	 * (非 Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
//		return this.loginUser.userName.getFullname();
		return this.loginUser.screenName;
	}

	/* ************************************************************ */
	/*
	 * クラス情報スタティックヘルパ
	 *
	 * @see models.reposts.IRepostElement#getMergedKeyColName()
	 */
	/* ************************************************************ */
	// SQLにて使用
	private static final String LOGIN_USER_COL_NAME = "loginUser";
	private static final String ENTERED_AT_COL_NAME = "enteredAt";
	private static final String VISITED_AT_COL_NAME = "visitedAt";
	private static final String LIFE_COL_NAME = "life";
	private static final String LOGIN_USERS_UNIQUE_COL_NAME =
			LOGIN_USER_COL_NAME + "." + User.getSerialCodeColName();
	private static final String LOGIN_USERS_SIGNATURE_COL_NAME =
			LOGIN_USER_COL_NAME + "." + User.getSignatureColName();
	private static final String LOGIN_USERS_DISPLAY_NAME_COL_NAME =
			LOGIN_USER_COL_NAME + "." + User.getDisplayNameColName();
	private static final String LOGIN_USERS_OUTER_ID_COL_NAME =
			LOGIN_USER_COL_NAME + "." + User.getOuterIdColName();

	public static String getUniqueColName() {
		return LOGIN_USERS_UNIQUE_COL_NAME;
	}

	public static String getSignatureColName() {
		return LOGIN_USERS_SIGNATURE_COL_NAME;
	}

	public static String getDisplayNameColName() {
		return LOGIN_USERS_DISPLAY_NAME_COL_NAME;
	}

	/* ************************************************************ */
	/*
	 * 検索ヘルパ
	 */
	/* ************************************************************ */
	// 引数をStringにする必要ありか？
	public static JPAQuery findByOuterId(Long... _ids) {
		return find(LOGIN_USERS_OUTER_ID_COL_NAME, _ids);
	}

	public static JPAQuery findByLoginName(String... _name) {
		return find(LOGIN_USERS_SIGNATURE_COL_NAME, _name);
	}

	public static JPAQuery findByDisplayName(String... _name) {
		return find("loginUser.userName", _name);
	}

	public static JPAQuery findByUser(User _user) {
		return find("byLoginUser", _user);
	}

	/* ************************************************************ */
	/*
	 * 認証ヘルパ
	 */
	/* ************************************************************ */
	public void clearAuthentication(){
		this.loginUser.authenticated = false;
		this.loginUser.accessToken.accessKey = null;
		this.loginUser.accessToken.accessKeySecret = null;
		this.loginUser.validateAndSave();
		this.accessToken = null;
		this.life = LifeStateType.AUTHENTICATION_ERROR;
		this.setVisitedAt();
	}


	/* ************************************************************ */

//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * 利用状況のチェック ( void method )
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public static void checkAndSetupStatus() {
//		List<MasterUser> masters = MasterUser.findAll();
//		for (MasterUser masterUser : masters) {
//			List<User> users = User.find("byAccount", masterUser)
//					.fetch();
//			// List<RepostUser> users = RepostUser.find(
//			// "SELECT ru FROM RepostUser ru WHERE ru.xAccount.id =?",
//			// account.id ).fetch();
//
//			masterUser.life = (users.size() > 0)
//					? LifeStateType.ALIVE
//					: LifeStateType.DEAD;
//			masterUser.save();
//			Logger.debug("account.id:" + masterUser.id);
//			Logger.debug("users.size:" + users.size());
//		}
//	}

	/* ************************************************************ */

//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * アカウントユーザー 重複チェックしてリストに追加 コア処理
//	 *
//	 * @param _user
//	 *            アカウントユーザー（オブジェクト）
//	 * @return 追加されたか否か
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public boolean addUserCore(User _user) {
//		Logger.debug(">>ItemBase.addUserCommonCore");
//		if (_user == null)
//			return false;
//		// if (!this.users.contains(_user)) {
//		try {
//			this.users.put(_user);
//			return true;
//		}
//		catch (Exception e) {
//			// expression★
//			return false;
//			// } finally {
//			// expression
//		}
//		// }
//	}
//
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	/**
//	 * アカウントユーザー 検索してリストに追加 ListがNullの場合はNewする
//	 *
//	 * @param _user
//	 *            アカウントユーザー（オブジェクト）
//	 * @return 追加されたか否か
//	 */
//	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public boolean findAndAddUser(User _user) {
//		Logger.debug(">>ItemBase.findAndAddUserCommon");
//		if (_user == null)
//			return false;
//		User user = User.findOrCreateAndSave(_user);// Validate含む
//		if (user == null)
//			return false;
//		// fieldの初期化
//		if (this.users == null) {
//			this.users = new HashMap<User>();
//		}
//		return addUserCore(_user);// Validate含まない
//	}

	/* ************************************************************ */

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
