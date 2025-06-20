package io.dustin.virtualinvestment.stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dustin.virtualinvestment.stock.domain.Candle;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalCandleResponse {

    @JsonProperty("chart")
    private Chart chart;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Chart {

        @JsonProperty("result")
        private List<Result> result;

        @JsonProperty("error")
        private Object error;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {

        @JsonProperty("meta")
        private Meta meta;

        @JsonProperty("timestamp")
        private List<Long> timestamp;

        @JsonProperty("indicators")
        private Indicators indicators;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {

        @JsonProperty("symbol")
        private String symbol;

        @JsonProperty("dataGranularity")
        private String interval;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Indicators {

        @JsonProperty("quote")
        private List<Quote> quote;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Quote {

        private List<Double> open;
        private List<Double> high;
        private List<Double> low;
        private List<Double> close;
        private List<Long> volume;
    }

    public static List<Candle> from(ExternalCandleResponse.Result result) {
        String symbol = result.getMeta().getSymbol();
        String interval = result.getMeta().getInterval();
        List<Long> timestamps = result.getTimestamp();
        ExternalCandleResponse.Quote quote = result.getIndicators().getQuote().get(0);

        return buildCandles(symbol, interval, timestamps, quote);
    }

    private static List<Candle> buildCandles(String symbol, String interval, List<Long> timestamps, ExternalCandleResponse.Quote quote) {
        List<Double> opens = quote.getOpen();
        List<Double> highs = quote.getHigh();
        List<Double> lows = quote.getLow();
        List<Double> closes = quote.getClose();
        List<Long> volumes = quote.getVolume();

        List<Candle> candles = new ArrayList<>();
        for (int i = 0; i < timestamps.size(); i++) {
            if (opens.get(i) == null || highs.get(i) == null || lows.get(i) == null || closes.get(i) == null || volumes.get(i) == null) {
                continue;
            }

            candles.add(Candle.from(
                    symbol,
                    interval,
                    timestamps.get(i),
                    opens.get(i),
                    highs.get(i),
                    lows.get(i),
                    closes.get(i),
                    volumes.get(i)
            ));
        }

        return candles;
    }
}