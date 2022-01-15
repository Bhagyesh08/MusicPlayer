package com.example.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerActivity : AppCompatActivity(), ServiceConnection,MediaPlayer.OnCompletionListener {
    companion object{
       lateinit var musicPlayer: ArrayList<Music>
       var songP:Int=0
        lateinit var binding: ActivityPlayerBinding
        var isPlay:Boolean=false
        var musicService:MusicService?=null
        var repeat:Boolean=false
        var min15:Boolean=false
        var min30:Boolean=false
        var min60:Boolean=false
        var now:String=""
        var isFavourite: Boolean=false
        var ind: Int=-1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThhemeNav[MainActivity.themeIndex])
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeL()
        binding.backPA.setOnClickListener {
            finish()
        }
        binding.PlayPause.setOnClickListener {
            if (isPlay) pauseMusic()
            else playMusic()
        }
        binding.prevPA.setOnClickListener {
            prevNext(increment = false)
        }
        binding.nextPA.setOnClickListener {
            prevNext(increment = true)
        }
        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService!!.mediaPlayerR!!.seekTo(progress)
                    musicService!!.showNotification(if (isPlay) R.drawable.pause_icon else R.drawable.play_icon)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit


            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit


        })
        binding.repeatPA.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.repeatPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            } else {
                repeat = false
                binding.repeatPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
            }
        }
        binding.equalizerPA.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayerR!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 13)
            } catch (e: java.lang.Exception) {
                Toast.makeText(this, "Equalizer Feature not Supported", Toast.LENGTH_SHORT).show()

            }

        }
        binding.timerPA.setOnClickListener {
            var timer = min15 || min30 || min60
            if (!timer) {
                showBottomSheet()
            } else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Do you want to stop timer?")
                    .setPositiveButton("Yes") { _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.timerPA.setColorFilter(
                            ContextCompat.getColor(
                                this,
                                R.color.cool_pink
                            )
                        )

                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            }
        }
        binding.sharePA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicPlayer[songP].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))
        }
        binding.favPA.setOnClickListener {
            ind== favCheck(musicPlayer[songP].id)
            if(isFavourite){
                isFavourite=false
                binding.favPA.setImageResource(R.drawable.empty_icon)
           FavouriteActivity.favSong.removeAt(ind)
            }else{
                isFavourite=true
                binding.favPA.setImageResource(R.drawable.favourite_icon)
                FavouriteActivity.favSong.add(musicPlayer[songP])
            }
        }
    }


    private fun setImage(){
        ind= favCheck(musicPlayer[songP].id)
        Glide.with(applicationContext)
            .load(musicPlayer[songP].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_playstore_splash_screen).centerCrop())
            .into(binding.songImg)
        binding.songPA.text= musicPlayer[songP].title
        if(repeat)  binding.repeatPA.setColorFilter(ContextCompat.getColor(applicationContext,R.color.purple_500))
   if(min15|| min30||min60) binding.timerPA.setColorFilter(ContextCompat.getColor(applicationContext,R.color.purple_500))
   if(isFavourite) binding.favPA.setImageResource(R.drawable.favourite_icon)
   else binding.favPA.setImageResource(R.drawable.empty_icon)
    }
    private fun create(){
        try{
        if(musicService!!.mediaPlayerR==null) musicService!!.mediaPlayerR= MediaPlayer()
        musicService!!.mediaPlayerR!!.reset()
        musicService!!.mediaPlayerR!!.setDataSource(musicPlayer[songP].path)
        musicService!!.mediaPlayerR!!.prepare()
            //musicService!!.mediaPlayerR!!.start()
        //isPlay=true
        //binding.PlayPause.setIconResource(R.drawable.pause_icon)
       //     musicService!!.showNotification(R.drawable.pause_icon)
      binding.tvStart.text= formatD(musicService!!.mediaPlayerR!!.currentPosition.toLong())
            binding.tvEnd.text= formatD(musicService!!.mediaPlayerR!!.duration.toLong())
       binding.seekBarPA.progress=0
            binding.seekBarPA.max= musicService!!.mediaPlayerR!!.duration
       musicService!!.mediaPlayerR!!.setOnCompletionListener(this)
now= musicPlayer[songP].id
playMusic()
        }
        catch (e:Exception){return}
    }
    private fun initializeL(){
        songP=intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){
            "FavouriteAdapter"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicPlayer= ArrayList()
                musicPlayer.addAll(FavouriteActivity.favSong)
                setImage()

            }
            "NowPlaying"->{
                setImage()
                binding.tvStart.text= formatD(musicService!!.mediaPlayerR!!.currentPosition.toLong())
binding.tvEnd.text= formatD(musicService!!.mediaPlayerR!!.duration.toLong())
           binding.seekBarPA.progress= musicService!!.mediaPlayerR!!.currentPosition
                binding.seekBarPA.max= musicService!!.mediaPlayerR!!.duration
            if(isPlay) binding.PlayPause.setIconResource(R.drawable.pause_icon)
                else binding.PlayPause.setIconResource(R.drawable.play_icon)
            }
           "MusicAdapterSearch"->{
               val intent = Intent(this, MusicService::class.java)
               bindService(intent, this, BIND_AUTO_CREATE)
               startService(intent)
               musicPlayer= ArrayList()
               musicPlayer.addAll(MainActivity.musicListSearch)
               setImage()

           }
            "MusicAdapter" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicPlayer=ArrayList()
                musicPlayer.addAll(MainActivity.musicListMA)
                setImage()
              // create()
            }
            "MainActivity"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicPlayer= ArrayList()
                musicPlayer.addAll(MainActivity.musicListMA)
                musicPlayer.shuffle()
                setImage()
              // create()
            }
            "FavouriteShuffle"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicPlayer= ArrayList()
                musicPlayer.addAll(FavouriteActivity.favSong)
                musicPlayer.shuffle()
                setImage()
            }
            "PlaylistDetailsAdapter"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicPlayer= ArrayList()
                musicPlayer.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPos].playlist)

                setImage()

            }
            "PlaylistShuffle"->{

                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicPlayer= ArrayList()
                musicPlayer.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPos].playlist)
                musicPlayer.shuffle()
                setImage()
            }
        }
