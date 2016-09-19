package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * Created by Vinod on 19/9/16.
 */
@Component
@ConfigurationProperties(prefix = "catalyst")
public class CatalystSettings {
    private String cron;
    private String host;
    private String key;

    public String getCron() {
        return cron;
    }

    public CatalystSettings setCron(String cron) {
        this.cron = cron;
        return this;
    }

    public String getHost() {
        return host;
    }

    public CatalystSettings setHost(String host) {
        this.host = host;
        return this;
    }

    public String getKey() {
        return key;
    }

    public CatalystSettings setKey(String key) {
        this.key = key;
        return this;
    }

    public int getFirstRunHistoryDays() {
        return firstRunHistoryDays;
    }

    public CatalystSettings setFirstRunHistoryDays(int firstRunHistoryDays) {
        this.firstRunHistoryDays = firstRunHistoryDays;
        return this;
    }

//    public String getApi() {
//        return api;
//    }
//
//    public CatalystSettings setApi(String api) {
//        this.api = api;
//        return this;
//    }
//
//    public int getPageSize() {
//        return pageSize;
//    }
//
//    public CatalystSettings setPageSize(int pageSize) {
//        this.pageSize = pageSize;
//        return this;
//    }

    public String getCatalystBaseUrl() {
        return catalystBaseUrl;
    }

    public CatalystSettings setCatalystBaseUrl(String catalystBaseUrl) {
        this.catalystBaseUrl = catalystBaseUrl;
        return this;
    }

    public String getCatalystUserName() {
        return catalystUserName;
    }

    public CatalystSettings setCatalystUserName(String catalystUserName) {
        this.catalystUserName = catalystUserName;
        return this;
    }

    public String getCatalystPassword() {
        return catalystPassword;
    }

    public CatalystSettings setCatalystPassword(String catalystPassword) {
        this.catalystPassword = catalystPassword;
        return this;
    }

    private int firstRunHistoryDays;
//    private String api;
//    private int pageSize;
    private String catalystBaseUrl;
    private String catalystUserName;
    private String catalystPassword;

}
