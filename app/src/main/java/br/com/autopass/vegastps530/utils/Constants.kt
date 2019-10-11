package br.com.autopass.vegastps530.utils

object Constants{
    //Card block Constants
    const val CARD_INFO_BLOCK: Byte = 0
    const val CARD_STATUS_BLOCK: Byte = 1
    const val CARD_BITMAP_BLOCK: Byte = 2
    const val INTEGRATIONS_BLOCK: Byte = 8

    //Application 1 block Constants
    const val APP1_RECHARGE_INFO: Byte = 20
    const val APP1_WALLET_INFO: Byte = 36   //37 also
    const val APP1_INFO: Byte = 38

    //Application 2 block Constants
    const val APP2_RECHARGE_INFO: Byte = 28
    const val APP2_WALLET_INFO: Byte = 44  //45 also
    const val APP2_INFO: Byte = 46
}
