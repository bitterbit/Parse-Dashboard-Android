package com.galtashma.parsedashboard

import android.util.Log
import java.security.MessageDigest
import kotlin.experimental.and

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

object Hash {
    private const val salt = "1m4bqk"

    fun sha1(value: String): String? {
        try {
            val messageDigest = MessageDigest.getInstance("SHA-1")
            messageDigest.update((value + salt).toByteArray(Charsets.UTF_8))
            val bytes = messageDigest.digest()
            val buffer = StringBuilder()
            bytes.forEach {
                buffer.append(((it and 0xff.toByte()) + 0x100).toString(16).drop(1))
            }
            val result = buffer.toString()
            Log.d(Const.TAG, "Hash result $result")

            return result
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            return null
        }
    }
}
