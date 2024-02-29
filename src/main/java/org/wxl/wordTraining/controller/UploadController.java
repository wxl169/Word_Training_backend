package org.wxl.wordTraining.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.service.UploadService;
import org.wxl.wordTraining.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wxl
 */
@Api(tags = "上传文件模块")
@RestController
public class UploadController {

   @Resource
    private UploadService uploadService;

    /**
     * 上传文件
     *
     * @param avatar 文件名
     * @return 返回图片路径
     */
    @ApiOperation(value = "上传图片")
    @PostMapping("/upload")
    public BaseResponse<String> uploadImg(MultipartFile avatar, HttpServletRequest request){
        if (avatar == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请上传图片");
        }
        String img = uploadService.uploadImg(avatar,request);
        if (StringUtils.isBlank(img)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"上传图片失败");
        }
        return ResultUtils.success(img);
    }
}
