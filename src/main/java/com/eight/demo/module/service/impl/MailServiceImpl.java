package com.eight.demo.module.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.eight.demo.module.common.error.BaseException;
import com.eight.demo.module.constant.StatusCode;
import com.eight.demo.module.kafka.KafkaSender;
import com.eight.demo.module.kafka.Topic;
import com.eight.demo.module.model.Mail;
import com.eight.demo.module.model.MailInbox;
import com.eight.demo.module.repository.IMailInboxRepo;
import com.eight.demo.module.repository.IMailRepo;
import com.eight.demo.module.repository.IRoleRepo;
import com.eight.demo.module.repository.IUserRoleRepo;
import com.eight.demo.module.service.MailService;
import com.eight.demo.module.to.MailInboxTO;
import com.eight.demo.module.to.MailSendTO;
import com.eight.demo.module.to.NotificationTO;
import com.eight.demo.module.to.UserMailInboxTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final IMailRepo mailRepo;
    private final IMailInboxRepo mailInboxRepo;
    private final IUserRoleRepo userRoleRepo;
    private final IRoleRepo roleRepo;
    private final KafkaSender kafkaSender;

    @Override
    @Transactional
    public void sendMail(MailSendTO mailSendTO) {
        var mail = new Mail();
        var role = roleRepo.findByRoleType(mailSendTO.getReceiverRole())
                .orElseThrow(() -> new BaseException(StatusCode.REQ_PARAM_ERR, "Role type does not exist"));

        mail.setTitle(mailSendTO.getTitle());
        mail.setSender(mailSendTO.getSender());
        mail.setContent(mailSendTO.getContent());
        mail.setReceiverRoleId(role.getRoleId());
        mailRepo.saveAndFlush(mail);

        var userIds = userRoleRepo.findUserIdByRoleId(role.getRoleId());
        var mailInboxes = new ArrayList<MailInbox>();
        userIds.forEach(userId -> {
            var mailInbox = new MailInbox();
            mailInbox.setMailId(mail.getMailId());
            mailInbox.setUserId(userId);
            mailInboxes.add(mailInbox);
        });
        mailInboxRepo.saveAll(mailInboxes);

        var notificationTO = NotificationTO.builder()
                .mailId(mail.getMailId())
                .userIds(userIds)
                .title(mail.getTitle())
                .content("Your have a new message")
                .build();

        kafkaSender.send(Topic.NOTIFICATION, notificationTO);
    }

    @Override
    @Transactional
    public void saveUserMailInbox(MailInboxTO to) {
        if (to.getMailId() == null || to.getRoleId() == null) {
            log.info("Mail id can not be null");
            return;
        }

        var userIds = userRoleRepo.findUserIdByRoleId(to.getRoleId());
        var mailInboxes = new ArrayList<MailInbox>();
        userIds.forEach(userId -> {
            var mailInbox = new MailInbox();
            mailInbox.setMailId(to.getMailId());
            mailInbox.setUserId(userId);
            mailInboxes.add(mailInbox);
        });

        log.info("Total {} mails inbox", mailInboxes.size());
        mailInboxRepo.saveAll(mailInboxes);
    }

    @Override
    public List<UserMailInboxTO> getUserInboxMails(Integer userId) {
        var mailInboxes = mailInboxRepo.findByUserId(userId);
        var statusMap = mailInboxes.stream()
                .collect(Collectors.toMap(MailInbox::getMailId, MailInbox::getStatus));

        var ids = mailInboxes.stream()
                .map(MailInbox::getMailId)
                .toList();

        var mails = mailRepo.findInMailIdsOrderByCreateTime(ids);
        var result = new ArrayList<UserMailInboxTO>();

        mails.forEach(mail -> {
            var userMail = UserMailInboxTO.builder()
                    .title(mail.getTitle())
                    .content(mail.getContent())
                    .status(statusMap.get(mail.getMailId()))
                    .build();
            result.add(userMail);
        });
        return result;
    }

    @Override
    @Transactional
    public void markAsRead(Long mailId, Integer userId) {
        mailInboxRepo.updateStatusByMailIdAndUserId(mailId, userId);
    }

}
