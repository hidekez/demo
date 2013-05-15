/** ************************************************************
 * フォルダーリスト・サービス
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
		"folderService",
		['$q', '$log', '_',
		function ($q, $log, _) {


			// I get all of the units.
			function getFolders(lineName) {
$log.ex.debug("----------------------- getFolders ------------------");
				var deferred = $q.defer();

				deferred.resolve(ng.copy(cache[lineName]));

				return( deferred.promise );

			}

			// I get the unit with the given ID.
			function getFolderByID(id) {

				var deferred = $q.defer();
				var folder = _.findWithProperty(cache, "id", id);

				if (folder) {
					deferred.resolve(ng.copy(folder));
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
			cache["my"] = [
				{
					id          : "f01",
					name        : "プログラム関係",
					description : "プログラムに関すること",
					favorite    : false
				},
				{
					id          : "f02",
					name        : "ユーモア",
					description : "ユーモアあるの",
					favorite    : false
				}
			];
			cache["friends"] = [
				{
					id          : "u01",
					name        : "太郎",
					description : ""
				},
				{
					id          : "u02",
					name        : "ジロウ",
					description : ""
				}
			];
			cache["favorite"] = [
				{
					id          : "f03",
					name        : "遊び関係",
					description : "遊びの情報いろいろ",
					favorite    : true
				},
				{
					id          : "f04",
					name        : "政治",
					description : "",
					favorite    : true
				}
			];
			// ---------------------------------------------- //
			// ---------------------------------------------- //

			// Return the public API.
			return({
				getFolders    : getFolders,
				getFolderByID : getFolderByID
			});

		}]
	);

})(angular, myApp);