package com.consulmedics.patientdata.scardlib;

import com.consulmedics.patientdata.scardlib.Defs.MiscelaneousIOCTLS;
import com.consulmedics.patientdata.scardlib.IFDLayer.IFD_COMMAND;
import java.nio.ByteBuffer;

class IFDLayerInterface {
    static final int HID_SCARD_VENDOR_ID = 1254;
    static final int HID_SCARD_PRODUCT_MASK = 61680;
    static final int HID_SCARD_PRODUCT_ID = 20496;
    static final int MAX_IFSD = 254;
    static final long IFD_MAX_APDU_SIZE = 4096L;

    IFDLayerInterface() {
    }

    static final boolean NT_SUCCESS(long ntStatus) {
        return ntStatus == 0L;
    }

    static void _UpdateCurrentCardState(SmcLib.SMARTCARD_EXTENSION Smartcard, long newState) {
        Smartcard.ReaderCapabilities.CurrentState = newState;
    }

    static long IFD_TransmitRaw(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION Reader) {
        long ntStatus = 0L;
        IFDLayer.IFD_PARAMS IfdParam = null;
        long ulIoRequestDataLength = 0L;
        Smartcard.SmartcardRequest.BufferLength = 0L;
        if (Smartcard.iorequest.RequestBufferLength >= Smartcard.SmartcardRequest.BufferSize) {
            ntStatus = -1000L;
        } else {
            ulIoRequestDataLength = Smartcard.iorequest.RequestBufferLength;
            System.arraycopy(Smartcard.iorequest.ReplyBuffer.array(), 0, Smartcard.SmartcardRequest.Buffer.array(), 0, (int)ulIoRequestDataLength);
            Smartcard.SmartcardRequest.BufferLength = ulIoRequestDataLength;
            long L = ulIoRequestDataLength - 4L;
            char B1 = Smartcard.SmartcardRequest.Buffer.getChar(4);
            if (0L == L) {
                ((IFDLayer.IFD_PARAMS)IfdParam).transmit.TransferLevel = 2;
            } else if (0 != B1) {
                ((IFDLayer.IFD_PARAMS)IfdParam).transmit.TransferLevel = 2;
            } else {
                ((IFDLayer.IFD_PARAMS)IfdParam).transmit.TransferLevel = 2;
            }

            ((IFDLayer.IFD_PARAMS)IfdParam).transmit.BWI = 0;
            IFDLayer ifdLayer = new IFDLayer();
            CCIDHandler.CCID_RESPONSE validResponse = ifdLayer.IFD_Process(IFD_COMMAND.IFD_TRANSMIT, (IFDLayer.IFD_PARAMS)IfdParam, Smartcard, Reader, Smartcard.SmartcardRequest.Buffer, Smartcard.SmartcardRequest.BufferLength, Smartcard.SmartcardReply.BufferSize);
            if (validResponse == null) {
                ntStatus = -982L;
            } else {
                ntStatus = validResponse.ntStatus;
                if (NT_SUCCESS(ntStatus)) {
                    if (Smartcard.iorequest.ReplyBufferLength < Smartcard.SmartcardReply.BufferLength) {
                        ntStatus = -992L;
                    } else {
                        System.arraycopy(Smartcard.SmartcardReply.Buffer, 0, Smartcard.iorequest.ReplyBuffer, 0, (int)Smartcard.SmartcardReply.BufferLength);
                        Smartcard.iorequest.Information = Smartcard.SmartcardReply.BufferLength;
                        ntStatus = 0L;
                    }
                }
            }
        }

        return ntStatus;
    }

