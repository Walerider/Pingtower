package com.walerider.pingdom.api.entitys;

import java.util.Date;
import java.util.List;

public class SiteDTO {
    Long id;
    String url;
    String status;

    String response_time_ms;
    Boolean ssl_valid;
    String ssl_expires_at;
    Integer ssl_days_left;
    String ssl_error;
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
        if(status == null){
            return "offline";
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public SiteDTO(String url) {
        this.url = url;
    }

    public String getResponse_time_ms() {
        if(response_time_ms == null){
            return "0";
        }
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

    public SiteDTO(Long id, String url, String status, String response_time_ms, List<EndpointDTO> endpoints) {
        this.id = id;
        this.url = url;
        this.status = status;
        this.response_time_ms = response_time_ms;
        this.endpoints = endpoints;
    }

    public Boolean getSsl_valid() {
        if(ssl_valid == null){
            return false;
        }
        return ssl_valid;
    }

    public void setSsl_valid(Boolean ssl_valid) {

        this.ssl_valid = ssl_valid;
    }

    public String getSsl_expires_at() {
        if(ssl_expires_at == null){
            return "";
        }
        return ssl_expires_at;
    }

    public void setSsl_expires_at(String ssl_expires_at) {
        this.ssl_expires_at = ssl_expires_at;
    }

    public Integer getSsl_days_left() {
        if(ssl_days_left == null){
            return 0;
        }
        return ssl_days_left;
    }

    public void setSsl_days_left(Integer ssl_days_left) {
        this.ssl_days_left = ssl_days_left;
    }

    public String getSsl_error() {
        if(ssl_error == null){
            return "";
        }
        return ssl_error;
    }

    public void setSsl_error(String ssl_error) {
        this.ssl_error = ssl_error;
    }

    public List<EndpointDTO> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointDTO> endpoints) {
        this.endpoints = endpoints;
    }

    public SiteDTO() {
    }

    @Override
    public String toString() {
        return "SiteDTO{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", status='" + status + '\'' +
                ", response_time_ms='" + response_time_ms + '\'' +
                ", endpoints=" + endpoints +
                '}';
    }
}
