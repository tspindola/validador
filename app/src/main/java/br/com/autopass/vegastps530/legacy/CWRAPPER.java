package br.com.autopass.vegastps530.legacy;

/**
 * Created by rafae on 03/04/2018.
 */

public class CWRAPPER
{
    public static int sizeof(byte[] data) {
        return data.length;
    }

    public static void memcpy(byte[] dest, byte[] src, int len) {
        memcpy(dest, 0, src, 0, len);
    }

    public static void memcpy(byte[] dest, int destOffset, byte[] src, int len) {
        memcpy(dest, destOffset, src, 0, len);
    }

    public static void memcpy(byte[] dest, int destOffset, byte[] src, int srcOffset,
                        int len) {
        if ((len > (dest.length + destOffset))
                || (len > (src.length + srcOffset)) || len < 0)
            return;
        System.arraycopy(src, srcOffset, dest, destOffset, len);
    }

    public static void memset(byte[] dest, int destOffset, int src, int len) {
        byte[] fill = new byte[len];
        for (int i = 0; i < len; i++)
            fill[i] = (byte) src;

        if ((len > (dest.length + destOffset)))
            return;
        System.arraycopy(fill, 0, dest, destOffset, len);
    }

    public static void memset(byte[] dest, int src, int len) {
        memset(dest, 0, src, len);
    }

    public static boolean memcmp(byte[] par1, byte[] par2, int len) {
        // no C retorna 0 para igual e 1 para diferente, logo usamos logica
        // invertida
        // aqui
        for (int i = 0; i < len; i++) {
            if (par1[i] != par2[i])
                return true;
        }
        return false;
    }

}
