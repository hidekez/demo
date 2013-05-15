/** ************************************************************
 * ユニットリスト・コントローラー集
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */

///** ************************************************************
// * Itemリスト（ユニットリスト）・コントローラー
// *
// * Author      : H.Kezuka
// * Created at  : 2013/04/10
// * Modified at :
// * *********************************************************** */
//(function (ng, app) {
//
//	"use strict";
//
//	var controllerName = "ItemListCtrl";
//
//	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
//	app.controller(
//		controllerName,
//		function ($scope, $log, requestContext, renderHelper, twitter) {
//
//			// debug ------------------------------
//			//setLabel(controllerName) and debug(controllerName)
//			$log.ex.init(controllerName);
//			// debug ------------------------------
//
//		}
//	);
//
//})(angular, myApp);
//
///** ************************************************************
// * Labelリスト（ユニットリスト）・コントローラー
// *
// * Author      : H.Kezuka
// * Created at  : 2013/04/10
// * Modified at :
// * *********************************************************** */
//(function (ng, app) {
//
//	"use strict";
//
//	var controllerName = "LabelListCtrl";
//
//	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
//	app.controller(
//		controllerName,
//		function ($scope, $log, requestContext, renderHelper, twitter) {
//
//			// debug ------------------------------
//			//setLabel(controllerName) and debug(controllerName)
//			$log.ex.init(controllerName);
//			// debug ------------------------------
//
//		}
//	);
//
//})(angular, myApp);
//
///** ************************************************************
// * Repostリスト（ユニットリスト）・コントローラー
// *
// * Author      : H.Kezuka
// * Created at  : 2013/04/10
// * Modified at :
// * *********************************************************** */
//(function (ng, app) {
//
//	"use strict";
//
//	var controllerName = "RepostListCtrl";
//
//	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
//	app.controller(
//		controllerName,
//		function ($scope, $log, requestContext, renderHelper, twitter) {
//
//			// debug ------------------------------
//			//setLabel(controllerName) and debug(controllerName)
//			$log.ex.init(controllerName);
//			// debug ------------------------------
//
//		}
//	);
//
//})(angular, myApp);

