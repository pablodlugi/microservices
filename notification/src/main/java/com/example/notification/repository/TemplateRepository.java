package com.example.notification.repository;


import com.example.notification.dao.Template;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface TemplateRepository extends ReactiveMongoRepository<Template, String> {

    Mono<Template> findByName(String name);

}
