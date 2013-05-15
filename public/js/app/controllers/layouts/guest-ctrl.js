/** ************************************************************
 * ゲストページ・基本レイアウト
 *
 * Author      : H.Kezuka
 * Created at  : 2013/03/31
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var controllerName = "layouts.GuestCtrl";

	app.controller(
		controllerName,
		['$scope', '$log', 'requestContext', 'renderHelper', 'consts',
		function ($scope, $log, requestContext, renderHelper, consts) {

			// debug ------------------------------
			//setLabel(controllerName) and debug(controllerName)
			$log.ex.init(controllerName);
			// debug ------------------------------

			$scope.renderEventId = "guest";

			$scope.getRenderEventId = function () {
				return $scope.renderEventId;
			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Controller Variables.

			var renderContext = renderHelper.setup($scope, requestContext, "guest", $scope.getRenderEventId());


			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Scope Variables.

//			$scope.subview // setup in renderHelper

			// テンプレート・パス
			$scope.welcomeTemplate = consts.templatePath.welcome;

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
