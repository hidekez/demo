/** ************************************************************
 * フェイス・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/03/31
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "standard.FaceCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log', 'requestContext', 'renderHelper', 'uiActivateHelper',
		function ($scope, $log, requestContext, renderHelper, uiActivateHelper) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			// debug ------------------------------

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Controller Variables.

			var context = $scope.consts.context.face,
				activateEventName = "faceChanged",
				initialized = false;

			var renderContext = renderHelper.setup($scope, requestContext, context, $scope.getRenderEventId());

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Scope Variables.

//			$scope.subview // setup in renderHelper
			$scope.windowTitle = "tolist";
			$scope.controllerName = controllerName;// AngularJSデバッガ表示用
			$scope.templatePath = $scope.consts.templatePath.face;// ショートカット
//			$scope.faceId = $scope.$routeParams.faceId;// 固定になってしまうのでNG
			$log.ex.debug("★★★$routeParams.faceId:", $scope.$routeParams.faceId);

			$scope.mainArray = $scope.faces;
			$log.ex.debug("□□$scope.mainArray(=faces):", $scope.mainArray);
			$scope.prev = {};
			$scope.next = {};
			$scope.isOnRoute;
			$scope.changed;// undefinedならば初回

//			$scope.parentUrl ="/" + $scope.$routeParams.columnId;
//			$scope.currentPath = "";//init内でセット
//			var path = $scope.currentPath;
//			path = $scope.rootPath($scope, path)
//			$log.ex.debug("-------------------------$scope.rootPath():",path);

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 全般
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// ゲッター
//			$scope.getFace = function () {
//				return $scope.face;//この変数は設定されていない
//			};
//
//			$scope.getFaceName = function () {
//				return $scope.face.name;//この変数は設定されていない
//			};

			// カラムではActiveを使ってないため切り出している。
			$scope.getCurrentPath = function(){
//				return "/" + $scope.getFaceName();
				return "/" + $scope.getActiveFaceName();
			}

			// どのレイヤースコープからでも呼び出せるように。
			// フェース固有の上書きされないメソッドとして
			$scope.getActiveFace = function () {
				return $scope.getActive($scope);
			};

			$scope.getActiveFaceName = function () {
				return $scope.getActiveName($scope);
			};

			$scope.isOnRouteFace = function () {
				return $scope.isOnRoute;
			};

			$scope.faceChanged = function () {
				return $scope.changed;
			};

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// ヘッダー部分
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			// =============================================*
			$scope.setPrevFace = function () {
				$log.ex.debug("setPrevFace");
				$scope.prev = ($scope.getPrev($scope));
			};

			$scope.setNextFace = function () {
				$log.ex.debug("setNextFace");
				$scope.next = ($scope.getNext($scope));
			};

			// =============================================*
			// プロパティの隠蔽として
			// TODO getActiveName?
			var namingProp = $scope.propNames.naming;

			$scope.getPrevFaceName = function () {
				return $scope.prev[namingProp]
			};

			$scope.getNextFaceName = function () {
				return $scope.next[namingProp]
			};

//			$scope.activeFaceName = function () {
//				return $scope.activeFace[namingProp]
//			};

			// =============================================*
			/**
			 * アクティブフェース変更
			 * 呼び出しはinit、もしくは$routeChangeSuccessによる
			 */
			$scope.changeActiveFace = function () {

				var previous = $scope.getActive($scope),
					paramId = $scope.$routeParams.faceId,
					isOnRoute = $scope.isOnRouteColumn(),
					parentChanged = $scope.columnChanged();

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

			$scope.getUnitListCtrl = function(){
				var name = $scope.getColumnName(),
					face = $scope.getActiveFaceName();

				face =face.substring(0, 1).toUpperCase()
					+ face.slice(1).toLowerCase();
				if(name=="item"){
					name += ".twitter";
				}
				name += "." + face + "ListCtrl";

//				$scope.unitListCtrl = name;
//				$log.ex.debug("$scope.unitListCtrl:",$scope.unitListCtrl);

				return name;
			};
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Bind To Scope Events.
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			/**
			 * アクティブフェース変更に連結した処理
			 */
			$scope.$on(activateEventName, function () {
				$log.ex.debug("on " + activateEventName);
				$scope.setPrevFace();
				$scope.setNextFace();
				$scope.lines = $scope.getActive($scope).lines;
			});

			/**
			 * ルート($location)変更に連結した処理
			 */
			$scope.$on(
				"$routeChangeSuccess",
				function (event) {

					// debug ------------------------------
					$log.ex.debug("△△△△△△routeChangeSuccess");
					// debug ------------------------------

					$scope.changeActiveFace();
				}
			);

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initFace = function () {

				// debug ------------------------------
				$log.ex.debug("initFace()");
				$log.ex.debug("$scope.mainArray(=faces):", $scope.mainArray);
				// debug ------------------------------

				$scope.changeActiveFace();
				initialized = true;

				// debug ------------------------------
				$log.ex.debug("initFace/$scope.getActiveFaceName:", $scope.getActiveFaceName());
				$log.ex.debug("initFace/$scope.lines:", $scope.lines);
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
