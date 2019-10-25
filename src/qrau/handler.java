/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrau;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 *
 * @author kennethkreindler
 */
public class handler {

    static volatile boolean isRunning = false;
    static volatile boolean isRunningsingle = false;
    static String ANSI_RESET = "\u001B[0m";
    static String ANSI_GREEN = "\u001B[32m";
    static String ANSI_RED = "\u001B[31m";
    static String ANSI_LBLUE = "\u001B[94m";

    public static void runqrauth(int amount) throws Exception {
        qrcode.openweb();
        //qrcode.openbrowserqr(true);
        mysql.truncate("scanned");
        mysql.resetidnscan();
        System.out.println("Reset scanned");
        Thread qrauth = new Thread() {

            public void run() {
                do {
                    try {
                        List < String > attendancelist = new ArrayList < String > (Arrays.asList(mysql.checkattendance().split("\r\n")));
                        for (int t = 0; t < attendancelist.size(); t++) {
                            if ((t & 1) != 0) //odd
                            {
                                System.out.println(ANSI_RED + attendancelist.get(t) + ANSI_RESET);
                            } else {
                                System.out.println(ANSI_LBLUE + attendancelist.get(t) + ANSI_RESET);
                            }
                        }
                        handler.cycle();
                        int amounttoremove = mysql.clearallbut(5);
                        if (amounttoremove == 0) {} else {
                            System.out.println("Removed " + amounttoremove + " token...");
                        }
                        Thread.sleep(3000);
                    } catch (Exception ex) {
                        Logger.getLogger(handler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } while (isRunning && isRunningsingle);
            }

        };

        Thread scancheck = new Thread() {
            public void run() {
                do {
                    try {
                        String checkscan = mysql.checkscan();
                        if (!checkscan.equals("")) {
                            System.out.println(ANSI_GREEN + "Scanned by: " + checkscan + ANSI_RESET);
                            handler.cycle();
                            mysql.clearallbut(2);
                        }

                        List < String > attendancelist = new ArrayList < String > (Arrays.asList(mysql.checkattendance().split("\r\n")));
                        for (int t = 0; t < attendancelist.size(); t++) {
                            String attendancenew = attendancelist.get(t);
                            if (!attendancenew.isEmpty()) {
                                if ((t & 1) != 0) //odd
                                {
                                    System.out.println(ANSI_RED + attendancenew + ANSI_RESET);
                                } else {
                                    System.out.println(ANSI_LBLUE + attendancenew + ANSI_RESET);
                                }
                            }
                        }

                        Thread.sleep(500);
                    } catch (Exception ex) {
                        Logger.getLogger(handler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } while (isRunning);
            }
        };

        if (amount == 1) {
            handler.cycle();
            handler.cycle();
            scancheck.start();
            System.out.println("Single thread");


        } else {
            qrauth.start();
            scancheck.start();
            System.out.println("Double thread");
        }
    }


    public static void runqrauth(boolean run) throws Exception {
        isRunning = run;
    }
    public static void runqrauthsingle(boolean run) throws Exception {
        isRunningsingle = run;
    }
    public static boolean isrun() throws Exception {
        return isRunning;
    }

    public static void cycle() throws Exception {
        String token = qrcode.generatetoken();
        System.out.println("Generated token = " + token);

        ByteArrayOutputStream bytesteam = qrcode.generateQR(token);

        System.out.println("QR code has been generated ");
        //qrcode.showqrcode(); 
        //System.out.println("QR code has been Displayed ");
        mysql.uploadtoken(token);
        System.out.println("Token has been uploaded ");
        mysql.uploadqrcode(bytesteam);
        //qrcode.opencurrent(token);
    }
}