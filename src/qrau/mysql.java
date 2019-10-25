/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrau;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author kennethkreindler
 */
public class mysql {
    private static String HOST = null;
    private static String USERNAME = null;
    private static String PASSWORD = null;
    static int currentattendance = 0;
    static int currentscanned = 0;
    static int idnscan = -1;

    static URL location = QRaumain.class.getProtectionDomain().getCodeSource().getLocation();
    static String filelocation = location.getFile().replace("/dist/QRau.jar", "");
    static String filelocation2 = filelocation.replace("/build/classes/", "");
    static String filelocation3 = filelocation2.replace("%20", " ");
    static String folderlocation = filelocation3 + "/update";
    Connection connectionstring = null;

    //Set host from file 
    public static void sethost(String host) throws SQLException, FileNotFoundException {
        HOST = host;
        PrintWriter pwb = new PrintWriter(new File(folderlocation + "/host.csv"));
        StringBuilder sbb = new StringBuilder();
        sbb.append(host);
        pwb.write(sbb.toString());
        pwb.close();
    }

    //Set user 
    public static void setuser(String user) throws SQLException, FileNotFoundException {
        USERNAME = user;
        PrintWriter pwb = new PrintWriter(new File(folderlocation + "/user.csv"));
        StringBuilder sbb = new StringBuilder();
        sbb.append(user);
        pwb.write(sbb.toString());
        pwb.close();
    }
    //Set password
    public static void setpass(String pass) throws SQLException, FileNotFoundException {
        PASSWORD = pass;
        PrintWriter pwb = new PrintWriter(new File(folderlocation + "/pass.csv"));
        StringBuilder sbb = new StringBuilder();
        sbb.append(pass);
        pwb.write(sbb.toString());
        pwb.close();
    }
    //Get password from file
    public static String getpass() throws SQLException, FileNotFoundException, IOException {
        String line = "";
        String[] password = null;
        if (new File(folderlocation + "/pass.csv").exists()) {
            BufferedReader br = new BufferedReader(new FileReader(folderlocation + "/pass.csv"));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                password = line.split(",");
                PASSWORD = password[0];
            }
        } else {
            return "null";
        }
        return password[0];
    }

    //Get host from file
    public static String gethost() throws SQLException, FileNotFoundException, IOException {
        String line = "";
        String[] host = null;
        if (new File(folderlocation + "/host.csv").exists()) {
            BufferedReader br = new BufferedReader(new FileReader(folderlocation + "/host.csv"));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                host = line.split(",");
                HOST = host[0];
            }
        } else {
            return "null";
        }
        return host[0];
    }

    //Get user from file
    public static String getuser() throws SQLException, FileNotFoundException, IOException {
        String line = "";
        String[] user = null;
        if (new File(folderlocation + "/user.csv").exists()) {
            BufferedReader br = new BufferedReader(new FileReader(folderlocation + "/user.csv"));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                user = line.split(",");
                USERNAME = user[0];
            }
        } else {
            return "null";
        }
        return user[0];
    }

    // Get connection to mysql
    public static Connection getconnection() throws SQLException {
        Connection myConn = null;
        myConn = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
        return myConn;
    }

    //Check if mysql connection works
    public static boolean checkconnection() {
        boolean validconnection = true;
        Connection myConn = null;
        try {
            myConn = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
            myConn.close();
        } catch (SQLException ex) {
            validconnection = false;
        }
        return validconnection;
    }

    //reset counter
    public static void resetidnscan() throws SQLException {
        idnscan = -1;
        currentscanned = 0;
    }

    // TOKEN HANDLERS
    // TOKEN HANDLERS
    // TOKEN HANDLERS

    //Check if tables exist
    public static boolean checktables(String table) throws SQLException {
        boolean tableexists = true;
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            String sql = "Select * from " + table;
            myStmt.execute(sql);
            myConn.close();
        } catch (Exception exc) {
            tableexists = false;
        }
        return tableexists;
    }

    //Upload token to database
    public static void uploadtoken(String token) throws SQLException {
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            String sql = "insert into tokens " + " (token)" + " values ('" + token + "')";
            myStmt.executeUpdate(sql);
            // System.out.println("Token " + token + " has been inserted into database");
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    //Upload QRcode to database
    public static void uploadqrcode(ByteArrayOutputStream qrcodeinbyte) throws SQLException {
        String sql = "insert into qrcode(qrimage) values (?)";
        PreparedStatement pstmt = mysql.getconnection().prepareStatement(sql);
        ByteArrayInputStream bais = new ByteArrayInputStream(qrcodeinbyte.toByteArray());
        pstmt.setBinaryStream(1, bais);
        pstmt.execute();
        pstmt.close();
        System.out.println("Cleared " + mysql.clearallbutqrcodes(2) + " qr codes from db");
    }

    //Get the amount of tokens on database 
    public static int getamountoftokens() throws SQLException {
        int rowcounter = 0;
        ResultSet myRs = null;
        try {
            // 1. Get a connection to database
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            // 3. Execute SQL query
            myRs = myStmt.executeQuery("select * from tokens");
            while (myRs.next()) {
                rowcounter = rowcounter + 1;
            }
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return rowcounter;
    }

    //Get amount of qrcode on database
    public static int getamountofqrcodes() throws SQLException {
        int rowcounter = 0;
        ResultSet myRs = null;
        try {
            // 1. Get a connection to database
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            // 3. Execute SQL query
            myRs = myStmt.executeQuery("select * from qrcode");
            while (myRs.next()) {
                rowcounter = rowcounter + 1;
            }
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return rowcounter;
    }

    //Check how many tokens to delete
    public static int clearallbut(int amount) throws SQLException {
        int currentamount = mysql.getamountoftokens();
        int amounttoremove = 0;
        if (currentamount > amount) {
            amounttoremove = currentamount - amount;
            mysql.deletetokens(amounttoremove);
            // System.out.println("Deleted " + amounttoremove + " token. Current amount of tokens = " + (currentamount - 1));
        }
        return amounttoremove;
    }

    //Clear all but some tokens
    public static int clearallbutqrcodes(int amount) throws SQLException {
        int currentamount = mysql.getamountofqrcodes();
        int amounttoremove = 0;
        if (currentamount > amount) {
            amounttoremove = currentamount - amount;
            mysql.deletetokensqrcode(amounttoremove);
            // System.out.println("Deleted " + amounttoremove + " token. Current amount of tokens = " + (currentamount - 1));
        }
        return amounttoremove;
    }

    //Delete tokens from database
    public static void deletetokens(int amount) throws SQLException {
        try {
            // 1. Get a connection to database
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            // 3. Execute SQL query
            String sql = "DELETE FROM tokens WHERE id > 0 LIMIT " + amount;
            myStmt.executeUpdate(sql);
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    //Delete QRcodes
    public static void deletetokensqrcode(int amount) throws SQLException {
        try {
            // 1. Get a connection to database
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            // 3. Execute SQL query
            String sql = "DELETE FROM qrcode WHERE id > 0 LIMIT " + amount;
            myStmt.executeUpdate(sql);
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    // VIEW METHODS
    // VIEW METHODS
    // VIEW METHODS
    // VIEW METHODS

    //View attendance
    public static String viewattendance() throws SQLException {
        String attendancefinal = null;
        ResultSet myRs = null;
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            myRs = myStmt.executeQuery("select * from attendance");
            while (myRs.next()) {
                String fName = myRs.getString("first_name");
                String lName = myRs.getString("last_name");
                String uuid = myRs.getString("uuid");
                String email = myRs.getString("email");
                String reason = myRs.getString("reason");
                String status = myRs.getString("status");
                String time = myRs.getString("time");
                //System.out.println("");
                // System.out.format("%s, %s, %s, %s, %s, %s, %s\n", uuid, fName, lName, email, status, reason, time);
                attendancefinal = attendancefinal + "\n" + uuid + ", " + fName + ", " + lName + ", " + email + ", " + status + ", " + reason + ", " + time;
            }
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return attendancefinal;
    }

    //View users
    public static String viewusers() throws SQLException {
        ResultSet myRs = null;
        String userlistfinal = null;
        try {
            // 1. Get a connection to database
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            // 3. Execute SQL query
            myRs = myStmt.executeQuery("select uuid, last_name, first_name, email from users");
            System.out.println("");
            while (myRs.next()) {
                String fName = myRs.getString("first_name");
                String lName = myRs.getString("last_name");
                String uuid = myRs.getString("uuid");
                String email = myRs.getString("email");
                //System.out.format("%s, %s, %s, %s\n", fName, lName, email, uuid);
                userlistfinal = userlistfinal + "\n" + fName + ", " + lName + ", " + email + ", " + uuid;
            }
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return userlistfinal;
    }

    //Check if qr code was scanned
    public static String checkscan() throws SQLException {
        String atfinal = "";
        String id = null;
        ResultSet myRs = null;
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            myRs = myStmt.executeQuery("select * from scanned");
            while (myRs.next()) {
                id = myRs.getString("id");
                String fName = myRs.getString("first_name");
                String lName = myRs.getString("last_name");
                idnscan = Integer.parseInt(id);
                if (idnscan > currentscanned) {
                    //System.err.format("%s, %s, %s, %s, %s, %s\n", fName, lName, email, status, reason, time);
                    String scanned = fName + ", " + lName;
                    atfinal = atfinal + scanned + "\n";
                    currentscanned = idnscan;
                }
            }
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return atfinal;
    }

    //Check if there is new attendance
    public static String checkattendance() throws SQLException {
        int rowcounter = 0;
        String atfinal = "";
        String id = null;
        int idn = -1;
        ResultSet myRs = null;
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            myRs = myStmt.executeQuery("select * from attendance");
            while (myRs.next()) {
                id = myRs.getString("id");
                String fName = myRs.getString("first_name");
                String lName = myRs.getString("last_name");
                String uuid = myRs.getString("uuid");
                String email = myRs.getString("email");
                String reason = myRs.getString("reason");
                String status = myRs.getString("status");
                String time = myRs.getString("time");
                idn = Integer.parseInt(id);
                if (idn > currentattendance) {
                    //System.err.format("%s, %s, %s, %s, %s, %s\n", fName, lName, email, status, reason, time);
                    String attendanceadd = uuid + ", " + fName + ", " + lName + ", " + email + ", " + status + ", " + reason + ", " + time;
                    atfinal = atfinal + attendanceadd + "\r\n";
                    currentattendance = idn;
                }
                if (atfinal.equals(" ")) {
                    return atfinal;
                }
            }
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {}
        return atfinal;
    }

    //Get attendance and print
    public static void printattendance(String location) throws SQLException, FileNotFoundException {
        PrintWriter pwb = new PrintWriter(new File(location, "Desktop" + "/" + "attendance.csv"));
        StringBuilder sbb = new StringBuilder();
        sbb.append("UUID , First Name , Last Name , Email , Status , Reason , Time" + "\n");
        sbb.append(viewattendance());
        pwb.write(sbb.toString());
        pwb.close();
    }

    //Print the users
    public static void printusers(String location) throws SQLException, FileNotFoundException {
        PrintWriter pwb = new PrintWriter(new File(location, "Desktop" + "/" + "users.csv"));
        StringBuilder sbb = new StringBuilder();
        sbb.append("First Name , Last Name , Email , UUID" + "\n");
        sbb.append(viewusers());
        pwb.write(sbb.toString());
        pwb.close();
    }


    // CLEAR METHODS
    // CLEAR METHODS
    // CLEAR METHODS


    //Clear table
    public static void truncate(String table) throws SQLException {
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            // 3. Execute SQL query
            String sql = "TRUNCATE TABLE " + table;
            myStmt.executeUpdate(sql);
            System.out.println(table + " has been cleared");
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    // FORMAT TABLE METHODS
    // FORMAT TABLE METHODS
    // FORMAT TABLE METHODS

    //Format users table
    public static void formattableusers() throws SQLException {
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            String sql1 = "DROP TABLE IF EXISTS `users`";
            String sql = "CREATE TABLE `users` ( `id` int(11) NOT NULL AUTO_INCREMENT, `uuid` varchar(64) DEFAULT NULL, `last_name` varchar(64) DEFAULT NULL, `first_name` varchar(64) DEFAULT NULL, `email` varchar(64) DEFAULT NULL, `password` varchar(64) DEFAULT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
            myStmt.executeUpdate(sql1);
            myStmt.executeUpdate(sql);
            //System.out.println("Users has been formated");
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    //Format QRcodetable
    public static void formatQRcode() throws SQLException {
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            String sql1 = "DROP TABLE IF EXISTS `qrcode`";
            String sql = "CREATE TABLE `qrcode` ( `id` int(11) NOT NULL AUTO_INCREMENT, `qrimage` LONGBLOB NOT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
            myStmt.executeUpdate(sql1);
            myStmt.executeUpdate(sql);
            //System.out.println("Users has been formated");
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    //Format scanned table
    public static void formattablescanned() throws SQLException {
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            String sql1 = "DROP TABLE IF EXISTS `scanned`";
            String sql = "CREATE TABLE `scanned` ( `id` int(11) NOT NULL AUTO_INCREMENT, `last_name` varchar(64) DEFAULT NULL,  `first_name` varchar(64) DEFAULT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
            myStmt.executeUpdate(sql1);
            myStmt.executeUpdate(sql);
            //System.out.println("Tokens has been formated");
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    //Format attendance
    public static void formattableattendance() throws SQLException {
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            String sql1 = "DROP TABLE IF EXISTS `attendance`";
            String sql = "CREATE TABLE `attendance` ( `id` int(11) NOT NULL AUTO_INCREMENT, `uuid` varchar(64) DEFAULT NULL, `last_name` varchar(64) DEFAULT NULL, `first_name` varchar(64) DEFAULT NULL, `email` varchar(64) DEFAULT NULL, `reason` varchar(64) DEFAULT NULL, `status` varchar(64) DEFAULT NULL, `time` varchar(64) DEFAULT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
            myStmt.executeUpdate(sql1);
            myStmt.executeUpdate(sql);
            //System.out.println("Attendance has been formated");
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    //Format tokens
    public static void formattabletokens() throws SQLException {
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            String sql1 = "DROP TABLE IF EXISTS `tokens`";
            String sql = "CREATE TABLE `tokens` ( `id` int(11) NOT NULL AUTO_INCREMENT, `token` varchar(64) DEFAULT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
            myStmt.executeUpdate(sql1);
            myStmt.executeUpdate(sql);
            //System.out.println("Tokens has been formated");
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    //Add to scanned
    public static void insertscanned() throws SQLException {
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            String sql = "insert into scanned " + " (last_name)" + " values ('" + "null" + "')";
            myStmt.executeUpdate(sql);
            // System.out.println("Token " + token + " has been inserted into database");
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    //Insert qrcode to database
    public static void insertqrcode() throws SQLException {
        try {
            Connection myConn = mysql.getconnection();
            Statement myStmt = myConn.createStatement();
            String sql = "insert into scanned " + " (last_name)" + " values ('" + "null" + "')";
            myStmt.executeUpdate(sql);
            // System.out.println("Token " + token + " has been inserted into database");
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    //Check if tables exist    
    public static boolean checkalltables() throws SQLException, IOException {
        String ANSI_RED = "\u001B[31m";
        String ANSI_RESET = "\u001B[0m";
        boolean allexist = true;
        if (mysql.checkconnection()) {
            if (!mysql.checktables("users")) {
                System.out.println(ANSI_RED + "Please format users table!! - Database options (2), Format tables (3), Users (1) " + ANSI_RESET);
                allexist = false;
            }
            if (!mysql.checktables("tokens")) {
                System.out.println(ANSI_RED + "Please format tokens table!! - Database options (2), Format tables (3), Tokens (2)" + ANSI_RESET);
                allexist = false;
            }
            if (!mysql.checktables("attendance")) {
                System.out.println(ANSI_RED + "Please format attendance table!! - Database options (2), Format tables (3), Attendance (3)" + ANSI_RESET);
                allexist = false;
            }
            if (!mysql.checktables("scanned")) {
                System.out.println(ANSI_RED + "Please format scanned table!! - Database options (2), Format tables (3), Scanned (4)" + ANSI_RESET);
                allexist = false;
            }
            if (!mysql.checktables("qrcode")) {
                System.out.println(ANSI_RED + "Please format qrcode table!! - Database options (2), Format tables (3), QRimage (5) " + ANSI_RESET);
                allexist = false;
            }
        }
        return allexist;
    }
}