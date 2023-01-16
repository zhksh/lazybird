package com.example.blog_e.ui.post

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blog_e.data.repository.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_10
import org.java_websocket.drafts.Draft_17
import org.java_websocket.drafts.Draft_76
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject

data class WebsocketEvent(
    val eventType: String = "subscribe",
    val postId: String,
)


@HiltViewModel
class PostThreadViewModel @Inject constructor(
    private val blogRepo: BlogRepo,
) : ViewModel() {

    private val gson = Gson()
    private val data: MutableLiveData<String> = MutableLiveData()
    // TODO: Rename postId
    private var id: String = ""

    val postId = ObservableField<String>();

    fun getPost(id: String): LiveData<String> {
        println(id)
        loadPost(id)
        return data
    }

    private fun loadPost(id: String){
        // this.id = id
        this.id = "be54098d-aa84-49ad-b710-33649123103e"
        //val client = MyWebSocketClient(URI("wss://mvsp-api.ncmg.eu"), Draft_17())
        val client = MyWebSocketClient(URI("ws://localhost:6969"), Draft_17())
        client.connect()
    }

    inner class MyWebSocketClient(serverUri: URI?, draft: Draft?) : WebSocketClient(serverUri, draft) {
        override fun onError(ex: Exception?) {
            // TODO: Handle error?
            println(ex)
        }
        override fun onOpen(handshakedata: ServerHandshake?) {
            println("connection opened")
            val event = gson.toJson(WebsocketEvent(postId = id))
            this.send(event)
        }
        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            println("connection closed code: ${code} reason: ${reason}")
        }
        override fun onMessage(message: String?) {
            println(message)
            if (message != null) {
                data.value = message!!
            }
        }
    }
}