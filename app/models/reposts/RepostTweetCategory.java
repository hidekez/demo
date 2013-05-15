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

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.jpa.Model;

import models.*;
import models.items.*;
import models.items.twitter.*;
import models.labels.*;

/**
 * つぶやきｘカテゴリーのリポストモデル.
 * O/Rマッパー対策として
 *
 * @author H.Kezuka
 */
@Entity
//@Table(name = "Repost_Tweet_Category")
public class RepostTweetCategory extends RepostBase {
// implements IRepostTweet,IRepostCategory

	@Required
	@Valid
	@OneToOne
	public Tweet tweet;

	@Required
	@Valid
	@OneToOne
	public Category category;

	/* ************************************************************ */
	/*
	 * コンストラクタ
	 */
	/* ************************************************************ */
	public RepostTweetCategory(
			Tweet _tweet, Category _category, Account _contributor) {
		super(_contributor);
		this.tweet = _tweet;
		this.category = _category;
		this.item = _tweet;
		this.label = _category;
	}

	/* ************************************************************ */
	/*
	 * アクセッサ
	 */
	/* ************************************************************ */
	@Override
	public void setItem() {
		this.item = this.tweet;
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
	public static RepostTweetCategory findOrCreateAndSave(
			Tweet _tweet, Category _category, Account _contributor) {

		RepostTweetCategory repost =
				findUniquely(_tweet, _category, _contributor);
		if (repost == null) {
			repost = new RepostTweetCategory(_tweet, _category, _contributor);
			repost.validateAndSave();
		}
		return repost;
	}

	// 特定的な取得
	public static RepostTweetCategory findUniquely(
			Tweet _tweet, Category _category, Account _contributor) {

		return find("byTweetAndCategoryAndContributor",
				_tweet, _category, _contributor).first();
	}

	/* ************************************************************ */
	/*
	 * JPA関連
	 */
	/* ************************************************************ */
	@Override
	public void mergeSubs() {
		super.mergeSubs();// contributor
		if (this.tweet != null && this.tweet.isPersistent() == false) {
			this.tweet.mergeSubs();
			this.tweet = tweet.merge();
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
