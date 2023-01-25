package com.example.blog_e.ui.post

import android.util.Log
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import com.example.blog_e.Config
import com.example.blog_e.data.model.CommentAPIModel
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.model.UserAPIModel
import com.example.blog_e.data.repository.*
import com.example.blog_e.utils.SessionManager
import com.example.blog_e.utils.calculatePastTime
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.java_websocket.client.DefaultSSLWebSocketClientFactory
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_17
import org.java_websocket.framing.CloseFrame
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject
import javax.net.ssl.SSLContext

data class WebsocketEvent(
    val eventType: String = "subscribe",
    val postId: String,
)

data class WebsocketResponse(
    val eventType: String,
    val data: PostAPIModel,
)

data class PostState(
    val user: UserAPIModel = UserAPIModel(username = "", iconId = "0", displayName = "", bio = ""),
    val content: String = "",
    val timeSinceString: String = "",
    val likes: Number = 0,
    val comments: List<CommentAPIModel> = emptyList(),
    val isLiked: Boolean = false,
)

@HiltViewModel
class PostThreadViewModel @Inject constructor(
    private val blogRepo: BlogRepo,
    private val sessionManager: SessionManager,
) : ViewModel() {

    val isLoading = ObservableBoolean(true)
    val hasFailed = ObservableBoolean(false)

    private val _uiState = MutableStateFlow(PostState())
    val uiState = _uiState.asStateFlow()

    private val tag = Config.tag(this.toString())
    private val gson = Gson()
    private var client: PostWebSocketClient? = null
    private var postId = ""

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

    suspend fun onClickLike() {
        val isLiked = !uiState.value.isLiked

        // Optimistically update ui for better user experience.
        // If the API call fails for some reason, the state might be changed again.
        updateIsLiked(isLiked)

        val result = if (isLiked) {
            blogRepo.addLike(postId)
        } else {
            blogRepo.removeLike(postId)
        }

        when (result) {
            is ApiSuccess -> {}
            is ApiError -> {
                Log.e(tag, "api error: $result.message")
            }
            is ApiException -> {
                Log.e(tag, "api exception: $result.e")
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
                    updateUIState(response.data)
                    setReadyState()
                }
            }
        }

        private fun updateUIState(post: PostAPIModel) {
            _uiState.update {
                it.copy(
                    // TODO: Only update static fields if not set?
                    comments = post.comments,
                    likes = post.likes.count(),
                    content = post.content,
                    timeSinceString = calculatePastTime(post.timestamp),
                    user = post.user,
                    isLiked = post.likes.contains(sessionManager.getUsername()),
                )
            }
        }
    }

    /**
     * Sets the uiState isLiked field to the given value and updates all observers if the value changed.
     */
    private fun updateIsLiked(to: Boolean) {
        val current = _uiState.value.isLiked
        if (to != current) {
            _uiState.update { it.copy(isLiked = to) }
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