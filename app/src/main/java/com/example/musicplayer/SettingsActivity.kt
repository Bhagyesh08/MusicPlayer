package com.example.musicplayer

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThhemeNav[MainActivity.themeIndex])
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Settings"
        when (MainActivity.themeIndex) {
            0 -> binding.cp.setBackgroundColor(Color.YELLOW)
            1 -> binding.bp.setBackgroundColor(Color.YELLOW)
      2->binding.cb.setBackgroundColor(Color.YELLOW)
        }
        binding.cp.setOnClickListener {
            saveTheme(0)
        }
        binding.bp.setOnClickListener {
            saveTheme(1)
        }
        binding.cb.setOnClickListener {
            saveTheme(2)
        }
        binding.version.text=setVersion()
    }

    private fun saveTheme(Index: Int) {
        if (MainActivity.themeIndex != Index) {
            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeIndex", Index)
            editor.apply()

            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Exit")
                .setMessage("Do you want to close app?")
                .setPositiveButton("Yes") { _, _ ->

                    exitApplication()

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

    private fun setVersion(): String {
        return "Version Name: ${BuildConfig.VERSION_NAME}"
    }
}