/*******************************************************************************
 * Copyright 2020 CPqD. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package br.com.cpqd.asr

import com.neovisionaries.ws.client.*

interface WebSocketListenerAsr : WebSocketListener {


    override fun handleCallbackError(websocket: WebSocket?, cause: Throwable?) {

    }

    override fun onFrame(websocket: WebSocket?, frame: WebSocketFrame?) {

    }

    override fun onThreadCreated(websocket: WebSocket?, threadType: ThreadType?, thread: Thread?) {

    }

    override fun onThreadStarted(websocket: WebSocket?, threadType: ThreadType?, thread: Thread?) {

    }

    override fun onStateChanged(websocket: WebSocket?, newState: WebSocketState?) {

    }

    override fun onTextMessageError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        data: ByteArray?
    ) {

    }

    override fun onTextFrame(websocket: WebSocket?, frame: WebSocketFrame?) {

    }

    override fun onUnexpectedError(websocket: WebSocket?, cause: WebSocketException?) {

    }

    override fun onConnectError(websocket: WebSocket?, cause: WebSocketException?) {

    }

    override fun onSendError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        frame: WebSocketFrame?
    ) {

    }

    override fun onFrameUnsent(websocket: WebSocket?, frame: WebSocketFrame?) {

    }

    override fun onDisconnected(
        websocket: WebSocket?,
        serverCloseFrame: WebSocketFrame?,
        clientCloseFrame: WebSocketFrame?,
        closedByServer: Boolean
    ) {

    }

    override fun onSendingFrame(websocket: WebSocket?, frame: WebSocketFrame?) {

    }

    override fun onBinaryFrame(websocket: WebSocket?, frame: WebSocketFrame?) {

    }

    override fun onPingFrame(websocket: WebSocket?, frame: WebSocketFrame?) {

    }

    override fun onSendingHandshake(
        websocket: WebSocket?,
        requestLine: String?,
        headers: MutableList<Array<String>>?
    )

    override fun onTextMessage(websocket: WebSocket?, text: String?) {

    }

    override fun onTextMessage(websocket: WebSocket?, data: ByteArray?) {

    }

    override fun onFrameError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        frame: WebSocketFrame?
    ) {

    }

    override fun onCloseFrame(websocket: WebSocket?, frame: WebSocketFrame?) {

    }

    override fun onBinaryMessage(websocket: WebSocket?, binary: ByteArray?)

    override fun onContinuationFrame(websocket: WebSocket?, frame: WebSocketFrame?) {

    }

    override fun onConnected(
        websocket: WebSocket?,
        headers: MutableMap<String, MutableList<String>>?
    )

    override fun onFrameSent(websocket: WebSocket?, frame: WebSocketFrame?) {

    }

    override fun onThreadStopping(websocket: WebSocket?, threadType: ThreadType?, thread: Thread?) {

    }

    override fun onError(websocket: WebSocket?, cause: WebSocketException?) {

    }

    override fun onMessageDecompressionError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        compressed: ByteArray?
    ) {

    }

    override fun onPongFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
    }

    override fun onMessageError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        frames: MutableList<WebSocketFrame>?
    ) {

    }
}