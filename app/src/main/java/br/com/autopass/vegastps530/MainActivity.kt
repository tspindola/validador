package br.com.autopass.vegastps530

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.com.autopass.vegastps530.cardblocks.PurseInfoBlock

class MainActivity : AppCompatActivity() {
    private val device = SerialDeviceManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        device.open(applicationContext)

        device.startReading()

        device.listener = {
            val ret = device.readCardSerialNumber()
            val issuer = readCardBalance(ret)
            Log.d("READER_LIB", "Issuer = $issuer")
            device.waitCardRemove()
        }
    }

    private fun readCardBalance(serial: ByteArray):Int{
        val cardFunctions = CardFunctions(this,serial)
        val purseblock = PurseInfoBlock("1111111100000000111111110000000011111111".padEnd(128,'0'))
        var ret:Int
        ret = cardFunctions.writePurseInfo(1,purseblock)
        Log.d("READER_LIB", "Cartão gravado: retorno = $ret")
        val block = cardFunctions.readPurseInfo(1)
        ret = block.creditType
        //val amountBlock = cardFunctions.readWalletBalance(1)
        //Log.d("READER_LIB", "Cartão lido: Wallet Block: "+amountBlock.toBinaryString())
        //amountBlock.walletAAmount = amountBlock.walletAAmount - 430
        //cardFunctions.writeWalletBalance(1,amountBlock)
        return ret
    }
}
