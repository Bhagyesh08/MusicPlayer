package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter
    companion object{
       lateinit var musicListMA:ArrayList<Music>
   lateinit var musicListSearch:ArrayList<Music>
    var search:Boolean=false
        var themeIndex:Int=0
        val currentTheme= arrayOf(R.style.coolPink,R.style.coolBlack,R.style.coolBlue)
        val currentThhemeNav= arrayOf(R.style.coolPinkNav,R.style.coolBlackNav,R.style.coolBlueNav)
    val currGrad= arrayOf(R.drawable.gradient_pink,R.drawable.gradient_black,R.drawable.gradient_blue)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
val themeE=getSharedPreferences("THEMES", MODE_PRIVATE)
     themeIndex=   themeE.getInt("themeIndex",0)
        setTheme(currentThhemeNav[themeIndex])
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toggle= ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
       if(Permission()) {
           initializeLayout()
           FavouriteActivity.favSong = ArrayList()
           val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
           val jsonString = editor.getString("FavouriteSongs", null)
           val typeToken = object : TypeToken<ArrayList<Music>>() {}.type
           if (jsonString != null) {
               val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
               FavouriteActivity.favSong.addAll(data)
           }
           PlaylistActivity.musicPlaylist = MusicPlaylist()

           val jsonStringPL = editor.getString("MusicPlaylist", null)

           if (jsonStringPL != null) {
               val dataPL: MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPL,MusicPlaylist::class.java)
               PlaylistActivity.musicPlaylist=dataPL
           }
       }

binding.shuffleE.setOnClickListener{
    val intent=Intent(this,PlayerActivity::class.java)
    intent.putExtra("index",0)
    intent.putExtra("class","MainActivity")
    startActivity(intent)

}
        binding.favoriteE.setOnClickListener {
            val intent=Intent(this,FavouriteActivity::class.java)
       startActivity(intent)
        }
        binding.playlistE.setOnClickListener {
       startActivity(Intent(this,PlaylistActivity::class.java))
         }


        binding.navMenu.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.feedback-> {
                    startActivity(Intent(this,FeedbackActivity::class.java))
                }
                R.id.settings-> {
                   startActivity(Intent(this,SettingsActivity::class.java))
                }
                R.id.about-> {
                    val intent=Intent(this,AboutActivity::class.java)
                    startActivity(intent)
                }
R.id.exit->{
    val builder= MaterialAlertDialogBuilder(this)
    builder.setTitle("Exit")
        .setMessage("Do you want to close app?")
        .setPositiveButton("Yes") { _, _ ->
            exitApplication()

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
            true
        }
    }
    private fun Permission():Boolean{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),5)
return false
        }
return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==5){
            if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
       Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show()
        initializeLayout()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),5)

        }


    }
    override  fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }
    private fun initializeLayout(){

search=false
musicListMA=getAudio()




        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager=LinearLayoutManager(this@MainActivity)
        musicAdapter= MusicAdapter(this@MainActivity, musicListMA)
        binding.musicRV.adapter=musicAdapter
        binding.totalSongs.text="Total Songs : "  +musicAdapter.itemCount
    }

@SuppressLint("Range")
private fun getAudio():ArrayList<Music> {
    val list = ArrayList<Music>()
    val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATE_ADDED,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ALBUM_ID
    )
    val cursor = this.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection, selection, null, MediaStore.Audio.Media.DATE_ADDED
                + " DESC", null
    )
    if (cursor != null){


        if (cursor.moveToFirst())
            do {

                val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    ?: "Unknown"
                val idC =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)) ?: "Unknown"
                val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    ?: "Unknown"
                val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    ?: "Unknown"
                val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val durationC =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val albumIdC =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                        .toString()
                val uri = Uri.parse("context://media/external/audio/albumart")
                val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                val music = Music(
                    id = idC,
                    title = titleC,
                    album = albumC,
                    artist = artistC,
                    path = pathC,
                    duration = durationC,
                    artUri = artUriC
                )
                val file = File(music.path)
                if (file.exists())
                    list.add(music)
            } while (cursor.moveToNext())
            cursor!!.close()
        }
return list
}

    override fun onDestroy() {
        super.onDestroy()
          if(!PlayerActivity.isPlay&&PlayerActivity.musicService!=null){
              exitApplication()
          }

    }

    override fun onResume() {
        super.onResume()
        val editor=getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString= GsonBuilder().create().toJson(FavouriteActivity.favSong)
        editor.putString("FavouriteSongs",jsonString)
        val jsonStringPlaylist= GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menuInflater.inflate(R.menu.search_view_menu,menu)
        findViewById<LinearLayout>(R.id.navL)?.setBackgroundResource(currGrad[themeIndex])
        val searchView=menu?.findItem(R.id.searchView)?.actionView as SearchView
       searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
           override fun onQueryTextSubmit(query: String?): Boolean =true



           override fun onQueryTextChange(newText: String?): Boolean {
               musicListSearch= ArrayList()
if(newText!=null){
    val user=newText.lowercase()
    for(song in musicListMA)
        if(song.title.lowercase().contains(user))
            musicListSearch.add(song)
    search=true
    musicAdapter.update(searchList = musicListSearch)
}
               return true
           }
       })
        return super.onCreateOptionsMenu(menu)
    }
}