package com.arthall.modam.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.AlramEntitiy;

@Repository
public interface AlramRepository extends JpaRepository<AlramEntitiy, Integer> {

    // readed(0)인 안읽은 메시지 가져오기
    List<AlramEntitiy> findAllByReadedOrderByIdDesc(boolean reded);

    // readed(1) 인 알람 삭제하기
    void deleteAllByReaded(boolean readed);

    // 읽은알람찾기
    List<AlramEntitiy> findAllByReaded(boolean readed);

}
