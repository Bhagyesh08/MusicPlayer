package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.FragmentNowPlayingBinding

class NowPlaying: Fragment(){
    companion object{
        lateinit var binding:FragmentNowPlayingBinding
    }
 override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_now_playing, container, false)
 binding= FragmentNowPlayingBinding.bind(view)
     binding.root.visibility=View.INVISIBLE
     binding.playN.setOnClickListener {
         if(PlayerActivity.isPlay)pauseMusic()else playMusic()
     }
     binding.nextN.setOnClickListener {
         setSongP(increment=true)
         PlayerActivity.musicService!!.create()

         Glide.with(requireContext())
             .load(PlayerActivity.musicPlayer[PlayerActivity.songP].artUri)
             .apply(RequestOptions().placeholder(R.drawable.music_player_icon_playstore_splash_screen).centerCrop())
             .into(binding.imgN)
         binding.nameN.text=PlayerActivity.musicPlayer[PlayerActivity.songP].title
         PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
         playMusic()
     }
     binding.root.setOnClickListener {
         val intent= Intent(requireContext(),PlayerActivity::class.java)
         intent.putExtra("Index",PlayerActivity.songP)
         intent.putExtra("class","NowPlaying")
         ContextCompat.startActivity(requireContext(),intent,null)

     }
     return view
 }

    override fun onResume() {
        super.onResume()
        if(PlayerActivity.musicService!=null){
            binding.root.visibility=View.VISIBLE
            binding.nameN.isSelected=true
            Glide.with(this)
                .load(PlayerActivity.musicPlayer[PlayerActivity.songP].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon_playstore_splash_screen).centerCrop())
                .into(binding.imgN)
            binding.nameN.text=PlayerActivity.musicPlayer[PlayerActivity.songP].title
       if(PlayerActivity.isPlay) binding.playN.setIconResource(R.drawable.pause_icon)
            else binding.playN.setIconResource(R.drawable.play_icon)
        }
    }
private fun playMusic(){
   PlayerActivity.musicService!!.mediaPlayerR!!.start()
    binding.playN.setIconResource(R.drawable.pause_icon)
    PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
    PlayerActivity.binding.nextPA.setIconResource(R.drawable.pause_icon)
    PlayerActivity.isPlay=true

}
    private fun pauseMusic(){
        PlayerActivity.musicService!!.mediaPlayerR!!.pause()
        binding.playN.setIconResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.nextPA.setIconResource(R.drawable.play_icon)
        PlayerActivity.isPlay=false
    }
}