/** ************************************************************
 * アプリ・コントローラー
 *
 * ng-appレベルで設定される、アプリの中心的なコントローラー。
 *
 * Author      : H.Kezuka
 * Created at  : 2013/03/29
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "AppCtrl";

	app.controller(
		controllerName,
		['$scope', '$route', '$routeParams', '$location', '$log', '$rootScope', '$http', '$resource', '$timeout', 'requestContext', 'renderHelper', '_', 'consts', 'utils',
		function ($scope, $route, $routeParams, $location, $log, $rootScope, $http, $resource, $timeout, requestContext, renderHelper, _, consts, utils) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			$log.ex.debug(controllerName + "#requestContext:", requestContext);
			$log.ex.debug("$location:", $location);
			// debug ------------------------------

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Controller Variables.
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			// =============================================*
			// 一時的な情報
			// =============================================*
			var tweetLines = [
				{name : "timeline", short : "tl", active : true},
				{name : "retweet", short : "rt", active : false},
				{name : "favorite", short : "fav", active : false}
			];
			var userLines = [
				{name : "following", short : "-ing", active : true},
				{name : "follower", short : "-er", active : false}
			];
			var listLines = [
				{name : "subscribed", short : "sub", active : true},
				{name : "member of", short : "mem", active : false},
				{name : "favorite", short : "fav", active : false}
			];
//			var hashtagLines = [
//				{name : "newel", short : "new", active : true},
//				{name : "trend", short : "trnd", active : false},
//				{name : "favorite", short : "fav", active : false}
//			];
			// -------------------------------------+
			var categoryLines = [
				{name : "general", short : "gene", active : true, mustLogin : false},
				{name : "news", short : "news", active : true, mustLogin : false},
				{name : "trend", short : "trnd", active : false, mustLogin : false},
				{name : "favorite", short : "fav", active : false, mustLogin : true}
			];
			var tagLines = [
				{name : "my", short : "my", active : true},
				{name : "trend", short : "trnd", active : false},
				{name : "favorite", short : "fav", active : false}
//				{name : "edit", short : "edit", active : false}
			];
			var folderLines = [
				{name : "my", short : "my", active : true},
				{name : "friends", short : "frnd", active : false},
				{name : "favorite", short : "fav", active : false}
			];
			// -------------------------------------+
//			var hogeLines = [
//				{name : "hoge", short : "hg", active : true},
//				{name : "piyo", short : "py", active : false},
//				{name : "fuga", short : "fg", active : false}
//			];
			// -------------------------------------+
			var items = [
				{name : "tweet", lines : tweetLines, active : true, mustLogin : true},
				{name : "user", lines : userLines, active : false, mustLogin : true},
				{name : "list", lines : listLines, active : false, mustLogin : true}
//				{name : "hashtag", lines : hashtagLines, active : false, mustLogin:true}
			];
			var labels = [
				{name : "category", lines : categoryLines, active : true, mustLogin : false},
				{name : "tag", lines : tagLines, active : false, mustLogin : true},
				{name : "folder", lines : folderLines, active : false, mustLogin : true}
			];
			var reposts = [
				{name : "not selected", lines : null, active : true, mustLogin : false}
//				{name : "piyo", lines : hogeLines, active : false},
//				{name : "fuga", lines : hogeLines, active : false}
			];
			// -------------------------------------+
			var cols = [
				{name : "item", faces : items, active : false},
				{name : "label", faces : labels, active : false},
				{name : "repost", faces : reposts, active : false}
			];

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Scope Variables.
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			$scope.mainTree = {
				cols : cols
			};

			$scope.mainData = [];

			for (var col in cols) {
//				$log.ex.debug("col:", col, "cols[col].name:", cols[col].name);
				var col = cols[col];
				$scope.mainData[col.name] = [];//$scope.mainData[col.name] || [];

				var faces = col.faces;

				for (var face in faces) {
//					$log.ex.debug("face:", face, "faces[face].name:", faces[face].name);
					var face = faces[face];
					$scope.mainData[col.name][face.name] = [];//$scope.mainData[col.name][face.name] || [];

					var lines = face.lines;

					for (var line in lines) {
//						$log.ex.debug("line:", line, "lines[line].name:", lines[line].name);
						var line = lines[line];
						$scope.mainData[col.name][face.name][line.name] = [];//$scope.mainData[col.name][face.name][line.name] || [];
					}
				}
			}
			// debug ------------------------------
			$log.ex.debug("mainData:", $scope.mainData);
			// debug ------------------------------

			// -------------------------------------+
//			$scope.panes = [
//				{title : "item", content : "hoge", active : true},
//				{title : "label", content : "piyo", active : false},
//				{title : "repost", content : "fuga", active : false}
//			];

			// debug ------------------------------
			$log.ex.debug("tweetLines:", $scope.mainTree.cols[0].faces[0].lines);
			// debug ------------------------------

			$scope.controllerName = controllerName;// デバッグ用
			$scope.$location = $location;
			$scope.$route = $route;
			$scope.$routeParams = $routeParams;
			$scope.consts = consts;
			$scope.utils = utils;
			$scope.windowTitle = "tolist";

			$scope.appRootPath = consts.appRootPath;
			$scope.currentPath = consts.appRootPath;
			$scope.parentPath = "";
			$scope.fullPath = consts.appRootPath;

			$scope.config = {};
			$scope.config.uid = null;
			$scope.config.isDev = null;
			$scope.config.isLocal = null;
			$scope.config.isLogin = null;

			// コピーライト表示用の本年度の取得 // Get the current year for copyright output.
			$scope.copyrightYear = ( new Date() ).getFullYear();

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Controller Methods
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//			// Routing
//			/**
//			 * 与えられたルートが有効なルートであるか、（パターンと一致するように
//			 * 失敗したため）デフォルトルートにリダイレクトされているかを確認。// I check to see if the given route is a valid route; or, is the route being re-directed to the default route (due to failure to match pattern).
//			 * @param route
//			 * @return {boolean}
//			 */
//			function isRouteRedirect(route) {
//				$log.ex.debug("route:",route);
//				// アクションがない場合は、未知のルートから既知のルートへのリダイレクト。// If there is no action, then the route is redirection from an unknown route to a known route.
//				return( !route.current.action );
//			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Scope Methods.
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			$scope.getMainTree = function () {
				return $scope.mainTree;
			};

			// -------------------------------------+
			$scope.getMainData = function () {
				return $scope.mainData;
			};

			// -------------------------------------+
			$scope.getColumnData = function (colName) {
				return $scope.mainData[colName];
			};

			$scope.setColumnData = function (colName, data) {
				ng.copy(data, $scope.mainData[colName]);
			};

			// -------------------------------------+
			$scope.getFaceData = function (faceName) {
				for (var col in cols) {
					col = cols[col];
//					$log.ex.debug("col:",col);
					if ($scope.mainData[col.name][faceName]) {
						return $scope.mainData[col.name][faceName];
					}
				}
			};

			$scope.setFaceData = function (faceName, data) {
				for (var col in cols) {
					col = cols[col];
//					$log.ex.debug("col:",col);
					if ($scope.mainData[col.name][faceName]) {
						ng.copy(data, $scope.mainData[col.name][faceName]);
						return;
					}
				}
			};

			// debug ------------------------------
			$log.ex.debug("$scope.getFaceData('tweet'):", $scope.getFaceData('tweet'));
			// debug ------------------------------

			// -------------------------------------+
			$scope.getLineData = function (colName, faceName, lineName) {
				if ($scope.mainData[colName][faceName][lineName]) {
					return $scope.mainData[colName][faceName][lineName];
				}
			};

			$scope.setLineData = function (colName, faceName, lineName, data) {
				if ($scope.mainData[colName][faceName][lineName]) {
					ng.copy(data, $scope.mainData[colName][faceName][lineName]);
				}
			}

			// debug ------------------------------
