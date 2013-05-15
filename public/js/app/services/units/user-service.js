/** ************************************************************
 * ユーザーリスト・サービス
 *
 * 参照元：
 *     ブログ：http://www.bennadel.com/blog/2441-Nested-Views-Routing-And-Deep-Linking-With-AngularJS.htm
 *    Github：https://github.com/bennadel/AngularJS-Routing
 *    デモ：http://bennadel.github.com/AngularJS-Routing
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/22
 * Modified at :
 * *********************************************************** */

(function (ng, app) {

	"use strict";

	// I provide a repository for the units.
	app.service(
		"userService",
		['$q', '_',
		function ($q, _) {


			// I get all of the units.
			function getUsers(lineName) {

				var deferred = $q.defer();

				deferred.resolve(ng.copy(cache[lineName]));

				return( deferred.promise );

			}

			// I get the unit with the given ID.
			function getUserByID(id) {

				var deferred = $q.defer();
				var user = _.findWithProperty(cache, "id", id);

				if (user) {
					deferred.resolve(ng.copy(user));
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
			cache["following"] = [
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
			cache["follower"] = [
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

			// ---------------------------------------------- //
			// ---------------------------------------------- //

			// Return the public API.
			return({
				getUsers    : getUsers,
				getUserByID : getUserByID
			});

		}]
	);

})(angular, myApp);