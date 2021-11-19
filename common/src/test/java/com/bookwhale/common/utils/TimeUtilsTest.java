package com.bookwhale.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookwhale.utils.TimeUtils;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TimeUtilsTest {

    @DisplayName("BeforeTime Test")
    @Test
    void BeforeTime() {
        LocalDateTime cur = LocalDateTime.of(2021, 10, 2, 17, 0, 0);
        LocalDateTime year = LocalDateTime.of(2019, 10, 3, 12, 0, 0);
        LocalDateTime month = LocalDateTime.of(2021, 7, 3, 12, 0, 0);
        LocalDateTime day = LocalDateTime.of(2021, 9, 15, 12, 0, 0);
        LocalDateTime hours = LocalDateTime.of(2021, 10, 2, 5, 30, 20);
        LocalDateTime minute = LocalDateTime.of(2021, 10, 2, 16, 20, 40);
        LocalDateTime second = LocalDateTime.of(2021, 10, 2, 16, 59, 30);

        assertThat(TimeUtils.BeforeTime(cur, year)).isEqualTo("2년 전");
        assertThat(TimeUtils.BeforeTime(cur, month)).isEqualTo("3달 전");
        assertThat(TimeUtils.BeforeTime(cur, day)).isEqualTo("17일 전");
        assertThat(TimeUtils.BeforeTime(cur, hours)).isEqualTo("11시간 전");
        assertThat(TimeUtils.BeforeTime(cur, minute)).isEqualTo("39분 전");
        assertThat(TimeUtils.BeforeTime(cur, second)).isEqualTo("30초 전");
    }
}
