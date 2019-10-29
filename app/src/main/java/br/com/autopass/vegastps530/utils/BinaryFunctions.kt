package br.com.autopass.vegastps530.utils

import org.joda.time.Days
import org.joda.time.LocalDate

object BinaryFunctions{
    @JvmStatic
    fun dateToJulianDate(date: LocalDate):String{
        val dateInit = LocalDate(2000,1,1)
        val days =  Days.daysBetween(dateInit, date).days
        return intToPaddedBinaryString(days,16)
    }

    @JvmStatic
    fun dateFromJulianDate(julianDate:String): LocalDate {
        val days = Integer.parseInt(julianDate,2)
        val dateInit = LocalDate(2000,1,1)
        return dateInit.plusDays(days)
    }

    @JvmStatic
    private fun padBinaryString(s:String,size:Int):String{
        return s.padStart(size,'0')
    }

    @JvmStatic
    private fun intToBinaryString(value:Int):String{
        return value.toString(2)
    }

    @JvmStatic
    fun intToPaddedBinaryString(value:Int,size:Int):String{
        return padBinaryString(intToBinaryString(value),size)
    }

    @JvmStatic
    fun blockStringToByteArray(block:String, blocksize: Int):ByteArray{
        val formattedString = block.padEnd(128,'0')
        val blockByteArray = ByteArray(16*blocksize)
        for(i in 0 until 16*blocksize){
            val byteString = formattedString.substring(8*i,8*(i+1))
            blockByteArray[i] = Integer.parseInt(byteString,2).toByte()
        }
        return blockByteArray
    }

    @JvmStatic
    fun byteArrayToBlockString(array:ByteArray, blocksize: Int):String{
        var ret = ""
        if(array.size == 16*blocksize) {
            for (i in 0 until 16*blocksize) {
                var a = array[i].toInt()
                if(a<0) a += 256
                ret += String.format("%8s", Integer.toBinaryString(a)).replace(' ', '0')
            }
        }
        return ret
    }

    @JvmStatic
    fun binaryStringToInt(s:String):Int{
        return Integer.parseInt(s,2)
    }
}