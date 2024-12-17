package com.arthall.modam.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.QnaEntity;
import com.arthall.modam.repository.QnaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QnaService {

    @Autowired
    private final QnaRepository qnaRepository;

    // QnA 목록 조회
    public Page<QnaEntity> getAllQna(Pageable pageable) {
        return qnaRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // 특정 QnA 조회
    public QnaEntity getQnaById(int id) {
        return qnaRepository.findById(id).orElse(null);
    }

    // QnA 생성
    public QnaEntity createQna(QnaEntity qnaEntity) {
        return qnaRepository.save(qnaEntity);
    }

    // QnA 수정
    public QnaEntity updateQna(QnaEntity qnaEntity) {
        return qnaRepository.save(qnaEntity);
    }

    // QnA 삭제
    public void deleteQna(int id) {
        qnaRepository.deleteById(id);
    }

    public Page<QnaEntity> getPagedQnaList(Pageable pageable) {
        return qnaRepository.findAll(pageable);
    }

    // 미처리 모달창 띄우기
    public List<QnaEntity> getUnansweredQuestions() {
        return qnaRepository.findUnansweredQuestions();
    }

    /**** 1209 QnA 수정관련**** */
    public boolean isAuthorized(QnaEntity qna, Authentication authentication) {
        if (!qna.isPrivate()) {
            return true; // 비밀글이 아니면 누구나 접근 가능
        }
        if (authentication == null || !authentication.isAuthenticated()) {
            return false; // 인증되지 않은 사용자
        }

        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        return currentUsername.equals(qna.getUserId()) || isAdmin; // 작성자이거나 관리자인 경우
    }

    /**** 1209 QnA 수정관련**** */

    public boolean canEditQna(QnaEntity qna, Authentication authentication) {
        if (qna.isAnswered()) {
            return false; // 답변 완료된 QnA는 수정 불가
        }
        if (authentication == null || !authentication.isAuthenticated()) {
            return false; // 인증되지 않은 사용자
        }

        String currentUsername = authentication.getName();
        if (!currentUsername.equals(qna.getUserId())) {
            return false; // 일치하지 않은 사용자
        }

        return currentUsername.equals(qna.getUserId()); // 작성자만 수정 가능
    }
}
