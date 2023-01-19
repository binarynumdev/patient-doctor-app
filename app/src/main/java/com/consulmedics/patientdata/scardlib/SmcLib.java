package com.consulmedics.patientdata.scardlib;

import java.nio.ByteBuffer;

class SmcLib {
    static final int MAX_ATR_CODES = 10;
    static final int MIN_ATR_LEN = 2;
    static final int MAX_ATR_LEN = 33;
    static final int TS_BYTE_INVERSE = 63;
    static final int TS_BYTE_DIRECT = 59;
    static final int LOWER_NIBBLE = 15;
    static final int UPPER_NIBBLE = 240;
    static final long TR = 1000000L;
    static long[][] sBitRateAdjustment = new long[][]{{0L, 0L}, {1L, 1L}, {2L, 1L}, {4L, 1L}, {8L, 1L}, {16L, 1L}, {32L, 1L}, {64L, 1L}, {12L, 1L}, {20L, 1L}, {20L, 1L}, {20L, 1L}, {20L, 1L}, {20L, 1L}, {20L, 1L}};
    static long[][] sClockRateConversion = new long[][]{{372L, 4000000L}, {372L, 5000000L}, {558L, 6000000L}, {744L, 8000000L}, {1116L, 12000000L}, {1488L, 16000000L}, {1860L, 20000000L}, {372L, 4000000L}, {372L, 4000000L}, {512L, 5000000L}, {768L, 7500000L}, {1024L, 10000000L}, {1536L, 15000000L}, {2048L, 20000000L}, {372L, 4000000L}, {372L, 4000000L}};
    static final int SCARD_T1_EPILOGUE_LENGTH = 2;
    static final int SCARD_T1_MAX_IFS = 254;
    static final int MIN_BUFFER_SIZE = 288;
    static final int MAXIMUM_ATR_CODES = 4;
    static final int MAXIMUM_ATR_LENGTH = 33;
    static final int T1_INIT = 0;
    static final int T1_START = 1;
    static final int T1_I_BLOCK = 2;
    static final int T1_R_BLOCK = 3;
    static final int T1_RESTART = 4;
    static final int T1_RESYNCH_REQUEST = 192;
    static final int T1_RESYNCH_RESPONSE = 224;
    static final int T1_IFS_REQUEST = 193;
    static final int T1_IFS_RESPONSE = 225;
    static final int T1_ABORT_REQUEST = 194;
    static final int T1_ABORT_RESPONSE = 226;
    static final int T1_WTX_REQUEST = 195;
    static final int T1_WTX_RESPONSE = 227;
    static final int T1_VPP_ERROR = 228;
    static final int T1_IFSD = 254;
    static final int T1_IFSD_DEFAULT = 32;
    static final int T1_MAX_RETRIES = 2;
    static final int T1_MORE_DATA = 32;
    static final int T1_ERROR_CHKSUM = 1;
    static final int T1_ERROR_OTHER = 2;
    static final int T1_CWI_DEFAULT = 13;
    static final int T1_BWI_DEFAULT = 4;
    static final int SCARD_T1_PROLOGUE_LENGTH = 3;
    static final int T1_CRC_CHECK = 1;

    SmcLib() {
    }

    static void SmartcardInvertData(ByteBuffer Buffer, long Length, int tempI) {
        for(long i = (long)tempI; i < Length; ++i) {
            char inv = 0;

            for(char j = 0; j < '\b'; ++j) {
                if ((Buffer.get((int)i) & 1 << j) != 0) {
                    inv = (char)(inv | 1 << 7 - j);
                }
            }

            Buffer.put((int)i, (byte)(inv ^ 255));
        }

    }

    static boolean SmartcardT1Chksum(ByteBuffer Block, char Edc, boolean Verify, int tempI) {
        boolean fRet = true;
        short crc = 0;
        Short offset = (short)((Block.get(2) & 255) + 3);
        short[] crc16a = new short[]{0, -16191, -15999, 320, -15615, 960, 640, -15807, -14847, 1728, 1920, -14527, 1280, -14911, -15231, 1088};
        short[] crc16b = new short[]{0, -13311, -10239, 5120, -4095, 15360, 10240, -7167, -24575, 27648, 30720, -19455, 20480, -25599, -30719, 17920};
        short i;
        if ((Edc & 1) != 0) {
            for(i = (short)tempI; i < offset; ++i) {
                char tmp = (char)(Block.get(i) ^ (char)crc);
                crc = (short)(crc >> 8 ^ crc16a[tmp & 15] ^ crc16b[tmp >> 4]);
            }

            if (Verify) {
                if (crc == (Block.get(offset + 1) | Block.get(offset) << 8)) {
                    return true;
                }

                return false;
            }

            Block.put(offset, (byte)(crc >> 8));
            Block.put(offset + 1, (byte)(crc & 255));
        } else {
            short lrc = (short)Block.get(0);

            for(i = 1; i < offset; ++i) {
                lrc = (short)(lrc ^ Block.get(i));
            }

            if (Verify) {
                return lrc == Block.get(offset);
            }

            Block.put(offset, (byte)lrc);
        }

        return fRet;
    }

    long SmartcardInitialize(SMARTCARD_EXTENSION Smartcard) {
        long status = 0L;
        if (Smartcard == null) {
            return -995L;
        } else {
            if (Smartcard.SmartcardRequest.BufferSize < 288L) {
                Smartcard.SmartcardRequest.BufferSize = 288L;
            }

            if (Smartcard.SmartcardReply.BufferSize < 288L) {
                Smartcard.SmartcardReply.BufferSize = 288L;
            }

            Smartcard.SmartcardRequest.Buffer = ByteBuffer.allocate(288);
            Smartcard.SmartcardReply.Buffer = ByteBuffer.allocate(288);
            if (Smartcard.SmartcardRequest.Buffer == null || Smartcard.SmartcardReply.Buffer == null) {
                status = -997L;
            }

            if (status != 0L) {
                return status;
            } else {
                Smartcard.T1.IFSD = (char)((int)Smartcard.ReaderCapabilities.MaxIFSD);
                return status;
            }
        }
    }

    void SmartcardExit(SMARTCARD_EXTENSION Smartcard) {
        if (Smartcard.SmartcardRequest.Buffer != null) {
            Smartcard.SmartcardRequest.Buffer = null;
        }

        if (Smartcard.SmartcardReply.Buffer != null) {
            Smartcard.SmartcardReply.Buffer = null;
        }

        if (Smartcard.T1.ReplyData != null) {
            Smartcard.T1.ReplyData = null;
        }

    }

