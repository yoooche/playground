package com.eight.demo.module.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.eight.demo.module.model.MailInbox;
import java.util.List;

public interface IMailInboxRepo extends JpaRepository<MailInbox, Long> {

    List<MailInbox> findByUserId(Integer userId);

    @Modifying
    @Query(value = "update MailInbox m set m.status = 1 where m.mailId = ?1 and m.userId = ?2")
    void updateStatusByMailIdAndUserId(Long mailId, Integer userId);
}
