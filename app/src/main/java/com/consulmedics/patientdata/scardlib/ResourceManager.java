package com.consulmedics.patientdata.scardlib;

import android.util.Log;
import java.nio.ByteBuffer;

class ResourceManager {
    private static final String TAG = "SCard Library";
    private static SmcLib.SMARTCARD_EXTENSION smartcard;
    private static IFDLayer.READER_EXTENSION readerExt;
    private static String Reader;
    private static int Mode;
    private static byte[] atr;
    private static int atrlen;
    private boolean bIsStateChanged = false;

    ResourceManager() {
    }

    long RM_SCardConnect(String RdrName, int nMode, int nPreferredProtocols) {
        Log.i("SCard Library", "----- RM_SCardConnect -----");
        boolean bIsInitPass = false;
        boolean bIsPoweredON = false;
        long lRetval = -2146435052L;
        IFDLayerInterface layerInterface = new IFDLayerInterface();
        if (RdrName == null) {
            lRetval = -2146435063L;
            Log.e("SCard Library", "SCARD_E_UNKNOWN_READER");
        } else if (1 != nMode && 3 != nMode) {
            lRetval = -2146435055L;
            Log.e("SCard Library", "SCARD_E_INVALID_VALUE - 1");
        } else if (((long)nPreferredProtocols & 3L) != (long)nPreferredProtocols) {
            lRetval = -2146435068L;
            Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
        } else if (65536L == (long)nPreferredProtocols) {
            lRetval = -2146435055L;
            Log.e("SCard Library", "SCARD_E_INVALID_VALUE - 2");
        } else if (3 != nMode && (nPreferredProtocols & 3) == 0) {
            lRetval = -2146435055L;
            Log.e("SCard Library", "SCARD_E_INVALID_VALUE - 3");
        } else {
            Reader = RdrName;
            Mode = nMode;
            lRetval = layerInterface.IFD_Init(smartcard, readerExt);
            if (0L != lRetval) {
                lRetval = this.RM_ReturnStatus(lRetval);
            } else {
                bIsInitPass = true;
                lRetval = layerInterface.IFD_SlotStatus(smartcard, readerExt);
                if (smartcard.ReaderCapabilities.CurrentState <= 1L) {
                    lRetval = -2146434967L;
                    Log.e("SCard Library", "SCARD_W_REMOVED_CARD");
                    if (3 == nMode) {
                        lRetval = 0L;
                    }
                } else {
                    smartcard.MinorIoControlCode = 2L;
                    lRetval = layerInterface.IFD_PowerControl(smartcard, readerExt);
                    if (0L != lRetval) {
                        if (3 == nMode) {
                            lRetval = 0L;
                        } else {
                            lRetval = this.RM_ReturnStatus(lRetval);
                        }
                    } else {
                        bIsPoweredON = true;
                        atrlen = smartcard.CardCapabilities.atr.Length;
                        if (atrlen != 0) {
                            atr = new byte[atrlen];
                        }

                        System.arraycopy(smartcard.CardCapabilities.atr.Buffer, 0, atr, 0, atrlen);
                        if (3 == nMode) {
                            lRetval = 0L;
                        } else {
                            smartcard.MinorIoControlCode = (long)nPreferredProtocols;
                            lRetval = layerInterface.IFD_SetProtocol(smartcard, readerExt);
                            if (0L != lRetval) {
                                lRetval = this.RM_ReturnStatus(lRetval);
                            }
                        }
                    }
                }
            }
        }

        if (bIsPoweredON && 3 == nMode) {
            smartcard.MinorIoControlCode = 0L;
            layerInterface.IFD_PowerControl(smartcard, readerExt);
        }

        if (0L != lRetval && bIsInitPass) {
            if (bIsPoweredON) {
                smartcard.MinorIoControlCode = 0L;
                layerInterface.IFD_PowerControl(smartcard, readerExt);
            }

            this.RM_CloseHandle(smartcard, readerExt);
        }

        return lRetval;
    }

    long RM_GetSlotStatus(SmcLib.SMARTCARD_EXTENSION pSmartcard, IFDLayer.READER_EXTENSION pReaderExtn) {
        Log.i("SCard Library", "----- RM_GetSlotStatus -----");
        long lRetVal = -2146435052L;
        IFDLayerInterface layerInterface = new IFDLayerInterface();
        lRetVal = layerInterface.IFD_SlotStatus(pSmartcard, pReaderExtn);
        if (0L != lRetVal) {
            lRetVal = this.RM_ReturnStatus(lRetVal);
        }

        return lRetVal;
    }

