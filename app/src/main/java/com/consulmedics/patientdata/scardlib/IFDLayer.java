package com.consulmedics.patientdata.scardlib;

import java.nio.ByteBuffer;

class IFDLayer {
    int gnSlotNum = 0;
    static final int CCID_MAX_PKT_SIZE;

    IFDLayer() {
    }

    CCIDHandler.CCID_RESPONSE IFD_Process(IFD_COMMAND ifdCommand, IFD_PARAMS ifdParam, SmcLib.SMARTCARD_EXTENSION Smartcard, READER_EXTENSION Reader, ByteBuffer sendBuffer, long sendLength, long maxReceiveLength) {
        long status = 0L;
        char ccidCommand = 255;
        char rfu0 = 0;
        char rfu1 = 0;
        char rfu2 = 0;
        CCIDHandler.CCID_RESPONSE validResponse = null;
        ByteBuffer tlBuffer = ByteBuffer.allocate(65536);
        switch (ifdCommand) {
            case IFD_POWER:
                if (ifdParam.power.ResetType == 0L) {
                    ccidCommand = 'c';
                } else {
                    ccidCommand = 'b';
                }
                break;
            case IFD_TRANSMIT:
                ccidCommand = 'o';
                rfu0 = ifdParam.transmit.BWI;
                rfu1 = ifdParam.transmit.TransferLevel;
                break;
            case IFD_SETPROTOCOL:
                ccidCommand = 'a';
                sendBuffer = tlBuffer;
                tlBuffer.put(0, (byte)((int)(ifdParam.protocol.fVal << 4 | ifdParam.protocol.dVal)));
                if (ifdParam.protocol.NewProtocol == 1L) {
                    rfu0 = (char)IFDLayer.IFD_PROTOCOL.IFD_PROTOCOL_T0.getCode();
                    sendLength = 5L;
                    maxReceiveLength = 5L;
                } else {
                    rfu0 = (char)IFDLayer.IFD_PROTOCOL.IFD_PROTOCOL_T1.getCode();
                    sendLength = 7L;
                    maxReceiveLength = 7L;
                }
                break;
            case IFD_ESCAPE:
                ccidCommand = 'k';
                break;
            case IFD_SLOT_STATUS:
                ccidCommand = 'e';
                break;
            case IFD_ABORT:
                ccidCommand = 'r';
                break;
            default:
                status = -995L;
        }

        if (status >= 0L) {
            CCIDHandler ccidHandler = new CCIDHandler();
            byte[] Request = ccidHandler.Ccid_BuildRequest(ccidCommand, '\u0000', rfu0, rfu1, rfu2, sendBuffer, sendLength);
            SCard usb = new SCard();
            ByteBuffer ccidResponse = usb.UsbTransfer(Request);
            if (ccidResponse != null) {
                validResponse = ccidHandler.Ccid_VerifyResponse(ccidResponse.array(), (long)ccidResponse.capacity(), ccidCommand, (char)this.gnSlotNum, maxReceiveLength);
                if (validResponse.ntStatus == 0L) {
                    switch (validResponse.slotStatus) {
                        case 0:
                            if (Smartcard.ReaderCapabilities.CurrentState < 4L) {
                                Smartcard.ReaderCapabilities.CurrentState = 4L;
                            }
                            break;
                        case 1:
                            Smartcard.ReaderCapabilities.CurrentState = 2L;
                            break;
                        case 2:
                        default:
                            Smartcard.ReaderCapabilities.CurrentState = 1L;
                    }

                    validResponse.ntStatus = ccidHandler.Ccid_ToNtStatus(validResponse.ccidStatus);
                }
            }
        }

        return validResponse;
    }

    static {
        CCID_MAX_PKT_SIZE = SCard.nMaxCCIDLen;
    }

    class READER_EXTENSION {
        long handle;
        short fVal;
        short dVal;

        READER_EXTENSION() {
        }
    }

    static class IFD_PARAMS {
        Power power = new Power();
        Protocol protocol = new Protocol();
        Transmit transmit = new Transmit();

        IFD_PARAMS() {
        }

        class Transmit {
            char TransferLevel;
            char BWI;

            Transmit() {
            }
        }

        class Protocol {
            long NewProtocol;
            long fVal;
            long dVal;

            Protocol() {
            }
        }

        class Power {
            long ResetType;

            Power() {
            }
        }
    }

    static enum IFD_PROTOCOL {
        IFD_PROTOCOL_T0(0),
        IFD_PROTOCOL_T1(1);

        private int code;

        private IFD_PROTOCOL(int code) {
            this.code = code;
        }

        int getCode() {
            return this.code;
        }
    }

    static class IFD_TRANSFER_LEVEL {
        static final int IFD_TRANSFER_TPDU = 1;
        static final int IFD_TRANSFER_APDU = 2;
        static final int IFD_TRANSFER_XAPDU = 4;

        IFD_TRANSFER_LEVEL() {
        }
    }

    static enum IFD_COMMAND {
        IFD_POWER,
        IFD_TRANSMIT,
        IFD_SETPROTOCOL,
        IFD_ESCAPE,
        IFD_SLOT_STATUS,
        IFD_VERIFY,
        IFD_MODIFY,
        IFD_ABORT;

        private IFD_COMMAND() {
        }
    }

    static enum IFD_VENDOR_ESCAPE_CODES {
        VENDOR_READER_GETINFO(0),
        VENDOR_SLOT_SETMODE(1),
        VENDOR_SLOT_GETMODE(2),
        VENDOR_READER_UNDEFINED1(3),
        VENDOR_READER_SET_DBG_INFO(4),
        VENDOR_SCARD_EMV_LOOPBACK(5),
        VENDOR_SCARD_EMV_SINGLEMODE(6),
        VENDOR_SCARD_EMV_TIMEDMODE(7),
        VENDOR_SCARD_APDU_TRANSFER(8),
        VENDOR_READER_DFU_DETACH(9),
        VENDOR_READER_SWITCH_SPEED(10),
        VENDOR_READER_GETINFOEX(11),
        VENDOR_READER_SWITCH_PROTOCOL(12),
        VENDOR_READER_PRODUCTION_DFU_DETACH(13),
        VENDOR_RESERVED(14),
        VENDOR_READER_DISABLE_PPS(15),
        VENDOR_READER_EXCHANGE_RAW(16),
        VENDOR_RESERVED2(17),
        VENDOR_READER_GETIFDTYPE(18),
        VENDOR_READER_GETCHIPREV(19),
        VENDOR_READER_SETTRANSPORT(20),
        VENDOR_READER_SET_PINPARAM(128),
        VENDOR_BUZZER_DISABLE(81),
        VENDOR_BUZZER_ENABLE(82),
        VENDOR_BUZZER_TEST(83),
        VENDOR_LED_ENABLE(84),
        VENDOR_LED_DISABLE(85),
        VENDOR_BACKLITE_TEST(86),
        VENDOR_LCD_TEST(87),
        VENDOR_KEYPAD_TEST(88);

        private int code;

        private IFD_VENDOR_ESCAPE_CODES(int code) {
            this.code = code;
        }

        int getCode() {
            return this.code;
        }
    }

    class CCID_CHAIN_PARAM {
        static final int CCID_NO_CHAIN = 0;
        static final int CCID_CHAIN_BEGINS_AND_CONTINUES = 1;
        static final int CCID_CHAIN_ENDS = 2;
        static final int CCID_CHAIN_CONTINUES = 3;
        static final int CCID_CHAIN_ACK_BLOCK = 16;
        static final int CCID_CHAIN_EXPECTED = 4;

        CCID_CHAIN_PARAM() {
        }
    }
}
