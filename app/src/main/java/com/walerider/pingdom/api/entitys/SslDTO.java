package com.walerider.pingdom.api.entitys;

public class SslDTO {
    Boolean valid;
    String expires_at;
    Integer days_left;
    String ssl_error;

    public SslDTO(Boolean valid, String expires_at, Integer days_left, String ssl_error) {
        this.valid = valid;
        this.expires_at = expires_at;
        this.days_left = days_left;
        this.ssl_error = ssl_error;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }

    public Integer getDays_left() {
        return days_left;
    }

    public void setDays_left(Integer days_left) {
        this.days_left = days_left;
    }

    public String getSsl_error() {
        return ssl_error;
    }

    public void setSsl_error(String ssl_error) {
        this.ssl_error = ssl_error;
    }
}
