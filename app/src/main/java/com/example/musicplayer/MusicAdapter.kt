package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.MusicViewBinding

class MusicAdapter(private val context: Context, private var musicList: ArrayList<Music>, private val playlistDetails:Boolean=false, private val selection:Boolean=false) : RecyclerView.Adapter<MusicAdapter.MyHolder>() {
    class MyHolder(binding: MusicViewBinding):RecyclerView.ViewHolder(binding.root){
     val title=binding.songName
        val album=binding.albumV
        val image=binding.mv
        val duration=binding.songDuration
        val root=binding.root


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyHolder {
       return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicAdapter.MyHolder, position: Int) {
   holder.title.text=musicList[position].title
        holder.album.text=musicList[position].album

        holder.duration.text= formatD(musicList[position].duration)
   Glide.with(context)
       .load(musicList[position].artUri)
       .apply(RequestOptions().placeholder(R.drawable.music_player_icon_playstore_splash_screen).centerCrop())
       .into(holder.image)
        when{
          playlistDetails->{
              holder.root.setOnClickListener {
                  sendIntent(ref="PlaylistDetailsAdapter", pos = position)
              }
          }
            selection->{
                holder.root.setOnClickListener {
                    if(addSong(musicList[position])){
                        holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.cool_pink))
                    }else{
                        holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                    }
                }
            }
          else-> {
              holder.root.setOnClickListener {
                  when {
                      MainActivity.search -> sendIntent(ref = "MusicAdapterSearch", pos = position)
                      musicList[position].id == PlayerActivity.now ->
                          sendIntent(ref = "NowPlaying", pos = PlayerActivity.songP)
                      else -> sendIntent("MusicAdapter", pos = position)
                  }
              }
          }

        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
    fun update(searchList:ArrayList<Music>){
        musicList= ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun sendIntent(ref:String,pos:Int){
        val intent= Intent(context,PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }
    private fun addSong(song:Music):Boolean{
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPos].playlist.forEachIndexed{index, music ->
         if(song.id==music.id){
             PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPos].playlist.removeAt(index)
             return false
         }
        }

        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPos].playlist.add(song)
    return true
    }
    fun refreshSelection(){
        musicList= ArrayList()
        musicList=PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPos].playlist
    notifyDataSetChanged()
    }
}