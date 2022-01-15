package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySelectionBinding.inflate(layoutInflater)
        setTheme(MainActivity.currentThhemeNav[MainActivity.themeIndex])
        setContentView(binding.root)
        binding.selectionRV.setItemViewCacheSize(10)
        binding.selectionRV.setHasFixedSize(true)
        binding.selectionRV.layoutManager= LinearLayoutManager(this)

        adapter= MusicAdapter(this,MainActivity.musicListMA, selection = true)
        binding.selectionRV.adapter=adapter
        binding.backSA.setOnClickListener { finish() }
        binding.searchSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean =true



            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if(newText!=null){
                    val user=newText.lowercase()
                    for(song in MainActivity.musicListMA)
                        if(song.title.lowercase().contains(user))
                            MainActivity.musicListSearch.add(song)
                    MainActivity.search =true
                    adapter.update(searchList = MainActivity.musicListSearch)
                }
                return true
            }
        })
    }
}