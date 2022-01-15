package com.example.musicplayer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {
    lateinit var binding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThhemeNav[MainActivity.themeIndex])
        binding= ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title="Feedback"
        binding.send.setOnClickListener {
            val subject=binding.suggestion.text.toString()
            val msg=binding.feedBack.text.toString()
            val na=binding.names.text.toString()
            if(subject.isNotEmpty()&&na.isNotEmpty()&&msg.isNotEmpty()){
                Toast.makeText(this,"Thanks for feedback!!",Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this,"Fill the empty fields!!",Toast.LENGTH_SHORT).show()
            }
        }
    }
}