package com.eight.demo.module.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eight.demo.module.service.MailService;
import com.eight.demo.module.to.MailSendTO;
import com.eight.demo.module.to.MarkMailReadTO;
import com.eight.demo.module.to.UserMailInboxTO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping(value = "/mail")
@RestController
public class MailController {

    private final MailService mailService;

    @PostMapping
    public ResponseEntity<String> sendMail(@RequestBody MailSendTO mailSendTO) {
        mailService.sendMail(mailSendTO);
        return ResponseEntity.ok("Platform mails sent!");
    }

    @GetMapping
    public ResponseEntity<List<UserMailInboxTO>> getUserInboxMails(@RequestParam(value = "userId") Integer userId) {
        return ResponseEntity.ok(mailService.getUserInboxMails(userId));
    }

    @PutMapping
    public ResponseEntity<String> markAsRead(@RequestBody MarkMailReadTO to) {
        mailService.markAsRead(to.getMailId(), to.getUserId());
        return ResponseEntity.ok("okkk");
    }

}
