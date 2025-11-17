package com.eight.demo.module.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eight.demo.module.model.Mail;

public interface IMailRepo extends JpaRepository<Mail, Long> {

    @Query(value = "select m from Mail m where m.mailId in ?1 order by m.createTime desc")
    List<Mail> findInMailIdsOrderByCreateTime(List<Long> ids);
}
