/** ************************************************************
 * リストリスト・サービス
 *
 * 参照元：
 *     ブログ：http://www.bennadel.com/blog/2441-Nested-Views-Routing-And-Deep-Linking-With-AngularJS.htm
 *    Github：https://github.com/bennadel/AngularJS-Routing
 *    デモ：http://bennadel.github.com/AngularJS-Routing
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/29
 * Modified at :
 * *********************************************************** */

(function (ng, app) {

	"use strict";

	// I provide a repository for the units.
	app.service(
		"listService",
		['$q', '_',
		function ($q, _) {


			// I get all of the units.
			function getLists(lineName) {

				var deferred = $q.defer();

				deferred.resolve(ng.copy(cache[lineName]));

				return( deferred.promise );

			}

			// I get the unit with the given ID.
			function getListByID(id) {

				var deferred = $q.defer();
				var list = _.findWithProperty(cache, "id", id);

				if (list) {
					deferred.resolve(ng.copy(list));
				}
				else {
					deferred.reject();
				}

				return( deferred.promise );

			}

			// ---------------------------------------------- //
			// ---------------------------------------------- //

			// Set up the units data cache. For this demo, we'll just use static data.
			var cache = [];
			cache["subscribed"] = [
				{
					id          : "taro",
					name        : "太郎",
					description : "こんにちは、太郎です♪"
				},
				{
					id          : "jiro",
					name        : "ジロー",
					description : "ラーメンで有名になっちゃった"
				}
			];
			cache["member of"] = [
				{
					id          : "hanako",
					name        : "花子",
					description : "花子です"
				},
				{
					id          : "tom",
					name        : "トム",
					description : "Hello!"
				}
			];
			cache["favorite"] = [
				{
					id          : "yoshiko",
					name        : "良子",
					description : "良子よ"
				},
				{
					id          : "bob",
					name        : "ボブ",
					description : "What's up!"
				}
			];

			// ---------------------------------------------- //
			// ---------------------------------------------- //

			// Return the public API.
			return({
				getLists    : getLists,
				getListByID : getListByID
			});

		}]
	);

})(angular, myApp);