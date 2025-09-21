package com.walerider.pingdom.api.entitys;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class PingtowerResponse {

    @SerializedName("uptime_percent")
    private double uptimePercent;

    @SerializedName("down_time_percent")
    private double downTimePercent;

    @SerializedName("http_codes")
    private Map<String, HttpCodeInfo> httpCodes;

    @SerializedName("avg_response_time")
    private double avgResponseTime;

    @SerializedName("max_response_time")
    private int maxResponseTime;

    @SerializedName("ssl")
    private SslInfo ssl;

    @SerializedName("events")
    private List<EventDTO> events;

    public PingtowerResponse() {}

    public double getUptimePercent() {
        return uptimePercent;
    }

    public double getDownTimePercent() {
        return downTimePercent;
    }

    public Map<String, HttpCodeInfo> getHttpCodes() {
        return httpCodes;
    }

    public double getAvgResponseTime() {
        return avgResponseTime;
    }

    public int getMaxResponseTime() {
        return maxResponseTime;
    }

    public SslInfo getSsl() {
        return ssl;
    }

    public List<EventDTO> getEvents() {
        return events;
    }

    public static class HttpCodeInfo {
        @SerializedName("count")
        private int count;

        @SerializedName("percent")
        private double percent;

        public HttpCodeInfo() {}

        public int getCount() {
            return count;
        }

        public double getPercent() {
            return percent;
        }

        @Override
        public String toString() {
            return "HttpCodeInfo{count=" + count + ", percent=" + percent + '}';
        }
    }

    public static class SslInfo {
        @SerializedName("valid")
        private int valid;

        @SerializedName("expires_at")
        private String expiresAt;

        @SerializedName("days_left")
        private int daysLeft;

        public SslInfo() {}

        public int getValid() {
            return valid;
        }

        public String getExpiresAt() {
            return expiresAt;
        }

        public int getDaysLeft() {
            return daysLeft;
        }

        @Override
        public String toString() {
            return "SslInfo{valid=" + valid + ", expiresAt='" + expiresAt + "', daysLeft=" + daysLeft + '}';
        }
    }

    public static class EventDTO {
        @SerializedName("time")
        private String time;

        @SerializedName("status")
        private String status;

        @Nullable
        @SerializedName("http_code")
        private Integer httpCode;

        @Nullable
        @SerializedName("response_time_ms")
        private Long responseTimeMs;

        @Nullable
        @SerializedName("ssl_valid")
        private Integer sslValid;

        public EventDTO() {}

        public String getTime() {
            return time;
        }

        public String getStatus() {
            return status;
        }

        @Nullable
        public Integer getHttpCode() {
            return httpCode;
        }

        @Nullable
        public Long getResponseTimeMs() {
            return responseTimeMs;
        }

        @Nullable
        public Integer getSslValid() {
            return sslValid;
        }

        @Override
        public String toString() {
            return "EventDTO{" +
                    "time='" + time + '\'' +
                    ", status='" + status + '\'' +
                    ", httpCode=" + httpCode +
                    ", responseTimeMs=" + responseTimeMs +
                    ", sslValid=" + sslValid +
                    '}';
        }
    }
}