    long RM_SCardPowerControl(int nDisposition, boolean bIsDisConnect) {
        Log.i("SCard Library", "----- RM_SCardPowerControl -----");
        long lRetval = 0L;
        long lStatus = 0L;
        byte nCardState;
        switch (nDisposition) {
            case 1:
                nCardState = 2;
                break;
            case 2:
            case 3:
            default:
                nCardState = 1;
        }

        if (bIsDisConnect) {
            nCardState = 0;
        }

        smartcard.MinorIoControlCode = (long)nCardState;
        IFDLayerInterface layerInterface = new IFDLayerInterface();
        lStatus = layerInterface.IFD_PowerControl(smartcard, readerExt);
        if (0L != lStatus) {
            lRetval = this.RM_ReturnStatus(lStatus);
        }

        if (bIsDisConnect) {
            lRetval = 0L;
        }

        return lRetval;
    }

    long RM_ReturnStatus(long lStatus) {
        Log.i("SCard Library", "----- RM_ReturnStatus -----");
        long lRetval = -2146435052L;
        switch ((int)lStatus) {
            case -1000:
                lRetval = -2146435064L;
                Log.e("SCard Library", "SCARD_E_INSUFFICIENT_BUFFER");
                break;
            case -999:
                lRetval = -2146434969L;
                Log.e("SCard Library", "SCARD_W_UNPOWERED_CARD");
                break;
            case -998:
            case -990:
                lRetval = -2146434967L;
                Log.e("SCard Library", "SCARD_W_REMOVED_CARD");
                break;
            case -997:
                lRetval = -2146435055L;
                Log.e("SCard Library", "SCARD_E_INVALID_VALUE");
                break;
            case -996:
                lRetval = -2146435071L;
                Log.e("SCard Library", "SCARD_F_INTERNAL_ERROR");
                break;
            case -995:
                lRetval = -2146435068L;
                Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
                break;
            case -993:
                lRetval = -2146434970L;
                Log.e("SCard Library", "SCARD_W_UNRESPONSIVE_CARD");
                break;
            case -992:
                lRetval = -2146435064L;
                Log.e("SCard Library", "SCARD_E_INSUFFICIENT_BUFFER");
                break;
            case -991:
                lRetval = -2146434971L;
                Log.e("SCard Library", "SCARD_W_UNSUPPORTED_CARD");
                break;
            case -989:
                lRetval = -2146435057L;
                Log.e("SCard Library", "SCARD_E_PROTO_MISMATCH");
                break;
            case -988:
                lRetval = 1235L;
                Log.e("SCard Library", "ERROR_REQUEST_ABORTED");
                break;
            case -987:
                lRetval = -2146435056L;
                Log.e("SCard Library", "SCARD_E_NOT_READY");
                break;
            case -986:
                lRetval = -2146435052L;
                Log.e("SCard Library", "SCARD_F_UNKNOWN_ERROR");
                break;
            case -985:
                lRetval = 22L;
                Log.e("SCard Library", "ERROR_BAD_COMMAND");
                break;
            case -984:
                lRetval = -2146435060L;
                Log.e("SCard Library", "SCARD_E_NO_SMARTCARD");
                break;
            case -983:
                lRetval = -2146435057L;
                Log.e("SCard Library", "SCARD_E_PROTO_MISMATCH");
                break;
            case -982:
                lRetval = -2146435049L;
                Log.e("SCard Library", "SCARD_E_READER_UNAVAILABLE");
                break;
            case 0:
                lRetval = 0L;
        }

        return lRetval;
    }

    int RM_FinalizeCardStatus(int uSlotStatus, long lRetValue) {
        Log.i("SCard Library", "----- RM_FinalizeCardStatus -----");
        int pdwCardStatus;
        if (uSlotStatus <= 1) {
            pdwCardStatus = 16;
        } else if (uSlotStatus < 6 && uSlotStatus >= 2) {
            pdwCardStatus = 1056;
        } else {
            pdwCardStatus = 32;
        }

        if (0L != lRetValue) {
            if ((uSlotStatus >= 2 || uSlotStatus >= 4) && -990L == lRetValue) {
                pdwCardStatus |= 512;
            }

            switch ((int)lRetValue) {
                case -987:
                    pdwCardStatus = 288;
                    break;
                case -986:
                case -985:
                default:
                    pdwCardStatus = 8;
                    break;
                case -984:
                    pdwCardStatus = 16;
                    break;
                case -983:
                    pdwCardStatus = 160;
                    break;
                case -982:
                    pdwCardStatus = 4;
            }
        }

        return pdwCardStatus;
    }

