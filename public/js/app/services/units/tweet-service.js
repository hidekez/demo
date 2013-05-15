/** ************************************************************
 * つぶやきリスト・サービス
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
		"tweetService",
		['$q', '_', '$log', '$resource',
		function ($q, _, $log, $resource) {


			// I get all of the units.
			// TODO タイムアウト的な対処としてechoしておくか？
			function getTweets(lineName) {

				var deferred = $q.defer(),
					unitId;

				switch (lineName) {
					case 'timeline':
						unitId = "tl";
						break;
					case 'retweet':
						unitId = "rt";
						break;
					case 'favorite':
						unitId = "fav";
				}
				if (unitId === null) {
					return;
				}
				var Statuses = $resource("/twt/" + unitId);
				var Result = Statuses.query();
				$log.ex.debug("Result:", Result);
				deferred.resolve(Result);

				return( deferred.promise );

			}

			// I get the unit with the given ID.
			function getTweetByID(id) {

				var deferred = $q.defer();
				var tweet = _.findWithProperty(cache, "id", id);

				if (tweet) {
					deferred.resolve(ng.copy(tweet));
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
			cache["timeline"] = [
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
			cache["retweet"] = [
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
				getTweets    : getTweets,
				getTweetByID : getTweetByID
			});

		}]
	);

})(angular, myApp);