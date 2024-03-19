package com.nelumbo.parqueadero.services;

import com.nelumbo.parqueadero.dto.response.UploadFileResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface IAWSS3Service {

    void uploadFile(MultipartFile file);
    List<String> getObjectsFromS3();
    Date getObjectUploadDate(String objectKey);

    String getNameFileS3();

    UploadFileResponseDto uploadFileExcel(byte[] fileBytes);

    InputStream dowloadFile (String key);

    InputStream descargarArchivo ();
}
