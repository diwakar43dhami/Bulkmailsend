package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

/**
 * Created by Diwakar on 5/23/2019.
 */
@RestController
public class BulkmailsendController {

    @Autowired
    ExcellReaderService excellReaderService;
    @RequestMapping("send-mail-attachment")
    public String sendWithAttachment(@RequestParam("file") MultipartFile file) throws MessagingException {
        excellReaderService.readEmailFromExcel(file);
        return "Congratulations! Your mail has been send to the user.";
    }
}
