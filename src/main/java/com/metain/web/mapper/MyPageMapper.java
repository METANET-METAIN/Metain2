package com.metain.web.mapper;

import com.metain.web.domain.Emp;
import com.metain.web.domain.Issue;
import com.metain.web.domain.Vacation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyPageMapper {

    /**개인정보 수정*/
    public int updateMyPage(Emp emp);

    /**휴가 신청 현황 조회*/
    public List<Vacation> selectVacAll();


    /**증명서 발급 내역 조회*/
    public List<Issue> selectIssueAll();


    /**증명서 다운로드*/
    public int certDownload(Long issueId);




}