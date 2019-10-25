/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
//main form = new main();//Open window
  //form.setVisible(true);
 */
package qrau;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
public class QRaumain {

    public static void main(String[] args) throws Exception {

        String ANSI_RESET = "\u001B[0m";
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_RED = "\u001B[31m";
        String ANSI_LBLUE = "\u001B[94m";
        Scanner s = new Scanner(System.in);

        //Menu

        System.out.println("");
        System.out.println("Welcome to QRauth");
        System.out.println("QRauth can be used to validate that a user is present");
        System.out.println("Made by: Kenneth Kreindler");
        mysql.getpass(); //Import mysql information from csv file
        mysql.gethost(); //Import mysql information from csv file
        mysql.getuser(); //Import mysql information from csv file  
        //Select option
        boolean end = false;
        do {
            mysql.getpass();
            mysql.gethost();
            mysql.getuser();
            if (!mysql.checkconnection()) {
                System.out.println(ANSI_RED + "Please check MySQL data!! - Database options (2), Edit databse (3)" + ANSI_RESET);
            } else {
                System.out.println(ANSI_GREEN + "Valid Database" + ANSI_RESET);
            }

            System.out.println("");
            System.out.println("OPTIONS:");
            System.out.println("1 - Start QRauth");
            System.out.println("2 - Database options");
            System.out.println("3 - View and Export data");
            System.out.println("4 - Other functions");
            System.out.println("5 - Exit");
            int mode = s.nextInt();


            if (mode == -2) {
                //export attendance data
                System.out.println("Exporting attendance...");
                mysql.printattendance(System.getProperty("user.home"));
                System.out.println("Printed to Desktop...");
            }

            if (mode == 1) {
                if (!mysql.checkconnection()) {
                    System.out.println(ANSI_RED + "Unable to start/stop QRauth! Check Database" + ANSI_RESET);
                } else {
                    if (!mysql.checkalltables()) { //Check if any tables are missing in the database
                        System.out.println(ANSI_RED + "Unable to start QRauth!" + ANSI_RESET);
                    } else {

                        //start normal QRauth
                        if (!handler.isrun()) {

                            mysql.getpass();
                            mysql.gethost();
                            mysql.getuser();
                            if (!mysql.checkconnection()) {
                                System.out.println(ANSI_RED + "Can not start QRauth, Mysql data incorrect or wifi connection" + ANSI_RESET);

                            } else {

                                System.out.println("Running");
                                handler.runqrauth(false);
                                handler.runqrauth(2);
                                handler.runqrauth(true);
                                handler.runqrauthsingle(true);
                                System.out.println("To view QRcode on another device, go to this link http://qrauth.epizy.com/private/qrcodedisp.php");
                            }
                        } else {
                            //if it is running
                            System.out.println("");
                            System.out.println("QRAuth is already running in the background");
                            System.out.println("0 - Stop QRAuth");
                            System.out.println("-2 - to Export data");
                        }
                    }
                }
            }

            if (mode == 0) {
                //stop QRauth
                handler.runqrauth(false);
                System.out.println("QRAuth has been stopped");
                System.out.println("Removed " + mysql.clearallbut(0) + " tokens...");
                mysql.truncate("qrcode");
                if (qrcode.deleteqrcode()) {
                    System.out.println("QRcode Cleared");
                } else {
                    System.out.println("No QRcode to clear");
                }
            }

            if (mode == 2) {
                System.out.println("");
                System.out.println("OPTIONS:");
                System.out.println("1 - Format tables");
                System.out.println("2 - Clear tables");
                System.out.println("3 - Edit database");
                int databaseop = s.nextInt();

                if (databaseop == 1) {
                    System.out.println("");
                    System.out.println("OPTIONS:");
                    System.out.println("1 - Format users");
                    System.out.println("2 - Format tokens");
                    System.out.println("3 - Format attendance");
                    System.out.println("4 - Format Scanned");
                    System.out.println("5 - Format QRimage");
                    System.out.println("6 - Format all tables");
                    int formatop = s.nextInt();

                    if (!mysql.checkconnection()) {
                        System.out.println(ANSI_RED + "Unable to Format table! Check Database" + ANSI_RESET);
                    } else {
                        if (formatop == 1) {
                            mysql.formattableusers();
                            System.out.println("Users has been formated");
                        }
                        if (formatop == 2) {
                            mysql.formattabletokens();
                            System.out.println("Tokens has been formated");
                        }
                        if (formatop == 3) {
                            mysql.formattableattendance();
                            System.out.println("Attendance has been formated");
                        }
                        if (formatop == 4) {
                            mysql.formattablescanned();
                            System.out.println("Scanned has been formated");
                        }
                        if (formatop == 5) {
                            mysql.formatQRcode();
                            System.out.println("Qrcode has been formated");
                        }

                        if (formatop == 6) {
                            mysql.formattableusers();
                            mysql.formattabletokens();
                            mysql.formattableattendance();
                            mysql.formattablescanned();
                            mysql.formatQRcode();
                            System.out.println("All tables has been formated");
                        }
                    }
                }

                if (databaseop == 2) {
                    System.out.println("");
                    System.out.println("OPTIONS:");
                    System.out.println("1 - Clear users");
                    System.out.println("2 - Clear tokens");
                    System.out.println("3 - Clear attendance");
                    System.out.println("4 - Clear all tables");
                    int clearop = s.nextInt();
                    if (!mysql.checkconnection()) {
                        System.out.println(ANSI_RED + "Unable to clear table! Check Database" + ANSI_RESET);
                    } else {
                        if (clearop == 1) {
                            mysql.truncate("users");
                        }
                        if (clearop == 2) {
                            mysql.truncate("tokens");
                        }
                        if (clearop == 3) {
                            mysql.truncate("attendance");
                        }
                        if (clearop == 4) {
                            mysql.truncate("users");
                            mysql.truncate("tokens");
                            mysql.truncate("attendance");
                        }
                    }
                }

                if (databaseop == 3) {
                    System.out.println("");
                    System.out.println("CURRENT DATABASE DATA:");
                    System.out.println("Host: " + mysql.gethost());
                    System.out.println("Username: " + mysql.getuser());
                    System.out.println("Password: " + mysql.getpass());
                    if (!mysql.checkconnection()) {
                        System.out.println(ANSI_RED + "Please check MySQL data!!" + ANSI_RESET);
                    } else {
                        System.out.println(ANSI_GREEN + "Current databse is valid" + ANSI_RESET);
                    }
                    System.out.println("");
                    System.out.println("Enter new database ( . for no change)");
                    System.out.println("Database Host:");
                    String newhost = s.next();
                    System.out.println("Database Username:");
                    String newuser = s.next();
                    System.out.println("Database Password:");
                    String newpass = s.next();
                    if (!newhost.equals(".")) {
                        mysql.sethost(newhost);
                    }
                    if (!newuser.equals(".")) {
                        mysql.setuser(newuser);
                    }
                    if (!newpass.equals(".")) {
                        mysql.setpass(newpass);
                    }
                    continue;
                }
            }

            if (mode == 3) {
                System.out.println("");
                System.out.println("OPTIONS:");
                System.out.println("1 - View users");
                System.out.println("2 - View attendance");
                System.out.println("3 - Export users");
                System.out.println("4 - Export attendance");
                int viewop = s.nextInt();
                if (!mysql.checkconnection()) {
                    System.out.println(ANSI_RED + "Unable to view/export data! Check Database" + ANSI_RESET);
                } else {
                    if (viewop == 1) {
                        List < String > userlistfinal = new ArrayList < String > (Arrays.asList(mysql.viewattendance().split("\n")));
                        for (int t = 0; t < userlistfinal.size(); t++) {

                            if ((t & 1) != 0) {
                                System.out.println(ANSI_RED + userlistfinal.get(t) + ANSI_RESET);
                            } else {
                                System.out.println(ANSI_LBLUE + userlistfinal.get(t) + ANSI_RESET);
                            }
                        }
                    }

                    if (viewop == 2) {
                        List < String > attendancelistfinal = new ArrayList < String > (Arrays.asList(mysql.viewattendance().split("\n")));
                        for (int t = 0; t < attendancelistfinal.size(); t++) {
                            if ((t & 1) != 0) {
                                System.out.println(ANSI_RED + attendancelistfinal.get(t) + ANSI_RESET);
                            } else {
                                System.out.println(ANSI_LBLUE + attendancelistfinal.get(t) + ANSI_RESET);
                            }
                        }
                    }

                    if (viewop == 3) {
                        System.out.println("Exporting users...");
                        mysql.printusers(System.getProperty("user.home"));
                        System.out.println("Printed to Desktop...");
                    }

                    if (viewop == 4) {
                        System.out.println("Exporting attendance...");
                        mysql.printattendance(System.getProperty("user.home"));
                        System.out.println("Printed to Desktop...");

                    }
                }
            }

            if (mode == 4) {
                System.out.println("");
                System.out.println("OPTIONS:");
                System.out.println("1 - Start QRauthLite (Less CPU usage, Less secure, No Ipad compatibility)");
                int othermode = s.nextInt();
                if (othermode == 1) {
                    if (mysql.checkconnection()) {
                        //start QRauth with low CPU usuage, less reliable data
                        handler.runqrauth(false);
                        handler.cycle();
                        handler.runqrauth(1);
                        handler.runqrauth(true);
                        handler.runqrauthsingle(false);
                        mysql.insertscanned();
                        System.out.println("Static Running");
                    }
                } else {
                    System.out.println(ANSI_RED + "Unable to start QRauthLite. Check database" + ANSI_RESET);
                }
            }


            if (mode == 5) {
                handler.runqrauth(false);
                if (mysql.checkconnection()) {
                    mysql.truncate("tokens");
                    mysql.truncate("qrcode");
                }
                if (qrcode.deleteqrcode()) {
                    System.out.println("QRcode Cleared");
                } else {
                    System.out.println("Local QR codes cleared");
                }
                end = true;
            }
        } while (!end);
    }
}