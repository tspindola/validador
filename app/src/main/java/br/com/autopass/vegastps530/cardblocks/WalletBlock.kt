package br.com.autopass.vegastps530.cardblocks

import br.com.autopass.vegastps530.utils.BinaryFunctions.binaryStringToInt
import br.com.autopass.vegastps530.utils.BinaryFunctions.intToPaddedBinaryString

fun WalletBlock(s:String):WalletBlock{
    val amount = binaryStringToInt(s.substring(0,31))

    return WalletBlock(amount)
}

class WalletBlock(val amount:Int): CardBlockInterface{

    override fun toBinaryString():String{
        return intToPaddedBinaryString(amount,31)
    }
}