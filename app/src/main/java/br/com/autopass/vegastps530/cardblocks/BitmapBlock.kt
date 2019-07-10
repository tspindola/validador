package br.com.autopass.vegastps530.cardblocks

class BitmapBlock(val bitmap:String): CardBlockInterface{

    override fun toBinaryString():String{
        return bitmap
    }
}