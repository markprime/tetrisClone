package uk.co.projectprime.tetris.view

import android.graphics.Paint
import android.view.View
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.Toast



import uk.co.projectprime.tetris.GameActivity
import uk.co.projectprime.tetris.models.AppModel
import uk.co.projectprime.tetris.constants.CellConstants
import uk.co.projectprime.tetris.constants.FieldConstants
import uk.co.projectprime.tetris.models.Block


class TetrisView: View {

    private val paint = Paint()
    private var lastMove: Long = 0
    internal var model: AppModel?=null
    internal var activity: GameActivity?=null
    private val viewHandler= ViewHandler(this)
    private var cellSize: Dimension = Dimension(0,0)
    private var frameOffset: Dimension = Dimension(0,0)
    constructor(context: Context, attrs:AttributeSet): super(context,attrs)
    constructor(context:Context, attrs: AttributeSet,defStyle:Int):super(context,attrs,defStyle)

    companion object {
        private val DELAY = 500
        private val BLOCK_OFFSET = 2
        private val FRAME_OFFSET_BASE = 10
    }


    fun setModel(model:AppModel){

        this.model = model

//        end of setModel
    }

    fun setActivity(gameActivity:GameActivity){
        this.activity=gameActivity
    }

    fun setGameCommand(move:AppModel.Motions){
        if(null !=model &&(model?.currentState == AppModel.Statuses.ACTIVE.name)){
            if(AppModel.Motions.DOWN==move){
                model?.generateField(move.name)
                invalidate()
                return
            }
            setGameCommandWithDelay(move)
        }

    }

    fun setGameCommandWithDelay(move:AppModel.Motions){

        val now = System.currentTimeMillis()

        if(now-lastMove>DELAY){
            model?.generateField(move.name)
            invalidate()
            lastMove = now
        }
        updateScores()
        viewHandler.sleep(DELAY.toLong())


    }


    private fun updateScores(){

        activity?.currentScore?.text= "${model?.score}"
        activity?.highScore?.text="${activity?.appPreferences?.getHighScore()}"


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFrame(canvas)

        if(model !=null){
            for(i in 0 until FieldConstants.ROW_COUNT.Value){
                for(j in 0 until FieldConstants.COLUMN_COUNT.Value){
                    drawCell(canvas,i,j)
                }
            }
        }
    }

    private fun drawFrame(canvas: Canvas){
        paint.color=Color.LTGRAY

        canvas.drawRect(frameOffset.width.toFloat(),frameOffset.height.toFloat(),width-frameOffset.width.toFloat(),height-frameOffset.height.toFloat(),paint)
    }

    private fun drawCell(canvas: Canvas, row:Int, col:Int){

        val cellStatus = model?.getCellStatus(row,col)

        if (CellConstants.EMPTY.value !=cellStatus){
            val color = if(CellConstants.EPHEMERAL.value==cellStatus){
                model?.currentBlock?.color
            }else{
                Block.getColor(cellStatus as Byte)
            }
            drawCell(canvas, row, col as Int)
        }


    }


    private fun drawCell(canvas:Canvas, x:Int, y:Int, rgbColor:Int){

        paint.color=rgbColor

        val top:Float = (frameOffset.height + y * cellSize.height + BLOCK_OFFSET).toFloat()
        val left:Float= (frameOffset.width + x * cellSize.width + BLOCK_OFFSET).toFloat()
        val bottom:Float = (frameOffset.height + (y+1) * cellSize.height + BLOCK_OFFSET).toFloat()
        val right:Float= (frameOffset.width + (x+1) * cellSize.width + BLOCK_OFFSET).toFloat()

        val rectangle = RectF(left,top,right,bottom)

        canvas.drawRoundRect(rectangle, 4F, 4F, paint)



    }

    override fun onSizeChanged(width: Int, height: Int, previousWidth:Int, previousHeight:Int){

        super.onSizeChanged(width,height,previousWidth,previousHeight)

        val cellWidth=(width - 2 * FRAME_OFFSET_BASE) / FieldConstants.COLUMN_COUNT.Value
        val cellHeight=(height - 2 * FRAME_OFFSET_BASE) / FieldConstants.ROW_COUNT.Value
        val n = Math.min(cellWidth,cellHeight)
        this.cellSize= Dimension(n,n)
        val offsetX=(width-FieldConstants.COLUMN_COUNT.Value*n)/2
        val offsetY=(height-FieldConstants.ROW_COUNT.Value*n)/2
        this.frameOffset= Dimension(offsetX,offsetY)



    }




//    end of Class

}

private class ViewHandler(private val owner: TetrisView):Handler(){

    override fun handleMessage(message: Message){

        if(message.what == 0){
            if(owner.model !=null){
                if(owner.model!!.isGameOver()){
                    owner.model?.endGame()
                    Toast.makeText(owner.activity, "Game Over", Toast.LENGTH_LONG).show();
                }
                if(owner.model!!.isGameActive()){
                    owner.setGameCommandWithDelay(AppModel.Motions.DOWN)
                }
            }
        }
    }

    fun sleep (delay: Long){

        this.removeMessages(0)
        sendMessageDelayed(obtainMessage(0),delay)

    }



//    end of class

}


private data class Dimension(val width:Int,val height:Int)