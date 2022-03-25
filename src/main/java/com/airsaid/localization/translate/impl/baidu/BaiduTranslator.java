/*
 * Copyright 2021 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.airsaid.localization.translate.impl.baidu;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.TranslationException;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.lang.Languages;
import com.airsaid.localization.translate.util.GsonUtil;
import com.airsaid.localization.translate.util.LogUtils;
import com.airsaid.localization.translate.util.MD5;
import com.intellij.openapi.util.Pair;
import com.intellij.util.io.RequestBuilder;
import icons.PluginIcons;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author airsaid
 */
public class BaiduTranslator extends AbstractTranslator {
    private static final String KEY = "Baidu";
    private static final String HOST_URL = "https://aip.baidubce.com";
    private static final String TRANSLATE_URL = HOST_URL.concat("/rpc/2.0/mt/texttrans/v1");
    private static final String APPLY_APP_ID_URL = "https://aip.baidubce.com/oauth/2.0/token";

    private String BaiDuToken = "";

    private List<Lang> supportedLanguages;

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
        if (supportedLanguages == null) {
            supportedLanguages = new ArrayList<>();
            supportedLanguages.add(Languages.CHINESE_SIMPLIFIED.clone().setCode("zh"));
            supportedLanguages.add(Languages.ENGLISH);
            supportedLanguages.add(Languages.JAPANESE.clone().setCode("jp"));
            supportedLanguages.add(Languages.KOREAN.clone().setCode("kor"));
            supportedLanguages.add(Languages.FRENCH.clone().setCode("fra"));
            supportedLanguages.add(Languages.SPANISH.clone().setCode("spa"));
            supportedLanguages.add(Languages.THAI);
            supportedLanguages.add(Languages.ARABIC.clone().setCode("ara"));
            supportedLanguages.add(Languages.RUSSIAN);
            supportedLanguages.add(Languages.PORTUGUESE);
            supportedLanguages.add(Languages.GERMAN);
            supportedLanguages.add(Languages.ITALIAN);
            supportedLanguages.add(Languages.GREEK);
            supportedLanguages.add(Languages.DUTCH);
            supportedLanguages.add(Languages.POLISH);
            supportedLanguages.add(Languages.BULGARIAN.clone().setCode("bul"));
            supportedLanguages.add(Languages.ESTONIAN.clone().setCode("est"));
            supportedLanguages.add(Languages.DANISH.clone().setCode("dan"));
            supportedLanguages.add(Languages.FINNISH.clone().setCode("fin"));
            supportedLanguages.add(Languages.CZECH);
            supportedLanguages.add(Languages.ROMANIAN.clone().setCode("rom"));
            supportedLanguages.add(Languages.SLOVENIAN.clone().setCode("slo"));
            supportedLanguages.add(Languages.SWEDISH.clone().setCode("swe"));
            supportedLanguages.add(Languages.HUNGARIAN);
            supportedLanguages.add(Languages.CHINESE_TRADITIONAL.clone().setCode("cht"));
            supportedLanguages.add(Languages.VIETNAMESE.clone().setCode("vie"));
        }
        return supportedLanguages;
    }


    @Override
    public String doTranslate(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) throws TranslationException {
        String result = "";
        // 检查是否有token
        if (StringUtils.isAllEmpty(BaiDuToken)){
            BaiDuToken =  getBaiduToken();  // 请求token
        }
        if (StringUtils.isAllEmpty(BaiDuToken)){
            return result;
        }else {
            result= baiduTranslate(fromLang,toLang,text);
        }
        return result;


      /*  //百度的翻译

        HashMap<String, String> parms = new HashMap<String, String>();
        parms.put("grant_type", "client_credentials");
        parms.put("client_id", getAppId());
        parms.put("client_secret",  getAppKey());

        String requestUrl = getApplyAppIdUrl() + getParms(parms);
        LogUtils.d("requestUrl:"+requestUrl);

        try {

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .method("POST", body)
                    .build();
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            if (responseBody != null) {
                String string = responseBody.string();
                BaiduTokenRespons tokenResult = GsonUtil.getInstance().getGson().fromJson(string, BaiduTokenRespons.class);
                if (tokenResult.getAccess_token() != null) {
                    // 翻译
                    mediaType = MediaType.parse("application/json;charset=utf-8");
                    BaiduResquest baiduResquest = new BaiduResquest();
                    baiduResquest.setQ(text);
                    baiduResquest.setFrom(fromLang.getCode());
                    baiduResquest.setTo(toLang.getCode());

                    body = RequestBody.create(mediaType, GsonUtil.getInstance().getGson().toJson(baiduResquest));

                    request = new Request.Builder()
                            .url(getRequestUrl(fromLang,toLang,text) + tokenResult.getAccess_token())
                            .url("https://aip.baidubce.com/rpc/2.0/mt/texttrans/v1?access_token=" + tokenResult.getAccess_token())
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json;charset=utf-8")
                            .build();
                    response = client.newCall(request).execute();

                    String resultText = response.body().string();

                    result = parsingResult(fromLang, toLang, text, resultText);
                }
            }

        } catch (Exception e) {

            LogUtils.d("" + e.toString());
            e.printStackTrace();


        }

        return result;*/
    }

    private String baiduTranslate(Lang fromLang, Lang toLang, String text) {

        String result = "";

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType   mediaType = MediaType.parse("application/json;charset=utf-8");

        BaiduResquest baiduResquest = new BaiduResquest();
        baiduResquest.setQ(text);
        baiduResquest.setFrom(fromLang.getCode());
        baiduResquest.setTo(toLang.getCode());

        RequestBody body = RequestBody.create(mediaType, GsonUtil.getInstance().getGson().toJson(baiduResquest));
        Request request = new Request.Builder()
                .url(getRequestUrl(fromLang,toLang,text) +"?access_token="+ BaiDuToken)
//                .url("https://aip.baidubce.com/rpc/2.0/mt/texttrans/v1?access_token=" + BaiDuToken)
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .build();

        Response response = null;
        try {
            response= client.newCall(request).execute();
            String resultText = response.body().string();
            result = parsingResult(fromLang, toLang, text, resultText);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    private String getBaiduToken() {

        String BaiDuToken = null;

        HashMap<String, String> parms = new HashMap<String, String>();
        parms.put("grant_type", "client_credentials");
        parms.put("client_id", getAppId());
        parms.put("client_secret", getAppKey());

        String requestUrl = getApplyAppIdUrl() + getParms(parms);
        LogUtils.d("requestUrl:" + requestUrl);

        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .method("POST", body)
                    .build();
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            String string = responseBody.string();
            BaiduTokenRespons tokenResult = GsonUtil.getInstance().getGson().fromJson(string, BaiduTokenRespons.class);

            if (tokenResult != null) {
                BaiDuToken = tokenResult.getAccess_token();
            }

        } catch (Exception e) {

        }

        return BaiDuToken;

    }

    private String getParms(HashMap<String, String> parms) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("?");
        for (HashMap.Entry<String, String> entry : parms.entrySet()) {
            stringBuffer.append("" + entry.getKey());
            stringBuffer.append("=");
            stringBuffer.append("" + entry.getValue());
            stringBuffer.append("&");
        }
        return stringBuffer.toString().substring(0, stringBuffer.length() - 1);
    }

    @Override
    public @Nullable String getApplyAppIdUrl() {
        return APPLY_APP_ID_URL;
    }

    @Override
    public @NotNull String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
        return TRANSLATE_URL;
    }

    @Override
    public @NotNull List<Pair<String, String>> getRequestParams(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
        String salt = String.valueOf(System.currentTimeMillis());
        String appId = getAppId();
        String securityKey = getAppKey();
        String sign = MD5.md5(appId + text + salt + securityKey);
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("from", fromLang.getCode()));
        params.add(Pair.create("to", toLang.getCode()));
        params.add(Pair.create("appid", appId));
        params.add(Pair.create("salt", salt));
        params.add(Pair.create("sign", sign));
        params.add(Pair.create("q", text));
        return params;
    }

    @Override
    public void configureRequestBuilder(@NotNull RequestBuilder requestBuilder) {
        requestBuilder.tuner(connection -> connection.setRequestProperty("Referer", HOST_URL));
    }

    @Override
    public @NotNull String parsingResult(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull String resultText) {
        LOG.info("parsingResult: " + resultText);
        BaiduTranslationResult2 baiduTranslationResult = GsonUtil.getInstance().getGson().fromJson(resultText, BaiduTranslationResult2.class);
        if (baiduTranslationResult != null && baiduTranslationResult.isSuccess()) {
            return baiduTranslationResult.getResult().getTrans_result().get(0).getDst();
        } else {
            BaiduTokenResponsError responsError = GsonUtil.getInstance().getGson().fromJson(resultText, BaiduTokenResponsError.class);
            throw new TranslationException(fromLang, toLang, text, "error_code " + responsError.getError_code() + " | " + responsError.getError_msg());
        }
    }
}
