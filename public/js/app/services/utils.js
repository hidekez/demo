/** ************************************************************
 * ユーティリティ
 *
 * service→factoryにしても同じ
 *
 * Author      : H.Kezuka
 * Created at  : 2013/03/24
 * Modified at : 2013/03/28
 * *********************************************************** */
(function (ng) {

	"use strict";

	var serviceName = "utils";

	ng.module('myUtils', []).
		service(
			serviceName,
			['_',
				function (_) {

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					// 確認用
					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
//			function hoge() {
//				return console.log("hello hoge");
//			}
//
//			function piyo(mes) {
//				return console.log(mes);
//			}

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					// ネームスペース関連
					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

					// （「JavaScriptパターン」参照）
					var _domain = "";

					/**
					 * ネームドメイン（ネームスペースのルート）を設定
					 *
					 * $rootScope内に名前空間を作る兼ね合いで、
					 * $rootScopeを取得するために外部化されたもの。
					 *
					 * @param domain 文字列
					 */
					function setNameDomain(domain) {
						_domain = domain;
					}

					/**
					 * ネームスペース作成
					 *
					 * @param name
					 * @return {string}
					 */
					function namespace(name) {

						var parts = name.split("."),
							parent = _domain;

						// 先頭の冗長なグローバルを取り除く
						if (parts[0] === _domain) {
							parts = parts.slice(1);
						}

						for (var i = 0, len = parts.length; i < len; i += 1) {
							// プロパティが存在しなければ作成する
							if (typeof parent[parts[i]] === "undefined") {
								parent[parts[i]] = {};
							}
							parent = parent[parts[i]];
						}

						return parent;

					}

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					// 細々したもの
					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					function addDigit(org, len) {
						if (!isNaN(org)) {
							var n = len - (Number(org) + "").length;
							for (var i = 0; i < n; i++) {
								org = "0" + org;
							}
						}
						return org;
					}

					function addSpace(org, len) {
						var s = "" + org;
						while (s.length < len) {
							s = " " + s;
						}
						return s;
					}

//			function recursion(functionName, scope){
//				if(scope[functionName]){
//					recursion(functionName, scope);
//				}
//			}
					function memoizer(memo, fundamental) {
						var shell = function (key) {
							var result = memo[key];
							if (typeof result !== "number") {
								result = fundamental(shell, key);
								memo[key] = result;
							}
							return result;
						};
						return shell;
					}

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					// トグル処理
					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					/**
					 * トグル処理
					 *
					 * @param obj 処理したいオブジェクト
					 * @param propName トグル判定するプロパティ名
					 */
					function toggle(obj, propName) {
						obj[propName] = !obj[propName];
					}

					/**
					 * 全トグル処理
					 * 配列のすべてにトグル処理を実行
					 *
					 * @param array 処理したい配列
					 * @param propName トグル判定するプロパティ名
					 */
					function toggleAll(array, propName) {
						_.each(array, function (data) {
							toggle(data, propName);
						});
					}

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					// 配列から取得
					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					function find(array, key, value) {
						return _.find(array, function (data) {
							return data[key] == value;
						})
					}

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					// 配列インデックスのシフト計算
					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					/**
					 * 指定分だけずらした位置情報の取得
					 * 配列で、先頭の前は末尾に、末尾の次は先頭から取るためのもの
					 *
					 * @param length 配列の長さ
					 * @param currentIndex 現在位置
					 * @param shift ずらしたい数、符号付きで
					 * @return {number} シフト後のインデックス値
					 */
					function getIndex(length, currentIndex, shift) {
						return (((currentIndex + shift) + length) % length)
					}

					/**
					 * １つ前のインデックス
					 *
					 * @param length 配列の長さ
					 * @param index 現在位置
					 * @return {number} シフト後のインデックス値
					 */
					function getPrevIndex(length, index) {
						return getIndex(length, index, -1);
					}

					/**
					 * １つ次のインデックス
					 *
					 * @param length 配列の長さ
					 * @param index 現在位置
					 * @return {number} シフト後のインデックス値
					 */
					function getNextIndex(length, index) {
						return getIndex(length, index, 1);
					}

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					// 配列からのアクティブデータ取得
					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					/**
					 * アクティブなデータを取得（配列）
					 * 使用上の設定として、配列の１つだけがtrueとなっている配列を想定。
					 *
					 * @param array 検査対象となる配列
					 * @param propName アクティベートフラグとなるプロパティ名
					 * @return {*} 特定のプロパティフラグがtrueだったデータの配列
					 */
					function getActives(array, propName) {
						return _.filter(array, function (data) {
							return data[propName]
						})
					}

					/**
					 * アクティブなデータを取得（単体）
					 * 使用上の設定として、配列の１つだけがtrueとなっている配列を想定。
					 *
					 * @param array 検査対象となる配列
					 * @param propName アクティベートフラグとなるプロパティ名
					 * @return {*} 特定のプロパティフラグがtrueだった先頭のデータ
					 */
					function getActive(array, propName) {
						return getActives(array, propName)[0];
					}

					/**
					 * アクティブなデータの名前を取得（単体）
					 * 使用上の設定として、配列の１つだけがtrueとなっている配列を想定。
					 *
					 * @param array 検査対象となる配列
					 * @param props プロパティ名の配列
					 *                １番目にアクティベートフラグとなるプロパティ
					 *                ２番目に名称を示すプロパティ
					 * @return {*} 特定のプロパティフラグがtrueだった先頭のデータ
					 */
					function getActiveName(array, props) {
						return getActive(array, props[0])[props[1]];
					}

					// =============================================*
					/**
					 * アクティブなデータのインデックス番号を取得
					 * 使用上の設定として、配列の１つだけがtrueとなっている配列を想定。
					 *
					 * @param array 検査対象となる配列
					 * @param propName アクティベートフラグとなるプロパティ名
					 * @return {*} 特定のプロパティフラグがtrueだったデータの位置情報
					 */
					function getCurrentIndex(array, propName) {
						return _.indexOf(array, getActive(array, propName))
					}

					// =============================================*
					//TODO 最適化の検討
					/**
					 * 前のデータの取得
					 * @param array 検査対象となる配列
					 * @return {*} 配列上で前（先頭位置なら末尾）となるデータの位置情報
					 */
					function getPrev(array, propName) {
						var prevIndex = getPrevIndex(array.length, getCurrentIndex(array, propName));
						return array[prevIndex];
					}

					/**
					 * 後のデータの取得
					 * @param array 検査対象となる配列
					 * @return {*} 配列上で前（先頭位置なら末尾）となるデータの位置情報
					 */
					function getNext(array, propName) {
						var nextIndex = getNextIndex(array.length, getCurrentIndex(array, propName));
						return array[nextIndex];
					}

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					// アクティベート
					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					/**
					 * アクティベート（有効化）
					 *
					 * 対象となるデータのフラグ（有効判定用プロパティ）をtrueとし、
					 * 他のデータではそのフラグをfalseとする。
					 * リターンせずにそのまま配列情報を変更している（副作用あり）
					 *
					 * @param array 処理する配列
					 * @param target アクティブとしたいデータ
					 * @param keys 検査のための情報
					 *         id_property_name：比較するプロパティ
					 *         flag_property_name：有効判定用プロパティ
					 */
					function activate(array, target, keys) {
						_.each(array, function (data) {
							data[keys[1]] = !!((data[keys[0]] === target[keys[0]]));
						});
					}

					/**
					 * 名前によるアクティベート（有効化）
					 *
					 * 対象となるデータのフラグ（有効判定用プロパティ）をtrueとし、
					 * 他のデータではそのフラグをfalseとする。
					 * リターンせずにそのまま配列情報を変更している（副作用あり）
					 *
					 * @param array 処理する配列
					 * @param targetName アクティブとしたいデータの名前
					 * @param keys 検査のための情報
					 *         id_property_name：比較するプロパティ
					 *         flag_property_name：有効判定用プロパティ
					 */
					function activateByName(array, targetName, keys) {
						_.each(array, function (data) {
							data[keys[1]] = !!((data[keys[0]] === targetName));
						});
					}

//			// test
//			var list = [
//				{name:"hoge", active:true},
//				{name:"piyo", active:false},
//				{name:"fuga", active:false}
//			];
//			activate(list,list[1],["name","active"]);
//			console.log("activate", list);

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

					// Tips and Tricks から

					/**
					 * $rootScope拡張
					 * @param $$rootScope
					 * @param $$routeParams
					 */
					function extendRootScope($$rootScope, $$routeParams) {

						// ネームスペース関数
						$$rootScope.namespace = namespace;

						/**
						 * Easy access to route params
						 * REMEMBER: this object is empty until the $routeChangeSuccess event is broadcasted
						 */
						$$rootScope.params = $$routeParams;

						/**
						 * Wrapper for angular.isArray, isObject, etc checks for use in the view
						 *
						 * @param type {string} the name of the check (casing sensitive)
						 * @param value {string} value to check
						 */
						$$rootScope.namespace("is");
						$$rootScope.is = function (type, value) {
							return angular['is' + type](value);
						};

						/**
						 * Wrapper for $.isEmptyObject()
						 *
						 * @param value    {mixed} Value to be tested
						 * @return boolean
						 */
						$$rootScope.namespace("empty");
						$$rootScope.empty = function (value) {
							return $.isEmptyObject(value);
						};

						/**
						 * Debugging Tools
						 *
						 * Allows you to execute debug functions from the view
						 */
							// Use $log
//				$$rootScope.namespace("log");
//				$$rootScope.log = function (variable) {
//					console.log(variable);
//				};

						$$rootScope.namespace("alert");
						$$rootScope.alert = function (text) {
							alert(text);
						};

					}

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

					/**
					 * $log拡張
					 *
					 * @param $$log
					 * @param $$rootScope
					 */
					function extendLog($$log, $$rootScope) {

						var mode = $$log.mode = $$rootScope.logMode,
							counter = 0,
							label = "";
						/*$$rootScope.logCounter,*/

						var prefix = {
							log   : "LOG  ",
							info  : "INFO ",
							warn  : "WARN ",
							debug : "DEBUG"
						}

						function time() {
							var d = new Date(),
								s = d.getSeconds(),
								ms = d.getMilliseconds();
							s = addDigit(s, 2);
							ms = addDigit(ms, 3);
							return s + ":" + ms
						}

						function mergeArgs(prefix, args) {
							var cnt = addSpace(++counter, 3),
								str = cnt + "[" + time() + "]";
							while (str.length < 3) {
								str = " " + str;
							}
							str = str + " " + prefix + " - ";
							if (label.length) {
								str += label + ": ";
							}

							if (args.length == 1 && !angular.isObject(args[0])) {
								return [str + args[0]];
							}
							else {
								return [str].concat(args);
							}
						}

						function setLabel(lbl) {
							label = lbl;
						}

						$$log.ex = {};
						$$log.ex.setLabel = setLabel;

						$$log.ex.log = function () {
							$$log.log.apply($$log, mergeArgs(prefix.log, arguments));
						};

						$$log.ex.info = function () {
							$$log.info.apply($$log, mergeArgs(prefix.info, arguments));
						};

						$$log.ex.warn = function () {
							$$log.warn.apply($$log, mergeArgs(prefix.warn, arguments));
						};

						$$log.ex.debug = function () {
							if (mode == "debug") {
								$$log.log.apply($$log, mergeArgs(prefix.debug, arguments));
							}
						};

						$$log.ex.init = function (lbl) {
							$$log.ex.setLabel(lbl);
							$$log.ex.debug("");
						};

					}

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */

					function setSubview(scope, renderContext) {
						// このコントローラに関連していることを確認。// Make sure this change is relevant to this controller.
						if (renderContext.isChangeRelevant()) {
							// レンダリングされているビューを更新。// Update the view that is being rendered.
							scope.subview = renderContext.getNextSection();
						}
					}

//			function setOnRequestContextChanged(scope, renderContext){
//				scope.$on(
//					"requestContextChanged",
//					function () {
//						// このコントローラに関連していることを確認。// Make sure this change is relevant to this controller.
//						if (renderContext.isChangeRelevant()) {
//							// レンダリングされているビューを更新。// Update the view that is being rendered.
//							scope.subview = renderContext.getNextSection();
//						}
//
//					}
//				);
//
//			}

					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					// パブリックAPI
					/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
					return {
//				hoge           : hoge,
//				piyo           : piyo,
						setNameDomain   : setNameDomain,
						namespace       : namespace,
						addDigit        : addDigit,
						toggle          : toggle,
						toggleAll       : toggleAll,
						find            : find,
						getIndex        : getIndex,
						getPrevIndex    : getPrevIndex,
						getNextIndex    : getNextIndex,
						getActives      : getActives,
						getActive       : getActive,
						getActiveName   : getActiveName,
						getCurrentIndex : getCurrentIndex,
						getPrev         : getPrev,
						getNext         : getNext,
						activate        : activate,
						activateByName  : activateByName,
						extendRootScope : extendRootScope,
						extendLog       : extendLog,
						setSubview      : setSubview
//				setOnRequestContextChanged:setOnRequestContextChanged
					}
				}]
		);

})(angular);

/** ************************************************************
 * *********************************************************** */
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
// =============================================*
// -------------------------------------+
