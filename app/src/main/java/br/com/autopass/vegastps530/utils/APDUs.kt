package br.com.autopass.vegastps530.utils

object APDUs{
    val ATR = byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0xE1.toByte(), 0, 0)
    val SELECT_FILE_2FF7 = byteArrayOf(0, 0xA4.toByte(),0,0, 0x02.toByte(), 0x2F.toByte(), 0xF7.toByte())
    val READ_FILE = byteArrayOf(0,0xB0.toByte(),0,0,0)
    val WAIT_REMOVE = byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0xE1.toByte(), 0, 0)

    //VLib Open
    val VLIB_FIRST_0305 = byteArrayOf(0,0xA4.toByte(),0,0x0C.toByte(),0x02.toByte(),0x03.toByte(),0x05.toByte())
    val READ_FILE_38 = byteArrayOf(0,0xB0.toByte(),0,0,0x38.toByte())
    val VLIB_THIRD_ADPU = byteArrayOf(0x80.toByte(),0x12.toByte(),0,0,0x03,0x10,0,0x04.toByte(),0x28.toByte())
}