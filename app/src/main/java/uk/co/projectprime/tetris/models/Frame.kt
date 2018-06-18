package uk.co.projectprime.tetris.models

import uk.co.projectprime.tetris.helpers.array2dOfByte

class Frame (private val width: Int){

    val data: ArrayList<ByteArray> = ArrayList()

    fun addRow(byteStr: String): Frame{

        val row = ByteArray(byteStr.length)

        for(index in byteStr.indices){

            row[index]="${byteStr[index]}".toByte()


        }

        data.add(row)

        return this


//        end of addRow method
    }

    fun as2dByteArray(): Array<ByteArray>{

        val bytes = array2dOfByte(data.size,width)

        return data.toArray(bytes)

//        end of as2dByteArray method
    }







//    end of class
}