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

    /**
     * 上传文章图片
     * @param avatar 文件名
     * @param request 当前用户信息
     * @return 图片路径
     */
    String uploadArticleImg(MultipartFile avatar, HttpServletRequest request);
}
