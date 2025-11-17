package com.eight.demo.module.to;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTO {

    private Long mailId;
    private List<Integer> userIds;
    private String title;
    private String content;
    private String sender;

    @Builder.Default
    private Long timestamp = System.currentTimeMillis();
}
