package com.airsaid.localization.translate.lang

import java.util.Locale

private val LANGUAGE_FLAG_OVERRIDES: Map<String, String> = mapOf(
    "sq" to "\uD83C\uDDE6\uD83C\uDDF1", // 🇦🇱 Albania
    "ar" to "\uD83C\uDDF8\uD83C\uDDE6", // 🇸🇦 Saudi Arabia
    "am" to "\uD83C\uDDEA\uD83C\uDDF9", // 🇪🇹 Ethiopia
    "az" to "\uD83C\uDDE6\uD83C\uDDFF", // 🇦🇿 Azerbaijan
    "ga" to "\uD83C\uDDEE\uD83C\uDDEA", // 🇮🇪 Ireland
    "et" to "\uD83C\uDDEA\uD83C\uDDEA", // 🇪🇪 Estonia
    "eu" to "\uD83C\uDDEA\uD83C\uDDF8", // 🇪🇸 Spain (Basque Country)
    "be" to "\uD83C\uDDE7\uD83C\uDDFE", // 🇧🇾 Belarus
    "bg" to "\uD83C\uDDE7\uD83C\uDDEC", // 🇧🇬 Bulgaria
    "is" to "\uD83C\uDDEE\uD83C\uDDF8", // 🇮🇸 Iceland
    "pl" to "\uD83C\uDDF5\uD83C\uDDF1", // 🇵🇱 Poland
    "bs" to "\uD83C\uDDE7\uD83C\uDDE6", // 🇧🇦 Bosnia and Herzegovina
    "fa" to "\uD83C\uDDEE\uD83C\uDDF7", // 🇮🇷 Iran
    "af" to "\uD83C\uDDFF\uD83C\uDDE6", // 🇿🇦 South Africa
    "da" to "\uD83C\uDDE9\uD83C\uDDF0", // 🇩🇰 Denmark
    "de" to "\uD83C\uDDE9\uD83C\uDDEA", // 🇩🇪 Germany
    "ru" to "\uD83C\uDDF7\uD83C\uDDFA", // 🇷🇺 Russia
    "fr" to "\uD83C\uDDEB\uD83C\uDDF7", // 🇫🇷 France
    "fil" to "\uD83C\uDDF5\uD83C\uDDED", // 🇵🇭 Philippines
    "fi" to "\uD83C\uDDEB\uD83C\uDDEE", // 🇫🇮 Finland
    "fy" to "\uD83C\uDDF3\uD83C\uDDF1", // 🇳🇱 Netherlands
    "km" to "\uD83C\uDDF0\uD83C\uDDED", // 🇰🇭 Cambodia
    "ka" to "\uD83C\uDDEC\uD83C\uDDEA", // 🇬🇪 Georgia
    "gu" to "\uD83C\uDDEE\uD83C\uDDF3", // 🇮🇳 India (Gujarati)
    "kk" to "\uD83C\uDDF0\uD83C\uDDFF", // 🇰🇿 Kazakhstan
    "ht" to "\uD83C\uDDED\uD83C\uDDF9", // 🇭🇹 Haiti
    "ko" to "\uD83C\uDDF0\uD83C\uDDF7", // 🇰🇷 South Korea
    "ha" to "\uD83C\uDDF3\uD83C\uDDEC", // 🇳🇬 Nigeria
    "nl" to "\uD83C\uDDF3\uD83C\uDDF1", // 🇳🇱 Netherlands
    "ky" to "\uD83C\uDDF0\uD83C\uDDEC", // 🇰🇬 Kyrgyzstan
    "gl" to "\uD83C\uDDEA\uD83C\uDDF8", // 🇪🇸 Spain (Galicia)
    "ca" to "\uD83C\uDDEA\uD83C\uDDF8", // 🇪🇸 Spain (Catalonia)
    "cs" to "\uD83C\uDDE8\uD83C\uDDFF", // 🇨🇿 Czech Republic
    "kn" to "\uD83C\uDDEE\uD83C\uDDF3", // 🇮🇳 India (Kannada)
    "co" to "\uD83C\uDDEB\uD83C\uDDF7", // 🇫🇷 France (Corsica)
    "hr" to "\uD83C\uDDED\uD83C\uDDF7", // 🇭🇷 Croatia
    "ku" to "\uD83C\uDDEE\uD83C\uDDEC", // 🇮🇶 Iraq (Kurdish)
    "la" to "\uD83C\uDDFB\uD83C\uDDE6", // 🇻🇦 Vatican City
    "lv" to "\uD83C\uDDF1\uD83C\uDDFB", // 🇱🇻 Latvia
    "lo" to "\uD83C\uDDF1\uD83C\uDDE6", // 🇱🇦 Laos
    "lt" to "\uD83C\uDDF1\uD83C\uDDF9", // 🇱🇹 Lithuania
    "lb" to "\uD83C\uDDF1\uD83C\uDDEA", // 🇱🇺 Luxembourg
    "ro" to "\uD83C\uDDF7\uD83C\uDDF4", // 🇷🇴 Romania
    "mg" to "\uD83C\uDDF2\uD83C\uDDEC", // 🇲🇬 Madagascar
    "mt" to "\uD83C\uDDF2\uD83C\uDDF9", // 🇲🇹 Malta
    "mr" to "\uD83C\uDDEE\uD83C\uDDF3", // 🇮🇳 India (Marathi)
    "ml" to "\uD83C\uDDEE\uD83C\uDDF3", // 🇮🇳 India (Malayalam)
    "ms" to "\uD83C\uDDF2\uD83C\uDDFE", // 🇲🇾 Malaysia
    "mk" to "\uD83C\uDDF2\uD83C\uDDF0", // 🇲🇰 North Macedonia
    "mi" to "\uD83C\uDDF3\uD83C\uDDFF", // 🇳🇿 New Zealand (Maori)
    "mn" to "\uD83C\uDDF2\uD83C\uDDF3", // 🇲🇳 Mongolia
    "bn" to "\uD83C\uDDFA\uD83C\uDDEC", // 🇧🇩 Bangladesh
    "my" to "\uD83C\uDDF2\uD83C\uDDF2", // 🇲🇲 Myanmar
    "hmn" to "\uD83C\uDDE8\uD83C\uDDF3", // 🇨🇳 China (Hmong)
    "xh" to "\uD83C\uDDFF\uD83C\uDDE6", // 🇿🇦 South Africa
    "zu" to "\uD83C\uDDFF\uD83C\uDDE6", // 🇿🇦 South Africa
    "ne" to "\uD83C\uDDF3\uD83C\uDDF5", // 🇳🇵 Nepal
    "no" to "\uD83C\uDDF3\uD83C\uDDF4", // 🇳🇴 Norway
    "pa" to "\uD83C\uDDEE\uD83C\uDDF3", // 🇮🇳 India (Punjabi)
    "pt" to "\uD83C\uDDF5\uD83C\uDDF9", // 🇵🇹 Portugal
    "ps" to "\uD83C\uDDE6\uD83C\uDDEB", // 🇦🇫 Afghanistan
    "ny" to "\uD83C\uDDF2\uD83C\uDDFC", // 🇲🇼 Malawi
    "ja" to "\uD83C\uDDEF\uD83C\uDDF5", // 🇯🇵 Japan
    "sv" to "\uD83C\uDDF8\uD83C\uDDEA", // 🇸🇪 Sweden
    "sm" to "\uD83C\uDDFC\uD83C\uDDF8", // 🇼🇸 Samoa
    "sr" to "\uD83C\uDDF7\uD83C\uDDF8", // 🇷🇸 Serbia
    "st" to "\uD83C\uDDF1\uD83C\uDDF8", // 🇱🇸 Lesotho
    "si" to "\uD83C\uDDF1\uD83C\uDDF0", // 🇱🇰 Sri Lanka
    "eo" to "\uD83C\uDDFA\uD83C\uDDF3", // 🇺🇳 United Nations flag approximation
    "sk" to "\uD83C\uDDF8\uD83C\uDDF0", // 🇸🇰 Slovakia
    "sl" to "\uD83C\uDDF8\uD83C\uDDEE", // 🇸🇮 Slovenia
    "sw" to "\uD83C\uDDF9\uD83C\uDDF5", // 🇹🇿 Tanzania
    "gd" to "\uD83C\uDDEC\uD83C\uDDE7", // 🇬🇧 United Kingdom (Scottish Gaelic)
    "ceb" to "\uD83C\uDDF5\uD83C\uDDED", // 🇵🇭 Philippines
    "so" to "\uD83C\uDDF8\uD83C\uDDF4", // 🇸🇴 Somalia
    "tg" to "\uD83C\uDDF9\uD83C\uDDEF", // 🇹🇯 Tajikistan
    "te" to "\uD83C\uDDEE\uD83C\uDDF3", // 🇮🇳 India (Telugu)
    "ta" to "\uD83C\uDDEE\uD83C\uDDF3", // 🇮🇳 India (Tamil)
    "th" to "\uD83C\uDDF9\uD83C\uDDED", // 🇹🇭 Thailand
    "tr" to "\uD83C\uDDF9\uD83C\uDDF7", // 🇹🇷 Turkey
    "cy" to "\uD83C\uDDEC\uD83C\uDDE7", // 🇬🇧 United Kingdom (Welsh)
    "ur" to "\uD83C\uDDF5\uD83C\uDDF0", // 🇵🇰 Pakistan
    "uk" to "\uD83C\uDDFA\uD83C\uDDE6", // 🇺🇦 Ukraine
    "uz" to "\uD83C\uDDFA\uD83C\uDDFF", // 🇺🇿 Uzbekistan
    "es" to "\uD83C\uDDEA\uD83C\uDDF8", // 🇪🇸 Spain
    "iw" to "\uD83C\uDDEE\uD83C\uDDF1", // 🇮🇱 Israel
    "el" to "\uD83C\uDDEC\uD83C\uDDF7", // 🇬🇷 Greece
    "haw" to "\uD83C\uDDFA\uD83C\uDDF8", // 🇺🇸 United States (Hawaii)
    "sd" to "\uD83C\uDDF5\uD83C\uDDF0", // 🇵🇰 Pakistan (Sindhi)
    "hu" to "\uD83C\uDDED\uD83C\uDDFA", // 🇭🇺 Hungary
    "sn" to "\uD83C\uDDFF\uD83C\uDDFC", // 🇿🇼 Zimbabwe
    "hy" to "\uD83C\uDDE6\uD83C\uDDF2", // 🇦🇲 Armenia
    "ig" to "\uD83C\uDDF3\uD83C\uDDEC", // 🇳🇬 Nigeria
    "it" to "\uD83C\uDDEE\uD83C\uDDF9", // 🇮🇹 Italy
    "yi" to "\uD83C\uDDEE\uD83C\uDDF1", // 🇮🇱 Israel
    "hi" to "\uD83C\uDDEE\uD83C\uDDF3", // 🇮🇳 India
    "su" to "\uD83C\uDDEE\uD83C\uDDE9", // 🇮🇩 Indonesia
    "jv" to "\uD83C\uDDEE\uD83C\uDDE9", // 🇮🇩 Indonesia
    "en" to "\uD83C\uDDFA\uD83C\uDDF8", // 🇺🇸 United States
    "yo" to "\uD83C\uDDF3\uD83C\uDDEC", // 🇳🇬 Nigeria
    "vi" to "\uD83C\uDDFB\uD83C\uDDF3", // 🇻🇳 Vietnam
    "as" to "\uD83C\uDDEE\uD83C\uDDF3", // 🇮🇳 India (Assamese)
    "prs" to "\uD83C\uDDE6\uD83C\uDDEB", // 🇦🇫 Afghanistan (Dari)
    "fj" to "\uD83C\uDDEB\uD83C\uDDEF", // 🇫🇯 Fiji
    "mww" to "\uD83C\uDDE8\uD83C\uDDF3", // 🇨🇳 China (Hmong Daw)
    "iu" to "\uD83C\uDDE8\uD83C\uDDE6", // 🇨🇦 Canada (Inuktitut)
    "or" to "\uD83C\uDDEE\uD83C\uDDF3", // 🇮🇳 India (Odia)
    "otq" to "\uD83C\uDDF2\uD83C\uDDFD", // 🇲🇽 Mexico (Querétaro Otomi)
    "ty" to "\uD83C\uDDF5\uD83C\uDDEB", // 🇵🇫 French Polynesia (Tahitian)
    "ti" to "\uD83C\uDDEA\uD83C\uDDF7", // 🇪🇷 Eritrea (Tigrinya)
    "to" to "\uD83C\uDDF9\uD83C\uDDF4", // 🇹🇴 Tonga
    "yua" to "\uD83C\uDDF2\uD83C\uDDFD" // 🇲🇽 Mexico (Yucatec Maya)
)