    static long IFD_TransmitT0(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION Reader) {
        long ntStatus = 0L;
        IFDLayer.IFD_PARAMS IfdParam = new IFDLayer.IFD_PARAMS();
        Smartcard.SmartcardRequest.BufferLength = 0L;
        SmcLib scmlib = new SmcLib();
        ntStatus = scmlib.SmartcardT0Request(Smartcard);
        if (NT_SUCCESS(ntStatus)) {
            IfdParam.transmit.TransferLevel = 0;
            IfdParam.transmit.BWI = 0;
            IFDLayer ifdLayer = new IFDLayer();
            CCIDHandler.CCID_RESPONSE validResponse = ifdLayer.IFD_Process(IFD_COMMAND.IFD_TRANSMIT, IfdParam, Smartcard, Reader, Smartcard.SmartcardRequest.Buffer, Smartcard.SmartcardRequest.BufferLength, Smartcard.SmartcardReply.BufferSize);
            if (validResponse == null) {
                ntStatus = -982L;
            } else {
                ntStatus = validResponse.ntStatus;
                if (NT_SUCCESS(ntStatus)) {
                    Smartcard.SmartcardReply.BufferLength = validResponse.responseLen;
                    System.arraycopy(validResponse.respData, 0, Smartcard.SmartcardReply.Buffer.array(), 0, (int)validResponse.responseLen);
                    ntStatus = scmlib.SmartcardT0Reply(Smartcard);
                }
            }
        }

        return ntStatus;
    }

    static long IFD_TransmitT1(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION Reader) {
        long ntStatus = 0L;
        IFDLayer.IFD_PARAMS IfdParam = new IFDLayer.IFD_PARAMS();
        boolean bAbortResponse = false;
        boolean bRBlock = false;
        IfdParam.transmit.TransferLevel = 0;
        IfdParam.transmit.BWI = 0;

        do {
            Smartcard.SmartcardRequest.BufferLength = 0L;
            Smartcard.T1.NAD = 0;
            SmcLib scmlib = new SmcLib();
            ntStatus = scmlib.SmartcardT1Request(Smartcard);
            if (!NT_SUCCESS(ntStatus)) {
                break;
            }

            if (225L == Smartcard.T1.State && 2L == Smartcard.T1.OriginalState) {
                Smartcard.T1.State = 2L;
            }

            bAbortResponse = false;
            bRBlock = false;
            if (Smartcard.SmartcardRequest.BufferLength == 4L && Smartcard.SmartcardRequest.Buffer.get(1) == 226) {
                bAbortResponse = true;
            }

            if (Smartcard.SmartcardReply.BufferSize != 0L) {
                Smartcard.SmartcardReply.Buffer = ByteBuffer.allocate(131072);
            }

            Smartcard.SmartcardReply.BufferLength = 0L;
            IFDLayer ifdLayer = new IFDLayer();
            CCIDHandler.CCID_RESPONSE validResponse = ifdLayer.IFD_Process(IFD_COMMAND.IFD_TRANSMIT, IfdParam, Smartcard, Reader, Smartcard.SmartcardRequest.Buffer, Smartcard.SmartcardRequest.BufferLength, Smartcard.SmartcardReply.BufferSize);
            if (validResponse == null) {
                ntStatus = -982L;
                break;
            }

            ntStatus = validResponse.ntStatus;
            if (!NT_SUCCESS(ntStatus) && -990L != ntStatus) {
                break;
            }

            Smartcard.SmartcardReply.BufferLength = validResponse.responseLen;
            if (validResponse.responseLen != 0L) {
                System.arraycopy(validResponse.respData, 0, Smartcard.SmartcardReply.Buffer.array(), 0, (int)validResponse.responseLen);
            }

            if (bAbortResponse) {
                char byTemp = (char)(Smartcard.SmartcardReply.Buffer.get(1) & 240);
                if (byTemp == 128 || byTemp == 144) {
                    bRBlock = true;
                }
            }

            if (Smartcard.SmartcardReply.BufferSize > 3L && Smartcard.SmartcardReply.BufferLength > 3L && 195 == (Smartcard.SmartcardReply.Buffer.get(1) & 255)) {
                IfdParam.transmit.BWI = (char)Smartcard.SmartcardReply.Buffer.get(3);
            }

            ntStatus = scmlib.SmartcardT1Reply(Smartcard, Reader);
            if (bAbortResponse && bRBlock) {
                Smartcard.T1.State = 1L;
                ntStatus = -988L;
            }
        } while(-994L == ntStatus);

        return ntStatus;
    }

