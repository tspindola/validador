package br.com.autopass.vegastps530

class MemoryOperations {
    companion object {
        fun memoryCopy(destiny: ByteArray, source: ByteArray, length: Int) {
            memoryCopy(destiny, 0, source, 0, length)
        }

        fun memoryCopy(destiny: ByteArray, destinyOffset: Int, source: ByteArray, sourceOffset: Int, length: Int) {
            if (length <= (destiny.size + destinyOffset) && (length <= source.size + sourceOffset) && length >= 0) {
                System.arraycopy(source, sourceOffset, destiny, destinyOffset, length)
            }
        }
    }
}