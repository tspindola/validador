package br.com.autopass.vegastps530

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import br.com.autopass.vegastps530.cardblocks.*
import br.com.autopass.vegastps530.legacy.CONST
import br.com.autopass.vegastps530.legacy.VL4MIF
import br.com.autopass.vegastps530.legacy.VSC_COMM
import br.com.autopass.vegastps530.utils.BinaryFunctions.blockStringToByteArray
import br.com.autopass.vegastps530.utils.BinaryFunctions.byteArrayToBlockString
import br.com.autopass.vegastps530.utils.Constants

class CardFunctions(ctx: Context, serial:ByteArray){
    private var comm = VSC_COMM(ctx)
    private var vl = VL4MIF(comm)

    init{
        val key = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00)
        val keyType: Byte = 0x60
        val keyIndex: Byte = 0x01
        val atqsak = byteArrayOf(0x00, 0x04, 0x028)
        val fid = byteArrayOf(0x03, 0x05)
        val samLtc = ByteArray(3)
        vl.VL4MIF_Open(atqsak, serial.size.toByte(), serial, fid, samLtc)
        vl.VL4MIF_LoadKey(key, keyType, keyIndex)
        vl.VL4MIF_Authenticate(0.toByte(), keyType, keyIndex)
    }

    fun readInfoBlock():InfoBlock{
        return InfoBlock(readBlockFromCard(Constants.CARD_INFO_BLOCK,1))
    }

    fun writeInfoBlock(infoBlock: InfoBlock):Int{
        return writeBlockToCard(infoBlock.toBinaryString(),Constants.CARD_INFO_BLOCK,1)
    }

    fun readStatusBlock():StatusBlock{
        return StatusBlock(readBlockFromCard(Constants.CARD_STATUS_BLOCK,1))
    }

    fun writeStatusBlock(statusBlock: StatusBlock):Int{
        return writeBlockToCard(statusBlock.toBinaryString(),Constants.CARD_STATUS_BLOCK,1)
    }

    fun readBitmapBlock():BitmapBlock{
        return BitmapBlock(readBlockFromCard(Constants.CARD_BITMAP_BLOCK,1))
    }

    fun writeBitmapBlock(bitmapBlock: BitmapBlock):Int{
        return writeBlockToCard(bitmapBlock.toBinaryString(),Constants.CARD_BITMAP_BLOCK,1)
    }

    fun readIntegrationBlock():IntegrationBlock{
        return IntegrationBlock(readBlockFromCard(Constants.INTEGRATIONS_BLOCK,1))
    }

    fun writeIntegrationBlock(integrationBlock:IntegrationBlock):Int{
        return writeBlockToCard(integrationBlock.toBinaryString(),Constants.INTEGRATIONS_BLOCK,1)
    }

    fun readRechargeData(app: Int):RechargeBlock{
        val block = when(app){
            2->Constants.APP2_RECHARGE_INFO
            else->Constants.APP1_RECHARGE_INFO
        }
        return RechargeBlock(readBlockFromCard(block,1))
    }

    fun writeRechargeData(app:Int, rechargeBlock: RechargeBlock):Int{
        val block = when(app){
            2->Constants.APP2_RECHARGE_INFO
            else->Constants.APP1_RECHARGE_INFO
        }
        return writeBlockToCard(rechargeBlock.toBinaryString(),block,1)
    }


    fun readWalletBalance(app:Int):WalletBlock{
        val block = when(app) {
            2 -> Constants.APP2_WALLET_INFO
            else -> Constants.APP1_WALLET_INFO
        }
        return WalletBlock(readBlockFromCard(block,2))
    }

    //TODO: Wallet Balance tem que usar Value block
    fun writeWalletBalance(app:Int, walletBlock: WalletBlock):Int{
        val block = when(app) {
            2 ->  Constants.APP2_WALLET_INFO
            else -> Constants.APP1_WALLET_INFO
        }
        return writeBlockToCard(walletBlock.toBinaryString(),block,2)
    }

    fun readPurseInfo(app:Int):PurseInfoBlock{
        val block = when(app){
            2->Constants.APP2_INFO
            else->Constants.APP1_INFO
        }
        return PurseInfoBlock(readBlockFromCard(block,1))
    }

    fun writePurseInfo(app: Int,purseInfoBlock: PurseInfoBlock):Int{
        val block = when(app){
            2->Constants.APP2_INFO
            else->Constants.APP1_INFO
        }
        return writeBlockToCard(purseInfoBlock.toBinaryString(),block,1)
    }

    private fun commitChangesToCard(certificate: ByteArray): Int {
        var ret: Int
        val commitApdu = byteArrayOf(0x80.toByte(),0x12.toByte(),0x0F.toByte(),0xC1.toByte(),0x10.toByte())
        val pAnswer = ByteArray(255)
        val SW = ByteArray(2)

        ret = comm.VL_ScardTransmit(CONST.CLV_DEVICE_ID, commitApdu, 5, pAnswer, SW)

        if (ret > 0) {
            if (SW[0] == 0x90.toByte() && SW[1] == 0x00.toByte()) {
                System.arraycopy(pAnswer, 0, certificate, 0, ret)
                ret = 0
            } else
                ret = -0x80000000
        }
        return ret
    }

    private fun writeBlockToCard(value:String,block:Byte, blocksize: Int):Int{
        val blockBytes = blockStringToByteArray(value, blocksize)
        val ret = vl.VL4MIF_Write(block,blocksize.toByte(),blockBytes)
        Log.d("READER_LIB", "Write block $block with value = ${byteArrayToBlockString(blockBytes,blocksize)} Result: $ret")
        return ret
    }

    private fun readBlockFromCard(block:Byte, blocksize: Int):String{
        var ret = ""
        var blockBytes = ByteArray(16*blocksize)
        val result = vl.VL4MIF_Read(block,blocksize.toByte(),blockBytes)
        Log.d("READER_LIB", "Read block $block. Result: $result")
        if(result>=0){
            ret = byteArrayToBlockString(blockBytes,blocksize)
        }
        else{
            ret = ret.padEnd(128,'0')
        }
        return ret
    }
}