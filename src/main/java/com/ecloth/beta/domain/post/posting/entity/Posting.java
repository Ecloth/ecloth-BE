package com.ecloth.beta.domain.post.posting.entity;

import com.ecloth.beta.common.entity.BaseEntity;
import com.ecloth.beta.domain.member.entity.Member;
import com.ecloth.beta.domain.post.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
public class Posting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postingId;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member writer;
    private String title;
    private String content;

    @OneToMany(mappedBy = "posting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> imageList;
    @OneToMany(mappedBy = "posting")
    private List<Comment> commentList = new ArrayList<>();

    private Long likeCount = 0L;
    private Long viewCount = 0L;


    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeImageList(List<Image> imageList){
        this.imageList = imageList;
    }

    public void increaseViewCount() {
        this.viewCount = 0L;
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount = 0L;
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = 0L;
        this.likeCount--;
    }


    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

}