    long SmartcardT1Request(SMARTCARD_EXTENSION Smartcard) {
        SMARTCARD_REQUEST smartcardRequest = Smartcard.SmartcardRequest;
        T1_BLOCK_FRAME t1SendFrame = new T1_BLOCK_FRAME();
        if (Smartcard.T1.WaitForReply) {
            Smartcard.T1.State = 0L;
        }

        Smartcard.T1.WaitForReply = true;
        switch ((int)Smartcard.T1.State) {
            case 0:
                Smartcard.T1.State = 193L;
                Smartcard.AwaitingFirstResponse = true;
            case 1:
                Smartcard.T1.Resynch = 0;
                Smartcard.CardStartedChaining = false;
                Smartcard.InvalidRetryCount = 0;
                Smartcard.ResynchRetryCount = 0;
                if (Smartcard.T1.ReplyData != null) {
                    Smartcard.T1.ReplyData = null;
                }

                if (Smartcard.iorequest.ReplyBufferLength < 2L) {
                    return -992L;
                }

                Smartcard.T1.ReplyData = ByteBuffer.allocate(131072);
                if (Smartcard.T1.ReplyData == null) {
                    return -997L;
                }
            case 4:
                Smartcard.T1.BytesToSend = Smartcard.iorequest.RequestBufferLength;
                Smartcard.T1.BytesSent = 0L;
                Smartcard.T1.BytesReceived = 0L;
                Smartcard.T1.IFSC = Smartcard.CardCapabilities.t1.IFSC;
                Smartcard.T1.Resend = 0;
                Smartcard.T1.OriginalState = 0L;
                Smartcard.T1.MoreData = false;
            case 193:
                if (Smartcard.T1.State == 193L) {
                    Smartcard.T1.State = 193L;
                    t1SendFrame.Nad = (byte)Smartcard.T1.NAD;
                    t1SendFrame.Pcb = -63;
                    t1SendFrame.Len = 1;
                    t1SendFrame.Inf.put(0, (byte)Smartcard.T1.IFSD);
                    Smartcard.AwaitingFirstResponse = true;
                    break;
                } else {
                    Smartcard.T1.State = 2L;
                }
            case 2:
                Smartcard.T1.State = 2L;
                Smartcard.T1.InfBytesSent = Smartcard.T1.IFSC;
                if (Smartcard.T1.InfBytesSent > Smartcard.T1.IFSD) {
                    Smartcard.T1.InfBytesSent = Smartcard.T1.IFSD;
                }

                if (Smartcard.T1.BytesToSend > (long)Smartcard.T1.InfBytesSent) {
                    Smartcard.T1.MoreData = true;
                    t1SendFrame.Len = (byte)Smartcard.T1.InfBytesSent;
                } else {
                    Smartcard.T1.MoreData = false;
                    t1SendFrame.Len = (byte)((int)Smartcard.T1.BytesToSend);
                }

                t1SendFrame.Nad = (byte)Smartcard.T1.NAD;
                t1SendFrame.Pcb = (byte)(Smartcard.T1.SSN << 6 | (Smartcard.T1.MoreData ? 32 : 0));
                System.arraycopy(Smartcard.iorequest.RequestBuffer.array(), (int)Smartcard.T1.BytesSent, t1SendFrame.Inf.array(), 0, t1SendFrame.Len & 255);
                break;
            case 3:
                t1SendFrame.Nad = (byte)Smartcard.T1.NAD;
                t1SendFrame.Pcb = (byte)(128 | Smartcard.T1.RSN << 4 | Smartcard.T1.LastError);
                if (Smartcard.T1.LastError != 0) {
                    Smartcard.T1.LastError = 0;
                    if (Smartcard.T1.OriginalState == 0L) {
                        Smartcard.T1.State = 1L;
                        return -996L;
                    }

                    Smartcard.T1.State = Smartcard.T1.OriginalState;
                }

                t1SendFrame.Len = 0;
                t1SendFrame.Inf = null;
                break;
            case 192:
                t1SendFrame.Nad = (byte)Smartcard.T1.NAD;
                t1SendFrame.Pcb = -64;
                t1SendFrame.Len = 0;
                t1SendFrame.Inf = null;
                Smartcard.T1.SSN = 0;
                break;
            case 194:
                t1SendFrame.Nad = (byte)Smartcard.T1.NAD;
                t1SendFrame.Pcb = -62;
                t1SendFrame.Len = 0;
                t1SendFrame.Inf = null;
                break;
            case 225:
                if (Smartcard.T1.OriginalState == 0L) {
                    Smartcard.T1.State = Smartcard.T1.OriginalState;
                }

                t1SendFrame.Nad = (byte)Smartcard.T1.NAD;
                t1SendFrame.Pcb = -31;
                t1SendFrame.Len = 1;
                t1SendFrame.Inf.putInt(Smartcard.T1.IFSC);
                break;
            case 226:
                Smartcard.T1.State = 1L;
                t1SendFrame.Nad = (byte)Smartcard.T1.NAD;
                t1SendFrame.Pcb = -30;
                t1SendFrame.Len = 0;
                t1SendFrame.Inf = null;
                break;
            case 227:
                Smartcard.T1.State = Smartcard.T1.OriginalState;
                t1SendFrame.Nad = (byte)Smartcard.T1.NAD;
                t1SendFrame.Pcb = -29;
                t1SendFrame.Len = 1;
                t1SendFrame.Inf.putInt(Smartcard.T1.Wtx);
        }

        smartcardRequest.Buffer.put((int)smartcardRequest.BufferLength, t1SendFrame.Nad);
        smartcardRequest.Buffer.put((int)smartcardRequest.BufferLength + 1, t1SendFrame.Pcb);
        smartcardRequest.Buffer.put((int)smartcardRequest.BufferLength + 2, t1SendFrame.Len);
        if ((t1SendFrame.Len & 255) > 0) {
            System.arraycopy(t1SendFrame.Inf.array(), 0, smartcardRequest.Buffer.array(), (int)(smartcardRequest.BufferLength + 3L), t1SendFrame.Len & 255);
        }

        SmartcardT1Chksum(smartcardRequest.Buffer, Smartcard.CardCapabilities.t1.EDC, false, (int)smartcardRequest.BufferLength);
        if (Smartcard.CardCapabilities.InversConvention) {
            SmartcardInvertData(smartcardRequest.Buffer, (long)(((Smartcard.CardCapabilities.t1.EDC & 1) != 0 ? 5 : 4) + t1SendFrame.Len), (int)smartcardRequest.BufferLength);
        }

        smartcardRequest.BufferLength += (long)(((Smartcard.CardCapabilities.t1.EDC & 1) != 0 ? 5 : 4) + (t1SendFrame.Len & 255));
        return 0L;
    }