    static long IFD_TransmitAPDU(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION Reader) {
        long ntStatus = 0L;
        IFDLayer.IFD_PARAMS IfdParam = new IFDLayer.IFD_PARAMS();
        IfdParam.transmit.BWI = 0;

        do {
            SmcLib scmlib = new SmcLib();
            ntStatus = scmlib.SmartcardAPDURequest(Smartcard, IfdParam);
            if (!NT_SUCCESS(ntStatus)) {
                break;
            }

            if (Smartcard.SmartcardReply.BufferSize != 0L) {
                Smartcard.SmartcardReply.Buffer = ByteBuffer.allocate(131072);
            }

            Smartcard.SmartcardReply.BufferLength = 0L;
            IFDLayer ifdLayer = new IFDLayer();
            CCIDHandler.CCID_RESPONSE validResponse = ifdLayer.IFD_Process(IFD_COMMAND.IFD_TRANSMIT, IfdParam, Smartcard, Reader, Smartcard.SmartcardRequest.Buffer, Smartcard.SmartcardRequest.BufferLength, Smartcard.SmartcardReply.BufferSize);
            if (validResponse == null) {
                ntStatus = -982L;
                break;
            }

            ntStatus = validResponse.ntStatus;
            if (!NT_SUCCESS(ntStatus) && -990L != ntStatus) {
                break;
            }

            if (validResponse.pExtra != 1 && validResponse.pExtra != 3) {
                if (validResponse.pExtra == 16) {
                    Smartcard.AwaitingFirstResponse = true;
                } else {
                    Smartcard.CardStartedChaining = false;
                }
            } else {
                Smartcard.CardStartedChaining = true;
            }

            Smartcard.SmartcardReply.BufferLength = validResponse.responseLen;
            if (validResponse.responseLen != 0L) {
                System.arraycopy(validResponse.respData, 0, Smartcard.SmartcardReply.Buffer.array(), 0, (int)validResponse.responseLen);
            }

            ntStatus = scmlib.SmartcardAPDUReply(Smartcard, Reader);
        } while(-994L == ntStatus);

        return ntStatus;
    }

    long IFD_Init(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION Reader) {
        long status = 0L;
        long[] ClockFrequencies = new long[]{4800L, 6000L, 8000L};
        long[] DataRates = new long[]{12903L, 15625L, 16129L, 18750L, 21505L, 23438L, 25806L, 31250L, 32258L, 37500L, 43011L, 46875L, 51613L, 62500L, 64516L, 70313L, 75000L, 77419L, 86022L, 93750L, 96774L, 103226L, 112500L, 117188L, 125000L, 129032L, 150000L, 154839L, 156250L, 161290L, 172043L, 187500L, 206452L, 215054L, 250000L, 258065L, 300000L, 344086L, 412903L};
        Smartcard.ReaderCapabilities.ReaderType = 32L;
        Smartcard.ReaderCapabilities.ReaderMode = SCard.bAPDURdrType ? 2L : 1L;
        Smartcard.VendorAttr.ifdVersion.BuildNumber = 0;
        Smartcard.ReaderCapabilities.Channel = 0L;
        Smartcard.ReaderCapabilities.MechProperties = 0L;
        Smartcard.ReaderCapabilities.CurrentState = 0L;
        Smartcard.ReaderCapabilities.frequenciesSupported.List = ClockFrequencies;
        Smartcard.ReaderCapabilities.frequenciesSupported.Entries = (char)ClockFrequencies.length;
        Smartcard.ReaderCapabilities.dataRatesSupported.List = DataRates;
        Smartcard.ReaderCapabilities.dataRatesSupported.Entries = (char)DataRates.length;
        Smartcard.SmartcardRequest.BufferSize = 4096L;
        Smartcard.SmartcardReply.BufferSize = 4096L;
        SmcLib scmLib = new SmcLib();
        scmLib.SmartcardInitialize(Smartcard);
        Reader.fVal = -1;
        Reader.dVal = -1;
        return status;
    }

