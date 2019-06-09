package br.com.autopass.vegastps530.legacy;

public class VL4MIF extends CWRAPPER {
    class MIFARE_KEY {
        byte keyType;
        byte[] key = new byte[6];
    }

    ;

    byte cl_on;

    byte[] pApdu;
    byte[] pAnswer;
    MIFARE_KEY[] keyS;
    byte[] keyL; // a key for the last command.
    VSC_COMM comm;

    public VL4MIF(VSC_COMM comm) {
        pApdu = new byte[CONST.SIZE_BUFFER];
        pAnswer = new byte[CONST.SIZE_BUFFER];
        keyS = new MIFARE_KEY[CONST.MAX_NUM_KEYS];
        for (int i = 0; i < keyS.length; i++) {
            keyS[i] = new MIFARE_KEY();
        }
        keyL = new byte[6];

        this.comm = comm;
    }

    public int VL4MIF_Open(byte[] atq_sak, byte szUID, byte[] uid, byte[] fid, byte[] ltc) {
        byte[] sw = new byte[2];
        byte szApdu, szAnswer;
        int rc;
        cl_on = 1;

        // Select Sam APP
        pApdu[0] = (byte) 0x00;
        pApdu[1] = (byte) 0xA4;
        pApdu[2] = (byte) 0x00;
        pApdu[3] = (byte) 0x0C;
        pApdu[4] = (byte) 0x02;
        pApdu[5] = fid[0];
        pApdu[6] = fid[1];
        rc = comm.VL_ScardTransmit(CONST.SAM_DEVICE_ID, pApdu, 7, pAnswer, sw);

        // Select Sam APP
        pApdu[0] = (byte) 0x00;
        pApdu[1] = (byte) 0xB0;
        pApdu[2] = (byte) 0x00;
        pApdu[3] = (byte) 0x00;
        pApdu[4] = (byte) 0x38;
        rc = comm.VL_ScardTransmit(CONST.SAM_DEVICE_ID, pApdu, 5, pAnswer, sw);
        if (rc < 0) return rc;

        byte[] _ltc = new byte[3];
        for (int i = 0; i < 3; i++) _ltc[i] = pAnswer[22 + 19 + i]; // to get the newest data
        memcpy(ltc, _ltc, ltc.length);

        szApdu = 0;
        // DO THE SAM RESET CHANNEL.
        memcpy(pApdu, CONST.VIRTUALIZATION_APDU, sizeof(CONST.VIRTUALIZATION_APDU));
        pApdu[CONST.P1_OFS] = CONST.VIRTUAL_OPEN;
        pApdu[CONST.P2_OFS] = (byte) 0x00;
        szApdu = CONST.DATA_OFS;
        memcpy(pApdu, szApdu, CONST.VIRTUAL_FID, 0, sizeof(CONST.VIRTUAL_FID));
        szApdu += sizeof(CONST.VIRTUAL_FID);
        memcpy(pApdu, szApdu, atq_sak, 3);
        szApdu += 3;
        if (uid != null) memcpy(pApdu, szApdu, uid, szUID);
        else {
            pApdu[CONST.P2_OFS] = (byte) 0x00; // CARD NOT PRESENT
            memset(pApdu, szApdu, (byte) 0x00, szUID);
        }
        szApdu += szUID;
        pApdu[CONST.P3_OFS] = (byte) (szApdu - CONST.DATA_OFS);
        szAnswer = (byte) sizeof(pAnswer);

        rc = comm.VL_ScardTransmit(CONST.CLV_DEVICE_ID, pApdu, szApdu, pAnswer, sw);
        szAnswer = (byte) rc;
        // memset(VL4MIF_CONST.VIRTUAL_KAUTH, 0x00, sizeof(VL4MIF_CONST.VIRTUAL_KAUTH));
        // // INIT THE KEY AUTH.
        if (rc >= 0) {
            if (!memcmp(CONST.SW_OK, sw, sizeof(CONST.SW_OK))) return CONST.OK;
            if (sw[0] == (byte) 0x90 && sw[1] == (byte) 0x90) {
                memcpy(pApdu, CONST.SELECT_FILE, sizeof(CONST.SELECT_FILE));
                szApdu = (byte) sizeof(CONST.SELECT_FILE);
                szAnswer = (byte) sizeof(pAnswer);
                rc = comm.VL_ScardTransmit(CONST.SAM_DEVICE_ID, pApdu, szApdu, pAnswer, sw);
                szAnswer = (byte) rc;
                memcpy(pApdu, CONST.READ_FILE, sizeof(CONST.READ_FILE));
                szApdu = (byte) sizeof(CONST.READ_FILE);
                szAnswer = (byte) sizeof(pAnswer);
                rc = comm.VL_ScardTransmit(CONST.SAM_DEVICE_ID, pApdu, szApdu, pAnswer, sw);
                szAnswer = (byte) rc;
            }
            if (sw[0] == (byte) 0x90) return sw[1]; // to reurn OK with SIZE ANSWER.
            return CONST.NOK;
        } else return rc;
    }

    public int VL4MIF_LoadKey(byte[] key, byte keyType, byte keyIndex) {
        if (keyIndex < CONST.MAX_NUM_KEYS) {
            memcpy(keyS[keyIndex].key, key, sizeof(keyS[keyIndex].key));
            keyS[keyIndex].keyType = keyType;
            return CONST.OK;
        } else {
            if (keyIndex == CONST.KEY_IMMEDIATE) {
                memcpy(keyL, key, sizeof(keyL));
                return CONST.OK;
            }
        }
        return CONST.NOK;
    }

