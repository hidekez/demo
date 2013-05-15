package models.items;

import java.util.Date;
import java.util.List;

import models.items.ItemBase.AbstractBuilder;
import models.items.User.Builder;
import models.labels.Category;
import models.labels.LabelBase;
import models.labels.Tag;

import constants.ItemStateType;

/**
 * ItemBaseをテストするためのダミークラス.
 * テーブルは生成されないよう、@Entityはつけない。
 *
 * @author H.Kezuka
 */
public class ItemCore extends ItemBase {

	/**
	 * @param _builder
	 */
	public ItemCore(AbstractBuilder _builder) {
		super(_builder);
	}

	/* ************************************************************ */

	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	/**
	 * ビルダー（インナークラス）.
	 */
	/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
	public static class Builder extends ItemBase.AbstractBuilder {

		// =============================================*
		// コンストラクター
		// =============================================*
		public Builder() {}

		// =============================================*
		// 親のセッターラッパー
		// =============================================*
		// -------------------------------------+
		// コア
		@Override
		public Builder itemId(ItemId _val) {
			setItemId(_val);
			return this;
		}

		@Override
		public Builder createdAt(Date _val) {
			setCreatedAt(_val);
			return this;
		}

		@Override
		public Builder enteredAt(Date _val) {
			setEnteredAt(_val);
			return this;
		}

		@Override
		public Builder state(ItemStateType _val) {
			setState(_val);
			return this;
		}

		// -------------------------------------+
		// キャッシュ
		@Override
		public <T extends AbstractBuilder> T labels(List<LabelBase> _val) {
			setLabels(_val);
			return (T) this;
		}

		@Override
		public <T extends AbstractBuilder> T categories(List<Category> _val) {
			setCategories(_val);
			return (T) this;
		}

		@Override
		public <T extends AbstractBuilder> T tags(List<Tag> _val) {
			setTags(_val);
			return (T) this;
		}

		// =============================================*
		// ビルド
		// =============================================*
		/**
		 * ビルド
		 *
		 * @see models.items.ItemBase.AbstractBuilder#build()
		 */
		@Override
		public ItemCore build() {
			return new ItemCore(this);
		}

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see models.items.ItemBase#initSubs()
	 */
	@Override
	public void initSubs() {
		;// 何もしない。作る必要なし。
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see models.items.ItemBase#createUID()
	 */
	@Override
	protected String createSerialCode() {
		return "dummy" + this.id;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see models.items.ItemBase#setupSubs()
	 */
	@Override
	public void setupSubs() {
		;// 何もしない。作る必要なし。
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see models.items.ItemBase#getCategories()
	 */
	@Override
	public List<Category> getCategories() {
		return null;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see models.items.ItemBase#setCategories(java.util.List)
	 */
	@Override
	public void setCategories(List<Category> _categories) {
		;// 何もしない。作る必要なし。
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see models.items.ItemBase#getTags()
	 */
	@Override
	public List<Tag> getTags() {
		return null;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see models.items.ItemBase#setTags(java.util.List)
	 */
	@Override
	public void setTags(List<Tag> _tags) {
		;// 何もしない。作る必要なし。
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see models.items.ItemBase#getItemType()
	 */
	@Override
	protected ItemType getItemType() {
		return null;// 何もしない。作る必要なし。
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see models.items.ItemBase#getSelf()
	 */
	@Override
	protected <T extends ItemBase> T getSelf() {
		return null;// 何もしない。作る必要なし。
	}

}
