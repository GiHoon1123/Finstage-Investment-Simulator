package io.dustin.virtualinvestment.stock.service;

import io.dustin.virtualinvestment.stock.domain.Candle;
import io.dustin.virtualinvestment.stock.domain.Interval;
import io.dustin.virtualinvestment.stock.dto.CandleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * WebSocket(STOMP)을 통해 클라이언트로 실시간 데이터를 전송하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트에 캔들 데이터를 전송합니다.
     *
     * @param symbol   주식 심볼 (예: AAPL)
     * @param interval 주기 (예: 1m, 5m 등)
     * @param candles  전송할 캔들 데이터 목록
     */
    public void broadcast(String symbol, Interval interval, List<CandleResponse> candles) {
        String destination = "/topic/stocks/" + symbol + "/" + interval.getValue();
        log.info("🔊 WebSocket 전송: {}", destination); // 로그 추가
        messagingTemplate.convertAndSend(destination, candles);
    }
}