    boolean RM_ProcessCurrentState(int dwInputCurrentState, int dwCardState) {
        Log.i("SCard Library", "----- RM_ProcessCurrentState -----");
        boolean bIsStateChanged = false;
        if (dwInputCurrentState >= 4095) {
            dwInputCurrentState = 0;
        }

        if (0 == dwInputCurrentState) {
            bIsStateChanged = true;
        } else if ((dwInputCurrentState & 16) != 0) {
            bIsStateChanged = (dwCardState & 16) == 0;
        } else if ((dwInputCurrentState & 32) != 0) {
            bIsStateChanged = (dwCardState & 32) == 0;
        } else if ((dwInputCurrentState & 12) != 0) {
            bIsStateChanged = true;
        }

        return bIsStateChanged;
    }

    long RM_CloseHandle(SmcLib.SMARTCARD_EXTENSION Smartcard, IFDLayer.READER_EXTENSION ReaderExtn) {
        Log.i("SCard Library", "----- RM_CloseHandle -----");
        IFDLayerInterface layerInterface = new IFDLayerInterface();
        layerInterface.IFD_SlotStatus(Smartcard, ReaderExtn);
        if (Smartcard.ReaderCapabilities.CurrentState >= 4L) {
            Smartcard.MinorIoControlCode = 0L;
            layerInterface.IFD_PowerControl(Smartcard, ReaderExtn);
        }

        long lRetval = layerInterface.IFD_Cleanup(Smartcard, ReaderExtn);
        if (0L != lRetval) {
            lRetval = this.RM_ReturnStatus(lRetval);
        }

        atrlen = 0;
        atr = null;
        Mode = 0;
        return lRetval;
    }

    long RM_CloseCardHandle(int nDisposition) {
        Log.i("SCard Library", "----- RM_CloseCardHandle -----");
        long lRetval = -2146435052L;
        if (3 != Mode) {
            lRetval = this.RM_SCardPowerControl(nDisposition, true);
            if (0L != lRetval) {
                return lRetval;
            }
        }

        lRetval = this.RM_CloseHandle(smartcard, readerExt);
        if (0L != lRetval) {
        }

        return lRetval;
    }

    long RM_Transmit(SCard.SCardIOBuffer transmit) {
        Log.i("SCard Library", "----- RM_Transmit -----");
        long lRetval = -2146435052L;
        IFDLayerInterface layerInterface = new IFDLayerInterface();
        if (Mode == 3) {
            lRetval = -2146435056L;
        } else if (null != transmit.abyInBuffer && -1 != transmit.nInBufferSize) {
            if (0 == transmit.nInBufferSize) {
                lRetval = -2146435068L;
            } else if (transmit.abyInBuffer.length < transmit.nInBufferSize) {
                lRetval = -2146435068L;
            } else {
                if (4 == transmit.nInBufferSize) {
                    smartcard.iorequest.RequestBufferLength = 5L;
                } else {
                    smartcard.iorequest.RequestBufferLength = (long)transmit.nInBufferSize;
                }

                smartcard.iorequest.RequestBuffer = ByteBuffer.allocate((int)smartcard.iorequest.RequestBufferLength);
                System.arraycopy(transmit.abyInBuffer, 0, smartcard.iorequest.RequestBuffer.array(), 0, transmit.nInBufferSize);
                smartcard.iorequest.ReplyBuffer = ByteBuffer.allocate(transmit.nOutBufferSize);
                smartcard.iorequest.ReplyBufferLength = (long)transmit.nOutBufferSize;
                if (smartcard.ReaderCapabilities.CurrentState < 6L) {
                    lRetval = -2146434967L;
                } else {
                    lRetval = layerInterface.IFD_Transmit(smartcard, readerExt);
                    if (0L != lRetval) {
                        lRetval = this.RM_ReturnStatus(lRetval);
                        if (smartcard.ReaderCapabilities.CurrentState < 2L) {
                            lRetval = -2146434967L;
                        } else if (6L != smartcard.ReaderCapabilities.CurrentState) {
                            lRetval = -2146435025L;
                        }
                    } else {
                        transmit.nBytesReturned = (int)smartcard.iorequest.Information;
                        System.arraycopy(smartcard.iorequest.ReplyBuffer.array(), 0, transmit.abyOutBuffer, 0, (int)smartcard.iorequest.Information);
                    }
                }
            }
        } else {
            lRetval = -2146435068L;
        }

        return lRetval;
    }

