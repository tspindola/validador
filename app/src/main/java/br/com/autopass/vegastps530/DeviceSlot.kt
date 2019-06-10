package br.com.autopass.vegastps530

enum class DeviceSlot(val deviceId: Byte, val slot: Int) {
    SAM(0x10, 1),
    NFC(0x20, 5),
    CARD(0x30, 0)
}