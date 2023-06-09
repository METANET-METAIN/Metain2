package com.metain.web.service;

import com.metain.web.domain.Emp;
import com.metain.web.domain.EmpCert;
import com.metain.web.domain.ExperienceCert;
import com.metain.web.domain.RetireCert;
import com.metain.web.dto.AlarmDTO;
import com.metain.web.dto.MyVacDTO;
import com.metain.web.mapper.AlarmMapper;
import com.metain.web.mapper.HrMapper;
import com.metain.web.mapper.MyPageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class MyPageServiceImpl implements MyPageService{


    @Autowired
    private AwsS3Service awsS3Service;
    @Autowired
    private MyPageMapper myPageMapper;
    @Autowired
    private AlarmMapper alarmMapper;

    @Autowired
    private HrMapper hrMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public List<MyVacDTO> myVacList(Long empId) {
        List<MyVacDTO> list =myPageMapper.myVacList(empId);
        return list;
    }

    //재직증명서 목록
    @Override
    public List<EmpCert> selectMyEmpCert(Long empId) {
        List<EmpCert> list = myPageMapper.selectMyEmpCert(empId);
        return list;
    }


    //경력증명서 목록
    @Override
    public List<ExperienceCert> selectMyExperCert(Long empId) {
        List<ExperienceCert> list = myPageMapper.selectMyExperCert(empId);
        return list;
    }

    //퇴직증명서 목록
    @Override
    public List<RetireCert> selectMyRetCert(Long empId) {
        List<RetireCert> list = myPageMapper.selectMyRetCert(empId);
        return list;
    }

    //다운로드할 증명서 파일이름가져오기
    @Override
    public String getCertFilename(Long certId, String certSort){


        String certFilename="";

        if (certSort.equals("A01")) {
            certFilename = myPageMapper.selectEmpCertFilename(certId);
        }else if (certSort.equals("A02")){
            certFilename = myPageMapper.selectExperCertFilename(certId);
        }else if (certSort.equals("A03")){
            certFilename = myPageMapper.selectRetireCertFilename(certId);
        }
        return certFilename;
    }

    public void updateIssueStatus(Long certId, String certSort){

        if (certSort.equals("A01") ) {
            myPageMapper.updateEmpIssueStatus(certId);
        }else if (certSort.equals("A02")){
            myPageMapper.updateExperIssueStatus(certId);
        }else if (certSort.equals("A03")){
            myPageMapper.updateRetireIssueStatus(certId);
        }else {
            logger.info("Issue Status 업데이트할 정보 [[없음]]");
        }
    }



    @Override
    public List<AlarmDTO> alarmList(Long empId) {
        List<AlarmDTO> list= alarmMapper.alarmListAll(empId);
        if(list==null){
            return null;
        }else return list;

    }
    @Override
    public void updateMy(Emp emp, MultipartFile file) throws IOException {
        Emp dbemp = hrMapper.selectEmpInfo(emp.getEmpId());
        dbemp.setEmpAddr(emp.getEmpAddr());
        dbemp.setEmpPhone(emp.getEmpPhone());
        dbemp.setEmpZipcode(emp.getEmpZipcode());
        dbemp.setEmpDetailAddr(emp.getEmpDetailAddr());
        String sabun = dbemp.getEmpSabun();

        if (file != null && !file.isEmpty()) {

            UUID uuid = UUID.randomUUID();

            File files = new File(file.getOriginalFilename());
            FileCopyUtils.copy(file.getBytes(), files);
            String originalImgName = file.getOriginalFilename();
            String extension = "";

            int dotIndex = originalImgName.lastIndexOf(".");
            if (dotIndex >= 0) {
                extension = originalImgName.substring(dotIndex);
            }

            String savedImgName = sabun + uuid.toString().substring(0, 5) + extension;
            String path = "user";
            dbemp.setEmpProfile(savedImgName);


            awsS3Service.updateProfileInS3(file.getBytes(), savedImgName, path);
        } else {
            // 사진이 첨부되지 않은 경우에는 기존 사진 정보를 그대로 유지합니다.
            dbemp.setEmpProfile(dbemp.getEmpProfile());
        }




        myPageMapper.updateMyPage(dbemp);
    }

    public void updatePwd(Emp emp) {
        Emp dbemp = hrMapper.selectEmpInfo(emp.getEmpId()) ;
        String encryptedPwd = bCryptPasswordEncoder.encode(emp.getEmpPwd());
        dbemp.setEmpPwd(encryptedPwd);
        logger.info("encryptedPwd: {}", encryptedPwd);
        myPageMapper.updatePwd(dbemp);
    }


}