private val REGION_CODE_PATTERN = Regex("(?:-|_)(?:r)?([A-Za-z]{2})$")

val Lang.flagEmoji: String?
    get() {
        extractRegion(code)?.let { region ->
            regionToFlag(region)?.let { return it }
        }

        val translation = translationCode
        extractRegion(translation)?.let { region ->
            regionToFlag(region)?.let { return it }
        }

        val normalizedCode = code.lowercase(Locale.US)
        LANGUAGE_FLAG_OVERRIDES[normalizedCode]?.let { return it }

        val normalizedTranslation = translation.lowercase(Locale.US)
        LANGUAGE_FLAG_OVERRIDES[normalizedTranslation]?.let { return it }

        return null
    }

private fun extractRegion(value: String?): String? {
    if (value.isNullOrBlank()) return null
    val match = REGION_CODE_PATTERN.find(value)
    return match?.groupValues?.getOrNull(1)?.uppercase(Locale.US)
}

private fun regionToFlag(region: String): String? {
    val code = region.uppercase(Locale.US)
    if (code.length != 2 || code.any { it !in 'A'..'Z' }) return null
    return buildString {
        append(Character.toChars(0x1F1E6 + (code[0].code - 'A'.code)))
        append(Character.toChars(0x1F1E6 + (code[1].code - 'A'.code)))
    }
}
