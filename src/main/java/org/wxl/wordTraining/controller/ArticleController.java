package org.wxl.wordTraining.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.wxl.wordTraining.annotation.AuthCheck;
import org.wxl.wordTraining.annotation.JwtToken;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.IdRequest;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.constant.ArticleConstant;
import org.wxl.wordTraining.constant.UserConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.article.*;
import org.wxl.wordTraining.model.entity.TbArticle;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.article.ArticleByUserIdVO;
import org.wxl.wordTraining.model.vo.article.ArticleNumberVO;
import org.wxl.wordTraining.model.vo.article.ArticleOneVO;
import org.wxl.wordTraining.model.vo.article.ArticleOtherVO;
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入文章相关内容");
        }
        if (StringUtils.isAnyBlank(articleAddRequest.getTitle(),articleAddRequest.getContent(),articleAddRequest.getDescription())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入文章标题或描述");
        }
        if (articleAddRequest.getPermissions() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择用户浏览权限");
        }
        if (articleAddRequest.getStatus() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(ArticleConstant.CONTENT.equals(articleAddRequest.getContent()) || StringUtils.isBlank(articleAddRequest.getContent())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入文章正文");
        }
        boolean add = articleService.addArticle(articleAddRequest,request);
        if (add){
            return ResultUtils.success(true);
        }else{
            return ResultUtils.error(ErrorCode.OPERATION_ERROR);
        }
    }

    /**
     * 用户修改文章
     * @param articleUpdateRequest 发布的文章信息
     * @param request 获取当前登录用户
     * @return 是否发布成功
     */
    @PostMapping("/update")
    public BaseResponse updateArticle(@RequestBody ArticleUpdateRequest articleUpdateRequest, HttpServletRequest request){
        if (articleUpdateRequest == null ||articleUpdateRequest.getArticleId() == null || articleUpdateRequest.getArticleId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isAnyBlank(articleUpdateRequest.getTitle(),articleUpdateRequest.getContent(),articleUpdateRequest.getDescription())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (articleUpdateRequest.getPermissions() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入文章描述");
        }
        if (articleUpdateRequest.getStatus() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(ArticleConstant.CONTENT.equals(articleUpdateRequest.getContent())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入文章内容");
        }
        boolean update = articleService.updateArticle(articleUpdateRequest,request);
        if (update){
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
     * 禁用/解禁文章的状态
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
     * 文章审核通过
     * @param idRequest 当前文章id
     * @return 是否修改成功
     */
    @PostMapping("/update/status/pass")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse updateArticleStatusPass(@RequestBody IdRequest idRequest){
        if (idRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //修改状态
        boolean update = articleService.updateArticleStatusPass(idRequest);
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
        if (StringUtils.isEmpty(articleUpdateReviewOpinionsRequest.getReviewOpinions() )){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请填写审核修改意见");
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


    /**
     * 用户查询文章列表信息
     * @param articleAllRequest 查询条件
     * @return 文章列表信息
     */
    @PostMapping("/get/all")
    @JwtToken
    public BaseResponse<PageVO> selectArticleListAll (@RequestBody ArticleAllRequest articleAllRequest,HttpServletRequest request){
        if (articleAllRequest.getCurrent() == null){
            articleAllRequest.setCurrent(1);
        }
        PageVO pageVO = articleService.selectArticleListAll(articleAllRequest,request);
        return ResultUtils.success(pageVO);
    }


    /**
     * 根据文章id查询文章详细信息
     * @param idRequest 文章id
     * @param request 判断是否登录
     * @return 文章详细信息
     */
    @PostMapping("/get")
    @JwtToken
    public BaseResponse<ArticleOneVO> selectArticleOne (@RequestBody IdRequest idRequest,HttpServletRequest request){
        if (idRequest == null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ArticleOneVO articleOneVO = articleService.selectArticleOne(idRequest.getId(),request);
        return ResultUtils.success(articleOneVO);
    }


    /**
     * 我的发布 获取发布文章的数量/标签/时间信息
     * @param request 获取当前登录时间
     * @return 获取文章数量/标签/时间信息
     */
    @GetMapping("/get/num")
    public BaseResponse<ArticleNumberVO> getArticleNumber(HttpServletRequest request){
        ArticleNumberVO articleNumberVO =  articleService.getArticleNumber(request);
         return ResultUtils.success(articleNumberVO);
    }


    /**
     * 根据用户选择的条件查询用户发布的文章
     * @param articleUserWriteDTO 用户筛选条件
     * @param request 获取当前登录用户
     * @return 用户发布的文章
     */
    @PostMapping("/get/user/write")
    public  BaseResponse<PageVO> getArticleUserWrite(@RequestBody ArticleUserWriteDTO articleUserWriteDTO,HttpServletRequest request){
        if (articleUserWriteDTO.getStatus() == null || articleUserWriteDTO.getStatus() < 0){
            articleUserWriteDTO.setStatus(0);
        }
        if (articleUserWriteDTO.getCurrent() == null || articleUserWriteDTO.getCurrent() <= 0){
            articleUserWriteDTO.setCurrent(1);
        }
        PageVO pageVO =  articleService.getArticleByUserWrite(articleUserWriteDTO,request);
        return ResultUtils.success(pageVO);
    }


    /**
     * 根据文章id删除文章信息
     *
     * @param idRequest          文章id
     * @param httpServletRequest 获取当前用户信息
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse deleteArticleById(@RequestBody IdRequest idRequest, HttpServletRequest httpServletRequest){
        if (idRequest == null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择要删除的文章");
        }
        boolean delete = articleService.deleteArticle(idRequest.getId(),httpServletRequest);
        if (!delete){
            return ResultUtils.error(ErrorCode.OPERATION_ERROR,"删除文章失败");
        }
        return ResultUtils.success(true);
    }


    /**
     * 根据文章id获取文章修改信息
     * @param idRequest 文章id
     * @return 返回的文章信息
     */
    @PostMapping("/get/id")
    public BaseResponse<ArticleByUserIdVO> getArticleUpdateById(@RequestBody IdRequest idRequest){
        if (idRequest == null || idRequest.getId() == null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ArticleByUserIdVO article = articleService.getArticleUpdateById(idRequest.getId());
        return ResultUtils.success(article);
    }


    /**
     * 获取其他相关文章:推荐文章，该文章作者其他文章
     * @param idRequest 文章id
     * @param request 获取登录用户
     * @return 推荐文章，该文章作者其他文章列表
     */
    @PostMapping("/get/other")
    @JwtToken
    public BaseResponse<ArticleOtherVO> getArticleOther(@RequestBody IdRequest idRequest,HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() == null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(articleService.getArticleOther(idRequest.getId(),request));
    }

}
