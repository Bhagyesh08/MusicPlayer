package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.FavouriteVewBinding

class FavouriteAdapter(private val context: Context, private var musicList: ArrayList<Music>) : RecyclerView.Adapter<FavouriteAdapter.MyHolder>() {
    class MyHolder(binding: FavouriteVewBinding): RecyclerView.ViewHolder(binding.root){

val im=binding.imgFV
        val nm=binding.nameFv
val root = binding.root
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(FavouriteVewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
holder.nm.text=musicList[position].title
            Glide.with(context)
                .load(musicList[position].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon_playstore_splash_screen).centerCrop())
                .into(holder.im)
        holder.root.setOnClickListener{
            val intent= Intent(context,PlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","FavouriteAdapter")
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }


}