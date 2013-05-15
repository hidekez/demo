/** ************************************************************
 * ＵＩアクティベートヘルパ
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/08
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var serviceName = "uiActivateHelper";

	app.service(
		serviceName,
		['$log',
		function ($log) {

			// =============================================*
			/**
			 * アクティブ変更
			 * 呼び出しはinit、もしくは$routeChangeSuccessによる
			 *
			 * @param {*} scope
			 * @param {*} previous
			 * @param {string} paramId
			 * @param {boolean} isOnRoute
			 * @param {boolean} parentChanged
			 * @return {boolean} changed
			 */
			function changeActive(scope, previous, paramId, isOnRoute, parentChanged, eventName) {

				var active,
					current = scope.getActive(scope);

				function getPrevName() {
					return (previous) ? previous.name : "";
				}

				function debugPrintSkipped() {
					$log.ex.debug(getPrevName() + " skipped");
				}

				function debugPrintPrevName() {
					$log.ex.debug("previous.name:", getPrevName());
				}

				function redirect() {
					$log.ex.info("URL has dirty path -> redirectTo");
					scope.redirectTo(scope.$parent.fullPath);
				}

				// debug ------------------------------
				$log.ex.debug("＋＋＋＋＋＋＋＋＋　changeActive　＋＋＋＋＋＋＋＋");
				$log.ex.debug("previous      :", getPrevName());
				$log.ex.debug("paramId       :", paramId);
				$log.ex.debug("isOnRoute     :", isOnRoute);
				$log.ex.debug("parentChanged :", parentChanged);
				// debug ------------------------------

				scope.changed = false;

				if (parentChanged) {
					previous = null;
				}

				// ルートにパス指定がなく、アクティブデータもまだない場合
				if (!paramId && !previous) {
					active = current;
				}

				// ルートにパス指定がなく、アクティブデータはある場合
				else if (!paramId && previous) {
					debugPrintSkipped();// デバッグ
					return;
				}

				// ルートにパス指定があり、アクティブデータがまだない場合
				else if (paramId && !previous) {

					// 親がルートに乗っている場合
					if (isOnRoute) {

						// location中のparamIdから取得してみる（undefinedの場合あり）
						active = scope.findByName(scope, paramId);
						// 不正なパスだった場合（主にカラム用）
						if (ng.isUndefined(active)) {
							redirect();
						}
					}
					// 親がルートに乗っていない場合
					else {
						active = current;
					}

				}

				// ルートにパス指定があり、アクティブデータもある場合
				else if (paramId && previous) {

					debugPrintPrevName();

					// 親がルートに乗っている場合
					if (isOnRoute) {

						if (paramId != getPrevName()) {
							// location中のparamIdから取得してみる（undefinedの場合あり）
							active = scope.findByName(scope, paramId);
//							$log.ex.debug("active:",active);
							// 不正なパスだった場合
							if (!active) {
								redirect();
							}
						}
						else {
							debugPrintSkipped();// デバッグ
							return;
						}
					}
					// 親がルートに乗っていない場合
					else {
						debugPrintSkipped();// デバッグ
						return;
					}
				}

				// debug ------------------------------
				$log.ex.debug("active:", active);
				// debug ------------------------------

				// undefinedは無視
				if (!active || previous === active) {
					debugPrintSkipped();// デバッグ
					return;
				}

				scope.activate(scope, active);
				scope.isOnRoute = isOnRoute;

				setLocation(scope);

				// debug ------------------------------
				$log.ex.debug(eventName + "**********************************");
				// debug ------------------------------

				scope.changed = true;

//				scope.$broadcast("activeChanged");
				scope.$broadcast(eventName);

			}

			/**
			 * ロケーションパスセット
			 * @param {*} scope
			 */
			function setLocation(scope) {
//				scope.currentPath = "/" + scope.getActiveName(scope);
//				scope.currentPath = scope.getCurrentPath();//各コントローラに設定
				scope.parentPath = scope.$parent.fullPath;
				scope.fullPath = scope.getFullPath(scope);
				$log.ex.debug("~~~~~~~~~~~~~~~~~~~~~~~$scope.fullPath:", scope.fullPath);
			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			// パブリックAPI

			return({
				changeActive : changeActive,
				setLocation  : setLocation
			});

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