/** ************************************************************
 * 標準ページ・基本レイアウト
 *
 * Author      : H.Kezuka
 * Created at  : 2013/03/31
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "layouts.StandardCtrl";

	//app-ctrlにて、$route, $routeParams, $location, consts, utilsは$scopeに代入済み
	app.controller(
		controllerName,
		['$scope', '$log', 'requestContext', 'renderHelper',
		function ($scope, $log, requestContext, renderHelper) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			$log.ex.debug("$location:", $scope.$location);
			// debug ------------------------------

//			$scope.$location = $location;
			$scope.controllerName = controllerName;// デバッグ用
			$scope.renderEventId = "standard";

			$scope.getRenderEventId = function () {
				return $scope.renderEventId;
			}
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Controller Variables.

			// 戻り値でrenderContextを返しているが、現状は使ってない
			var renderContext = renderHelper.setup($scope, requestContext, $scope.$route.current.context, $scope.getRenderEventId());

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
