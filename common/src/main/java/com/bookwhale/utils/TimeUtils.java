package com.bookwhale.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {

  private static final int SEC = 60;
  private static final int MIN = 60;
  private static final int HOUR = 24;
  private static final int DAY = 30;
  private static final int MONTH = 12;

  public static String BeforeTime(LocalDateTime current, LocalDateTime target) {
    StringBuilder msg = new StringBuilder();
    long diff = Duration.between(target, current).getSeconds();

    if (diff < SEC) {
      msg.append(diff).append("초 전");
    } else if ((diff /= SEC) < MIN) {
      msg.append(diff).append("분 전");
    } else if ((diff /= MIN) < HOUR) {
      msg.append(diff).append("시간 전");
    } else if ((diff /= HOUR) < DAY) {
      msg.append(diff).append("일 전");
    } else if ((diff /= DAY) < MONTH) {
      msg.append(diff).append("달 전");
    } else {
      diff /= MONTH;
      msg.append(diff).append("년 전");
    }
    return msg.toString();
  }
}
