package com.example.blog_e.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blog_e.data.repository.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.sql.Timestamp
import javax.inject.Inject

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

    private val gson = Gson()
    private val data: MutableLiveData<FullPost> = MutableLiveData()

    // TODO: Rename postId
    private var id: String = ""

    fun getPost(id: String): LiveData<FullPost> {
        println(id)
        loadPost(id)
        return data
    }

    private fun loadPost(id: String){
        // this.id = id
        this.id = "d5a12370-13f4-4fac-862f-e66a28a5a116"
        // val client = MyWebSocketClient(URI("wss://mvsp-api.ncmg.eu"), Draft_17())
        val client = MyWebSocketClient(URI("ws://10.0.2.2:6969"), Draft_17())
        client.connect()
    }

    inner class MyWebSocketClient(serverUri: URI?, draft: Draft?) : WebSocketClient(serverUri, draft) {
        override fun onError(ex: Exception?) {
            // TODO: Handle error?
            println(ex)
        }

        override fun onOpen(handshakedata: ServerHandshake?) {
            val event = gson.toJson(WebsocketEvent(postId = id))
            this.send(event)
        }
        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            println("connection closed code: $code reason: $reason")
        }
        override fun onMessage(message: String?) {
            println(message)
            if (message != null) {
                val response = gson.fromJson(message, WebsocketResponse::class.java)
                println(response)

                if (response.eventType == "error") {
                    // TODO: Handle error
                    println("received websocket error")
                }

                if (response.eventType == "update") {
                    data.postValue(response.data)
                }
            }
        }
    }
}