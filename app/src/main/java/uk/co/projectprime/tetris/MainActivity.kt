package uk.co.projectprime.tetris

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import android.widget.TextView
import uk.co.projectprime.tetris.storage.AppPreferences


class MainActivity : AppCompatActivity() {

var highScore: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()


        val btnNewGame = findViewById<Button>(R.id.new_game)
        val btnResetScore = findViewById<Button>(R.id.reset_score)
        val btnExit = findViewById<Button>(R.id.exit)
        highScore = findViewById(R.id.score)

        btnNewGame.setOnClickListener(this::onBtnNewGameClick)
        btnResetScore.setOnClickListener(this::onBtnResetScoreClick)
        btnExit.setOnClickListener(this::onBtnExitClick)



//        End of onCreate
    }



//    new game method
    private fun onBtnNewGameClick(view: View){

    val intent = Intent (this, GameActivity::class.java)
    startActivity(intent)



//    End of new game button click method
}

    //    reset score method
    private fun onBtnResetScoreClick(view: View){

        val preferences = AppPreferences(this)
        preferences.clearHighScore()
        Snackbar.make(view, "Score has been successfully reset",
                Snackbar.LENGTH_SHORT).show()
        highScore?.text= "${preferences.getHighScore()}"



//    End of reset score button click method
    }

    //    reset score method
    private fun onBtnExitClick(view: View){

        System.exit(0)



//    End of reset score button click method
    }





//    End of class
}
