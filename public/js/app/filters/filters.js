/** ************************************************************
 * オリジナルフィルター
 *
 * Author      : H.Kezuka
 * Created at  : 2013/03/24
 * Modified at :
 * *********************************************************** */
(function (ng) {

	"use strict";

	/**
	 * 単語の先頭だけ大文字にする。
	 *
	 * @param word 単語文字列
	 * @return {string} 加工済みの文字列
	 */
	function word(word) {
		return word.substring(0, 1).toUpperCase()
				+ word.slice(1).toLowerCase();
	}

	/**
	 * 複数形にする処理
	 * （汎用的な処理ではない）
	 *
	 * @param word 検査する単語
	 * @param items 複数形データ配列
	 * @return {*} 複数形の単語
	 */
	function pluralize(word, items) {
		for (var i in items) {
			if (word == items[i].word) return items[i].plural;
		}
	}

	/**
	 * 文章の単語すべてを先頭大文字化する。
	 *
	 * @param sentence 対象の文章文字列
	 * @return {string} 加工済みの文字列
	 */
	function sentence(sentence) {
		var word_arr = sentence.split(" ");
		var new_sentence = '';

		for (var i in word_arr) {
			new_sentence += word(word_arr[i]) + " ";
		}
		return new_sentence;
	}

	// モジュールで宣言
	ng.module('myFilters', []).
//		filter('exp', function () {
//			return function(){console.log("exp");}
//		});
		filter('word',function () {
			return word
		}).
		filter('pluralize',function () {
			return pluralize
		}).
		filter('sentence', function () {
			return sentence
		});

})(angular);

/** ************************************************************
 * *********************************************************** */
/* ************************************************************ */
/* ++++++++++++++++++++++++++*+++++++++++++++++++++++++ */
// =============================================*
// -------------------------------------+
