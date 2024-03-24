package com.ibrahimbasit.I210669

import android.content.Context
import io.agora.rtc.RtcEngine
import io.agora.rtc.IRtcEngineEventHandler

object AgoraEngine {
    private var rtcEngine: RtcEngine? = null
    private const val APP_ID = "f20c3ac6233140a3b77738abbe050a64" // Replace with your Agora App ID

    fun initialize(context: Context, eventHandler: IRtcEngineEventHandler): RtcEngine {
        if (rtcEngine == null) {
            synchronized(this) {
                rtcEngine = RtcEngine.create(context.applicationContext, APP_ID, eventHandler)
            }
        }
        return rtcEngine!!
    }

    fun getInstance(): RtcEngine? {
        if (rtcEngine == null) {
            throw IllegalStateException("AgoraEngine not initialized")
        }
        return rtcEngine
    }

    fun destroy() {
        RtcEngine.destroy()
        rtcEngine = null
    }
}
