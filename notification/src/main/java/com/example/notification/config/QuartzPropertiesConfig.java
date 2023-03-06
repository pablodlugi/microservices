package com.example.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

        @Configuration
        @ConfigurationProperties(prefix = "quartz-data-source")
        @Data
        public class QuartzPropertiesConfig {

            private String url;

            private String user;

            private String password;

}
