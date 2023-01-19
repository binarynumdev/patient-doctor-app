package com.consulmedics.patientdata.scardlib;

class Defs {
    static final long ERROR_IPSEC_IKE_OUT_OF_MEMORY = 13859L;
    static final long INVALID_HANDLE_VALUE = -1L;
    static long[][] sBitRateAdjustment = new long[][]{{0L, 0L}, {1L, 1L}, {2L, 1L}, {4L, 1L}, {8L, 1L}, {16L, 1L}, {32L, 1L}, {64L, 1L}, {12L, 1L}, {20L, 1L}, {20L, 1L}, {20L, 1L}, {20L, 1L}, {20L, 1L}, {20L, 1L}};
    static long[][] sClockRateConversion = new long[][]{{372L, 4000000L}, {372L, 5000000L}, {558L, 6000000L}, {744L, 8000000L}, {1116L, 12000000L}, {1488L, 16000000L}, {1860L, 20000000L}, {372L, 4000000L}, {372L, 4000000L}, {512L, 5000000L}, {768L, 7500000L}, {1024L, 10000000L}, {1536L, 15000000L}, {2048L, 20000000L}, {372L, 4000000L}, {372L, 4000000L}};
    static final long MAX_CMD_RSP_LEN = 65536L;
    static final int MAXIMUM_ATTR_STRING_LENGTH = 32;
    static final int MAXIMUM_SMARTCARD_READERS = 16;
    static final int SCARD_CLASS_VENDOR_INFO = 1;
    static final int SCARD_CLASS_COMMUNICATIONS = 2;
    static final int SCARD_CLASS_PROTOCOL = 3;
    static final int SCARD_CLASS_POWER_MGMT = 4;
    static final int SCARD_CLASS_SECURITY = 5;
    static final int SCARD_CLASS_MECHANICAL = 6;
    static final int SCARD_CLASS_VENDOR_DEFINED = 7;
    static final int SCARD_CLASS_IFD_PROTOCOL = 8;
    static final int SCARD_CLASS_ICC_STATE = 9;
    static final int SCARD_CLASS_PERF = 32766;
    static final int SCARD_CLASS_SYSTEM = 32767;
    static final int SCARD_ATTR_VENDOR_NAME = SCARD_ATTR_VALUE(1, 256);
    static final int SCARD_ATTR_VENDOR_IFD_TYPE = SCARD_ATTR_VALUE(1, 257);
    static final int SCARD_ATTR_VENDOR_IFD_VERSION = SCARD_ATTR_VALUE(1, 258);
    static final int SCARD_ATTR_VENDOR_IFD_SERIAL_NO = SCARD_ATTR_VALUE(1, 259);
    static final int SCARD_ATTR_CHANNEL_ID = SCARD_ATTR_VALUE(2, 272);
    static final int SCARD_ATTR_PROTOCOL_TYPES = SCARD_ATTR_VALUE(3, 288);
    static final int SCARD_ATTR_DEFAULT_CLK = SCARD_ATTR_VALUE(3, 289);
    static final int SCARD_ATTR_MAX_CLK = SCARD_ATTR_VALUE(3, 290);
    static final int SCARD_ATTR_DEFAULT_DATA_RATE = SCARD_ATTR_VALUE(3, 291);
    static final int SCARD_ATTR_MAX_DATA_RATE = SCARD_ATTR_VALUE(3, 292);
    static final int SCARD_ATTR_MAX_IFSD = SCARD_ATTR_VALUE(3, 293);
    static final int SCARD_ATTR_POWER_MGMT_SUPPORT = SCARD_ATTR_VALUE(4, 305);
    static final int SCARD_ATTR_USER_TO_CARD_AUTH_DEVICE = SCARD_ATTR_VALUE(5, 320);
    static final int SCARD_ATTR_USER_AUTH_INPUT_DEVICE = SCARD_ATTR_VALUE(5, 322);
    static final int SCARD_ATTR_CHARACTERISTICS = SCARD_ATTR_VALUE(6, 336);
    static final int SCARD_ATTR_CURRENT_PROTOCOL_TYPE = SCARD_ATTR_VALUE(8, 513);
    static final int SCARD_ATTR_CURRENT_CLK = SCARD_ATTR_VALUE(8, 514);
    static final int SCARD_ATTR_CURRENT_F = SCARD_ATTR_VALUE(8, 515);
    static final int SCARD_ATTR_CURRENT_D = SCARD_ATTR_VALUE(8, 516);
    static final int SCARD_ATTR_CURRENT_N = SCARD_ATTR_VALUE(8, 517);
    static final int SCARD_ATTR_CURRENT_W = SCARD_ATTR_VALUE(8, 518);
    static final int SCARD_ATTR_CURRENT_IFSC = SCARD_ATTR_VALUE(8, 519);
    static final int SCARD_ATTR_CURRENT_IFSD = SCARD_ATTR_VALUE(8, 520);
    static final int SCARD_ATTR_CURRENT_BWT = SCARD_ATTR_VALUE(8, 521);
    static final int SCARD_ATTR_CURRENT_CWT = SCARD_ATTR_VALUE(8, 522);
    static final int SCARD_ATTR_CURRENT_EBC_ENCODING = SCARD_ATTR_VALUE(8, 523);
    static final int SCARD_ATTR_EXTENDED_BWT = SCARD_ATTR_VALUE(8, 524);
    static final int SCARD_ATTR_ICC_PRESENCE = SCARD_ATTR_VALUE(9, 768);
    static final int SCARD_ATTR_ICC_INTERFACE_STATUS = SCARD_ATTR_VALUE(9, 769);
    static final int SCARD_ATTR_CURRENT_IO_STATE = SCARD_ATTR_VALUE(9, 770);
    static final int SCARD_ATTR_ATR_STRING = SCARD_ATTR_VALUE(9, 771);
    static final int SCARD_ATTR_ICC_TYPE_PER_ATR = SCARD_ATTR_VALUE(9, 772);
    static final int SCARD_ATTR_ESC_RESET = SCARD_ATTR_VALUE(7, 40960);
    static final int SCARD_ATTR_ESC_CANCEL = SCARD_ATTR_VALUE(7, 40963);
    static final int SCARD_ATTR_ESC_AUTHREQUEST = SCARD_ATTR_VALUE(7, 40965);
    static final int SCARD_ATTR_MAXINPUT = SCARD_ATTR_VALUE(7, 40967);
    static final int SCARD_ATTR_DEVICE_UNIT = SCARD_ATTR_VALUE(32767, 1);
    static final int SCARD_ATTR_DEVICE_IN_USE = SCARD_ATTR_VALUE(32767, 2);
    static final int SCARD_ATTR_DEVICE_FRIENDLY_NAME = SCARD_ATTR_VALUE(32767, 3);
    static final int SCARD_ATTR_DEVICE_SYSTEM_NAME = SCARD_ATTR_VALUE(32767, 4);
    static final int SCARD_ATTR_SUPRESS_T1_IFS_REQUEST = SCARD_ATTR_VALUE(32767, 7);
    static final int SCARD_PERF_NUM_TRANSMISSIONS = SCARD_ATTR_VALUE(32766, 1);
    static final int SCARD_PERF_BYTES_TRANSMITTED = SCARD_ATTR_VALUE(32766, 2);
    static final int SCARD_PERF_TRANSMISSION_TIME = SCARD_ATTR_VALUE(32766, 3);
    static final int SCARD_READER_TYPE_SERIAL = 1;
    static final int SCARD_READER_TYPE_PARALELL = 2;
    static final int SCARD_READER_TYPE_KEYBOARD = 4;
    static final int SCARD_READER_TYPE_SCSI = 8;
    static final int SCARD_READER_TYPE_IDE = 16;
    static final int SCARD_READER_TYPE_USB = 32;
    static final int SCARD_READER_TYPE_PCMCIA = 64;
    static final int SCARD_READER_TYPE_VENDOR = 240;
    static final int FILE_ANY_ACCESS = 0;
    static final int FILE_SPECIAL_ACCESS = 0;
    static final int FILE_READ_ACCESS = 1;
    static final int FILE_WRITE_ACCESS = 2;
    static final int METHOD_BUFFERED = 0;
    static final int METHOD_IN_DIRECT = 1;
    static final int METHOD_OUT_DIRECT = 2;
    static final int METHOD_NEITHER = 3;
    static final long FILE_DEVICE_SMARTCARD = 49L;
    static final long IOCTL_SMARTCARD_POWER = SCARD_CTL_CODE(1);
    static final long IOCTL_SMARTCARD_GET_ATTRIBUTE = SCARD_CTL_CODE(2);
    static final long IOCTL_SMARTCARD_SET_ATTRIBUTE = SCARD_CTL_CODE(3);
    static final long IOCTL_SMARTCARD_CONFISCATE = SCARD_CTL_CODE(4);
    static final long IOCTL_SMARTCARD_TRANSMIT = SCARD_CTL_CODE(5);
    static final long IOCTL_SMARTCARD_EJECT = SCARD_CTL_CODE(6);
    static final long IOCTL_SMARTCARD_SWALLOW = SCARD_CTL_CODE(7);
    static final long IOCTL_SMARTCARD_IS_PRESENT = SCARD_CTL_CODE(10);
    static final long IOCTL_SMARTCARD_IS_ABSENT = SCARD_CTL_CODE(11);
    static final long IOCTL_SMARTCARD_SET_PROTOCOL = SCARD_CTL_CODE(12);
    static final long IOCTL_SMARTCARD_GET_STATE = SCARD_CTL_CODE(14);
    static final long IOCTL_SMARTCARD_GET_LAST_ERROR = SCARD_CTL_CODE(15);
    static final long IOCTL_SMARTCARD_GET_PERF_CNTR = SCARD_CTL_CODE(16);
    static final int SCARD_SHARE_EXCLUSIVE = 1;
    static final int SCARD_SHARE_SHARED = 2;
    static final int SCARD_SHARE_DIRECT = 3;
    static final int SCARD_LEAVE_CARD = 0;
    static final int SCARD_RESET_CARD = 1;
    static final int SCARD_UNPOWER_CARD = 2;
    static final int SCARD_EJECT_CARD = 3;
    static final int SCARD_T1_PROLOGUE_LENGTH = 3;
    static final int SCARD_T1_EPILOGUE_LENGTH = 2;
    static final int SCARD_T1_MAX_IFS = 254;
    static final long SCARD_POWER_DOWN = 0L;
    static final long SCARD_COLD_RESET = 1L;
    static final long SCARD_WARM_RESET = 2L;
    static final long SCARD_ATR_LENGTH = 33L;
    static final int SCARD_READER_MODE_TPDU = 1;
    static final int SCARD_READER_MODE_APDU = 2;

