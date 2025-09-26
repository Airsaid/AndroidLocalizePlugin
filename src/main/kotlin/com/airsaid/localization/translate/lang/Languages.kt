/*
 * Copyright 2021 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.airsaid.localization.translate.lang

/**
 * Enum class for supported languages with Android values directory mapping.
 *
 * @author airsaid
 */
enum class Languages(
  val code: String,
  val displayName: String,
  val englishName: String,
  val flag: String,
  val directoryName: String,
) {
  AUTO("auto", "Auto", "Auto", "🌏", ""),
  ALBANIAN("sq", "Shqiptar", "Albanian", "🇦🇱", "sq"),
  ARABIC("ar", "العربية", "Arabic", "🇸🇦", "ar"),
  AMHARIC("am", "አማርኛ", "Amharic", "🇪🇹", "am"),
  AZERBAIJANI("az", "азәрбајҹан", "Azerbaijani", "🇦🇿", "az"),
  IRISH("ga", "Gaeilge", "Irish", "🇮🇪", "ga"),
  ESTONIAN("et", "Eesti", "Estonian", "🇪🇪", "et"),
  BASQUE("eu", "Euskal", "Basque", "🇪🇸", "eu"),
  BELARUSIAN("be", "беларускі", "Belarusian", "🇧🇾", "be"),
  BULGARIAN("bg", "Български", "Bulgarian", "🇧🇬", "bg"),
  ICELANDIC("is", "Íslenska", "Icelandic", "🇮🇸", "is"),
  POLISH("pl", "Polski", "Polish", "🇵🇱", "pl"),
  BOSNIAN("bs", "Bosanski", "Bosnian", "🇧🇦", "bs"),
  PERSIAN("fa", "Persian", "Persian", "🇮🇷", "fa"),
  AFRIKAANS("af", "Afrikaans", "Afrikaans", "🇿🇦", "af"),
  DANISH("da", "Dansk", "Danish", "🇩🇰", "da"),
  GERMAN("de", "Deutsch", "German", "🇩🇪", "de"),
  RUSSIAN("ru", "Русский", "Russian", "🇷🇺", "ru"),
  FRENCH("fr", "Français", "French", "🇫🇷", "fr"),
  FILIPINO("fil", "Filipino", "Filipino", "🇵🇭", "fil"),
  FINNISH("fi", "Suomi", "Finnish", "🇫🇮", "fi"),
  FRISIAN("fy", "Frysk", "Frisian", "🇳🇱", "fy"),
  KHMER("km", "ខ្មែរ", "Khmer", "🇰🇭", "km"),
  GEORGIAN("ka", "ქართული", "Georgian", "🇬🇪", "ka"),
  GUJARATI("gu", "ગુજરાતી", "Gujarati", "🇮🇳", "gu"),
  KAZAKH("kk", "Kazakh", "Kazakh", "🇰🇿", "kk"),
  HAITIAN_CREOLE("ht", "Haitian Creole", "Haitian Creole", "🇭🇹", "ht"),
  KOREAN("ko", "한국어", "Korean", "🇰🇷", "ko"),
  HAUSA("ha", "Hausa", "Hausa", "🇳🇬", "ha"),
  DUTCH("nl", "Nederlands", "Dutch", "🇳🇱", "nl"),
  KYRGYZ("ky", "Кыргыз тили", "Kyrgyz", "🇰🇬", "ky"),
  GALICIAN("gl", "Galego", "Galician", "🇪🇸", "gl"),
  CATALAN("ca", "Català", "Catalan", "🇪🇸", "ca"),
  CZECH("cs", "Čeština", "Czech", "🇨🇿", "cs"),
  KANNADA("kn", "ಕನ್ನಡ", "Kannada", "🇮🇳", "kn"),
  CORSICAN("co", "Corsa", "Corsican", "🇫🇷", "co"),
  CROATIAN("hr", "Hrvatski", "Croatian", "🇭🇷", "hr"),
  KURDISH("ku", "Kurdî", "Kurdish", "🇮🇶", "ku"),
  LATIN("la", "Latina", "Latin", "🇻🇦", "la"),
  LATVIAN("lv", "Latviešu", "Latvian", "🇱🇻", "lv"),
  LAO("lo", "ລາວ", "Lao", "🇱🇦", "lo"),
  LITHUANIAN("lt", "Lietuvių", "Lithuanian", "🇱🇹", "lt"),
  LUXEMBOURGISH("lb", "Lëtzebuergesch", "Luxembourgish", "🇱🇺", "lb"),
  ROMANIAN("ro", "Română", "Romanian", "🇷🇴", "ro"),
  MALAGASY("mg", "Malagasy", "Malagasy", "🇲🇬", "mg"),
  MALTESE("mt", "Il-Malti", "Maltese", "🇲🇹", "mt"),
  MARATHI("mr", "मराठी", "Marathi", "🇮🇳", "mr"),
  MALAYALAM("ml", "മലയാളം", "Malayalam", "🇮🇳", "ml"),
  MALAY("ms", "Melayu", "Malay", "🇲🇾", "ms"),
  MACEDONIAN("mk", "Македонски", "Macedonian", "🇲🇰", "mk"),
  MAORI("mi", "Māori", "Maori", "🇳🇿", "mi"),
  MONGOLIAN("mn", "Монгол хэл", "Mongolian", "🇲🇳", "mn"),
  BANGLA("bn", "বাংল", "Bangla", "🇧🇩", "bn"),
  BURMESE("my", "မြန်မာ", "Burmese", "🇲🇲", "my"),
  HMONG("hmn", "Hmoob", "Hmong", "🇨🇳", "hmn"),
  XHOSA("xh", "IsiXhosa", "Xhosa", "🇿🇦", "xh"),
  ZULU("zu", "Zulu", "Zulu", "🇿🇦", "zu"),
  NEPALI("ne", "नेपाली", "Nepali", "🇳🇵", "ne"),
  NORWEGIAN("no", "Norsk", "Norwegian", "🇳🇴", "no"),
  PUNJABI("pa", "ਪੰਜਾਬੀ", "Punjabi", "🇮🇳", "pa"),
  PORTUGUESE("pt", "Português", "Portuguese", "🇵🇹", "pt"),
  PASHTO("ps", "Pashto", "Pashto", "🇦🇫", "ps"),
  CHICHEWA("ny", "Chichewa", "Chichewa", "🇲🇼", "ny"),
  JAPANESE("ja", "日本語", "Japanese", "🇯🇵", "ja"),
  SWEDISH("sv", "Svenska", "Swedish", "🇸🇪", "sv"),
  SAMOAN("sm", "Samoa", "Samoan", "🇼🇸", "sm"),
  SERBIAN("sr", "Српски", "Serbian", "🇷🇸", "sr"),
  SOTHO("st", "Sesotho", "Sotho", "🇱🇸", "st"),
  SINHALA("si", "සිංහල", "Sinhala", "🇱🇰", "si"),
  ESPERANTO("eo", "Esperanta", "Esperanto", "🇺🇳", "eo"),
  SLOVAK("sk", "Slovenčina", "Slovak", "🇸🇰", "sk"),
  SLOVENIAN("sl", "Slovenščina", "Slovenian", "🇸🇮", "sl"),
  SWAHILI("sw", "Kiswahili", "Swahili", "🇹🇿", "sw"),
  SCOTTISH_GAELIC("gd", "Gàidhlig na h-Alba", "Scottish Gaelic", "🇬🇧", "gd"),
  CEBUANO("ceb", "Cebuano", "Cebuano", "🇵🇭", "ceb"),
  SOMALI("so", "Somali", "Somali", "🇸🇴", "so"),
  TAJIK("tg", "Тоҷикӣ", "Tajik", "🇹🇯", "tg"),
  TELUGU("te", "తెలుగు", "Telugu", "🇮🇳", "te"),
  TAMIL("ta", "தமிழ்", "Tamil", "🇮🇳", "ta"),
  THAI("th", "ไทย", "Thai", "🇹🇭", "th"),
  TURKISH("tr", "Türkçe", "Turkish", "🇹🇷", "tr"),
  WELSH("cy", "Cymraeg", "Welsh", "🇬🇧", "cy"),
  URDU("ur", "اردو", "Urdu", "🇵🇰", "ur"),
  UKRAINIAN("uk", "Українська", "Ukrainian", "🇺🇦", "uk"),
  UZBEK("uz", "O'zbek", "Uzbek", "🇺🇿", "uz"),
  SPANISH("es", "Español", "Spanish", "🇪🇸", "es"),
  HEBREW("iw", "עברית", "Hebrew", "🇮🇱", "iw"),
  GREEK("el", "Ελληνικά", "Greek", "🇬🇷", "el"),
  HAWAIIAN("haw", "Hawaiian", "Hawaiian", "🇺🇸", "haw"),
  SINDHI("sd", "سنڌي", "Sindhi", "🇵🇰", "sd"),
  HUNGARIAN("hu", "Magyar", "Hungarian", "🇭🇺", "hu"),
  SHONA("sn", "Shona", "Shona", "🇿🇼", "sn"),
  ARMENIAN("hy", "Հայերեն", "Armenian", "🇦🇲", "hy"),
  IGBO("ig", "Igbo", "Igbo", "🇳🇬", "ig"),
  ITALIAN("it", "Italiano", "Italian", "🇮🇹", "it"),
  YIDDISH("yi", "ייִדיש", "Yiddish", "🇮🇱", "yi"),
  HINDI("hi", "हिंदी", "Hindi", "🇮🇳", "hi"),
  SUNDANESE("su", "Sunda", "Sundanese", "🇮🇩", "su"),
  INDONESIAN("id", "Indonesia", "Indonesian", "🇮🇩", "in"),
  JAVANESE("jv", "Wong Jawa", "Javanese", "🇮🇩", "jv"),
  ENGLISH("en", "English", "English", "🇺🇸", "en"),
  YORUBA("yo", "Yorùbá", "Yoruba", "🇳🇬", "yo"),
  VIETNAMESE("vi", "Tiếng Việt", "Vietnamese", "🇻🇳", "vi"),
  CHINESE_TRADITIONAL("zh-rTW", "正體中文", "Chinese Traditional", "🇨🇳", "zh-rTW"),
  CHINESE_SIMPLIFIED("zh-rCN", "简体中文", "Chinese Simplified", "🇨🇳", "zh-rCN"),
  ASSAMESE("as", "Assamese", "Assamese", "🇮🇳", "as"),
  DARI("prs", "Dari", "Dari", "🇦🇫", "prs"),
  FIJIAN("fj", "Fijian", "Fijian", "🇫🇯", "fj"),
  HMONG_DAW("mww", "Hmong Daw", "Hmong Daw", "🇨🇳", "mww"),
  INUKTITUT("iu", "ᐃᓄᒃᑎᑐᑦ", "Inuktitut", "🇨🇦", "iu"),
  ODIA("or", "Odia", "Odia", "🇮🇳", "or"),
  QUERETARO_OTOMI("otq", "Querétaro Otomi", "Querétaro Otomi", "🇲🇽", "otq"),
  TAHITIAN("ty", "Tahitian", "Tahitian", "🇵🇫", "ty"),
  TIGRINYA("ti", "ትግርኛ", "Tigrinya", "🇪🇷", "ti"),
  TONGAN("to", "lea fakatonga", "Tongan", "🇹🇴", "to"),
  YUCATEC_MAYA("yua", "Yucatec Maya", "Yucatec Maya", "🇲🇽", "yua");

  companion object {
    fun languages(): List<Lang> {
      return Languages.entries.map { it.toLang() }
    }

    fun allSupportedLanguages(): List<Lang> {
      return Languages.entries.filter { it != AUTO }.map { it.toLang() }
    }

    private val DEFAULT_FAVORITES = listOf(
      ENGLISH,
      CHINESE_SIMPLIFIED,
      CHINESE_TRADITIONAL,
      SPANISH,
      FRENCH,
      GERMAN,
      JAPANESE,
      KOREAN,
      PORTUGUESE,
      HINDI
    )

    fun defaultFavoriteCodes(): List<String> = DEFAULT_FAVORITES.map { it.code }

    fun defaultFavoriteLanguages(): List<Lang> = DEFAULT_FAVORITES.map { it.toLang() }
  }
}

fun Languages.toLang(): Lang {
  return Lang(
    code = this.code,
    name = this.displayName,
    englishName = this.englishName,
    flag = this.flag,
    directoryName = this.directoryName
  )
}