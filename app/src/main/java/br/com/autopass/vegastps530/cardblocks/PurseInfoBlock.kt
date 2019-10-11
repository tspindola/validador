package br.com.autopass.vegastps530.cardblocks

import br.com.autopass.vegastps530.utils.BinaryFunctions.binaryStringToInt
import br.com.autopass.vegastps530.utils.BinaryFunctions.dateFromJulianDate
import br.com.autopass.vegastps530.utils.BinaryFunctions.dateToJulianDate
import br.com.autopass.vegastps530.utils.BinaryFunctions.intToPaddedBinaryString
import org.joda.time.LocalDate

fun PurseInfoBlock(s:String):PurseInfoBlock{
    val creditType = binaryStringToInt(s.substring(0,4))
    val appType = binaryStringToInt(s.substring(4,12))
    val appVersion = binaryStringToInt(s.substring(12,22))
    val creationDate = dateFromJulianDate(s.substring(22,38))
    val appStatus = binaryStringToInt(s.substring(38,40))
    val expireDate = dateFromJulianDate(s.substring(40,56))
    val lastUsage = dateFromJulianDate(s.substring(56,72))
    val dailyUsage = binaryStringToInt(s.substring(72,82))
    val monthlyUsage = binaryStringToInt(s.substring(82,98))

    return PurseInfoBlock(creditType, appType, appVersion, creationDate, appStatus,
        expireDate, lastUsage, dailyUsage, monthlyUsage)
}

class PurseInfoBlock(val creditType:Int, val appType:Int, val appVersion:Int,
                     val creationDate: LocalDate, val appStatus:Int, val expireDate:LocalDate,
                     val lastUsage:LocalDate, val dailyUsage:Int, val monthlyUsage: Int): CardBlockInterface{

    override fun toBinaryString():String{
        return intToPaddedBinaryString(creditType,4)+
                intToPaddedBinaryString(appType,8)+
                intToPaddedBinaryString(appVersion,10)+
                dateToJulianDate(creationDate)+
                intToPaddedBinaryString(appStatus,2)+
                dateToJulianDate(expireDate)+
                dateToJulianDate(lastUsage)+
                intToPaddedBinaryString(dailyUsage,10)+
                intToPaddedBinaryString(monthlyUsage,16)
    }
}