    long SmartcardT1Reply(SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION ReaderExtn) {
        T1_BLOCK_FRAME t1RecFrame = new T1_BLOCK_FRAME();
        long ntStatus = -994L;
        boolean packetOk = true;
        boolean chksumOk = true;
        IFDLayerInterface ifdLayerInterface = new IFDLayerInterface();
        Smartcard.T1.WaitForReply = false;
        if (Smartcard.CardCapabilities.InversConvention) {
            SmartcardInvertData(Smartcard.SmartcardReply.Buffer, Smartcard.SmartcardReply.BufferLength, 0);
        }

        Smartcard.T1.Wtx = 0;
        long expectedLength = (long)(3 + (Smartcard.SmartcardReply.Buffer.get(2) & 255) + ((Smartcard.CardCapabilities.t1.EDC & 1) != 0 ? 2 : 1));
        if (Smartcard.SmartcardReply.BufferLength >= 4L && Smartcard.SmartcardReply.BufferLength == expectedLength) {
            chksumOk = SmartcardT1Chksum(Smartcard.SmartcardReply.Buffer, Smartcard.CardCapabilities.t1.EDC, true, 0);
        } else {
            packetOk = false;
        }

        T1_DATA var10000;
        char var10002;
        if (packetOk && chksumOk) {
            Smartcard.InvalidRetryCount = 0;
            Smartcard.T1.LastError = 0;
            t1RecFrame.Nad = Smartcard.SmartcardReply.Buffer.get(0);
            t1RecFrame.Pcb = Smartcard.SmartcardReply.Buffer.get(1);
            t1RecFrame.Len = Smartcard.SmartcardReply.Buffer.get(2);
            System.arraycopy(Smartcard.SmartcardReply.Buffer.array(), 3, t1RecFrame.Inf.array(), 0, t1RecFrame.Len & 255);
            if (Smartcard.T1.State == 193L) {
                if ((t1RecFrame.Pcb & 255) == 225) {
                    Smartcard.T1.IFSC = (char)t1RecFrame.Inf.get(0);
                    Smartcard.T1.State = 2L;
                } else if ((t1RecFrame.Pcb & 130) == 130) {
                    Smartcard.T1.State = 2L;
                } else {
                    var10000 = Smartcard.T1;
                    var10002 = var10000.Resend;
                    var10000.Resend = (char)(var10002 + 1);
                    if (var10002 == 2) {
                        Smartcard.T1.Resend = 0;
                        Smartcard.T1.State = 192L;
                    } else {
                        Smartcard.T1.State = 3L;
                        ntStatus = -998L;
                    }
                }
            } else if (Smartcard.T1.State == 192L) {
                label131: {
                    if (t1RecFrame.Pcb != 224) {
                        byte var14 = Smartcard.ResynchRetryCount;
                        Smartcard.ResynchRetryCount = (byte)(var14 + 1);
                        if (var14 >= 2) {
                            Smartcard.ResynchRetryCount = 0;
                            Smartcard.MinorIoControlCode = 0L;
                            if (ifdLayerInterface.IFD_PowerControl(Smartcard, ReaderExtn) == 0L) {
                                ntStatus = -998L;
                            } else {
                                ntStatus = -997L;
                            }
                            break label131;
                        }
                    }

                    if (t1RecFrame.Pcb != 224) {
                        Smartcard.T1.State = 192L;
                    } else {
                        Smartcard.T1.Resend = 0;
                        Smartcard.T1.RSN = 0;
                        Smartcard.T1.SSN = 0;
                        Smartcard.AwaitingFirstResponse = false;
                        Smartcard.T1.State = 4L;
                        ntStatus = -998L;
                    }
                }
            } else if ((t1RecFrame.Pcb & 128) == 0) {
                if ((t1RecFrame.Pcb & 64) >> 6 == Smartcard.T1.RSN) {
                    Smartcard.AwaitingFirstResponse = false;
                    Smartcard.T1.Resend = 0;
                    Smartcard.T1.OriginalState = 0L;
                    var10000 = Smartcard.T1;
                    var10000.RSN = (char)(var10000.RSN ^ 1);
                    long minBufferSize = Smartcard.T1.BytesReceived + (long)(t1RecFrame.Len & 255);
                    System.arraycopy(t1RecFrame.Inf.array(), 0, Smartcard.T1.ReplyData.array(), (int)Smartcard.T1.BytesReceived, t1RecFrame.Len & 255);
                    var10000 = Smartcard.T1;
                    var10000.BytesReceived += (long)(t1RecFrame.Len & 255);
                    if ((t1RecFrame.Pcb & 32) != 0) {
                        Smartcard.T1.State = 3L;
                        Smartcard.CardStartedChaining = true;
                    } else {
                        var10000 = Smartcard.T1;
                        var10000.SSN = (char)(var10000.SSN ^ 1);
                        Smartcard.CardStartedChaining = false;
                        if (Smartcard.iorequest.ReplyBufferLength < minBufferSize) {
                            ntStatus = -992L;
                        } else {
                            Smartcard.iorequest.Information = Smartcard.T1.BytesReceived;
                            System.arraycopy(Smartcard.T1.ReplyData.array(), 0, Smartcard.iorequest.ReplyBuffer.array(), 0, (int)Smartcard.T1.BytesReceived);
                            ntStatus = 0L;
                        }
                    }
                } else {
                    Smartcard.T1.LastError = 2;
                    if (Smartcard.T1.OriginalState == 0L) {
                        Smartcard.T1.OriginalState = Smartcard.T1.State;
                    }

                    var10000 = Smartcard.T1;
                    var10002 = var10000.Resend;
                    var10000.Resend = (char)(var10002 + 1);
                    if (var10002 == 2) {
                        Smartcard.T1.Resend = 0;
                        Smartcard.T1.State = 192L;
                    } else if (192L != (192L & Smartcard.T1.State) || 0L != (32L & Smartcard.T1.State)) {
                        Smartcard.T1.State = 3L;
                    }
                }
            } else if ((t1RecFrame.Pcb & 192) == 128) {
                char RSN = (char)((t1RecFrame.Pcb & 16) >> 4);
                if (RSN != Smartcard.T1.SSN && Smartcard.T1.MoreData) {
                    Smartcard.T1.Resend = 0;
                    Smartcard.AwaitingFirstResponse = false;
                    var10000 = Smartcard.T1;
                    var10000.BytesSent += (long)Smartcard.T1.InfBytesSent;
                    var10000 = Smartcard.T1;
                    var10000.BytesToSend -= (long)Smartcard.T1.InfBytesSent;
                    var10000 = Smartcard.T1;
                    var10000.SSN = (char)(var10000.SSN ^ 1);
                    Smartcard.T1.State = 2L;
                } else {
                    label184: {
                        if (RSN == Smartcard.T1.SSN && 2L == Smartcard.T1.State) {
                            Smartcard.AwaitingFirstResponse = false;
                            var10000 = Smartcard.T1;
                            var10002 = var10000.Resend;
                            var10000.Resend = (char)(var10002 + 1);
                            if (var10002 < 2) {
                                Smartcard.T1.State = 2L;
                                break label184;
                            }
                        }

                        var10000 = Smartcard.T1;
                        var10002 = var10000.Resend;
                        var10000.Resend = (char)(var10002 + 1);
                        if (var10002 >= 2) {
                            Smartcard.T1.Resend = 0;
                            if (Smartcard.T1.OriginalState == 0L) {
                                Smartcard.T1.OriginalState = Smartcard.T1.State;
                            }

                            if (Smartcard.AwaitingFirstResponse) {
                                Smartcard.MinorIoControlCode = 0L;
                                if (ifdLayerInterface.IFD_PowerControl(Smartcard, ReaderExtn) == 0L) {
                                    ntStatus = -998L;
                                } else {
                                    ntStatus = -997L;
                                }
                            } else {
                                Smartcard.T1.State = 192L;
                            }
                        } else if (192L != (192L & Smartcard.T1.State) || 0L != (32L & Smartcard.T1.State)) {
                            Smartcard.T1.State = 3L;
                        }
                    }
                }
            } else {
                Smartcard.AwaitingFirstResponse = false;
                switch (t1RecFrame.Pcb) {
                    case -63:
                        Smartcard.T1.IFSC = (char)Smartcard.SmartcardReply.Buffer.get(3);
                        Smartcard.T1.OriginalState = Smartcard.T1.State;
                        Smartcard.T1.State = 225L;
                        break;
                    case -62:
                        Smartcard.T1.State = 226L;
                        break;
                    case -61:
                        Smartcard.T1.Wtx = (char)Smartcard.SmartcardReply.Buffer.get(3);
                        Smartcard.T1.OriginalState = Smartcard.T1.State;
                        Smartcard.T1.State = 227L;
                        break;
                    case -28:
                        ntStatus = -999L;
                        break;
                    default:
                        ntStatus = -998L;
                }
            }
        } else {
            Smartcard.T1.LastError = (char)(chksumOk ? 2 : 1);
            if (Smartcard.T1.OriginalState == 0L) {
                Smartcard.T1.OriginalState = Smartcard.T1.State;
            }

            var10002 = (char)Smartcard.InvalidRetryCount;
            Smartcard.InvalidRetryCount = (byte)(var10002 + 1);
            if (var10002 >= 2) {
                Smartcard.InvalidRetryCount = 0;
                if (Smartcard.AwaitingFirstResponse) {
                    Smartcard.MinorIoControlCode = 0L;
                    if (ifdLayerInterface.IFD_PowerControl(Smartcard, ReaderExtn) == 0L) {
                        ntStatus = -998L;
                    } else {
                        ntStatus = -997L;
                    }
                } else {
                    Smartcard.T1.State = 192L;
                }
            } else if (192L != (192L & Smartcard.T1.State) || 0L != (32L & Smartcard.T1.State)) {
                Smartcard.T1.State = 3L;
            }
        }

        if (Smartcard.T1.State == 192L) {
            var10000 = Smartcard.T1;
            var10002 = var10000.Resynch;
            var10000.Resynch = (char)(var10002 + 1);
            if (var10002 == 2) {
                ntStatus = -998L;
            }
        }

        if (ntStatus != -994L) {
            if (Smartcard.T1.OriginalState == 193L) {
                Smartcard.T1.State = 193L;
            } else {
                Smartcard.T1.State = 1L;
            }

            if (Smartcard.T1.ReplyData != null) {
                Smartcard.T1.ReplyData = null;
            }

            Smartcard.T1.OriginalState = 0L;
            Smartcard.T1.NAD = 0;
        }

        return ntStatus;
    }

