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

package com.airsaid.localization.translate.impl.google

import com.airsaid.localization.translate.util.AgentUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.util.io.HttpRequests
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs

/**
 * @author airsaid
 */
object GoogleToken {

    private val LOG = Logger.getInstance(GoogleToken::class.java)

    private const val MIM = 60 * 60 * 1000
    private val GENERATOR = Random()
    private val TKK_PATTERN = Pattern.compile("tkk='(\\d+).(-?\\d+)'")
    private const val ELEMENT_URL = "%s/translate_a/element.js"

    private var sInnerValue = Pair.create(0L, 0L)
    private var sNeedUpdate = true

    @JvmStatic
    fun getToken(text: String): String {
        return getToken(text, getDefaultTKK())
    }

    @JvmStatic
    fun getToken(text: String, tkk: Pair<Long, Long>): String {
        val length = text.length
        val a = mutableListOf<Long>()
        var b = 0
        val ch = text.toCharArray()

        while (b < length) {
            var c = ch[b].code
            when {
                128 > c -> a.add(c.toLong())
                2048 > c -> a.add((c shr 6 or 192).toLong())
                else -> {
                    if (55296 == (c and 64512) && b + 1 < length && 56320 == (ch[b + 1].code and 64512)) {
                        c = 65536 + ((c and 1023) shl 10) + (ch[++b].code and 1023)
                        a.add((c shr 18 or 240).toLong())
                        a.add((c shr 12 and 63 or 128).toLong())
                    } else {
                        a.add((c shr 12 or 224).toLong())
                    }
                    a.add((c shr 6 and 63 or 128).toLong())
                }
            }
            if (2048 > ch[b].code) {
                // Only add the last part if c was not modified in the 55296 branch above
            } else {
                a.add((c and 63 or 128).toLong())
            }
            b++
        }

        val d = tkk.first
        val e = tkk.second
        var f = d
        for (h in a) {
            f += h
            f = transform(f, "+-a^+6")
        }

        f = transform(f, "+-3^+b+-f")
        f = f xor e
        if (0 > f) {
            f = (f and Int.MAX_VALUE.toLong()) + Int.MAX_VALUE + 1
        }
        f = (f % 1E6).toLong()

        return "$f.${f xor d}"
    }

    private fun transform(a: Long, b: String): Long {
        var g = a
        val ch = b.toCharArray()
        var c = 0
        while (c < ch.size - 1) {
            val d = ch[c + 2]
            val e = if ('a' <= d) (d.code - 87) else (d.code - '0'.code)
            val f = if ('+' == ch[c + 1]) g ushr e else g shl e
            g = if ('+' == ch[c]) g + f and (Int.MAX_VALUE.toLong() * 2 + 1) else g xor f
            c += 3
        }
        return g
    }

    private fun getDefaultTKK(): Pair<Long, Long> {
        val now = System.currentTimeMillis() / MIM
        val curVal = sInnerValue.first
        if (!sNeedUpdate && now == curVal) {
            return sInnerValue
        }

        val newTKK = getTKKFromGoogle()
        sNeedUpdate = newTKK == null
        sInnerValue = newTKK ?: Pair.create(now, abs(GENERATOR.nextInt().toLong()) + GENERATOR.nextInt().toLong())

        return sInnerValue
    }

    private fun getTKKFromGoogle(): Pair<Long, Long>? {
        return try {
            val url = String.format(ELEMENT_URL, GoogleTranslator.HOST_URL)
            LOG.info("getTKKFromGoogle url: $url")
            val elementJs = HttpRequests.request(url)
                .userAgent(AgentUtil.getUserAgent())
                .tuner { connection -> connection.setRequestProperty("Referer", GoogleTranslator.HOST_URL) }
                .readString()
            val matcher = TKK_PATTERN.matcher(elementJs)
            if (matcher.find()) {
                val value1 = matcher.group(1)!!.toLong()
                val value2 = matcher.group(2)!!.toLong()
                LOG.info(String.format("TKK: %d.%d", value1, value2))
                Pair.create(value1, value1)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LOG.warn("TKK get failed.", e)
            null
        }
    }
}