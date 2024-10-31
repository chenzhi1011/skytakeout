package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@Slf4j
@RequestMapping("/admin/common")
public class CommonController {
    @PostMapping("/upload")
    public Result<String> uploadFile(MultipartFile file){
        return Result.success("https://web-tilas29.oss-ap-northeast-1.aliyuncs.com/1.jpg");
    }
}
