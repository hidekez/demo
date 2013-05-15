package utils;

import java.util.HashMap;
import java.util.Map;

import constants.WordCharacter;
import static utils.MyStringUtils.hiraToKata;

public class KanaToRoman {

	public static String org = "";
	boolean aa = false;
	boolean ii = false;
	boolean uu = false;
	boolean ee = false;
	boolean oo = false;
	boolean shi = false;
	boolean ti = false;
	boolean tsu = false;
	boolean fu = false;
	boolean wo = false;
	boolean ji = false;
	boolean di = false;
	boolean du = false;
	boolean sha = false;
	boolean shu = false;
	boolean sho = false;
	boolean jya = false;
	boolean ju = false;
	boolean jo = false;
	boolean cha = false;
	boolean chu = false;
	boolean cho = false;
	boolean mba = false;
	boolean tti = false;
	boolean kwa = false;
	boolean gwa = false;

	public static Map<String,String[]> tbl = new HashMap<String, String[]>();
	public static Map<String,String[]> boin = new HashMap<String, String[]>();

	static {

		tbl.put("アー", new String[]{"a"});
		tbl.put("イー", new String[]{"i"});
		tbl.put("ウー", new String[]{"u"});
		tbl.put("エー", new String[]{"e"});
		tbl.put("オー", new String[]{"o"});
		tbl.put("アア", new String[]{"a"});
		tbl.put("イイ", new String[]{"i"});
		tbl.put("ウウ", new String[]{"u"});
		tbl.put("エエ", new String[]{"e"});
		tbl.put("オオ", new String[]{"o"});
		tbl.put("オウ", new String[]{"o"});
		tbl.put("クァ", new String[]{"o"});
		tbl.put("クィ", new String[]{"o"});
		tbl.put("クェ", new String[]{"o"});
		tbl.put("クォ", new String[]{"o"});

		//tbl.put("アァ", new String[]{"a"});
		//tbl.put("イィ", new String[]{"i"});
		//tbl.put("ウゥ", new String[]{"u"});
		//tbl.put("エェ", new String[]{"e"});
		//tbl.put("オォ", new String[]{"o"});
		//tbl.put("カァ", new String[]{"ka"});
		//tbl.put("キィ", new String[]{"ki"});
		//tbl.put("クゥ", new String[]{"ku"});
		//tbl.put("ケェ", new String[]{"ke"});
		//tbl.put("コォ", new String[]{"ko"});
		//tbl.put("サァ", new String[]{"sa"});
		//tbl.put("シィ", new String[]{"si","shi"});
		//tbl.put("スゥ", new String[]{"su"});
		//tbl.put("セェ", new String[]{"se"});
		//tbl.put("ソォ", new String[]{"so"});
		//tbl.put("タァ", new String[]{"ta"});
		//tbl.put("チィ", new String[]{"ti","chi"});
		//tbl.put("ツゥ", new String[]{"tu"});
		//tbl.put("テェ", new String[]{"te"});
		//tbl.put("トォ", new String[]{"to"});
		//tbl.put("ナァ", new String[]{"na"});
		//tbl.put("ニィ", new String[]{"ni"});
		//tbl.put("ヌゥ", new String[]{"nu"});
		//tbl.put("ネェ", new String[]{"ne"});
		//tbl.put("ノォ", new String[]{"no"});
		//tbl.put("ハァ", new String[]{"ha"});
		//tbl.put("ヒィ", new String[]{"hi"});
		//tbl.put("フゥ", new String[]{"hu","fu"});
		//tbl.put("ヘェ", new String[]{"he"});
		//tbl.put("ホォ", new String[]{"ho"});
		//tbl.put("マァ", new String[]{"ma"});
		//tbl.put("ミィ", new String[]{"mi"});
		//tbl.put("ムゥ", new String[]{"mu"});
		//tbl.put("メェ", new String[]{"me"});
		//tbl.put("モォ", new String[]{"mo"});
		//tbl.put("ヤァ", new String[]{"ya"});
		//tbl.put("ユゥ", new String[]{"yu"});
		//tbl.put("ヨォ", new String[]{"yo"});
		//tbl.put("ラァ", new String[]{"ra"});
		//tbl.put("リィ", new String[]{"ri"});
		//tbl.put("ルゥ", new String[]{"ru"});
		//tbl.put("レェ", new String[]{"re"});
		//tbl.put("ロォ", new String[]{"ro"});
		//tbl.put("ワァ", new String[]{"wa"});
		//tbl.put("ヲォ", new String[]{"wo","o"});
		//tbl.put("ガァ", new String[]{"ga"});
		//tbl.put("ギィ", new String[]{"gi"});
		//tbl.put("グゥ", new String[]{"gu"});
		//tbl.put("ゲェ", new String[]{"ge"});
		//tbl.put("ゴォ", new String[]{"go"});
		//tbl.put("ザァ", new String[]{"za"});
		//tbl.put("ジィ", new String[]{"zi","ji"});
		//tbl.put("ズゥ", new String[]{"zu"});
		//tbl.put("ゼェ", new String[]{"ze"});
		//tbl.put("ゾォ", new String[]{"zo"});
		//tbl.put("ダァ", new String[]{"da"});
		//tbl.put("ヂィ", new String[]{"di","zi","ji"});
		//tbl.put("ヅゥ", new String[]{"du","zu"});
		//tbl.put("デェ", new String[]{"de"});
		//tbl.put("ドォ", new String[]{"do"});
		//tbl.put("バァ", new String[]{"ba"});
		//tbl.put("ビィ", new String[]{"bi"});
		//tbl.put("ブゥ", new String[]{"bu"});
		//tbl.put("ベェ", new String[]{"be"});
		//tbl.put("ボォ", new String[]{"bo"});
		//tbl.put("パァ", new String[]{"pa"});
		//tbl.put("ピィ", new String[]{"pi"});
		//tbl.put("プゥ", new String[]{"pu"});
		//tbl.put("ペェ", new String[]{"pe"});
		//tbl.put("ポォ", new String[]{"po"});
		//tbl.put("キャァ", new String[]{"kya"});
		//tbl.put("キュゥ", new String[]{"kyu"});
		//tbl.put("キョォ", new String[]{"kyo"});
		//tbl.put("シャァ", new String[]{"sya","sha"});
		//tbl.put("シュゥ", new String[]{"syu","shu"});
		//tbl.put("ショォ", new String[]{"syo","sho"});
		//tbl.put("チャァ", new String[]{"tya","cha"});
		//tbl.put("チュゥ", new String[]{"tyu","chu"});
		//tbl.put("チョォ", new String[]{"tyo","cho"});
		//tbl.put("ニャァ", new String[]{"nya"});
		//tbl.put("ニュゥ", new String[]{"nyu"});
		//tbl.put("ニョォ", new String[]{"nyo"});
		//tbl.put("ヒャァ", new String[]{"hya"});
		//tbl.put("ヒュゥ", new String[]{"hyu"});
		//tbl.put("ヒョォ", new String[]{"hyo"});
		//tbl.put("リャァ", new String[]{"rya"});
		//tbl.put("リュゥ", new String[]{"ryu"});
		//tbl.put("リョォ", new String[]{"ryo"});
		//tbl.put("ギャァ", new String[]{"gya"});
		//tbl.put("ギュゥ", new String[]{"gyu"});
		//tbl.put("ギョォ", new String[]{"gyo"});
		//tbl.put("ジャァ", new String[]{"zya","ja"});
		//tbl.put("ジュゥ", new String[]{"zyu","ju"});
		//tbl.put("ジョォ", new String[]{"zyo","jo"});
		//tbl.put("ヂャァ", new String[]{"dya"});
		//tbl.put("ヂュゥ", new String[]{"dyu"});
		//tbl.put("ヂョォ", new String[]{"dyo"});
		//tbl.put("ビャァ", new String[]{"bya"});
		//tbl.put("ビュゥ", new String[]{"byu"});
		//tbl.put("ビョォ", new String[]{"byo"});
		//tbl.put("ピャァ", new String[]{"pya"});
		//tbl.put("ビュゥ", new String[]{"pyu"});
		//tbl.put("ピョォ", new String[]{"pyo"});

		tbl.put("ア", new String[]{"a"});
		tbl.put("イ", new String[]{"i"});
		tbl.put("ウ", new String[]{"u"});
		tbl.put("エ", new String[]{"e"});
		tbl.put("オ", new String[]{"o"});
		tbl.put("カ", new String[]{"ka"});
		tbl.put("キ", new String[]{"ki"});
		tbl.put("ク", new String[]{"ku"});
		tbl.put("ケ", new String[]{"ke"});
		tbl.put("コ", new String[]{"ko"});
		tbl.put("サ", new String[]{"sa"});
		tbl.put("シ", new String[]{"si","shi"});
		tbl.put("ス", new String[]{"su"});
		tbl.put("セ", new String[]{"se"});
		tbl.put("ソ", new String[]{"so"});
		tbl.put("タ", new String[]{"ta"});
		tbl.put("チ", new String[]{"ti","chi"});
		tbl.put("ツ", new String[]{"tu"});
		tbl.put("テ", new String[]{"te"});
		tbl.put("ト", new String[]{"to"});
		tbl.put("ナ", new String[]{"na"});
		tbl.put("ニ", new String[]{"ni"});
		tbl.put("ヌ", new String[]{"nu"});
		tbl.put("ネ", new String[]{"ne"});
		tbl.put("ノ", new String[]{"no"});
		tbl.put("ハ", new String[]{"ha"});
		tbl.put("ヒ", new String[]{"hi"});
		tbl.put("フ", new String[]{"hu","fu"});
		tbl.put("ヘ", new String[]{"he"});
		tbl.put("ホ", new String[]{"ho"});
		tbl.put("マ", new String[]{"ma"});
		tbl.put("ミ", new String[]{"mi"});
		tbl.put("ム", new String[]{"mu"});
		tbl.put("メ", new String[]{"me"});
		tbl.put("モ", new String[]{"mo"});
		tbl.put("ヤ", new String[]{"ya"});
		tbl.put("ユ", new String[]{"yu"});
		tbl.put("ヨ", new String[]{"yo"});
		tbl.put("ラ", new String[]{"ra"});
		tbl.put("リ", new String[]{"ri"});
		tbl.put("ル", new String[]{"ru"});
		tbl.put("レ", new String[]{"re"});
		tbl.put("ロ", new String[]{"ro"});
		tbl.put("ワ", new String[]{"wa"});
		tbl.put("ヲ", new String[]{"wo","o"});
		tbl.put("ン", new String[]{"n"});
		tbl.put("ガ", new String[]{"ga"});
		tbl.put("ギ", new String[]{"gi"});
		tbl.put("グ", new String[]{"gu"});
		tbl.put("ゲ", new String[]{"ge"});
		tbl.put("ゴ", new String[]{"go"});
		tbl.put("ザ", new String[]{"za"});
		tbl.put("ジ", new String[]{"zi","ji"});
		tbl.put("ズ", new String[]{"zu"});
		tbl.put("ゼ", new String[]{"ze"});
		tbl.put("ゾ", new String[]{"zo"});
		tbl.put("ダ", new String[]{"da"});
		tbl.put("ヂ", new String[]{"di","zi","ji"});
		tbl.put("ヅ", new String[]{"du","zu"});
		tbl.put("デ", new String[]{"de"});
		tbl.put("ド", new String[]{"do"});
		tbl.put("バ", new String[]{"ba"});
		tbl.put("ビ", new String[]{"bi"});
		tbl.put("ブ", new String[]{"bu"});
		tbl.put("ベ", new String[]{"be"});
		tbl.put("ボ", new String[]{"bo"});
		tbl.put("パ", new String[]{"pa"});
		tbl.put("ピ", new String[]{"pi"});
		tbl.put("プ", new String[]{"pu"});
		tbl.put("ペ", new String[]{"pe"});
		tbl.put("ポ", new String[]{"po"});
		tbl.put("キャ", new String[]{"kya"});
		tbl.put("キュ", new String[]{"kyu"});
		tbl.put("キョ", new String[]{"kyo"});
		tbl.put("シャ", new String[]{"sya","sha"});
		tbl.put("シュ", new String[]{"syu","shu"});
		tbl.put("ショ", new String[]{"syo","sho"});
		tbl.put("チャ", new String[]{"tya","cha"});
		tbl.put("チュ", new String[]{"tyu","chu"});
		tbl.put("チョ", new String[]{"tyo","cho"});
		tbl.put("ニャ", new String[]{"nya"});
		tbl.put("ニュ", new String[]{"nyu"});
		tbl.put("ニョ", new String[]{"nyo"});
		tbl.put("ヒャ", new String[]{"hya"});
		tbl.put("ヒュ", new String[]{"hyu"});
		tbl.put("ヒョ", new String[]{"hyo"});
		tbl.put("リャ", new String[]{"rya"});
		tbl.put("リュ", new String[]{"ryu"});
		tbl.put("リョ", new String[]{"ryo"});
		tbl.put("ギャ", new String[]{"gya"});
		tbl.put("ギュ", new String[]{"gyu"});
		tbl.put("ギョ", new String[]{"gyo"});
		tbl.put("ジャ", new String[]{"zya","ja"});
		tbl.put("ジュ", new String[]{"zyu","ju"});
		tbl.put("ジョ", new String[]{"zyo","jo"});
		tbl.put("ヂャ", new String[]{"dya"});
		tbl.put("ヂュ", new String[]{"dyu"});
		tbl.put("ヂョ", new String[]{"dyo"});
		tbl.put("ビャ", new String[]{"bya"});
		tbl.put("ビュ", new String[]{"byu"});
		tbl.put("ビョ", new String[]{"byo"});
		tbl.put("ピャ", new String[]{"pya"});
		tbl.put("ビュ", new String[]{"pyu"});
		tbl.put("ピョ", new String[]{"pyo"});

		//tbl.put("ッカ", new String[]{"kka"});
		//tbl.put("ッキ", new String[]{"kki"});
		//tbl.put("ック", new String[]{"kku"});
		//tbl.put("ッケ", new String[]{"kke"});
		//tbl.put("ッコ", new String[]{"kko"});
		//tbl.put("ッサ", new String[]{"ssa"});
		//tbl.put("ッシ", new String[]{"ssi","sshi"});
		//tbl.put("ッス", new String[]{"ssu"});
		//tbl.put("ッセ", new String[]{"sse"});
		//tbl.put("ッソ", new String[]{"sso"});
		//tbl.put("ッタ", new String[]{"tta"});
		//tbl.put("ッチ", new String[]{"tti","cchi"});
		//tbl.put("ッツ", new String[]{"ttu"});
		//tbl.put("ッテ", new String[]{"tte"});
		//tbl.put("ット", new String[]{"tto"});
		//tbl.put("ッナ", new String[]{"nna"});
		//tbl.put("ッニ", new String[]{"nni"});
		//tbl.put("ッヌ", new String[]{"nnu"});
		//tbl.put("ッネ", new String[]{"nne"});
		//tbl.put("ッノ", new String[]{"nno"});
		//tbl.put("ッハ", new String[]{"hha"});
		//tbl.put("ッヒ", new String[]{"hhi"});
		//tbl.put("ッフ", new String[]{"hhu","ffu"});
		//tbl.put("ッヘ", new String[]{"hhe"});
		//tbl.put("ッホ", new String[]{"hho"});
		//tbl.put("ッマ", new String[]{"mma"});
		//tbl.put("ッミ", new String[]{"mmi"});
		//tbl.put("ッム", new String[]{"mmu"});
		//tbl.put("ッメ", new String[]{"mme"});
		//tbl.put("ッモ", new String[]{"mmo"});
		//tbl.put("ッヤ", new String[]{"yya"});
		//tbl.put("ッユ", new String[]{"yyu"});
		//tbl.put("ッヨ", new String[]{"yyo"});
		//tbl.put("ッラ", new String[]{"rra"});
		//tbl.put("ッリ", new String[]{"rri"});
		//tbl.put("ッル", new String[]{"rru"});
		//tbl.put("ッレ", new String[]{"rre"});
		//tbl.put("ッロ", new String[]{"rro"});
		//tbl.put("ッワ", new String[]{"wwa"});
		//tbl.put("ッヲ", new String[]{"wwo","o"});
		//tbl.put("ッン", new String[]{"nn"});
		//tbl.put("ッガ", new String[]{"gga"});
		//tbl.put("ッギ", new String[]{"ggi"});
		//tbl.put("ッグ", new String[]{"ggu"});
		//tbl.put("ッゲ", new String[]{"gge"});
		//tbl.put("ッゴ", new String[]{"ggo"});
		//tbl.put("ッザ", new String[]{"zza"});
		//tbl.put("ッジ", new String[]{"zzi","jji"});
		//tbl.put("ッズ", new String[]{"zzu"});
		//tbl.put("ッゼ", new String[]{"zze"});
		//tbl.put("ッゾ", new String[]{"zzo"});
		//tbl.put("ッダ", new String[]{"dda"});
		//tbl.put("ッヂ", new String[]{"ddi","zzi","jji"});
		//tbl.put("ッヅ", new String[]{"ddu","zzu"});
		//tbl.put("ッデ", new String[]{"dde"});
		//tbl.put("ッド", new String[]{"ddo"});
		//tbl.put("ッバ", new String[]{"bba"});
		//tbl.put("ッビ", new String[]{"bbi"});
		//tbl.put("ッブ", new String[]{"bbu"});
		//tbl.put("ッベ", new String[]{"bbe"});
		//tbl.put("ッボ", new String[]{"bbo"});
		//tbl.put("ッパ", new String[]{"ppa"});
		//tbl.put("ッピ", new String[]{"ppi"});
		//tbl.put("ップ", new String[]{"ppu"});
		//tbl.put("ッペ", new String[]{"ppe"});
		//tbl.put("ッポ", new String[]{"ppo"});
		//tbl.put("ッキャ", new String[]{"kkya"});
		//tbl.put("ッキュ", new String[]{"kkyu"});
		//tbl.put("ッキョ", new String[]{"kkyo"});
		//tbl.put("ッシャ", new String[]{"ssya","ssha"});
		//tbl.put("ッシュ", new String[]{"ssyu","sshu"});
		//tbl.put("ッショ", new String[]{"ssyo","ssho"});
		//tbl.put("ッチャ", new String[]{"ttya","ccha"});
		//tbl.put("ッチュ", new String[]{"ttyu","cchu"});
		//tbl.put("ッチョ", new String[]{"ttyo","ccho"});
		//tbl.put("ッニャ", new String[]{"nnya"});
		//tbl.put("ッニュ", new String[]{"nnyu"});
		//tbl.put("ッニョ", new String[]{"nnyo"});
		//tbl.put("ッヒャ", new String[]{"hhya"});
		//tbl.put("ッヒュ", new String[]{"hhyu"});
		//tbl.put("ッヒョ", new String[]{"hhyo"});
		//tbl.put("ッリャ", new String[]{"rrya"});
		//tbl.put("ッリュ", new String[]{"rryu"});
		//tbl.put("ッリョ", new String[]{"rryo"});
		//tbl.put("ッギャ", new String[]{"ggya"});
		//tbl.put("ッギュ", new String[]{"ggyu"});
		//tbl.put("ッギョ", new String[]{"ggyo"});
		//tbl.put("ッジャ", new String[]{"zzya","jja"});
		//tbl.put("ッジュ", new String[]{"zzyu","jju"});
		//tbl.put("ッジョ", new String[]{"zzyo","jjo"});
		//tbl.put("ッヂャ", new String[]{"ddya"});
		//tbl.put("ッヂュ", new String[]{"ddyu"});
		//tbl.put("ッヂョ", new String[]{"ddyo"});
		//tbl.put("ッビャ", new String[]{"bbya"});
		//tbl.put("ッビュ", new String[]{"bbyu"});
		//tbl.put("ッビョ", new String[]{"bbyo"});
		//tbl.put("ッピャ", new String[]{"ppya"});
		//tbl.put("ッビュ", new String[]{"ppyu"});
		//tbl.put("ッピョ", new String[]{"ppyo"});

		tbl.put("カ", new String[]{"ka"});
		tbl.put("キ", new String[]{"ki"});
		tbl.put("ク", new String[]{"ku"});
		tbl.put("ケ", new String[]{"ke"});
		tbl.put("コ", new String[]{"ko"});
		tbl.put("サ", new String[]{"sa"});
		tbl.put("シ", new String[]{"si","shi"});
		tbl.put("ス", new String[]{"su"});
		tbl.put("セ", new String[]{"se"});
		tbl.put("ソ", new String[]{"so"});
		tbl.put("タ", new String[]{"ta"});
		tbl.put("チ", new String[]{"ti","chi"});
		tbl.put("ツ", new String[]{"tu"});
		tbl.put("テ", new String[]{"te"});
		tbl.put("ト", new String[]{"to"});
		tbl.put("ナ", new String[]{"na"});
		tbl.put("ニ", new String[]{"ni"});
		tbl.put("ヌ", new String[]{"nu"});
		tbl.put("ネ", new String[]{"ne"});
		tbl.put("ノ", new String[]{"no"});
		tbl.put("ハ", new String[]{"ha"});
		tbl.put("ヒ", new String[]{"hi"});
		tbl.put("フ", new String[]{"hu","fu"});
		tbl.put("ヘ", new String[]{"he"});
		tbl.put("ホ", new String[]{"ho"});
		tbl.put("マ", new String[]{"ma"});
		tbl.put("ミ", new String[]{"mi"});
		tbl.put("ム", new String[]{"mu"});
		tbl.put("メ", new String[]{"me"});
		tbl.put("モ", new String[]{"mo"});
		tbl.put("ヤ", new String[]{"ya"});
		tbl.put("ユ", new String[]{"yu"});
		tbl.put("ヨ", new String[]{"yo"});
		tbl.put("ラ", new String[]{"ra"});
		tbl.put("リ", new String[]{"ri"});
		tbl.put("ル", new String[]{"ru"});
		tbl.put("レ", new String[]{"re"});
		tbl.put("ロ", new String[]{"ro"});
		tbl.put("ワ", new String[]{"wa"});
		tbl.put("ヲ", new String[]{"wo","o"});
		tbl.put("ン", new String[]{"n"});
		tbl.put("ガ", new String[]{"ga"});
		tbl.put("ギ", new String[]{"gi"});
		tbl.put("グ", new String[]{"gu"});
		tbl.put("ゲ", new String[]{"ge"});
		tbl.put("ゴ", new String[]{"go"});
		tbl.put("ザ", new String[]{"za"});
		tbl.put("ジ", new String[]{"zi","ji"});
		tbl.put("ズ", new String[]{"zu"});
		tbl.put("ゼ", new String[]{"ze"});
		tbl.put("ゾ", new String[]{"zo"});
		tbl.put("ダ", new String[]{"da"});
		tbl.put("ヂ", new String[]{"di","zi","ji"});
		tbl.put("ヅ", new String[]{"du","zu"});
		tbl.put("デ", new String[]{"de"});
		tbl.put("ド", new String[]{"do"});
		tbl.put("バ", new String[]{"ba"});
		tbl.put("ビ", new String[]{"bi"});
		tbl.put("ブ", new String[]{"bu"});
		tbl.put("ベ", new String[]{"be"});
		tbl.put("ボ", new String[]{"bo"});
		tbl.put("パ", new String[]{"pa"});
		tbl.put("ピ", new String[]{"pi"});
		tbl.put("プ", new String[]{"pu"});
		tbl.put("ペ", new String[]{"pe"});
		tbl.put("ポ", new String[]{"po"});
		tbl.put("キャ", new String[]{"kya"});
		tbl.put("キュ", new String[]{"kyu"});
		tbl.put("キョ", new String[]{"kyo"});
		tbl.put("シャ", new String[]{"sya","sha"});
		tbl.put("シュ", new String[]{"syu","shu"});
		tbl.put("ショ", new String[]{"syo","sho"});
		tbl.put("チャ", new String[]{"tya","cha"});
		tbl.put("チュ", new String[]{"tyu","chu"});
		tbl.put("チョ", new String[]{"tyo","cho"});
		tbl.put("ニャ", new String[]{"nya"});
		tbl.put("ニュ", new String[]{"nyu"});
		tbl.put("ニョ", new String[]{"nyo"});
		tbl.put("ヒャ", new String[]{"hya"});
		tbl.put("ヒュ", new String[]{"hyu"});
		tbl.put("ヒョ", new String[]{"hyo"});
		tbl.put("リャ", new String[]{"rya"});
		tbl.put("リュ", new String[]{"ryu"});
		tbl.put("リョ", new String[]{"ryo"});
		tbl.put("ギャ", new String[]{"gya"});
		tbl.put("ギュ", new String[]{"gyu"});
		tbl.put("ギョ", new String[]{"gyo"});
		tbl.put("ジャ", new String[]{"zya","ja"});
		tbl.put("ジュ", new String[]{"zyu","ju"});
		tbl.put("ジョ", new String[]{"zyo","jo"});
		tbl.put("ヂャ", new String[]{"dya"});
		tbl.put("ヂュ", new String[]{"dyu"});
		tbl.put("ヂョ", new String[]{"dyo"});
		tbl.put("ビャ", new String[]{"bya"});
		tbl.put("ビュ", new String[]{"byu"});
		tbl.put("ビョ", new String[]{"byo"});
		tbl.put("ピャ", new String[]{"pya"});
		tbl.put("ビュ", new String[]{"pyu"});
		tbl.put("ピョ", new String[]{"pyo"});
		tbl.put("ー", new String[]{"-"});

		boin.put("ア", new String[]{"ア","ァ","カ","サ","タ","ナ","ハ","マ","ヤ","ャ","ラ","ワ","ガ","ザ","ダ","バ","パ"});
		boin.put("イ", new String[]{"イ","ィ","キ","シ","チ","ニ","ヒ","ミ","リ","ギ","ジ","ヂ","ビ","ピ"});
		boin.put("ウ", new String[]{"ウ","ゥ","ク","ス","ツ","ッ","ヌ","フ","ム","ユ","ュ","ル","グ","ズ","ヅ","ブ","プ"});
		boin.put("エ", new String[]{"エ","ェ","ケ","セ","テ","ネ","ヘ","メ","レ","ゲ","ゼ","デ","ベ","ペ"});
		boin.put("オ", new String[]{"オ","ォ","コ","ソ","ト","ノ","ホ","モ","ヨ","ョ","ロ","ヲ","ゴ","ゾ","ド","ボ","ポ"});
	}

