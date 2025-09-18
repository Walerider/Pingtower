package com.walerider.pingdom.api.entitys;

public class SiteDTO {
    Long id;
    String url;
    String status;
    Integer responseTimeMs;

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

    public Integer getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    @Override
    public String toString() {
        return "SiteDTO{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", status='" + status + '\'' +
                ", responseTimeMs=" + responseTimeMs +
                '}';
    }
}
