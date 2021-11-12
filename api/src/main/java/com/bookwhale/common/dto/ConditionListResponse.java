package com.bookwhale.common.dto;

import com.bookwhale.common.domain.Location;
import com.bookwhale.post.domain.BookStatus;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConditionListResponse {

  private String code;
  private String name;

  @Builder
  public ConditionListResponse(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public static ConditionListResponse of(String code, String name) {
    Preconditions.checkArgument(StringUtils.isNotEmpty(code),
        "조건 목록 - 코드 값은 빈 값이 될 수 없습니다.");

    return ConditionListResponse.builder()
        .code(code)
        .name(name)
        .build();
  }

  public static List<ConditionListResponse> listOfBookStatus() {
    return Arrays.stream(BookStatus.values())
        .map(status ->
            ConditionListResponse.of(status.getCode(), status.getName()))
        .collect(Collectors.toList());
  }

  public static List<ConditionListResponse> listOfSellingLocation() {
    return Arrays.stream(Location.values())
        .map(location ->
            ConditionListResponse.of(location.getCode(), location.getName()))
        .collect(Collectors.toList());
  }

}
