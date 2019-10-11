package br.com.autopass.vegastps530.cardblocks

import br.com.autopass.vegastps530.utils.BinaryFunctions.binaryStringToInt
import br.com.autopass.vegastps530.utils.BinaryFunctions.intToPaddedBinaryString

fun WalletBlock(s:String):WalletBlock{
    val walletAAmount = binaryStringToInt(s.substring(0,32))
    val walletBAmount = binaryStringToInt(s.substring(32,64))
    val walletARechargeCounter = binaryStringToInt(s.substring(64,96))
    val walletBRechargeCounter = binaryStringToInt(s.substring(96,128))
    val transactionCounter = binaryStringToInt(s.substring(128,160))
    val ltt = binaryStringToInt(s.substring(160,168))
    val subAppCode = binaryStringToInt(s.substring(168,176))

    return WalletBlock(walletAAmount,walletBAmount,walletARechargeCounter,walletBRechargeCounter,
                        transactionCounter,ltt,subAppCode)
}

class WalletBlock(var walletAAmount:Int, var walletBAmount: Int, var walletARechargeCounter: Int,
                  var walletBRechargeCounter: Int, var transactionCounter: Int, val ltt: Int,
                  val subAppCode: Int): CardBlockInterface{

    override fun toBinaryString():String{
        return intToPaddedBinaryString(walletAAmount,32) +
                intToPaddedBinaryString(walletBAmount,32) +
                intToPaddedBinaryString(walletARechargeCounter,32) +
                intToPaddedBinaryString(walletBRechargeCounter,32) +
                intToPaddedBinaryString(transactionCounter,32) +
                intToPaddedBinaryString(ltt,8)+
                intToPaddedBinaryString(subAppCode,8)
    }
}