    long IFD_Cleanup(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION Reader) {
        long status = 0L;
        SmcLib scmLib = new SmcLib();
        scmLib.SmartcardExit(Smartcard);
        Reader.handle = -1L;
        return status;
    }

    long IFD_PowerControl(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION Reader) {
        SmcLib scmLib = new SmcLib();
        new IFDLayer();
        long ntStatus = 0L;
        IFDLayer.IFD_PARAMS IfdParam = new IFDLayer.IFD_PARAMS();
        IfdParam.power.ResetType = Smartcard.MinorIoControlCode;
        if (Smartcard.ReaderCapabilities.CurrentState <= 1L) {
            ntStatus = -990L;
        } else {
            scmLib.SmartcardInitializeCardCapabilities(Smartcard);
            if (IfdParam.power.ResetType == 2L && Smartcard.ReaderCapabilities.CurrentState < 4L) {
                IfdParam.power.ResetType = 1L;
            }

            IFDLayer ifdLayer = new IFDLayer();
            CCIDHandler.CCID_RESPONSE validResponse = ifdLayer.IFD_Process(IFD_COMMAND.IFD_POWER, IfdParam, Smartcard, Reader, (ByteBuffer)null, 0L, 33L);
            if (validResponse == null) {
                ntStatus = -982L;
            } else {
                ntStatus = validResponse.ntStatus;
                if (NT_SUCCESS(ntStatus)) {
                    if (IfdParam.power.ResetType != 0L) {
                        Smartcard.CardCapabilities.atr.Buffer = new byte[(int)validResponse.responseLen];
                        Smartcard.CardCapabilities.atr.Length = (char)((int)validResponse.responseLen);
                        System.arraycopy(validResponse.respData, 0, Smartcard.CardCapabilities.atr.Buffer, 0, (int)validResponse.responseLen);
                    }

                    if (IfdParam.power.ResetType != 1L && IfdParam.power.ResetType != 2L) {
                        Smartcard.CardCapabilities.atr.Length = 0;
                        Smartcard.CardCapabilities.protocol.Selected = 0L;
                        Reader.fVal = -1;
                        Reader.dVal = -1;
                    } else {
                        ntStatus = scmLib.SmartcardUpdateCardCapabilities(Smartcard);
                    }
                }
            }
        }

        return ntStatus;
    }

    long IFD_Transmit(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION Reader) {
        long ntStatus = 0L;
        if (Smartcard.ReaderCapabilities.CurrentState != 6L) {
            ntStatus = -985L;
        } else if (Smartcard.iorequest.RequestBufferLength < 5L) {
            ntStatus = -995L;
        } else {
            Smartcard.SmartcardRequest.BufferLength = 0L;
            if (Smartcard.SmartcardRequest.BufferSize != 0L) {
                Smartcard.SmartcardRequest.Buffer = ByteBuffer.allocate(65536);
            }

            if (Smartcard.SmartcardReply.BufferSize != 0L) {
                Smartcard.SmartcardReply.Buffer = ByteBuffer.allocate(131072);
            }

            if (Smartcard.ReaderCapabilities.ReaderType == 1L) {
                IFD_TransmitRaw(Smartcard, Reader);
            }

            if (Smartcard.ReaderCapabilities.ReaderMode == 2L) {
                ntStatus = IFD_TransmitAPDU(Smartcard, Reader);
            } else if (Smartcard.CardCapabilities.protocol.Selected == 2L) {
                ntStatus = IFD_TransmitT1(Smartcard, Reader);
            } else {
                ntStatus = IFD_TransmitT0(Smartcard, Reader);
            }
        }

        return ntStatus;
    }

