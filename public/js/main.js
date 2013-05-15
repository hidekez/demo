/** ************************************************************
 * アプリケーション・メイン
 *
 * AngularJSでは唯一なアプリケーションをまず設定する。
 * またここでは、ルーティングの設定と初期化を行っている。
 *
 * （このJSファイルがエントリーポイントとなる）
 *
 * Author      : H.Kezuka
 * Created at  : 2013/03/28
 * Modified at :
 * *********************************************************** */
var myApp = angular.module(
	"myApp",
	[
		"myUtils",
		"myFilters",
		"ui",
		"ngResource"
	]);

(function (ng, app) {

	"use strict";

	app.config(
		['$routeProvider',
			function ($routeProvider) {

				/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
				// initialize.
				/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

				// Routing
				// -------
				// Typically, when defining routes, you will map the route to a Template to be
				// rendered; however, this only makes sense for simple web sites. When you are
				// building more complex applications, with nested navigation, you probably need
				// something more complex. In this case, we are mapping routes to render "Actions"
				// rather than a template.
				/**
				 * 通常、ルート定義する際には、レンダリングするテンプレートへの
				 * ルートをマップしますが、これは単純なWebサイトには適切です。
				 * ネストされたナビゲーションで、より複雑なアプリケーションを
				 * 構築している場合には、おそらくもっと複雑な何かが必要です。
				 * この場合、我々はむしろテンプレート以外の "アクション"を
				 * レンダリングするためのルートをマッピングしている。
				 */
				$routeProvider
					.when(
					"/welcome",
					{
						action : "guest.welcome"
//					template:"public/js/app/views/guest/welcome.html"
					}
				)
					.when(
					"/app",
					{
						action : "standard.col.face"
//					action : "standard.cols.col.faces.face"
//					template:"public/js/app/views/layouts/standard.html"
					}
				)
					.when(
					"/app/:columnId",
					{
						action : "standard.col.face.line"
					}
				)
					.when(
					"/app/:columnId/:faceId",
					{
						action : "standard.col.face.line"
					}
				)
					.when(
					"/app/:columnId/:faceId/:lineId",
					{
						action : "standard.col.face.line"
					}
				)
					.when(
					"/app/:columnId/:faceId/:lineId/:unitId",
					{
						action : "standard.col.face.line.unit"
					}
				)
					.otherwise(
					{
						redirectTo : "/app"
					}
				)
				;
			}]
	)
})(angular, myApp);

/* ************************************************************ */
/**
 * 定数
 */
/* ************************************************************ */
(function (ng, app) {

	"use strict";

	app.constant(
		{
			consts : {
				appName      : "tolist",
				logMode      : {
					log   : "log",
					info  : "info",
					warn  : "warn",
					debug : "debug"
				},
				echoTimeout  : (function () {
					// cache.life=30分に対して、10分に一度
					return (1000 * 60 * 10)//60 * 10
				})(),
				appRootPath  : "/app",
				context      : {
					app      : "",
					standard : "standard",
					column   : "standard.col",
					face     : "standard.col.face",
					line     : "standard.col.face.line",
					unit     : "standard.col.face.line.unit",
					welcome  : "guest.welcome"
				},
				/**
				 * テンプレートのパス情報
				 * Play配下のhtmlからの場合は@{}を使用するが、AngularJS配下の
				 * 場合には、JS上でルートとなる場所からのパスが必要となる。
				 * 混乱やミスを起こさないために、一カ所で集中管理している。
				 */
				templatePath : (function path() {

					function merge() {
						var p = "public/js/app/views";
						for (var i = 0, len = arguments.length; i < len; i++) {
							p += "/" + arguments[i];
						}
						return p;
					}

					return {
						welcome  : merge("guest", "welcome.html"),
						guest    : merge("layouts", "guest.html"),
						standard : merge("layouts", "standard.html"),
						nav      : merge("standard", "nav.html"),
						column   : merge("standard", "column.html"),
						face     : merge("standard", "face.html"),
						line     : merge("standard", "line.html"),
//						lineSub  : merge("standard", "line-sub.html"),
//						unit     : merge("standard", "unit.html"),
						list     : {
							item   : merge("standard/lists", "item.html"),
							label  : merge("standard/lists", "label.html"),
							repost : merge("standard/lists", "repost.html")
						},
						items    : {
							tweet : {
								list : merge("standard/lists/items", "twitter", "tweet-l.html"),
								unit : merge("standard/units/items", "twitter", "tweet-u.html")
							},
							user  : {
								list : merge("standard/lists/items", "twitter", "user-l.html"),
								unit : merge("standard/units/items", "twitter", "user-u.html")
							},
							list  : {
								list : merge("standard/lists/items", "twitter", "list-l.html"),
								unit : merge("standard/units/items", "twitter", "list-u.html")
							}
						},
						labels   : {
							category : {
								list : merge("standard/lists/labels", "category-l.html"),
								unit : merge("standard/units/labels", "category-u.html")
							},
							tag      : {
								list : merge("standard/lists/labels", "tag-l.html"),
								unit : merge("standard/units/labels", "tag-u.html")
							},
							folder   : {
								list : merge("standard/lists/labels", "folder-l.html"),
								unit : merge("standard/units/labels", "folder-u.html")
							}
						},
						reposts  : {
							hoge : {
								list : merge("standard/lists/reposts", "hoge-l.html"),
								unit : merge("standard/units/reposts", "hoge-u.html")
							},
							piyo : {
								list : merge("standard/lists/reposts", "piyo-l.html"),
								unit : merge("standard/units/reposts", "piyo-u.html")
							},
							fuga : {
								list : merge("standard/lists/reposts", "fuga-l.html"),
								unit : merge("standard/units/reposts", "fuga-u.html")
							}
						}
					}
				})()
			}
		}
	)
})(angular, myApp);

/* ************************************************************ */
// 初期化処理
/* ************************************************************ */
(function (ng, app) {

	"use strict";

	app.run(
		['$rootScope', '$routeParams', '$log', 'consts', 'utils',
			function ($rootScope, $routeParams, $log, consts, utils) {

				// =============================================*
				// RootScopeに変数を設定
				// =============================================*
				// logModeの設定
				// {log,info,warn,debug}
				$rootScope.logMode = consts.logMode.debug;//"debug";

				// ビュー中のデバッグ用情報表示の切り替えフラグ
				$rootScope.debugPrintOnView = false;

				// ゲッターメソッド
				$rootScope.showDebugPrintOnView = function () {
					return $rootScope.debugPrintOnView;
				};
//		$rootScope.logCounter = 0;

				// namespace関数の追加
				utils.setNameDomain($rootScope);

				// =============================================*
				// RootScopeを拡張
				// =============================================*
				// 便利な関数などを追加
				utils.extendRootScope($rootScope, $routeParams);

				// $logの拡張
				utils.extendLog($log, $rootScope);

				// debug ------------------------------
				$log.info("$rootScope", $rootScope);
				//ex.debugの確認
				$log.ex.debug("hoge", "hoge");
				$log.ex.debug("piyo");
				// debug ------------------------------

			}]
	)
})(angular, myApp);

/** ************************************************************
 * *********************************************************** */
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
// =============================================*
// -------------------------------------+
