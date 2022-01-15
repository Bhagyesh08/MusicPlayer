package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NotificationReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
when(intent?.action){
    ApplicationClass.PREVIOUS-> prevNextSong(increment = false, context = context!!)
    ApplicationClass.PLAY-> if(PlayerActivity.isPlay) pauseMusic() else playMusic()
    ApplicationClass.NEXT-> prevNextSong(increment = true,context=context!!)
ApplicationClass.EXIT-> {
    exitApplication()
}
}
    }
    private fun playMusic(){
        PlayerActivity.isPlay=true
        PlayerActivity.musicService!!.mediaPlayerR!!.start()
    PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
PlayerActivity.binding.PlayPause.setIconResource(R.drawable.pause_icon)
    NowPlaying.binding.playN.setIconResource(R.drawable.pause_icon)
    }
    private fun pauseMusic(){
        PlayerActivity.isPlay=false
        PlayerActivity.musicService!!.mediaPlayerR!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.PlayPause.setIconResource(R.drawable.play_icon)
    NowPlaying.binding.playN.setIconResource(R.drawable.play_icon)
    }
    private fun prevNextSong(increment:Boolean,context: Context){
        setSongP(increment=increment)
        PlayerActivity.musicService!!.create()
        Glide.with(context)
            .load(PlayerActivity.musicPlayer[PlayerActivity.songP].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_playstore_splash_screen).centerCrop())
            .into(PlayerActivity.binding.songImg)
        PlayerActivity.binding.songPA.text= PlayerActivity.musicPlayer[PlayerActivity.songP].title
        Glide.with(context)
            .load(PlayerActivity.musicPlayer[PlayerActivity.songP].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_playstore_splash_screen).centerCrop())
            .into(NowPlaying.binding.imgN)
        NowPlaying.binding.nameN.text=PlayerActivity.musicPlayer[PlayerActivity.songP].title
        playMusic()
        PlayerActivity.ind= favCheck(PlayerActivity.musicPlayer[PlayerActivity.songP].id)
        if(PlayerActivity.isFavourite)  PlayerActivity.binding.favPA.setImageResource(R.drawable.favourite_icon)
        else PlayerActivity.binding.favPA.setImageResource(R.drawable.empty_icon)
    }
}