package br.com.autopass.vegastps530.cardblocks

import br.com.autopass.vegastps530.utils.BinaryFunctions.binaryStringToInt
import br.com.autopass.vegastps530.utils.BinaryFunctions.dateFromJulianDate
import br.com.autopass.vegastps530.utils.BinaryFunctions.dateToJulianDate
import br.com.autopass.vegastps530.utils.BinaryFunctions.intToPaddedBinaryString
import org.joda.time.LocalDate

fun StatusBlock(s:String):StatusBlock{
    val cardStatus = binaryStringToInt(s.substring(0,4))
    val expireDate = dateFromJulianDate(s.substring(4,20))

    return StatusBlock(cardStatus, expireDate)
}

class StatusBlock(val cardStatus:Int, val expireDate: LocalDate): CardBlockInterface{

    override fun toBinaryString():String{
        return intToPaddedBinaryString(cardStatus,4) +
                dateToJulianDate(expireDate)
    }
}