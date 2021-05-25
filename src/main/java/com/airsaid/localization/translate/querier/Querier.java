package com.airsaid.localization.translate.querier;

import com.airsaid.localization.translate.http.AbstractHttpAttribute;
import com.airsaid.localization.translate.lang.LANG;

import java.util.ArrayList;
import java.util.List;

public final class Querier<T extends AbstractHttpAttribute> {
    private LANG from;
    private LANG to;
    private String text;
    private List<T> collection;

    public Querier() {
        collection = new ArrayList<T>();
    }

    public String executeSingle() {
        return collection.get(0).run(from, to, text);
    }

    public List<String> execute() {
        List<String> result = new ArrayList<>();
        for (T element : collection) {
            if (element.getClass().getName().contains("Translator")) {
                result.add(element.run(from, to, text));
            } else if (element.getClass().getName().contains("TTS")) {
                result.add(element.run(from, text));
            }
        }
        return result;
    }

    public void setParams(LANG from, LANG to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public void setParams(LANG source, String text) {
        this.from = source;
        this.text = text;
    }

    public void attach(T element){
        collection.add(element);
    }

    public void detach(T element) {
        collection.remove(element);
    }
}
