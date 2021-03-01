package com.example.cyan;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author Chunyu Li
 * @File: Util.java
 * @Package com.example.cyan
 * @date 12/12/20 8:36 PM
 * @Description: Encapsulate the process of using some necessary utils in this class.
 */
public class Util {

    private static final String myEmailAccount = "lcy2387906726@163.com";
    private static String verificationCode = "";

    public static void showSnackBar(String type, View view, String content, Context context) {
        if (type.contentEquals("blue")) {
            Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorBlue));
            snackbar.show();
        } else if (type.contentEquals("red")) {
            Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorRed));
            snackbar.show();
        } else if (type.contentEquals("yellow")) {
            Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorGold));
            snackbar.show();
        }
    }

    public static void generateCode() {
        Random random = new Random();
        char[] chars = new char[4];
        for (int i = 0; i < 4; i++) {

            // use Random object to generate four chars
            chars[i] = (char) (random.nextInt(26) + 65);
        }
        verificationCode = new String(chars);
    }

    public static void sendEmail(String email, View view, Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.163.com");
                Session session = Session.getInstance(props, null);
                try {
                    MimeMessage msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress(myEmailAccount, "ChunyuLi", "UTF-8"));
                    msg.setRecipients(Message.RecipientType.TO, email);
                    msg.setSubject("This email is for verifying your account");
                    msg.setSentDate(new Date());
                    msg.setText("The verification code is " + verificationCode);
                    Transport.send(msg, myEmailAccount, "JNQVODPUWYRNGOAE");
                    showSnackBar("blue", view, "The verification code has been sent to your email!", context);
                } catch (MessagingException e) {
                    showSnackBar("red", view, "Failed to send email!", context);
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    showSnackBar("red", view, "Failed to send email!", context);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String getVerificationCode() {
        return verificationCode;
    }

    public static void setVerificationCode(String verificationCode) {
        Util.verificationCode = verificationCode;
    }
}
