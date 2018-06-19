package uk.co.projectprime.tetris

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import uk.co.projectprime.tetris.models.AppModel
import uk.co.projectprime.tetris.storage.AppPreferences
import uk.co.projectprime.tetris.view.TetrisView

class GameActivity : AppCompatActivity() {

    var highScore: TextView? = null
    var currentScore: TextView? = null
    private lateinit var tetrisView: TetrisView


    var appPreferences: AppPreferences? = null
    private val appModel: AppModel = AppModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        appPreferences= AppPreferences(this)

        appModel.setPreferences(appPreferences)


        val btnRestart = findViewById<Button>(R.id.btn_restart)
        highScore = findViewById(R.id.high_score)
        currentScore = findViewById(R.id.current_score)

        tetrisView = findViewById(R.id.view_tetris)
        tetrisView.setActivity(this)
        tetrisView.setModel(appModel)

        tetrisView.setOnTouchListener(this::onTetrisViewTouch)
        btnRestart.setOnClickListener(this::btnRestartClick)

        updateHighScore()
        updateCurrentScore()


//    End of onCreate method
    }

    private fun btnRestartClick(view: View){

        appModel.restartGame()

//        end of btnrestartclick
    }

    private fun onTetrisViewTouch(view: View, event: MotionEvent): Boolean{

        if(appModel.isGameOver()||appModel.isGameAwaitingStart()){
            appModel.startGame()
            tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
        } else if (appModel.isGameActive()) {

            when (resolveTouchDirection(view,event)){
                0 -> moveTetromino(AppModel.Motions.LEFT)
                1 -> moveTetromino(AppModel.Motions.ROTATE)
                2 -> moveTetromino(AppModel.Motions.DOWN)
                3 -> moveTetromino(AppModel.Motions.RIGHT)
            }


        }

        return true

//        end of onTetrisViewTouch
    }


    private fun resolveTouchDirection(view: View, event: MotionEvent):Int{

        val x = event.x / view.width
        val y = event.y / view.height
        val direction:Int

        direction = if (y>x){
            if (x>1-y)2 else 0
        } else {
            if (x>1-y)3 else 1
        }

        return direction



//        end of resolve method
    }


    private fun moveTetromino(motion:AppModel.Motions){

        if(appModel.isGameActive()){

            tetrisView.setGameCommand(motion)
        }


//        end of moveTetromino
    }





    private fun updateHighScore(){

        highScore?.text = "${appPreferences?.getHighScore()}"


//        end of updateHighScore method
    }

    private fun updateCurrentScore(){

        currentScore?.text = "0"


//        end of updateCurrentScore method
    }





//    End of ClaSS
}
