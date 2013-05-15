/*
 * 作成日 2012/04/04
 * 修正日 2012/06/05 コメントの変更（WZアウトライン用記号の削除）
 * 修正日 2013/01/11 JPAマージヘルパ追加
 */
package models;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import play.Logger;
import play.db.jpa.JPABase;
import play.db.jpa.Model;
import play.db.jpa.GenericModel.JPAQuery;
import play.data.validation.Validation;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * 独自モデル基底抽象クラス.
 *
 * @author H.Kezuka
 */
@MappedSuperclass
abstract public class MySuperModel extends Model {

	// =============================================*
	// エンティティステータス
	// =============================================*
	@Transient
	public boolean saved = false;
	@Transient
	public boolean hasErrors = false;
	@Transient
	public boolean hasSubs = false;
	@Transient
	public boolean isSubsPersistent = false;
	@Transient
	public boolean isNew = false;

//	@Transient
//	public static boolean hasRelation = false;

	/* ************************************************************ */
	/*
	 * 初期化
	 */
	/* ************************************************************ */
	// abstract public void initialize();

	/* ************************************************************ */
	/*
	 * 基本メソッド
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * toString
	 *
	 * @return インスタンス情報文字列
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Override
	public String toString() {
		return getFields().toString();
		// return toMap().toString();
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * 出力用データ作成
	 *
	 * @return ReflectionToStringBuilder
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	private ReflectionToStringBuilder getFields() {
		return new ReflectionToStringBuilder(this);
	}

	/* ************************************************************ */
	/*
	 * バリデーション
	 */
	/* ************************************************************ */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * バリデーションチェック.
	 *
	 * @return 検証に適合するならば true
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public boolean valid() {
		return Validation.current().valid(this).ok;
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * validateAndSave. {@link play.db.jpa.GenericModel#validateAndSave()}
	 *
	 * @return 検証に適合しセーブされた場合は true
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Override
	public boolean validateAndSave() {
		this.saved = super.validateAndSave();
		if (this.saved == false) {
			printValidationErrors();
		}
		return saved;
	}

	public void printValidationErrors() {
		Logger.info("%s ValidationAndSave has errors:"
				, this.getClass().getSimpleName());
		for (play.data.validation.Error e : Validation.current().errors()) {
			Logger.debug("mes:%s", e.message());
		}
	}

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * マージヘルパ.
	 * リレーショナルな関係を持つエンティティをすべてマージする。
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//	public void deepMerge() {
//		mergeAll();
//	}
//	public Deque<String> mergeErrors = new ArrayDeque<String>();
//	public boolean hasMergeError;

	abstract public void mergeSubs();

	public <T extends MySuperModel> T mergeAll() {
		mergeSubs();
		return merge();
	}

	/* ************************************************************ */
	/*
	 * 検索クエリーヘルパ
	 */
	/* ************************************************************ */
	public static class FindQueryHelper {
		protected String[] signatureParams;
		protected String[] displayNameParams;
		protected boolean isSignature;

		public FindQueryHelper signatures(String... _params){
			isSignature = true;
			this.signatureParams = _params;
			return this;
		}
		public FindQueryHelper displayNames(String... _params){
			isSignature = false;
			this.displayNameParams = _params;
			return this;
		}

		public JPAQuery getJPAQuery() throws IllegalStateException {
			throw new IllegalStateException();
		}
	}
}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
/* ************************************************************ */
/*
 * 以下、削除したコード
 * 　
 * 　
 * 　
 * 　
 * 　
 * 　
 * 　
 * 　
 * 　
 * 　
 */

