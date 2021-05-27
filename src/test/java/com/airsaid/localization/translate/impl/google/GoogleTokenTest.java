package com.airsaid.localization.translate.impl.google;

import com.intellij.openapi.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author airsaid
 */
public class GoogleTokenTest {

  @Test
  public void getToken() {
    long a = 202905874L;
    long b = 544157181L;
    long c = 419689L;
    Pair<Long, Long> tkk = Pair.create(c, a + b);
    Assertions.assertEquals("34939.454418", GoogleToken.getToken("Translate", tkk));
    Assertions.assertEquals("671407.809414", GoogleToken.getToken("Google translate", tkk));
  }

}