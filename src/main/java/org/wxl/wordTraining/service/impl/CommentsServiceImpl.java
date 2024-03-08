package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.ArticleMapper;
import org.wxl.wordTraining.model.dto.comment.CommentAddRequest;
import org.wxl.wordTraining.model.entity.Comments;
import org.wxl.wordTraining.mapper.CommentsMapper;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.comment.CommentListVO;
import org.wxl.wordTraining.service.ICommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.utils.BeanCopyUtils;

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
@Autowired
public CommentsServiceImpl(ArticleMapper articleMapper,CommentsMapper commentsMapper){
    this.articleMapper = articleMapper;
    this.commentsMapper = commentsMapper;
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
        comments.setPraiseNumber(0L);
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
    public List<CommentListVO> getCommentListAll(Long articleId) {
        //获取顶层评论信息
        List<CommentListVO> commentListVOList = commentsMapper.getCommentListAll(articleId);
        commentListVOList = commentListVOList.stream().peek(commentListVO -> {
            //通过评论id获取子评论信息
            List<CommentListVO> commentChildVOS = getCommentChildList(commentListVO.getId(),commentListVO.getId());
            if (commentChildVOS != null && !commentChildVOS.isEmpty()){
                commentListVO.setCommentChildList(commentChildVOS);
            }
        }).collect(Collectors.toList());
        return commentListVOList;
    }


    /**
     * 获取子评论
     * @param parentId 父评论id
     * @param  topId 顶层评论id
     * @return 子评论列表
     */
    private List<CommentListVO> getCommentChildList(Long parentId,Long topId){
        if (parentId == null || parentId <= 0){
            return null;
        }
        if (topId == null || topId <= 0){
            return  null;
        }
        List<CommentListVO> commentChildVOS = commentsMapper.getCommentChildList(parentId,topId);
        commentChildVOS = commentChildVOS.stream().peek(commentChildVO -> {
            //通过评论id获取子评论信息
            List<CommentListVO> commentChildVOList = getCommentChildList(commentChildVO.getId(),commentChildVO.getTopId());
            if (commentChildVOList != null && !commentChildVOList.isEmpty()){
                commentChildVO.setCommentChildList(commentChildVOList);
            }
        }).collect(Collectors.toList());
        return commentChildVOS;
    }
}
