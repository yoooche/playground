package com.eight.demo.module.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MAIL")
public class Mail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MAIL_ID")
    private Long mailId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "SENDER")
    private String sender;

    @Column(name = "RECEIVER_ROLE_ID")
    private Integer receiverRoleId;

    @Column(name = "CONTENT")
    private String content;

}
