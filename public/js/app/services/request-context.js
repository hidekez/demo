/** ************************************************************
 * 現在のルートリクエストに関する情報を提供。// I provide information about the current route request.
 *
 * 参照元：
 *     ブログ：http://www.bennadel.com/blog/2441-Nested-Views-Routing-And-Deep-Linking-With-AngularJS.htm
 *    Github：https://github.com/bennadel/AngularJS-Routing
 *    デモ：http://bennadel.github.com/AngularJS-Routing
 *
 * RequestContext = Routeマッピングそのもの(e.g. /#/hoge/piyo/3)
 * action = レンダリングと関連したリクエストのことで、
 *             {main.js > app.config}にて$routeProvider機能によりマッピングされている。
 *             (e.g. "standard.hoge.piyo.fuga")
 * section = actionを"."で分割した配列のこと。
 *             合体させればRequestContextと同じものになる。
 *
 * Author      : H.Kezuka
 * Created at  : 2013/03/28
 * Modified at :
 * *********************************************************** */
(function (ng, app) {

	"use strict";

	var serviceName = "requestContext";

	app.service(
		serviceName,
		['$log', 'RenderContext',
		function ($log, RenderContext) {

//			$log.ex.setLabel(serviceName);
//			$log.ex.debug(serviceName);
			$log.ex.init(serviceName);

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Local Valiables.
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			// 現在のアクションパス。// Store the current action path.
			var action = "";

			// Store the action as an array of parts so we can more easily examine parts of it.
			// 部品の配列としてので、我々はより簡単にそれの部分を調べることができる
			// アクションを保管します。
			var sections = [];

			// 現在のルートparams。// Store the current route params.
			var params = {};

			// Store the previous action and route params.
			// We'll use these to make a comparison from one route change to the next.
			// 前のアクションとルートparams。我々は1経路変更から
			// 次へとの比較を行うために、これらを使用することにします。
			var previousAction = "";
			var previousParams = {};

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
			// Member Methods.
			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			// =============================================*
			// 取得関連（get）
			// =============================================*

			// Action
			/**
			 * 現在のアクションを取得。// I get the current action.
			 *
			 * @return {string}
			 */
//			function getAction() {
//				return( action );
//			}

			// Section
			/**
			 * 次のセクションの取得。
			 *
			 * アクションパスでの与えられたロケーションの、次のセクションを取得。// I get the next section at the given location on the action path.
			 *
			 * @param prefix
			 * @return {*}
			 */
			function getNextSection(prefix) {

				// prefixが現在のアクションに実在することを確認。// Make sure the prefix is actually in the current action.
				if (!startsWith(prefix)) {
					return( null );
				}

				// prefixが空の場合は、最初のセクションを返す。// If the prefix is empty, return the first section.
				if (prefix === "") {
					return( sections[ 0 ] );
				}

				// プレフィックスは有効。
				// 現在のパスの深さを把握する。// Now that we know the prefix is valid, lets figure out the depth of the current path.
				var depth = prefix.split(".").length;

				// 深さが範囲外の場合、そのアクションは同一のパスに対して
				// セクションが未定義であるため、nullを返します。// If the depth is out of bounds, meaning the current action doesn't define sections to that path (they are equal), then return null.
				if (depth === sections.length) {
					return( null );
				}

				// セクションを返します。// Return the section.
				return( sections[ depth ] );

			}

			// Params
			/**
			 * パラメータ取得。
			 *
			 * 与えられた名前、またはデフォルト値（またはNULL）を使って
			 * パラメータを返します。// I return the param with the given name, or the default value (or null).
			 *
			 * @param name
			 * @param defaultValue
			 * @return {*}
			 */
			function getParam(name, defaultValue) {

				if (ng.isUndefined(defaultValue)) {
					defaultValue = null;
				}

				return( params[ name ] || defaultValue );
			}

			// Params
			/**
			 * intでのパラメータ取得。
			 *
			 * intとして返せない場合は、指定されたデフォルト値を返す。
			 * デフォルト値が定義されていない場合、戻り値はゼロになる。// I return the param as an int. If the param cannot be returned as an int, the given default value is returned. If no default value is defined, the return will be zero.
			 *
			 * @param name
			 * @param defaultValue
			 * @return {*}
			 */
			function getParamAsInt(name, defaultValue) {

				// intに変換　// Try to parse the number.
				var valueAsInt = ( this.getParam(name, defaultValue || 0) );

				// 強制変換が失敗した場合、デフォルト値を返す。// Check to see if the coersion failed. If so, return the default.
				if (isNaN(valueAsInt)) {
					return( defaultValue || 0 );
				}
				else {
					return( valueAsInt );
				}
			}

			// RenderContext
			/**
			 * レンダーコンテキストの取得。
			 *
			 * 与えられたアクション接頭辞とルートパラメータのサブセットの
			 * レンダリングに関するコンテキストを返す。// I return the render context for the given action prefix and sub-set of route params.
			 *
			 * @param requestActionLocation
			 * @param paramNames
			 * @return {RenderContext}
			 */
			function getRenderContext(requestActionLocation, paramNames) {

				// リクエストアクションをデフォルトにします。// Default the requestion action.
				requestActionLocation = ( requestActionLocation || "" );

				// paramの名前をデフォルトにします。// Default the param names.
				paramNames = ( paramNames || [] );

				// パラメータ名は、単一の名前か名前の配列として渡せます。
				// 単一の名前が指定されている場合、配列に変換してみる。// The param names can be passed in as a single name; or, as an array of names. If a single name was provided, let's convert it to the array.
				if (!ng.isArray(paramNames)) {
					paramNames = [ paramNames ];
				}

				// デバッグプリント
				$log.ex.debug("---- RenderContext -----------------------------");
				$log.ex.debug("requestContext=this:", this);
				$log.ex.debug("actionPrefix=requestActionLocation:", requestActionLocation);
				$log.ex.debug("paramNames:", paramNames);
				$log.ex.debug("---- /RenderContext -----------------------------");

				return(new RenderContext(this, requestActionLocation, paramNames));
			}

			// =============================================*
			// 状態チェック（has/have/is）
			// =============================================*
			// Action
			/**
			 * アクションが変更されたかを判定。// I determine if the action has changed in this particular request context.
			 *
			 * @return {boolean}
			 */
			function hasActionChanged() {
				return( action !== previousAction );
			}

			// Params
			/**
			 * パラメータ変更チェック。
			 *
			 * 与えられたパラメータが変更されたか判断。
			 * 唯一のパラメータ名が定義されている場合、比較は前回の
			 * スナップショットを背景に行われるか、または、この変化の比較は、
			 * 特定の値（paramValue）に対して行うことができます。// I determine if the given param has changed in this particular request context. This change comparison can be made against a specific value(paramValue); or, if only the param name is defined, the comparison will  be made agains the previous snapshot.
			 *
			 * @param paramName
			 * @param paramValue
			 * @return {boolean}
			 */
			function hasParamChanged(paramName, paramValue) {

				// paramの値が存在する場合は、単純に現在のスナップショットと比較。// If the param value exists, then we simply want to use that to compare against the current snapshot.
				if (!ng.isUndefined(paramValue)) {
					return( !isParam(paramName, paramValue) );
				}

				// 以前のスナップショットに含まれてない場合、変更されたと見なす。// If the param was NOT in the previous snapshot, then we'll consider it changing.
				if (!previousParams.hasOwnProperty(paramName) &&
					params.hasOwnProperty(paramName)) {
					return( true );
				}
				// 前回のスナップショットに含まれているが現在は含まれていない場合、
				// 変更されたと見なす。// If the param was in the previous snapshot, but NOT in the current, we'll consider it to be changing.
				else if (previousParams.hasOwnProperty(paramName) && !params.hasOwnProperty(paramName)) {
					return( true );
				}

				// これまでのところ、paramは変更していない。
				// 実際の値を比較する。// If we made it this far, the param existence has not change; as such, let's compare their actual values.
				return( previousParams[ paramName ] !== params[ paramName ] );

			}

			// Params
			/**
			 * 複数パラメータ変更チェック。
			 *
			 * 与えられたparamsのいずれかに変更があったかを判定。// I determine if any of the given params have changed in this particular request context.
			 *
			 * @param paramNames
			 * @return {boolean}
			 */
			function haveParamsChanged(paramNames) {

				for (var i = 0, length = paramNames.length; i < length; i++) {

					if (hasParamChanged(paramNames[ i ])) {

						// １つでも変更があればtrueを返す。
						// 他のパラメータをチェックし続ける必要はなし。// If one of the params has changed, return true - no need to continue checking the other parameters.
						return( true );
					}
				}

				// paramsに変更はなし。// If we made it this far then none of the params have changed.
				return( false );
			}

			// Params
			/**
			 * パラメータ名と値とで、値に変更ないかを確認。// I check to see if the given param is still the given value.
			 *
			 * @param paramName
			 * @param paramValue
			 * @return {boolean}
			 */
			function isParam(paramName, paramValue) {

				// When comparing, using the coersive equals since we may be comparing 
				// parsed value against non-parsed values.
				// 我々は、非解析値に対して解析値を比較することができるので、
				// 比較するときには、coersiveを使用することに等しい。
				if (params.hasOwnProperty(paramName) &&
					( params[ paramName ] == paramValue )) {
					return( true );
				}

				// If we made it this far then param is either a different value; or, 
				// is no longer available in the route.
				// 我々はそれこれを行った場合ははるかにその後paramは別の値であり、
				// または、もはやルートで提供されていません。
				return( false );

			}

			// Action
			/**
			 * Prefixの妥当性をチェック
			 *
			 * 現在のアクションが指定されたパスで始まっているかを判定。// I determine if the current action starts with the given path.
			 * @param prefix
			 * @return {boolean}
			 */
			function startsWith(prefix) {

				// チェックするときには、我々は誤検知のための部分的なセクションと一致していないことを
				// 確認したい。だから、どちらかそれは全体的に一致する、または、それが終わりで追加点と
				// 一致する。// When checking, we want to make sure we don't match partial sections for false positives. So, either it matches in entirety; or, it matches with an additional dot at the end.
				if (!prefix.length ||
					( action === prefix ) ||
					( action.indexOf(prefix + ".") === 0 )) {

					return( true );
				}
				return( false );
			}

			// =============================================*
			// セットアップ（set）
			// =============================================*

			// RequestContext
			/**
			 * 新規のリクエストコンテキスト条件を設定。// I set the new request context conditions.
			 *
			 * @param newAction
			 * @param newRouteParams
			 */
			function setContext(newAction, newRouteParams) {

				// debug ------------------------------
				$log.ex.debug("request-context#setContext.newAction     :", newAction);
				$log.ex.debug("request-context#setContext.newRouteParams:", newRouteParams);
				// debug ------------------------------

				// 前回のスナップショットに、現在のアクションとparamsをコピーする。// Copy the current action and params into the previous snapshots.
				previousAction = action;
				previousParams = params;

				// アクションを設定。// Set the action.
				action = newAction;

				// セクションを決定するためのアクションを分割します。// Split the action to determine the sections.
				sections = action.split(".");

				// paramsのコレクションを更新する。// Update the params collection.
				params = ng.copy(newRouteParams);

			}

			// Routing
			/**
			 * リクエスト・コンテキストへの変更を処理。// I handle changes to the request context.
			 *
			 * @param scope
			 * @param renderContext
			 * @param optionalEventId イベント名を細分化するための追加文字列
			 */
			function setOnRequestContextChanged(scope, renderContext, optionalEventId) {
				var controllerName = scope.controllerName,
					$id = scope.$id,
					eventName = "requestContextChanged";
//				console.log("optionalEventId:",optionalEventId);
//				console.log("optionalEventId:",ng.isUndefined(optionalEventId));
				eventName += !ng.isUndefined(optionalEventId)? "_"+optionalEventId:"";
				console.log("eventName:",eventName);

				scope.$on(
					eventName,
					function () {

						// debug ------------------------------
						$log.ex.setLabel("");
//						$log.ex.debug("☆requestContextChanged in ", arguments[0].targetScope);
						$log.ex.debug("☆requestContextChanged:", eventName + "[" + $id + "]" + controllerName);
						// debug ------------------------------

						// このコントローラに関連していることを確認。// Make sure this change is relevant to this controller.
						if (renderContext.isChangeRelevant()) {
							// debug ------------------------------
							$log.ex.debug("★★★★★★★★★★★★★★requestContextChanged/renderContext.isChangeRelevant()==true:", controllerName);
							// debug ------------------------------
							// レンダリングされているビューを更新。// Update the view that is being rendered.
							scope.subview = renderContext.getNextSection();
						}

					}
				);

			}

			// Routing
			/**
			 * リクエスト・コンテキストのchangeイベントをトリガーできるようにルート変更を監視する。// Listen for route changes so that we can trigger request-context change events.
			 */
			function setOnRouteChangeSuccess(scope, route, routeParams) {
				var self = this;
				scope.$on(
					"$routeChangeSuccess",
					function (event) {

						// ディレクティブリダイレクトであれば、実行アクションなし。// If this is a redirect directive, then there's no taction to be taken.
						if (isRouteRedirect(route)) {
							// debug ------------------------------
							$log.ex.debug("▲routeChangeSuccess > isRouteRedirect(route)==false / route:", route);
							// debug ------------------------------
							return;
						}
						// debug ------------------------------
						$log.ex.debug("△routeChangeSuccess > isRouteRedirect(route)==true / route:", route);
						// debug ------------------------------

						// 現在のリクエストアクションの変更を更新。// Update the current request action change.
						self.setContext(route.current.action, routeParams);

						// レンダリング条件の変更を伝搬。// Announce the change in render conditions.
						scope.$broadcast("requestContextChanged", self);
					}
				);
			}

			// Routing
			/**
			 * 与えられたルートが有効なルートであるか、（パターンと一致するように
			 * 失敗したため）デフォルトルートにリダイレクトされているかを確認。// I check to see if the given route is a valid route; or, is the route being re-directed to the default route (due to failure to match pattern).
			 * @param route
			 * @return {boolean}
			 */
			function isRouteRedirect(route) {

				// debug ------------------------------
				$log.ex.debug("isRouteRedirect > route:", route);
//				$log.ex.debug("isRouteRedirect > $location.path():", $location.path());
//				$log.ex.debug("isRouteRedirect > $route.current.template  :", route.current.template);
//				$log.ex.debug("isRouteRedirect > $route.current.params    :", route.current.params);
//				$log.ex.debug("isRouteRedirect > $route.current.scope.name:", route.current.scope.name);
//				$log.ex.debug("isRouteRedirect > $routeParams:", $routeParams);
				// debug ------------------------------

				// アクションがない場合は、未知のルートから既知のルートへのリダイレクト。// If there is no action, then the route is redirection from an unknown route to a known route.
				return( !route.current.action );
			}

			/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

			// パブリックAPIを返します。// Return the public API.

			return({
				getNextSection             : getNextSection,
				getParam                   : getParam,
				getParamAsInt              : getParamAsInt,
				getRenderContext           : getRenderContext,
				hasActionChanged           : hasActionChanged,
				hasParamChanged            : hasParamChanged,
				haveParamsChanged          : haveParamsChanged,
				isParam                    : isParam,
				setContext                 : setContext,
				setOnRequestContextChanged : setOnRequestContextChanged,
				setOnRouteChangeSuccess    : setOnRouteChangeSuccess,
				startsWith                 : startsWith
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
