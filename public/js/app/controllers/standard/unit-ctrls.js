/** ************************************************************
 * ユニット・コントローラー集
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */

/** ************************************************************
 * Item/Twitterつぶやきユニット・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "item.twitter.TweetUnitCtrl";

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
				$log.ex.debug("init()");
				// debug ------------------------------
			};
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Item/Twitterユーザーユニット・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "item.twitter.UserUnitCtrl";

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
				$log.ex.debug("init()");
				// debug ------------------------------
			};
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Item/Twitterリストユニット・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "item.twitter.ListUnitCtrl";

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
				$log.ex.debug("init()");
				// debug ------------------------------
			};
		}]
	);

})(angular, myApp);

/* ************************************************************ */

/** ************************************************************
 * Label/カテゴリーユニット・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "label.CategoryUnitCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log',
		function ($scope, $log) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			// debug ------------------------------

			$scope.favoriteCategory = function (event, category) {
				$scope.utils.toggle(category, "favorite");
				//TODO サーバーとの連携

				$log.ex.debug("event:",event);
				event.stopPropagation();
			};

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.init = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("init()");
				// debug ------------------------------
			};
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Label/タグユニット・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "label.TagUnitCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log',
		function ($scope, $log) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			// debug ------------------------------

			//TODO サーバーとの連携
			$scope.editing = false;

			$scope.editTag = function (id) {
				$scope.editing = true;
			};
			$scope.doneEditTag = function($event){
//				alert("done");
				$scope.editing = false;
				$event.preventDefault();
			};
			$scope.deleteTag = function (id) {
				alert(id);
			};
			$scope.favoriteTag = function (tag) {
				$scope.utils.toggle(tag, "favorite");
			};
			$scope.isMy = false;

			function isMy(){
				return ($scope.getActiveLineName() === 'my');
			}
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initTagUnit = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initTagUnit()");
				// debug ------------------------------
				$scope.isMy = isMy();
			};
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Label/フォルダユニット・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "label.FolderUnitCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log',
		function ($scope, $log) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			// debug ------------------------------

			$scope.editing = false;

			$scope.editFolder = function (id) {
				$scope.editing = true;
			};
			$scope.doneEditFolder = function($event){
//				alert("done");
				$scope.editing = false;
				$event.preventDefault();
			};
			$scope.deleteFolder = function (id) {
				alert(id);
			};
			$scope.favoriteFolder = function (folder) {
				$scope.utils.toggle(folder, "favorite");
				//TODO サーバーとの連携
			};

			$scope.isMy = false;
			$scope.isNotFriends = false;
			function isMy(){
				return ($scope.getActiveLineName() === 'my');
			}
			function isNotFriends(){
				return ($scope.getActiveLineName() !== 'friends');
			}
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initFolderUnit = function () {
				// debug ------------------------------
				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initFolderUnit()");
				// debug ------------------------------

				$scope.isMy = isMy();
				$scope.isNotFriends = isNotFriends();
			};
		}]
	);

})(angular, myApp);

/* ************************************************************ */

/** ************************************************************
 * Repost/hogeユニット・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "repost.HogeUnitCtrl";

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
				$log.ex.debug("init()");
				// debug ------------------------------
			};
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Repost/piyoユニット・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "repost.PiyoUnitCtrl";

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
				$log.ex.debug("init()");
				// debug ------------------------------
			};
		}]
	);

})(angular, myApp);

/** ************************************************************
 * Repost/fugaユニット・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "repost.FugaUnitCtrl";

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
				$log.ex.debug("init()");
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