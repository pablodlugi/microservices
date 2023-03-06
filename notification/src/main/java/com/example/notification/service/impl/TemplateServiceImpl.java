package com.example.notification.service.impl;

import com.example.notification.dao.Template;
import com.example.notification.repository.TemplateRepository;
import com.example.notification.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;

    @Override
    public Mono<Template> getTemplateByName(String name) {
        return templateRepository.findByName(name);
    }

    @Override
    public Mono<Template> saveTemplate(Template template) {
        return templateRepository.save(template);
    }

    @Override
    public void deleteTemplate(String id) {
        templateRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Mono<Template> updateTemplate(Template template, String name) {
        return getTemplateByName(name)
                .map(templateDb -> {
                    templateDb.setBody(template.getBody());
                    templateDb.setName(template.getName());
                    templateDb.setSubject(template.getSubject());
                    return templateDb;
                });
    }

}
