package com.example.blog_e.ui.post

import android.util.Log
import android.view.View
import androidx.databinding.ObservableBoolean
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
import org.java_websocket.framing.CloseFrame
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.sql.Timestamp
import javax.inject.Inject
import javax.net.ssl.SSLContext

data class WebsocketEvent(
    val eventType: String = "subscribe",
    val postId: String,
)

// TODO: Duplicate data definitions. We should clean up all models and then integrate the following ones.
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

    val isLoading = ObservableBoolean(true)
    val hasFailed = ObservableBoolean(false)

    private val tag = Config.tag(this.toString())
    private val gson = Gson()
    private val data: MutableLiveData<FullPost> = MutableLiveData()
    private var client: PostWebSocketClient? = null
    private var postId = ""

    fun postData(): LiveData<FullPost> {
        return data
    }

    fun openPostConnection(id: String) {
        postId = id
        reconnect()
    }

    fun closePostConnection() {
        // TODO: Call unsubscribe?
        client?.close()
        client = null
    }

    fun onClickRetry(view: View) {
        reconnect()
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

    inner class PostWebSocketClient(serverUri: URI?, draft: Draft?, private val postId: String) : WebSocketClient(serverUri, draft) {
        init {
            setLoadingState()
        }

        override fun onError(ex: Exception?) {
            Log.e(tag, "websocket error: $ex?.message")
            setErrorState()
        }

        override fun onOpen(handshakedata: ServerHandshake?) {
            val event = gson.toJson(WebsocketEvent(postId = postId))
            this.send(event)
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            if (code == CloseFrame.NORMAL) {
                return
            }

            setErrorState()
        }

        override fun onMessage(message: String?) {
            if (message != null) {
                val response = gson.fromJson(message, WebsocketResponse::class.java)

                if (response.eventType == "error") {
                    Log.e(tag, "failed to subscribe to post: $message")
                    setErrorState()
                }

                if (response.eventType == "updated") {
                    data.postValue(response.data)
                    setReadyState()
                }
            }
        }
    }

    private fun reconnect() {
        if (client != null) {
            client?.close()
        }

        client = makeClient(postId)
        client?.connect()
    }

    private fun makeClient(postId: String): PostWebSocketClient {
        val client = PostWebSocketClient(URI(Config.socketAddress), Draft_17(), postId)
        if (Config.socketPrefix == "wss://") {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, null, null)
            client.setWebSocketFactory(DefaultSSLWebSocketClientFactory(sslContext))
        }

        return client
    }

    private fun setErrorState() {
        hasFailed.set(true)
        isLoading.set(false)
    }

    private fun setLoadingState() {
        hasFailed.set(false)
        isLoading.set(true)
    }

    private fun setReadyState() {
        hasFailed.set(false)
        isLoading.set(false)
    }
}