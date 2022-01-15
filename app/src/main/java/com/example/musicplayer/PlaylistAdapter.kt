package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.PalylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PlaylistAdapter(private val context: Context, private var playList: ArrayList<Playlist>) : RecyclerView.Adapter<PlaylistAdapter.MyHolder>() {
    class MyHolder(binding: PalylistViewBinding): RecyclerView.ViewHolder(binding.root){

        val im=binding.imgPL
        val nm=binding.namePL
        val root = binding.root
        val delete=binding.btnPL
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PalylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.nm.text = playList[position].name
        holder.nm.isSelected=true
        holder.delete.setOnClickListener {
            val builder= MaterialAlertDialogBuilder(context)
            builder.setTitle(playList[position].name)
                .setMessage("Do you want to  delete playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                PlaylistActivity.musicPlaylist.ref.removeAt(position)
                   refreshPlaylist()
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
        holder.root.setOnClickListener{
val intent=Intent(context,PlaylistDetails::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
        }
        if(PlaylistActivity.musicPlaylist.ref[position].playlist.size>0){
            Glide.with(context)
                .load(PlaylistActivity.musicPlaylist.ref[position].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon_playstore_splash_screen).centerCrop())
                .into(holder.im)
        }
    }

    override fun getItemCount(): Int {
        return playList.size
    }
fun refreshPlaylist(){
    playList= ArrayList()
    playList.addAll(PlaylistActivity.musicPlaylist.ref)
    notifyDataSetChanged()

}

}