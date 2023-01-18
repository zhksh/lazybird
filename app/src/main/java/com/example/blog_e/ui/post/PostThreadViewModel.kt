package com.example.blog_e.ui.post

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blog_e.Config
import com.example.blog_e.data.repository.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import org.java_websocket.client.DefaultSSLWebSocketClientFactory
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.sql.Timestamp
import javax.inject.Inject
import javax.net.ssl.SSLContext

data class WebsocketEvent(
    val eventType: String = "subscribe",
    val postId: String,
)

data class UserInfo(
    val username: String,
    val icon_id: String,
    val display_name: String?,
)

data class Comment(
    val id: String,
    val user: UserInfo,
    val content: String,
)

data class FullPost(
    val id: String,
    val content: String,
    val user: UserInfo,
    val timestamp: Timestamp,
    val likes: Int,
    val comments: List<Comment>,
)

data class WebsocketResponse(
    val eventType: String,
    val data: FullPost,
)

@HiltViewModel
class PostThreadViewModel @Inject constructor(
    private val blogRepo: BlogRepo,
) : ViewModel() {

    private val tag = Config.tag(this.toString())
    private val gson = Gson()
    private val data: MutableLiveData<FullPost> = MutableLiveData()
    private var client: PostWebSocketClient? = null

    fun postData(): LiveData<FullPost> {
        return data
    }

    fun openPostConnection(id: String) {
        client = makeClient(id)
        client?.connect()
    }

    fun closePostConnection() {
        client?.close()
        client = null
    }

    suspend fun addComment(content: String, postId: String) {
        when (blogRepo.createComment(postId, content)) {
            is ApiSuccess -> {}
            is ApiError -> {
                Log.e(tag, "api error")
            }
            is ApiException -> {
                Log.e(tag, "api exception")
            }
        }
    }

    private fun makeClient(postId: String): PostWebSocketClient {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, null, null)
        val client = PostWebSocketClient(URI("wss://mvsp-api.ncmg.eu"), Draft_17(), postId)
        client.setWebSocketFactory(DefaultSSLWebSocketClientFactory(sslContext))
        return client
    }

    inner class PostWebSocketClient(serverUri: URI?, draft: Draft?, private val postId: String) : WebSocketClient(serverUri, draft) {
        override fun onError(ex: Exception?) {
            Log.e(tag, "websocket error: $ex?.message")
            // TODO: Handle error?
        }

        override fun onOpen(handshakedata: ServerHandshake?) {
            val event = gson.toJson(WebsocketEvent(postId = postId))
            this.send(event)
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            Log.d(tag, "websocket closed: $reason")
        }

        override fun onMessage(message: String?) {
            if (message != null) {
                val response = gson.fromJson(message, WebsocketResponse::class.java)

                if (response.eventType == "error") {
                    Log.e(tag, "failed to subscribe to post: $message")
                    // TODO: Handle error
                }

                if (response.eventType == "updated") {
                    data.postValue(response.data)
                }
            }
        }
    }
}