package com.example.notification.service;

import java.util.Map;

public interface MailService {

    void send(String email, String templateName, Map<String, Object> variables);

}
