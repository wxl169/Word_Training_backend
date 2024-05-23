package org.wxl.wordTraining.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wxl.wordTraining.annotation.JwtToken;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.IdRequest;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.constant.ArticleConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.comment.CommentAddRequest;
import org.wxl.wordTraining.model.dto.comment.CommentDeleteRequest;
import org.wxl.wordTraining.model.dto.comment.CommentUserWriteRequest;
import org.wxl.wordTraining.model.entity.Comments;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.comment.CommentListVO;
import org.wxl.wordTraining.model.vo.comment.CommentUserHomeVO;
import org.wxl.wordTraining.model.vo.user.LoginUserVO;
import org.wxl.wordTraining.service.ICommentsService;
import org.wxl.wordTraining.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 评论前端控制器
 * </p>
 *
 * @author wxl
 */
@RestController
@RequestMapping("/comments")
public class CommentsController {
    private final ICommentsService commentsService;
    private final UserService userService;

    @Autowired
    public CommentsController(ICommentsService commentsService, UserService userService) {
        this.commentsService = commentsService;
        this.userService = userService;
    }

    /**
     * 添加评论
     *
     * @param commentAddRequest 评论信息
     * @param request           当前登录用户
     * @return 是否成功
     */
    @PostMapping("/add")
    public BaseResponse addComments(@RequestBody CommentAddRequest commentAddRequest, HttpServletRequest request) {
        if (commentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isEmpty(commentAddRequest.getContent()) || commentAddRequest.getContent().equals("<p><br></p>")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入评论内容");
        }
        if (commentAddRequest.getArticleId() == null || commentAddRequest.getArticleId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择评论的文章");
        }
        User loginUser = userService.getLoginUser(request);
        boolean add = commentsService.addComment(commentAddRequest, loginUser);
        if (add) {
            return ResultUtils.success(true);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR, "关注好友失败");
    }

    /**
     * 获取评论列表
     *
     * @param idRequest 文章id
     * @return 评论列表信息
     */
    @PostMapping("/get/id")
    @JwtToken
    public BaseResponse<List<CommentListVO>> getCommentListAll(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择文章");
        }
        List<CommentListVO> commentListVOS = commentsService.getCommentListAll(idRequest.getId(), request);
        return ResultUtils.success(commentListVOS);
    }


    /**
     * 删除评论及其子评论
     *
     * @param commentDeleteRequest 评论id
     * @param request              获取当前用户信息
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse deleteComment(@RequestBody CommentDeleteRequest commentDeleteRequest, HttpServletRequest request) {
        if (commentDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选中删除哪条评论");
        }
        if (commentDeleteRequest.getCommentId() == null || commentDeleteRequest.getCommentId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选中删除哪条评论");
        }
        if (commentDeleteRequest.getUserId() == null || commentDeleteRequest.getUserId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选中删除哪条评论");
        }
        User loginUser = userService.getLoginUser(request);
        boolean delete = commentsService.deleteComment(commentDeleteRequest, loginUser);
        if (delete) {
            return ResultUtils.success(true);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR, "删除评论失败");
    }


    /**
     * 获取用户发布的评论信息和回复用户评论的
     *
     * @param request 获取当前登录用户信息
     * @return 返回评论首页信息
     */
    @PostMapping("/get/user")
    public BaseResponse<CommentUserHomeVO> getCommentUserHome(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        CommentUserHomeVO commentUserHomeVO = commentsService.getCommentUserHome(loginUser);
        return ResultUtils.success(commentUserHomeVO);
    }

    /**
     * 根据条件获取有关用户的评论信息列表
     *
     * @param commentUserWriteRequest 筛选条件
     * @param request                 当前登录用户
     * @return 关于用户的评论信息列表
     */
    @PostMapping("/get/page")
    public BaseResponse<PageVO> getCommentUserVoList(@RequestBody CommentUserWriteRequest commentUserWriteRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        PageVO commentUserVoList = commentsService.getCommentUserVoList(commentUserWriteRequest, loginUser);
        return ResultUtils.success(commentUserVoList);
    }


    /**
     * 在用户个人评论区回复评论
     *
     * @param commentAddRequest 评论内容
     * @param request           获取当前登录用户
     * @return 是否评论成功
     */
    @PostMapping("/add/reply")
    public BaseResponse addCommentReply(@RequestBody CommentAddRequest commentAddRequest, HttpServletRequest request) {
        if (StringUtils.isBlank(commentAddRequest.getContent()) || commentAddRequest.getContent().equals(ArticleConstant.CONTENT)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请输入评论内容");
        }
        if (commentAddRequest.getParentId() == null || commentAddRequest.getParentId() <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请选择要回复的评论");
        }
        if (commentAddRequest.getArticleId() == null || commentAddRequest.getArticleId() <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请选择要评论的文章");
        }
        User loginUser = userService.getLoginUser(request);
        boolean add = commentsService.addCommentReply(commentAddRequest, loginUser);
        if (!add) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "新增评论失败");
        }
        return ResultUtils.success(true);
    }


    /**
     * 获取评论的回复列表
     * @param idRequest 评论id
     * @param request 获取当前登录用户
     * @return 评论的回复列表
     */
    @PostMapping("/get")
    public BaseResponse<Comments> getCommentById(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请选择评论");
        }
        Comments comments = commentsService.getById(idRequest.getId());
        return ResultUtils.success(comments);
    }
}