    long SmartcardT0Request(SMARTCARD_EXTENSION Smartcard) {
        SMARTCARD_REQUEST pSmartcardRequest = Smartcard.SmartcardRequest;
        long ulIoRequestDataLength = 0L;
        long ntStatus = 0L;
        if (pSmartcardRequest.BufferLength + Smartcard.iorequest.RequestBufferLength >= pSmartcardRequest.BufferSize) {
            ntStatus = -1000L;
        } else {
            ulIoRequestDataLength = Smartcard.iorequest.RequestBufferLength;
            System.arraycopy(Smartcard.iorequest.RequestBuffer.array(), 0, pSmartcardRequest.Buffer.array(), (int)pSmartcardRequest.BufferLength, (int)ulIoRequestDataLength);
            pSmartcardRequest.BufferLength += ulIoRequestDataLength;
            if (ulIoRequestDataLength < 4L) {
                ntStatus = -995L;
            } else {
                ByteBuffer pRequestBuffer = pSmartcardRequest.Buffer;
                if (ulIoRequestDataLength <= 5L) {
                    Smartcard.T0.Lc = 0L;
                    if (4L == ulIoRequestDataLength) {
                        Smartcard.T0.Le = 0L;
                        pSmartcardRequest.Buffer.put(4, (byte)0);
                        ++pSmartcardRequest.BufferLength;
                    } else {
                        Smartcard.T0.Le = (long)(pRequestBuffer.get(4) != 0 ? pRequestBuffer.get(4) : 256);
                    }
                } else {
                    Smartcard.T0.Lc = (long)pRequestBuffer.get(4);
                    Smartcard.T0.Le = 0L;
                    if ((Smartcard.T0.Lc & 255L) != ulIoRequestDataLength - 5L) {
                        if (Smartcard.T0.Lc + 1L != ulIoRequestDataLength - 5L) {
                            ntStatus = -995L;
                            return ntStatus;
                        }

                        Smartcard.T0.Le = (long)pRequestBuffer.get((int)(ulIoRequestDataLength - 1L));
                    }
                }

                if (Smartcard.CardCapabilities.InversConvention) {
                    SmartcardInvertData(pSmartcardRequest.Buffer, pSmartcardRequest.BufferLength, 0);
                }
            }
        }

        return ntStatus;
    }

