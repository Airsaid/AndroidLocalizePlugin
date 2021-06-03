package com.airsaid.localization.translate.impl.baidu;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.TranslationException;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.util.GsonUtil;
import com.airsaid.localization.translate.util.MD5;
import com.airsaid.localization.translate.util.UrlBuilder;
import com.intellij.openapi.util.Pair;
import com.intellij.util.io.RequestBuilder;
import icons.PluginIcons;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
public class BaiduTranslator extends AbstractTranslator {
  private static final String KEY = "Baidu";
  private static final String HOST_URL = "http://api.fanyi.baidu.com";
  private static final String TRANSLATE_URL = HOST_URL.concat("/api/trans/vip/translate");

  @Override
  public @NotNull String getKey() {
    return KEY;
  }

  @Override
  public @NotNull String getName() {
    return "Baidu";
  }

  @Override
  public @Nullable Icon getIcon() {
    return PluginIcons.BAIDU_ICON;
  }

  @Override
  public @NotNull List<Lang> getSupportedLanguages() {
    List<Lang> result = new ArrayList<>();
    result.add(Lang.CHINESE_SIMPLIFIED.setCode("zh"));
    result.add(Lang.ENGLISH);
    result.add(Lang.JAPANESE.setCode("jp"));
    result.add(Lang.KOREAN.setCode("kor"));
    result.add(Lang.FRENCH.setCode("fra"));
    result.add(Lang.SPANISH.setCode("spa"));
    result.add(Lang.THAI);
    result.add(Lang.ARABIC.setCode("ara"));
    result.add(Lang.RUSSIAN);
    result.add(Lang.PORTUGUESE);
    result.add(Lang.GERMAN);
    result.add(Lang.ITALIAN);
    result.add(Lang.GREEK);
    result.add(Lang.DUTCH);
    result.add(Lang.POLISH);
    result.add(Lang.BULGARIAN.setCode("bul"));
    result.add(Lang.ESTONIAN.setCode("est"));
    result.add(Lang.DANISH.setCode("dan"));
    result.add(Lang.FINNISH.setCode("fin"));
    result.add(Lang.CZECH);
    result.add(Lang.ROMANIAN.setCode("rom"));
    result.add(Lang.SLOVENIAN.setCode("slo"));
    result.add(Lang.SWEDISH.setCode("swe"));
    result.add(Lang.HUNGARIAN);
    result.add(Lang.CHINESE_TRADITIONAL.setCode("cht"));
    result.add(Lang.VIETNAMESE.setCode("vie"));
    return result;
  }

  @Override
  public @NotNull String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    String salt = String.valueOf(System.currentTimeMillis());
    String appId = getAppId();
    String securityKey = getAppKey();
    String sign = MD5.md5(appId + text + salt + securityKey);
    return new UrlBuilder(TRANSLATE_URL)
        .addQueryParameter("from", fromLang.getCode())
        .addQueryParameter("to", toLang.getCode())
        .addQueryParameters("appid", appId)
        .addQueryParameters("salt", salt)
        .addQueryParameters("sign", sign)
        .build();
  }

  @Override
  public @NotNull List<Pair<String, String>> getRequestParams(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    List<Pair<String, String>> params = new ArrayList<>();
    params.add(Pair.create("q", StringEscapeUtils.escapeJava(text)));
    return params;
  }

  @Override
  public void configureRequestBuilder(@NotNull RequestBuilder requestBuilder) {
    requestBuilder.tuner(connection -> connection.setRequestProperty("Referer", HOST_URL));
  }

  @Override
  public @NotNull String parsingResult(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull String resultText) {
    LOG.info("parsingResult: " + resultText);
    BaiduTranslationResult baiduTranslationResult = GsonUtil.getInstance().getGson().fromJson(resultText, BaiduTranslationResult.class);
    if (baiduTranslationResult.isSuccess()) {
      return baiduTranslationResult.getTranslationResult();
    } else {
      String message = baiduTranslationResult.getErrorMsg().concat("(").concat(baiduTranslationResult.getErrorCode()).concat(")");
      throw new TranslationException(fromLang, toLang, text, message);
    }
  }
}
