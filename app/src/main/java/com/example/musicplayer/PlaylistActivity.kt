package com.example.musicplayer
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.example.musicplayer.databinding.AddPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PlaylistActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityPlaylistBinding
   private lateinit var adapter:PlaylistAdapter
   companion object{
    var musicPlaylist: MusicPlaylist=MusicPlaylist()

   }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThhemeNav[MainActivity.themeIndex])
        binding= ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)





        binding.playPL.setHasFixedSize(true)
        binding.playPL.setItemViewCacheSize(13)
        binding.playPL.layoutManager= GridLayoutManager(this@PlaylistActivity,2)
        adapter= PlaylistAdapter(this, playList = musicPlaylist.ref)
        binding.playPL.adapter=adapter


        binding.backP.setOnClickListener {
            finish()
        }
        binding.addPL.setOnClickListener {
            customAlert()
        }
    }
    private fun customAlert(){
        val customDialog=LayoutInflater.from(this).inflate(R.layout.add_playlist,binding.root,false)
        val binder= AddPlaylistBinding.bind(customDialog)
        val builder= MaterialAlertDialogBuilder(this)
      val dialog=  builder.setView(customDialog)
        .setTitle("Playlist Details")

            .setPositiveButton("Add") { dialog, _ ->
                val playlistName=binder.playlistPL.text
                val created=binder.PLname.text
                if(playlistName!=null&&created!=null){
                    if(playlistName.isNotEmpty()&&created.isNotEmpty()){
                        addPlaylist(playlistName.toString(),created.toString())
                    }
                }
dialog.dismiss()

            }.create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)

    }
    private fun addPlaylist(name: String,created:String){
       var playlistExist=false
       for(i in musicPlaylist.ref){
           if(name.equals(i.name)){
               playlistExist=true
               break
               }
       }
        if(playlistExist) Toast.makeText(this,"Playlist Exist!!",Toast.LENGTH_SHORT).show()
        else{
            val tempPL=Playlist()
            tempPL.name=name
            tempPL.playlist= ArrayList()
            tempPL.by=created
            val calender=java.util.Calendar.getInstance().time
            val sdf=SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPL.On=sdf.format(calender)
            musicPlaylist.ref.add(tempPL)
            adapter.refreshPlaylist()
        }

    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}