    long SmartcardT0Reply(SMARTCARD_EXTENSION Smartcard) {
        SMARTCARD_REPLY pSmartcardReply = Smartcard.SmartcardReply;
        long ntStatus = 0L;
        if (Smartcard.iorequest.ReplyBufferLength < pSmartcardReply.BufferLength) {
            ntStatus = -992L;
        } else {
            if (Smartcard.CardCapabilities.InversConvention) {
                SmartcardInvertData(pSmartcardReply.Buffer, pSmartcardReply.BufferLength, 0);
            }

            System.arraycopy(pSmartcardReply.Buffer.array(), 0, Smartcard.iorequest.ReplyBuffer.array(), 0, (int)pSmartcardReply.BufferLength);
            Smartcard.iorequest.Information = pSmartcardReply.BufferLength;
        }

        return ntStatus;
    }

    long SmartcardUpdateCardCapabilities(SMARTCARD_EXTENSION Smartcard) {
        long ntStatus = 0L;
        SCARD_CARD_CAPABILITIES CardCapabilities = null;
        SCARD_READER_CAPABILITIES ReaderCapabilities = null;
        int chTck = 0;
        byte byNumOfProtocols = 0;
        byte byProtocolTypes = 0;
        long dwFs = 0L;
        boolean bTA2Present = false;
        ReaderCapabilities = Smartcard.ReaderCapabilities;
        CardCapabilities = Smartcard.CardCapabilities;
        int[] anATRString = new int[CardCapabilities.atr.Length];
        byte byATRLength = (byte)CardCapabilities.atr.Length;
        if (byATRLength >= 2 && (long)byATRLength <= 33L) {
            int i;
            for(i = 0; i < byATRLength; ++i) {
                anATRString[i] = CardCapabilities.atr.Buffer[i] & 255;
            }

            for(i = 1; i < byATRLength; ++i) {
                chTck ^= anATRString[i];
            }

            if (63 != anATRString[0] && 59 != anATRString[0]) {
                ntStatus = -993L;
            } else {
                CardCapabilities.protocol.Supported = 0L;
                int[] achTA = new int[4];
                int[] achTB = new int[4];
                int[] achTC = new int[4];
                int[] achTD = new int[4];
                achTA[0] = 17;
                achTB[0] = 37;
                achTC[1] = 10;
                int chTDi = anATRString[1];
                CardCapabilities.chars.Length = chTDi & 15;
                int pointer = 1;

                for(int pn = 0; pointer < byATRLength; ++pn) {
                    if ((chTDi | 239) == 255) {
                        ++pointer;
                        achTA[pn] = anATRString[pointer];
                        if (1 == pn) {
                            bTA2Present = true;
                        }
                    }

                    if ((chTDi | 223) == 255) {
                        ++pointer;
                        achTB[pn] = anATRString[pointer];
                    }

                    if ((chTDi | 191) == 255) {
                        ++pointer;
                        achTC[pn] = anATRString[pointer];
                    }

                    if ((chTDi | 127) != 255) {
                        break;
                    }

                    ++pointer;
                    chTDi = anATRString[pointer];
                    achTD[pn] = chTDi & 15;
                    if (0 == (1 << achTD[pn] & byProtocolTypes)) {
                        ++byNumOfProtocols;
                    }

                    byProtocolTypes = (byte)(byProtocolTypes | 1 << achTD[pn]);
                }

                if ((byNumOfProtocols > 1 || ((long)byProtocolTypes & -2L) != 0L) && 0 != chTck) {
                    ntStatus = -993L;
                } else {
                    CardCapabilities.Fl = (short)((achTA[0] & 240) >> 4);
                    CardCapabilities.Dl = (short)(achTA[0] & 15);
                    CardCapabilities.II = (short)((achTB[1] & 192) >> 6);
                    CardCapabilities.P = (short)(achTB[2] != 0 ? achTB[2] : (achTB[0] & 31) * 10);
                    CardCapabilities.N = (short)achTC[0];
                    if (0L != sBitRateAdjustment[CardCapabilities.Dl][0] && 0L != sClockRateConversion[CardCapabilities.Fl][0]) {
                        if (0 == CardCapabilities.PtsData.Type) {
                            dwFs = ReaderCapabilities.frequency.Default * 1000L;
                            if (0L == dwFs) {
                                dwFs = 3571200L;
                            }

                            CardCapabilities.PtsData.Fl = 1;
                            CardCapabilities.PtsData.Dl = 1;
                            CardCapabilities.PtsData.DataRate = ReaderCapabilities.dataRate.Default;
                            CardCapabilities.PtsData.CLKFrequency = ReaderCapabilities.frequency.Default;
                            CardCapabilities.etu = 1L + 1000000L * sBitRateAdjustment[CardCapabilities.PtsData.Dl][1] * sClockRateConversion[CardCapabilities.PtsData.Fl][0] / (sBitRateAdjustment[CardCapabilities.PtsData.Dl][0] * dwFs);
                        }

                        if (0 != CardCapabilities.PtsData.Type) {
                            label350: {
                                if (1 == CardCapabilities.PtsData.Type) {
                                    CardCapabilities.PtsData.Fl = CardCapabilities.Fl;
                                }

                                if (CardCapabilities.PtsData.Fl <= 15 && 0L != sClockRateConversion[CardCapabilities.PtsData.Fl][0]) {
                                    while(true) {
                                        long ulCardFreq = 0L;
                                        if (0 == ReaderCapabilities.frequenciesSupported.Entries || null == ReaderCapabilities.frequenciesSupported.List) {
                                            ReaderCapabilities.frequenciesSupported.List[0] = ReaderCapabilities.frequency.Default;
                                            ReaderCapabilities.frequenciesSupported.List[1] = ReaderCapabilities.frequency.Max;
                                            ReaderCapabilities.frequenciesSupported.Entries = 2;
                                        }

                                        ulCardFreq = sClockRateConversion[CardCapabilities.PtsData.Fl][1] / 1000L;
                                        CardCapabilities.PtsData.CLKFrequency = ReaderCapabilities.frequenciesSupported.List[0];

                                        for(i = 0; i < ReaderCapabilities.frequenciesSupported.Entries; ++i) {
                                            if (ReaderCapabilities.frequenciesSupported.List[i] > CardCapabilities.PtsData.CLKFrequency && ReaderCapabilities.frequenciesSupported.List[i] <= ulCardFreq) {
                                                CardCapabilities.PtsData.CLKFrequency = ReaderCapabilities.frequenciesSupported.List[i];
                                            }
                                        }

                                        dwFs = CardCapabilities.PtsData.CLKFrequency * 1000L;
                                        CardCapabilities.PtsData.DataRate = 0L;
                                        if (0L == dwFs) {
                                            ntStatus = -995L;
                                            break label350;
                                        }

                                        if (1 == CardCapabilities.PtsData.Type) {
                                            CardCapabilities.PtsData.Dl = CardCapabilities.Dl;
                                        }

                                        if (CardCapabilities.PtsData.Dl > 15 || 0L == sBitRateAdjustment[CardCapabilities.PtsData.Dl][0]) {
                                            ntStatus = -995L;
                                            break label350;
                                        }

                                        if (0 == ReaderCapabilities.dataRatesSupported.Entries || null == ReaderCapabilities.dataRatesSupported.List) {
                                            ReaderCapabilities.dataRatesSupported.List[0] = ReaderCapabilities.dataRate.Default;
                                            ReaderCapabilities.dataRatesSupported.Entries = 2;
                                        }

                                        while(CardCapabilities.PtsData.Dl > 1) {
                                            long ulDataRate = 0L;
                                            ulDataRate = sBitRateAdjustment[CardCapabilities.PtsData.Dl][0] * dwFs / (sBitRateAdjustment[CardCapabilities.PtsData.Dl][1] * sClockRateConversion[CardCapabilities.PtsData.Fl][0]);

                                            for(i = 0; i < ReaderCapabilities.dataRatesSupported.Entries; ++i) {
                                                if (ReaderCapabilities.dataRatesSupported.List[i] * 101L > ulDataRate * 100L && ReaderCapabilities.dataRatesSupported.List[i] * 99L < ulDataRate * 100L) {
                                                    CardCapabilities.PtsData.DataRate = ReaderCapabilities.dataRatesSupported.List[0];
                                                    break;
                                                }
                                            }

                                            if (CardCapabilities.PtsData.DataRate != 0L) {
                                                break;
                                            }

                                            while(0L == sBitRateAdjustment[--CardCapabilities.PtsData.Dl][0]) {
                                            }
                                        }

                                        if (1 == CardCapabilities.PtsData.Fl && 1 == CardCapabilities.PtsData.Dl) {
                                            CardCapabilities.PtsData.DataRate = ReaderCapabilities.dataRate.Default;
                                            CardCapabilities.PtsData.CLKFrequency = ReaderCapabilities.frequency.Default;
                                            break label350;
                                        }

                                        if (CardCapabilities.PtsData.DataRate != 0L) {
                                            break label350;
                                        }

                                        while(CardCapabilities.PtsData.Fl > 0 && 0L == sClockRateConversion[--CardCapabilities.PtsData.Fl][0]) {
                                        }

                                        if (0L != CardCapabilities.PtsData.DataRate) {
                                            break label350;
                                        }
                                    }
                                }

                                ntStatus = -995L;
                                return ntStatus;
                            }
                        }

                        CardCapabilities.etu = 1L + 1000000L * sBitRateAdjustment[CardCapabilities.PtsData.Dl][1] * sClockRateConversion[CardCapabilities.PtsData.Fl][0] / (sBitRateAdjustment[CardCapabilities.PtsData.Dl][0] * dwFs);
                        CardCapabilities.GT = 0L;
                        if (0 == CardCapabilities.N) {
                            CardCapabilities.PtsData.StopBits = 2;
                        } else if (255 == CardCapabilities.N) {
                            CardCapabilities.PtsData.StopBits = 1;
                        } else {
                            CardCapabilities.GT = (long)CardCapabilities.N * CardCapabilities.etu;
                        }

                        if (!bTA2Present && (byNumOfProtocols > 1 || 1 != CardCapabilities.Fl || 1 != CardCapabilities.Dl)) {
                            ReaderCapabilities.CurrentState = 5L;
                        } else {
                            ReaderCapabilities.CurrentState = 6L;
                        }

                        SCARD_CARD_CAPABILITIES.Protocol var10000;
                        if (0 == achTD[0]) {
                            var10000 = CardCapabilities.protocol;
                            var10000.Supported |= 1L;
                            CardCapabilities.t0.WI = (char)achTC[1];
                            if (CardCapabilities.PtsData.Dl > 0 && CardCapabilities.PtsData.Dl < 6) {
                                CardCapabilities.t0.WT = 1L + (long)(CardCapabilities.t0.WI * 960) * CardCapabilities.etu * ((long)(CardCapabilities.PtsData.Dl - 1) << 2);
                            } else {
                                CardCapabilities.t0.WT = 1L + (long)(CardCapabilities.t0.WI * 960) * CardCapabilities.etu / (long)((char)(CardCapabilities.PtsData.Dl - 1) << 2);
                            }
                        }

                        if (((long)byProtocolTypes & 2L) != 0L) {
                            for(i = 0; i < 4 && achTD[i] != 1; ++i) {
                            }

                            while(i < 4 && achTD[i] == 1) {
                                ++i;
                            }

                            if (4 == achTD[i]) {
                                ntStatus = -993L;
                                return ntStatus;
                            }

                            var10000 = CardCapabilities.protocol;
                            var10000.Supported |= 2L;
                            CardCapabilities.t1.IFSC = (char)(achTA[i] != 0 ? achTA[i] : 32);
                            CardCapabilities.t1.CWI = (char)((achTB[i] & 15) != 0 ? achTB[i] & 15 : 13);
                            CardCapabilities.t1.BWI = (char)((achTB[i] & 240) >> 4 != 0 ? (achTB[i] & 240) >> 4 : 4);
                            CardCapabilities.t1.EDC = (char)(achTC[i] & 1);
                            CardCapabilities.t1.CWT = 1L + (long)((CardCapabilities.t1.CWI << 2) + 11) * CardCapabilities.etu;
                            CardCapabilities.t1.BWT = 1L + (long)(CardCapabilities.t1.BWI << 2) * 1000000L / 10L + 11L * CardCapabilities.etu;
                        }

                        if (6L == ReaderCapabilities.CurrentState) {
                            if (bTA2Present) {
                                CardCapabilities.protocol.Selected = (long)(1 << achTA[1]);
                            } else {
                                CardCapabilities.protocol.Selected = CardCapabilities.protocol.Supported;
                            }
                        }

                        var10000 = CardCapabilities.protocol;
                        var10000.Supported |= 65536L;
                    } else {
                        ntStatus = -993L;
                    }
                }
            }
        } else {
            ntStatus = -993L;
        }

        return ntStatus;
    }

