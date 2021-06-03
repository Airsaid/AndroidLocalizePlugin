package com.airsaid.localization.translate.lang;

/**
 * All supported languages.
 *
 * @author airsaid
 */
// Some language codes and names cannot pass the compiler check
@SuppressWarnings(value = {"SpellCheckingInspection", "unused"})
public enum Lang {
  AUTO(0, "auto", "Auto", "Auto"),
  ALBANIAN(1, "sq", "Shqiptar", "Albanian"),
  ARABIC(2, "ar", "العربية", "Arabic"),
  AMHARIC(3, "am", "አማርኛ", "Amharic"),
  AZERBAIJANI(4, "az", "Azərbaycan", "Azerbaijani"),
  IRISH(5, "ga", "Irish", "Irish"),
  ESTONIAN(6, "et", "Eesti keel", "Estonian"),
  BASQUE(7, "eu", "Euskal", "Basque"),
  BELARUSIAN(8, "be", "беларускі", "Belarusian"),
  BULGARIAN(9, "bg", "Български", "Bulgarian"),
  ICELANDIC(10, "is", "Íslensku", "Icelandic"),
  POLISH(11, "pl", "Polski", "Polish"),
  BOSNIAN(12, "bs", "Bosanski", "Bosnian"),
  PERSIAN(13, "fa", "فارسی", "Persian"),
  AFRIKAANS(14, "af", "Afrikaans", "Afrikaans"),
  DANISH(15, "da", "Dansk", "Danish"),
  GERMAN(16, "de", "Deutsch", "German"),
  RUSSIAN(17, "ru", "Русский", "Russian"),
  FRENCH(18, "fr", "Français", "French"),
  FILIPINO(19, "tl", "Pilipino", "Filipino"),
  FINNISH(20, "fi", "Suomalainen", "Finnish"),
  FRISIAN(21, "fy", "Frysk", "Frisian"),
  KHMER(22, "km", "ភាសាខ្មែរ", "Khmer"),
  GEORGIAN(23, "ka", "ქართული", "Georgian"),
  GUJARATI(24, "gu", "ગુજરાતી", "Gujarati"),
  KAZAKH(25, "kk", "Қазақша", "Kazakh"),
  HAITIAN_CREOLE(26, "ht", "Kreyòl Ayisyen", "Haitian Creole"),
  KOREAN(27, "ko", "한국어", "Korean"),
  HAUSA(28, "ha", "Hausa", "Hausa"),
  DUTCH(29, "nl", "Nederlands", "Dutch"),
  KYRGYZ(30, "ky", "Кыргыз тили", "Kyrgyz"),
  GALICIAN(31, "gl", "Galego", "Galician"),
  CATALAN(32, "ca", "Català", "Catalan"),
  CZECH(33, "cs", "Česky", "Czech"),
  KANNADA(34, "kn", "ಕನ್ನಡ", "Kannada"),
  CORSICAN(35, "co", "Corsa", "Corsican"),
  CROATIAN(36, "hr", "hrvatski", "Croatian"),
  KURDISH(37, "ku", "Kurdî", "Kurdish"),
  LATIN(38, "la", "Latina", "Latin"),
  LATVIAN(39, "lv", "Latviešu", "Latvian"),
  LAOTIAN(40, "lo", "Laotian", "Laotian"),
  LITHUANIAN(41, "lt", "Lietuviškai", "Lithuanian"),
  LUXEMBOURGISH(42, "lb", "Lëtzebuergesch", "Luxembourgish"),
  ROMANIAN(43, "ro", "Românesc", "Romanian"),
  MALAGASY(44, "mg", "Malagasy", "Malagasy"),
  MALTESE(45, "mt", "Malti", "Maltese"),
  MARATHI(46, "mr", "मराठी", "Marathi"),
  MALAYALAM(47, "ml", "മലയാളം", "Malayalam"),
  MALAY(48, "ms", "Melayu", "Malay"),
  MACEDONIAN(49, "mk", "Македонски", "Macedonian"),
  MAORI(50, "mi", "Maori", "Maori"),
  MONGOLIAN(51, "mn", "Монгол хэл", "Mongolian"),
  BENGALI(52, "bn", "বাংলা ভাষার", "Bengali"),
  BURMESE(53, "my", "မြန်မာ", "Burmese"),
  HMONG(54, "hmn", "Hmoob", "Hmong"),
  XHOSA(55, "xh", "IsiXhosa", "Xhosa"),
  ZULU(56, "zu", "Zulu", "Zulu"),
  NEPALI(57, "ne", "नेपाली", "Nepali"),
  NORWEGIAN(58, "no", "Norsk språk", "Norwegian"),
  PUNJABI(59, "pa", "ਪੰਜਾਬੀ", "Punjabi"),
  PORTUGUESE(60, "pt", "Português", "Portuguese"),
  PASHTO(61, "ps", "پښتو", "Pashto"),
  CHICHEWA(62, "ny", "Chichewa", "Chichewa"),
  JAPANESE(63, "ja", "日本語", "Japanese"),
  SWEDISH(64, "sv", "Svenska", "Swedish"),
  SAMOAN(65, "sm", "Samoa", "Samoan"),
  SERBIAN(66, "sr", "Српски", "Serbian"),
  SOTHO(67, "st", "Sesotho", "Sotho"),
  SINHALA(68, "si", "සිංහල", "Sinhala"),
  ESPERANTO(69, "eo", "Esperanta", "Esperanto"),
  SLOVAK(70, "sk", "Slovenčina", "Slovak"),
  SLOVENIAN(71, "sl", "Slovenščina", "Slovenian"),
  SWAHILI_SWAHILI(72, "sw", "Kiswahili", "Swahili"),
  SCOTTISH_GAELIC(73, "gd", "Gàidhlig na h-Alba", "Scottish Gaelic"),
  CEBUANO(74, "ceb", "Cebuano", "Cebuano"),
  SOMALI(75, "so", "Somali", "Somali"),
  TAJIK(76, "tg", "Тоҷикӣ", "Tajik"),
  TELUGU(77, "te", "తెలుగు", "Telugu"),
  TAMIL(78, "ta", "தமிழ்", "Tamil"),
  THAI(79, "th", "ไทย", "Thai"),
  TURKISH(80, "tr", "Türk", "Turkish"),
  WELSH(81, "cy", "Cymraeg", "Welsh"),
  URDU(82, "ur", "اردو", "Urdu"),
  UKRAINIAN(83, "uk", "Український", "Ukrainian"),
  UZBEK(84, "uz", "O'zbek", "Uzbek"),
  SPANISH(85, "es", "Español", "Spanish"),
  HEBREW(86, "iw", "עברית", "Hebrew"),
  GREEK(87, "el", "Ελληνικά", "Greek"),
  HAWAIIAN(88, "haw", "Hawaiian", "Hawaiian"),
  SINDHI(89, "sd", "سنڌي", "Sindhi"),
  HUNGARIAN(90, "hu", "Magyar", "Hungarian"),
  SHONA(91, "sn", "Shona", "Shona"),
  ARMENIAN(92, "hy", "Հայերեն", "Armenian"),
  IGBO(93, "ig", "Igbo", "Igbo"),
  ITALIAN(94, "it", "Italiano", "Italian"),
  YIDDISH(95, "yi", "ייִדיש", "Yiddish"),
  HINDI(96, "hi", "हिंदी", "Hindi"),
  SUNDANESE(97, "su", "Sunda", "Sundanese"),
  INDONESIAN(98, "id", "Indonesia", "Indonesian"),
  JAVANESE(99, "jw", "Wong Jawa", "Javanese"),
  ENGLISH(100, "en", "English", "English"),
  YORUBA(101, "yo", "Yorùbá", "Yoruba"),
  VIETNAMESE(102, "vi", "Tiếng Việt", "Vietnamese"),
  CHINESE_TRADITIONAL(103, "zh-TW", "正體中文", "Chinese Traditional"),
  CHINESE_SIMPLIFIED(104, "zh-CN", "简体中文", "Chinese Simplified");

  private final int id;
  private String code;
  private String name;
  private String englishName;

  Lang(int id, String code, String name, String englishName) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.englishName = englishName;
  }

  public int getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public Lang setCode(String code) {
    this.code = code;
    return this;
  }

  public String getName() {
    return name;
  }

  public Lang setName(String name) {
    this.name = name;
    return this;
  }

  public String getEnglishName() {
    return englishName;
  }

  public Lang setEnglishName(String englishName) {
    this.englishName = englishName;
    return this;
  }

  @Override
  public String toString() {
    return "Lang{" +
        "id=" + id +
        ", code='" + code + '\'' +
        ", name='" + name + '\'' +
        ", englishName='" + englishName + '\'' +
        '}';
  }
}