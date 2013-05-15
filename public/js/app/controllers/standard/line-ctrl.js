/** ************************************************************
 * ライン・コントローラー
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/05
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "standard.LineCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log', '$rootScope', 'requestContext', 'renderHelper', 'uiActivateHelper',
		function ($scope, $log, $rootScope, requestContext, renderHelper, uiActivateHelper) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			// debug ------------------------------

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Controller Variables.

			var context = $scope.consts.context.line,
				activateEventName = "lineChanged";

			var renderContext = renderHelper.setup($scope, requestContext, context, $scope.getRenderEventId());

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Scope Variables.

//			$scope.subview // setup in renderHelper
			$scope.windowTitle = "tolist";
			$scope.controllerName = controllerName;// AngularJSデバッガ表示用
			$scope.templatePath = $scope.consts.templatePath.line;
//			$scope.lineId = $scope.$routeParams.lineId;// 固定になってしまうのでNG
			$log.ex.debug("★★★$routeParams.lineId:", $scope.$routeParams.lineId);

			$scope.mainArray = $scope.lines;//ボタンから受け取るものとは別系統（別管理）
			$log.ex.debug("□□$scope.mainArray(=lines):", $scope.mainArray);

//			$scope.activeLine = $scope.getActive($scope);//changeActiveLineで入れる
//			$log.ex.debug("$scope.activeLine.name:",$scope.activeLine.name);
//			$scope.prev = {};
//			$scope.next = {};

			$scope.isOnRoute;
			$scope.changed;

//			$scope.parentUrl =
//				"/" + $scope.$routeParams.columnId + "/" + $scope.$routeParams.faceId;

//			$scope.state = {
//				name : "",
//				data : {}
//			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 全般
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// ゲッター
			$scope.getLine = function () {
				return $scope.line;//この変数は設定されていない
			};
			$scope.setLine = function (line) {
				$scope.line = line;//Headerのrepeatから受け取り
			};

//			$scope.getLineName = function () {
//				return $scope.line.name;//この変数は設定されていない
//			};

			// カラムではActiveを使ってないため切り出している。
			$scope.getCurrentPath = function(){
//				return "/" + $scope.getLineName();
				return "/" + $scope.getActiveLineName();
			}

			// どのレイヤースコープからでも呼び出せるように。
			// ライン固有の上書きされないメソッドとして
			$scope.getActiveLine = function () {
				return $scope.getActive($scope);
			};

			$scope.getActiveLineName = function () {
				return $scope.getActiveName($scope);
			};

			$scope.isOnRouteLine = function () {
				return $scope.isOnRoute;
			};

			$scope.lineChanged = function () {
				return $scope.changed;
			};

			$scope.getItemTmpl = function(){
				return ($scope.consts.templatePath.list.item)
			}
			$scope.getLabelTmpl = function(){
				return ($scope.consts.templatePath.list.label)
			}
			$scope.getRepostTmpl = function(){
				return ($scope.consts.templatePath.list.repost)
			};

//			$scope.unitListCtrl;
//			$scope.unitListTmpl;

//			$scope.getUnitListCtrl = function () {
////				var name = $scope.getColumnName(),
////					face = $scope.activeFaceName();
////				face =face.substring(0, 1).toUpperCase()
////					+ face.slice(1).toLowerCase();
////				if(name=="item"){
////					name += ".twitter";
////				}
////				name += "." + face + "ListCtrl";
////
//////				$scope.unitListCtrl = name;
//////				$log.ex.debug("$scope.unitListCtrl:",$scope.unitListCtrl);
////
////				return name;
////				alert("hello");
//			};

			$scope.getUnitListTmpl = function () {
//				var face = $scope.activeFaceName();
//				return $scope.consts.templatePath[face];
//				var str = $scope.getColumnName()+"."+$scope.activeFaceName();
//				$log.ex.debug("str:",str);
//				return $scope.consts.templatePath[str];
//				return $scope.consts.templatePath[$scope.getColumnName()][$scope.activeFaceName()];
//				if(!column){
//				}
				var column = $scope.getColumnName();
				return $scope.consts.templatePath["list"][column];
			};

			$scope.getUnitTmpl = function (face) {
				return $scope.consts.templatePath[$scope.getColumnName()][face];
			};

//			$scope.initUnitList = function(){
//				$controller.constructor()
//			}
//			$scope.checkUnitListCtrl = function(){
////				$log.ex.debug("$controller:",$controller);
//				$log.ex.debug("$controllerProvider:",$controllerProvider);
//			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// ヘッダー部分
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// ヘッダー・ボタングループ部分
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			// =============================================*
			// アクティベート

			// 指定のモデルがアクティブか否か
			// 現在のステートとの比較した結果を返している
			$scope.isActive = function (line) {
//				return $scope.state.name == line.name;
//				return $scope.activeLine.name === line.name;
				return ($scope.getActiveName($scope) === line.name)
			};

			// ボタンクリック時のイベント
			// A要素ではデザインが崩れるので
			$scope.activateLine = function (line) {
//				$scope.state.name = line.name;
				// debug ------------------------------
				$log.ex.debug("ONCLICKKKKKKKKKKKKKKK:line.name", line.name);
				$log.ex.debug("$scope.$id:", $scope.$id);
//				$log.ex.debug("$scope.mainArray:", $scope.mainArray);
//				$scope.activate($scope, line);
				$log.ex.debug("$scope.fullPath", $scope.fullPath);
				$log.ex.debug("$scope.parentPath", $scope.parentPath);
				// debug ------------------------------

				$scope.$location.path($scope.parentPath + "/" + line.name);
//				$scope.redirectTo();
			};

			// =============================================*
			/**
			 * アクティブライン変更
			 * 呼び出しはinit、もしくは$routeChangeSuccessによる
			 */
			$scope.changeActiveLine = function () {
				var previous = $scope.getActive($scope),
					paramId = $scope.$routeParams.lineId,
					isOnRoute = $scope.isOnRouteFace(),
					parentChanged = $scope.faceChanged();

//				$scope.changed = false;
				$scope.isOnRoute = isOnRoute;
				$scope.mainArray = $scope.lines;//ボタンから受け取るものとは別系統（別管理）

				uiActivateHelper.changeActive(
					$scope,
					previous,
					paramId,
					isOnRoute,
					parentChanged,
					activateEventName
				);
			};

			/**
			 * ヘッダーリストを表示するか否か
			 * @returns {boolean}
			 */
			$scope.showLineHeader = function(){
				var flag = true;
				var face = $scope.getActiveFace();
				if(face.mustLogin){
					flag = $scope.isLogin();
				}
				return (face.lines && flag)
			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Bind To Scope Events.
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			/**
			 * アクティブライン変更に連結した処理
			 */
			$scope.$on(activateEventName, function () {
				$log.ex.debug("on " + activateEventName);
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

					$scope.changeActiveLine();
				}
			);

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// 初期化処理
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			$scope.initLine = function () {

				if(!$scope.lines){
					return;
				}

				$log.ex.debug("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
				$log.ex.debug("initLine()");
				$log.ex.debug("$scope.mainArray(=lines):", $scope.mainArray);

//				$scope.state.name = $scope.getActiveName($scope);
//				$scope.setLinePath();
				$scope.changeActiveLine();

				// debug ------------------------------
				$log.ex.debug("initLine/$scope.getActiveLineName:", $scope.getActiveLineName());
//				$log.ex.debug("initLine/$scope.state.name:", $scope.state.name);
//				$log.ex.debug("initLine/$scope.lines:", $scope.lines);
				// debug ------------------------------
			};


//			$scope.sayHi = function(){
//				alert("hi," + $scope.getColumnName() + "," + $scope.$id);
//			};

//			return $scope[controllerName] = this;

		}]
	);

})(angular, myApp);

//(function (ng, app) {
//
//	app.directive("unitListView", function () {
//
//		return {
//			restrict : "E",
//			scope:{
//				column:"=",
////				col:"@",
////				ctrlcol:"@"
//				act:"@"
////				getColName:"&"
//			},
//			transclude:true,
//			template:'<div>directive: {{$id}}</div>',
//			link     : function (scope) {
////				scope.column = attrs.column;
//				scope.scopeName = "unitListView";
//				console.log("scope.column",scope.column);
////				console.log("scope.ctrlcol",scope.ctrlcol);
////				console.log("scope.col:",scope.col);
////				console.log("scope.getColName",scope.getColName());
////				console.log(scope);
////				console.log(scope.getUnitListTmpl());
////				console.log("scope.getColumnName():",scope.getColumnName());
//			}
//		}
//	});
//
//})(angular, myApp);

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