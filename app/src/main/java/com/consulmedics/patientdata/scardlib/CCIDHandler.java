package com.consulmedics.patientdata.scardlib;

import java.nio.ByteBuffer;

class CCIDHandler {
    private static final int CCID_DESCRIPTOR_INDEX = 18;
    static CCID_RESPONSE ccidResponse;

    CCIDHandler() {
    }

    private static int BITS(int m, int l, int n) {
        return (1 << m - l + 1) - 1 & n >> l;
    }

    static int Ccid_ResponseStatus(byte bStatus, byte bError) {
        char bmICCStatus = (char)BITS(1, 0, bStatus);
        char bmCommandStatus = (char)BITS(7, 6, bStatus);
        short ccidStatus;
        if (bmCommandStatus == 0) {
            ccidStatus = 256;
        } else if (bmCommandStatus == 2) {
            ccidStatus = 768;
        } else {
            ccidStatus = bError;
        }

        return ccidStatus;
    }

    byte[] Ccid_BuildRequest(char MessageType, char Slot, char rfu0, char rfu1, char rfu2, ByteBuffer Data, long DataLen) {
        byte[] ccidRequest = new byte[(int)(10L + DataLen)];
        ccidRequest[0] = (byte)MessageType;
        ccidRequest[1] = (byte)((int)(DataLen & 255L));
        ccidRequest[2] = (byte)((int)(DataLen >> 8 & 255L));
        ccidRequest[3] = (byte)((int)(DataLen >> 16 & 255L));
        ccidRequest[4] = (byte)((int)(DataLen >> 24 & 255L));
        ccidRequest[5] = (byte)Slot;
        ccidRequest[6] = (byte)MessageType;
        ccidRequest[7] = (byte)rfu0;
        ccidRequest[8] = (byte)rfu1;
        ccidRequest[9] = (byte)rfu2;
        if (DataLen > 0L) {
            System.arraycopy(Data.array(), 0, ccidRequest, 10, (int)DataLen);
        }

        return ccidRequest;
    }

    CCID_RESPONSE Ccid_VerifyResponse(byte[] ccidResponse, long ResponseLength, char RequestMessageType, char Slot, long MaxRespLength) {
        CCID_RESPONSE response = new CCID_RESPONSE();
        response.ntStatus = 0L;
        int ResponseMessageType = 255;
        switch (RequestMessageType) {
            case 'a':
            case 'l':
            case 'm':
                ResponseMessageType = 130;
                break;
            case 'b':
            case 'i':
            case 'o':
                ResponseMessageType = 128;
                break;
            case 'c':
            case 'e':
            case 'j':
            case 'n':
            case 'q':
            case 'r':
                ResponseMessageType = 129;
            case 'd':
            case 'f':
            case 'g':
            case 'h':
            case 'p':
            default:
                break;
            case 'k':
                ResponseMessageType = 131;
                break;
            case 's':
                ResponseMessageType = 132;
        }

        if ((ccidResponse[0] & 255) == ResponseMessageType && ccidResponse[5] == Slot && ccidResponse[6] == RequestMessageType) {
            response.ccidStatus = (short)Ccid_ResponseStatus(ccidResponse[7], ccidResponse[8]);
            response.slotStatus = (short)(ccidResponse[7] & 3);
            response.pExtra = (short)ccidResponse[9];
            byte[] tempByte = new byte[]{ccidResponse[1], ccidResponse[2], ccidResponse[3], ccidResponse[4]};

            for(int i = 0; i < 4; ++i) {
                response.responseLen += ((long)tempByte[i] & 255L) << 8 * i;
            }

            if (response.responseLen != 0L) {
                if (response.responseLen <= MaxRespLength) {
                    response.respData = new byte[(int)response.responseLen];
                    System.arraycopy(ccidResponse, 10, response.respData, 0, (int)response.responseLen);
                } else {
                    response.ntStatus = -992L;
                }
            }
        } else {
            response.ntStatus = -996L;
        }

        return response;
    }

