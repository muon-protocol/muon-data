package net.muon.data.gateio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
class GateioResponse {
    @JsonProperty(required = true)
    private long time;
    @JsonProperty(required = true)
    private String channel;

    @JsonProperty(required = true)
    private String event;

    private Result result;

    private ErrorResult error;

    public ErrorResult getError() {
        return error;
    }

    public void setError(ErrorResult error) {
        this.error = error;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "net.muon.data.gateio.GateioResponse{" +
                "time=" + time +
                ", channel='" + channel + '\'' +
                ", event='" + event + '\'' +
                ", result=" + result +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Result {
        private String status;
        @JsonProperty("create_time_ms")
        private BigDecimal time;
        @JsonProperty()
        private BigDecimal price;
        @JsonProperty("currency_pair")
        private String pair;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public BigDecimal getTime() {
            return time;
        }

        public void setTime(BigDecimal time) {
            this.time = time;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getPair() {
            return pair;
        }

        public void setPair(String pair) {
            this.pair = pair;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "status='" + status + '\'' +
                    ", time=" + time +
                    ", price=" + price +
                    ", pair='" + pair + '\'' +
                    '}';
        }
    }

    static class ErrorResult {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "ErrorResult{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}