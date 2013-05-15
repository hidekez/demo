package models.items;

import play.Logger;
import models.reposts.IRepostElementType;
import models.reposts.NoFittingDataExistException;

/**
 * アイテム種類.
 *
 * @author H.Kezuka
 */
public enum ItemType implements IRepostElementType {

	TWEET('t'),
	USER('u');

	private char prefix;

	ItemType(char _prefix) {
		this.prefix = _prefix;
	}

	public char getPrefix() {
		return prefix;
	}

	@Override
	public String getLowerClassName() {
		switch (this) {
		case TWEET:
			return Tweet.class.getSimpleName().toLowerCase();
		case USER:
			return User.class.getSimpleName().toLowerCase();
		}
		return null;
	}

	@Override
	public String getSerialCodeColName() {
		switch (this) {
		case TWEET:
			return Tweet.getSerialCodeColName();
		case USER:
			return User.getSerialCodeColName();
		}
		return null;
	}

	@Override
	@Deprecated
	public String getSignatureColName() {
		switch (this) {
		case TWEET:
			return Tweet.getSignatureColName();
		case USER:
			return User.getSignatureColName();
		}
		return null;
	}

	@Override
	@Deprecated
	public String getDisplayNameColName() {
		switch (this) {
		case TWEET:
			return Tweet.getDisplayNameColName();
		case USER:
			return User.getDisplayNameColName();
		}
		return null;
	}

	@Override
	public String getMergedSerialCodeColName() {
		return getLowerClassName() + "." + getSerialCodeColName();
	}

	@Override
	@Deprecated
	public String getMergedSignatureColName() {
		return getLowerClassName() + "." + getSignatureColName();
	}

	@Override
	@Deprecated
	public String getMergedDisplayNameColName() {
		return getLowerClassName() + "." + getDisplayNameColName();
	}

}
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//=============================================*
//-------------------------------------+
