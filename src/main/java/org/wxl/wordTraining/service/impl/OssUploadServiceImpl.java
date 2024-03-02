package org.wxl.wordTraining.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.config.OSSConfig;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.service.UploadService;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.utils.BeanCopyUtils;
import org.wxl.wordTraining.utils.PathUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * @author wxl
 */
@Service
public class OssUploadServiceImpl implements UploadService {
    @Resource
    private OSSConfig ossConfig;
    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadImg(MultipartFile avatarUrl,HttpServletRequest request) {
        if (avatarUrl == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请上传图片");
        }
        //判断文件类型或者文件大小
        //获取原始文件名
        String originalFilename = avatarUrl.getOriginalFilename();
        //对原始文件名进行判断
        assert originalFilename != null;
        if(!originalFilename.endsWith(".png") && !originalFilename.endsWith(".jpg")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片格式不符");
        }
        //如果判断通过上传文件到OSS
        String filePath = PathUtils.generateFilePath(originalFilename);
        String img = uploadOss(avatarUrl, filePath);

        //修改头像
        User loginUser = userService.getLoginUser(request);
        loginUser.setAvatar(img);
        User user = new User();
        BeanUtils.copyProperties(loginUser,user);
        boolean b = userService.updateById(user);
        if (!b){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"修改头像失败");
        }
        return img;
    }

    /**
     * 上传文章图片
     * @param avatar 文件名
     * @param request 当前用户信息
     * @return 图片路径
     */
    @Override
    public String uploadArticleImg(MultipartFile avatar, HttpServletRequest request) {
        if (avatar == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请上传图片");
        }
        //判断文件类型或者文件大小
        //获取原始文件名
        String originalFilename = avatar.getOriginalFilename();
        //对原始文件名进行判断
        assert originalFilename != null;
        if(!originalFilename.endsWith(".png") && !originalFilename.endsWith(".jpg")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片格式不符");
        }
        //如果判断通过上传文件到OSS
        String filePath = PathUtils.generateFilePath(originalFilename);
        return uploadOss(avatar, filePath);
    }

    private String uploadOss(MultipartFile imgFile,String filePath){
        //构造一个带指定 Region 对象的配置类       地区：华东
        Configuration cfg = new Configuration(	Region.huadongZheJiang2());
        // 指定分片上传版本
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = filePath;
        try {
            InputStream inputStream = imgFile.getInputStream();
            Auth auth = Auth.create(ossConfig.getAccessKey(), ossConfig.getSecretKey());
            String upToken = auth.uploadToken(ossConfig.getBucket());
            try {
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//                System.out.println(putRet.key);
//                System.out.println(putRet.hash);
                return ossConfig.getCdnTest() + key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return null;
    }

}
