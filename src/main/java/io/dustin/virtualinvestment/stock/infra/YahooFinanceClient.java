package io.dustin.virtualinvestment.stock.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dustin.virtualinvestment.common.annotation.ExternalApiClient;
import io.dustin.virtualinvestment.stock.domain.Candle;
import io.dustin.virtualinvestment.stock.domain.Interval;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ExternalApiClient
@RequiredArgsConstructor
public class YahooFinanceClient {

    private final ObjectMapper objectMapper;

    public List<Candle> fetchCandles(String symbol, Interval interval) {
        try {
            // 예: https://query1.finance.yahoo.com/v8/finance/chart/AAPL?interval=1m&range=1d
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://query1.finance.yahoo.com/v8/finance/chart/" + symbol)
                    .queryParam("interval", interval.getValue())
                    .queryParam("range", "1d")
                    .toUriString();

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Yahoo Finance 응답 실패: HTTP " + conn.getResponseCode());
            }

            JsonNode root = objectMapper.readTree(conn.getInputStream());
            JsonNode result = root.path("chart").path("result").get(0);
            JsonNode timestamps = result.path("timestamp");
            JsonNode indicators = result.path("indicators").path("quote").get(0);

            List<Candle> candles = new ArrayList<>();
            for (int i = 0; i < timestamps.size(); i++) {
                Instant timestamp = Instant.ofEpochSecond(timestamps.get(i).asLong());
                double open = indicators.path("open").get(i).asDouble();
                double high = indicators.path("high").get(i).asDouble();
                double low = indicators.path("low").get(i).asDouble();
                double close = indicators.path("close").get(i).asDouble();
                long volume = indicators.path("volume").get(i).asLong();

                candles.add(new Candle(timestamp, open, high, low, close, volume));
            }

            return candles;

        } catch (IOException e) {
            throw new RuntimeException("Yahoo Finance 요청 중 오류 발생", e);
        }
    }
}
