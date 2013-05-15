/** ************************************************************
 * レンダー・ヘルパ
 *
 * 各コントロールに設定しているものが同じだったのでまとめた。
 *
 * 参照元：
 *     ブログ：http://www.bennadel.com/blog/2441-Nested-Views-Routing-And-Deep-Linking-With-AngularJS.htm
 *    Github：https://github.com/bennadel/AngularJS-Routing
 *    デモ：http://bennadel.github.com/AngularJS-Routing
 *
 * Author      : H.Kezuka
 * Created at  : 2013/04/03
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var serviceName = "renderHelper";

	app.service(
		serviceName,
		['$log',
		function ($log) {

			/**
			 * リクエストコンテキストからのレンダリングコンテキストの作成
			 *
			 * @param scope
			 * @param requestContext リクエストコンテキスト・インスタンス
			 * @param requestActionLocation このコントローラを示すアクションの文字列
			 * @param optionalEventId イベント名を細分化するための追加文字列
			 * @return {RenderContext} 新規のレンダーコンテキスト・インスタンス
			 */
			function setup(scope, requestContext, requestActionLocation, optionalEventId) {

				/**
				 * コントローラ（と、関連するパラメータ）への
				 * ローカルレンダリングコンテキストを取得。// Get the render context local to this controller (and relevant params).
				 * @type {*} RenderContextインスンタンス
				 */
				var renderContext = requestContext.getRenderContext(requestActionLocation);

				/**
				 * サブビューはページ上にレンダリングされようとしているビューを示す。// The subview indicates which view is going to be rendered on the page.
				 * @type {*} 次のセクションを示す文字列
				 */
				scope.subview = renderContext.getNextSection();
				scope.requestActionLocation = requestActionLocation;

				// debug ------------------------------
				$log.ex.debug("requestActionLocation:", requestActionLocation);
				$log.ex.debug("renderContext:", renderContext);
				// debug ------------------------------

				/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
				// Bind To Scope Events.
				/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
				/**
				 * リクエスト・コンテキストへの変更を処理。// I handle changes to the request context.
				 */
				requestContext.setOnRequestContextChanged(scope, renderContext, optionalEventId);

				/**
				 * リクエスト・コンテキストのchangeイベントをトリガーできるようにルート変更を監視する。// Listen for route changes so that we can trigger request-context change events.
				 */
//				requestContext.setOnRouteChangeSuccess($scope, $route, $routeParams)

				/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
				// Return
				/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

				return renderContext;

			}

			return {
				setup : setup
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
