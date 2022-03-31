package fr.app.pling.view

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.app.pling.R
import fr.app.pling.databinding.ActivityMainBinding

/**
 * MainActivity
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class MainActivity : AppCompatActivity() {

    private lateinit var b : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // data binding
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)

        // link between buttons and views/fragments
        val navView: BottomNavigationView = b.bottomNavigationMain
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        b.ivAdd.setOnClickListener {
            // Navigate to the fragment New Project
            navController.navigate(R.id.navigation_new_project)
        }

        navController.addOnDestinationChangedListener { _, _, arguments ->
            b.toolbar.isVisible = arguments?.getBoolean("isShowingToolbar", false) == true
            val isVisible = arguments?.getBoolean("ShowAppBar", false) == true

            b.ivAdd.isVisible = isVisible
            b.bottomNavigationMain.isVisible = isVisible
        }

        navView.setupWithNavController(navController)
    }

    /**
     * Setup the toolbar
     */
    fun setToolbar(toolbar: Toolbar, title : String, hasReturn : Boolean){
        setSupportActionBar(toolbar)
        val textView = findViewById<TextView>(R.id.toolbar_title)
        textView.text = title

        // display the return button
        if(hasReturn){
            supportActionBar?.setDisplayHomeAsUpEnabled(hasReturn)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_previous)
        }
    }
}