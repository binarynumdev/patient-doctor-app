//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.consulmedics.patientdata.scardlib;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

public class SCard {
    private static final String TAG = "SCard Library";
    private static UsbManager mManager = null;
    private static UsbDeviceConnection mConnection = null;
    private static UsbEndpoint mEndpointOut = null;
    private static UsbEndpoint mEndpointIn = null;
    private static UsbEndpoint mEndpointInterrupt = null;
    private static UsbInterface mInterface = null;
    private static int nReaders;
    static boolean bAPDURdrType = false;
    static int nMaxCCIDLen = 0;
    private static PendingIntent mPermissionIntent;
    private static ArrayList<RdrDetails> details;
    public static Long difference = new Long(0L);
    private static final String ACTION_USB_PERMISSION = "com.android.scard.USB_PERMISSION";
    private static ArrayList<UsbDevice> UsbRdrList;
    private static int gnRdrPos = 0;
    private SCardListener listener = null;
    private Thread listenerThread = null;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("usb-receiver", "Broadcast Receiver. ONReceive >");
            String action = intent.getAction();
            if ("com.android.scard.USB_PERMISSION".equals(action)) {
                synchronized(this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                    if (intent.getBooleanExtra("permission", false)) {
                        if (device != null) {
                            Log.d("usb-receiver", "permission granted for device " + device);
                        }
                    } else {
                        Log.d("usb-receiver", "permission denied for device " + device);
                    }
                }

                if (SCard.gnRdrPos != -1) {
                    SCard.gnRdrPos = SCard.gnRdrPos + 1;
                    if (SCard.gnRdrPos + 1 > SCard.UsbRdrList.size()) {
                        SCard.gnRdrPos = -1;
                    }

                    Log.d("usb-receiver", "position is : " + SCard.gnRdrPos);
                    if (SCard.gnRdrPos != -1) {
                        Log.d("usb-receiver", "Calling reader " + SCard.UsbRdrList.get(SCard.gnRdrPos));
                        SCard.this.USBGetPermission(context);
                    }
                }
            }

