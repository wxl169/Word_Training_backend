package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.PraiseConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.ArticleMapper;
import org.wxl.wordTraining.mapper.PraiseMapper;
import org.wxl.wordTraining.model.dto.comment.CommentAddRequest;
import org.wxl.wordTraining.model.dto.comment.CommentDeleteRequest;
import org.wxl.wordTraining.model.entity.Comments;
import org.wxl.wordTraining.mapper.CommentsMapper;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.comment.CommentListVO;
import org.wxl.wordTraining.service.ICommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.service.IPraiseService;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements ICommentsService {
private final ArticleMapper articleMapper;
private final  CommentsMapper commentsMapper;
private final IPraiseService praiseService;
private final UserService userService;
@Autowired
public CommentsServiceImpl(ArticleMapper articleMapper,CommentsMapper commentsMapper,IPraiseService praiseService,UserService userService){
    this.articleMapper = articleMapper;
    this.commentsMapper = commentsMapper;
    this.praiseService = praiseService;
    this.userService = userService;
}

    /**
     * 发表评论
     * @param commentAddRequest 评论信息
     * @param loginUser 当前登录用户
     * @return 是否发表成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addComment(CommentAddRequest commentAddRequest, User loginUser) {
        Comments comments = new Comments();
        comments.setArticleId(commentAddRequest.getArticleId());
        comments.setContent(commentAddRequest.getContent());
        comments.setUserId(loginUser.getId());
        comments.setCreateTime(LocalDateTime.now());
        if (commentAddRequest.getParentId() != null && commentAddRequest.getParentId() > 0){
            comments.setParentId(commentAddRequest.getParentId());
        }
        if (commentAddRequest.getTopId() != null && commentAddRequest.getTopId() > 0){
            comments.setTopId(commentAddRequest.getTopId());
        }
        comments.setIsTop(commentAddRequest.getIsTop());
        comments.setPraiseNumber(0);
        comments.setIsSticky(0);
        boolean save = this.save(comments);
        if (save){
            //评论数加1
            boolean add = articleMapper.addCommentNumber(commentAddRequest.getArticleId());
            if (!add){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"自增评论数失败");
            }
            return true;
        }
        return false;
    }

    /**
     * 根据文章id获取评论信息
     * @param articleId 文章id
     * @return 评论列表信息
     */
    @Override
    public List<CommentListVO> getCommentListAll(Long articleId, HttpServletRequest request) {
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        //获取顶层评论信息
        List<CommentListVO> commentListVOList = commentsMapper.getCommentListAll(articleId);
        commentListVOList = commentListVOList.stream().peek(commentListVO -> {
            //通过评论id获取子评论信息
            List<CommentListVO> commentChildVOS = getCommentChildList(commentListVO.getId(),commentListVO.getId(),loginUserPermitNull);
            if (commentChildVOS != null && !commentChildVOS.isEmpty()){
                commentListVO.setCommentChildList(commentChildVOS);
            }
            //判断是否点赞
            if(loginUserPermitNull != null){
                //判断当前用户是否点赞
                boolean praise = praiseService.judgePraise(commentListVO.getId(),loginUserPermitNull.getId(), PraiseConstant.COMMENT_TYPE);
                if (praise){
                    commentListVO.setIsPraise(1);
                }
            }
        }).collect(Collectors.toList());
        return commentListVOList;
    }

    /**
     * 删除评论及其子评论
     * @param commentDeleteRequest 评论id
     * @param loginUser 获取当前用户信息
     * @return 是否删除成功
     */
    @Override
    public boolean deleteComment(CommentDeleteRequest commentDeleteRequest, User loginUser) {
        //判断是否是当前用户发送的评论
        if (!commentDeleteRequest.getUserId().equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不是本人发送的评论");
        }
        return commentsMapper.deleteComment(commentDeleteRequest.getCommentId());
    }


    /**
     * 获取子评论
     * @param parentId 父评论id
     * @param  topId 顶层评论id
     * @return 子评论列表
     */
    private List<CommentListVO> getCommentChildList(Long parentId,Long topId,User loginUser){
        if (parentId == null || parentId <= 0){
            return null;
        }
        if (topId == null || topId <= 0){
            return  null;
        }
        List<CommentListVO> commentChildVOS = commentsMapper.getCommentChildList(parentId,topId);
        commentChildVOS = commentChildVOS.stream().peek(commentChildVO -> {
            //通过评论id获取子评论信息
            List<CommentListVO> commentChildVOList = getCommentChildList(commentChildVO.getId(),commentChildVO.getTopId(),loginUser);
            if (commentChildVOList != null && !commentChildVOList.isEmpty()){
                commentChildVO.setCommentChildList(commentChildVOList);
            }
            //判断是否点赞
            if(loginUser != null){
                //判断当前用户是否点赞
                boolean praise = praiseService.judgePraise(commentChildVO.getId(),loginUser.getId(), PraiseConstant.COMMENT_TYPE);
                if (praise){
                    commentChildVO.setIsPraise(1);
                }
            }
        }).collect(Collectors.toList());
        return commentChildVOS;
    }
}
