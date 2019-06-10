package br.com.autopass.vegastps530

import android.app.Activity
import android.os.Handler
import android.util.Log
import com.telpo.tps550.api.TimeoutException
import com.telpo.tps550.api.nfc.Nfc
import com.telpo.tps550.api.reader.SmartCardReader
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SerialDeviceManager(ctx: Activity) {
    private val slotSAM = 0
    private val slotPSAM = 1
    private val slotNFC = 5
    private val HEX_CHARS = "0123456789ABCDEF"
    private val nfc = Nfc(ctx)
    private val reader = SmartCardReader(ctx, slotSAM)

    fun open() {
        val disposable = Single.create<Boolean> { emitter ->
            val ret = openReader()
            emitter.onSuccess(ret)

        }
            .subscribe { t ->
                if (t)
                    openNFC()
                else
                    closeNFC()
            }

        disposable.dispose()
    }

    private fun openReader(): Boolean {
        Log.d("READER_LIB", "Opening reader")
        return reader.open()
    }

    private fun openNFC() {
        Log.d("READER_LIB", "Opening NFC")
        nfc.open()
        reader.iccPowerOn()
    }

    private fun closeNFC() {
        Log.d("READER_LIB", "Closing NFC")
        nfc.close()
        reader.iccPowerOff()
    }

    private fun resetDevice(slot: Int): ByteArray {
        return if (slot == slotNFC) {
            nfc.activate(50)

        } else {
            reader.atrString.hexStringToByteArray()
        }
    }

    fun sendCommandToSAM(device: DeviceSlot, apdu: ByteArray): ByteArray? {
        return if (DeviceSlot.SAM == device) {
            reader.transmit(apdu)
        } else {
            nfc.transmit(apdu, apdu.size)
        }
    }

    private fun isCardPresent(): Boolean {
        val nfcData = nfc.activate(30)
        return (nfcData != null && nfcData.size >= 6)
    }

    fun String.hexStringToByteArray(): ByteArray {

        val result = ByteArray(length / 2)

        for (i in 0 until length step 2) {
            val firstIndex = HEX_CHARS.indexOf(this[i])
            val secondIndex = HEX_CHARS.indexOf(this[i + 1])
            val octet = firstIndex.shl(4).or(secondIndex)
            result[i.shr(1)] = octet.toByte()
        }

        return result
    }

    private fun readBalance() {

    }

    private fun readCard(b: Boolean) {
        if (b) {

        } else {
            Log.d("READER_LIB", "Waiting card")
        }
    }

    fun reader() {
        var ret = false
        val disposable = Observable.fromCallable {
            try {
                ret = isCardPresent()
            } catch (t: TimeoutException) {
            }
        }
            .repeatUntil { ret }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { readCard(ret) }
    }
}