    void SmartcardInitializeCardCapabilities(SMARTCARD_EXTENSION Smartcard) {
        CLOCK_RATE_CONVERSION HoldClockRateConversion = Smartcard.CardCapabilities.ClockRateConversion;
        BIT_RATE_ADJUSTMENT HoldBitRateAdjustment = Smartcard.CardCapabilities.BitRateAdjustment;
        Smartcard.CardCapabilities = new SCARD_CARD_CAPABILITIES();
        Smartcard.CardCapabilities.ClockRateConversion = HoldClockRateConversion;
        Smartcard.CardCapabilities.BitRateAdjustment = HoldBitRateAdjustment;
        Smartcard.CardCapabilities.protocol.Supported = 65536L;
        Smartcard.T1.State = 0L;
        Smartcard.T1.SSN = 0;
        Smartcard.T1.RSN = 0;
        if (Smartcard.ReaderCapabilities.MaxIFSD != 0L && Smartcard.ReaderCapabilities.MaxIFSD < 254L) {
            Smartcard.T1.IFSD = (char)((int)Smartcard.ReaderCapabilities.MaxIFSD);
        } else {
            Smartcard.T1.IFSD = 254;
        }

    }

    long SmartcardAPDURequest(SMARTCARD_EXTENSION Smartcard, IFDLayer.IFD_PARAMS ifdParams) {
        SMARTCARD_REQUEST smartcardRequest = Smartcard.SmartcardRequest;
        if (!Smartcard.T1.MoreData && Smartcard.T1.BytesSent == 0L) {
            Smartcard.CardStartedChaining = false;
            if (Smartcard.T1.ReplyData != null) {
                Smartcard.T1.ReplyData = null;
            }

            if (Smartcard.iorequest.ReplyBufferLength < 2L) {
                return -992L;
            }

            Smartcard.T1.ReplyData = ByteBuffer.allocate(131072);
            if (Smartcard.T1.ReplyData == null) {
                return -997L;
            }

            Smartcard.T1.BytesToSend = Smartcard.iorequest.RequestBufferLength;
            Smartcard.T1.BytesSent = 0L;
            Smartcard.T1.BytesReceived = 0L;
            Smartcard.T1.InfBytesSent = (char)(IFDLayer.CCID_MAX_PKT_SIZE - 10);
        }

        if (Smartcard.T1.BytesToSend == 0L) {
            ifdParams.transmit.TransferLevel = 16;
            smartcardRequest.Buffer = null;
            smartcardRequest.BufferLength = 0L;
        } else {
            T1_DATA var10000;
            if (Smartcard.T1.BytesToSend > (long)Smartcard.T1.InfBytesSent) {
                Smartcard.T1.MoreData = true;
                var10000 = Smartcard.T1;
                var10000.BytesToSend -= (long)(IFDLayer.CCID_MAX_PKT_SIZE - 10);
                smartcardRequest.BufferLength = (long)Smartcard.T1.InfBytesSent;
            } else {
                Smartcard.T1.MoreData = false;
                smartcardRequest.BufferLength = Smartcard.T1.BytesToSend;
                Smartcard.T1.BytesToSend = 0L;
            }

            System.arraycopy(Smartcard.iorequest.RequestBuffer.array(), (int)Smartcard.T1.BytesSent, smartcardRequest.Buffer.array(), 0, (int)smartcardRequest.BufferLength);
            if (Smartcard.T1.MoreData) {
                if (Smartcard.T1.BytesToSend > (long)IFDLayer.CCID_MAX_PKT_SIZE) {
                    ifdParams.transmit.TransferLevel = 3;
                } else {
                    ifdParams.transmit.TransferLevel = 1;
                }
            } else if (Smartcard.T1.BytesSent > 0L) {
                ifdParams.transmit.TransferLevel = 2;
            } else {
                ifdParams.transmit.TransferLevel = 0;
            }

            var10000 = Smartcard.T1;
            var10000.BytesSent += smartcardRequest.BufferLength;
        }

        return 0L;
    }

