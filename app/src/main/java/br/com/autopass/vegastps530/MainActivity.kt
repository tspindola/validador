package br.com.autopass.vegastps530

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.com.autopass.vegastps530.cardblocks.InfoBlock

class MainActivity : AppCompatActivity() {
    private val device = SerialDeviceManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        device.open()
        device.startReading()

        device.listener = {
            val issuer = readCardBalance()
            Log.d("READER_LIB", "Issuer = $issuer")
            device.restartReading()
        }
    }

    private fun readCardBalance():Int{
        val cardFunctions = CardFunctions(this)
        val infoBlock = InfoBlock("1111111100000000111111110000000011111111".padEnd(128,'0'))
        var ret:Int
        ret = cardFunctions.writeInfoBlock(infoBlock)
        Log.d("READER_LIB", "Cartão gravado: retorno = $ret")
        val block = cardFunctions.readInfoBlock()
        ret = block.issuer
        return ret
    }
}
