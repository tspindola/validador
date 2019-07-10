package br.com.autopass.vegastps530.cardblocks

import br.com.autopass.vegastps530.utils.BinaryFunctions.binaryStringToInt
import br.com.autopass.vegastps530.utils.BinaryFunctions.dateFromJulianDate
import br.com.autopass.vegastps530.utils.BinaryFunctions.dateToJulianDate
import br.com.autopass.vegastps530.utils.BinaryFunctions.intToPaddedBinaryString
import org.joda.time.LocalDate

fun IntegrationBlock(s:String):IntegrationBlock{
    val originApp = binaryStringToInt(s.substring(0,4))
    val timeLimit = binaryStringToInt(s.substring(4,16))
    val modalCode = binaryStringToInt(s.substring(16,20))
    val direction = binaryStringToInt(s.substring(20,21))
    val lineCode = binaryStringToInt(s.substring(21,33))
    val groupLineCode = binaryStringToInt(s.substring(33,41))
    val feeCode = binaryStringToInt(s.substring(41,51))
    val transactionCount = binaryStringToInt(s.substring(51,55))
    val totalAmount = binaryStringToInt(s.substring(55,69))
    val startDate = dateFromJulianDate(s.substring(69,85))
    val startTime = binaryStringToInt(s.substring(85,96))

    return IntegrationBlock(originApp, timeLimit, modalCode, direction, lineCode, groupLineCode, feeCode,
        transactionCount, totalAmount, startDate, startTime)
}

class IntegrationBlock(val originApp:Int, val timeLimit:Int, val modalCode:Int,
                       val direction:Int, val lineCode:Int, val groupLineCode:Int,
                       val feeCode:Int, val transactionCount:Int, val totalAmount:Int,
                       val startDate: LocalDate, val startTime:Int): CardBlockInterface{

    override fun toBinaryString():String{
        return intToPaddedBinaryString(originApp,4)+
                intToPaddedBinaryString(timeLimit,12)+
                intToPaddedBinaryString(modalCode,4)+
                intToPaddedBinaryString(direction,1)+
                intToPaddedBinaryString(lineCode,12)+
                intToPaddedBinaryString(groupLineCode,8)+
                intToPaddedBinaryString(feeCode,10)+
                intToPaddedBinaryString(transactionCount,4)+
                intToPaddedBinaryString(totalAmount,14)+
                dateToJulianDate(startDate)+
                intToPaddedBinaryString(startTime,11)
    }
}