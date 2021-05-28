package com.airsaid.localization.translate.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author airsaid
 */
public class GsonUtil {

  private final Gson gson;

  public GsonUtil() {
    gson = new GsonBuilder().create();
  }

  public static GsonUtil getInstance() {
    return GsonUtilHolder.sInstance;
  }

  public Gson getGson() {
    return gson;
  }

  private static class GsonUtilHolder {
    private static final GsonUtil sInstance = new GsonUtil();
  }

}
