package com.eight.demo.module.to;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailSendTO {

    // 前端 ignore
    private Long mailId;
    private String title;
    private String sender;
    private String receiverRole;
    private String content;
}
