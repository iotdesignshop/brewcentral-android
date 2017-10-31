package com.iotdesignshop.brewcentral;

/**
 * Created by trentshumay on 2017-10-25.
 */

public class CommandManager {

    private UARTManager mUARTManager;

    public CommandManager(UARTManager uartManager)
    {
        mUARTManager = uartManager;
    }


    public void setMixerTemp(float temp)
    {
        // Send command to Arduino
        String command = "t-mix "+Float.toString(Utility.tempFToDegreesC(temp))+" 5.0 ";
        mUARTManager.writeData(command.getBytes());
    }

    public void setMixerFlow(float flow)
    {
        // Send command to Arduino
        String command = "f-mix "+Float.toString(Utility.volumeGaltoL(flow))+" 10.0 ";
        mUARTManager.writeData(command.getBytes());
    }

    public void setMashFlow(float flow)
    {
        // Send command to Arduino
        String command = "f-msh "+Float.toString(Utility.volumeGaltoL(flow))+" 10.0 ";
        mUARTManager.writeData(command.getBytes());
    }

    public void resetMixerVolume()
    {
        // Send command to Arduino
        String command = "v-mix 0.0 0.0";
        mUARTManager.writeData(command.getBytes());
    }

    public void resetMashVolume()
    {
        // Send command to Arduino
        String command = "v-msh 0.0 0.0";
        mUARTManager.writeData(command.getBytes());
    }
}
