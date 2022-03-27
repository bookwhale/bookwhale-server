package com.bookwhale.push.dto;

import lombok.Getter;

@Getter
public class PushMessageParams {
    private final String targetToken;
    private final String title;
    private final String body;

    protected PushMessageParams(String targetToken, String title, String body) {
        this.targetToken = targetToken;
        this.title = title;
        this.body = body;
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
        sb.append('}');
        return sb.toString();
    }

    public static final class PushMessageParamsBuilder {

        private String targetToken;
        private String title;
        private String body;

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

        public PushMessageParams build() {
            return new PushMessageParams(targetToken, title, body);
        }
    }
}
