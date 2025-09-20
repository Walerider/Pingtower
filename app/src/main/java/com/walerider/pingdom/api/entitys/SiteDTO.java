package com.walerider.pingdom.api.entitys;

import java.util.List;

public class SiteDTO {
    Long id;
    String url;
    String status;

    String response_time_ms;
    List<EndpointDTO> endpoints;

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public SiteDTO(String url) {
        this.url = url;
    }

    public String getResponse_time_ms() {
        return response_time_ms;
    }

    public void setResponse_time_ms(String response_time_ms) {
        this.response_time_ms = response_time_ms;
    }

    public SiteDTO(String url, String status, String response_time_ms) {
        this.url = url;
        this.status = status;
        this.response_time_ms = response_time_ms;
    }

    public SiteDTO(String url, String status, String response_time_ms, List<EndpointDTO> list) {
        this.url = url;
        this.status = status;
        this.response_time_ms = response_time_ms;
        this.endpoints = list;
    }

    public SiteDTO() {
    }

    @Override
    public String toString() {
        return "SiteDTO{" +
                "url='" + url + '\'' +
                ", status='" + status + '\'' +
                ", response_time_ms='" + response_time_ms + '\'' +
                ", endpoints=" + endpoints +
                '}';
    }
}
