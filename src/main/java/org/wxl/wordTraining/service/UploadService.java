package org.wxl.wordTraining.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wxl
 */
public interface UploadService {
    /**
     * 上传文件
     *
     * @param avatarUrl 文件名
     * @return 返回图片路径
     */
    String uploadImg(MultipartFile avatarUrl, HttpServletRequest request);
}