    long SmartcardAPDUReply(SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION ReaderExtn) {
        long ntStatus = -994L;
        if (Smartcard.CardCapabilities.InversConvention) {
            SmartcardInvertData(Smartcard.SmartcardReply.Buffer, Smartcard.SmartcardReply.BufferLength, 0);
        }

        if (Smartcard.AwaitingFirstResponse) {
            Smartcard.AwaitingFirstResponse = false;
        } else {
            System.arraycopy(Smartcard.SmartcardReply.Buffer.array(), 0, Smartcard.T1.ReplyData.array(), (int)Smartcard.T1.BytesReceived, (int)Smartcard.SmartcardReply.BufferLength);
            T1_DATA var10000 = Smartcard.T1;
            var10000.BytesReceived += Smartcard.SmartcardReply.BufferLength;
            if (!Smartcard.CardStartedChaining) {
                if (Smartcard.iorequest.ReplyBufferLength < Smartcard.T1.BytesReceived) {
                    ntStatus = -992L;
                    Smartcard.T1.BytesSent = 0L;
                } else {
                    System.arraycopy(Smartcard.T1.ReplyData.array(), 0, Smartcard.iorequest.ReplyBuffer.array(), 0, (int)Smartcard.T1.BytesReceived);
                    Smartcard.iorequest.Information = Smartcard.T1.BytesReceived;
                    Smartcard.T1.BytesSent = 0L;
                    ntStatus = 0L;
                }
            }
        }

        return ntStatus;
    }

