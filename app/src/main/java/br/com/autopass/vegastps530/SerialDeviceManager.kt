package br.com.autopass.vegastps530

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import br.com.autopass.vegastps530.legacy.VSC_COMM
import br.com.autopass.vegastps530.utils.APDUs
import br.com.autopass.vegastps530.utils.DeviceSlot
import br.com.autopass.vegastps530.utils.SingletonHolder
import br.com.autopass.vegastps530.utils.BinaryFunctions
import br.inf.planeta.Reader
import com.telpo.tps550.api.TimeoutException
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

private val ACTION_USB_PERMISSION = "br.com.autopass.USB_PERMISSION"

class SerialDeviceManager private constructor(context: Context) {

    companion object : SingletonHolder<SerialDeviceManager, Context>(::SerialDeviceManager)

    private var reader: Reader? = null
    private lateinit var disposable: Disposable
    var listener: (()->Unit)? = null
    private var device: UsbDevice? = null
    private var isCardPresent = false
    private var comm: VSC_COMM? = null

    fun getCardReader():Reader?{
        return reader
    }

    fun open(context: Context) {
        if(device == null) {
            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val deviceList = manager.deviceList
            val iterator = deviceList.values.iterator()

            while (iterator.hasNext()) {
                val currentDevice = iterator.next()
                val intent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION), 0)
                manager.requestPermission(currentDevice, intent)

                if (currentDevice.productId == 87 && currentDevice.vendorId == 2816) {
                    device = currentDevice
                    reader = Reader(context)
                    comm = VSC_COMM(context)
                }
            }
        }
    }

    private fun waitCard() {
        val ret = comm!!.newSCardTransmit(0, APDUs.ATR)
        Log.d("READER_LIB", "Wait card ret = ${ret.toHexString()}, size = ${ret.size}")
        if (ret.size >= 5) {
            isCardPresent = true
            val atq = ByteArray(3)
            System.arraycopy(ret, 0, atq, 0, atq.size)
            Log.d("READER_LIB", "Cartão detectado")
        }
    }

    fun readCardSerialNumber(): ByteArray {
        var answer = comm!!.newSCardTransmit(0, APDUs.SELECT_FILE_2FF7)
        Log.d("READER_LIB", "Select file 2FF7 answer = ${answer.toHexString()}")
        if (isAnswerOk(answer)){
            answer = comm!!.newSCardTransmit(0, APDUs.READ_FILE)
            Log.d("READER_LIB", "Read file 2FF7 answer = ${answer.toHexString()}")
            if(isAnswerOk(answer)){
                val resp = ByteArray(4)
                System.arraycopy(answer, 17, resp, 0, 4)
                return resp
            }
        }
        return ByteArray(0)
    }

    fun verifySW(array: ByteArray):String{
        var ret = "0000"
        if(array.size >= 2){
            ret = array.copyOfRange(array.size-2,array.size).toHexString()
        }
        return ret
    }

    private fun isAnswerOk(array: ByteArray):Boolean{
        return verifySW(array) == "9000"
    }

    fun ByteArray.toHexString() : String {
        return this.joinToString("") {
            java.lang.String.format("%02x", it)
        }
    }

    fun startReading() {
        disposable = Observable.timer(100, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .repeat()
            .subscribe{
                if(!isCardPresent) {
                    Log.d("READER_LIB", "Aguardando cartão...")
                    waitCard()
                }
                else{
                    listener?.invoke()
                }
            }
    }

    fun waitCardRemove(){
        while (true) {
            val answer = comm!!.newSCardTransmit(0, APDUs.WAIT_REMOVE)
            Log.d("READER_LIB", "Esperando cartão ser removido. Ret = ${answer.toHexString()} size = ${answer.size}")
            if (answer.size <= 2) {
                isCardPresent = false
                startReading()
                return
            }
            try {
                Thread.sleep(20)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }
}