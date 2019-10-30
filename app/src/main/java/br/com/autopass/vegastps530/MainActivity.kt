package br.com.autopass.vegastps530

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import br.com.autopass.vegastps530.cardblocks.*
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.LocalDate

class MainActivity : AppCompatActivity(), OnClickListener {
    private val device = SerialDeviceManager.getInstance(this)
    private var readOption = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btReadAll.setOnClickListener(this)
        btReadBlock.setOnClickListener(this)

        val listOfOptions = arrayOf("Informações do cartão", "Status do cartão", "Bitmap do cartão", "Integrações",
            "Informações de recarga (App 1)", "Informações App 1")

        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOfOptions)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spBlocks.adapter = aa

        device.open(applicationContext)
        device.startReading()
        device.listener = {
            configureCardListener()
            device.waitCardRemove()
        }
    }

    override fun onClick(p0: View?) {
        when(p0){
            btReadAll-> readOption = -1
            btReadBlock-> readOption = spBlocks.selectedItemPosition
        }
        Toast.makeText(this,"Index = $readOption",Toast.LENGTH_SHORT).show()
    }

    private fun configureCardListener(){
        when(readOption){
            -1->readAllBlocks()
            else->readSpecificBlock(readOption)
        }
    }

    private fun readAllBlocks(){
        val cardFunctions = CardFunctions(this,device.cardSerialNumber)
        var binary = cardFunctions.readInfoBlock().toBinaryString()
        Log.d("READER_LIB", "Info block (raw) = $binary")
        binary = cardFunctions.readStatusBlock().toBinaryString()
        Log.d("READER_LIB", "Status block (raw) = $binary")
        binary = cardFunctions.readBitmapBlock().toBinaryString()
        Log.d("READER_LIB", "Bitmap block (raw) = $binary")
        binary = cardFunctions.readIntegrationBlock().toBinaryString()
        Log.d("READER_LIB", "Integration block (raw) = $binary")
        binary = cardFunctions.readRechargeData(1).toBinaryString()
        Log.d("READER_LIB", "Recharge block - App 1 (raw) = $binary")
        binary = cardFunctions.readPurseInfo(1).toBinaryString()
        Log.d("READER_LIB", "Info App 1 block (raw) = $binary")
    }

    private fun readSpecificBlock(index: Int){
        val cardFunctions = CardFunctions(this,device.cardSerialNumber)
        val binary: String
        when(index) {
            0 -> {
                binary = cardFunctions.readInfoBlock().toBinaryString()
                Log.d("READER_LIB", "Info block (raw) = $binary")
            }
            1 -> {
                binary = cardFunctions.readStatusBlock().toBinaryString()
                Log.d("READER_LIB", "Status block (raw) = $binary")
            }
            2 -> {
                binary = cardFunctions.readBitmapBlock().toBinaryString()
                Log.d("READER_LIB", "Bitmap block (raw) = $binary")
            }
            3 -> {
                binary = cardFunctions.readIntegrationBlock().toBinaryString()
                Log.d("READER_LIB", "Integration block (raw) = $binary")
            }
            4 -> {
                binary = cardFunctions.readRechargeData(1).toBinaryString()
                Log.d("READER_LIB", "Recharge block - App 1 (raw) = $binary")
            }
            5 -> {
                //val purseinfo = PurseInfoBlock(1,1,0, LocalDate.now(),0,LocalDate.now(),LocalDate.now(),20,999)
                //cardFunctions.writePurseInfo(1,purseinfo)
                binary = cardFunctions.readPurseInfo(1).toBinaryString()
                Log.d("READER_LIB", "Info App 1 block (raw) = $binary")
            }
            else -> return
        }
    }
}
