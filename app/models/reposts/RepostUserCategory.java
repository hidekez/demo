/*
 * 作成日 2013/01/01 リポスト試作からの取り入れ
 * 修正日 2013/01/07 適応のための簡単な手直し
 */
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
 * ユーザーｘカテゴリーのリポストモデル.
 * O/Rマッパー対策として
 *
 * @author H.Kezuka
 */
@Entity
//@Table(name = "Repost_User_Category")
public class RepostUserCategory extends RepostBase {

	@Required
	@Valid
	@OneToOne
	public User user;

	@Required
	@Valid
	@OneToOne
	public Category category;

	/* ************************************************************ */
	/*
	 * コンストラクタ
	 */
	/* ************************************************************ */
	public RepostUserCategory(
			User _user, Category _category, Account _contributor) {
		super(_contributor);
		this.user = _user;
		this.category = _category;
		this.item = _user;
		this.label = _category;
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
		this.label = this.category;
	}

	/* ************************************************************ */
	/*
	 * 遅延生成
	 */
	/* ************************************************************ */
	// TODO バリデーション例外
	public static RepostUserCategory findOrCreateAndSave(
			User _user, Category _category, Account _contributor) {

		RepostUserCategory repost =
				findUniquely(_user, _category, _contributor);
		if (repost == null) {
			repost = new RepostUserCategory(_user, _category, _contributor);
			repost.validateAndSave();
		}
		return repost;
	}

	// 特定的な取得
	public static RepostUserCategory findUniquely(
			User _user, Category _category, Account _contributor) {

		return find("byUserAndCategoryAndContributor",
				_user, _category, _contributor).first();
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
		if (this.category != null && this.category.isPersistent() == false) {
			this.category.mergeSubs();
			this.category = category.merge();
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