if(musicService!=null&&!isPlay) playMusic()
    }
private fun playMusic(){
    isPlay=true

    musicService!!.mediaPlayerR!!.start()
    binding.PlayPause.setIconResource(R.drawable.pause_icon)
musicService!!.showNotification(R.drawable.pause_icon)

}
    private fun pauseMusic(){
        isPlay=false
        musicService!!.mediaPlayerR!!.pause()
        binding.PlayPause.setIconResource(R.drawable.play_icon)
   musicService!!.showNotification(R.drawable.play_icon)


    }
    private fun prevNext(increment: Boolean){
        if(increment){
            setSongP(increment = true)
          //  ++songP
            setImage()
            create()

        }else{
            setSongP(increment=false)
           //--songP
            setImage()
            create()
        }
    }


    override fun onServiceConnected(name: ComponentName?,service: IBinder?) {
        if (musicService == null) {
            val binder = service as MusicService.MyBinder
            musicService = binder.current()


            musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            musicService!!.audioManager.requestAudioFocus(
                musicService,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        create()
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService=null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongP(true)
        create()
        try{setImage()}catch (e:Exception){return}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
  if(requestCode==13||requestCode== RESULT_OK){
return
  }
    }
    private fun showBottomSheet(){
        val dialog=BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            Toast.makeText(baseContext,"Music stop after 15 min",Toast.LENGTH_SHORT).show()
            binding.timerPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
           min15=true
Thread{Thread.sleep((15*60000).toLong())
            if(min15)
                exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(baseContext,"Music stop after 30 min",Toast.LENGTH_SHORT).show()
            binding.timerPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            min30=true
            Thread{Thread.sleep((30*60000).toLong())
                if(min30)
                    exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener{
            Toast.makeText(baseContext,"Music stop after 60 min",Toast.LENGTH_SHORT).show()
            binding.timerPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            min60=true
            Thread{Thread.sleep((60*60000).toLong())
                if(min60)
                    exitApplication()}.start()
            dialog.dismiss()
        }
    }
}



