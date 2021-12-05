package com.bookwhale.article.dto;

import com.bookwhale.article.domain.ArticleStatus;
import com.bookwhale.common.validator.ValueOfEnum;
import com.bookwhale.post.domain.PostStatus;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ArticleStatusUpdateRequest {

    @NotBlank
    @ValueOfEnum(enumClass = ArticleStatus.class)
    private String articleStatus;

    public String getArticleStatus() {
        return this.articleStatus;
    }

    public PostStatus getArticleStatusByName(String name) {
        return ArticleStatus.valueOf(name);
    }
}
