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
 * つぶやきｘタグのリポストモデル.
 * O/Rマッパー対策として
 *
 * @author H.Kezuka
 */
@Entity
//@Table(name = "Repost_Tweet_Tag")
public class RepostTweetTag extends RepostBase {

	@Required
	@Valid
	@OneToOne
	public Tweet tweet;

	@Required
	@Valid
	@OneToOne
	public Tag tag;

	/* ************************************************************ */
	/*
	 * コンストラクタ
	 */
	/* ************************************************************ */
	public RepostTweetTag(Tweet _tweet, Tag _tag, Account _contributor) {
		super(_contributor);
		this.tweet = _tweet;
		this.tag = _tag;
		this.item = _tweet;
		this.label = _tag;
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
		this.label = this.tag;
	}

	/* ************************************************************ */
	/*
	 * 遅延生成
	 */
	/* ************************************************************ */
	// TODO バリデーション例外
	public static RepostTweetTag findOrCreateAndSave(
			Tweet _tweet, Tag _tag, Account _contributor) {

		RepostTweetTag repost =
				findUniquely(_tweet, _tag, _contributor);
		if (repost == null) {
			repost = new RepostTweetTag(_tweet, _tag, _contributor);
			repost.validateAndSave();
		}
		return repost;
	}

	// 特定的な取得
	public static RepostTweetTag findUniquely(
			Tweet _tweet, Tag _tag, Account _contributor) {

		return find("byTweetAndTagAndContributor",
				_tweet, _tag, _contributor).first();
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
