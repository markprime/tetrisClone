package uk.co.projectprime.tetris.models

import android.graphics.Point
import uk.co.projectprime.tetris.constants.FieldConstants
import uk.co.projectprime.tetris.helpers.array2dOfByte
import uk.co.projectprime.tetris.storage.AppPreferences
import uk.co.projectprime.tetris.constants.CellConstants

class AppModel{





    var score: Int = 0
    private var preferences: AppPreferences?=null
    var currentBlock:Block?=null
    var currentState:String=Statuses.AWAITING_START.name

    private var field: Array<ByteArray> = array2dOfByte(
            FieldConstants.ROW_COUNT.Value,
            FieldConstants.COLUMN_COUNT.Value
        )


    fun setPreferences(preferences: AppPreferences?){

        this.preferences=preferences

//        end of SetPreferences
    }

    fun getCellStatus(row: Int, column: Int): Byte?{

        return field[row][column]

//        end of getCellStatus
    }

    private fun setCellStatus(row:Int,column:Int,status:Byte?){

        if(status !=null){

            field[row][column] = status
        }

//        end of setCellStatus
    }

    fun isGameOver():Boolean{

        return currentState==Statuses.OVER.name


//        end of isGameOVer
    }

    fun isGameActive():Boolean{

        return currentState==Statuses.ACTIVE.name


//        end of isGameActive
    }

    fun isGameAwaitingStart():Boolean{

        return currentState==Statuses.AWAITING_START.name


//        end of isGameAwaitingStart
    }

    private fun boostScore(){

        score += 10

        if(score>preferences?.getHighScore() as Int)
            preferences?.saveHighScore(score)


//        end of boostScore
    }


    private fun generateNextBlock(){

        currentBlock = Block.createBlock()

//        end of genNextBlock
    }

    private fun validTranslation(position: Point, shape: Array<ByteArray>): Boolean{

        return if (position.y <0 || position.x < 0){

            false
        } else if (position.x + shape[0].size > FieldConstants.COLUMN_COUNT.Value){

            false
        }else{
            for (i in 0 until shape.size){
                for (j in 0 until shape[i].size){
                    val y = position.y + i
                    val x = position.x + j

                    if(CellConstants.EMPTY.value !=shape[i][j] && CellConstants.EMPTY.value !=field[y][x] ){
                        return false

                    }
                }
            }
            true
        }




//        end of validTranslation
    }


    private fun moveValid(position:Point, frameNumber:Int?):Boolean{

        val shape: Array<ByteArray>?=currentBlock?.getShape(frameNumber as Int)
        return validTranslation(position, shape as Array<ByteArray>)

//        end of moveValid method
    }

    fun generateField(action:String){

        if(isGameActive()){
            resetField()
            var frameNumber: Int?= currentBlock?.frameNumber
            val coordinate:Point?= Point()
            coordinate?.x = currentBlock?.position?.x
            coordinate?.y = currentBlock?.position?.y

            when (action){

                Motions.LEFT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.minus(1)

                }

                Motions.RIGHT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.plus(1)

                }

                Motions.DOWN.name -> {
                    coordinate?.y = currentBlock?.position?.y?.plus(1)

                }

                Motions.ROTATE.name -> {

                    frameNumber = frameNumber?.plus(1)

                    if(frameNumber !=null){
                        if(frameNumber >= currentBlock?.frameCount as Int){
                            frameNumber = 0
                        }
                    }

                }


            }

            if (!moveValid(coordinate as Point, frameNumber)){
                translateBlock(currentBlock?.position as Point, currentBlock?.frameNumber as Int)
                if(Motions.DOWN.name == action){
                    boostScore()
                    persistCellData()
                    assessField()
                    generateNextBlock()

                    if(!blockAdditionPossible()){

                        currentState = Statuses.OVER.name;
                        currentBlock = null;
                        resetField(false);

                    }
                }
            } else{
                if(frameNumber !=null){
                    translateBlock(coordinate, frameNumber)
                    currentBlock?.setState(frameNumber,coordinate)
                }
            }


        }

//        end of generateField
    }


    private fun resetField (emphemeralCellsOnly:Boolean=true){

        for (i in 0 until FieldConstants.ROW_COUNT.Value){
            (0 until FieldConstants.COLUMN_COUNT.Value).filter{
                !emphemeralCellsOnly||field[i][it] == CellConstants.EPHEMERAL.value
            }.forEach{
                field[i][it]=CellConstants.EMPTY.value
            }
        }

//        end of resetField method
    }


    private fun persistCellData(){

        for(i in 0 until field.size){
            for(j in 0 until field[i].size){
                var status = getCellStatus(i,j)

                if(status == CellConstants.EPHEMERAL.value){
                    status = currentBlock?.staticValue
                    setCellStatus(i,j,status)
                }
            }
        }


//        end of persistCellData
    }


    private fun assessField(){

        for(i in 0 until field.size){

            var emptyCells=0;

            for(j in 0 until field[i].size){

                val status = getCellStatus(i,j)
                val isEmpty = CellConstants.EMPTY.value == status
                if (isEmpty) emptyCells++
            }
            if(emptyCells == 0)
                shiftRows(i)
        }


//        end of assessField
    }

    private fun translateBlock(position: Point, frameNumber: Int){

        synchronized(field){

            val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber)

            if(shape !=null){

                for(i in shape.indices){
                    for (j in 0 until shape[i].size){
                        val y = position.y+i
                        val x = position.x+j

                        if(CellConstants.EMPTY.value !=shape[i][j]){
                            field[y][x] =shape[i][j]
                        }
                    }
                }
            }
        }


//        end of translateBlock
    }

    private fun blockAdditionPossible():Boolean{

        if(!moveValid(currentBlock?.position as Point, currentBlock?.frameNumber)){
            return false
        }

        return true


//        end of blockAdditionPossible
    }


    private fun shiftRows(nToRow: Int){

        if(nToRow>0){
            for(j in nToRow - 1 downTo 0){

                for(m in 0 until field[j].size){

                    setCellStatus(j+1,m,getCellStatus(j, m))
                }
            }

        }
        for (j in 0 until field[0].size){
            setCellStatus(0,j,CellConstants.EMPTY.value)
        }


//        end of shiftRows
    }

    fun startGame(){

        if(!isGameActive()){

            currentState=Statuses.ACTIVE.name
            generateNextBlock()
        }

//        end of startGame
    }

    fun restartGame(){

        resetModel()
        startGame()

//        end of restartGame
    }

    fun endGame(){

        score = 0
        currentState=AppModel.Statuses.OVER.name

//        end of endGame
    }

    private fun resetModel(){

        resetField(false)
        currentState=Statuses.AWAITING_START.name
        score = 0
    }










    enum class Statuses{

        AWAITING_START, ACTIVE, INACTIVE, OVER

//        end of Statuses
    }

    enum class Motions{

        LEFT, RIGHT,DOWN, ROTATE


//        end of Motions
    }






//    end of AppModel class

}