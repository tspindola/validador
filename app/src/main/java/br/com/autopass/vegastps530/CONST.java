package br.com.autopass.vegastps530;

/**
 * Created by rafae on 03/04/2018.
 */

public class CONST
{
    public static final int SIZE_BUFFER = 255;
    public static final byte CLA_OFS = 0; // CLA OFFSET INSIDE APUD
    public static final byte INS_OFS = 1; // CLA OFFSET INSIDE APUD
    public static final byte P1_OFS = 2; // CLA OFFSET INSIDE APUD
    public static final byte P2_OFS = 3; // CLA OFFSET INSIDE APUD
    public static final byte P3_OFS = 4; // CLA OFFSET INSIDE APUD
    public static final byte DATA_OFS = 5; // CLA OFFSET INSIDE APUD

    public static final short SAM_DEVICE_ID = 0x10;
    public static final short CLD_DEVICE_ID = 0x20;
    public static final short CARD_DEVICE_ID = 0x30;
    public static final short CLV_DEVICE_ID = (short) ((CLD_DEVICE_ID << 8) | SAM_DEVICE_ID);

    // ===== VIRTUALIZING COMMANDS FOR MIFARE VIRTUALIZATION OVER
    // CIPURSE/PLUS_SL3/DESFIRE/ETC =====//
    public static final byte VIRTUAL_OPEN = 0x00; // P1, P2=(DMD DEFINITION= )
    // DATA= FILE_ID[2],
    // UID[4,7,10]{optional}.
    public static final byte VIRTUAL_CLOSE = 0x0F; // P1, P2=0x80 = FLUSH
    public static final byte VIRTUAL_AUTHENTICATE = 0x01; // P1, P2=
    // BLOCK_NUMBER
    public static final byte VIRTUAL_READ_BLOCK = 0x02; // P1, P2= BLOCK_NUMBER
    public static final byte VIRTUAL_WRITE_BLOCK = 0x03; // P1, P2= BLOCK_NUMBER
    public static final byte VIRTUAL_VALUE_TRANSFER = 0x04; // P1, P2= BLOCK_NrS
    // DATA=VALUE[0],[1],[2],[3=00..7F=INC,
    // 80..FF=DEC],BLKNRD[1][if
    // !=0 DO TRANSFER]
    public static final byte VIRTUAL_RESTORE_TRANSFER = 0x05; // P1, P2=
    // BLOCK_NrS
    // DATA=BLOCK_NdD
    // {if !=0 do
    // TRANSFER}

    public static final byte[] SW_OK = { (byte) 0x90, 0x00 };
    public static final byte[] VIRTUALIZATION_APDU = { (byte) 0x80, 0x12 };
    public static final byte[] VIRTUAL_FID = { 0x03, 0x10 }; // FILE_ID WITH A
    // TRANSLATION
    // TABLE INSIDE
    // THE SAM. EACH
    // FILE_ID CAN
    // SUPPORT
    // MULTIPLE
    // VIRTUALIZATIONS.
    // Or the USER MAY SELECT A SPECIFIC FILE_ID for different projects.

    public static final byte[] SELECT_FILE = { 0x00, (byte) 0xA4, 0x00, 0x0C,
            0x02, 0x3F, 0x03 };
    public static final byte[] READ_FILE = { 0x00, (byte) 0xB0, 0x00, 0x00,
            (byte) 0x80 };
    public static final int OK = 0;
    public static final int NOK = 0x80000000;

    public class KEY_AUTH_BM {
        byte A;
        byte B;
    };

    public static KEY_AUTH_BM[] VIRTUAL_KAUTH = new KEY_AUTH_BM[5];

    public static final int MAX_NUM_KEYS = 16;
    public static final byte KEY_IMMEDIATE = (byte) 0xFF;
}
