package translate.lang;

/**
 * @author airsaid
 */
public enum LANG {
    Albanian("sq", "Shqiptar", "Albanian"),
    Arabic("ar", "العربية", "Arabic"),
    Amharic("am", "አማርኛ", "Amharic"),
    Azerbaijani("az", "Azərbaycan", "Azerbaijani"),
    Irish("ga", "Irish", "Irish"),
    Estonian("et", "Eesti keel", "Estonian"),
    Basque("eu", "Euskal", "Basque"),
    Belarusian("be", "беларускі", "Belarusian"),
    Bulgarian("bg", "Български", "Bulgarian"),
    Icelandic("is", "Íslensku", "Icelandic"),
    Polish("pl", "Polski", "Polish"),
    Bosnian("bs", "Bosanski", "Bosnian"),
    Persian("fa", "فارسی", "Persian"),
    Afrikaans("af", "Afrikaans", "Afrikaans"),
    Danish("da", "Dansk", "Danish"),
    German("de", "Deutsch", "German"),
    Russian("ru", "Русский", "Russian"),
    French("fr", "Français", "French"),
    Filipino("tl", "Pilipino", "Filipino"),
    Finnish("fi", "Suomalainen", "Finnish"),
    Frisian("fy", "Frysk", "Frisian"),
    Khmer("km", "ភាសាខ្មែរ", "Khmer"),
    Georgian("ka", "ქართული", "Georgian"),
    Gujarati("gu", "ગુજરાતી", "Gujarati"),
    Kazakh("kk", "Қазақша", "Kazakh"),
    HaitianCreole("ht", "Kreyòl Ayisyen", "Haitian Creole"),
    Korean("ko", "한국어", "Korean"),
    Hausa("ha", "Hausa", "Hausa"),
    Dutch("nl", "Nederlands", "Dutch"),
    Kyrgyz("ky", "Кыргыз тили", "Kyrgyz"),
    Galician("gl", "Galego", "Galician"),
    Catalan("ca", "Català", "Catalan"),
    Czech("cs", "Česky", "Czech"),
    Kannada("kn", "ಕನ್ನಡ", "Kannada"),
    Corsican("co", "Corsa", "Corsican"),
    Croatian("hr", "hrvatski", "Croatian"),
    Kurdish("ku", "Kurdî", "Kurdish"),
    Latin("la", "Latina", "Latin"),
    Latvian("lv", "Latviešu", "Latvian"),
    Laotian("lo", "Laotian", "Laotian"),
    Lithuanian("lt", "Lietuviškai", "Lithuanian"),
    Luxembourgish("lb", "Lëtzebuergesch", "Luxembourgish"),
    Romanian("ro", "Românesc", "Romanian"),
    Malagasy("mg", "Malagasy", "Malagasy"),
    Maltese("mt", "Malti", "Maltese"),
    Marathi("mr", "मराठी", "Marathi"),
    Malayalam("ml", "മലയാളം", "Malayalam"),
    Malay("ms", "Melayu", "Malay"),
    Macedonian("mk", "Македонски", "Macedonian"),
    Maori("mi", "Maori", "Maori"),
    Mongolian("mn", "Монгол хэл", "Mongolian"),
    Bengali("bn", "বাংলা ভাষার", "Bengali"),
    Burmese("my", "မြန်မာ", "Burmese"),
    Hmong("hmn", "Hmoob", "Hmong"),
    Xhosa("xh", "IsiXhosa", "Xhosa"),
    Zulu("zu", "Zulu", "Zulu"),
    Nepali("ne", "नेपाली", "Nepali"),
    Norwegian("no", "Norsk språk", "Norwegian"),
    Punjabi("pa", "ਪੰਜਾਬੀ", "Punjabi"),
    Portuguese("pt", "Português", "Portuguese"),
    Pashto("ps", "پښتو", "Pashto"),
    Chichewa("ny", "Chichewa", "Chichewa"),
    Japanese("ja", "日本語", "Japanese"),
    Swedish("sv", "Svenska", "Swedish"),
    Samoan("sm", "Samoa", "Samoan"),
    Serbian("sr", "Српски", "Serbian"),
    Sotho("st", "Sesotho", "Sotho"),
    Sinhala("si", "සිංහල", "Sinhala"),
    Esperanto("eo", "Esperanta", "Esperanto"),
    Slovak("sk", "Slovenčina", "Slovak"),
    Slovenian("sl", "Slovenščina", "Slovenian"),
    SwahiliSwahili("sw", "Kiswahili", "Swahili"),
    ScottishGaelic("gd", "Gàidhlig na h-Alba", "Scottish Gaelic"),
    Cebuano("ceb", "Cebuano", "Cebuano"),
    Somali("so", "Somali", "Somali"),
    Tajik("tg", "Тоҷикӣ", "Tajik"),
    Telugu("te", "తెలుగు", "Telugu"),
    Tamil("ta", "தமிழ்", "Tamil"),
    Thai("th", "ไทย", "Thai"),
    Turkish("tr", "Türk", "Turkish"),
    Welsh("cy", "Cymraeg", "Welsh"),
    Urdu("ur", "اردو", "Urdu"),
    Ukrainian("uk", "Український", "Ukrainian"),
    Uzbek("uz", "O'zbek", "Uzbek"),
    Spanish("es", "Español", "Spanish"),
    Hebrew("iw", "עברית", "Hebrew"),
    Greek("el", "Ελληνικά", "Greek"),
    Hawaiian("haw", "Hawaiian", "Hawaiian"),
    Sindhi("sd", "سنڌي", "Sindhi"),
    Hungarian("hu", "Magyar", "Hungarian"),
    Shona("sn", "Shona", "Shona"),
    Armenian("hy", "Հայերեն", "Armenian"),
    Igbo("ig", "Igbo", "Igbo"),
    Italian("it", "Italiano", "Italian"),
    Yiddish("yi", "ייִדיש", "Yiddish"),
    Hindi("hi", "हिंदी", "Hindi"),
    Sundanese("su", "Sunda", "Sundanese"),
    Indonesian("id", "Indonesia", "Indonesian"),
    Javanese("jw", "Wong Jawa", "Javanese"),
    English("en", "English", "English"),
    Yoruba("yo", "Yorùbá", "Yoruba"),
    Vietnamese("vi", "Tiếng Việt", "Vietnamese"),
    ChineseTraditional("zh-TW", "正體中文", "Chinese Traditional"),
    ChineseSimplified("zh-CN", "简体中文", "Chinese Simplified");

    private String code;
    private String name;
    private String englishName;

    LANG(String code, String name, String englishName) {
        this.code = code;
        this.name = name;
        this.englishName = englishName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    @Override
    public String toString() {
        return "LANG{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", englishName='" + englishName + '\'' +
                '}';
    }

}