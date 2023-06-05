package com.metain.web.service;

import com.metain.web.domain.File;
import com.metain.web.domain.Vacation;
import com.metain.web.dto.FileDTO;
import com.metain.web.dto.VacationListDTO;
import com.metain.web.mapper.FileMapper;
import com.metain.web.mapper.VacationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class VacationServiceImpl implements VacationService{
    @Autowired
    private VacationMapper vacMapper;
    @Autowired
    private FileMapper fileMapper;

    @Override
    public List<VacationListDTO> selectAllList() {
        List<VacationListDTO> list = vacMapper.selectAllList();
        if (list==null) {
            return null;
        }
            return list;
    }

    @Override
    public Vacation vacationDetail(Long vacationId) {
        Vacation dbVacation=vacMapper.vacDetail(vacationId);
        if (dbVacation==null){
            return null;
        }
        return dbVacation;}

    @Override
    public List<VacationListDTO> requestList() {
        List<VacationListDTO> list = vacMapper.requestList();
        if (list==null) {
            return null;
        }
        return list;
    }

    @Override
    public void approveVacationRequest(Long vacId) {
        int result =vacMapper.approveVacationRequest(vacId);

        if(result==0) {
            new Exception("에러");
        }
    }
    @Override
    public void rejectVacationRequest(Long vacId) {
        int result =vacMapper.rejectVacationRequest(vacId);
        if(result==0) {
            new Exception("에러");
        }
    }

    @Override
    public void cancelVacationRequest(Long vacId, Long empId,String vacStatus) {
        if(vacStatus.equals("승인대기")) {
            int result = vacMapper.cancelVacationRequest(vacId, empId);
        }
    }

    @Override
    public void insertVacation(Vacation vacation) {
        vacMapper.requestVacation(vacation);
    }

    @Override
    public void insertAfterVacation(Vacation vacation) {
        String fn = vacation.getFileName();
        // 파일 정보 삽입
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFileName(fn);
        fileDTO.setEmpId(5L);
        fileMapper.insertFile(fileDTO);
        //인서트 되면 AUTOINCREAMENT 값 가져 와서 VAC에 넣기 
        int fileId = fileMapper.getFileId();
        vacation.setFileId((long) fileId);
        //그로 VAC INSERT 시킨당
        vacMapper.insertAfterVacation(vacation);
    }

}
