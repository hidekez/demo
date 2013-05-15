/*
 * 作成日 2013/01/10
 * 修正日
 */

package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import constants.LifeStateType;

import models.items.User;
import models.labels.Tag;

/**
 * アカウントモデル・テスト用POJOクラス.
 *
 * @author H.Kezuka
 */
public class AccountPOJO {

	// =============================================*
	// コア
	// =============================================*
	// public Map<User, Boolean> users;

	// public User currentUser;

	public User loginUser;

	public Date enteredAt;

	public Date visitedAt;

	public LifeStateType life = LifeStateType.ALIVE;

//	// =============================================*
//	// 数値
//	// =============================================*
//	public int userSize;
//	public int wholeVisitCount;

	// =============================================*
	// 双方向リレーション
	// =============================================*
	public List<Tag> tags = new ArrayList<Tag>();

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