    long IFD_SetProtocol(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION Reader) {
        long ntStatus = 0L;
        long ulLength = 0L;
        IFDLayer.IFD_PARAMS IfdParam = new IFDLayer.IFD_PARAMS();
        long NewProtocol = Smartcard.MinorIoControlCode;
        IFDLayer ifdLayer = new IFDLayer();
        if (Smartcard.ReaderCapabilities.CurrentState == 6L && (Smartcard.CardCapabilities.protocol.Selected & Smartcard.MinorIoControlCode) != 0L) {
            ntStatus = 0L;
        } else if (Smartcard.ReaderCapabilities.CurrentState != 0L && Smartcard.ReaderCapabilities.CurrentState != 2L) {
            if (Smartcard.ReaderCapabilities.CurrentState == 1L) {
                ntStatus = -984L;
            } else if ((NewProtocol & 3L) == 0L) {
                ntStatus = -993L;
            } else {
                if ((NewProtocol & -2147483648L) != 0L) {
                    Smartcard.CardCapabilities.PtsData.Type = 0;
                } else {
                    Smartcard.CardCapabilities.PtsData.Type = 1;
                }

                SmcLib scmlib = new SmcLib();
                ntStatus = scmlib.SmartcardUpdateCardCapabilities(Smartcard);
                if (NT_SUCCESS(ntStatus)) {
                    if (Smartcard.ReaderCapabilities.CurrentState < 5L) {
                        ntStatus = -985L;
                    } else if ((Smartcard.CardCapabilities.protocol.Supported & NewProtocol) == 0L) {
                        _UpdateCurrentCardState(Smartcard, 5L);
                        Smartcard.CardCapabilities.protocol.Selected = 0L;
                        ntStatus = -983L;
                    } else {
                        long rstStatus;
                        if ((Smartcard.ReaderCapabilities.SupportedProtocols & NewProtocol) != 0L) {
                            do {
                                long SelectedProtocol;
                                if ((NewProtocol & Smartcard.CardCapabilities.protocol.Supported & Smartcard.ReaderCapabilities.SupportedProtocols & 2L) != 0L) {
                                    SelectedProtocol = 2L;
                                } else {
                                    SelectedProtocol = 1L;
                                }

                                short fVal = Smartcard.CardCapabilities.PtsData.Fl;
                                short dVal = Smartcard.CardCapabilities.PtsData.Dl;
                                if (0 != Smartcard.CardCapabilities.PtsData.Type && Reader.fVal != -1 && Reader.dVal != -1) {
                                    fVal = Reader.fVal;
                                    dVal = Reader.dVal;
                                }

                                CCIDHandler.CCID_RESPONSE validResponse;
                                do {
                                    IfdParam.protocol.NewProtocol = SelectedProtocol;
                                    IfdParam.protocol.fVal = (long)fVal;
                                    IfdParam.protocol.dVal = (long)dVal;
                                    validResponse = ifdLayer.IFD_Process(IFD_COMMAND.IFD_SETPROTOCOL, IfdParam, Smartcard, Reader, (ByteBuffer)null, 0L, 0L);
                                    if (validResponse == null) {
                                        ntStatus = -982L;
                                        break;
                                    }

                                    ntStatus = validResponse.ntStatus;
                                    if (NT_SUCCESS(ntStatus) || 0 == Smartcard.CardCapabilities.PtsData.Type) {
                                        break;
                                    }

                                    IfdParam.power.ResetType = 1L;
                                    validResponse = ifdLayer.IFD_Process(IFD_COMMAND.IFD_POWER, IfdParam, Smartcard, Reader, (ByteBuffer)null, 0L, 33L);
                                    if (validResponse == null) {
                                        ntStatus = -982L;
                                        break;
                                    }

                                    ntStatus = validResponse.ntStatus;
                                    if (!NT_SUCCESS(validResponse.ntStatus)) {
                                        break;
                                    }

                                    --dVal;
                                    if (7 == dVal) {
                                        --dVal;
                                    }
                                } while(dVal > 0);

                                if (NT_SUCCESS(ntStatus)) {
                                    Smartcard.CardCapabilities.Dl = (short)((int)IfdParam.protocol.dVal);
                                    Smartcard.CardCapabilities.Fl = (short)((int)IfdParam.protocol.fVal);
                                    Smartcard.CardCapabilities.protocol.Selected = IfdParam.protocol.NewProtocol;
                                    break;
                                }

                                if (0 == Smartcard.CardCapabilities.PtsData.Type) {
                                    break;
                                }

                                Smartcard.CardCapabilities.PtsData.Type = 0;
                                IfdParam.power.ResetType = 1L;
                                validResponse = ifdLayer.IFD_Process(IFD_COMMAND.IFD_POWER, IfdParam, Smartcard, Reader, (ByteBuffer)null, 0L, 33L);
                                if (validResponse == null) {
                                    ntStatus = -982L;
                                    break;
                                }

                                ntStatus = validResponse.ntStatus;
                                if (!NT_SUCCESS(ntStatus)) {
                                    if (Smartcard.ReaderCapabilities.CurrentState <= 1L) {
                                        ntStatus = -984L;
                                    }
                                    break;
                                }

                                Smartcard.CardCapabilities.atr.Length = (char)((int)ulLength);
                                rstStatus = scmlib.SmartcardUpdateCardCapabilities(Smartcard);
                            } while(NT_SUCCESS(rstStatus) && NT_SUCCESS(rstStatus));
                        } else {
                            ntStatus = -983L;
                        }
                    }
                }
            }
        } else {
            ntStatus = -985L;
        }

        if (NT_SUCCESS(ntStatus)) {
            _UpdateCurrentCardState(Smartcard, 6L);
        } else {
            Smartcard.CardCapabilities.protocol.Selected = 0L;
        }

        return ntStatus;
    }

