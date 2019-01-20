package com.example.akito.voicealarm

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {
    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    private var mFileName: String = ""

    private var mRecorder: MediaRecorder? = null

    private var mPlayer: MediaPlayer? = null

    fun startRecording(view: View) {
        messageTextView.text = "record start"

        mRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) // オーディオソースを設定 MICを使用
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // 出力ファイルフォーマットを設定
            setOutputFile(mFileName) // 出力ファイル名を設定
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) // オーディオエンコーダを設定

            try {
                prepare() // 初期化
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()  // レコーダーを起動
        }
    }

    fun stopRecording(view: View) {
        messageTextView.text = "record stop"

        mRecorder?.apply {
            stop() //  レコーダーを停止
            release() // リソースを解放
        }
        mRecorder = null
    }

    fun startPlaying(view: View) {
        messageTextView.text = "play start"

        mPlayer = MediaPlayer().apply {
            try {
                setDataSource(mFileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    fun stopPlaying(view: View) {
        messageTextView.text = "play stop"

        mPlayer?.release()
        mPlayer = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFileName = "${externalCacheDir.absolutePath}/audiorecordtest.3gp"
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        setContentView(R.layout.activity_main)
    }

    fun changeTextView(view: View) {
        messageTextView.text = "Hello there! Again!"
    }

    override fun onStop() {
        super.onStop()
        mRecorder?.release()
        mRecorder = null
        mPlayer?.release()
        mPlayer = null
    }
}
