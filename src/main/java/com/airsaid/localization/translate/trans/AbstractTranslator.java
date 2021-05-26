package com.airsaid.localization.translate.trans;

import com.airsaid.localization.translate.http.AbstractHttpAttribute;
import com.airsaid.localization.translate.http.HttpParams;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.trans.impl.GoogleTranslator;

import java.io.IOException;
import java.util.List;

/**
 * AbstractTranslator is an abstract base class for all translators
 * which includes several (abstract) functions. By setting parameters,
 * the request is sent to the target server, and then parse the return
 * result to achieve the the purpose of translation.
 *
 * @see GoogleTranslator
 */
public abstract class AbstractTranslator extends AbstractHttpAttribute implements HttpParams {

    public AbstractTranslator(String url) {
        super(url);
        setLangSupport();
    }

    @Override
    public String run(Lang source, String text) {
        return null;
    }

    @Override
    public String run(Lang from, Lang to, String text) {
        String result = "";
        setFormData(from, to, text);
        try {
            result = parses(query());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Initialize the supported language mapping.
     */
    public abstract void setLangSupport();

    /**
     * Get the language currently supported for translation.
     *
     * @return support lang list.
     */
    public List<Lang> getSupportLang() {
        return langData;
    }

    @Override
    public void setFormData(Lang source, String text){}

    @Override
    public abstract void setFormData(Lang from, Lang to, String text);

    @Override
    public abstract String query() throws Exception;

    /**
     * Parse the string to extract the content of interest.
     *
     * @param text the string form of the translated result.
     * @return translation results after parsing.
     * @throws IOException if the parsing fails.
     */
    public abstract String parses(String text) throws IOException;

}
