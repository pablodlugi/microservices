package com.pablito.product.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto extends AuditableDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank(message = "Product code cannot be null")
    private String code;

    @NotNull
    private Double price;

    @NotNull
    private Long quantity;

    private Integer revisionNumber;

    private String imagePath;
}
