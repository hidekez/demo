/** ************************************************************
 * タグ・サービス
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
		"tagService",
		['$q', '_',
		function ($q, _) {


			// I get all of the units.
			function getTags(lineName) {

				var deferred = $q.defer();

				deferred.resolve(ng.copy(cache[lineName]));

				return( deferred.promise );

			}

			// I get the unit with the given ID.
			function getTagByID(id) {

				var deferred = $q.defer();
				var tag = _.findWithProperty(cache, "id", id);

				if (tag) {
					deferred.resolve(ng.copy(tag));
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
					id          : "t01",
					name        : "Java",
					description : "",
					favorite    : false
				},
				{
					id          : "t02",
					name        : "JavaScript",
					description : "",
					favorite    : false
				}
			];
			cache["trend"] = [
				{
					id          : "t03",
					name        : "AngulerJS",
					description : "",
					favorite    : false
				},
				{
					id          : "t04",
					name        : "program",
					description : "",
					favorite    : false
				}
			];
			cache["favorite"] = [
				{
					id          : "t05",
					name        : "tolist",
					description : "",
					favorite    : true
				},
				{
					id          : "t01",
					name        : "Java",
					description : "",
					favorite    : true
				},
				{
					id          : "t06",
					name        : "プログラミング",
					description : "",
					favorite    : true
				}
			];
//			cache["edit"] = [
//				{
//					id          : "saburo",
//					name        : "三郎",
//					description : "きたこれ"
//				}
//			];

			// ---------------------------------------------- //
			// ---------------------------------------------- //

			// Return the public API.
			return({
				getTags    : getTags,
				getTagByID : getTagByID
			});

		}]
	);

})(angular, myApp);