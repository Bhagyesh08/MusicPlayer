package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adapter: FavouriteAdapter

    companion object {
        var favSong: ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThhemeNav[MainActivity.themeIndex])
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
favSong= checkPlaylist(favSong)

        binding.backFA.setOnClickListener {
            finish()
        }
        binding.favRV.setHasFixedSize(true)
        binding.favRV.setItemViewCacheSize(13)
        binding.favRV.layoutManager = GridLayoutManager(this, 4)
        adapter = FavouriteAdapter(this, favSong)
        binding.favRV.adapter = adapter
if(favSong.size<1) binding.shuffleFA.visibility= View.INVISIBLE
    binding.shuffleFA.setOnClickListener {
        val intent= Intent(this,PlayerActivity::class.java)
        intent.putExtra("index",0)
        intent.putExtra("class","FavouriteShuffle")
        startActivity(intent)
    }
    }
}