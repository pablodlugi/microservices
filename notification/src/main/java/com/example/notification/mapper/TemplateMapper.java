package com.example.notification.mapper;

import com.example.notification.dao.Template;
import com.example.notification.dto.TemplateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TemplateMapper {
    Template toDao(TemplateDto templateDto);

    TemplateDto toDto(Template template);
}
