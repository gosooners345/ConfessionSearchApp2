package com.confessionsearch.release1

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.confessionsearch.release1.data.notes.Notes
import com.confessionsearch.release1.databinding.ActivityMainBinding
import com.confessionsearch.release1.ui.notesActivity.NotesComposeActivity
import com.confessionsearch.release1.ui.notesActivity.NotesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vdx.designertoast.DesignerToast


class MainActivity : AppCompatActivity() {

    val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            var binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_notes,
                    R.id.navigation_bible,
                    R.id.navigation_help

                )
            )

            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        } catch (ex: Exception) {
            ex.printStackTrace()
            DesignerToast.Error(this, ex.message, Gravity.BOTTOM, Toast.LENGTH_LONG)
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (Configuration.UI_MODE_NIGHT_MASK and resources.configuration.uiMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                var restart = Intent(context, MainActivity::class.java)
                Log.i("ConfigChange", "Restarting Actviity due to UI Change")
                finish()
                startActivity(restart)
            }

            Configuration.UI_MODE_NIGHT_YES -> {
                setDefaultNightMode(MODE_NIGHT_YES)
                var restart = Intent(context, MainActivity::class.java)
                Log.i("ConfigChange", "Restarting Actviity due to UI Change")
                finish()
                startActivity(restart)
            }
        }
    }

    //required to be here for adding notes to the note list fragment
    fun NewNote(view: View?) {
        val intent = Intent(context, NotesComposeActivity::class.java)
        intent.putExtra("activity_ID", NotesFragment.ACTIVITY_ID)
        startActivity(intent)

    }

    override fun onBackPressed() {
        this.finish()
    }

    //Pass any static variables along here
    companion object {
        var notesArrayList = ArrayList<Notes>()


    }


}

