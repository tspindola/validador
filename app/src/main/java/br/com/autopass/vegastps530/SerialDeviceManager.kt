package br.com.autopass.vegastps530

import android.app.Activity
import android.util.Log
import com.telpo.tps550.api.nfc.Nfc
import com.telpo.tps550.api.reader.SmartCardReader
import io.reactivex.Single

class SerialDeviceManager{
    private lateinit var nfc: Nfc
    private lateinit var reader: SmartCardReader
    private val slotSAM = 1
    private val slotNFC = 5
    private val HEX_CHARS = "0123456789ABCDEF"


    companion object Factory{
        fun create(ctx: Activity):SerialDeviceManager{
            val ret = SerialDeviceManager()
            ret.nfc = Nfc(ctx)
            ret.reader = SmartCardReader(ctx, ret.slotSAM)
            return ret
        }
    }

    fun open(){
        val disposable = Single.create<Boolean> { emitter ->
            val ret = openReader()
            emitter.onSuccess(ret)

        }
            .subscribe{t->
                if(t)
                    openNFC()
                else
                    closeNFC()
            }

        disposable.dispose()
    }

    private fun openReader():Boolean{
        Log.d("READER_LIB", "Opening reader")
        return reader.open()
    }

    private fun openNFC(){
        nfc.open()
        reader.iccPowerOn()
    }

    private fun closeNFC(){
        nfc.close()
        reader.iccPowerOff()
    }

    private fun resetDevice(slot: Int):ByteArray{
        return if (slot == slotNFC) {
            nfc.activate(50)

        } else {
            reader.atrString.hexStringToByteArray()
        }
    }

    private fun sendCommandToSAM(slot: Int, apdu: ByteArray):ByteArray{
        return if (slotSAM == slot) {
            reader.transmit(apdu)
        } else {
            nfc.transmit(apdu, apdu.size)
        }
    }

    private fun readCardSerialNumber():ByteArray?{
        var nfcCardID: ByteArray? = null
        val nfcData = nfc.activate(50)
        if (nfcData != null && nfcData.size >= 6) {
            nfcCardID = ByteArray(nfcData[5].toInt())
            System.arraycopy(nfcData, 6, nfcCardID, 0, nfcCardID.size)
        }
        return nfcCardID
    }

    fun String.hexStringToByteArray() : ByteArray {

        val result = ByteArray(length / 2)

        for (i in 0 until length step 2) {
            val firstIndex = HEX_CHARS.indexOf(this[i])
            val secondIndex = HEX_CHARS.indexOf(this[i + 1])

            val octet = firstIndex.shl(4).or(secondIndex)
            result[i.shr(1)] = octet.toByte()
        }

        return result
    }
}