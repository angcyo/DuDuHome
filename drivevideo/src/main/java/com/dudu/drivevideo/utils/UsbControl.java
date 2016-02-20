package com.dudu.drivevideo.utils;

/**
 * Created by dengjun on 2016/2/20.
 * Description :
 */
public class UsbControl {
    public static String TO_HOST_CMD = "echo 1 > /sys/bus/platform/devices/obd_gpio.68/usb_id_enable";
    public static String TO_CLIENT_CMD = "echo 0 > /sys/bus/platform/devices/obd_gpio.68/usb_id_enable";

    public static String setToHost(){
        return  ShellExe.execShellCmd(TO_HOST_CMD);
    }

    public static String setToClient(){
        return  ShellExe.execShellCmd(TO_CLIENT_CMD);
    }
}
