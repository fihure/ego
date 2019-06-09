package com.ego.upload.service;

import com.ego.upload.controller.UploadController;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
@Slf4j
@Service
public class UploadService {

   // private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    // 支持的文件类型
    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg");

    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    public String upload(MultipartFile file) {
        // 1、图片信息校验
        // 1)校验文件类型
        String type = file.getContentType();
        if(!suffixes.contains(type)){
            log.info("上传失败，文件类型不匹配！{}",type);
            return null;
        }
        //校验图片内容
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                log.info("上传失败，文件内容不符合要求");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
     /*   //保存到硬盘
        // 2、保存图片
//         2.1、生成保存目录
        File dir = new File("H://IDEWorkspace/ego/ego/images");
        if(!dir.exists()){
            dir.mkdirs();
        }*/
//          2.2、保存图片
        /*try {
            file.transferTo(new File(dir, file.getOriginalFilename()));
            // 2.3、拼接图片地址
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        String fullPath=null;
        String ext = StringUtils.substringAfterLast(file.getOriginalFilename(),".");
        try {
            StorePath storePath =fastFileStorageClient.uploadFile(file.getInputStream(),file.getSize(),ext,null);
            fullPath=storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "http://image.ego.com/"+fullPath;
    }
}
