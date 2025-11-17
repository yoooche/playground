package com.eight.demo.module.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

    @Column(name = "VERSION")
    private Short version = 0;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime = LocalDateTime.now();
}
