package com.bhushan.vendor.url;

public class Links {

    //  for local phpMyAdmin

    // Root link for loginRegister part
    private static final String ROOT_URL = "http://192.168.43.13/comkart1_fresh/loginRegister/";

    // Root endpoint of links for loginRegister part
    public static final String URL_REGISTER = ROOT_URL + "register.php";
    public static final String URL_LOGIN = ROOT_URL + "login.php";



    //links for profile part
    public static final String upload = "http://192.168.43.13/comkart1_fresh/profile%20images/Api.php?apicall=upload";
    public static final String retrive = "http://192.168.43.13/comkart1_fresh/profile%20images/Api.php?apicall=images";
    public static final String single = "http://192.168.43.13/comkart1_fresh/profile%20images/Api.php?apicall=single";

    // links for Adding Product
    public static String productFetch = "http://192.168.43.13/comkart1_fresh/addproduct/product.php?product=fetch";
    public static String productUpload = "http://192.168.43.13/comkart1_fresh/addproduct/product.php?product=upload";
    public static String productDelete = "http://192.168.43.13/comkart1_fresh/addproduct/product.php?product=delete";
    public static String productDataGet = "http://192.168.43.13/comkart1_fresh/addproduct/product.php?product=id";
    public static String productSortByName = "http://192.168.43.13/comkart1_fresh/addproduct/product.php?product=sortByName";
    public static String productSortByDate = "http://192.168.43.13/comkart1_fresh/addproduct/product.php?product=sortByDate";

    // category & subcategories
    public static String loadCategory = "http://192.168.43.13/comkart1_fresh/addproduct/product.php?product=category";
    public static String loadSubcategory = "http://192.168.43.13/comkart1_fresh/addproduct/product.php?product=subcategory";

    // home fragment
    public static String getdata = "http://192.168.43.13/comkart1_fresh/homeDatabase/fetchdata.php?orders=getdata";


    // aadhar & pan
    public static String uploadAadhar = "http://192.168.43.13/comkart1_fresh/Documents/AadharDetails.php";
    public static String uploadPan = "http://192.168.43.13/comkart1_fresh/Documents/PanDetails.php";

    // fcm for Notification
    public static String fcmRegister = "http://192.168.43.13/comkart1_fresh/FCM/fcmRegister.php";

    // notification
    public static String fetchNoti = "http://192.168.43.13/comkart1_fresh/notification/notification.php?noti=fetchNoti";

    public static String insertNoti = "http://192.168.43.13/comkart1_fresh/notification/notification.php?noti=insertNoti";

    public static String deleteNoti = "http://192.168.43.13/comkart1_fresh/notification/notification.php?noti=deleteNoti";

    public static String markAsReaded = "http://192.168.43.13/comkart1_fresh/notification/notification.php?noti=markAsReaded";

    public static String fetchMarkAsReaded = "http://192.168.43.13/comkart1_fresh/notification/notification.php?noti=fetchMarkAsReaded";

    public static String unreadedCount = "http://192.168.43.13/comkart1_fresh/notification/notification.php?noti=unreadedCount";


 }
