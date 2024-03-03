package org.wxl.wordTraining.controller;


import nonapi.io.github.classgraph.json.Id;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wxl.wordTraining.annotation.AuthCheck;
import org.wxl.wordTraining.annotation.JwtToken;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.IdRequest;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.constant.UserConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.article.ArticleAddRequest;
import org.wxl.wordTraining.model.dto.article.ArticleListRequest;
import org.wxl.wordTraining.model.dto.article.ArticleUpdateReviewOpinionsRequest;
import org.wxl.wordTraining.model.entity.TbArticle;
import org.wxl.wordTraining.model.vo.PageVO;
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


    /**
     * 根据查询条件查询文章列表数据
     * @param articleListRequest 查询条件
     * @return 返回文章列表数据
     */
    @PostMapping("/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PageVO> selectArticleList(@RequestBody ArticleListRequest articleListRequest){
        if (articleListRequest.getCurrent() == null || articleListRequest.getCurrent() <= 0){
            articleListRequest.setCurrent(1);
        }
        if (articleListRequest.getPageSize() == null || articleListRequest.getPageSize() <= 0){
            articleListRequest.setPageSize(5);
        }
        PageVO pageVO = articleService.selectArticleList(articleListRequest);
        return ResultUtils.success(pageVO);
    }

    /**
     * 修改文章的状态
     * @param idRequest 当前文章id
     * @return 是否修改成功
     */
    @PostMapping("/update/status")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse updateArticleStatus(@RequestBody IdRequest idRequest){
        if (idRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //修改状态
        boolean update = articleService.updateArticleStatus(idRequest);
        if (update){
            return ResultUtils.success(true);
        }else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR);
        }
    }

    /**
     * 上传文章审核意见
     * @param articleUpdateReviewOpinionsRequest 审核意见
     * @return 是否修改成功
     */
    @PostMapping("/update/reviewOpinions")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse updateArticleReviewOpinions(@RequestBody ArticleUpdateReviewOpinionsRequest articleUpdateReviewOpinionsRequest){
        if (articleUpdateReviewOpinionsRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (articleUpdateReviewOpinionsRequest.getId() == null || articleUpdateReviewOpinionsRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //上传审核意见
        boolean update = articleService.updateArticleReviewOpinions(articleUpdateReviewOpinionsRequest);
        if (update){
            return ResultUtils.success(true);
        }else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR);
        }
    }

    /**
     * 根据id获取文章详细信息
     * @param idRequest 文章id
     * @return 文章详细信息
     */
    @PostMapping("/id")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<TbArticle> selectArticleById(@RequestBody IdRequest idRequest){
        if (idRequest == null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        TbArticle article = articleService.getById(idRequest.getId());
        return ResultUtils.success(article);

    }


}
