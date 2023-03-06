package com.pablito.product.validator.impl;

import com.pablito.product.validator.FileExtensionValid;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FileExtensionValidator implements ConstraintValidator<FileExtensionValid, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        return multipartFile == null || multipartFile.getOriginalFilename().toLowerCase().endsWith(".png")
                || multipartFile.getOriginalFilename().toLowerCase().endsWith(".jpg");
    }
}
