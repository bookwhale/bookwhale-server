package com.bookwhale.push.dto;

import com.bookwhale.common.domain.ActiveYn;
import lombok.Getter;

@Getter
public class PushMessageParams {
    private final String targetToken;
    private final String title;
    private final String body;
    private final ActiveYn pushActivateStatus;

    protected PushMessageParams(String targetToken, String title, String body,
        ActiveYn pushActivateStatus) {
        this.targetToken = targetToken;
        this.title = title;
        this.body = body;
        this.pushActivateStatus = pushActivateStatus == null ? ActiveYn.N : pushActivateStatus;
    }

    public static PushMessageParamsBuilder builder() {
        return new PushMessageParamsBuilder();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PushMessageParams{");
        sb.append("targetToken='").append(targetToken).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", pushActivateStatus=").append(pushActivateStatus);
        sb.append('}');
        return sb.toString();
    }

    public static final class PushMessageParamsBuilder {

        private String targetToken;
        private String title;
        private String body;
        private ActiveYn pushActivateStatus;

        private PushMessageParamsBuilder() {
        }

        public PushMessageParamsBuilder targetToken(String targetToken) {
            this.targetToken = targetToken;
            return this;
        }

        public PushMessageParamsBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PushMessageParamsBuilder body(String body) {
            this.body = body;
            return this;
        }

        public PushMessageParamsBuilder pushActivateStatus(ActiveYn pushActivateStatus) {
            this.pushActivateStatus = pushActivateStatus;
            return this;
        }

        public PushMessageParams build() {
            return new PushMessageParams(targetToken, title, body, pushActivateStatus);
        }
    }
}
