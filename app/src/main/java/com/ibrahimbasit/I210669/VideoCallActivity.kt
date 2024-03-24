package com.ibrahimbasit.I210669

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.mediaio.AgoraTextureView
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration

class VideoCallActivity : AppCompatActivity() {

    private lateinit var rtcEngine: RtcEngine
    private var isMuted = false
    private var isVideoEnabled = true
    private lateinit var channelName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        val mentorId = intent.getStringExtra("mentorId") ?: return // Return or handle the null case
        channelName = (FirebaseAuth.getInstance().currentUser?.uid ?: "") + mentorId

        rtcEngine = RtcEngine.create(this, "f20c3ac6233140a3b77738abbe050a64", object : IRtcEngineEventHandler() {
            override fun onUserJoined(uid: Int, elapsed: Int) {
                runOnUiThread {
                    // Ensure the remote container is ready
                    val remoteContainer = findViewById<FrameLayout>(R.id.remote_video_view)
                    val remoteSurfaceView = RtcEngine.CreateRendererView(baseContext)
                    remoteContainer.addView(remoteSurfaceView)
                    rtcEngine.setupRemoteVideo(VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
                }
            }
        })



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
        rtcEngine.setVideoEncoderConfiguration(VideoEncoderConfiguration(
            VideoEncoderConfiguration.VD_640x360,
            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
            VideoEncoderConfiguration.STANDARD_BITRATE,
            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        ))

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

        AgoraEngine.destroy()
    }
}


