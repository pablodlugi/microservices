package com.example.notification.controller;

import com.example.notification.dto.TemplateDto;
import com.example.notification.exception.MongoEntityNotFoundException;
import com.example.notification.mapper.TemplateMapper;
import com.example.notification.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {

    private final TemplateService templateService;
    private final TemplateMapper templateMapper;

    @PostMapping
//    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public Mono<TemplateDto> saveTemplate(@RequestBody @Valid TemplateDto templateDto) {
        return templateService.saveTemplate(templateMapper.toDao(templateDto))
                .map(templateMapper::toDto);

    }

    @PutMapping
    //   @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public void updateTemplate(@RequestBody @Valid TemplateDto templateDto) {
        templateService.updateTemplate(templateMapper.toDao(templateDto), templateDto.getName());
    }

    @GetMapping
    //   @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public Mono<TemplateDto> getTemplateByName(@RequestParam String name) {
        return (templateService.getTemplateByName(name))
                .map(templateMapper::toDto).switchIfEmpty(Mono.error(new MongoEntityNotFoundException("Template not found!!")));
    }

    @DeleteMapping("{id}")
    //   @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public void deleteTemplate(@PathVariable String id) {
        templateService.deleteTemplate(id);
    }
}