            Log.d("usb-receiver", "Broadcast Receiver. ONReceive <");
        }
    };

    public SCard() {
    }

    public void setSCardListener(final Context context, final String reader, final SCardListener listener) {
        Log.d("SCardListener", "setSCardListener " + reader);
        this.listener = listener;
        if (null != this.listener) {
            if (null == this.listenerThread) {
                this.listenerThread = new Thread(new Runnable() {
                    public void run() {
                        long status = SCard.this.SCardEstablishContext(context);
                        int prevState = 0;
                        if (0L == status) {
                            SCARD_READERSTATE[] rs = new SCARD_READERSTATE[]{SCard.this.new SCARD_READERSTATE()};
                            rs[0].setSzReader(reader);
                            rs[0].setnEventState(0);
                            rs[0].setnCurrentState(0);

                            while(true) {
                                try {
                                    status = SCard.this.SCardGetSlotState(rs);
                                    if (0L == status) {
                                        int cardState = rs[0].getnEventState();
                                        if (16 == (cardState & 16)) {
                                            if (prevState != 16) {
                                                listener.onCardEvent(0);
                                                prevState = 16;
                                            }
                                        } else if (32 == (cardState & 32) && prevState != 32) {
                                            listener.onCardEvent(1);
                                            prevState = 32;
                                        }
                                    } else {
                                        if (-2146435062L != status) {
                                            Log.d("SCardListener thread", "status: " + status + " aborting");
                                            throw new InterruptedException();
                                        }

                                        Log.d("SCardListener thread", "timeout");
                                    }

                                    Thread.sleep(1000L);
                                } catch (InterruptedException var6) {
                                    Log.d("SCardListener thread", "exception");
                                    return;
                                }
                            }
                        }
                    }
                });
                this.listenerThread.start();
            }
        } else if (null != this.listenerThread) {
            this.listenerThread.interrupt();
            this.listenerThread = null;
        }

    }

    private long USBGetPermission(Context context) {
        long lRetval = 0L;
        Log.d("usb-get-perm", "USBGetPermission >");
        if (context == null) {
            lRetval = -2146435068L;
            Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
        } else if (gnRdrPos != -1) {
            Log.d("usb-get-perm", "Asking for >" + UsbRdrList.get(gnRdrPos));
            mManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
            mManager.requestPermission((UsbDevice)UsbRdrList.get(gnRdrPos), mPermissionIntent);

            try {
                Thread.sleep(2000L);
                Log.d("usb-get-perm", "Woke up");
            } catch (InterruptedException var5) {
                Log.d("usb-get-perm", "Crash");
                var5.printStackTrace();
                lRetval = -2146435055L;
            }
        }

        Log.d("usb-get-perm", "USBGetPermission <. " + lRetval);
        return lRetval;
    }

    public long USBRequestPermission(Context context) {
        long lRetval = 0L;
        if (context == null) {
            lRetval = -2146435068L;
            Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
            return lRetval;
        } else {
            mManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
            mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.android.scard.USB_PERMISSION"), PendingIntent.FLAG_IMMUTABLE);
            IntentFilter filter = new IntentFilter("com.android.scard.USB_PERMISSION");
            filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
            filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
            context.registerReceiver(this.mUsbReceiver, filter);
            UsbRdrList = new ArrayList();
            UsbRdrList.clear();
            Iterator var5 = mManager.getDeviceList().values().iterator();

            while(true) {
                UsbDevice device;
                do {
                    if (!var5.hasNext()) {
                        if (!UsbRdrList.isEmpty()) {
                            gnRdrPos = 0;
                            Log.d("GetPermission", "Calling reader " + UsbRdrList.get(gnRdrPos));
                            lRetval = this.USBGetPermission(context);
                        }

                        return lRetval;
                    }

                    device = (UsbDevice)var5.next();
                    Log.i("GetPermission", "Found device: " + device);
                } while(device.getVendorId() != 1254 && device.getVendorId() != 8186);

                UsbRdrList.add(device);
                Log.i("GetPermission", "Adding device: " + device);
            }
        }
    }

    public long SCardEstablishContext(Context context) {
        Log.i("SCard Library", "<< SCardEstablishContext >>" + context);
        long lRetval = 0L;
        if (context == null) {
            lRetval = -2146435068L;
            Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
        } else {
            mManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
            if (mManager == null) {
                lRetval = -2146435069L;
                Log.e("SCard Library", "SCARD_E_INVALID_HANDLE");
            }
        }

        Log.d("SCard Library", "Passed - " + mManager);
        return lRetval;
    }

    public long SCardListReaders(Context context, ArrayList<String> deviceList) {
        Log.i("SCard Library", "<< SCardListReaders >>");
        Log.d("SCard Library", "Parameters - 1. " + context + "2. " + deviceList);
        long lRetval = 0L;
        details = new ArrayList();
        if (context == null) {
            lRetval = -2146435068L;
            Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
        } else if (mManager == null) {
            lRetval = -2146435069L;
            Log.e("SCard Library", "SCARD_E_INVALID_HANDLE - 1");
        } else {
            Log.d("SCard Library", "" + mManager.getDeviceList().isEmpty());
            Log.d("SCard Library", "" + mManager.getDeviceList().size());
            if (mManager.getDeviceList().isEmpty()) {
                lRetval = -2146435026L;
                Log.e("SCard Library", "SCARD_E_NO_READERS_AVAILABLE-1");
            } else if (deviceList == null) {
                lRetval = -2146435064L;
                Log.e("SCard Library", "SCARD_E_INSUFFICIENT_BUFFER-1");
            } else {
                deviceList.clear();
                Iterator var5 = mManager.getDeviceList().values().iterator();

                label110:
                while(true) {
                    while(true) {
                        UsbDevice device;
                        String deviceName;
                        UsbDeviceConnection connection;
                        do {
                            if (!var5.hasNext()) {
                                if (lRetval == 0L && !mManager.getDeviceList().isEmpty() && deviceList.isEmpty()) {
                                    lRetval = -2146435026L;
                                    Log.e("SCard Library", "SCARD_E_NO_READERS_AVAILABLE - 2");
                                }
                                break label110;
                            }

                            device = (UsbDevice)var5.next();
                            Log.d("SCard Library", "Device - " + device.getDeviceName() + "Device ID - " + device.getDeviceId() + "Device VID - " + device.getVendorId());
                            deviceName = null;
                            connection = null;
                            int index = 0;
                        } while(device.getVendorId() != 1254 && device.getVendorId() != 8186);

                        if (!mManager.hasPermission(device)) {
                            Log.d("SCard Library", device + " does not have permission.. skipping");
                        } else {
                            connection = mManager.openDevice(device);
                            Log.d("SCard Library", "Connection - " + connection);
                            if (connection == null) {
                                lRetval = -2146435069L;
                                Log.e("SCard Library", "SCARD_E_INVALID_HANDLE - " + lRetval);
                            } else {
                                byte[] rawBuf = new byte[255];
                                rawBuf = connection.getRawDescriptors();
                                int start = 27;

                                for(int i = 0; i < device.getInterfaceCount(); ++i) {
                                    RdrDetails rdetails = new RdrDetails();
                                    rdetails.rDevice = device;
                                    UsbInterface intf = device.getInterface(i);
                                    rdetails.rIntf = intf;
                                    if (intf.getInterfaceClass() == 11 && intf.getInterfaceSubclass() == 0) {
                                        int index;
                                        if (device.getInterfaceCount() == 1) {
                                            index = 770;
                                        } else {
                                            index = 768 | rawBuf[start + 8];
                                        }

                                        byte[] ManuStringBuf = new byte[255];
                                        int len = connection.controlTransfer(128, 6, 769, 1033, ManuStringBuf, 255, 5000);
                                        if (len < 0) {
                                            lRetval = 22L;
                                            Log.e("SCard Library", "ERROR_BAD_COMMAND");
                                            break;
                                        }

                                        ManuStringBuf = Arrays.copyOfRange(ManuStringBuf, 2, len);
                                        deviceName = new String(ManuStringBuf);
                                        rdetails.MString = new String(ManuStringBuf);
                                        byte[] RdrStringBuf = new byte[255];
                                        len = connection.controlTransfer(128, 6, index, 1033, RdrStringBuf, 255, 5000);
                                        if (len < 0) {
                                            lRetval = 22L;
                                            Log.e("SCard Library", "ERROR_BAD_COMMAND");
                                            break;
                                        }

                                        RdrStringBuf = Arrays.copyOfRange(RdrStringBuf, 2, len);
                                        rdetails.RString = new String(RdrStringBuf);
                                        int j = 0;

                                        for(deviceName = deviceName + " " + new String(RdrStringBuf) + " " + j; deviceList.contains(deviceName); deviceName = deviceName + " " + new String(RdrStringBuf) + " " + j) {
                                            ++j;
                                        }

                                        deviceList.add(deviceName);
                                        rdetails.RdrName = new String(deviceName);
                                        rdetails.unitno = (short)j;
                                        Log.d("SCard Library", "Device Name - " + deviceName);
                                        index = 768 | rawBuf[16];
                                        byte[] serialNo = new byte[30];
                                        len = connection.controlTransfer(128, 6, index, 1033, serialNo, 30, 5000);
                                        if (len < 0) {
                                            lRetval = 22L;
                                            Log.e("SCard Library", "ERROR_BAD_COMMAND");
                                            break;
                                        }

                                        serialNo = Arrays.copyOfRange(serialNo, 2, len);
                                        rdetails.serialno = new String(serialNo);
                                        rdetails.nVerMajor = (short)rawBuf[13];
                                        rdetails.nVerMinor = (short)rawBuf[12];
                                        rdetails.byProtocols = new byte[]{rawBuf[42], rawBuf[43], rawBuf[44], rawBuf[45]};
                                        rdetails.byDefClk = new byte[]{rawBuf[46], rawBuf[47], rawBuf[48], rawBuf[49]};
                                        rdetails.byMaxClk = new byte[]{rawBuf[50], rawBuf[51], rawBuf[52], rawBuf[53]};
                                        rdetails.byDefDataRate = new byte[]{rawBuf[55], rawBuf[56], rawBuf[57], rawBuf[58]};
                                        rdetails.byMaxDataRate = new byte[]{rawBuf[59], rawBuf[60], rawBuf[61], rawBuf[62]};
                                        rdetails.byMaxIFSD = new byte[]{rawBuf[64], rawBuf[65], rawBuf[66], rawBuf[67]};
                                        if (rawBuf[start + 51] == 4) {
                                            bAPDURdrType = true;
                                        }

                                        rdetails.rMode = bAPDURdrType;

                                        for(int k = 0; k < 4; ++k) {
                                            nMaxCCIDLen = (int)((long)nMaxCCIDLen + (((long)rawBuf[start + 53 + k] & 255L) << 8 * k));
                                        }

                                        rdetails.nCCIDLen = nMaxCCIDLen;
                                        start += 84;
                                    } else {
                                        start += 9 + rawBuf[9 + start] + intf.getEndpointCount() * 7;
                                    }

                                    details.add(rdetails);
                                }
                            }
                        }
                    }
                }
            }
        }

        for(int k = 0; k < deviceList.size(); ++k) {
            Log.d("SCard Library", (String)deviceList.get(k));
        }

        nReaders = deviceList.size();
        return lRetval;
    }

    private void defineEndPoints(UsbInterface mIntf) {
        Log.i("SCard Library", "<< defineEndPoints >>");
        UsbEndpoint epOut = null;
        UsbEndpoint epIn = null;
        UsbEndpoint epInt = null;

        for(int i = 0; i < mIntf.getEndpointCount(); ++i) {
            UsbEndpoint ep = mIntf.getEndpoint(i);
            if (ep.getType() == 2) {
                if (ep.getDirection() == 0) {
                    epOut = ep;
                } else {
                    epIn = ep;
                }
            } else if (ep.getType() == 3) {
                epInt = ep;
            }
        }

        if (epOut != null && epIn != null) {
            mEndpointOut = epOut;
            mEndpointIn = epIn;
            mEndpointInterrupt = epInt;
        } else {
            throw new IllegalArgumentException("not all endpoints found");
        }
    }

    ByteBuffer UsbTransfer(byte[] inBuffer) {
        Log.i("SCard Library", "<< UsbTransfer >>");
        int len = 0;
        byte[] outbuffer = new byte[300];
        long lStartTime = (new Date()).getTime();
        synchronized(Thread.currentThread()) {
            len = mConnection.bulkTransfer(mEndpointOut, inBuffer, inBuffer.length, 5000);
            if (len < 0) {
                return null;
            }

            StringBuilder str = new StringBuilder();
            int i = 0;

            label49:
            while(true) {
                if (i >= len) {
                    Log.d("SCard Library", str.toString());

                    while(true) {
                        len = mConnection.bulkTransfer(mEndpointIn, outbuffer, outbuffer.length, 5000);
                        if (len < 0) {
                            return null;
                        }

                        if (len != 10 || (outbuffer[7] & 255) != 128) {
                            break label49;
                        }
                    }
                }

                str.append(String.format("%02x ", inBuffer[i]));
                ++i;
            }
        }

        StringBuilder str = new StringBuilder();

        for(int i = 0; i < len; ++i) {
            str.append(String.format("%02x ", outbuffer[i]));
        }

        Log.d("SCard Library", str.toString());
        long lEndTime = (new Date()).getTime();
        difference = difference + lEndTime - lStartTime;
        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.put(outbuffer, 0, len);
        return buffer;
    }

    public long SCardConnect(String RdrName, int nMode, int nPreferredProtocols) {
        Log.i("SCard Library", "<< SCardConnect >>");
        long lRetval = -2146435052L;
        ResourceManager resManager = new ResourceManager();
        int rdrIndex = 0;
        if (mManager == null) {
            lRetval = -2146435069L;
            Log.e("SCard Library", "SCARD_E_INVALID_HANDLE - 1");
        } else if (details.isEmpty()) {
            lRetval = -2146435063L;
        } else {
            while(rdrIndex < nReaders && !((RdrDetails)details.get(rdrIndex)).RdrName.equals(RdrName)) {
                ++rdrIndex;
            }

            if (rdrIndex == nReaders) {
                lRetval = -2146435049L;
                Log.e("SCard Library", "SCARD_E_READER_UNAVAILABLE");
            } else {
                UsbDevice device = ((RdrDetails)details.get(rdrIndex)).rDevice;
                mInterface = ((RdrDetails)details.get(rdrIndex)).rIntf;
                UsbDeviceConnection connection = mManager.openDevice(device);
                if (connection != null) {
                    if (connection.claimInterface(mInterface, true)) {
                        mConnection = connection;
                        this.defineEndPoints(mInterface);
                        resManager.RM_UpdateReaderNameInDB((RdrDetails)details.get(rdrIndex));
                        lRetval = resManager.RM_SCardConnect(RdrName, nMode, nPreferredProtocols);
                        if (lRetval != 0L) {
                            mConnection.releaseInterface(mInterface);
                            mConnection = null;
                            mEndpointIn = null;
                            mEndpointInterrupt = null;
                            mEndpointOut = null;
                            mInterface = null;
                        }
                    } else {
                        connection.close();
                    }
                } else {
                    lRetval = -2146435069L;
                    Log.e("SCard Library", "SCARD_E_INVALID_HANDLE - 2");
                }
            }
        }

        return lRetval;
    }

    public long SCardTransmit(SCardIOBuffer transmit) {
        Log.i("SCard Library", "<< SCardTransmit >>");
        long lRetval = -2146435052L;
        ResourceManager resManager = new ResourceManager();
        if (mManager != null && mConnection != null && mEndpointIn != null && mEndpointOut != null && mEndpointInterrupt != null && mInterface != null) {
            if (transmit == null) {
                lRetval = -2146435068L;
                Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
            } else {
                lRetval = resManager.RM_Transmit(transmit);
            }
        } else {
            lRetval = -2146435069L;
            Log.e("SCard Library", "SCARD_E_INVALID_HANDLE");
        }

        return lRetval;
    }

    public long SCardReconnect(int nMode, int nPreferredProtocols, int nInitialization) {
        Log.i("SCard Library", "<< SCardReconnect >>");
        long lRetval = -2146435052L;
        ResourceManager resManager = new ResourceManager();
        if (mManager != null && mConnection != null && mEndpointIn != null && mEndpointOut != null && mEndpointInterrupt != null && mInterface != null) {
            lRetval = resManager.RM_SCardReconnect(nMode, nPreferredProtocols, nInitialization);
            if (lRetval != 0L) {
                mConnection = null;
                mEndpointIn = null;
                mEndpointOut = null;
                mEndpointInterrupt = null;
                mInterface = null;
            }
        } else {
            lRetval = -2146435069L;
            Log.e("SCard Library", "SCARD_E_INVALID_HANDLE");
        }

        return lRetval;
    }

    public long SCardDisconnect(int nDisposition) {
        Log.i("SCard Library", "<< SCardDisconnect >>");
        long lRetval = 0L;
        ResourceManager resManager = new ResourceManager();
        if (mManager != null && mConnection != null && mEndpointIn != null && mEndpointOut != null && mEndpointInterrupt != null && mInterface != null) {
            if (nDisposition != 0 && nDisposition <= 4) {
                lRetval = resManager.RM_CloseCardHandle(nDisposition);
                mConnection.releaseInterface(mInterface);
                mConnection.close();
            } else {
                lRetval = -2146435068L;
                Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
            }
        } else {
            lRetval = -2146435069L;
            Log.e("SCard Library", "SCARD_E_INVALID_HANDLE");
        }

        mConnection = null;
        mEndpointIn = null;
        mEndpointOut = null;
        mEndpointInterrupt = null;
        mInterface = null;
        return lRetval;
    }

    public long SCardControl(int nControlCode, SCardIOBuffer transmit) {
        Log.i("SCard Library", "<< SCardControl >>");
        long lRetval = -2146435052L;
        ResourceManager resManager = new ResourceManager();
        if (mManager != null && mConnection != null && mEndpointIn != null && mEndpointOut != null && mEndpointInterrupt != null) {
            if (transmit != null && nControlCode == (int)WinDefs.IOCTL_CCID_ESCAPE) {
                lRetval = resManager.RM_SCardControl(nControlCode, transmit);
            } else {
                lRetval = -2146435068L;
                Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
            }
        } else {
            lRetval = -2146435069L;
            Log.e("SCard Library", "SCARD_E_INVALID_HANDLE");
        }

        return lRetval;
    }

    public long SCardStatus(SCardState cState) {
        Log.i("SCard Library", "<< SCardStatus >>");
        long lRetval = 0L;
        ResourceManager resManager = new ResourceManager();
        if (mManager != null && mConnection != null && mEndpointIn != null && mEndpointOut != null) {
            if (cState == null) {
                lRetval = -2146435068L;
                Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
            } else {
                lRetval = resManager.RM_SCardStatus(cState);
            }
        } else {
            lRetval = -2146435069L;
            Log.e("SCard Library", "SCARD_E_INVALID_HANDLE");
        }

        return lRetval;
    }

    public long SCardReleaseContext() {
        Log.i("SCard Library", "<< SCardReleaseContext >>");
        if (mManager != null) {
            mManager = null;
        }

        return 0L;
    }

    public long SCardGetStatusChange(long dwTimeout, SCARD_READERSTATE[] rgReaderStates, int cReaders) {
        Log.i("SCard Library", "<< SCardGetStatusChange >>");
        long lRetval = -2146435062L;
        ResourceManager resManager = new ResourceManager();
        int rdrIndex = 0;
        if (mManager == null) {
            lRetval = -2146435069L;
            Log.e("SCard Library", "SCARD_E_INVALID_HANDLE");
        } else if (rgReaderStates != null && cReaders == 1) {
            if (details.isEmpty()) {
                lRetval = -2146435063L;
            } else {
                while(rdrIndex < nReaders && !((RdrDetails)details.get(rdrIndex)).RdrName.equals(rgReaderStates[0].getSzReader())) {
                    ++rdrIndex;
                }

                if (rdrIndex == nReaders) {
                    lRetval = -2146435049L;
                    Log.e("SCard Library", "SCARD_E_READER_UNAVAILABLE");
                } else {
                    if (mConnection == null) {
                        mConnection = mManager.openDevice(((RdrDetails)details.get(rdrIndex)).rDevice);
                        if (mConnection != null) {
                            UsbInterface intf = ((RdrDetails)details.get(rdrIndex)).rIntf;
                            if (mConnection.claimInterface(intf, true)) {
                                this.defineEndPoints(intf);
                            }

                            resManager.RM_UpdateReaderNameInDB((RdrDetails)details.get(rdrIndex));
                        }

                        rgReaderStates[0].setPvUserData(new byte[]{1});
                    } else {
                        rgReaderStates[0].setPvUserData(new byte[]{0});
                    }

                    lRetval = resManager.RM_SCardGetStatusChange(dwTimeout, rgReaderStates, cReaders);
                }
            }
        } else {
            lRetval = -2146435068L;
            Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
        }

        return lRetval;
    }

    public long SCardGetSlotState(SCARD_READERSTATE[] rgReaderStates) {
        Log.i("SCard Library", "<< SCardGetSlotState >>");
        long lRetval = -2146435062L;
        ResourceManager resManager = new ResourceManager();
        int rdrIndex = 0;
        if (mManager == null) {
            lRetval = -2146435069L;
            Log.e("SCard Library", "SCARD_E_INVALID_HANDLE");
        } else if (details.isEmpty()) {
            lRetval = -2146435063L;
        } else {
            for(rdrIndex = 0; rdrIndex < nReaders && !((RdrDetails)details.get(rdrIndex)).RdrName.equals(rgReaderStates[0].getSzReader()); ++rdrIndex) {
            }

            if (rdrIndex == nReaders) {
                lRetval = -2146435049L;
                Log.e("SCard Library", "SCARD_E_READER_UNAVAILABLE");
            } else {
                if (mConnection == null) {
                    mConnection = mManager.openDevice(((RdrDetails)details.get(rdrIndex)).rDevice);
                    if (mConnection != null) {
                        UsbInterface intf = ((RdrDetails)details.get(rdrIndex)).rIntf;
                        if (mConnection.claimInterface(intf, true)) {
                            this.defineEndPoints(intf);
                        }

                        resManager.RM_UpdateReaderNameInDB((RdrDetails)details.get(rdrIndex));
                    }

                    rgReaderStates[0].setPvUserData(new byte[]{1});
                } else {
                    rgReaderStates[0].setPvUserData(new byte[]{0});
                }

                lRetval = resManager.RM_GetSlotState(rgReaderStates);
            }
        }

        return lRetval;
    }

    public long SCardGetAttrib(SCardAttribute attr) {
        Log.i("SCard Library", "<< SCardGetAttrib >>");
        long lRetval = 0L;
        ResourceManager resManager = new ResourceManager();
        if (mManager != null && mConnection != null && mEndpointIn != null && mEndpointOut != null) {
            if (attr == null) {
                lRetval = -2146435068L;
                Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
            } else {
                lRetval = resManager.RM_SCardGetAttrib(attr);
            }
        } else {
            lRetval = -2146435069L;
            Log.e("SCard Library", "SCARD_E_INVALID_HANDLE");
        }

        return lRetval;
    }

    long SCardSetAttrib(SCardAttribute attr) {
        Log.i("SCard Library", "<< SCardSetAttrib >>");
        long lRetval = 0L;
        ResourceManager resManager = new ResourceManager();
        if (mManager != null && mConnection != null && mEndpointIn != null && mEndpointOut != null) {
            if (attr == null) {
                lRetval = -2146435068L;
                Log.e("SCard Library", "SCARD_E_INVALID_PARAMETER");
            } else {
                lRetval = resManager.RM_SCardSetAttrib(attr);
            }
        } else {
            lRetval = -2146435069L;
            Log.e("SCard Library", "SCARD_E_INVALID_HANDLE");
        }

        return lRetval;
    }

    long SCardBeginTransaction() {
        return 0L;
    }

    long SCardEndTransaction(int dwDisposition) {
        return 0L;
    }

    public class SCardAttribute {
        int nAttrId;
        int nAttrLen;
        byte[] abyAttr;

        public SCardAttribute() {
        }

        public int getnAttrId() {
            return this.nAttrId;
        }

        public void setnAttrId(int nAttrId) {
            this.nAttrId = nAttrId;
        }

        public int getnAttrLen() {
            return this.nAttrLen;
        }

        public void setnAttrLen(int nAttrLen) {
            this.nAttrLen = nAttrLen;
        }

        public byte[] getAbyAttr() {
            return this.abyAttr;
        }

        public void setAbyAttr(byte[] abyAttr) {
            this.abyAttr = abyAttr;
        }
    }

    public class SCardState {
        String szReader;
        int nState;
        int nProtocol;
        byte[] abyATR = new byte[33];
        int nATRlen;

        public SCardState() {
        }

        public String getSzReader() {
            return this.szReader;
        }

        public void setSzReader(String szReader) {
            this.szReader = szReader;
        }

        public int getnState() {
            return this.nState;
        }

        public void setnState(int nState) {
            this.nState = nState;
        }

        public int getnProtocol() {
            return this.nProtocol;
        }

        public void setnProtocol(int nProtocol) {
            this.nProtocol = nProtocol;
        }

        public byte[] getAbyATR() {
            return this.abyATR;
        }

        public void setAbyATR(byte[] abyATR) {
            this.abyATR = abyATR;
        }

        public int getnATRlen() {
            return this.nATRlen;
        }

        public void setnATRlen(int nATRlen) {
            this.nATRlen = nATRlen;
        }
    }

    public class SCardIOBuffer {
        byte[] abyInBuffer;
        int nInBufferSize;
        byte[] abyOutBuffer;
        int nOutBufferSize;
        int nBytesReturned;

        public SCardIOBuffer() {
        }

        public byte[] getAbyInBuffer() {
            return this.abyInBuffer;
        }

        public void setAbyInBuffer(byte[] abyInBuffer) {
            this.abyInBuffer = abyInBuffer;
        }

        public int getnInBufferSize() {
            return this.nInBufferSize;
        }

        public void setnInBufferSize(int nInBufferSize) {
            this.nInBufferSize = nInBufferSize;
        }

        public byte[] getAbyOutBuffer() {
            return this.abyOutBuffer;
        }

        public void setAbyOutBuffer(byte[] abyOutBuffer) {
            this.abyOutBuffer = abyOutBuffer;
        }

        public int getnOutBufferSize() {
            return this.nOutBufferSize;
        }

        public void setnOutBufferSize(int nOutBufferSize) {
            this.nOutBufferSize = nOutBufferSize;
        }

        public int getnBytesReturned() {
            return this.nBytesReturned;
        }

        public void setnBytesReturned(int nBytesReturned) {
            this.nBytesReturned = nBytesReturned;
        }
    }

    class RdrDetails {
        UsbDevice rDevice;
        UsbInterface rIntf;
        boolean rMode;
        int nCCIDLen;
        String RdrName;
        String MString;
        String RString;
        short unitno;
        String serialno;
        short nVerMajor;
        short nVerMinor;
        byte[] byProtocols;
        byte[] byDefClk;
        byte[] byMaxClk;
        byte[] byDefDataRate;
        byte[] byMaxDataRate;
        byte[] byMaxIFSD;

        RdrDetails() {
        }
    }

    public class SCARD_READERSTATE {
        String szReader;
        byte[] pvUserData;
        int nCurrentState;
        int nEventState;
        int nAtr;
        byte[] abyAtr = new byte[36];

        public SCARD_READERSTATE() {
        }

        public String getSzReader() {
            return this.szReader;
        }

        public void setSzReader(String szReader) {
            this.szReader = szReader;
        }

        public byte[] getPvUserData() {
            return this.pvUserData;
        }

        public void setPvUserData(byte[] pvUserData) {
            this.pvUserData = pvUserData;
        }

        public int getnCurrentState() {
            return this.nCurrentState;
        }

        public void setnCurrentState(int nCurrentState) {
            this.nCurrentState = nCurrentState;
        }

        public int getnEventState() {
            return this.nEventState;
        }

        public void setnEventState(int nEventState) {
            this.nEventState = nEventState;
        }

        public int getnAtr() {
            return this.nAtr;
        }

        public void setnAtr(int nAtr) {
            this.nAtr = nAtr;
        }

        public byte[] getabyAtr() {
            return this.abyAtr;
        }

        public void setsbyAtr(byte[] abyAtr) {
            this.abyAtr = abyAtr;
        }
    }

    public interface SCardListener {
        void onCardEvent(int var1);
    }
}
