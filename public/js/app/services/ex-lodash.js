/** ************************************************************
 * 拡張lodashライブラリを提供 // I provide an augmented lodash library.
 *
 * 参照元：
 *     ブログ：http://www.bennadel.com/blog/2441-Nested-Views-Routing-And-Deep-Linking-With-AngularJS.htm
 *    Github：https://github.com/bennadel/AngularJS-Routing
 *    デモ：http://bennadel.github.com/AngularJS-Routing
 *
 * Author      : H.Kezuka
 * Created at  : 2013/03/28
 * Modified at :
 * *********************************************************** */
(function (ng, app, _) {

	"use strict";

	app.factory(
		"_",
		function () {

			/**
			 * 与えられた名前と値を持つアイテムだけのコレクションを返すフィルタ// I filter the collection down to items with the given property value.
			 *
			 * @param collection
			 * @param name
			 * @param value
			 * @return {*}
			 */
			_.filterWithProperty = function (collection, name, value) {

				var result = _.filter(
					collection,
					function (item) {
						return( item[ name ] === value );
					}
				);

				return( result );
			};

			/**
			 * 与えられた名前と値を持つ、最初のアイテムを抽出。// I find the first collection item with the given property value.
			 *
			 * @param collection
			 * @param name
			 * @param value
			 * @return {*}
			 */
			_.findWithProperty = function (collection, name, value) {

				var result = _.find(
					collection,
					function (item) {
						return( item[ name ] === value );
					}
				);

				return( result );
			};

			/**
			 * 指定されたプロパティでコレクションをソート。// I sort the collection on the given property.
			 *
			 * @param collection 処理したいコレクション
			 * @param name ソート基準となるプロパティ名
			 * @param direction ソート方向、"asc"で昇順、他は降順
			 * @return {*} ソート済みのコレクション
			 */
			_.sortOnProperty = function (collection, name, direction) {

				var indicator = ( ( direction.toLowerCase() === "asc" ) ? -1 : 1 );

				collection.sort(
					function (a, b) {

						if (a[ name ] < b[ name ]) {
							return( indicator );
						}
						else if (a[ name ] > b[ name ]) {
							return( -indicator );
						}

						return( 0 );
					}
				);

				return( collection );
			};

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			// パブリックAPIを返します。// Return the public API.

			return( _ );

		}
	);

})(angular, myApp, _.noConflict());

// Release the global reference to the lodash library.
// This way, we make sure that everyone goes
// through our service object in order to get to the utility library.
// lodashライブラリへのグローバル参照をリリースする。
// この方法では、誰もがユーティリティライブラリを取得するために、
// 私たちのサービスオブジェクトを通過していることを確認してください。

/** ************************************************************
 * *********************************************************** */
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
// =============================================*
// -------------------------------------+
