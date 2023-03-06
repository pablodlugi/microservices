package com.example.notification.config;

import com.example.notification.scheduler.SampleJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

import javax.sql.DataSource;

@Configuration
public class QuartzConfig {

    @Bean
    @QuartzDataSource
    public DataSource DataSource(QuartzPropertiesConfig quartzPropertiesConfig) {
        return DataSourceBuilder.create()
                .url(quartzPropertiesConfig.getUrl())
                .username(quartzPropertiesConfig.getUser())
                .password(quartzPropertiesConfig.getPassword())
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob()
                .ofType(SampleJob.class)
                .storeDurably()
                .withIdentity("QUARTZ_SCHEDULER")
                .withDescription("SAMPLE_QUARTZ_SCHEDULER")
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("QUARTZ_TRIGGER")
                .withDescription("SAMPLE_QUARTZ_TRIGGER")
                .build();
    }

    @Bean
    public CronTriggerFactoryBean cronTriggerFactoryBean(JobDetail jobDetail) {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression("0 */1 * ? * * *");
        cronTriggerFactoryBean.setJobDetail(jobDetail);
        return cronTriggerFactoryBean;
    }
}
