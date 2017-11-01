package com.iotdesignshop.brewcentral;

import android.net.Uri;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.in;

/**
 * Created by trentshumay on 2017-10-18.
 */

public class UARTManager {

    private final String TAG = "BrewCentral";
    private UartDevice mUARTDevice;
    private List<String> mDeviceList;
    private UartDeviceCallback mUartCallback;
    private UARTCommandListener mListener;

    public UARTManager()
    {
        PeripheralManagerService manager = new PeripheralManagerService();
        mDeviceList = manager.getUartDeviceList();

        if (mDeviceList.isEmpty()) {
            Log.w(TAG, "No UART port available on this device.");
        } else {
            Log.i(TAG, "List of available devices: " + mDeviceList);
        }
    }

    public void setListener(UARTCommandListener listener)
    {
        mListener = listener;
    }

    private String readUartBuffer(UartDevice uart)
    {
        // Maximum amount of data to read at one time
        final int maxCount = 256;
        byte[] buffer = new byte[maxCount];

        String result = new String();

        int count;
        try {
            count = mUARTDevice.read(buffer, buffer.length);
            if (count > 0)
            {
                result += new String(Arrays.copyOfRange(buffer,0,count));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }


    public boolean openDevice(String deviceName, int baudRate)
    {
        PeripheralManagerService manager = new PeripheralManagerService();
        try {
            mUARTDevice = manager.openUartDevice(deviceName);

            if (mUARTDevice != null)
            {
                mUARTDevice.setBaudrate(baudRate);
                mUARTDevice.setDataSize(8);
                mUARTDevice.setStopBits(1);
                mUARTDevice.setParity(UartDevice.PARITY_NONE);
                mUARTDevice.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_NONE);

                mUartCallback = new UartDeviceCallback() {
                    @Override
                    public boolean onUartDeviceDataAvailable(UartDevice uart) {
                        // Read available data from the UART device
                        String commandBuffer = readUartBuffer(uart);
                        //Log.d(TAG, "Received command:" + commandBuffer);

                        // Split on newlines in case multiple commands were received
                        String commands[] = commandBuffer.split("\\r?\\n");

                        for (String command : commands)
                        {
                            String[] params = command.split("\\s+");

                            if (mListener != null)
                            {
                                mListener.uartCommandReceived(params);
                            }
                        }



                        // Continue listening for more interrupts
                        return true;
                    }

                    @Override
                    public void onUartDeviceError(UartDevice uart, int error) {
                        Log.w(TAG, uart + ": Error event " + error);
                    }
                };

                mUARTDevice.registerUartDeviceCallback(mUartCallback);

                return true;
            }
            else
            {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


    }

    void closeDevice()
    {
        mUARTDevice.unregisterUartDeviceCallback(mUartCallback);
        try {
            mUARTDevice.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mUARTDevice = null;
    }

    public int writeData(byte[] buffer)
    {
        try {
            int bytes = mUARTDevice.write(buffer, buffer.length);

            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface UARTCommandListener {
        void uartCommandReceived(String[] parameters);
    }

}