    long Ccid_ToNtStatus(int ccidStatus) {
        int temp = 0;

        if (ccidStatus < 0) {
            temp = ccidStatus & 255;
        } else {
            temp = ccidStatus;
        }

        long ntStatus;
        switch (temp) {
            case 0:
                ntStatus = -991L;
                break;
            case 64:
                ntStatus = -986L;
                break;
            case 130:
            case 179:
            case 189:
            case 190:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case 248:
            case 251:
            case 253:
                ntStatus = -998L;
                break;
            case 162:
                ntStatus = -989L;
                break;
            case 176:
                ntStatus = -992L;
                break;
            case 177:
            case 191:
                ntStatus = -995L;
                break;
            case 192:
            case 239:
            case 240:
            case 252:
            case 254:
                ntStatus = -990L;
                break;
            case 224:
                ntStatus = -987L;
                break;
            case 255:
                ntStatus = -988L;
                break;
            case 256:
                ntStatus = 0L;
                break;
            case 512:
                ntStatus = -996L;
                break;
            case 768:
                ntStatus = -994L;
                break;
            default:
                ntStatus = -986L;
        }

        return ntStatus;
    }

    class CCID_MODIFY_PARAMS {
        char bPinOperation;
        char bTimeOut;
        char bmFormatString;
        char bmPINBlockString;
        char bmPINLengthFormat;
        char bInsertionOffsetOld;
        char bInsertionOffsetNew;
        short wPINMaxExtraDigit;
        char bConfirmPIN;
        char bEntryValidationCondition;
        char bNumberMessage;
        short wLangId;
        char bMsgIndex1;
        char bMsgIndex2;
        char bMsgIndex3;
        char[] bTeoPrologue;
        ByteBuffer abData;

        CCID_MODIFY_PARAMS() {
        }
    }

    class CCID_VERIFY_PARAMS {
        char bPinOperation;
        char bTimeOut;
        char bmFormatString;
        char bmPINBlockString;
        char bmPINLengthFormat;
        short wPINMaxExtraDigit;
        char bEntryValidationCondition;
        char bNumberMessage;
        short wLangId;
        char bMsgIndex;
        char[] bTeoPrologue;
        ByteBuffer abData;

        CCID_VERIFY_PARAMS() {
        }
    }

    class CCID_RESPONSE {
        byte[] respData;
        long responseLen;
        short ccidStatus;
        short slotStatus;
        short pExtra;
        long ntStatus;

        CCID_RESPONSE() {
        }
    }

    class CCID_REQUEST {
        char bMessageType;
        long dwLength;
        char bSlot;
        char bSeq;
        ByteBuffer abRFU;
        ByteBuffer abData;

        CCID_REQUEST() {
        }
    }

    class USB_CCID_DESCRIPTOR {
        char bLength;
        char bDescriptorType;
        short bcdCCID;
        char bMaxSlotIndex;
        char bVoltageSupport;
        long dwProtocols;
        long dwDefaultClock;
        long dwMaximumClock;
        char bNumClockSupported;
        long dwDataRate;
        long dwMaxDataRate;
        char bNumDataRatesSupported;
        long dwMaxIFSD;
        long dwSyncProtocols;
        long dwMechanical;
        long dwFeatures;
        long dwMaxCCIDMsgLength;
        char bClassGetResponse;
        char bClassEnvelope;
        short wLCDLayout;
        char bPINSupport;
        char bMaxCCIDBusySlots;

        USB_CCID_DESCRIPTOR() {
        }
    }

