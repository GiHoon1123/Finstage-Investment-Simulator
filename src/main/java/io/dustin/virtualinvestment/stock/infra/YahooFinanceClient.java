package io.dustin.virtualinvestment.stock.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dustin.virtualinvestment.common.annotation.ExternalApiClient;
import io.dustin.virtualinvestment.stock.domain.Candle;
import io.dustin.virtualinvestment.stock.domain.Interval;
import io.dustin.virtualinvestment.stock.dto.ExternalCandleResponse;
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

    public ExternalCandleResponse fetchRawCandles(String symbol, Interval interval) {
        try {
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

            return objectMapper.readValue(conn.getInputStream(), ExternalCandleResponse.class);

        } catch (IOException e) {
            throw new RuntimeException("Yahoo Finance 요청 중 오류 발생", e);
        }
    }
}
