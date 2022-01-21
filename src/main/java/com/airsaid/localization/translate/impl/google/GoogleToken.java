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

package com.airsaid.localization.translate.impl.google;

import com.airsaid.localization.translate.util.AgentUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.util.io.HttpRequests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author airsaid
 */
public class GoogleToken {

  private static final Logger LOG = Logger.getInstance(GoogleToken.class);

  private static final int MIM = 60 * 60 * 1000;
  private static final Random GENERATOR = new Random();
  private static final Pattern TKK_PATTERN = Pattern.compile("tkk='(\\d+).(-?\\d+)'");
  private static final String ELEMENT_URL = "%s/translate_a/element.js";

  private static Pair<Long, Long> sInnerValue = Pair.create(0L, 0L);
  private static boolean sNeedUpdate = true;

  public static String getToken(String text) {
    return getToken(text, getDefaultTKK());
  }

  public static String getToken(String text, Pair<Long, Long> tkk) {
    int length = text.length();
    List<Long> a = new ArrayList<>();
    int b = 0;
    char[] ch = text.toCharArray();
    while (b < length) {
      int c = ch[b];
      if (128 > c) {
        a.add((long) c);
      } else {
        if (2048 > c) {
          a.add((long) (c >> 6 | 192));
        } else {
          if (55296 == (c & 64512) && b + 1 < length && 56320 == (ch[b + 1] & 64512)) {
            c = 65536 + ((c & 1023) << 10) + (ch[++b] & 1023);
            a.add((long) (c >> 18 | 240));
            a.add((long) (c >> 12 & 63 | 128));
          } else {
            a.add((long) (c >> 12 | 224));
          }
          a.add((long) (c >> 6 & 63 | 128));
        }
        a.add((long) (c & 63 | 128));
      }
      b++;
    }

    long d = tkk.first;
    long e = tkk.second;
    long f = d;
    for (Long h : a) {
      f += h;
      f = fun(f, "+-a^+6");
    }

    f = fun(f, "+-3^+b+-f");
    f = f ^ e;
    if (0 > f) {
      f = (f & Integer.MAX_VALUE) + Integer.MAX_VALUE + 1;
    }
    f = (long) (f % 1E6);

    return f + "." + (f ^ d);
  }

  private static Long fun(Long a, String b) {
    long g = a;
    char[] ch = b.toCharArray();
    for (int c = 0; c < ch.length - 1; c += 3) {
      char d = ch[c + 2];
      int e = 'a' <= d ? (d - 87) : d - '0';
      long f = '+' == ch[c + 1] ? g >>> e : g << e;
      g = '+' == ch[c] ? g + f & ((long) Integer.MAX_VALUE * 2 + 1) : g ^ f;
    }
    return g;
  }

  private static Pair<Long, Long> getDefaultTKK() {
    long now = System.currentTimeMillis() / MIM;
    long curVal = sInnerValue.first;
    if (!sNeedUpdate && now == curVal) {
      return sInnerValue;
    }

    Pair<Long, Long> newTKK = getTKKFromGoogle();
    sNeedUpdate = newTKK == null;
    sInnerValue = newTKK != null ? newTKK : Pair.create(now, Math.abs((long) GENERATOR.nextInt()) + (long) GENERATOR.nextInt());

    return sInnerValue;
  }

  private static Pair<Long, Long> getTKKFromGoogle() {
    try {
      String url = String.format(ELEMENT_URL, GoogleTranslator.HOST_URL);
      LOG.info("getTKKFromGoogle url: " + url);
      String elementJs = HttpRequests.request(url)
          .userAgent(AgentUtil.getUserAgent())
          .tuner(connection -> connection.setRequestProperty("Referer", GoogleTranslator.HOST_URL))
          .readString();
      Matcher matcher = TKK_PATTERN.matcher(elementJs);
      if (matcher.find()) {
        long value1 = Long.parseLong(matcher.group(1));
        long value2 = Long.parseLong(matcher.group(2));
        LOG.info(String.format("TKK: %d.%d", value1, value2));
        return Pair.create(value1, value1);
      }
    } catch (Exception e) {
      e.printStackTrace();
      LOG.warn("TKK get failed.", e);
    }
    return null;
  }

}
