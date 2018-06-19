package uk.co.projectprime.tetris.storage

import android.content.Context
import android.content.SharedPreferences


class AppPreferences(ctx: Context){

    var data: SharedPreferences = ctx.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

    fun saveHighScore(highScore: Int){
        data.edit().putInt("HIGH_SCORE", highScore).apply()
//    end of saveHighScore method
    }

    fun getHighScore():Int {

        return data.getInt("HIGH_SCORE", 0)

//        end of getHighScore method
    }

    fun clearHighScore(){

        data.edit().putInt("HIGH_SCORE",0).apply()

//        end of clearHighScore method
    }


//    end of class
}