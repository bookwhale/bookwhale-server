package com.teamherb.bookstoreback.image.domain;

import com.teamherb.bookstoreback.post.domain.Post;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Enumerated(EnumType.STRING)
    private ImageType type;

    private String path;

    private Image(Post post, ImageType type, String path) {
        this.post = post;
        this.type = type;
        this.path = path;
    }

    public static List<Image> createPostImage(Post post, List<String> uploadFilePaths) {
        return uploadFilePaths.stream().map(
            v -> new Image(post, ImageType.POST, v)
        ).collect(Collectors.toList());
    }
}
