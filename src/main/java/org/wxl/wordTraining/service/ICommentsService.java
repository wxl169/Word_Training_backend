package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.comment.CommentAddRequest;
import org.wxl.wordTraining.model.dto.comment.CommentDeleteRequest;
import org.wxl.wordTraining.model.dto.comment.CommentUserWriteRequest;
import org.wxl.wordTraining.model.entity.Comments;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.comment.CommentListVO;
import org.wxl.wordTraining.model.vo.comment.CommentUserHomeVO;
import org.wxl.wordTraining.model.vo.comment.CommentUserVO;

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

    /**
     * 获取用户发布的评论信息和回复用户评论的
     * @param loginUser 获取当前登录用户信息
     * @return 返回评论首页信息
     */
    CommentUserHomeVO getCommentUserHome(User loginUser);

    /**
     * 根据条件获取有关用户的评论信息列表
     * @param commentUserWriteRequest 筛选条件
     * @param loginUser 当前登录用户
     * @return 关于用户的评论信息列表
     */
     PageVO getCommentUserVoList(CommentUserWriteRequest commentUserWriteRequest, User loginUser);

    /**
     * 在用户个人评论区回复评论
     * @param commentAddRequest 评论内容
     * @param loginUser 获取当前登录用户
     * @return 是否评论成功
     */
    boolean addCommentReply(CommentAddRequest commentAddRequest, User loginUser);
}
