package data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
public enum SupportLanguage {
    Afrikaans("af", "Afrikaans", "Afrikaans"),
    Albanian("sq", "Shqiptar", "Albanian"),
    Arabic("ar", "العربية", "Arabic"),
    Azerbaijani("az", "Azərbaycan", "Azerbaijani"),
    Basque("eu", "Euskal", "Basque"),
    Bengali("bn", "বাঙালি", "Bengali"),
    Belarusian("be", "Беларускі", "Belarusian"),
    Bulgarian("bg", "Български", "Bulgarian"),
    Catalan("ca", "Català", "Catalan"),
    Chinese_Simplified("zh-CN", "简体中文", "Chinese Simplified"),
    Chinese_Traditional("zh-TW", "正體中文", "Chinese Traditional"),
    Croatian("hr", "Hrvatski", "Croatian"),
    Czech("cs", "Čeština", "Czech"),
    Danish("da", "Dansk", "Danish"),
    Dutch("nl", "Nederlands", "Dutch"),
    English("en", "English", "English"),
    Esperanto("eo", "Esperanta", "Esperanto"),
    Estonian("et", "Eesti", "Estonian"),
    Filipino("tl", "Pilipino", "Filipino"),
    Finnish("fi", "Suomi", "Finnish"),
    French("fr", "Français", "French"),
    Galician("gl", "Galego", "Galician"),
    Georgian("ka", "ქართული", "Georgian"),
    German("de", "Deutsch", "German"),
    Greek("el", "Ελληνικά", "Greek"),
    Gujarati("gu", "ગુજરાતી", "Gujarati"),
    Haitian_Creole("ht", "Haitiancreole", "Haitian Creole"),
    Hebrew("iw", "עברית", "Hebrew"),
    Hindi("hi", "हिंदी", "Hindi"),
    Hungarian("hu", "Magyar", "Hungarian"),
    Icelandic("is", "Icelandic", "Icelandic"),
    Indonesian("id", "Indonesia", "Indonesian"),
    Irish("ga", "Irish", "Irish"),
    Italian("it", "Italiano", "Italian"),
    Japanese("ja", "日本語", "Japanese"),
    Kannada("kn", "ಕನ್ನಡ", "Kannada"),
    Korean("ko", "한국의", "Korean"),
    Latin("la", "Latina", "Latin"),
    Latvian("lv", "Latvijas", "Latvian"),
    Lithuanian("lt", "Lietuvos", "Lithuanian"),
    Macedonian("mk", "Македонски", "Macedonian"),
    Malay("ms", "Melayu", "Malay"),
    Maltese("mt", "Malti", "Maltese"),
    Norwegian("no", "Norsk", "Norwegian"),
    Persian("fa", "فارسی", "Persian"),
    Polish("pl", "Polski", "Polish"),
    Portuguese("pt", "Português", "Portuguese"),
    Romanian("ro", "Român", "Romanian"),
    Russian("ru", "Русский", "Russian"),
    Serbian("sr", "Српски", "Serbian"),
    Slovak("sk", "Slovenčina", "Slovak"),
    Slovenian("sl", "Slovenščina", "Slovenian"),
    Spanish("es", "Español", "Spanish"),
    SwahiliSwahili("sw", "Kiswahili", "Swahili"),
    Swedish("sv", "Svenska", "Swedish"),
    Tamil("ta", "தமிழ்", "Tamil"),
    Telugu("te", "తెలుగు", "Telugu"),
    Thai("th", "ไทย", "Thai"),
    Turkish("tr", "Türk", "Turkish"),
    Ukrainian("uk", "Український", "Ukrainian"),
    Urdu("ur", "اردو", "Urdu"),
    Vietnamese("vi", "Tiếng Việt", "Vietnamese"),
    Welsh("cy", "Cymraeg", "Welsh"),
    Yiddish("yi", "ייִדיש", "Yiddish");

    private String code;
    private String name;
    private String englishName;

    SupportLanguage(String code, String name, String englishName) {
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

    public static List<SupportLanguage> getSupportLanguage() {
        List<SupportLanguage> data = new ArrayList<>();
        data.add(Afrikaans);
        data.add(Albanian);
        data.add(Arabic);
        data.add(Azerbaijani);
        data.add(Basque);
        data.add(Bengali);
        data.add(Belarusian);
        data.add(Bulgarian);
        data.add(Catalan);
        data.add(Chinese_Simplified);
        data.add(Chinese_Traditional);
        data.add(Croatian);
        data.add(Czech);
        data.add(Danish);
        data.add(Dutch);
        data.add(English);
        data.add(Esperanto);
        data.add(Estonian);
        data.add(Filipino);
        data.add(Finnish);
        data.add(French);
        data.add(Galician);
        data.add(Georgian);
        data.add(German);
        data.add(Greek);
        data.add(Gujarati);
        data.add(Haitian_Creole);
        data.add(Hebrew);
        data.add(Hindi);
        data.add(Hungarian);
        data.add(Icelandic);
        data.add(Indonesian);
        data.add(Irish);
        data.add(Italian);
        data.add(Japanese);
        data.add(Kannada);
        data.add(Korean);
        data.add(Latin);
        data.add(Latvian);
        data.add(Lithuanian);
        data.add(Macedonian);
        data.add(Malay);
        data.add(Maltese);
        data.add(Norwegian);
        data.add(Persian);
        data.add(Polish);
        data.add(Portuguese);
        data.add(Romanian);
        data.add(Russian);
        data.add(Serbian);
        data.add(Slovak);
        data.add(Slovenian);
        data.add(Spanish);
        data.add(SwahiliSwahili);
        data.add(Swedish);
        data.add(Tamil);
        data.add(Telugu);
        data.add(Thai);
        data.add(Turkish);
        data.add(Ukrainian);
        data.add(Urdu);
        data.add(Vietnamese);
        data.add(Welsh);
        data.add(Yiddish);
        return data;
    }

    @Override
    public String toString() {
        return "SupportLanguage{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", englishName='" + englishName + '\'' +
                '}';
    }

}
