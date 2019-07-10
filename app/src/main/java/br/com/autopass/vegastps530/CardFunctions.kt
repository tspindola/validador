package br.com.autopass.vegastps530

import android.content.Context
import br.com.autopass.vegastps530.cardblocks.*
import br.com.autopass.vegastps530.legacy.CONST
import br.com.autopass.vegastps530.legacy.VL4MIF
import br.com.autopass.vegastps530.legacy.VSC_COMM
import br.com.autopass.vegastps530.utils.BinaryFunctions.blockStringToByteArray
import br.com.autopass.vegastps530.utils.BinaryFunctions.byteArrayToBlockString
import br.com.autopass.vegastps530.utils.Constants

class CardFunctions(ctx: Context){
    private var comm = VSC_COMM(ctx)
    private var vl = VL4MIF(comm)

    init{
        val key = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00)
        val keyType: Byte = 0x60
        val keyIndex: Byte = 0x01
        val atqsak = byteArrayOf(0x00, 0x04, 0x028)
        val fid = byteArrayOf(0x03, 0x05)
        val samLtc = ByteArray(3)
        val uid = ByteArray(6)

        vl.VL4MIF_Open(atqsak, uid.size.toByte(), uid, fid, samLtc)
        vl.VL4MIF_LoadKey(key, keyType, keyIndex)
        vl.VL4MIF_Authenticate(0.toByte(), keyType, keyIndex)
    }

    fun readInfoBlock():InfoBlock{
        return InfoBlock(readBlockFromCard(Constants.CARD_INFO_BLOCK))
    }

    fun writeInfoBlock(infoBlock: InfoBlock):Int{
        return writeBlockToCard(infoBlock.toBinaryString(),Constants.CARD_INFO_BLOCK)
    }

    fun readStatusBlock():StatusBlock{
        return StatusBlock(readBlockFromCard(Constants.CARD_STATUS_BLOCK))
    }

    fun writeStatusBlock(statusBlock: StatusBlock):Int{
        return writeBlockToCard(statusBlock.toBinaryString(),Constants.CARD_STATUS_BLOCK)
    }

    fun readBitmapBlock():BitmapBlock{
        return BitmapBlock(readBlockFromCard(Constants.CARD_BITMAP_BLOCK))
    }

    fun writeBitmapBlock(bitmapBlock: StatusBlock):Int{
        return writeBlockToCard(bitmapBlock.toBinaryString(),Constants.CARD_BITMAP_BLOCK)
    }

    fun readIntegrationBlock():IntegrationBlock{
        return IntegrationBlock(readBlockFromCard(Constants.INTEGRATIONS_BLOCK))
    }

    fun writeIntegrationBlock(integrationBlock:IntegrationBlock):Int{
        return writeBlockToCard(integrationBlock.toBinaryString(),Constants.INTEGRATIONS_BLOCK)
    }

    fun readRechargeData(app: Int):RechargeBlock{
        val block = when(app){
            2->Constants.APP2_RECHARGE_INFO
            else->Constants.APP1_RECHARGE_INFO
        }
        return RechargeBlock(readBlockFromCard(block))
    }

    fun writeRechargeData(app:Int, rechargeBlock: RechargeBlock):Int{
        val block = when(app){
            2->Constants.APP2_RECHARGE_INFO
            else->Constants.APP1_RECHARGE_INFO
        }
        return writeBlockToCard(rechargeBlock.toBinaryString(),block)
    }

    fun readOperationCounter(app: Int, isRecharge: Boolean):CounterBlock{
        val block = when(app) {
            2 -> if(isRecharge) Constants.APP2_RECHARGE_COUNTER else Constants.APP2_TRANSACTION_COUNTER
            else -> if(isRecharge) Constants.APP1_RECHARGE_COUNTER else Constants.APP1_TRANSACTION_COUNTER
        }
        return CounterBlock(readBlockFromCard(block))
    }

    //TODO: Recharge Count tem que ser get/set com increment only
    fun writeOperationCounter(app:Int, isRecharge: Boolean, counterBlock: CounterBlock):Int{
        val block = when(app) {
            2 -> if(isRecharge) Constants.APP2_RECHARGE_COUNTER else Constants.APP2_TRANSACTION_COUNTER
            else -> if(isRecharge) Constants.APP1_RECHARGE_COUNTER else Constants.APP1_TRANSACTION_COUNTER
        }
        return writeBlockToCard(counterBlock.toBinaryString(),block)
    }

    fun readWalletBalance(app:Int,isWalletA: Boolean):WalletBlock{
        val block = when(app) {
            2 -> if (isWalletA) Constants.APP2_WALLET_A_BALANCE else Constants.APP2_WALLET_B_BALANCE
            else -> if (isWalletA) Constants.APP1_WALLET_A_BALANCE else Constants.APP1_WALLET_B_BALANCE
        }
        return WalletBlock(readBlockFromCard(block))
    }

    //TODO: Wallet Balance tem que usar Value block
    fun writeWalletBalance(app:Int, isWalletA:Boolean, walletBlock: WalletBlock):Int{
        val block = when(app) {
            2 -> if (isWalletA) Constants.APP2_WALLET_A_BALANCE else Constants.APP2_WALLET_B_BALANCE
            else -> if (isWalletA) Constants.APP1_WALLET_A_BALANCE else Constants.APP1_WALLET_B_BALANCE
        }
        return writeBlockToCard(walletBlock.toBinaryString(),block)
    }

    fun readPurseInfo(app:Int):PurseInfoBlock{
        val block = when(app){
            2->Constants.APP2_INFO
            else->Constants.APP1_INFO
        }
        return PurseInfoBlock(readBlockFromCard(block))
    }

    fun writePurseInfo(app: Int,purseInfoBlock: PurseInfoBlock):Int{
        val block = when(app){
            2->Constants.APP2_INFO
            else->Constants.APP1_INFO
        }
        return writeBlockToCard(purseInfoBlock.toBinaryString(),block)
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

    private fun writeBlockToCard(value:String,block:Byte):Int{
        val blockBytes = blockStringToByteArray(value)
        return vl.VL4MIF_Write(block,1,blockBytes)
    }

    private fun readBlockFromCard(block:Byte):String{
        var ret = ""
        var blockBytes = ByteArray(16)
        val result = vl.VL4MIF_Read(block,1,blockBytes)
        if(result>=0){
            ret = byteArrayToBlockString(blockBytes)
        }
        return ret
    }
}