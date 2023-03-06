package com.example.notification.service;

import com.example.notification.dao.Template;
import reactor.core.publisher.Mono;


public interface TemplateService {

    Mono<Template> getTemplateByName(String name);

    Mono<Template> saveTemplate(Template template);

    void deleteTemplate(String id);

    Mono<Template> updateTemplate(Template template, String name);

}
