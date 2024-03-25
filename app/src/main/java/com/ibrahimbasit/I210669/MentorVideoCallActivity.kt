package com.ibrahimbasit.I210669

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.ibrahimbasit.I210669.AgoraEngine
import com.ibrahimbasit.I210669.R
import com.ibrahimbasit.I210669.AgoraVideoEngine
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration

class MentorVideoCallActivity : AppCompatActivity() {
    private lateinit var rtcEngine: RtcEngine
    private var isMuted = false
    private var isVideoEnabled = true
    private lateinit var channelName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentor_video_call)

        val userId = intent.getStringExtra("userId") ?: return // Return or handle the null case
        channelName = userId + (FirebaseAuth.getInstance().currentUser?.uid ?: "")



        rtcEngine = AgoraVideoEngine.getInstance()!!



        val closeButton: View = findViewById(R.id.closeButton)
        val micButton: FrameLayout = findViewById(R.id.micButton)
        val videoButton: FrameLayout = findViewById(R.id.videoButton)

        closeButton.setOnClickListener {
            leaveChannel()
            finish()
        }

        micButton.setOnClickListener {
            isMuted = !isMuted
            rtcEngine.muteLocalAudioStream(isMuted)
        }

        videoButton.setOnClickListener {
            isVideoEnabled = !isVideoEnabled
            rtcEngine.muteLocalVideoStream(!isVideoEnabled)
        }

        initializeAndJoinChannel()
    }

    private fun initializeAndJoinChannel() {
        // Enable video module
        rtcEngine.enableVideo()

        // Setup video profile, for example 360P
        rtcEngine.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
            VideoEncoderConfiguration.VD_640x360,
            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
            VideoEncoderConfiguration.STANDARD_BITRATE,
            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        )
        )

        // Set up the local video feed
        val localContainer = findViewById<FrameLayout>(R.id.local_video_view)
        val localSurfaceView = RtcEngine.CreateRendererView(baseContext)
        localContainer.addView(localSurfaceView)
        val localVideoCanvas = VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0)
        rtcEngine.setupLocalVideo(localVideoCanvas)

        // Join the channel
        rtcEngine.joinChannel(null, channelName, "Extra Optional Data", 0)
    }




    private fun leaveChannel() {
        rtcEngine.leaveChannel()
    }


    override fun onDestroy() {
        super.onDestroy()
        leaveChannel()

        AgoraVideoEngine.destroy()
    }
}