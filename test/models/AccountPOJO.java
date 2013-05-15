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
	public User loginUser;

	public Date enteredAt;

	public Date visitedAt;

	public LifeStateType life = LifeStateType.ALIVE;

	// =============================================*
	// 双方向リレーション
	// =============================================*
	public List<Tag> tags = new ArrayList<Tag>();

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
