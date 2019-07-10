package br.com.autopass.vegastps530.cardblocks

import br.com.autopass.vegastps530.utils.BinaryFunctions.binaryStringToInt
import br.com.autopass.vegastps530.utils.BinaryFunctions.dateFromJulianDate
import br.com.autopass.vegastps530.utils.BinaryFunctions.dateToJulianDate
import br.com.autopass.vegastps530.utils.BinaryFunctions.intToPaddedBinaryString
import org.joda.time.LocalDate

//TODO: Mover pra um arquivo factory?
fun InfoBlock(s:String):InfoBlock{
    val issuer = binaryStringToInt(s.substring(0,8))
    val emissionDate = dateFromJulianDate(s.substring(8,24))
    val cardType = binaryStringToInt(s.substring(24,32))
    val cardId = binaryStringToInt(s.substring(32,62))
    val mapVersion = binaryStringToInt(s.substring(62,68))

    return InfoBlock(emissionDate,cardType, cardId, mapVersion, issuer)
}

data class InfoBlock(val emissionDate: LocalDate, val cardType: Int, val cardId: Int, val mapVersion: Int,
                     val issuer: Int):CardBlockInterface{

    override fun toBinaryString(): String {
        return intToPaddedBinaryString(issuer,8) +
                dateToJulianDate(emissionDate)+
                intToPaddedBinaryString(cardType,8) +
                intToPaddedBinaryString(cardId,30) +
                intToPaddedBinaryString(mapVersion,6)
    }
}