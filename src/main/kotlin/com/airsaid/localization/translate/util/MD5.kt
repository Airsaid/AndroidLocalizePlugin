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

package com.airsaid.localization.translate.util

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @author airsaid
 */
object MD5 {

    private val hexDigits = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    )

    fun md5(input: String?): String? {
        if (input == null) {
            return null
        }

        return try {
            val messageDigest = MessageDigest.getInstance("MD5")
            val inputByteArray = input.toByteArray(StandardCharsets.UTF_8)
            messageDigest.update(inputByteArray)
            val resultByteArray = messageDigest.digest()
            byteArrayToHex(resultByteArray)
        } catch (e: NoSuchAlgorithmException) {
            null
        }
    }

    private fun byteArrayToHex(byteArray: ByteArray): String {
        val resultCharArray = CharArray(byteArray.size * 2)
        var index = 0
        for (b in byteArray) {
            resultCharArray[index++] = hexDigits[b.toInt() ushr 4 and 0xf]
            resultCharArray[index++] = hexDigits[b.toInt() and 0xf]
        }
        return String(resultCharArray)
    }
}