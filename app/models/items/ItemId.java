/*
 * 作成日 2012/04/09
 * 修正日 2012/04/13 entryDate, stateの削除
 * 修正日 2012/04/24 toString,toMap,toJsonの削除、@Hide導入
 * 修正日 2012/06/05 コメントの変更（WZアウトライン用記号の削除）
 * 修正日 2013/01/11 JPAマージヘルパ追加
 */
package models.items;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.db.jpa.Model;
import models.MySuperModel;

/**
 * アイテム共通ID生成用クラス.
 *
 * @author H.Kezuka
 */
@Entity
public class ItemId extends MySuperModel {

	// play.db.jpa.Modelを継承しているので、long id が自動で付与されている。

	//ItemBaseは固有のテーブルを持たないので、双方向設定はできない
//	@OneToOne
//	public ItemBase item;

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/*
	 * (非 Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	@Override
	public String toString() {
		return "ItemId [id=" + this.id + "]";
	}

	/* ************************************************************ */
	/*
	 * JPA関連
	 */
	/* ************************************************************ */
	@Override
	public void mergeSubs() {
		// 何もしない
	}
}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+