    Defs() {
    }

    static final long CCID_CTL_CODE(int code) {
        return CTL_CODE(49L, code, 0, 0);
    }

    static final int SCARD_ATTR_VALUE(int Class, int Tag) {
        return Class << 16 | Tag;
    }

    static int LOBYTE(int w) {
        return w & 255;
    }

    static int HIBYTE(int w) {
        return w >> 8;
    }

    static final long SCARD_CTL_CODE(int code) {
        return CTL_CODE(49L, code, 0, 0);
    }

    static final long CTL_CODE(long DeviceType, int Function, int Method, int Access) {
        return DeviceType << 16 | (long)(Access << 14) | (long)(Function << 2) | (long)Method;
    }

    static class SCARD_IO_REQUEST {
        int dwProtocol;
        int cbPciLength;

        SCARD_IO_REQUEST() {
        }
    }

    class NT_STATUS_CODE {
        static final long STATUS_SUCCESS = 0L;
        static final long STATUS_BUFFER_OVERFLOW = -1000L;
        static final long STATUS_DEVICE_POWER_FAILURE = -999L;
        static final long STATUS_DEVICE_PROTOCOL_ERROR = -998L;
        static final long STATUS_INSUFFICIENT_RESOURCES = -997L;
        static final long STATUS_INTERNAL_ERROR = -996L;
        static final long STATUS_INVALID_PARAMETER = -995L;
        static final long STATUS_MORE_PROCESSING_REQUIRED = -994L;
        static final long STATUS_UNRECOGNIZED_MEDIA = -993L;
        static final long STATUS_BUFFER_TOO_SMALL = -992L;
        static final long STATUS_NOT_SUPPORTED = -991L;
        static final long STATUS_IO_TIMEOUT = -990L;
        static final long STATUS_OBJECT_TYPE_MISMATCH = -989L;
        static final long STATUS_REQUEST_ABORTED = -988L;
        static final long STATUS_DEVICE_BUSY = -987L;
        static final long STATUS_UNSUCCESSFUL = -986L;
        static final long STATUS_INVALID_DEVICE_STATE = -985L;
        static final long STATUS_NO_MEDIA = -984L;
        static final long STATUS_INVALID_DEVICE_REQUEST = -983L;
        static final long STATUS_DEVICE_NOT_CONNECTED = -982L;

