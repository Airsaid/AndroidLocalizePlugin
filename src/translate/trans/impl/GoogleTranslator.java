package translate.trans.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
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

    public GoogleTranslator(){
        super(url);
    }

    @Override
    public void setLangSupport() {
        langData.add(LANG.Afrikaans);
        langData.add(LANG.Albanian);
        langData.add(LANG.Arabic);
        langData.add(LANG.Azerbaijani);
        langData.add(LANG.Basque);
        langData.add(LANG.Bengali);
        langData.add(LANG.Belarusian);
        langData.add(LANG.Bulgarian);
        langData.add(LANG.Catalan);
        langData.add(LANG.ChineseSimplified);
        langData.add(LANG.ChineseTraditional);
        langData.add(LANG.Croatian);
        langData.add(LANG.Czech);
        langData.add(LANG.Danish);
        langData.add(LANG.Dutch);
        langData.add(LANG.English);
        langData.add(LANG.Esperanto);
        langData.add(LANG.Estonian);
        langData.add(LANG.Filipino);
        langData.add(LANG.Finnish);
        langData.add(LANG.French);
        langData.add(LANG.Galician);
        langData.add(LANG.Georgian);
        langData.add(LANG.German);
        langData.add(LANG.Greek);
        langData.add(LANG.Gujarati);
        langData.add(LANG.HaitianCreole);
        langData.add(LANG.Hebrew);
        langData.add(LANG.Hindi);
        langData.add(LANG.Hungarian);
        langData.add(LANG.Icelandic);
        langData.add(LANG.Indonesian);
        langData.add(LANG.Irish);
        langData.add(LANG.Italian);
        langData.add(LANG.Japanese);
        langData.add(LANG.Kannada);
        langData.add(LANG.Korean);
        langData.add(LANG.Latin);
        langData.add(LANG.Latvian);
        langData.add(LANG.Lithuanian);
        langData.add(LANG.Macedonian);
        langData.add(LANG.Malay);
        langData.add(LANG.Maltese);
        langData.add(LANG.Norwegian);
        langData.add(LANG.Persian);
        langData.add(LANG.Polish);
        langData.add(LANG.Portuguese);
        langData.add(LANG.Romanian);
        langData.add(LANG.Russian);
        langData.add(LANG.Serbian);
        langData.add(LANG.Slovak);
        langData.add(LANG.Slovenian);
        langData.add(LANG.Spanish);
        langData.add(LANG.SwahiliSwahili);
        langData.add(LANG.Swedish);
        langData.add(LANG.Tamil);
        langData.add(LANG.Telugu);
        langData.add(LANG.Thai);
        langData.add(LANG.Turkish);
        langData.add(LANG.Ukrainian);
        langData.add(LANG.Urdu);
        langData.add(LANG.Vietnamese);
        langData.add(LANG.Welsh);
        langData.add(LANG.Yiddish);
    }

    @Override
    public void setFormData(LANG from, LANG to, String text) {
        formData.put("client", "t");
        formData.put("sl", from.getCode());
        formData.put("tl", to.getCode());
        formData.put("hl", "zh-CN");
        formData.put("dt", "at");
        formData.put("dt", "bd");
        formData.put("dt", "ex");
        formData.put("dt", "ld");
        formData.put("dt", "md");
        formData.put("dt", "qca");
        formData.put("dt", "rw");
        formData.put("dt", "rm");
        formData.put("dt", "ss");
        formData.put("dt", "t");
        formData.put("ie", "UTF-8");
        formData.put("oe", "UTF-8");
        formData.put("source", "btn");
        formData.put("ssel", "0");
        formData.put("tsel", "0");
        formData.put("kc", "0");
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
        HttpUriRequest request = new HttpGet(uri.toString());
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        String result = EntityUtils.toString(entity, "utf-8");

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
                Invocable invoke = (Invocable)engine;
                tk = String.valueOf(invoke.invokeFunction("token", text));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tk;
    }
}
