package com.pablito.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pablito.common.validator.PasswordValid;
import com.pablito.common.validator.group.Create;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) //jesli pole bedzie null to nie zostanie dodoane do json
@SuperBuilder
@PasswordValid(groups = Create.class)
public non-sealed class UserDto extends AuditableDto {
    private Long id;

    @NotBlank(message = "Pole USERNAME nie może być puste")
    private String username;

    @NotBlank(groups = Create.class)
    private String password;

    private String confirmedPassword;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private Integer revisionNumber;
}
