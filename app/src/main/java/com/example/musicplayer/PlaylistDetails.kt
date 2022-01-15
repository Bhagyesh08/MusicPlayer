package com.example.musicplayer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var adapter:MusicAdapter
   companion object{
       var currentPos:Int=-1
   }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThhemeNav[MainActivity.themeIndex])
        binding= ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPos= intent.extras?.get("index") as Int
       try{ PlaylistActivity.musicPlaylist.ref[currentPos].playlist= checkPlaylist(playlist = PlaylistActivity.musicPlaylist.ref[currentPos].playlist)}
       catch (e:Exception){}
        binding.playPD.setItemViewCacheSize(10)
        binding.playPD.setHasFixedSize(true)
        binding.playPD.layoutManager=LinearLayoutManager(this)
        adapter= MusicAdapter(this,PlaylistActivity.musicPlaylist.ref[currentPos].playlist,playlistDetails = true)
   binding.playPD.adapter=adapter
        binding.backPD.setOnClickListener {
            finish()
        }
        binding.shufflePD.setOnClickListener {
            val intent= Intent(this,PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","PlaylistShuffle")
            startActivity(intent)
        }
        binding.addPD.setOnClickListener {
            startActivity(Intent(this,SelectionActivity::class.java))
        }
        binding.removePD.setOnClickListener {
            val builder= MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to remove all songs from playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                 PlaylistActivity.musicPlaylist.ref[currentPos].playlist.clear()
                    adapter.refreshSelection()
                    dialog.dismiss()

                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog= builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.namePD.text=PlaylistActivity.musicPlaylist.ref[currentPos].name
        binding.infoPD.text="Total ${adapter.itemCount} Songs.\n\n" +
                "Created On:\n${PlaylistActivity.musicPlaylist.ref[currentPos].On}\n\n"+
                " -- ${PlaylistActivity.musicPlaylist.ref[currentPos].by}"
        if(adapter.itemCount>0){
            Glide.with(this)
                .load(PlaylistActivity.musicPlaylist.ref[currentPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon_playstore_splash_screen).centerCrop())
                .into(binding.imgPD)
            binding.shufflePD.visibility= View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        val editor=getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()

        val jsonStringPlaylist= GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)
        editor.apply()

    }

}