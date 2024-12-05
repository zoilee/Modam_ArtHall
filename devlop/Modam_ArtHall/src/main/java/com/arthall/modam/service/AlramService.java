package com.arthall.modam.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.AlramEntitiy;
import com.arthall.modam.repository.AlramRepository;

import jakarta.transaction.Transactional;

@Service
public class AlramService {

    @Autowired
    AlramRepository alramRepository;

    // 알람 생성
    public AlramEntitiy createdAlram(AlramEntitiy alramEntitiy) {
        return alramRepository.save(alramEntitiy);
    }

    // 알람 조회
    public Optional<AlramEntitiy> findAlramByIdAndReaded(int id, boolean readed) {
        return alramRepository.findAlramByIdAndReadedOrderByCreatedAtDesc(id, readed);
    }

    @Transactional
    // 알람 읽음
    public void updateReaded(int id, boolean readed) {
        AlramEntitiy alramEntitiy = alramRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("알람을 찾을 수 없습니다."));

        alramEntitiy.setReaded(readed);
    }

    // 알람 삭제 (일정 시간마다? or 관리자 대시보드 접속시 사라지게)
    public void deleteAllByReaded(boolean readed) {
        List<AlramEntitiy> deletedAlarms = alramRepository.findAllByReaded(readed);
        alramRepository.deleteAllByReaded(readed);
        System.out.println("삭제된 알람 수: " + deletedAlarms.size());
    }
}