    class SMARTCARD_EXTENSION {
        long Version;
        VENDOR_ATTR VendorAttr = SmcLib.this.new VENDOR_ATTR();
        SCARD_CARD_CAPABILITIES CardCapabilities = SmcLib.this.new SCARD_CARD_CAPABILITIES();
        long LastError;
        IoRequest iorequest = new IoRequest();
        long MajorIoControlCode;
        long MinorIoControlCode;
        SCARD_READER_CAPABILITIES ReaderCapabilities = SmcLib.this.new SCARD_READER_CAPABILITIES();
        SMARTCARD_REPLY SmartcardReply = SmcLib.this.new SMARTCARD_REPLY();
        SMARTCARD_REQUEST SmartcardRequest = SmcLib.this.new SMARTCARD_REQUEST();
        T0_DATA T0 = SmcLib.this.new T0_DATA();
        T1_DATA T1 = SmcLib.this.new T1_DATA();
        boolean AwaitingFirstResponse;
        boolean CardStartedChaining;
        byte InvalidRetryCount;
        byte ResynchRetryCount;

        SMARTCARD_EXTENSION() {
        }

        class IoRequest {
            long Information;
            ByteBuffer RequestBuffer;
            long RequestBufferLength;
            ByteBuffer ReplyBuffer;
            long ReplyBufferLength;

            IoRequest() {
            }
        }
    }

    class VENDOR_ATTR {
        VendorName vendorName = new VendorName();
        IfdType ifdType = new IfdType();
        int UnitNo;
        IfdVersion ifdVersion = new IfdVersion();
        IfdSerialNo ifdSerialNo = new IfdSerialNo();
        int[] Reserved;

        VENDOR_ATTR() {
        }

        class IfdSerialNo {
            int Length;
            byte[] Buffer = new byte[32];

            IfdSerialNo() {
            }
        }

        class IfdVersion {
            short BuildNumber;
            short VersionMinor;
            short VersionMajor;

            IfdVersion() {
            }
        }

        class IfdType {
            int Length;
            byte[] Buffer = new byte[33];

            IfdType() {
            }
        }

        class VendorName {
            int Length;
            byte[] Buffer = new byte[33];

            VendorName() {
            }
        }
    }

    class SCARD_READER_CAPABILITIES {
        long SupportedProtocols;
        long Reserved;
        long ReaderMode;
        long ReaderType;
        long MechProperties;
        long CurrentState;
        long Channel;
        CLKFrequency frequency = new CLKFrequency();
        DataRate dataRate = new DataRate();
        long MaxIFSD;
        long PowerMgmtSupport;
        long CardConfiscated;
        DataRatesSupported dataRatesSupported = new DataRatesSupported();
        CLKFrequenciesSupported frequenciesSupported = new CLKFrequenciesSupported();
        ByteBuffer Reserved1;

        SCARD_READER_CAPABILITIES() {
        }

        class CLKFrequenciesSupported {
            long[] List;
            char Entries;

            CLKFrequenciesSupported() {
            }
        }

        class DataRatesSupported {
            long[] List;
            char Entries;

            DataRatesSupported() {
            }
        }

        class DataRate {
            long Default;
            long Max;

            DataRate() {
            }
        }

        class CLKFrequency {
            long Default;
            long Max;

            CLKFrequency() {
            }
        }
    }

    class SCARD_CARD_CAPABILITIES {
        boolean InversConvention;
        long etu;
        ATR atr = new ATR();
        HistoricalChars chars = new HistoricalChars();
        CLOCK_RATE_CONVERSION ClockRateConversion = SmcLib.this.new CLOCK_RATE_CONVERSION();
        BIT_RATE_ADJUSTMENT BitRateAdjustment = SmcLib.this.new BIT_RATE_ADJUSTMENT();
        short Fl;
        short Dl;
        short II;
        short P;
        short N;
        long GT;
        Protocol protocol = new Protocol();
        T0 t0 = new T0();
        T1 t1 = new T1();
        PTS_DATA PtsData = SmcLib.this.new PTS_DATA();
        ByteBuffer Reserved;

        SCARD_CARD_CAPABILITIES() {
        }

        class T1 {
            char IFSC;
            char CWI;
            char BWI;
            char EDC;
            long CWT;
            long BWT;
            long BGT;

            T1() {
            }
        }

        class T0 {
            char WI;
            long WT;

            T0() {
            }
        }

        class Protocol {
            long Supported;
            long Selected;

            Protocol() {
            }
        }

        class HistoricalChars {
            ByteBuffer Buffer;
            int Length;

            HistoricalChars() {
            }
        }

        class ATR {
            byte[] Buffer;
            char Length;

            ATR() {
            }
        }
    }

    class PTS_DATA {
        static final int PTS_TYPE_DEFAULT = 0;
        static final int PTS_TYPE_OPTIMAL = 1;
        static final int PTS_TYPE_USER = 2;
        char Type;
        short Fl;
        short Dl;
        long CLKFrequency;
        long DataRate;
        char StopBits;

        PTS_DATA() {
        }
    }

    class BIT_RATE_ADJUSTMENT {
        final long DNumerator = 0L;
        final long DDivisor = 0L;

        BIT_RATE_ADJUSTMENT() {
        }
    }

    class CLOCK_RATE_CONVERSION {
        final long F = 0L;
        final long fs = 0L;

        CLOCK_RATE_CONVERSION() {
        }
    }

    class SMARTCARD_REPLY {
        ByteBuffer Buffer;
        long BufferSize;
        long BufferLength;

        SMARTCARD_REPLY() {
        }
    }

    class SMARTCARD_REQUEST {
        ByteBuffer Buffer;
        long BufferSize;
        long BufferLength;

        SMARTCARD_REQUEST() {
        }
    }

    class T1_BLOCK_FRAME {
        byte Nad;
        byte Pcb;
        byte Len;
        ByteBuffer Inf = ByteBuffer.allocate(255);

        T1_BLOCK_FRAME() {
        }
    }

    class T1_DATA {
        char IFSC;
        char IFSD;
        long BytesReceived;
        long BytesSent;
        long BytesToSend;
        char LastError;
        boolean MoreData;
        char NAD;
        long OriginalState;
        char Resend;
        char Resynch;
        char RSN;
        char SSN;
        long State;
        char Wtx;
        ByteBuffer ReplyData;
        boolean WaitForReply;
        char InfBytesSent;
        ByteBuffer Reserved;

        T1_DATA() {
        }
    }

    class T0_DATA {
        long Lc;
        long Le;

        T0_DATA() {
        }
    }
}
