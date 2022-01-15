package com.example.musicplayer

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class MusicService: Service(),AudioManager.OnAudioFocusChangeListener {
    private var myBinder=MyBinder()
    var mediaPlayerR:MediaPlayer ? = null
    private lateinit var mediaSession:MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager
    override fun onBind(intent: Intent?): IBinder? {
        mediaSession= MediaSessionCompat(baseContext,"My Music")
        return myBinder
    }




    inner class MyBinder:Binder() {
        fun current(): MusicService {
            return this@MusicService
        }
    }
        fun showNotification(playPauseBtn:Int) {
            val intent= Intent(baseContext,PlayerActivity::class.java)
            intent.putExtra("Index",PlayerActivity.songP)
            intent.putExtra("class","NowPlaying")
            val contentIntent=PendingIntent.getActivity(this,0,intent,0)
            val prevIntent= Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
            val prevPendingIntent= PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            val playIntent= Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
            val playPendingIntent= PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            val nextIntent= Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
            val nextPendingIntent= PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            val exitIntent= Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
            val exitPendingIntent= PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val imgArt= getImg(PlayerActivity.musicPlayer[PlayerActivity.songP].path)
            val images=if(imgArt!=null){
                BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
            }else{
                BitmapFactory.decodeResource(resources,R.drawable.music_player_icon_playstore_splash_screen)
            }
            val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(PlayerActivity.musicPlayer[PlayerActivity.songP].title)
                .setContentText(PlayerActivity.musicPlayer[PlayerActivity.songP].artist)
                .setSmallIcon(R.drawable.music_icon)
                .setLargeIcon(images)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
                .setPriority(NotificationCompat.VISIBILITY_PUBLIC)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.prev_icon, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "Play", playPendingIntent)
                .addAction(R.drawable.next_icon, "Next", nextPendingIntent)
                .addAction(R.drawable.exit_icon, "Exit", exitPendingIntent)
                .build()
            startForeground(13,notification)
    }
     fun create(){
        try{
            if(PlayerActivity.musicService!!.mediaPlayerR==null) PlayerActivity.musicService!!.mediaPlayerR= MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayerR!!.reset()
            PlayerActivity.musicService!!.mediaPlayerR!!.setDataSource(PlayerActivity.musicPlayer[PlayerActivity.songP].path)
            PlayerActivity.musicService!!.mediaPlayerR!!.prepare()

            PlayerActivity.binding.PlayPause.setIconResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            PlayerActivity.binding.tvStart.text= formatD(mediaPlayerR!!.currentPosition.toLong())
            PlayerActivity.binding.tvEnd.text= formatD(mediaPlayerR!!.duration.toLong())
            PlayerActivity.binding.seekBarPA.progress=0
            PlayerActivity.binding.seekBarPA.max= mediaPlayerR!!.duration
            PlayerActivity.now = PlayerActivity.musicPlayer[PlayerActivity.songP].id
        }
        catch (e:Exception){return}
    }
    fun seekBarSetup(){
        runnable= Runnable {
            PlayerActivity.binding.tvStart.text= formatD(mediaPlayerR!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress=mediaPlayerR!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    override fun onAudioFocusChange(Focus: Int) {
        if(Focus<=0) {
            PlayerActivity.isPlay = false
            mediaPlayerR!!.pause()
            PlayerActivity.binding.PlayPause.setIconResource(R.drawable.play_icon)
            NowPlaying.binding.playN.setIconResource(R.drawable.play_icon)

            showNotification(R.drawable.play_icon)
        }
//        }else{
//            PlayerActivity.isPlay =true
//
//           mediaPlayerR!!.start()
//            PlayerActivity.binding.PlayPause.setIconResource(R.drawable.pause_icon)
//            NowPlaying.binding.playN.setIconResource(R.drawable.pause_icon)
//            showNotification(R.drawable.pause_icon)
//
//        }
    }
}


