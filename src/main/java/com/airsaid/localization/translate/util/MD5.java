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

package com.airsaid.localization.translate.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author airsaid
 */
public class MD5 {

  private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  private MD5() {
    throw new AssertionError("No com.airsaid.localization.translate.util.MD5 instances for you!");
  }

  public static String md5(String input) {
    if (input == null) {
      return null;
    }

    try {
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      byte[] inputByteArray = input.getBytes(StandardCharsets.UTF_8);
      messageDigest.update(inputByteArray);
      byte[] resultByteArray = messageDigest.digest();
      return byteArrayToHex(resultByteArray);
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }

  private static String byteArrayToHex(byte[] byteArray) {
    char[] resultCharArray = new char[byteArray.length * 2];
    int index = 0;
    for (byte b : byteArray) {
      resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
      resultCharArray[index++] = hexDigits[b & 0xf];
    }
    return new String(resultCharArray);
  }

}