    long IFD_SlotStatus(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION Reader) {
        long ntStatus = 0L;
        IFDLayer ifdLayer = new IFDLayer();
        IFDLayer.IFD_PARAMS IfdParam = new IFDLayer.IFD_PARAMS();
        CCIDHandler.CCID_RESPONSE validResponse = ifdLayer.IFD_Process(IFD_COMMAND.IFD_SLOT_STATUS, IfdParam, Smartcard, Reader, (ByteBuffer)null, 0L, 0L);
        if (validResponse == null) {
            ntStatus = -982L;
        } else {
            ntStatus = validResponse.ntStatus;
            if (!NT_SUCCESS(ntStatus)) {
            }
        }

        return ntStatus;
    }

    long IFD_VendorCommand(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION Reader) {
        long ntStatus = 0L;
        IFDLayer.IFD_PARAMS IfdParam = new IFDLayer.IFD_PARAMS();
        IFDLayer ifdLayer = new IFDLayer();
        if (Smartcard.MajorIoControlCode == MiscelaneousIOCTLS.IOCTL_CCID_ESCAPE) {
            CCIDHandler.CCID_RESPONSE validResponse = ifdLayer.IFD_Process(IFD_COMMAND.IFD_ESCAPE, IfdParam, Smartcard, Reader, Smartcard.iorequest.RequestBuffer, Smartcard.iorequest.RequestBufferLength, Smartcard.iorequest.ReplyBufferLength);
            if (validResponse == null) {
                ntStatus = -982L;
            } else if (validResponse.responseLen > Smartcard.iorequest.ReplyBufferLength) {
                ntStatus = -1000L;
            } else {
                ntStatus = validResponse.ntStatus;
                if (validResponse.responseLen != 0L) {
                    System.arraycopy(validResponse.respData, 0, Smartcard.iorequest.ReplyBuffer.array(), 0, (int)validResponse.responseLen);
                }

                Smartcard.iorequest.Information = validResponse.responseLen;
                if (ntStatus == -982L && Smartcard.iorequest.RequestBufferLength >= 1L && Smartcard.iorequest.RequestBuffer != null && Smartcard.iorequest.RequestBuffer.get(0) == 255) {
                    Smartcard.iorequest.Information = 0L;
                    ntStatus = 0L;
                }
            }
        }

        return ntStatus;
    }
}
