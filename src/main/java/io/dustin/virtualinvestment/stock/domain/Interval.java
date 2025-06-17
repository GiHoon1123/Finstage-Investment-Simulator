package io.dustin.virtualinvestment.stock.domain;

import lombok.Getter;

/**
 * 차트의 주기(interval)를 정의합니다.
 * Yahoo Finance API와 통신 시 사용됩니다.
 */
@Getter
public enum Interval {
    _1m("1m"), _2m("2m"), _5m("5m"), _15m("15m"),
    _30m("30m"), _60m("60m"), _90m("90m"), _1h("1h"),
    _4h("4h"), _1d("1d"), _5d("5d"), _1wk("1wk"),
    _1mo("1mo"), _3mo("3mo");

    private final String value;

    Interval(String value) {
        this.value = value;
    }

}
