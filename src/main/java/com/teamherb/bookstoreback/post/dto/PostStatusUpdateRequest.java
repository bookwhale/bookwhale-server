package com.teamherb.bookstoreback.post.dto;

import com.teamherb.bookstoreback.common.validator.ValueOfEnum;
import com.teamherb.bookstoreback.post.domain.PostStatus;
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
