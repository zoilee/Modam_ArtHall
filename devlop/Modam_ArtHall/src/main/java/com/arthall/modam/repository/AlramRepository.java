package com.arthall.modam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.AlramEntitiy;

@Repository
public interface AlramRepository extends JpaRepository<AlramEntitiy, Integer> {

    // id랑 readed(0) 로 안읽은 메시지 찾기
    Optional<AlramEntitiy> findAlramByIdAndReadedOrderByCreatedAtDesc(int id, boolean readed);

    // readed(1) 인 알람 삭제하기
    void deleteAllByReaded(boolean readed);

    // 읽은알람찾기
    List<AlramEntitiy> findAllByReaded(boolean readed);
}
