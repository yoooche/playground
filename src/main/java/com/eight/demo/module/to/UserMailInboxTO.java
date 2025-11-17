package com.eight.demo.module.to;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserMailInboxTO {

    private String sender;
    private String title;
    private String content;
    private Short status;
}
