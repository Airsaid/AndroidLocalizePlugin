package com.airsaid.localization.translate.trans.impl;

import com.airsaid.localization.config.PluginConfig;
import com.airsaid.localization.translate.lang.Lang;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import com.airsaid.localization.translate.trans.AbstractTranslator;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class GoogleTranslator extends AbstractTranslator {

    private static final String url = "https://translate.google.cn/translate_a/single";

    public GoogleTranslator() {
        super(url);
    }

    @Override
    public void setLangSupport() {
        langData.add(Lang.ALBANIAN);
        langData.add(Lang.ARABIC);
        langData.add(Lang.AMHARIC);
        langData.add(Lang.AZERBAIJANI);
        langData.add(Lang.IRISH);
        langData.add(Lang.ESTONIAN);
        langData.add(Lang.BASQUE);
        langData.add(Lang.BELARUSIAN);
        langData.add(Lang.BULGARIAN);
        langData.add(Lang.ICELANDIC);
        langData.add(Lang.POLISH);
        langData.add(Lang.BOSNIAN);
        langData.add(Lang.PERSIAN);
        langData.add(Lang.AFRIKAANS);
        langData.add(Lang.DANISH);
        langData.add(Lang.GERMAN);
        langData.add(Lang.RUSSIAN);
        langData.add(Lang.FRENCH);
        langData.add(Lang.FILIPINO);
        langData.add(Lang.FINNISH);
        langData.add(Lang.FRISIAN);
        langData.add(Lang.KHMER);
        langData.add(Lang.GEORGIAN);
        langData.add(Lang.GUJARATI);
        langData.add(Lang.KAZAKH);
        langData.add(Lang.HAITIAN_CREOLE);
        langData.add(Lang.KOREAN);
        langData.add(Lang.HAUSA);
        langData.add(Lang.DUTCH);
        langData.add(Lang.KYRGYZ);
        langData.add(Lang.GALICIAN);
        langData.add(Lang.CATALAN);
        langData.add(Lang.CZECH);
        langData.add(Lang.KANNADA);
        langData.add(Lang.CORSICAN);
        langData.add(Lang.CROATIAN);
        langData.add(Lang.KURDISH);
        langData.add(Lang.LATIN);
        langData.add(Lang.LATVIAN);
        langData.add(Lang.LAOTIAN);
        langData.add(Lang.LITHUANIAN);
        langData.add(Lang.LUXEMBOURGISH);
        langData.add(Lang.ROMANIAN);
        langData.add(Lang.MALAGASY);
        langData.add(Lang.MALTESE);
        langData.add(Lang.MARATHI);
        langData.add(Lang.MALAYALAM);
        langData.add(Lang.MALAY);
        langData.add(Lang.MACEDONIAN);
        langData.add(Lang.MAORI);
        langData.add(Lang.MONGOLIAN);
        langData.add(Lang.BENGALI);
        langData.add(Lang.BURMESE);
        langData.add(Lang.HMONG);
        langData.add(Lang.XHOSA);
        langData.add(Lang.ZULU);
        langData.add(Lang.NEPALI);
        langData.add(Lang.NORWEGIAN);
        langData.add(Lang.PUNJABI);
        langData.add(Lang.PORTUGUESE);
        langData.add(Lang.PASHTO);
        langData.add(Lang.CHICHEWA);
        langData.add(Lang.JAPANESE);
        langData.add(Lang.SWEDISH);
        langData.add(Lang.SAMOAN);
        langData.add(Lang.SERBIAN);
        langData.add(Lang.SOTHO);
        langData.add(Lang.SINHALA);
        langData.add(Lang.ESPERANTO);
        langData.add(Lang.SLOVAK);
        langData.add(Lang.SLOVENIAN);
        langData.add(Lang.SWAHILI_SWAHILI);
        langData.add(Lang.SCOTTISH_GAELIC);
        langData.add(Lang.CEBUANO);
        langData.add(Lang.SOMALI);
        langData.add(Lang.TAJIK);
        langData.add(Lang.TELUGU);
        langData.add(Lang.TAMIL);
        langData.add(Lang.THAI);
        langData.add(Lang.TURKISH);
        langData.add(Lang.WELSH);
        langData.add(Lang.URDU);
        langData.add(Lang.UKRAINIAN);
        langData.add(Lang.UZBEK);
        langData.add(Lang.SPANISH);
        langData.add(Lang.HEBREW);
        langData.add(Lang.GREEK);
        langData.add(Lang.HAWAIIAN);
        langData.add(Lang.SINDHI);
        langData.add(Lang.HUNGARIAN);
        langData.add(Lang.SHONA);
        langData.add(Lang.ARMENIAN);
        langData.add(Lang.IGBO);
        langData.add(Lang.ITALIAN);
        langData.add(Lang.YIDDISH);
        langData.add(Lang.HINDI);
        langData.add(Lang.SUNDANESE);
        langData.add(Lang.INDONESIAN);
        langData.add(Lang.JAVANESE);
        langData.add(Lang.ENGLISH);
        langData.add(Lang.YORUBA);
        langData.add(Lang.VIETNAMESE);
        langData.add(Lang.CHINESE_TRADITIONAL);
        langData.add(Lang.CHINESE_SIMPLIFIED);
    }

    @Override
    public void setFormData(Lang from, Lang to, String text) {
        formData.put("client", "t");
        formData.put("sl", from.getCode());
        formData.put("tl", to.getCode());
        formData.put("hl", "zh-CN");
        formData.put("dt", "t");
        formData.put("ie", "UTF-8");
        formData.put("oe", "UTF-8");
        formData.put("tk", token(text));
        formData.put("q", text);
    }

    @Override
    public String query() throws Exception {
        URIBuilder uri = new URIBuilder(url);
        for (String key : formData.keySet()) {
            String value = formData.get(key);
            uri.addParameter(key, value);
        }
        HttpGet request = new HttpGet(uri.toString());

        RequestConfig.Builder builder = RequestConfig.copy(RequestConfig.DEFAULT)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000);

        if (PluginConfig.isEnableProxy()) {
            HttpHost proxy = new HttpHost(PluginConfig.getHostName(), PluginConfig.getPortNumber());
            builder.setProxy(proxy);
        }

        RequestConfig config = builder.build();
        request.setConfig(config);
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        String result = EntityUtils.toString(entity, "UTF-8");
        EntityUtils.consume(entity);
        response.getEntity().getContent().close();
        response.close();

        return result;
    }

    @Override
    public String parses(String text) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(text).get(0).get(0).get(0).textValue();
    }

    private String token(String text) {
        String tk = "";
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        try (InputStream inputStream = getClass().getResourceAsStream("/tk/Google.js")) {
            engine.eval(new InputStreamReader(inputStream));

            if (engine instanceof Invocable) {
                Invocable invoke = (Invocable) engine;
                tk = String.valueOf(invoke.invokeFunction("token", text));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tk;
    }
}
