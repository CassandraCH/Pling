package fr.app.pling.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.app.pling.databinding.ActivityAccountBinding

/**
 * AccountActivity
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class AccountActivity : AppCompatActivity() {

    private lateinit var b: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       b = ActivityAccountBinding.inflate(layoutInflater)
       setContentView(b.root)
    }
}