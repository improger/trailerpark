package com.example.demo.Tools;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SocketUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.tools.Tool;
import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Tools {

    public String getdir(){

        //String rootPath = System.getProperty("user.dir");

        //System.out.println("file going to be saved in: "+rootPath);
        return "/opt/tomcat/webapps/ROOT/WEB-INF/classes/";
        //return rootPath;
       // return rootPath+"\\src\\main\\resources\\";


    }



    public void createExcel(String type, String name, String lname, String truck, String trailer, String date) throws IOException, InvalidFormatException {
         lname = lname.toUpperCase();
         name = name.toUpperCase();
         truck = truck.toUpperCase();
         trailer = trailer.toUpperCase();





        FileInputStream inputStream = null;



        inputStream = new FileInputStream(new File(getdir()+"permission.xls"));

        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(0);
        if (type.equals("IMPORT")) {
            Cell cellNameIn = sheet.getRow(7).getCell(1);
            Cell cellNameOut = sheet.getRow(7).getCell(7);
            Cell cellTruckIn = sheet.getRow(10).getCell(2);
            Cell cellTrlIn = sheet.getRow(11).getCell(2);
            Cell cellTruckOut = sheet.getRow(10).getCell(8);
            Cell cellDateIn = sheet.getRow(21).getCell(3);
            Cell cellDateOut = sheet.getRow(21).getCell(9);
            Cell cellTrlOut = sheet.getRow(11).getCell(8);

            cellTruckIn.setCellValue(truck);
            cellTrlIn.setCellValue(trailer);
            cellNameIn.setCellValue(name + " " + lname);
            cellNameOut.setCellValue(name + " " + lname);
            cellTruckOut.setCellValue(truck);
            cellTrlOut.setCellValue("");
            cellDateIn.setCellValue(date);
            cellDateOut.setCellValue(date);

        } else if (type.equals("EXPORT")) {
            Cell cellNameIn = sheet.getRow(7).getCell(1);
            Cell cellNameOut = sheet.getRow(7).getCell(7);
            Cell cellTruckIn = sheet.getRow(10).getCell(2);
            Cell cellTrlIn = sheet.getRow(11).getCell(2);
            Cell cellTruckOut = sheet.getRow(10).getCell(8);
            Cell cellDateIn = sheet.getRow(21).getCell(3);
            Cell cellDateOut = sheet.getRow(21).getCell(9);
            Cell cellTrlOut = sheet.getRow(11).getCell(8);

            cellTruckIn.setCellValue(truck);
            cellTrlIn.setCellValue("");
            cellNameIn.setCellValue(name + " " + lname);
            cellNameOut.setCellValue(name + " " + lname);
            cellTruckOut.setCellValue(truck);
            cellTrlOut.setCellValue(trailer);
            cellDateIn.setCellValue(date);
            cellDateOut.setCellValue(date);
        }


        inputStream.close();

        FileOutputStream outputStream = new FileOutputStream(getdir()+type + "_" + truck + "-" + trailer + "_" + date + ".xls");
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }



    public void delfile(String type, String truck, String trailer, String name, String lname, String date){


        String filefordelete = getdir()+type + "_" + truck + "-" + trailer + "_" + date + ".xls";

        Path path = Paths.get(filefordelete);

       try {
           Files.delete(path);
       } catch (IOException e) {
          e.printStackTrace();
        }
    }



    public void sendEmail(String type, String truck, String trailer, String name, String lname, String date, String status) {

        trailer = trailer.toUpperCase();
        truck = truck.toUpperCase();

        if (status.equals("Loaded")){
            status = "  (L)";
        } if (status.equals("Empty")){
            status = "  (E)";
        } if (status.equals("Need repair")){
            status = "  (NR)";
        } if (status.equals("Stack")){
            status = "  (ST)";
        }



        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        // Recipient's email ID needs to be mentioned.
         String to = "****";
        //String to = "borisr4zer@gmail.com";
        // Sender's email ID needs to be mentioned
        String from = "****";

        final String username = "****";//change accordingly
        final String password = "****";//change accordingly



        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.host", "smtp.gmail.com");


        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // Get the Session object.
        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));



            // Set Subject: header field
            message.setSubject(type+": "+truck+"/"+trailer+status);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText("issued by: "+currentUser+" email: "+currentUser+"@dfds.com");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String filedir = getdir()+type + "_" + truck + "-" + trailer + "_" + date + ".xls";
            String filename = type + "_" + truck + "-" + trailer + "_" + date + ".xls";
            DataSource source = new FileDataSource(filedir);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }





}



