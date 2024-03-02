package org.wxl.wordTraining.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.article.ArticleAddRequest;
import org.wxl.wordTraining.service.IArticleService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  文章前端控制器
 * </p>
 *
 * @author wxl
 */
@RestController
@RequestMapping("/article")
public class ArticleController {
    private final IArticleService articleService;
    @Autowired
    public ArticleController(IArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 用户上传文章
     * @param articleAddRequest 发布的文章信息
     * @param request 获取当前登录用户
     * @return 是否发布成功
     */
    @PostMapping("/add")
    public BaseResponse addArticle(@RequestBody ArticleAddRequest articleAddRequest, HttpServletRequest request){
        if (articleAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isAnyBlank(articleAddRequest.getTitle(),articleAddRequest.getContent(),articleAddRequest.getDescription())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (articleAddRequest.getTags() == null || articleAddRequest.getTags().isEmpty() || articleAddRequest.getPermissions() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean add = articleService.addArticle(articleAddRequest,request);
        if (add){
            return ResultUtils.success(true);
        }else{
            return ResultUtils.error(ErrorCode.OPERATION_ERROR);
        }

    }


}
