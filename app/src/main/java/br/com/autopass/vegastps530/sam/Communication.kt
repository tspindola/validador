package br.com.autopass.vegastps530.sam

import android.app.Activity
import br.com.autopass.vegastps530.DeviceSlot
import br.com.autopass.vegastps530.MainActivity
import br.com.autopass.vegastps530.SerialDeviceManager
import br.com.autopass.vegastps530.exception.SendCommandException

class Communication(ctx:Activity) {
    private val minimumTransmitionReturnSize = 2
    private val context = ctx

    fun transmit(device: DeviceSlot, apdu: ByteArray, szApdu: Int): Int {
        val sapdu = apdu.copyOfRange(0, szApdu)
        val serialDeviceManager = SerialDeviceManager.getInstance(context)
        val commandReturn = serialDeviceManager.sendCommandToSAM(device, sapdu)

        if (commandReturn == null || commandReturn.size < minimumTransmitionReturnSize) {
            throw SendCommandException()
        }

        return commandReturn.size - minimumTransmitionReturnSize
    }
}