    long RM_SCardReconnect(int nMode, int nPreferredProtocols, int nInitialization) {
        Log.i("SCard Library", "----- RM_SCardReconnect -----");
        long lRetval = -2146435052L;
        IFDLayerInterface layerInterface = new IFDLayerInterface();
        if (1 != nMode && 3 != nMode) {
            lRetval = -2146435055L;
            Log.e("SCard Library", "SCARD_E_INVALID_VALUE - 1");
        } else if (((long)nPreferredProtocols & 3L) != (long)nPreferredProtocols) {
            lRetval = -2146435055L;
            Log.e("SCard Library", "SCARD_E_INVALID_VALUE - 2");
        } else if (nInitialization >= 3) {
            lRetval = -2146435068L;
            Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
        } else {
            lRetval = this.RM_GetSlotStatus(smartcard, readerExt);
            if (0L == lRetval) {
                if (smartcard.ReaderCapabilities.CurrentState <= 1L) {
                    if (3 == Mode && nMode != 3) {
                        lRetval = -2146435060L;
                        Log.e("SCard Library", "SCARD_E_NO_SMARTCARD");
                    } else {
                        lRetval = -2146434967L;
                        Log.e("SCard Library", "SCARD_W_REMOVED_CARD");
                    }
                } else {
                    if (nInitialization == 0 && smartcard.ReaderCapabilities.CurrentState >= 4L) {
                        if (((long)nPreferredProtocols & smartcard.CardCapabilities.protocol.Selected) == 0L) {
                            lRetval = -2146435057L;
                            Log.e("SCard Library", "SCARD_E_PROTO_MISMATCH");
                            return lRetval;
                        }
                    } else {
                        if (smartcard.ReaderCapabilities.CurrentState < 4L) {
                            nInitialization = 1;
                        }

                        if (nInitialization == 2) {
                            lRetval = this.RM_SCardPowerControl(nInitialization, true);
                            if (0L != lRetval) {
                                return lRetval;
                            }
                        }

                        lRetval = this.RM_SCardPowerControl(nInitialization, false);
                        if (0L != lRetval) {
                            return lRetval;
                        }

                        smartcard.MinorIoControlCode = (long)nPreferredProtocols;
                        lRetval = layerInterface.IFD_SetProtocol(smartcard, readerExt);
                        if (0L != lRetval) {
                            lRetval = this.RM_ReturnStatus(lRetval);
                            return lRetval;
                        }

                        if (1L != smartcard.CardCapabilities.protocol.Selected && 2L != smartcard.CardCapabilities.protocol.Selected) {
                            lRetval = -2146435044L;
                            Log.e("SCard Library", "SCARD_E_CARD_UNSUPPORTED");
                            return lRetval;
                        }
                    }

                    Mode = nMode;
                    atrlen = smartcard.CardCapabilities.atr.Length;
                    if (atrlen != 0) {
                        atr = new byte[atrlen];
                    }

                    System.arraycopy(smartcard.CardCapabilities.atr.Buffer, 0, atr, 0, atrlen);
                }
            }
        }

        return lRetval;
    }

    long RM_SCardControl(int nControlCode, SCard.SCardIOBuffer transmit) {
        Log.i("SCard Library", "----- RM_SCardControl -----");
        long lRetval = -2146435052L;
        IFDLayerInterface layerInterface = new IFDLayerInterface();
        if (-1L == (long)transmit.nInBufferSize && null != transmit.abyInBuffer) {
            lRetval = -2146435068L;
        } else if (-1L == (long)transmit.nOutBufferSize) {
            lRetval = 14L;
        } else if (0 == transmit.nInBufferSize) {
            lRetval = -2146435068L;
        } else if (transmit.abyInBuffer.length < transmit.nInBufferSize) {
            lRetval = -2146435068L;
        } else if (transmit.nOutBufferSize < transmit.nBytesReturned && null != transmit.abyOutBuffer) {
            lRetval = -2146435068L;
        } else {
            layerInterface.IFD_SlotStatus(smartcard, readerExt);
            if (3 != Mode && smartcard.ReaderCapabilities.CurrentState < 4L) {
                lRetval = -2146434967L;
            } else {
                smartcard.MajorIoControlCode = (long)nControlCode;
                smartcard.MinorIoControlCode = 0L;
                smartcard.iorequest.RequestBuffer = ByteBuffer.wrap(transmit.abyInBuffer);
                smartcard.iorequest.RequestBufferLength = (long)transmit.nInBufferSize;
                smartcard.iorequest.ReplyBuffer = ByteBuffer.wrap(transmit.abyOutBuffer);
                smartcard.iorequest.ReplyBufferLength = (long)transmit.nOutBufferSize;
                smartcard.iorequest.Information = (long)transmit.nBytesReturned;
                lRetval = layerInterface.IFD_VendorCommand(smartcard, readerExt);
                if (0L != lRetval) {
                    if (-983L == lRetval && (nControlCode & 3211264) != 0) {
                        lRetval = -2146435068L;
                    } else if (-983L == lRetval) {
                        lRetval = -2146435068L;
                    } else {
                        lRetval = this.RM_ReturnStatus(lRetval);
                    }
                } else {
                    transmit.nBytesReturned = (int)smartcard.iorequest.Information;
                    if (transmit.nBytesReturned > 0) {
                        System.arraycopy(smartcard.iorequest.ReplyBuffer.array(), 0, transmit.abyOutBuffer, 0, (int)smartcard.iorequest.Information);
                    }
                }
            }
        }

        return lRetval;
    }

