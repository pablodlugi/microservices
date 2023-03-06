package com.pablito.common.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record MailDto(String receiveAddress, String templateName, Map<String, Object> variables) {

}
