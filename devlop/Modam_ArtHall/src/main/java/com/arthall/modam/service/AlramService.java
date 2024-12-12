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
    public List<AlramEntitiy> findAlramByReaded(boolean readed) {
        return alramRepository.findAllByReadedOrderByIdDesc(readed);
    }

    // 알람 삭제 (일정 시간마다? or 관리자 대시보드 접속시 사라지게)1
    public void deleteAllByReaded(boolean readed) {
        List<AlramEntitiy> deletedAlarms = alramRepository.findAllByReaded(readed);
        alramRepository.deleteAllByReaded(readed);
        System.out.println("삭제된 알람 수: " + deletedAlarms.size());
    }

    @Transactional
    public boolean markAsRead(int id) {
        Optional<AlramEntitiy> optionalAlram = alramRepository.findById(id);
        if (optionalAlram.isPresent()) {
            AlramEntitiy alram = optionalAlram.get();
            alram.setReaded(true); // 읽음 처리
            alramRepository.save(alram);
            return true;
        }
        return false;
    }

}
