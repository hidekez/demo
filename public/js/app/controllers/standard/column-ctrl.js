/** ************************************************************
 * カラム・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/03/31
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "standard.ColumnCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log', 'requestContext', 'renderHelper', 'uiActivateHelper',
		function ($scope, $log, requestContext, renderHelper, uiActivateHelper) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			$log.ex.debug("$location", $scope.$location);
			// debug ------------------------------

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Controller Variables.

			var context = $scope.consts.context.column,
				activateEventName = "columnChanged",
				initialized = false;

			// 戻り値でrenderContextを返しているが、現状は使ってない
			var renderContext = renderHelper.setup($scope, requestContext, context, $scope.getRenderEventId());

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Scope Variables.

//			$scope.subview // setup in renderHelper
			$scope.windowTitle = "tolist";
			$scope.controllerName = controllerName;// AngularJSデバッガ表示用
			$scope.templatePath = $scope.consts.templatePath.column;// ショートカット
			$scope.mainArray = $scope.mainTree.cols;
			$log.ex.debug("◇◇$scope.mainArray(=cols):", $scope.mainArray);
//			$scope.columnName = $scope.col.name;

//			$scope.$location = $location;
			$log.ex.debug("★★★$routeParams.columnId:", $scope.$routeParams.columnId);

//			$scope.currentPath = $scope.$routeParams.columnId;

			$scope.isOnRoute;
			$scope.changed;// undefinedならば初回

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 全般
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// ゲッター
			$scope.getColumn = function () {
				return $scope.col;
			};

			$scope.getColumnName = function () {
				return $scope.col.name;
			};

			// Activeじゃないのに注意。書き換えないこと。
			$scope.getCurrentPath = function(){
				return "/" + $scope.getColumnName();
			};

			/**
			 * どのレイヤースコープからでも呼び出せるように。
			 * カラム固有の上書きされないメソッドとして
			 * カラムはActiveなしからスタートなのに注意。
			 *
			 * @deprecated
			 *
			 * @returns {*}
			 */
			$scope.getActiveColumn = function () {
				return $scope.getActive($scope);
			};

			/**
			 * カラムはActiveなしからスタートなのに注意。
			 *
			 * @deprecated
			 *
			 * @returns {*}
			 */
			$scope.getActiveColumnName = function () {
				return $scope.getActiveName($scope);
			};

			// ↓これ、なんだ？
			$scope.getActiveFaceColumn = function () {
				return $scope.getActiveName($scope);
			};

			$scope.columnChanged = function () {
				return $scope.changed;
			};

			// debug ------------------------------
//			$log.ex.debug("scope.col:", $scope.col);
			// debug ------------------------------

			// TODO キャッシュ検討
			/**
			 * ルート対象のカラムか否か
			 * @returns {boolean}
			 */
			$scope.isOnRouteColumn = function () {
				var columnId = $scope.$routeParams.columnId;
				return (columnId && columnId === $scope.col.name);
				// && $scope.col.active
			};

			// =============================================*
			/**
			 * アクティブカラム変更
			 * 呼び出しはinit、もしくは$routeChangeSuccessによる
			 */
			$scope.changeActiveColumn = function () {

				var previous = $scope.getActive($scope),
					paramId = $scope.$routeParams.columnId,
					isOnRoute = true,
					parentChanged = false;

				$scope.isOnRoute = isOnRoute;

				if(initialized === false){
					previous = null;
				}

				uiActivateHelper.changeActive(
					$scope,
					previous,
					paramId,
					isOnRoute,
					parentChanged,
					activateEventName
				);
			};

//			$scope.setColumnPath = function () {
////				$scope.currentPath = activeColumn.name;
////				$scope.currentFullPath = $scope.rootPath($scope, $scope.currentPath);
////				$log.ex.debug("-------------------------$scope.rootPath():",$scope.currentFullPath);
//
////				$scope.currentPath = "/" + $scope.getColumnName();
//				$scope.parentPath = $scope.$parent.currentPath;
//				$scope.fullPath = $scope.getFullPath($scope);
//				$log.ex.debug("$scope.fullPath:", $scope.fullPath);
//			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Bind To Scope Events.
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//			/**
//			 * アクティブカラム変更に連結した処理
//			 */
//			$scope.$on(activateEventName, function () {
//				$log.ex.debug("on " + activateEventName);
//			$scope.lines = $scope.getActive($scope).lines;
//			});

			/**
			 * ルート($location)変更に連結した処理
			 */
			$scope.$on(
				"$routeChangeSuccess",
				function () {

					// debug ------------------------------
					$log.ex.debug("▼▼▼▼▼▼▼routeChangeSuccess");
					// debug ------------------------------

					$scope.changeActiveColumn();
				}
			);

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initColumn = function () {

				// debug ------------------------------
				$log.ex.debug("initColumn()");
//				$log.ex.debug("$scope.mainArray(=cols):", $scope.mainArray);
				// debug ------------------------------

				$scope.changeActiveColumn();
//				$scope.setColumnPath();
//				uiActivateHelper.setLocation($scope);// 初期状態でactiveなカラムがないので
				$scope.fullPath = $scope.getFullPath($scope);
				initialized = true;
			}

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