    long RM_SCardStatus(SCard.SCardState cState) {
        Log.i("SCard Library", "----- RM_SCardStatus -----");
        long lRetval = -2146435052L;
        cState.szReader = Reader;
        lRetval = this.RM_GetSlotStatus(smartcard, readerExt);
        if (3 == Mode) {
            if (-2146435049L == lRetval) {
                return lRetval;
            }

            lRetval = 0L;
        } else if (0L != lRetval) {
            return lRetval;
        }

        cState.nState = (int)smartcard.ReaderCapabilities.CurrentState;
        cState.nProtocol = (int)smartcard.CardCapabilities.protocol.Selected;
        if (atrlen != 0) {
            cState.nATRlen = atrlen;
            System.arraycopy(atr, 0, cState.abyATR, 0, cState.nATRlen);
        } else {
            cState.nATRlen = 0;
        }

        return lRetval;
    }

    long RM_SCardGetAttrib(SCard.SCardAttribute attr) {
        Log.i("SCard Library", "----- RM_SCardGetAttrib -----");
        long lRetval = 0L;
        Long lValOut = -1L;
        byte[] szTemp = null;
        int dwRetLength = 0;
        if (3 == Mode && (attr.nAttrId == WinDefs.SCARD_ATTR_ATR_STRING || attr.nAttrId == WinDefs.SCARD_ATTR_CURRENT_BWT || attr.nAttrId == WinDefs.SCARD_ATTR_CURRENT_CLK || attr.nAttrId == WinDefs.SCARD_ATTR_CURRENT_CWT || attr.nAttrId == WinDefs.SCARD_ATTR_CURRENT_D || attr.nAttrId == WinDefs.SCARD_ATTR_CURRENT_EBC_ENCODING || attr.nAttrId == WinDefs.SCARD_ATTR_CURRENT_F || attr.nAttrId == WinDefs.SCARD_ATTR_CURRENT_IFSC || attr.nAttrId == WinDefs.SCARD_ATTR_CURRENT_IFSD || attr.nAttrId == WinDefs.SCARD_ATTR_CURRENT_N || attr.nAttrId == WinDefs.SCARD_ATTR_CURRENT_PROTOCOL_TYPE || attr.nAttrId == WinDefs.SCARD_ATTR_CURRENT_W || attr.nAttrId == WinDefs.SCARD_ATTR_ICC_TYPE_PER_ATR)) {
            lRetval = 22L;
        } else {
            if (attr.nAttrId == Defs.SCARD_ATTR_VENDOR_NAME) {
                dwRetLength = smartcard.VendorAttr.vendorName.Length;
                szTemp = new byte[dwRetLength];
                System.arraycopy(smartcard.VendorAttr.vendorName.Buffer, 0, szTemp, 0, smartcard.VendorAttr.vendorName.Length);
            } else if (attr.nAttrId == Defs.SCARD_ATTR_VENDOR_IFD_TYPE) {
                dwRetLength = smartcard.VendorAttr.ifdType.Length;
                szTemp = new byte[dwRetLength];
                System.arraycopy(smartcard.VendorAttr.ifdType.Buffer, 0, szTemp, 0, smartcard.VendorAttr.ifdType.Length);
            } else if (attr.nAttrId == Defs.SCARD_ATTR_VENDOR_IFD_VERSION) {
                dwRetLength = 4;
                szTemp = new byte[dwRetLength];
                szTemp[3] = (byte)smartcard.VendorAttr.ifdVersion.VersionMajor;
                szTemp[2] = (byte)smartcard.VendorAttr.ifdVersion.VersionMinor;
                szTemp[1] = (byte)Defs.HIBYTE(smartcard.VendorAttr.ifdVersion.BuildNumber);
                szTemp[0] = (byte)Defs.LOBYTE(smartcard.VendorAttr.ifdVersion.BuildNumber);
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CHANNEL_ID) {
                lValOut = 2097152L;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_PROTOCOL_TYPES) {
                lValOut = smartcard.ReaderCapabilities.SupportedProtocols;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_DEFAULT_CLK) {
                lValOut = smartcard.ReaderCapabilities.frequency.Default;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_MAX_CLK) {
                lValOut = smartcard.ReaderCapabilities.frequency.Max;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_DEFAULT_DATA_RATE) {
                lValOut = smartcard.ReaderCapabilities.dataRate.Default;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_MAX_DATA_RATE) {
                lValOut = smartcard.ReaderCapabilities.dataRate.Max;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_MAX_IFSD) {
                lValOut = smartcard.ReaderCapabilities.MaxIFSD;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_POWER_MGMT_SUPPORT) {
                lValOut = smartcard.ReaderCapabilities.PowerMgmtSupport;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CHARACTERISTICS) {
                lValOut = 0L;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CURRENT_PROTOCOL_TYPE) {
                lValOut = smartcard.CardCapabilities.protocol.Selected;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CURRENT_CLK) {
                lValOut = smartcard.CardCapabilities.PtsData.CLKFrequency;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CURRENT_F) {
                lValOut = Defs.sClockRateConversion[smartcard.CardCapabilities.Fl][0];
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CURRENT_D) {
                lValOut = Defs.sBitRateAdjustment[smartcard.CardCapabilities.Dl][0];
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CURRENT_N) {
                lValOut = (long)smartcard.CardCapabilities.N;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CURRENT_W) {
                lValOut = (long)smartcard.CardCapabilities.t0.WI;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CURRENT_IFSC) {
                lValOut = (long)smartcard.CardCapabilities.t1.IFSC;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CURRENT_IFSD) {
                lValOut = (long)smartcard.T1.IFSD;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CURRENT_BWT) {
                lValOut = (long)smartcard.CardCapabilities.t1.BWI;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CURRENT_CWT) {
                lValOut = (long)smartcard.CardCapabilities.t1.CWI;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_CURRENT_EBC_ENCODING) {
                lValOut = (long)smartcard.CardCapabilities.t1.EDC;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_ICC_PRESENCE) {
                dwRetLength = 1;
                szTemp = new byte[dwRetLength];
                szTemp[0] = 0;
                if (smartcard.ReaderCapabilities.CurrentState >= 2L) {
                    szTemp[0] = 2;
                }
            } else if (attr.nAttrId == Defs.SCARD_ATTR_ICC_INTERFACE_STATUS) {
                dwRetLength = 1;
                szTemp = new byte[dwRetLength];
                szTemp[0] = 0;
                if (smartcard.ReaderCapabilities.CurrentState > 3L) {
                    szTemp[0] = -1;
                }
            } else if (attr.nAttrId == Defs.SCARD_ATTR_ATR_STRING) {
                dwRetLength = smartcard.CardCapabilities.atr.Length;
                szTemp = new byte[dwRetLength];
                System.arraycopy(smartcard.CardCapabilities.atr.Buffer, 0, szTemp, 0, smartcard.CardCapabilities.atr.Length);
            } else if (attr.nAttrId == Defs.SCARD_ATTR_ICC_TYPE_PER_ATR) {
                dwRetLength = 1;
                szTemp = new byte[dwRetLength];
                szTemp[0] = (byte)((smartcard.CardCapabilities.protocol.Selected & 3L) != 0L ? 1 : 0);
            } else if (attr.nAttrId == Defs.SCARD_ATTR_DEVICE_UNIT) {
                lValOut = (long)smartcard.VendorAttr.UnitNo;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_DEVICE_IN_USE) {
                lRetval = 50L;
            } else if (attr.nAttrId == Defs.SCARD_ATTR_DEVICE_FRIENDLY_NAME) {
                dwRetLength = Reader.length();
                szTemp = new byte[dwRetLength];
                szTemp = Reader.getBytes();
            } else if (attr.nAttrId == Defs.SCARD_ATTR_DEVICE_SYSTEM_NAME) {
                dwRetLength = Reader.length();
                szTemp = new byte[dwRetLength];
                szTemp = Reader.getBytes();
            } else if (attr.nAttrId == Defs.SCARD_ATTR_VENDOR_IFD_SERIAL_NO) {
                dwRetLength = smartcard.VendorAttr.ifdSerialNo.Length;
                szTemp = new byte[dwRetLength];
                System.arraycopy(smartcard.VendorAttr.ifdSerialNo.Buffer, 0, szTemp, 0, smartcard.VendorAttr.ifdSerialNo.Length);
            } else {
                lRetval = 22L;
            }

            if (50L != lRetval && lRetval != 22L) {
                if (lValOut != -1L) {
                    dwRetLength = 4;
                }

                if (lValOut != -1L) {
                    attr.nAttrLen = dwRetLength;
                    attr.abyAttr = new byte[dwRetLength];
                    attr.abyAttr[3] = (byte)((int)(lValOut & 255L));
                    attr.abyAttr[2] = (byte)((int)(lValOut >> 8 & 255L));
                    attr.abyAttr[1] = (byte)((int)(lValOut >> 16 & 255L));
                    attr.abyAttr[0] = (byte)((int)(lValOut >> 24 & 255L));
                } else {
                    attr.nAttrLen = dwRetLength;
                    attr.abyAttr = new byte[dwRetLength];
                    System.arraycopy(szTemp, 0, attr.abyAttr, 0, dwRetLength);
                }
            }
        }

        return lRetval;
    }

