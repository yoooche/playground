package com.eight.demo.module.service;

import java.util.List;

import com.eight.demo.module.to.MailInboxTO;
import com.eight.demo.module.to.MailSendTO;
import com.eight.demo.module.to.UserMailInboxTO;

public interface MailService {

    void sendMail(MailSendTO mailSendTO);

    void saveUserMailInbox(MailInboxTO mailInboxTO);

    List<UserMailInboxTO> getUserInboxMails(Integer userId);

    void markAsRead(Long mailId, Integer userId);
}
