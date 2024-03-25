package com.ibrahimbasit.I210669

import android.content.Context
import android.util.Log
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine

object AgoraVideoEngine {
    var rtcEngine: RtcEngine? = null
    private const val APP_ID = "f20c3ac6233140a3b77738abbe050a64" // Replace with your Agora App ID

    fun initialize(context: Context, eventHandler: IRtcEngineEventHandler): RtcEngine {
        if (rtcEngine == null) {
            synchronized(this) {
                Log.d("Shujaan GAY", "AgoraVideoEngine: initialize")
                rtcEngine = RtcEngine.create(context.applicationContext, APP_ID, eventHandler)
            }
        }
        return rtcEngine!!
    }

    fun getInstance(): RtcEngine? {
        if (rtcEngine == null) {
            Log.d("WTF", "AgoraVideoEngine: getInstance")
            throw IllegalStateException("AgoraEngine not initialized")
        }
        Log.d("YAY", "AgoraVideoEngine: getInstance")

        return rtcEngine
    }

    fun destroy() {
        RtcEngine.destroy()
        rtcEngine = null
    }
}