package com.example.notification.service.impl;

import com.example.notification.service.MailService;
import com.example.notification.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateService templateService;
    private final ITemplateEngine iTemplateEngine;

    @Override
    @Async
    public void send(String email, String templateName, Map<String, Object> variables) {
        var context = new Context(Locale.getDefault(), variables);
        templateService.getTemplateByName(templateName)
                .subscribe(t -> {
                    var mailBody = iTemplateEngine.process(t.getBody(), context);

                    javaMailSender.send(mimeMessage -> {
                        var mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                        mimeMessageHelper.setTo(email);
                        mimeMessageHelper.setSubject(t.getSubject());
                        mimeMessageHelper.setText(mailBody, true);
                    });
                });
    }

}