    long RM_SCardSetAttrib(SCard.SCardAttribute attr) {
        Log.i("SCard Library", "----- RM_SCardSetAttrib -----");
        long lRetval = -2146435052L;
        IFDLayerInterface layerInterface = new IFDLayerInterface();
        if (-1L == (long)attr.nAttrId) {
            lRetval = 50L;
        } else {
            lRetval = layerInterface.IFD_SlotStatus(smartcard, readerExt);
            if (0L == lRetval) {
                if (smartcard.ReaderCapabilities.CurrentState <= 1L) {
                    lRetval = -2146434967L;
                } else if (attr.nAttrId != Defs.SCARD_ATTR_SUPRESS_T1_IFS_REQUEST) {
                    lRetval = 50L;
                }
            }
        }

        return lRetval;
    }

    long RM_SCardGetStatusChange(final long dwTimeout, final SCard.SCARD_READERSTATE[] rgReaderStates, final int cReaders) {
        Log.i("SCard Library", "----- RM_SCardGetStatusChange -----");
        long lStatus = -2146435062L;
        final Thread t = Thread.currentThread();
        Thread status = new Thread(new Runnable() {
            public void run() {
                long lRetVal = 0L;
                boolean bIsNewHandle = false;
                IFDLayerInterface layerInterface = new IFDLayerInterface();
                int time = 0;

                int nTimeoutx;
                do {
                    if (dwTimeout == -1L) {
                        nTimeoutx = 101;
                        time = -2;
                    } else {
                        nTimeoutx = (int)dwTimeout;
                    }

                    for(int i = 0; i < cReaders; ++i) {
                        if ((rgReaderStates[i].nCurrentState & 1) == 0) {
                            bIsNewHandle = false;
                            if (rgReaderStates[i].pvUserData[0] == 1) {
                                lRetVal = layerInterface.IFD_Init(ResourceManager.smartcard, ResourceManager.readerExt);
                                if (0L != lRetVal || -1L == ResourceManager.readerExt.handle) {
                                    ResourceManager.this.bIsStateChanged = true;
                                    rgReaderStates[i].nEventState = 10;
                                    break;
                                }

                                bIsNewHandle = true;
                            }

                            lRetVal = ResourceManager.this.RM_GetSlotStatus(ResourceManager.smartcard, ResourceManager.readerExt);
                            int dwCardStatus;
                            if (ResourceManager.smartcard.ReaderCapabilities.CurrentState <= 1L) {
                                dwCardStatus = 528;
                            } else {
                                if (3 == ResourceManager.Mode) {
                                    dwCardStatus = 8;
                                } else {
                                    dwCardStatus = ResourceManager.this.RM_FinalizeCardStatus((int)ResourceManager.smartcard.ReaderCapabilities.CurrentState, lRetVal);
                                }

                                if (6L != ResourceManager.smartcard.ReaderCapabilities.CurrentState && 3 != ResourceManager.Mode) {
                                    ResourceManager.smartcard.MinorIoControlCode = 2L;
                                    bIsNewHandle = true;
                                    lRetVal = layerInterface.IFD_PowerControl(ResourceManager.smartcard, ResourceManager.readerExt);
                                    if (0L != lRetVal) {
                                        dwCardStatus = 32;
                                    } else {
                                        ResourceManager.smartcard.MinorIoControlCode = 3L;
                                        lRetVal = layerInterface.IFD_SetProtocol(ResourceManager.smartcard, ResourceManager.readerExt);
                                        dwCardStatus = ResourceManager.this.RM_FinalizeCardStatus((int)ResourceManager.smartcard.ReaderCapabilities.CurrentState, lRetVal);
                                        if (0L != lRetVal) {
                                            dwCardStatus = 544;
                                        }

                                        ResourceManager.atrlen = ResourceManager.smartcard.CardCapabilities.atr.Length;
                                        if (ResourceManager.atrlen != 0) {
                                            ResourceManager.atr = new byte[ResourceManager.atrlen];
                                        }

                                        System.arraycopy(ResourceManager.smartcard.CardCapabilities.atr.Buffer, 0, ResourceManager.atr, 0, ResourceManager.atrlen);
                                    }
                                }

                                if (ResourceManager.atrlen != 0) {
                                    rgReaderStates[i].abyAtr = new byte[ResourceManager.atrlen];
                                    System.arraycopy(ResourceManager.atr, 0, rgReaderStates[i].abyAtr, 0, ResourceManager.atrlen);
                                    rgReaderStates[i].nAtr = ResourceManager.atrlen;
                                }

                                if (bIsNewHandle) {
                                    ResourceManager.smartcard.MinorIoControlCode = 0L;
                                    lRetVal = layerInterface.IFD_PowerControl(ResourceManager.smartcard, ResourceManager.readerExt);
                                    if (0L != lRetVal) {
                                        dwCardStatus = 544;
                                        ResourceManager.this.bIsStateChanged = true;
                                    }
                                }
                            }

                            ResourceManager.this.bIsStateChanged = ResourceManager.this.RM_ProcessCurrentState(rgReaderStates[i].nCurrentState, dwCardStatus);
                            if (ResourceManager.this.bIsStateChanged) {
                                rgReaderStates[i].nEventState = 2 | dwCardStatus;
                                lRetVal = 0L;
                            } else {
                                rgReaderStates[i].nEventState = dwCardStatus;
                            }
                            break;
                        }
                    }

                    if (ResourceManager.this.bIsStateChanged) {
                        break;
                    }

                    try {
                        if (nTimeoutx > 100) {
                            Thread.sleep(100L);
                            time += 100;
                        } else {
                            Thread.sleep((long)nTimeoutx);
                            time += nTimeoutx;
                        }
                    } catch (InterruptedException var11) {
                        var11.printStackTrace();
                    }
                } while(time < nTimeoutx);

                synchronized(t) {
                    t.notify();
                }
            }
        });
        status.start();

        try {
            synchronized(t) {
                t.wait();
            }
        } catch (Exception var12) {
            var12.printStackTrace();
        }

        if (this.bIsStateChanged) {
            lStatus = 0L;
        }

        return lStatus;
    }

