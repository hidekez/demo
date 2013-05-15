package models.reposts;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.Model;

import models.*;
import models.items.*;
import models.items.twitter.*;
import models.labels.*;

/**
 * ユーザーｘタグのリポストモデル.
 * O/Rマッパー対策として
 *
 * @author H.Kezuka
 */
@Entity
//@Table(name = "Repost_User_Tag")
public class RepostUserTag extends RepostBase {

	@Required
	@Valid
	@OneToOne
	public User user;

	@Required
	@Valid
	@OneToOne
	public Tag tag;

	/* ************************************************************ */
	/*
	 * コンストラクタ
	 */
	/* ************************************************************ */
	public RepostUserTag(User _user, Tag _tag, Account _contributor) {
		super(_contributor);
		this.user = _user;
		this.tag = _tag;
		this.item = _user;
		this.label = _tag;
	}

	/* ************************************************************ */
	/*
	 * アクセッサ
	 */
	/* ************************************************************ */
	@Override
	public void setItem() {
		this.item = this.user;
	}

	@Override
	public void setLabel() {
		this.label = this.tag;
	}

	/* ************************************************************ */
	/*
	 * 遅延生成
	 */
	/* ************************************************************ */
	// TODO バリデーション例外
	public static RepostUserTag findOrCreateAndSave(
			User _user, Tag _tag, Account _contributor) {

		RepostUserTag repost =
				findUniquely(_user, _tag, _contributor);
		if (repost == null) {
			repost = new RepostUserTag(_user, _tag, _contributor);
			repost.validateAndSave();
		}
		return repost;
	}

	// 特定的な取得
	public static RepostUserTag findUniquely(
			User _user, Tag _tag, Account _contributor) {

		return find("byUserAndTagAndContributor",
				_user, _tag, _contributor).first();
	}

	/* ************************************************************ */
	/*
	 * JPA関連
	 */
	/* ************************************************************ */
	@Override
	public void mergeSubs() {
		super.mergeSubs();// contributor
		if (this.user != null && this.user.isPersistent() == false) {
			this.user.mergeSubs();
			this.user = user.merge();
		}
		if (this.tag != null && this.tag.isPersistent() == false) {
			this.tag.mergeSubs();
			this.tag = tag.merge();
		}
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

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
