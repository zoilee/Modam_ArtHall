package com.arthall.modam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.AdminNoticeList;

@Repository
public interface AdminNoticeListRepository extends JpaRepository<AdminNoticeList, Long>{

}
