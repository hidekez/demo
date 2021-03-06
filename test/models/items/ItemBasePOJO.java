package models.items;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import utils.MyDateUtils;
import constants.ItemStateType;

import models.items.ItemId;
import models.items.User;
import models.items.multi.UserName;
import models.labels.*;

/**
 * アイテム基底モデル・テスト用POJOクラス.
 * AbstractItemのフィールド。
 *
 * @author H.Kezuka
 */
public class ItemBasePOJO {

	// -------------------------------------+
	// コア
	public ItemId itemId;
	public User author;
	public String serialCode;
	public Date createdAt = MyDateUtils.now();
	public Date enteredAt = MyDateUtils.now();
	public ItemStateType state = ItemStateType.OK;
	// -------------------------------------+
	// キャッシュ
	public List<LabelBase> labels;
	public List<Category> categories;
	public List<Tag> tags;
	// -------------------------------------+
}