	public String kana2roma(String s) {
		StringBuilder t = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if (i <= s.length() - 2) {
				if (tbl.containsKey(s.substring(i, i + 2))) {
					t.append(tbl.get(s.substring(i, i + 2)));
					i++;
				}
				else if (tbl.containsKey(s.substring(i, i + 1))) {
					t.append(tbl.get(s.substring(i, i + 1)));
				}
				else if (s.charAt(i) == 'っ') {
//					t.append(tbl.get(s.substring(i + 1, i + 2)).charAt(0));
				}
				else {
					t.append(s.charAt(i));
				}
			}
			else {
				if (tbl.containsKey(s.substring(i, i + 1))) {
					t.append(tbl.get(s.substring(i, i + 1)));
				}
				else {
					t.append(s.charAt(i));
				}
			}
		}
		return t.toString();
	}

	public static void main(String[] args) {
		KanaToRoman k2map = new KanaToRoman();

		String[] strs =
		{ "ひょーる",
				"びょーく",
				"いろはにほへと",
				"きゃっぷ",
				"さぶっ",
				"てぃーんえいじ",
				"+：ぷらす",
				"ははのはははばば",
				"えいのいえ",
				"れんたるCD店" };
		int num = 1;
		for (String s : strs) {
			System.out.println(String.format("%1$2d", num++) +
					" : " + s + "→" + k2map.kana2roma(s));
		}

	}

	// インスタンスの禁止
	private KanaToRoman() {}

	// 変換
	// 基本カタカナからのみとする
	public static String conv(String _org, WordCharacter _type) {
		if (_type != null && _type == WordCharacter.HIRAGANA) {
			org = MyStringUtils.hiraToKata(_org);
		}
//		else if(_type == WordCharacter.KATAKANA){
//
//		}

		return _org;
	}


}