//			$log.ex.debug("$scope.getLineData('retweet'):", $scope.getLineData('retweet'));
//
//			var rt = $scope.getLineData('retweet');
//			$log.ex.debug("rt:", rt);
//			rt = [1, 2, 3];
//			$log.ex.debug("rt:", rt);
//			$log.ex.debug("$scope.getLineData('retweet'):", $scope.getLineData('retweet'));
//			$scope.setLineData('retweet', rt);
//			$log.ex.debug("$scope.setLineData -> $scope.getLineData('retweet'):", $scope.getLineData('retweet'));
//			$scope.setLineData('retweet', []);
			// debug ------------------------------

			// Routing
			/**
			 * インスタンスが作成された時刻として、現在時刻を取得。
			 * コントローラがインスタンス化された時刻とデータの再移入との違いがわかる。// I get the current time for use when display the time a controller was rendered. This way, we can see the difference between when a controller was instantiated and when it was re-populated with data.
			 * @return {*} TODO returnを確認して記述
			 */
			$scope.getInstanceTime = function () {

				var now = new Date();
				var timeString = now.toTimeString();
				var instanceTime = timeString.match(/\d+:\d+:\d+/i);
				// debug ------------------------------
				$log.debug("instanceTime:", instanceTime);
				// debug ------------------------------
				return( instanceTime[ 0 ] );
			};

			// ???
			/**
			 * TODO: Flesh this out - for now, just trying to create a wrapper for alert().
			 * @param modalType
			 */
			$scope.openModalWindow = function (modalType) {
				alert(arguments[ 1 ] || "Opps: Something went wrong.");
			};

			// View
			/**
			 * タイトルタグの更新// I update the title tag.
			 * @param title
			 */
			$scope.setWindowTitle = function (title) {
				$scope.windowTitle = title;
			};

			$scope.getCurrentScope = function () {
				return $scope;
			};

			// =============================================*
			// 配列のアクティベート
			// =============================================*
			// utilsに渡しているプロパティ名
			// 冗長だが、後々にデータ作成側でプロパティ名を変更した際に
			// 修正しやすいように分離してある。
			$scope.propNames = {
				naming : "name",
				active : "active"
			};

			$scope.mainArray = [];

			/**
			 * @see utils#getActive
			 */
			$scope.getActive = function (scope) {
				return utils.getActive(scope.mainArray, scope.propNames.active)
			};

			/**
			 * @see utils#getActiveName
			 */
			$scope.getActiveName = function (scope) {
				return utils.getActiveName(scope.mainArray, [scope.propNames.active, scope.propNames.naming])
			};

			/**
			 * @see utils#getCurrentIndex
			 */
			$scope.getCurrentIndex = function (scope) {
				return utils.getCurrentIndex(scope.mainArray, scope.propNames.active)
			};

