package com.bookwhale.common.dto;

import com.bookwhale.common.domain.Location;
import com.bookwhale.article.domain.BookStatus;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ConditionListResponse {

    private String code;
    private String name;

    @Builder
    public ConditionListResponse(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConditionListResponse that = (ConditionListResponse) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "ConditionListResponse{" +
            "code='" + code + '\'' +
            ", name='" + name + '\'' +
            '}';
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
