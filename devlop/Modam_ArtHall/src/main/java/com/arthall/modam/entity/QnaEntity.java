package com.arthall.modam.entity;


import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "qna")
@Data
public class QnaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 기본 키

    @Column(name = "user_id", nullable = false)
    private String userId; // 사용자 ID

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt; // TIMESTAMP 필드 //

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "contents")
    private String contents;

    @Column(name = "answer")
    private String answer;

    //* */
    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false; // 기본값은 비밀글 아님

    // 답변 완료 여부
    public boolean isAnswered() {
        return answer != null && !answer.isEmpty();
    }



}
