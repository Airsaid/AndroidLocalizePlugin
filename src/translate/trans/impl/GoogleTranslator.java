package translate.trans.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import config.PluginConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import translate.lang.LANG;
import translate.trans.AbstractTranslator;

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
        langData.add(LANG.Albanian);
        langData.add(LANG.Arabic);
        langData.add(LANG.Amharic);
        langData.add(LANG.Azerbaijani);
        langData.add(LANG.Irish);
        langData.add(LANG.Estonian);
        langData.add(LANG.Basque);
        langData.add(LANG.Belarusian);
        langData.add(LANG.Bulgarian);
        langData.add(LANG.Icelandic);
        langData.add(LANG.Polish);
        langData.add(LANG.Bosnian);
        langData.add(LANG.Persian);
        langData.add(LANG.Afrikaans);
        langData.add(LANG.Danish);
        langData.add(LANG.German);
        langData.add(LANG.Russian);
        langData.add(LANG.French);
        langData.add(LANG.Filipino);
        langData.add(LANG.Finnish);
        langData.add(LANG.Frisian);
        langData.add(LANG.Khmer);
        langData.add(LANG.Georgian);
        langData.add(LANG.Gujarati);
        langData.add(LANG.Kazakh);
        langData.add(LANG.HaitianCreole);
        langData.add(LANG.Korean);
        langData.add(LANG.Hausa);
        langData.add(LANG.Dutch);
        langData.add(LANG.Kyrgyz);
        langData.add(LANG.Galician);
        langData.add(LANG.Catalan);
        langData.add(LANG.Czech);
        langData.add(LANG.Kannada);
        langData.add(LANG.Corsican);
        langData.add(LANG.Croatian);
        langData.add(LANG.Kurdish);
        langData.add(LANG.Latin);
        langData.add(LANG.Latvian);
        langData.add(LANG.Laotian);
        langData.add(LANG.Lithuanian);
        langData.add(LANG.Luxembourgish);
        langData.add(LANG.Romanian);
        langData.add(LANG.Malagasy);
        langData.add(LANG.Maltese);
        langData.add(LANG.Marathi);
        langData.add(LANG.Malayalam);
        langData.add(LANG.Malay);
        langData.add(LANG.Macedonian);
        langData.add(LANG.Maori);
        langData.add(LANG.Mongolian);
        langData.add(LANG.Bengali);
        langData.add(LANG.Burmese);
        langData.add(LANG.Hmong);
        langData.add(LANG.Xhosa);
        langData.add(LANG.Zulu);
        langData.add(LANG.Nepali);
        langData.add(LANG.Norwegian);
        langData.add(LANG.Punjabi);
        langData.add(LANG.Portuguese);
        langData.add(LANG.Pashto);
        langData.add(LANG.Chichewa);
        langData.add(LANG.Japanese);
        langData.add(LANG.Swedish);
        langData.add(LANG.Samoan);
        langData.add(LANG.Serbian);
        langData.add(LANG.Sotho);
        langData.add(LANG.Sinhala);
        langData.add(LANG.Esperanto);
        langData.add(LANG.Slovak);
        langData.add(LANG.Slovenian);
        langData.add(LANG.SwahiliSwahili);
        langData.add(LANG.ScottishGaelic);
        langData.add(LANG.Cebuano);
        langData.add(LANG.Somali);
        langData.add(LANG.Tajik);
        langData.add(LANG.Telugu);
        langData.add(LANG.Tamil);
        langData.add(LANG.Thai);
        langData.add(LANG.Turkish);
        langData.add(LANG.Welsh);
        langData.add(LANG.Urdu);
        langData.add(LANG.Ukrainian);
        langData.add(LANG.Uzbek);
        langData.add(LANG.Spanish);
        langData.add(LANG.Hebrew);
        langData.add(LANG.Greek);
        langData.add(LANG.Hawaiian);
        langData.add(LANG.Sindhi);
        langData.add(LANG.Hungarian);
        langData.add(LANG.Shona);
        langData.add(LANG.Armenian);
        langData.add(LANG.Igbo);
        langData.add(LANG.Italian);
        langData.add(LANG.Yiddish);
        langData.add(LANG.Hindi);
        langData.add(LANG.Sundanese);
        langData.add(LANG.Indonesian);
        langData.add(LANG.Javanese);
        langData.add(LANG.English);
        langData.add(LANG.Yoruba);
        langData.add(LANG.Vietnamese);
        langData.add(LANG.ChineseTraditional);
        langData.add(LANG.ChineseSimplified);
    }

    @Override
    public void setFormData(LANG from, LANG to, String text) {
        formData.put("client", "webapp");
        formData.put("sl", from.getCode());
        formData.put("tl", to.getCode());
        formData.put("hl", "zh-CN");
        formData.put("dt", "at");
        formData.put("ie", "UTF-8");
        formData.put("oe", "UTF-8");
        formData.put("tk", token(text));
        formData.put("q", text);
    }

    @Override
    public String query() throws Exception {
        URIBuilder uri = new URIBuilder(url);
        Logger logger = Logger.getInstance(GoogleTranslator.class);
        logger.info("runQuery"+uri.toString());
        for (String key : formData.keySet()) {
            String value = formData.get(key);
            uri.addParameter(key, value);
        }
        HttpGet request = new HttpGet(uri.toString());
        logger.info("request"+uri.toString());
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
        logger.info("response"+result);
        EntityUtils.consume(entity);
        response.getEntity().getContent().close();
        response.close();

        return result;
    }

    @Override
    public String parses(String text) throws IOException {
        if(PluginConfig.isTranslateTogether()){
            return text;
        }
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder contentResult = new StringBuilder();
        JsonNode jsonNode = mapper.readTree(text).get(5);
        for(int i=0;i<jsonNode.size();i++){
            if("\n".equals(jsonNode.get(i).get(0).textValue())){
                break;
            }
            contentResult.append(jsonNode.get(i).get(2).get(0).get(0).textValue());
        }
        return contentResult.toString();
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
