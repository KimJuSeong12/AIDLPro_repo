package com.example.processaidlservicepro

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class MyAIDLService : Service() {
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onBind(intent: Intent): IBinder {
        return object : MyAIDLInterface.Stub() {
            override fun getMaxDuration(): Int {
                if (mediaPlayer.isPlaying) {
                    return mediaPlayer.duration
                } else {
                    return 0
                }
            }

            override fun start() {
                try {
                    if (!mediaPlayer.isPlaying) {
                        mediaPlayer = MediaPlayer.create(this@MyAIDLService, R.raw.my)
                        mediaPlayer.start()
                    }
                } catch (e: java.lang.Exception) {
                    Log.e("MyAIDLService", "${e.printStackTrace()}")
                }

            }

            override fun stop() {
                try {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                    }
                } catch (e: java.lang.Exception) {
                    Log.e("MyAIDLService", "${e.printStackTrace()}")
                }

            }

        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}