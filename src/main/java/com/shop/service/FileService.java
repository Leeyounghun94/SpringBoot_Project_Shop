package com.shop.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    public  String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {

        UUID uuid = UUID.randomUUID();// UUID는 서로 다른 개체 구별하기 위해 이름 부여할때 사용, 중복 문제 해결
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension; // uuid 받은 값과 원래 파일의 확장자 조합해서 파일 이름을 저장
        String fileuploadFullUrl = uploadPath + "/" + savedFileName;

        FileOutputStream fos = new FileOutputStream(fileuploadFullUrl);// 바이트 단위의 출력을 보내내는 클래스, 생성자로 파일이 저장될 위치, 파일 이름을 넘겨 파일 출력 스트림을 생성
        fos.write(fileData);//  파일 출력 스트림을 파일데이터 입력
        fos.close();

        return savedFileName;   // 업로드된 파일 이름 반환
    }

    public void deleteFile(String filePath) throws Exception {

        File deleteFile = new File(filePath);   // 저장된 경로 이용하여 파일 객체 생성

        if (deleteFile.exists()) {// 해당 파일이 있으면 삭제~
            deleteFile.delete();
            log.info("파일 삭제 완료 !");
        }else  {
            log.info("파일이 존재하지 않습니다. 다시 확인해 주세요");
        }
    }
}
