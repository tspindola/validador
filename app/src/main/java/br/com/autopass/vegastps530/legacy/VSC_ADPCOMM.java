package br.com.autopass.vegastps530.legacy;

/*import com.imagpay.Apdu_Send;
import com.imagpay.Settings;
import com.imagpay.mpos.MposHandler;
import com.imagpay.utils.StringUtils;*/

import android.util.Log;

/**
 * Created by rafae on 03/04/2018.
 */

public class VSC_ADPCOMM extends CConverter
{
    public final short SAM_DEVICE_ID	=	0x10;
    public final short CLD_DEVICE_ID	=	0x20;
    public final short CARD_DEVICE_ID =	0x30;
    public final short CLV_DEVICE_ID	 =	(short) ((CLD_DEVICE_ID<<8) | SAM_DEVICE_ID);

    public int VADPT_ScardTransmit(short deviceID, byte[] apdu, int szApdu, byte[] answer, byte[] sw)
    {
        int sz = 0, slot = -1;

        switch (deviceID)
        {
            case SAM_DEVICE_ID:
                slot = SerialDeviceManager.SLOT_PSAM;
                break;
            case CLD_DEVICE_ID:
                slot = SerialDeviceManager.SLOT_NFC;
                break;
            case CARD_DEVICE_ID:
                slot = SerialDeviceManager.SLOT_IC;
                break;
        }
        byte[] sapdu = new byte[szApdu];
        System.arraycopy(apdu, 0, sapdu, 0, szApdu);
        Global.APDU_TRACE += "SLOT" + slot + " >> " + Global.bytesToHex(sapdu) + "\r\n";
        long _t1 = System.currentTimeMillis();
        byte[] ret = SerialDeviceManager.SCardTransmit(slot, sapdu);
        long _t2 = System.currentTimeMillis();
        Log.w("APDU_TIME", (_t2 - _t1) + "ms");
        Global.APDU_TRACE += "SLOT" + slot + " << " + Global.bytesToHex(ret) + "\r\n";
        if(ret == null) return -1;
        sz = ret.length;
        if(sz < 2) return -2;
        System.arraycopy(ret, 0, answer, 0, sz - 2);
        System.arraycopy(ret, sz - 2, sw, 0, 2);

        return sz - 2;
    }


}
