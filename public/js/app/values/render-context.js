/** ************************************************************
 * 与えられたレンダーパスのための場所、
 * 現在のルートリクエストについての情報を提供。// I provide information about the current route request, local to the given render path.
 *
 * 参照元：
 *     ブログ：http://www.bennadel.com/blog/2441-Nested-Views-Routing-And-Deep-Linking-With-AngularJS.htm
 *    Github：https://github.com/bennadel/AngularJS-Routing
 *    デモ：http://bennadel.github.com/AngularJS-Routing
 *
 * Author      : H.Kezuka
 * Created at  : 2013/03/28
 * Modified at :
 * *********************************************************** */

(function (ng, app) {

	"use strict";

	// injection な書き方をするとエラーになるのでこのままで
	app.value(
		"RenderContext",
		function (requestContext, actionPrefix, paramNames) {

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Member Methods.
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			/**
			 * 監視ロケーションの、次のセクションを返す。// I return the next section after the location being watched.
			 * @return {*}
			 */
			function getNextSection() {
				return(requestContext.getNextSection(actionPrefix));
			}

			/**
			 *（現ロケーションにローカルな）アクションが変更されたかどうかを確認。// I check to see if the action has changed (and is local to the current location).
			 * @return {*|boolean}
			 */
			function isChangeLocal() {
				return(requestContext.startsWith(actionPrefix));
			}

			/**
			 * リクエスト・コンテキスト内の最後の変更が、このレンダリング・コンテキスト
			 * で観察されているアクションとルートparamsに関係あるかどうかを判断。// I determine if the last change in the request context is relevant to the action and route params being observed in this render context.
			 * @return {*}
			 */
			function isChangeRelevant() {

				// アクションがアクションの接頭辞に対してローカルでない場合、
				// paramsチェック不要。// If the action is not local to the action prefix, then we don't even  want to bother checking the params.
				if (!requestContext.startsWith(actionPrefix)) {
					return( false );
				}

				// アクションが変更されている場合、paramsチェック不要。// If the action has changed, we don't need to bother checking the params.
				if (requestContext.hasActionChanged()) {
					return( true );
				}

				// 観測されたパラメータに基づいて、変更決定を行う。// If we made it this far, we know that the action has not changed. As such, we''ll have to make the change determination based on the observed parameters.
				return(paramNames.length &&
					requestContext.haveParamsChanged(paramNames));

			}

			// =============================================*

			// request-context#getRenderContext内にデバッグプリントを配置
			// 拡張した$logを利用したいため
			/**
			 * デバッグプリント
			 */
//			function debugPrint() {
//				console.log("---- render-context.js -----------------------------");
//				console.log("requestContext:", requestContext);
//				console.log("actionPrefix:", actionPrefix);
//				console.log("paramNames:", paramNames);
//			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			// パブリックAPI // Return the public API.

			return({
				getNextSection   : getNextSection,
				isChangeLocal    : isChangeLocal,
				isChangeRelevant : isChangeRelevant
			});
		}
	);
})(angular, myApp);

/** ************************************************************
 * *********************************************************** */
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
// =============================================*
// -------------------------------------+
