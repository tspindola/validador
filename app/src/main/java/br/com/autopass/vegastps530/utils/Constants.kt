package br.com.autopass.vegastps530.utils

object Constants{
    //Card block Constants
    const val CARD_INFO_BLOCK: Byte = 1
    const val CARD_STATUS_BLOCK: Byte = 2
    const val CARD_BITMAP_BLOCK: Byte = 4
    const val INTEGRATIONS_BLOCK: Byte = 5

    //Application 1 block Constants
    const val APP1_RECHARGE_INFO: Byte = 20
    const val APP1_RECHARGE_COUNTER: Byte = 21
    const val APP1_WALLET_A_BALANCE: Byte = 22
    const val APP1_WALLET_B_BALANCE: Byte = 24
    const val APP1_TRANSACTION_COUNTER: Byte = 25
    const val APP1_INFO: Byte = 26

    //Application 2 block Constants
    const val APP2_RECHARGE_INFO: Byte = 28
    const val APP2_RECHARGE_COUNTER: Byte = 29
    const val APP2_WALLET_A_BALANCE: Byte = 30
    const val APP2_WALLET_B_BALANCE: Byte = 32
    const val APP2_TRANSACTION_COUNTER: Byte = 33
    const val APP2_INFO: Byte = 34
}