//			// debug ------------------------------
////			$log.ex.debug("getActive:", _.filter(items, function (data) {
////				return data.active
////			}));
//			$log.ex.debug("AppCtrl#getActive      :", $scope.getActive(items));
//			$log.ex.debug("AppCtrl#getCurrentIndex:", $scope.getCurrentIndex(items));
//			// debug ------------------------------

			// =============================================*

			/**
			 * @see utils#find
			 */
			$scope.find = function (scope, key, value) {
				return  utils.find(scope.mainArray, key, value)
			};

			$scope.findByName = function (scope, value) {
				return  utils.find(scope.mainArray, scope.propNames.naming, value)
			};

			// =============================================*

			/**
			 * @see utils#getPrevIndex
			 */
			$scope.getPrev = function (scope) {
				var index = utils.getPrevIndex(
					scope.mainArray.length, $scope.getCurrentIndex(scope)
				);
				return scope.mainArray[index];
			};

			/**
			 * @see utils#getNextIndex
			 */
			$scope.getNext = function (scope) {
				var index = utils.getNextIndex(
					scope.mainArray.length, $scope.getCurrentIndex(scope)
				);
				return scope.mainArray[index];
			};

			// =============================================*

			/**
			 * @see utils#activate
			 */
			$scope.activate = function (scope, target) {
				return utils.activate(
					scope.mainArray,
					target,
					[
						scope.propNames.naming,
						scope.propNames.active
					])
			};

			/**
			 * @see utils#activateByName
			 */
			$scope.activateByName = function (scope, targetName) {
				return utils.activateByName(
					scope.mainArray,
					targetName,
					[
						scope.propNames.naming,
						scope.propNames.active
					])
			};

			// =============================================*
			// リダイレクト
			// =============================================*
			/**
			 * 指定パス、もしくはアプリルートへのリダイレクト
			 * parentRouteは途中までは選択肢が定型だが、どのようなルートをリクエスト
			 * するかは状況によるので、リクエストする側で生成して送ることにしている。
			 * アプリルート以外のリダイレクトは $location.path を直接使うこと。
			 *
			 * @param parentRoute
			 */
			$scope.redirectTo = function (parentRoute) {
				var url = ng.isUndefined(parentRoute)
					? consts.appRootPath
					: parentRoute;
				$log.ex.debug("parentRoute:", parentRoute);
				$log.ex.debug("url        :", url);
				$location.path(url);
			};

			$scope.getFullPath = function (scope) {
//				return scope.$parent.fullPath + scope.currentPath;
				return scope.$parent.fullPath + scope.getCurrentPath();
			};

			// 再帰的に　←無駄なのでなし
//			$scope.rootPath = function(scope, path){
//				if(scope.$parent){
//					if(scope.$parent.currentPath){
//						path = scope.$parent.currentPath + "/" + path;
//					}
//					scope.rootPath(scope.$parent, path);
//				}
//				return path;
//			}

//			$scope.getRootPath = function(scope){
//				if(scope.rootPath){
//					return scope.rootPath;
//				}
//				else{
//
//				}
//			}