/** ************************************************************
 * Item/Twitterツイートリスト（ユニットリスト）・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "item.twitter.TweetListCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log', '_', 'tweetService', 'unitLoadHelper',
		function ($scope, $log, _, tweetService, unitLoadHelper) {

			// debug ------------------------------
			$log.ex.init(controllerName);
			// debug ------------------------------

			// 表示用のユーザーデータ // I am the tweet and the list of pets that are being viewed.
			$scope.tweets = null;

			$scope.getUnits = function(){
				return $scope.tweets;
			};

			// リモートデータをローカルビューモデルに適用 //  I apply the remote data to the local view model.
			function applyRemoteData(tweets) {
				$scope.tweets = _.sortOnProperty(tweets, "name", "asc");
				return $scope.tweets;
			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initTweetList = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initTweetList()");
				// debug ------------------------------
				unitLoadHelper.setup($scope, "tweet", $scope.tweets, applyRemoteData, tweetService.getTweets);

			};
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Item/Twitterユーザーリスト（ユニットリスト）・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "item.twitter.UserListCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log', '_', 'userService', 'unitLoadHelper',
//		function ($scope, $log, $resource, requestContext, renderHelper, twitter) {
//		function ($scope, $log, $location, $q, requestContext, userService, _) {
		function ($scope, $log, _, userService, unitLoadHelper) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			// debug ------------------------------

			//Cacheにしていくところ、とりあえず

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Define Controller Variables.

			// Get the render context local to this controller (and relevant params).
//			var renderContext = requestContext.getRenderContext("standard.pets.list", "userID");

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Define Scope Variables.
//			$scope.datas = $scope.datas || [];
//			var lineName = $scope.getActiveLineName();
//			$log.ex.debug("$$$$$$$$$$$$$$$$$$scope.getActiveLineName:", lineName);
//			var users = $scope.getLineData(lineName) || [];

			// 表示用のユーザーデータ // I am the tweet and the list of pets that are being viewed.
			$scope.users = null;

			$scope.getUnits = function(){
				return $scope.users;
			};

//			$scope.mainTree = $scope.getMainTree();
//			$scope.datas[lineName] = $scope.datas[lineName] || [];
//			$log.ex.debug("$scope.datas["+lineName+"]:",$scope.datas[lineName]);

			// Get the ID of the user.
//			$scope.userID = requestContext.getParam("userID");

			// ロード済みかのフラグ // I flag that data is being loaded.
//			$scope.isLoading = true;

			// 表示用のユーザーデータ // I am the user and the list of pets that are being viewed.
//			$scope.users = null;
//			$scope.pets = null;

			// The subview indicates which view is going to be rendered on the page.
//			$scope.subview = renderContext.getNextSection();

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Define Controller Methods.

			// リモートデータをローカルビューモデルに適用 //  I apply the remote data to the local view model.
			function applyRemoteData(users) {
				$scope.users = _.sortOnProperty(users, "name", "asc");
				return $scope.users;
			}

//			// サーバーからリモートデータをロード // I load the remote data from the server.
//			function loadRemoteData() {
//
//				var lineName = $scope.getActiveLineName();
//				$log.ex.debug("loadRemoteData/lineName:", lineName);
//
//				$scope.isLoading = true;
//
//				var promise = $q.all(
//					[
//						userService.getUsers(lineName)
//					]
//				);
//
//				promise.then(
//					function (response) {
//
//						$scope.isLoading = false;
//
//						applyRemoteData(response[ 0 ]);
//
//					},
//					function (response) {
//
//						// Userが何かの理由でロードできなかった時　// The user couldn't be loaded for some reason - possibly someone hacking with the URL.
//						$location.path($scope.$parent.fullPath);
//						// debug ------------------------------
//						$log.ex.debug("$scope.$parent.fullPath:", $scope.$parent.fullPath);
//						// debug ------------------------------
//					}
//				);
//
//			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Define Scope Methods.

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Bind To Scope Events.

			// I handle changes to the request context.
//			$scope.$on(
//				"requestContextChanged",
//				function () {
//
//					// Make sure this change is relevant to this controller.
//					if (!renderContext.isChangeRelevant()) {
//
//						return;
//
//					}
//
//					// Get the relevant route IDs.
//					$scope.userID = requestContext.getParam("userID");
//
//					// Update the view that is being rendered.
//					$scope.subview = renderContext.getNextSection();
//
//					// If the relevant IDs have changed, refresh the view.
//					if (requestContext.hasParamChanged("userID")) {
//
//						loadRemoteData();
//
//					}
//
//				}
//			);

			// I handle changes to the request context.
//			$scope.$on(
////				"requestContextChanged",
//				"$routeChangeSuccess",
//				function () {
//					$log.ex.debug("あれ?????:");
//					if ($scope.getActiveFaceName() !== "user" ||
//						$scope.$routeParams.faceId !== "user") {
//						$log.ex.debug("ロードはパス");
//						return;
//					}
//					loadRemoteData();
//				}
//			);

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initUserList = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initUserList()");
				// debug ------------------------------
				unitLoadHelper.setup($scope, "user", $scope.users, applyRemoteData, userService.getUsers);
			};

			// Set the interim title.
//			$scope.setWindowTitle("Loading Category");

			// Load the "remote" data.
//			if ($scope.getActiveFaceName() === "user") {
//				loadRemoteData();
//			}
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Item/Twitterリストリスト（ユニットリスト）・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "item.twitter.ListListCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log', '_', 'listService', 'unitLoadHelper',
		function ($scope, $log, _, listService, unitLoadHelper) {

			// debug ------------------------------
			$log.ex.init(controllerName);
			// debug ------------------------------

			// 表示用のユーザーデータ // I am the list and the list of pets that are being viewed.
			$scope.lists = null;

			$scope.getUnits = function(){
				return $scope.lists;
			};

			// リモートデータをローカルビューモデルに適用 //  I apply the remote data to the local view model.
			function applyRemoteData(lists) {
				$scope.lists = _.sortOnProperty(lists, "name", "asc");
				return $scope.lists;
			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initListList = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initListList()");
				// debug ------------------------------
				unitLoadHelper.setup($scope, "list", $scope.lists, applyRemoteData, listService.getLists);
			};
		}]
	);

})(angular, myApp);

/* ************************************************************ */

