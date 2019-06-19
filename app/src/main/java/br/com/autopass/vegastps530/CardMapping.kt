package br.com.autopass.vegastps530

import android.content.Context
import br.com.autopass.vegastps530.CWRAPPER.memcpy
import kotlin.experimental.and
import kotlin.experimental.or

class CardMapping(ctx: Context){
    private var comm =  VSC_COMM(ctx)
    private var vl = VL4MIF(comm)
    private var block48gl = ByteArray(16)
    private var block56gl = ByteArray(16)
    private var block22gl = ByteArray(16)

    private val RECHARGE_BLOCK: Byte = 48
    private val CONTINGENCE_RECHARGE_BLOCK: Byte = 49
    private val COUNTER_BLOCK: Byte = 56
    private val CONTINGENCE_COUNTER_BLOCK: Byte = 57

    fun DebitCard(uid:ByteArray, cardCounter:ByteArray):Int
    {
        val samLtc = ByteArray(3)
        var resp = ReadCardLite(byteArrayOf(0x00, 0x04, 0x028), uid, uid.size.toByte(), byteArrayOf(0x03, 0x05), samLtc)

        if(resp>=0){
            val offset = block56gl[3] and 0x7F
            resp = arrayToNumber(block48gl, 4) - offset
            System.arraycopy(block56gl, 0, cardCounter, 0, 4)

            val incvalue = numberToArray(1, 4)
            val decvalue = numberToArray(25, 4)
            decvalue[3] = decvalue[3] or 0x80.toByte()
            var ret = vl.VL4MIF_ValueTransfer(RECHARGE_BLOCK, decvalue, RECHARGE_BLOCK)
            if(ret >= 0){
                ret = vl.VL4MIF_ValueTransfer(CONTINGENCE_RECHARGE_BLOCK, decvalue, CONTINGENCE_RECHARGE_BLOCK)
                if(ret >= 0){
                    ret = vl.VL4MIF_ValueTransfer(COUNTER_BLOCK, incvalue, COUNTER_BLOCK)
                    if(ret >= 0){
                        ret = vl.VL4MIF_ValueTransfer(CONTINGENCE_COUNTER_BLOCK, incvalue, CONTINGENCE_COUNTER_BLOCK)
                        if(ret >= 0){
                            val cert = ByteArray(16)
                            ret = FlushCard(cert)
                            if(ret >= 0){
                                val offsetResp = block56gl[3] and 0x7F
                                resp = arrayToNumber(block48gl, 4) - offsetResp
                                System.arraycopy(block56gl, 0, cardCounter, 0, 4)
                                resp -= 25 // ?????????????????????????????????????????
                            }
                        }
                    }
                }
            }
        }

        return resp
    }

    //TODO: Urgente: Remover vários rets daqui
    private fun ReadCardLite(atqsak: ByteArray, uid: ByteArray, uidsz: Byte, fid: ByteArray, samLtc: ByteArray): Int {
        block48gl = ByteArray(16)
        block56gl = ByteArray(16)
        val block = ByteArray(16)

        var ret:Int = 0

        val key = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00)
        val keyType: Byte = 0x60
        val keyIndex: Byte = 0x01

        var rc: Int = vl.VL4MIF_Open(atqsak, uidsz, uid, fid, samLtc)

        if (rc != 0) return -3
        rc = vl.VL4MIF_LoadKey(key, keyType, keyIndex)
        if (rc != 0) return -4

        vl.VL4MIF_Authenticate(0.toByte(), keyType, keyIndex)
        rc = vl.VL4MIF_Read(48.toByte(), 1.toByte(), block)
        if (rc != 0) return -22
        memcpy(block48gl, block, 16)

        rc = vl.VL4MIF_Read(56.toByte(), 1.toByte(), block)
        if (rc != 0) return -26
        memcpy(block56gl, block, 16)

        return ret
    }

    private fun FlushCard(certificate: ByteArray): Int {
        var ret: Int
        val pApdu = byteArrayOf(0x80.toByte(),0x12.toByte(),0x0F.toByte(),0xC1.toByte(),0x10.toByte())
        val pAnswer = ByteArray(255)
        val SW = ByteArray(2)

        ret = comm.VL_ScardTransmit(CONST.CLV_DEVICE_ID, pApdu, 5, pAnswer, SW)

        if (ret > 0) {
            if (SW[0] == 0x90.toByte() && SW[1] == 0x00.toByte()) {
                System.arraycopy(pAnswer, 0, certificate, 0, ret)
                ret = 0
            } else
                ret = -0x80000000
        }
        return ret
    }

    private fun arrayToNumber(array: ByteArray, arraySize:Int): Int {

        var ret = 0
        for (i in 0 until arraySize) {
            ret += array[i].toInt() shl i * 8
        }
        return ret
    }

    private fun numberToArray(value: Int, arraySize: Int): ByteArray {
        val retarray = ByteArray(arraySize)
        for (i in 0 until arraySize) {
            retarray[i] = (value and (0xFF shl i * 8) shr i * 8).toByte()
        }
        return retarray
    }
}