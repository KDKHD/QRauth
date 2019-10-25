/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrau;


import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

/**
 *
 * @author kennethkreindler
 */
public class qrcode {
    static URL location = QRaumain.class.getProtectionDomain().getCodeSource().getLocation();
    static String filelocation = location.getFile().replace("/dist/QRau.jar", "");
    static String filelocation2 = filelocation.replace("/build/classes/", "");
    static String filelocation3 = filelocation2.replace("%20", " ");
    static String folderlocation = filelocation3 + "/update";
    static String folderlocationuse = folderlocation + "/newQR.png";
    static String URLCODE = "http://qrauth.epizy.com?";

    //Generate UUID 
    public static String generatetoken() throws Exception {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        return uuid;
    }
    //Open lecal QRcode in browser
    public static void openweb() throws IOException {
        File htmlFile = new File(folderlocation + "/index.html");
        Desktop.getDesktop().browse(htmlFile.toURI());
    }

    public static void opencurrent(String token) throws IOException {
        String url_open = URLCODE + token;
        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url_open));
    }
    //Delete local QRcode
    public static boolean deleteqrcode() throws IOException {
        boolean cleared = false;
        File f = new File(folderlocation + "/newQR.png");
        if (f.exists()) {
            Path todelete = Paths.get(folderlocation + "/newQR.png");
            Files.delete(todelete);
            cleared = true;
        }
        return cleared;
    }
    //Generate QRcode
    public static ByteArrayOutputStream generateQR(String token) throws Exception {
        String details = URLCODE + token;
        ByteArrayOutputStream out = QRCode.from(details).to(ImageType.PNG).withSize(250, 250).stream();
        File f = new File(folderlocation + "/newQR.png");
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(out.toByteArray());
        fos.flush();
        return out;
    }

    //Show QRcode in jframe (Currently unused)
    public static void showqrcode() throws Exception {
        //this will close frame i.e. NewJFrame
        JFrame frame1 = new JFrame();
        Image image1 = ImageIO.read(new File(folderlocation + "/newQR.png"));
        frame1 = new JFrame();
        frame1.setVisible(false);
        frame1.setVisible(true);
        JLabel lblimage = new JLabel(new ImageIcon(image1));
        frame1.getContentPane().add(lblimage, BorderLayout.CENTER);
        frame1.setSize(250, 250);
    }
    //Open local qrcode in prowser
    public static void openbrowserqr(Boolean show) throws IOException {
        String location = "file://" + folderlocation + "/index.html";
        System.out.println(location);
        JEditorPane website = new JEditorPane(location);
        website.setEditable(false);
        JFrame frame = new JFrame("Google");
        frame.add(new JScrollPane(website));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setVisible(show);
    }

}