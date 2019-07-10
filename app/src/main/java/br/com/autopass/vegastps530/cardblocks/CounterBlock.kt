package br.com.autopass.vegastps530.cardblocks

import br.com.autopass.vegastps530.utils.BinaryFunctions.binaryStringToInt
import br.com.autopass.vegastps530.utils.BinaryFunctions.intToPaddedBinaryString

fun CounterBlock(s:String):CounterBlock{
    val rechargeCount = binaryStringToInt(s.substring(0,24))

    return CounterBlock(rechargeCount)
}

class CounterBlock(val rechargeCount:Int): CardBlockInterface{

    override fun toBinaryString():String{
        return intToPaddedBinaryString(rechargeCount,24)
    }
}