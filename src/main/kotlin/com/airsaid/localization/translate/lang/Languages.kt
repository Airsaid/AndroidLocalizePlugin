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
  val id: Int,
  val code: String,
  val displayName: String,
  val englishName: String,
  val flag: String,
  val directoryName: String,
) {
  AUTO(0, "auto", "Auto", "Auto", "🌏", ""),
  ALBANIAN(1, "sq", "Shqiptar", "Albanian", "🇦🇱", "sq"),
  ARABIC(2, "ar", "العربية", "Arabic", "🇸🇦", "ar"),
  AMHARIC(3, "am", "አማርኛ", "Amharic", "🇪🇹", "am"),
  AZERBAIJANI(4, "az", "азәрбајҹан", "Azerbaijani", "🇦🇿", "az"),
  IRISH(5, "ga", "Gaeilge", "Irish", "🇮🇪", "ga"),
  ESTONIAN(6, "et", "Eesti", "Estonian", "🇪🇪", "et"),
  BASQUE(7, "eu", "Euskal", "Basque", "🇪🇸", "eu"),
  BELARUSIAN(8, "be", "беларускі", "Belarusian", "🇧🇾", "be"),
  BULGARIAN(9, "bg", "Български", "Bulgarian", "🇧🇬", "bg"),
  ICELANDIC(10, "is", "Íslenska", "Icelandic", "🇮🇸", "is"),
  POLISH(11, "pl", "Polski", "Polish", "🇵🇱", "pl"),
  BOSNIAN(12, "bs", "Bosanski", "Bosnian", "🇧🇦", "bs"),
  PERSIAN(13, "fa", "Persian", "Persian", "🇮🇷", "fa"),
  AFRIKAANS(14, "af", "Afrikaans", "Afrikaans", "🇿🇦", "af"),
  DANISH(15, "da", "Dansk", "Danish", "🇩🇰", "da"),
  GERMAN(16, "de", "Deutsch", "German", "🇩🇪", "de"),
  RUSSIAN(17, "ru", "Русский", "Russian", "🇷🇺", "ru"),
  FRENCH(18, "fr", "Français", "French", "🇫🇷", "fr"),
  FILIPINO(19, "fil", "Filipino", "Filipino", "🇵🇭", "fil"),
  FINNISH(20, "fi", "Suomi", "Finnish", "🇫🇮", "fi"),
  FRISIAN(21, "fy", "Frysk", "Frisian", "🇳🇱", "fy"),
  KHMER(22, "km", "ខ្មែរ", "Khmer", "🇰🇭", "km"),
  GEORGIAN(23, "ka", "ქართული", "Georgian", "🇬🇪", "ka"),
  GUJARATI(24, "gu", "ગુજરાતી", "Gujarati", "🇮🇳", "gu"),
  KAZAKH(25, "kk", "Kazakh", "Kazakh", "🇰🇿", "kk"),
  HAITIAN_CREOLE(26, "ht", "Haitian Creole", "Haitian Creole", "🇭🇹", "ht"),
  KOREAN(27, "ko", "한국어", "Korean", "🇰🇷", "ko"),
  HAUSA(28, "ha", "Hausa", "Hausa", "🇳🇬", "ha"),
  DUTCH(29, "nl", "Nederlands", "Dutch", "🇳🇱", "nl"),
  KYRGYZ(30, "ky", "Кыргыз тили", "Kyrgyz", "🇰🇬", "ky"),
  GALICIAN(31, "gl", "Galego", "Galician", "🇪🇸", "gl"),
  CATALAN(32, "ca", "Català", "Catalan", "🇪🇸", "ca"),
  CZECH(33, "cs", "Čeština", "Czech", "🇨🇿", "cs"),
  KANNADA(34, "kn", "ಕನ್ನಡ", "Kannada", "🇮🇳", "kn"),
  CORSICAN(35, "co", "Corsa", "Corsican", "🇫🇷", "co"),
  CROATIAN(36, "hr", "Hrvatski", "Croatian", "🇭🇷", "hr"),
  KURDISH(37, "ku", "Kurdî", "Kurdish", "🇮🇶", "ku"),
  LATIN(38, "la", "Latina", "Latin", "🇻🇦", "la"),
  LATVIAN(39, "lv", "Latviešu", "Latvian", "🇱🇻", "lv"),
  LAO(40, "lo", "ລາວ", "Lao", "🇱🇦", "lo"),
  LITHUANIAN(41, "lt", "Lietuvių", "Lithuanian", "🇱🇹", "lt"),
  LUXEMBOURGISH(42, "lb", "Lëtzebuergesch", "Luxembourgish", "🇱🇺", "lb"),
  ROMANIAN(43, "ro", "Română", "Romanian", "🇷🇴", "ro"),
  MALAGASY(44, "mg", "Malagasy", "Malagasy", "🇲🇬", "mg"),
  MALTESE(45, "mt", "Il-Malti", "Maltese", "🇲🇹", "mt"),
  MARATHI(46, "mr", "मराठी", "Marathi", "🇮🇳", "mr"),
  MALAYALAM(47, "ml", "മലയാളം", "Malayalam", "🇮🇳", "ml"),
  MALAY(48, "ms", "Melayu", "Malay", "🇲🇾", "ms"),
  MACEDONIAN(49, "mk", "Македонски", "Macedonian", "🇲🇰", "mk"),
  MAORI(50, "mi", "Māori", "Maori", "🇳🇿", "mi"),
  MONGOLIAN(51, "mn", "Монгол хэл", "Mongolian", "🇲🇳", "mn"),
  BANGLA(52, "bn", "বাংল", "Bangla", "🇧🇩", "bn"),
  BURMESE(53, "my", "မြန်မာ", "Burmese", "🇲🇲", "my"),
  HMONG(54, "hmn", "Hmoob", "Hmong", "🇨🇳", "hmn"),
  XHOSA(55, "xh", "IsiXhosa", "Xhosa", "🇿🇦", "xh"),
  ZULU(56, "zu", "Zulu", "Zulu", "🇿🇦", "zu"),
  NEPALI(57, "ne", "नेपाली", "Nepali", "🇳🇵", "ne"),
  NORWEGIAN(58, "no", "Norsk", "Norwegian", "🇳🇴", "no"),
  PUNJABI(59, "pa", "ਪੰਜਾਬੀ", "Punjabi", "🇮🇳", "pa"),
  PORTUGUESE(60, "pt", "Português", "Portuguese", "🇵🇹", "pt"),
  PASHTO(61, "ps", "Pashto", "Pashto", "🇦🇫", "ps"),
  CHICHEWA(62, "ny", "Chichewa", "Chichewa", "🇲🇼", "ny"),
  JAPANESE(63, "ja", "日本語", "Japanese", "🇯🇵", "ja"),
  SWEDISH(64, "sv", "Svenska", "Swedish", "🇸🇪", "sv"),
  SAMOAN(65, "sm", "Samoa", "Samoan", "🇼🇸", "sm"),
  SERBIAN(66, "sr", "Српски", "Serbian", "🇷🇸", "sr"),
  SOTHO(67, "st", "Sesotho", "Sotho", "🇱🇸", "st"),
  SINHALA(68, "si", "සිංහල", "Sinhala", "🇱🇰", "si"),
  ESPERANTO(69, "eo", "Esperanta", "Esperanto", "🇺🇳", "eo"),
  SLOVAK(70, "sk", "Slovenčina", "Slovak", "🇸🇰", "sk"),
  SLOVENIAN(71, "sl", "Slovenščina", "Slovenian", "🇸🇮", "sl"),
  SWAHILI(72, "sw", "Kiswahili", "Swahili", "🇹🇿", "sw"),
  SCOTTISH_GAELIC(73, "gd", "Gàidhlig na h-Alba", "Scottish Gaelic", "🇬🇧", "gd"),
  CEBUANO(74, "ceb", "Cebuano", "Cebuano", "🇵🇭", "ceb"),
  SOMALI(75, "so", "Somali", "Somali", "🇸🇴", "so"),
  TAJIK(76, "tg", "Тоҷикӣ", "Tajik", "🇹🇯", "tg"),
  TELUGU(77, "te", "తెలుగు", "Telugu", "🇮🇳", "te"),
  TAMIL(78, "ta", "தமிழ்", "Tamil", "🇮🇳", "ta"),
  THAI(79, "th", "ไทย", "Thai", "🇹🇭", "th"),
  TURKISH(80, "tr", "Türkçe", "Turkish", "🇹🇷", "tr"),
  WELSH(81, "cy", "Cymraeg", "Welsh", "🇬🇧", "cy"),
  URDU(82, "ur", "اردو", "Urdu", "🇵🇰", "ur"),
  UKRAINIAN(83, "uk", "Українська", "Ukrainian", "🇺🇦", "uk"),
  UZBEK(84, "uz", "O'zbek", "Uzbek", "🇺🇿", "uz"),
  SPANISH(85, "es", "Español", "Spanish", "🇪🇸", "es"),
  HEBREW(86, "iw", "עברית", "Hebrew", "🇮🇱", "iw"),
  GREEK(87, "el", "Ελληνικά", "Greek", "🇬🇷", "el"),
  HAWAIIAN(88, "haw", "Hawaiian", "Hawaiian", "🇺🇸", "haw"),
  SINDHI(89, "sd", "سنڌي", "Sindhi", "🇵🇰", "sd"),
  HUNGARIAN(90, "hu", "Magyar", "Hungarian", "🇭🇺", "hu"),
  SHONA(91, "sn", "Shona", "Shona", "🇿🇼", "sn"),
  ARMENIAN(92, "hy", "Հայերեն", "Armenian", "🇦🇲", "hy"),
  IGBO(93, "ig", "Igbo", "Igbo", "🇳🇬", "ig"),
  ITALIAN(94, "it", "Italiano", "Italian", "🇮🇹", "it"),
  YIDDISH(95, "yi", "ייִדיש", "Yiddish", "🇮🇱", "yi"),
  HINDI(96, "hi", "हिंदी", "Hindi", "🇮🇳", "hi"),
  SUNDANESE(97, "su", "Sunda", "Sundanese", "🇮🇩", "su"),
  INDONESIAN(98, "id", "Indonesia", "Indonesian", "🇮🇩", "in"),
  JAVANESE(99, "jv", "Wong Jawa", "Javanese", "🇮🇩", "jv"),
  ENGLISH(100, "en", "English", "English", "🇺🇸", "en"),
  YORUBA(101, "yo", "Yorùbá", "Yoruba", "🇳🇬", "yo"),
  VIETNAMESE(102, "vi", "Tiếng Việt", "Vietnamese", "🇻🇳", "vi"),
  CHINESE_TRADITIONAL(103, "zh-rTW", "正體中文", "Chinese Traditional", "🇨🇳", "zh-rTW"),
  CHINESE_SIMPLIFIED(104, "zh-rCN", "简体中文", "Chinese Simplified", "🇨🇳", "zh-rCN"),
  ASSAMESE(105, "as", "Assamese", "Assamese", "🇮🇳", "as"),
  DARI(106, "prs", "Dari", "Dari", "🇦🇫", "prs"),
  FIJIAN(107, "fj", "Fijian", "Fijian", "🇫🇯", "fj"),
  HMONG_DAW(108, "mww", "Hmong Daw", "Hmong Daw", "🇨🇳", "mww"),
  INUKTITUT(109, "iu", "ᐃᓄᒃᑎᑐᑦ", "Inuktitut", "🇨🇦", "iu"),
  ODIA(112, "or", "Odia", "Odia", "🇮🇳", "or"),
  QUERETARO_OTOMI(113, "otq", "Querétaro Otomi", "Querétaro Otomi", "🇲🇽", "otq"),
  TAHITIAN(114, "ty", "Tahitian", "Tahitian", "🇵🇫", "ty"),
  TIGRINYA(115, "ti", "ትግርኛ", "Tigrinya", "🇪🇷", "ti"),
  TONGAN(116, "to", "lea fakatonga", "Tongan", "🇹🇴", "to"),
  YUCATEC_MAYA(117, "yua", "Yucatec Maya", "Yucatec Maya", "🇲🇽", "yua");

  companion object {
    fun languages(): List<Lang> {
      return Languages.entries.map { it.toLang() }
    }

    fun allSupportedLanguages(): List<Lang> {
      return Languages.entries.filter { it != AUTO }.map { it.toLang() }
    }
  }
}

fun Languages.toLang(): Lang {
  return Lang(
    id = this.id,
    code = this.code,
    name = this.displayName,
    englishName = this.englishName,
    flag = this.flag,
    directoryName = this.directoryName
  )
}

val Lang.flagEmoji: String?
  get() = flag.takeIf { it.isNotBlank() }

val Lang.valuesDirectoryQualifier: String?
  get() = directoryName.takeIf { it.isNotBlank() }