    // If keyType==0xFF => NO KEY_TYPE USE KEY_TYPE DEFINED INSIDE THE KEY.
    public int VL4MIF_Authenticate(byte blockNr, byte keyType, byte keyIndex) {
        byte[] sw = new byte[2];
        byte szApdu, szAnswer, sectN;
        int rc;
        // ASK THE SAM FOR AUTHENTICATION
        memcpy(pApdu, CONST.VIRTUALIZATION_APDU, sizeof(CONST.VIRTUALIZATION_APDU));
        pApdu[CONST.P1_OFS] = CONST.VIRTUAL_AUTHENTICATE;
        pApdu[CONST.P2_OFS] = blockNr;
        szApdu = CONST.DATA_OFS;
        pApdu[szApdu++] = keyType;
        if (keyIndex < CONST.MAX_NUM_KEYS) memcpy(pApdu, szApdu, keyS[keyIndex].key, sizeof(keyS[keyIndex].key));
        else return CONST.NOK;
        szApdu += sizeof(keyS[keyIndex].key);
        pApdu[CONST.P3_OFS] = (byte) (szApdu - CONST.DATA_OFS);
        szAnswer = (byte) sizeof(pAnswer);

        sectN = (byte) ((blockNr < 128) ? (blockNr >> 2) : 32 + ((blockNr >> 4) & (byte) 0x7));

        rc = comm.VL_ScardTransmit(CONST.CLV_DEVICE_ID, pApdu, szApdu, pAnswer, sw);
        szAnswer = (byte) rc;
        if (rc >= 0) {
            if (!memcmp(CONST.SW_OK, sw, sizeof(CONST.SW_OK))) {
                /*
                 * if (szAnswer != 0 && (szAnswer <= sizeof(VL4MIF_CONST.VIRTUAL_KAUTH))) {
                 * memcpy(VL4MIF_CONST.VIRTUAL_KAUTH, pAnswer, szAnswer); }
                 */
                return CONST.OK;
            }
            return CONST.NOK;
        } else return rc;
    }

    public int VL4MIF_Read(byte blockNrS, byte nBlocks, byte[] data) {
        byte[] sw = new byte[2];
        byte szApdu, szAnswer;
        int rc;
        memcpy(pApdu, CONST.VIRTUALIZATION_APDU, sizeof(CONST.VIRTUALIZATION_APDU));
        pApdu[CONST.P1_OFS] = CONST.VIRTUAL_READ_BLOCK;
        pApdu[CONST.P2_OFS] = blockNrS;
        szApdu = CONST.DATA_OFS;
        if (nBlocks > 3) nBlocks = 3;
        pApdu[CONST.P3_OFS] = (byte) (nBlocks * 16);
        szAnswer = (byte) sizeof(pAnswer);
        rc = comm.VL_ScardTransmit(CONST.CLV_DEVICE_ID, pApdu, szApdu, pAnswer, sw);
        szAnswer = (byte) rc;
        if (rc >= 0) {
            if (!memcmp(CONST.SW_OK, sw, sizeof(CONST.SW_OK))) {
                memcpy(data, pAnswer, nBlocks << 4);
                return CONST.OK;
            } else return CONST.NOK;
        } else return rc;
    }

    public int VL4MIF_Write(byte blockNrS, byte nBlocks, byte[] data) {
        byte[] sw = new byte[2];
        byte szApdu, szAnswer;
        int rc;
        memcpy(pApdu, CONST.VIRTUALIZATION_APDU, sizeof(CONST.VIRTUALIZATION_APDU));
        pApdu[CONST.P1_OFS] = CONST.VIRTUAL_WRITE_BLOCK;
        pApdu[CONST.P2_OFS] = blockNrS;
        szApdu = CONST.DATA_OFS;
        if (nBlocks > 3) nBlocks = 3;
        pApdu[CONST.P3_OFS] = (byte) (nBlocks << 4);
        memcpy(pApdu, szApdu, data, nBlocks << 4);
        szApdu += nBlocks << 4;
        pApdu[CONST.P3_OFS] = (byte) (szApdu - CONST.DATA_OFS);
        szAnswer = (byte) sizeof(pAnswer);
        rc = comm.VL_ScardTransmit(CONST.CLV_DEVICE_ID, pApdu, szApdu, pAnswer, sw);
        szAnswer = (byte) rc;
        if (rc >= 0) {
            if (!memcmp(CONST.SW_OK, sw, sizeof(CONST.SW_OK))) return CONST.OK;
            return CONST.NOK;
        } else return rc;
    }

    public int VL4MIF_ValueTransfer(byte blockNrS, byte[] value, byte blockNrD) {
        byte[] sw = new byte[2];
        byte szApdu, szAnswer;
        int rc;
        memcpy(pApdu, CONST.VIRTUALIZATION_APDU, sizeof(CONST.VIRTUALIZATION_APDU));
        pApdu[CONST.P1_OFS] = CONST.VIRTUAL_VALUE_TRANSFER;
        pApdu[CONST.P2_OFS] = blockNrS;
        szApdu = CONST.DATA_OFS;
        memcpy(pApdu, szApdu, value, 4);
        szApdu += 4;
        pApdu[szApdu++] = blockNrD;
        pApdu[CONST.P3_OFS] = (byte) (szApdu - CONST.DATA_OFS);
        szAnswer = (byte) sizeof(pAnswer);
        rc = comm.VL_ScardTransmit(CONST.CLV_DEVICE_ID, pApdu, szApdu, pAnswer, sw);
        szAnswer = (byte) rc;
        if (rc >= 0) {
            if (!memcmp(CONST.SW_OK, sw, sizeof(CONST.SW_OK)))
                return CONST.OK;
            else if (sw[0] == (byte) 0x6B && sw[1] == (byte) 0x41) {
                return CONST.NOK | 0x00FF00FF;
            } else
                return CONST.NOK;
        } else return rc;
    }

}
