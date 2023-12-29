package com.example.CustomBlog;

import com.example.CustomBlog.user.User;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class Mail_Services {

    private final JavaMailSender javaMailSender;

    public Mail_Services(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    public void sendCustomEMail(
            String toEmail,
            String subject,
            String content
    )
    {
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("customblog.webpage@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }
    public void sendWelcomeEmail(User newuser)
    {
        try
        {
            String subject="WELCOME TO THE CUSTOM BLOG JOURNEY !";
            String content =
                    "Hello, **" + newuser.getDisplay_name() + "**," +
                            "\n\nWe are pleased to inform you that we have successfully created your new account: **" + newuser.getUsername() + "**." +
                            "\n\nWe are thrilled to have you on board for this journey with us at Custom_Blog. We hope that you will enjoy your time here." +
                            "\n\nBest regards," +
                            "\nCustom Blog team" +
                            "\n\n**This is an auto-generated message, please do not reply to this email.**";
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            helper.setTo(newuser.getEmail());
            helper.setSubject(subject);
            message.setContent(formatContent(content),"text/html");
            javaMailSender.send(message);
        }
        catch (Exception e)
        {
            e.getMessage();
        }

    }

    public void sendForgotPasswordEmail(User user)
    {
        try
        {
            String subject = "RESETTING YOUR PASSWORD !";
            String content =
                    "Forgot your password?" +
                            "\nNo worries, we've got you covered!" +
                            "\nPlease use the code below on the 'Forgot Password' page to create a new password. You can then completely forget about the old one!" +
                            "\n\n\n<center><h1><strong>" + user.getResetPassCode() + "</strong></h1></center>" +
                            "\n\nPlease note that this code will expire after 5 minutes. If you did not request this password reset, please contact our administrator immediately." +
                            "\n\nBest regards," +
                            "\nCustom Blog team" +
                            "\n\n**This is an auto-generated message, please do not reply to this email.**";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            message.setContent(formatContent(content), "text/html");
            javaMailSender.send(message);
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    public void notifyPasswordChanged(User user)
    {
        try
        {
            String subject = "YOUR PASSWORD FOR CUSTOM BLOG HAVE BEEN CHANGED !!";
            String content =
                    "Dear **" + user.getDisplay_name() + "**," +
                            "\n\nWe have noticed that your password has been changed recently. If you did not initiate this change, please contact our administrator immediately." +
                            "\n\nBest regards," +
                            "\nCustom Blog team" +
                            "\n\n**This is an auto-generated message, please do not reply to this email.**";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            message.setContent(formatContent(content), "text/html");
            javaMailSender.send(message);
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    public String formatContent(String content)
    {
        MutableDataSet options = new MutableDataSet();

        // Convert markdown to HTML
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        com.vladsch.flexmark.util.ast.Node document = parser.parse(content);
        String html = renderer.render(document);

        return html;
    }
}
