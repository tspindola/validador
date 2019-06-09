package br.com.autopass.vegastps530

class Card {
    companion object {
        const val OK = 0
    }

    private val comm: VSC_COMM = VSC_COMM()
    private val vl: VL4MIF

    constructor() {
        vl = VL4MIF(comm)
    }

    fun debit() {

    }

    private fun readCardLite(atqsak: ByteArray, uid: ByteArray, uidsz: Byte, fid: ByteArray, samLtc: ByteArray): Int {
        val block48gl = ByteArray(16)
        val block56gl = ByteArray(16)

        val block = ByteArray(16)
        val key = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00)
        val keyType: Byte = 0x60
        val keyIndex: Byte = 0x01

        var rc: Int = vl.VL4MIF_Open(atqsak, uidsz, uid, fid, samLtc)
        if (rc != OK) return -3
        rc = vl.VL4MIF_LoadKey(key, keyType, keyIndex)
        if (rc != OK) return -4

        vl.VL4MIF_Authenticate(0, keyType, keyIndex)
        rc = vl.VL4MIF_Read(48, 1, block)
        if (rc != OK) return -22
        MemoryOperations.memoryCopy(block48gl, block, 16)

        rc = vl.VL4MIF_Read(56, 1, block)
        if (rc != OK) return -26
        MemoryOperations.memoryCopy(block56gl, block, 16)
        return 0
    }
}