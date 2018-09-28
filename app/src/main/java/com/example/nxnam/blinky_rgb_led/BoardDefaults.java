package com.example.nxnam.blinky_rgb_led;

import android.os.Build;

public class BoardDefaults {
    private static final String  DEVICE_RPI3 = "rpi3";

    public static String getGPIOForLED(){
        switch (Build.DEVICE){
            case DEVICE_RPI3:
                return "RPI3";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }
}
