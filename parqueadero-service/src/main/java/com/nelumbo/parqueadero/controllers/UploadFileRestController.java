package com.nelumbo.parqueadero.controllers;

import com.nelumbo.parqueadero.services.IAWSS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class UploadFileRestController {
    private final IAWSS3Service awss3Service;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadFile(@RequestPart(value = "file")MultipartFile file){
        awss3Service.uploadFile(file);
        String response = "El archivo "+file.getOriginalFilename()+" fue cargado correctamente a S3";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<String>> listFiles(){
        return new ResponseEntity<>(awss3Service.getObjectsFromS3(), HttpStatus.OK);
    }

    @GetMapping(value = "/date")
    public ResponseEntity<Date> listFiles(@RequestParam("key") String param){
        return new ResponseEntity<>(awss3Service.getObjectUploadDate(param), HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> dowload(@RequestParam("key") String param){
        InputStreamResource resource = new InputStreamResource(awss3Service.dowloadFile(param));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+resource.getFilename()+"\"").body(resource);
    }


}
