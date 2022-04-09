package com.bookwhale.common.domain;

import lombok.Getter;

@Getter
public enum ActiveYn {
    Y("활성"),
    N("비활성");

    private final String statusName;

    ActiveYn(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ActiveYn{");
        sb.append("name='").append(name()).append('\'');
        sb.append("statusName='").append(statusName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
