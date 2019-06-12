package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by Diwakar on 5/23/2019.
 */
@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmailWithAttachment(User user , String path) throws MailException, MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(user.getEmailAddress());
        helper.setSubject("Testing Mail API with Attachment");
        String fName= user.getFirstName();
        String lName= user.getLastName();
        if(fName != null && lName != null){
            helper.setText(" Hi"+ " " +fName + " " + lName  + " " + "Please find the attached document below.");
        } else if(fName == null && lName == null ){
            helper.setText(" Hi Please find the attached document below.");
        }else if(fName!= null && lName == null ){
            helper.setText(" Hi"+" " +  fName +" " + "Please find the attached document below.");
        }else if(fName== null && lName != null ){
            helper.setText(" Hi"+ " " + lName + " " + "Please find the attached document below.");
        }
        FileSystemResource file = new FileSystemResource(path);
        helper.addAttachment(file.getFilename(), file);
        javaMailSender.send(mimeMessage);
    }
}
