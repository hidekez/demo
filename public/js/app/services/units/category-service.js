/** ************************************************************
 * カテゴリー・サービス
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
		"categoryService",
		['$q', '_', '$resource','$http',
			function ($q, _, $resource, $http) {


				// I get all of the units.
				function getCategories(lineName, scope) {

					var deferred = $q.defer();

					if ("general" === lineName) {
//						var Category = $resource("/public/js/data/json/preset-category-list.json");
//						var Result = Category.query();
//						$log.ex.debug("Result:", Result);
//						deferred.resolve(Result);
						$http.get("/public/js/data/json/preset-category-list.json")
							.success(function(data){
								$log.ex.debug("data:", data);
								scope.setLineData("label","category","general",data);
							});
					}
					else {
						deferred.resolve(ng.copy(cache[lineName]));
					}

					return( deferred.promise );

				}

				// I get the unit with the given ID.
				function getCategoryByID(id) {

					var deferred = $q.defer();
					var category = _.findWithProperty(cache, "id", id);

					if (category) {
						deferred.resolve(ng.copy(category));
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
				cache["general"] = [
					{
						index       : 1,
						id          : "social",
						displayName : "社会",
						description : "",
						favorite    : false
					},
					{
						index       : 2,
						id          : "biz",
						displayName : "ビジネス",
						description : "",
						favorite    : false
					},
					{
						index       : 3,
						id          : "enta",
						displayName : "エンターテインメント",
						description : "",
						favorite    : false
					},
					{
						index       : 4,
						id          : "it",
						displayName : "IT",
						description : "",
						favorite    : false
					}
				];
				cache["news"] = [
					{
						index       : 1,
						id          : "social",
						displayName : "社会",
						description : "",
						favorite    : false
					},
					{
						index       : 2,
						id          : "gav",
						displayName : "政治",
						description : "",
						favorite    : false
					},
					{
						index       : 3,
						id          : "sports",
						displayName : "スポーツ",
						description : "",
						favorite    : false
					},
					{
						index       : 4,
						id          : "weather",
						displayName : "天気",
						description : "",
						favorite    : false
					}
				];
				cache["trend"] = [
					{
						index       : 1,
						id          : "scr",
						displayName : "サッカー",
						description : "",
						volume      : 100,
						favorite    : false
					},
					{
						index       : 2,
						id          : "wbc",
						displayName : "WBC",
						description : "",
						volume      : 1000,
						favorite    : false
					}
				];
				cache["favorite"] = [
					{
						index       : 1,
						id          : "program",
						displayName : "プログラミング",
						description : "",
						favorite    : true
					}
				];

				// ---------------------------------------------- //
				// ---------------------------------------------- //

				// Return the public API.
				return({
					getCategories   : getCategories,
					getCategoryByID : getCategoryByID
				});

			}]
	);

})(angular, myApp);