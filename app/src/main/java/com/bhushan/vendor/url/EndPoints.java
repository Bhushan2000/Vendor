package com.bhushan.vendor.url;

public class EndPoints {
    // Links for register email id for FCM (Firebase Cloud Messaging)
    public static final String URL_REGISTER_DEVICE = "http://192.168.43.13/comkart1_fresh/FCM/fcm/RegisterDevice.php";
    // send notification to single user
    public static final String URL_SEND_SINGLE_PUSH = "http://192.168.43.13/comkart1_fresh/FCM/fcm/sendSinglePush.php";
    // send notification to multiple user
    public static final String URL_SEND_MULTIPLE_PUSH = "http://192.168.43.13/comkart1_fresh/FCM/fcm/sendMultiplePush.php";
    // It is use for fetching the register email id's
    public static final String URL_FETCH_DEVICES = "http://192.168.43.13/comkart1_fresh/FCM/fcm/GetRegisteredDevices.php";
}