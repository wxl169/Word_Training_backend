package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.comment.CommentAddRequest;
import org.wxl.wordTraining.model.dto.comment.CommentDeleteRequest;
import org.wxl.wordTraining.model.entity.Comments;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.comment.CommentListVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 */
public interface ICommentsService extends IService<Comments> {

    /**
     * 发表评论
     * @param commentAddRequest 评论信息
     * @param loginUser 当前登录用户
     * @return 是否发表成功
     */
    boolean addComment(CommentAddRequest commentAddRequest, User loginUser);

    /**
     * 根据文章id获取评论信息
     * @param articleId 文章id
     * @return 评论列表信息
     */
    List<CommentListVO> getCommentListAll(Long articleId, HttpServletRequest request);

    /**
     * 删除评论及其子评论
     * @param commentDeleteRequest 评论id
     * @param loginUser 获取当前用户信息
     * @return 是否删除成功
     */
    boolean deleteComment(CommentDeleteRequest commentDeleteRequest, User loginUser);
}