/** ************************************************************
 * Label/カテゴリーリスト（ユニットリスト）・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "label.CategoryListCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log', '_', 'categoryService', 'unitLoadHelper',
		function ($scope, $log, _, categoryService, unitLoadHelper) {

			// debug ------------------------------
			$log.ex.init(controllerName);
			// debug ------------------------------

			// 表示用のユーザーデータ // I am the category and the list of pets that are being viewed.
			$scope.categories = null;

			$scope.getUnits = function(){
				return $scope.categories;
			};

			// リモートデータをローカルビューモデルに適用 //  I apply the remote data to the local view model.
			function applyRemoteData(categories) {
				if ($scope.getActiveLineName() === 'trend') {
					$scope.categories = _.sortOnProperty(categories, "volume", "");
				}
				else {
					$scope.categories = _.sortOnProperty(categories, "index", "asc");
				}
				return $scope.categories;
			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initCategoryList = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initCategoryList()");
				// debug ------------------------------
				unitLoadHelper.setup($scope, "category", $scope.categories, applyRemoteData, categoryService.getCategories);
			};
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Label/タグリスト（ユニットリスト）・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "label.TagListCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log', '_', 'tagService', 'unitLoadHelper',
		function ($scope, $log, _, tagService, unitLoadHelper) {

			// debug ------------------------------
			$log.ex.init(controllerName);
			// debug ------------------------------

			// 表示用のユーザーデータ // I am the tag and the list of pets that are being viewed.
			$scope.tags = null;

			$scope.getUnits = function(){
				return $scope.tags;
			};

			// リモートデータをローカルビューモデルに適用 //  I apply the remote data to the local view model.
			function applyRemoteData(tags) {
				$scope.tags = _.sortOnProperty(tags, "name", "asc");
				return $scope.tags;
			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initTagList = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initTagList()");
				// debug ------------------------------
				unitLoadHelper.setup($scope, "tag", $scope.tags, applyRemoteData, tagService.getTags);
			};
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Label/フォルダリスト（ユニットリスト）・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "label.FolderListCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log', '_', 'folderService', 'unitLoadHelper',
		function ($scope, $log, _, folderService, unitLoadHelper) {

			// debug ------------------------------
			$log.ex.init(controllerName);
			// debug ------------------------------

			// 表示用のユーザーデータ // I am the folder and the list of pets that are being viewed.
			$scope.folders = null;

			$scope.getUnits = function(){
				return $scope.folders;
			};

			// リモートデータをローカルビューモデルに適用 //  I apply the remote data to the local view model.
			function applyRemoteData(folders) {
				$scope.folders = _.sortOnProperty(folders, "name", "asc");
				return $scope.folders;
			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initFolderList = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initFolderList()");
				// debug ------------------------------
				unitLoadHelper.setup($scope, "folder", $scope.folders, applyRemoteData, folderService.getFolders);
			};
		}]
	);

})(angular, myApp);

/* ************************************************************ */

/** ************************************************************
 * Repost/Hogeリスト（ユニットリスト）・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "repost.HogeListCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log',
		function ($scope, $log) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			// debug ------------------------------

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.init = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initUnit()");
				// debug ------------------------------
			};
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Repost/Piyoリスト（ユニットリスト）・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "repost.PiyoListCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log',
		function ($scope, $log) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			// debug ------------------------------

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.init = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initUnit()");
				// debug ------------------------------
			};
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Repost/Fugaリスト（ユニットリスト）・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "repost.FugaListCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log',
		function ($scope, $log) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			// debug ------------------------------

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.init = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initUnit()");
				// debug ------------------------------
			};
		}]
	);

})(angular, myApp);
/** ************************************************************
 * *********************************************************** */
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
// =============================================*
// -------------------------------------+

// Controller Methods.
// Scope Methods.
// Controller Variables.
// Scope Variables.
// Bind To Scope Events.
// Initialize.