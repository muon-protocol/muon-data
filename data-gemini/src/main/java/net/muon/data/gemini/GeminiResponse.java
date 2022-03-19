package net.muon.data.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {
    @JsonProperty(required = true)
    private String type;
    @JsonProperty(required = true, value = "timestampms")
    private long time;
    @JsonProperty(required = true)
    private List<Event> events;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Response{" +
                "type='" + type + '\'' +
                ", timestampms=" + time +
                ", events=" + events +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Event {
        @JsonProperty(required = true)
        private String type;
        @JsonProperty(required = true)
        private BigDecimal price;
        @JsonProperty(required = true)
        private String symbol;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "type='" + type + '\'' +
                    ", price=" + price +
                    ", symbol='" + symbol + '\'' +
                    '}';
        }
    }
}