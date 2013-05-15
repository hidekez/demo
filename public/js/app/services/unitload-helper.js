/** ************************************************************
 * ユニットロード・ヘルパ
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
/*
 * ・データ保存領域
 * 		loadBuffer
 * 	 		<- localStorage
 *
 *
 */

 (function (ng, app) {

	"use strict";

	var serviceName = "unitLoadHelper";

	app.service(
		serviceName,
		['$log', '$location', '$q',
		function ($log, $location, $q) {

			// サーバーからリモートデータをロード // I load the remote data from the server.
			function loadRemoteData(colName, faceName, lineName, $$scope) {

				$$scope.isLoading = true;

				// ロードメソッドは各ユニットごとのを使用
//				var promise = $q.all(
//					[
//						unitLoader(lineName)
//					]
//				);
				var promise = unitLoader(lineName);

				promise.then(
					// success
					function (response) {

						$$scope.isLoading = false;

						var data = applyRemoteData(response[ 0 ]);
						setUnitData(colName, faceName, lineName, data);

					},
					// failed
					function (response) {

						// Tweetが何かの理由でロードできなかった時　// The tweet couldn't be loaded for some reason - possibly someone hacking with the URL.
						$location.path($$scope.$parent.fullPath);
						// debug ------------------------------
						$log.ex.debug("$$scope.$parent.fullPath:", $$scope.$parent.fullPath);
						// debug ------------------------------
					}
				);

			}

			/**
			 * キャッシュ利用か、新規読み込みかの分岐処理
			 */
			function getUnitData(){
				// column は active ではない
				var colName = $$scope.getColumnName(),
					faceName = $$scope.getActiveFaceName(),
					lineName = $$scope.getActiveLineName(),
					lineData = $$scope.getLineData(colName, faceName, lineName);
				$log.ex.debug("loadRemoteData/colName-faceName-lineName:", colName + "-" + faceName + "-" + lineName);
				$log.ex.debug("$$scope.getLineData(lineName):",lineData);

				if(lineData && lineData.length > 0 ){
					$log.ex.debug("キャッシュローーーーーーード");
					applyRemoteData(lineData);
				}else{
					$log.ex.debug("サーバーローーーーーーード");
					loadRemoteData(colName, faceName, lineName);
				}
			}

			function setUnitData(colName, faceName, lineName, data){
				$log.ex.debug("$$scope.getMainData:",$$scope.getMainData());

				// TODO 追加（増加）の場合の処理
//					$$scope.setLineData(lineName, units);//★★★ここ★★★units=null
//					$$scope.setLineData(lineName, $$scope.getUnits());//★★★ここ★★★units=null
				$$scope.setLineData(colName, faceName, lineName, data);

				$log.ex.debug("$$scope.getMainData:",$$scope.getMainData());
			}

			/**
			 * 各ユニットのロード処理共通部分
			 *
			 * @param $$scope
			 * @return {RenderContext} 新規のレンダーコンテキスト・インスタンス
			 */
			function setup($$scope,  unitName, units, applyRemoteData, unitLoader) {

//				var lineName = $$scope.getActiveLineName();
//				$$log.ex.debug("$$$$$$$$$$$$$$$$$$$scope.getActiveLineName:", lineName);
//				var tweets = $$scope.getLineData(lineName) || [];
//				$$scope.tweets = tweets;

				// ロード済みかのフラグ // I flag that data is being loaded.
				$$scope.isLoading = true;

				// 表示用のユーザーデータ // I am the tweet and the list of pets that are being viewed.
				units = null;

				/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
				// Define Controller Methods.

				/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
				// Define Scope Methods.

				/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
				// Bind To Scope Events.

				// I handle changes to the request context.
				$$scope.$on(
					"$routeChangeSuccess",
					function () {
						$log.ex.debug("あれ?????:");
						if ($$scope.getActiveFaceName() !== unitName ||
							$$scope.$routeParams.faceId !== unitName) {
							$log.ex.debug("ロードはパス");
							return;
						}
						getUnitData();
					}
				);

//				$$scope.$on(
//					"applyRemoteData" + lineName,
//					function () {
//						setUnitData();
//					}
//				);
				/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
				// 初期化処理
				/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
				// Load the "remote" data.
				if ($$scope.getActiveFaceName() === unitName) {
					getUnitData();
				}

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