    class CCID_SLOT_ERROR {
        static final int CCID_STATUS_SUCCESS = 256;
        static final int CCID_INTERNAL_ERROR = 512;
        static final int CCID_STATUS_WAIT_TIME_EXTENSION = 768;
        static final int CCID_ERROR_CMD_ABORTED = 255;
        static final int CCID_ERROR_ICC_MUTE = 254;
        static final int CCID_ERROR_XFR_PARITY_ERROR = 253;
        static final int CCID_ERROR_XFR_OVERRUN = 252;
        static final int CCID_ERROR_HW_ERROR = 251;
        static final int CCID_ERROR_BAD_ATR_TS = 248;
        static final int CCID_ERROR_BAD_ATR_TCK = 247;
        static final int CCID_ERROR_ICC_PROTOCOL_NOT_SUPPORTED = 246;
        static final int CCID_ERROR_ICC_CLASS_NOT_SUPPORTED = 245;
        static final int CCID_ERROR_PROCEDURE_BYTE_CONFLICT = 244;
        static final int CCID_ERROR_DEACTIVATED_PROTOCOL = 243;
        static final int CCID_ERROR_BUSY_WITH_AUTO_SEQUENCE = 242;
        static final int CCID_ERROR_CMD_SLOT_BUSY = 224;
        static final int CCID_ERROR_CMD_FAILED = 64;
        static final int CCID_ERROR_CMD_NOT_SUPPORTED = 0;
        static final int CCID_USRERROR_TIMEOUT = 192;
        static final int CCID_USRERROR_INVALID_PARAMETER = 191;
        static final int CCID_USRERROR_PROTOCOL_ERROR = 190;
        static final int CCID_USRERROR_CARD_NOT_PRESENT = 189;
        static final int CCID_USRERROR_BYTE_MISMATCH_ERROR = 162;
        static final int CCID_USRERROR_BUFFER_TOO_SMALL = 176;
        static final int CCID_USRERROR_CMD_MISMATCH = 160;
        static final int CCID_USRERROR_CMD_NOT_SUPPORTED = 163;
        static final int CCID_ERROR_INVALID_ACTIVATION_ID = 177;
        static final int CCID_ERROR_OPERATION_NOT_ALLOWED = 130;
        static final int CCID_ERROR_COUNTER_ZERO = 179;
        static final int CCID_STATUS_PIN_TIMEOUT = 240;
        static final int CCID_STATUS_PIN_CANCELLED = 239;
        static final int CCID_STATUS_PIN_ENTRY_COMPLETE = 144;
        static final int CCID_STATUS_PIN_ENTRY_CONTINUING = 145;
        static final int CCID_STATUS_COMMAND_ABORTED = 146;
        static final int CCID_STATUS_PIN_LENGTH_UNDER_RUN = 147;
        static final int CCID_STATUS_PIN_LENGTH_OVER_RUN = 148;
        static final int CCID_STATUS_PIN_ENTRY_CLEARED = 149;
        static final int CCID_STATUS_NEWPIN1_ENTRY_STARTED = 150;
        static final int CCID_STATUS_NEWPIN2_ENTRY_STARTED = 151;
        static final int CCID_STATUS_PIN_ENTRY_NONE = 159;
        static final int CCID_STATUS_PIN_BKSPACE = 152;
        static final int CCID_STATUS_MAX_KEY_REACHED = 154;
        static final int CCID_STATUS_PIN_COMPLETE_ON_TIMEOUT = 155;

        CCID_SLOT_ERROR() {
        }
    }

    class CCID_SLOT_STATUS {
        static final int CCID_ICC_ACTIVE = 0;
        static final int CCID_ICC_PRESENT = 1;
        static final int CCID_ICC_ABSENT = 2;

        CCID_SLOT_STATUS() {
        }
    }

    class CCID_COMMAND_STATUS {
        private static final int CCID_COMMAND_STATUS_NO_ERROR = 0;
        private static final int CCID_COMMAND_STATUS_CMD_FAILED = 1;
        private static final int CCID_COMMAND_STATUS_TIME_EXTN_REQUESTED = 2;

        CCID_COMMAND_STATUS() {
        }
    }

    class RDR_TO_PC_MESSAGE {
        static final int RDR_to_PC_DataBlock = 128;
        static final int RDR_to_PC_SlotStatus = 129;
        static final int RDR_to_PC_Parameters = 130;
        static final int RDR_to_PC_Escape = 131;
        static final int RDR_to_PC_DataRateAndClockFrequency = 132;

        RDR_TO_PC_MESSAGE() {
        }
    }

    static class PC_TO_RDR_MESSAGE {
        static final int PC_to_RDR_IccPowerOn = 98;
        static final int PC_to_RDR_IccPowerOff = 99;
        static final int PC_to_RDR_GetSlotStatus = 101;
        static final int PC_to_RDR_XfrBlock = 111;
        static final int PC_to_RDR_GetParameters = 108;
        static final int PC_to_RDR_ResetParameters = 109;
        static final int PC_to_RDR_SetParameters = 97;
        static final int PC_to_RDR_Escape = 107;
        static final int PC_to_RDR_IccClock = 110;
        static final int PC_to_RDR_T0APDU = 106;
        static final int PC_to_RDR_Secure = 105;
        static final int PC_to_RDR_Mechanical = 113;
        static final int PC_to_RDR_Abort = 114;
        static final int PC_to_RDR_SetDataRateAndClockFrequency = 115;

        PC_TO_RDR_MESSAGE() {
        }
    }

    class CCID_PIN_OPERATION {
        private static final int CCID_PIN_VERIFY = 0;
        private static final int CCID_PIN_MODIFY = 1;

        CCID_PIN_OPERATION() {
        }
    }
}
