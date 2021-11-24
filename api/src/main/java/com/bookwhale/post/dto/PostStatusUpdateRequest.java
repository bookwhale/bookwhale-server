package com.bookwhale.post.dto;

import com.bookwhale.common.validator.ValueOfEnum;
import com.bookwhale.post.domain.PostStatus;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostStatusUpdateRequest {

    @NotBlank
    @ValueOfEnum(enumClass = PostStatus.class)
    private String postStatus;

    public String getPostStatus() {
        return postStatus;
    }

    public PostStatus getPostStatusByName(String name) {
        return PostStatus.valueOf(name);
    }
}