    long RM_GetSlotState(SCard.SCARD_READERSTATE[] rgReaderStates) {
        Log.i("SCard Library", "----- RM_GetSlotState -----");
        long lStatus = 0L;
        long lRetVal = 0L;
        IFDLayerInterface layerInterface = new IFDLayerInterface();
        if (1 == rgReaderStates[0].pvUserData[0]) {
            lRetVal = layerInterface.IFD_Init(smartcard, readerExt);
            if (0L != lRetVal || -1L == readerExt.handle) {
                rgReaderStates[0].nEventState = 10;
            }
        } else {
            lRetVal = this.RM_GetSlotStatus(smartcard, readerExt);
            short dwCardStatus;
            if (smartcard.ReaderCapabilities.CurrentState <= 1L) {
                dwCardStatus = 528;
            } else {
                dwCardStatus = 32;
            }

            rgReaderStates[0].nEventState = dwCardStatus;
        }

        return lStatus;
    }

    void RM_UpdateReaderNameInDB(SCard.RdrDetails det) {
//        SmcLib scmlib = new SmcLib();
//        IFDLayer layer = new IFDLayer();
//        scmlib.getClass();
//        smartcard = new SmcLib.SMARTCARD_EXTENSION(scmlib);
//        layer.getClass();
//        readerExt = new IFDLayer.READER_EXTENSION(layer);
//        smartcard.VendorAttr.vendorName.Buffer = det.MString.getBytes();
//        smartcard.VendorAttr.vendorName.Length = det.MString.length();
//        smartcard.VendorAttr.ifdType.Buffer = det.RString.getBytes();
//        smartcard.VendorAttr.ifdType.Length = det.RString.length();
//        smartcard.VendorAttr.UnitNo = det.unitno;
//        smartcard.VendorAttr.ifdSerialNo.Length = det.serialno.length();
//        smartcard.VendorAttr.ifdSerialNo.Buffer = det.serialno.getBytes();
//        smartcard.VendorAttr.ifdVersion.VersionMajor = det.nVerMajor;
//        smartcard.VendorAttr.ifdVersion.VersionMinor = det.nVerMinor;
//
//        for(int i = 0; i < 4; ++i) {
//            SmcLib.SCARD_READER_CAPABILITIES var10000 = smartcard.ReaderCapabilities;
//            var10000.MaxIFSD += ((long)det.byMaxIFSD[i] & 255L) << 8 * i;
//            var10000 = smartcard.ReaderCapabilities;
//            var10000.SupportedProtocols += ((long)det.byProtocols[i] & 255L) << 8 * i;
//            SmcLib.SCARD_READER_CAPABILITIES.CLKFrequency var5 = smartcard.ReaderCapabilities.frequency;
//            var5.Default += ((long)det.byDefClk[i] & 255L) << 8 * i;
//            var5 = smartcard.ReaderCapabilities.frequency;
//            var5.Max += ((long)det.byMaxClk[i] & 255L) << 8 * i;
//            SmcLib.SCARD_READER_CAPABILITIES.DataRate var6 = smartcard.ReaderCapabilities.dataRate;
//            var6.Default += ((long)det.byDefDataRate[i] & 255L) << 8 * i;
//            var6 = smartcard.ReaderCapabilities.dataRate;
//            var6.Max += ((long)det.byMaxDataRate[i] & 255L) << 8 * i;
//        }

    }
}