        NT_STATUS_CODE() {
        }
    }

    static class VERSION_CONTROL {
        long SmclibVersion;
        char DriverMajor;
        char DriverMinor;
        char FirmwareMajor;
        char FirmwareMinor;
        char UpdateKey;

        VERSION_CONTROL() {
        }
    }

    static class IFD_DFU_IOCTL {
        static final int IFD_INDEX = 1536;
        static final long IOCTL_CCID_GET_DEVICE_TYPE = Defs.CCID_CTL_CODE(1537);
        static final long IOCTL_CCID_GET_DESCRIPTOR = Defs.CCID_CTL_CODE(1538);
        static final long IOCTL_CCID_GET_FIRMWARE_VER = Defs.CCID_CTL_CODE(1541);
        static final long IOCTL_CCID_DETACH = Defs.CCID_CTL_CODE(1792);

        IFD_DFU_IOCTL() {
        }
    }

    static class MiscelaneousIOCTLS {
        static final long IOCTL_SET_PPS_PARAMS = Defs.SCARD_CTL_CODE(2073);
        static final long IOCTL_CCID_ESCAPE = Defs.SCARD_CTL_CODE(3500);

        MiscelaneousIOCTLS() {
        }
    }

    static class SmartCardOperations {
        static final long IOCTL_GET_VERSIONS = Defs.SCARD_CTL_CODE(2049);
        static final long IOCTL_READER_GET_MODE = Defs.SCARD_CTL_CODE(2560);
        static final long IOCTL_READER_SET_MODE = Defs.SCARD_CTL_CODE(2561);
        static final long IOCTL_READER_SWITCH_SPEED = Defs.SCARD_CTL_CODE(2052);
        static final long IOCTL_EMV_TIMER_MODE = Defs.SCARD_CTL_CODE(2053);
        static final long IOCTL_EMV_SINGLE_SHOT = Defs.SCARD_CTL_CODE(2054);
        static final long IOCTL_READER_GET_INFO = Defs.SCARD_CTL_CODE(2055);
        static final long IOCTL_SCARD_ESC_TRANSMIT = Defs.SCARD_CTL_CODE(2056);

        SmartCardOperations() {
        }
    }
}
