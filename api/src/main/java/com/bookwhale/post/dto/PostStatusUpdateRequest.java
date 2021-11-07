package com.bookwhale.post.dto;

import com.bookwhale.common.validator.ValueOfEnum;
import com.bookwhale.post.domain.PostStatus;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostStatusUpdateRequest {

  @NotBlank
  @ValueOfEnum(enumClass = PostStatus.class)
  private String postStatus;
}
