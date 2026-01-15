package com.moris.ergo.data.api

import android.content.Context
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.serialization.json.Json
import androidx.core.content.edit
import io.ktor.client.plugins.cookies.matches
import io.ktor.util.date.GMTDate
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable

@Serializable
data class SerializableCookie(
    val name: String,
    val value: String,
    val domain: String, // Store as non-null
    val path: String,   // Store as non-null
    val secure: Boolean,
    val httpOnly: Boolean,
    val maxAge: Int? = null,
    val expiresAt: Long? = null,
    val createdAt: Long
) {
    fun toCookie(): Cookie = Cookie(
        name = name,
        value = value,
        domain = domain,
        path = path,
        secure = secure,
        httpOnly = httpOnly,
        maxAge = maxAge,
        expires = expiresAt?.let { GMTDate(it) }
    )

    fun isExpired(now: Long): Boolean = when {
        maxAge != null -> (createdAt + maxAge * 1000L) < now
        expiresAt != null -> expiresAt < now
        else -> false
    }

    companion object {
        fun fromCookie(requestUrl: Url, cookie: Cookie): SerializableCookie {
            return SerializableCookie(
                name = cookie.name,
                value = cookie.value,
                // Explicitly fill domain/path from URL if missing to satisfy Ktor's matcher
                domain = cookie.domain?.trimStart('.') ?: requestUrl.host,
                path = cookie.path ?: "/",
                secure = cookie.secure,
                httpOnly = cookie.httpOnly,
                maxAge = cookie.maxAge,
                expiresAt = cookie.expires?.timestamp,
                createdAt = System.currentTimeMillis()
            )
        }
    }
}

class PersistentCookieStorage(context: Context) : CookiesStorage {

    private val prefs =
        context.getSharedPreferences("ktor_cookies", Context.MODE_PRIVATE)

    private val json = Json
    private val mutex = Mutex()

    private fun key(cookie: SerializableCookie): String =
        "${cookie.name}|${cookie.domain}|${cookie.path}"

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        if (cookie.name.isBlank()) return

        val serial = SerializableCookie.fromCookie(requestUrl, cookie)

        mutex.withLock {
            prefs.edit {
                putString(key(serial), json.encodeToString(serial))
            }
        }
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        val now = getTimeMillis()
        val toRemove = mutableListOf<String>()
        val result = mutableListOf<Cookie>()

        mutex.withLock {
            for ((k, v) in prefs.all) {
                val serial = runCatching {
                    json.decodeFromString<SerializableCookie>(v as String)
                }.getOrNull() ?: continue

                if (serial.isExpired(now)) {
                    toRemove += k
                    continue
                }

                val cookie = serial.toCookie()
                if (cookie.matches(requestUrl)) {
                    result += cookie
                }
            }


            if (toRemove.isNotEmpty()) {
                prefs.edit {
                    toRemove.forEach { remove(it) }
                }
            }
        }

        return result
    }

    override fun close() {}
}