//			$scope.getFullPath = function (scope) {
//				var parentPath
//				if (scope.$parent) {
//					if (scope.$parent.hasOwnProperty("getFullPath")) {
//						parentPath = scope.$parent.getFullPath();
//					}
//					else {
//						parentPath = scope.$parent.currentPath;
//					}
//				}
//				return parentPath + "/" + scope.currentPath;
//			}
//			$scope.level = function(scope, count){
//				if(scope.$parent.hasOwnProperty("point")){
//					count += scope.$parent.point;
//					scope.$parent.level(scope.$parent, count);
//				}
//				return count;
//			}

			// =============================================*
			// レンダリングイベント用フラグ・セッター
			// =============================================*
//			$scope.setDoRender = function(level){
//			}

			// =============================================*
			// コンフィグ情報
			// =============================================*
			$scope.getConfig = function () {
				return $scope.config;
			};
			$scope.getUID = function () {
				return $scope.config.uid;
			};
			$scope.isDev = function () {
				return $scope.config.isDev;
			};
			$scope.isLocal = function () {
				return $scope.config.isLocal;
			};
			$scope.isLogin = function () {
				return $scope.config.isLogin;
			};
			$scope.login = function () {
				$http.get("/login");
			};
			$scope.logout = function () {
				$http.get("/logout");
			};

			$scope.checkLogin = function(data){
				if(data && data.mustLogin){
					return $scope.isLogin();
				}
				else{
					return true
				}
			}
//			var Echo = $resource("/echo");
//
//			$scope.getEcho = function () {
//				$scope.echoResult = Echo.get();
//				$log.ex.debug("$scope.echoResult:", $scope.echoResult);
//				$scope.setLogin();
//			};
//
//			$scope.setLogin = function () {
//				$scope.login = !!$scope.echoResult.login;
//				$log.ex.debug("$scope.login:", $scope.login);
//			};

//			$scope.$watch("$scope.echo", function () {
//				$log.ex.debug("watching!!▲▲▲▲▲▲▲▲▲▲");
//				$log.ex.debug("$scope.echo:",$scope.echo);
//				$log.ex.debug("$scope.echo[1]:",$scope.echo[1]);
//				$scope.setLogin();
////				if($scope.echo.login){
////				}
//			});

			// nav.html/ng-showで使用している
			var doneEcho = false;
			$scope.doneEcho = function () {
				return doneEcho;
			};

			$scope.getEcho = function () {
				$http.get("/echo").success(function (result) {
					// debug ------------------------------
//					alert("getEcho!!");
					$log.ex.debug("$http.success.result:", result);
					$log.ex.debug("$http.success.result.login:", result.login);
					// debug ------------------------------

					$scope.config.uid = result.uid;
					$scope.config.isDev = result.dev;
					$scope.config.isLocal = result.loc;
					$scope.config.isLogin = result.login;

					doneEcho = true;
					// debug ------------------------------
					$log.ex.debug("$scope.config:", $scope.config);
					// debug ------------------------------
				});
			};

			var pingEchoInstance;
			$scope.startPing = function(){
				pingEchoInstance = $timeout(function(){
						$log.ex.debug("ping!");
						$log.ex.debug("echoTimeout", consts.echoTimeout);
						$scope.getEcho();
						$scope.startPing();
					},
				consts.echoTimeout);
			};
			$scope.stopPing = function(){
				$log.ex.debug("ping stop");
				$timeout.cancel(pingEchoInstance);
			};

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Bind To Scope Events.
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 戻り値でrenderContextを返しているが、現状は使ってない
			var renderContext = renderHelper.setup($scope, requestContext, $scope.consts.context.app);

			// Routing
			/**
			 * リクエスト・コンテキストへの変更を処理。// I handle changes to the request context.
			 */
//			requestContext.setOnRequestContextChanged($scope, _renderContext);

			// Routing
			/**
			 * リクエスト・コンテキストのchangeイベントをトリガーできるようにルート変更を監視する。// Listen for route changes so that we can trigger request-context change events.
			 */
			requestContext.setOnRouteChangeSuccess($scope, $route, $routeParams);

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Initialize.
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initApp = function () {
				$scope.getEcho();
				$scope.startPing();
			};

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 実験部分

			// モジュール経由の関数を直接テンプレートからは呼べない
//			utils.hoge();
//			utils.piyo("hello piyo");

//			$scope.hoge = utils.hoge;//インターフェースをつなぐ必要あり

		}]
	);

})(angular, myApp);

/** ************************************************************
 * *********************************************************** */
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
// =============================================*
// -------------------------------------+
