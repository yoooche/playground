package com.eight.demo.module.service;

import com.eight.demo.module.to.NotificationTO;

public interface NotificationService {

    void pushToUsers(NotificationTO notification);
}
