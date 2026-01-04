package com.example.saga.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "services")
public class DownstreamServicesProperties {

    private Service account;
    private Service transaction;

    public static class Service {
        private String baseUrl;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public Service getAccount() {
        return account;
    }

    public void setAccount(Service account) {
        this.account = account;
    }

    public Service getTransaction() {
        return transaction;
    }

    public void setTransaction(Service transaction) {
        this.transaction = transaction;
    }
}
