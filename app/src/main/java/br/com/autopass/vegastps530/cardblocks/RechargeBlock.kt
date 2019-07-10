package br.com.autopass.vegastps530.cardblocks

import br.com.autopass.vegastps530.utils.BinaryFunctions.binaryStringToInt
import br.com.autopass.vegastps530.utils.BinaryFunctions.dateFromJulianDate
import br.com.autopass.vegastps530.utils.BinaryFunctions.dateToJulianDate
import br.com.autopass.vegastps530.utils.BinaryFunctions.intToPaddedBinaryString
import org.joda.time.LocalDate

fun RechargeBlock(s:String):RechargeBlock{
    val rechargeDate = dateFromJulianDate(s.substring(0,16))
    val rechargeValue = binaryStringToInt(s.substring(16,36))
    val creditSeries = binaryStringToInt(s.substring(36,56))

    return RechargeBlock(rechargeDate, rechargeValue, creditSeries)
}

class RechargeBlock(val rechargeDate:LocalDate, val rechargeValue:Int,
                    val creditSeries:Int): CardBlockInterface{

    override fun toBinaryString():String{
        return dateToJulianDate(rechargeDate)+
                intToPaddedBinaryString(rechargeValue,20)+
                intToPaddedBinaryString(creditSeries,20)
    }
}