package com.airsaid.localization.translate.impl.google

import com.intellij.openapi.util.Pair
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author airsaid
 */
class GoogleTokenTest {

    @Test
    fun getToken() {
        val a = 202905874L
        val b = 544157181L
        val c = 419689L
        val tkk = Pair(c, a + b)
        Assertions.assertEquals("34939.454418", GoogleToken.getToken("Translate", tkk))
        Assertions.assertEquals("671407.809414", GoogleToken.getToken("Google translate", tkk))
    }
}