package br.com.autopass.vegastps530.legacy;

import android.content.Context;
import android.util.Log;
import br.com.autopass.vegastps530.SerialDeviceManager;
import br.com.autopass.vegastps530.utils.DeviceSlot;
import br.inf.planeta.Reader;

/**
 * Created by rafae on 03/04/2018.
 */

public class VSC_ADPCOMM
{
    public final short SAM_DEVICE_ID	=	0x10;
    public final short CLD_DEVICE_ID	=	0x20;
    public final short CARD_DEVICE_ID =	0x30;

    Context ctx;

    public VSC_ADPCOMM(Context ctx){
        this.ctx = ctx;
    }

    public int VADPT_ScardTransmit(short deviceID, byte[] apdu, int szApdu, byte[] answer, byte[] sw)
    {
        int sz = 0;
        DeviceSlot slot = DeviceSlot.OTHER;

        switch (deviceID)
        {
            case SAM_DEVICE_ID:
                slot = DeviceSlot.SAM;
                break;
            case CLD_DEVICE_ID:
                slot = DeviceSlot.NFC;
                break;
            case CARD_DEVICE_ID:
                slot = DeviceSlot.CARD;
                break;
        }
        byte[] sapdu = new byte[szApdu];
        System.arraycopy(apdu, 0, sapdu, 0, szApdu);
        SerialDeviceManager v = SerialDeviceManager.Companion.getInstance(ctx);
        //v.open(ctx);
        Reader reader = v.getCardReader();
        if(reader == null) return -1;
        long _t1 = System.currentTimeMillis();
        Log.d("APDU_STRING", "Apdu: "+ bytesToHex(sapdu));
        byte[] ret = reader.SCardTransmit(slot.getSlot(), sapdu);
        Log.d("APDU_STRING", "Apdu: "+ bytesToHex(ret));
        long _t2 = System.currentTimeMillis();
        Log.w("APDU_TIME", (_t2 - _t1) + "ms");
        if(ret == null) return -1;
        sz = ret.length;
        if(sz < 2) return -2;
        System.arraycopy(ret, 0, answer, 0, sz - 2);
        System.arraycopy(ret, sz - 2, sw, 0, 2);

        return sz - 2;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
