package com.frog.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;


@CrossOrigin(origins = "*") // 允许跨域请求
@RestController
@RequestMapping("/wav")
public class AudioController {

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getAudioFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("D:/qkl-zhny/znwl-agriculture-server-no/wav/" + fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM) // 设置响应体的媒体类型
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
