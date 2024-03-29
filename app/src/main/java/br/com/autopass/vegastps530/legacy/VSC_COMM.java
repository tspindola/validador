package br.com.autopass.vegastps530.legacy;
import android.content.Context;
import android.util.Log;
import br.com.autopass.vegastps530.legacy.CONST;
import br.com.autopass.vegastps530.legacy.CWRAPPER;
import br.com.autopass.vegastps530.legacy.VSC_ADPCOMM;


public class VSC_COMM extends CWRAPPER
{
    VSC_ADPCOMM adapt;
    Context ctx;

    public VSC_COMM(Context ctx){
        this.ctx = ctx;
    }

    public byte[] newSCardTransmit(short deviceID, byte[] pApdu)
    {
        byte[] panswer = new byte[512];
        byte[] sw = new byte[2];
        int res = VL_ScardTransmit(deviceID, pApdu, pApdu.length,
                panswer, sw);
        if(res >= 0)
        {
            byte[] retarray = new byte[res + 2];
            System.arraycopy(panswer, 0, retarray, 0, res);
            System.arraycopy(sw, 0, retarray, res, 2);
            return retarray;
        }
        return new byte[0];
    }

    public int VL_ScardTransmit(short deviceID, byte[] pApdu, int szApdu,
                                byte[] pAnswer, byte[] sw) {
        int rc;
        byte szAnsMax, swCL, VL_szAnswer, VL_szApdu, szAnswer;
        byte deviceS;
        byte deviceR;

        byte[] VL_pApdu = new byte[CONST.SIZE_BUFFER];
        byte[] VL_pAnswer = new byte[CONST.SIZE_BUFFER];

        deviceS = (byte) (deviceID & 0xFF);
        deviceR = (byte) (deviceID >> 8);

        szAnsMax = (byte) CONST.SIZE_BUFFER;
        if (adapt == null)
            adapt = new VSC_ADPCOMM(ctx);
        rc = adapt.VADPT_ScardTransmit(deviceS, pApdu, szApdu, pAnswer, sw);
        szAnswer = (byte) rc;
        if (rc < 0)
            return rc;

        if (sw[0] == (byte) 0x6C) {
            memcpy(VL_pApdu, pApdu, szApdu);
        }

        for (; sw[0] == (byte) 0x91 || sw[0] == (byte) 0x61
                || sw[0] == (byte) 0x6c;) {
            switch (sw[0]) {
                case (byte) 0x61: {
                    VL_pApdu[0] = (byte) 0x00;
                    VL_pApdu[1] = (byte) 0xC0;
                    VL_pApdu[2] = (byte) 0x00;
                    VL_pApdu[3] = (byte) 0x00;
                    VL_pApdu[4] = sw[1];
                    szApdu = 5;
                    rc = adapt.VADPT_ScardTransmit(deviceS, VL_pApdu, szApdu,
                            pAnswer, sw);
                    szAnswer = (byte) rc;
                    return rc;
                }
                case (byte) 0x6C: {
                    VL_pApdu[4] = sw[1];
                    szApdu = 5;
                    rc = adapt.VADPT_ScardTransmit(deviceS, VL_pApdu, szApdu,
                            pAnswer, sw);
                    szAnswer = (byte) rc;
                    return rc;
                }
                case (byte) (byte) 0x91: {
                    // CHEKCS IF LAST COMMAND WITH 91XX RETURNED DATA.. TO AVOID
                    // SEND GET_RESPONSE. @@@
                    if (szAnswer == 0)
                    {
                        VL_pApdu[0] = (byte) 0x00;
                        VL_pApdu[1] = (byte) 0xC0;
                        VL_pApdu[2] = (byte) 0x00;
                        VL_pApdu[3] = (byte) 0x00;
                        VL_pApdu[4] = sw[1];
                        // SEND GET_RESPONSE TO GET THE COMMAND FOR CL CARD.
                        szApdu = 5;
                        VL_szAnswer = (byte) CONST.SIZE_BUFFER;
                        rc = adapt.VADPT_ScardTransmit(deviceS, VL_pApdu, szApdu,
                                VL_pAnswer, sw);
                        VL_szAnswer = (byte) rc;
                        if (rc < 0)
                            return rc;
                        if (sw[0] == (byte) 0x6C) {
                            VL_pApdu[4] = sw[1];
                            szApdu = 5;
                            rc = adapt.VADPT_ScardTransmit(deviceS, VL_pApdu,
                                    szApdu, VL_pAnswer, sw);
                            VL_szAnswer = (byte) rc;
                            if (rc < 0)
                                return rc;
                        }
                    }
                    else
                        {
                        memcpy(VL_pAnswer, pAnswer, szAnswer);
                        VL_szAnswer = szAnswer;
                        sw[0] = (byte) 0x90;
                        sw[1] = (byte) 0x00;
                    }
                    if (sw[0] == (byte) 0x90 && sw[1] == (byte) 0x00)
                    {
                        memcpy(VL_pApdu, VL_pAnswer, VL_szAnswer);
                        {
                            // SENDS THE APDU TO CL CARD
                            VL_szApdu = VL_szAnswer;
                            VL_szAnswer = (byte) CONST.SIZE_BUFFER;

                            rc = adapt.VADPT_ScardTransmit(deviceR, VL_pApdu,
                                    VL_szApdu, VL_pAnswer, sw);


                            VL_szAnswer = (byte) rc;
                            if (rc <= 0)
                            {
                                szAnswer = szAnsMax;
                                VL_pApdu[0] = (byte) 0x00;
                                VL_pApdu[1] = (byte) 0xC2;
                                VL_pApdu[2] = (byte) 0x67;
                                VL_pApdu[3] = (byte) 0x00;
                                VL_pApdu[4] = (byte) 0x00;
                                rc = adapt.VADPT_ScardTransmit(deviceS, VL_pApdu,
                                        5, pAnswer, sw);
                                szAnswer = (byte) rc;
                                if(rc < 0)
                                    return rc;
                            }
                            else if (VL_szAnswer < CONST.SIZE_BUFFER)
                            {
                                swCL = sw[0];
                                memcpy(VL_pApdu, CONST.DATA_OFS, VL_pAnswer,
                                        VL_szAnswer);

                                // BUILD THE SET ANSWER
                                szAnswer = szAnsMax;
                                VL_pApdu[0] = (byte) 0x00;
                                VL_pApdu[1] = (byte) 0xC2;
                                VL_pApdu[2] = sw[0];
                                VL_pApdu[3] = sw[1];
                                VL_pApdu[4] = VL_szAnswer;
                                rc = adapt.VADPT_ScardTransmit(deviceS, VL_pApdu,
                                        VL_szAnswer + 5, pAnswer, sw);
                                szAnswer = (byte) rc;
                                if (rc < 0)
                                    return rc;
                            } else
                                return -1;
                        }
                    }
                    else
                        return -2;
                    break;
                }
                default:
                    return -3;
            }
        }
        return rc;
    }

}
