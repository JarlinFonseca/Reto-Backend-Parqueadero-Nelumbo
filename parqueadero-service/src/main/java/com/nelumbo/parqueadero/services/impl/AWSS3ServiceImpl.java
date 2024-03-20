package com.nelumbo.parqueadero.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.nelumbo.parqueadero.dto.response.UploadFileResponseDto;
import com.nelumbo.parqueadero.repositories.ReporteRepository;
import com.nelumbo.parqueadero.services.IAWSS3Service;
import com.nelumbo.parqueadero.services.IToken;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AWSS3ServiceImpl implements IAWSS3Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceImpl.class);
    private final AmazonS3 amazonS3;
    private final ReporteRepository reporteRepository;
    private final IToken token;
    private final Random random = new Random();

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public void uploadFile(MultipartFile file) {
        File mainFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream stream = new FileOutputStream(mainFile)) {
            stream.write(file.getBytes());
            String newFileName = System.currentTimeMillis() + "_" + mainFile.getName();
            LOGGER.info("Subiendo archivo con el nombre... {}", newFileName);
            PutObjectRequest request = new PutObjectRequest(bucketName, newFileName, mainFile);
            amazonS3.putObject(request);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }


    }

    @Override
    public List<String> getObjectsFromS3() {
        ListObjectsV2Result result = amazonS3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objets = result.getObjectSummaries();
        return objets.stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }

    @Override
    public Date getObjectUploadDate(String objectKey) {
        S3Object object = amazonS3.getObject(bucketName, objectKey);
        return object.getObjectMetadata().getLastModified();
    }

    @Override
    public String getNameFileS3() {
        String tokenBearer = token.getBearerToken();
        Long idUsuario = token.getUsuarioAutenticadoId(tokenBearer);


        String key = reporteRepository.findUltimoReporteNombreArchivoPorUsuarioId(idUsuario).orElseThrow();
        S3Object object = amazonS3.getObject(bucketName, key);
        // Obtener el nombre del archivo del objeto S3
        return object.getKey();
    }

    @Override
    public InputStream dowloadFile(String key) {
        S3Object object = amazonS3.getObject(bucketName, key);
        return object.getObjectContent();
    }

    @Override
    public InputStream descargarArchivo() {
        String tokenBearer = token.getBearerToken();
        Long idUsuario = token.getUsuarioAutenticadoId(tokenBearer);

        String key = reporteRepository.findUltimoReporteNombreArchivoPorUsuarioId(idUsuario).orElseThrow();
        S3Object object = amazonS3.getObject(bucketName, key);

        return object.getObjectContent();
    }

    public UploadFileResponseDto uploadFileExcel(byte[] fileBytes) {

        UploadFileResponseDto uploadFileResponseDto = new UploadFileResponseDto();
        try {
            File mainFile = File.createTempFile("temp-file", null);
            try (FileOutputStream stream = new FileOutputStream(mainFile)) {
                stream.write(fileBytes);
                String uniqueId = UUID.randomUUID().toString();
                long numero = random.nextLong();
                String newFileName = uniqueId + numero + "_reporte.xlsx";
                uploadFileResponseDto.setNombreArchivo(newFileName);
                LOGGER.info("Subiendo archivo con el nombre... {}", newFileName);
                PutObjectRequest request = new PutObjectRequest(bucketName, newFileName, mainFile);
                amazonS3.putObject(request);

            }
            // Eliminar el archivo temporal utilizando Files.delete
            Path tempFilePath = mainFile.toPath();
            Files.delete(tempFilePath);


        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return uploadFileResponseDto;
    }
}
