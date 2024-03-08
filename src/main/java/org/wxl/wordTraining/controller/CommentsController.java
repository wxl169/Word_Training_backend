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
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.comment.CommentAddRequest;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.comment.CommentListVO;
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
    public CommentsController(ICommentsService commentsService,UserService userService){
        this.commentsService = commentsService;
        this.userService = userService;
    }

    /**
     * 添加评论
     * @param commentAddRequest 评论信息
     * @param request 当前登录用户
     * @return 是否成功
     */
    @PostMapping("/add")
    public BaseResponse addComments(@RequestBody CommentAddRequest commentAddRequest, HttpServletRequest request){
        if (commentAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isEmpty(commentAddRequest.getContent()) || commentAddRequest.getContent().equals("<p><br></p>")){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入评论内容");
        }
        if (commentAddRequest.getArticleId() == null || commentAddRequest.getArticleId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择评论的文章");
        }
        User loginUser = userService.getLoginUser(request);
        boolean add = commentsService.addComment(commentAddRequest,loginUser);
        if (add){
            return ResultUtils.success(true);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR,"关注好友失败");
    }

    /**
     * 获取评论列表
     * @param idRequest 文章id
     * @return 评论列表信息
     */
    @PostMapping("/get/id")
    @JwtToken
    public BaseResponse<List<CommentListVO>> getCommentListAll(@RequestBody IdRequest idRequest){
        if (idRequest == null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择文章");
        }
        List<CommentListVO> commentListVOS = commentsService.getCommentListAll(idRequest.getId());
        return ResultUtils.success(commentListVOS);
    }

}
