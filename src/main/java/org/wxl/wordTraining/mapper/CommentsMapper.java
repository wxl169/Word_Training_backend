package org.wxl.wordTraining.mapper;

import org.apache.ibatis.annotations.Param;
import org.wxl.wordTraining.model.entity.Comments;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wxl.wordTraining.model.vo.comment.CommentListVO;
import org.wxl.wordTraining.model.vo.comment.CommentUserVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxl
 */
public interface CommentsMapper extends BaseMapper<Comments> {

    /**
     * 获取评论列表信息
     * @param articleId 文章id
     * @return 评论列表信息
     */
    List<CommentListVO> getCommentListAll(Long articleId);

    /**
     * 获取子评论信息
     * @param parentId 父评论id
     * @param topId 子评论id
     * @return 子评论列表信息
     */
    List<CommentListVO> getCommentChildList(@Param("parentId") Long parentId,@Param("topId") Long topId);

    /**
     * 修改评论点赞数
     * @param commentId 评论id
     * @return 是否修改成功
     */
    boolean addCommentPraiseNumber(Long commentId);

    /**
     * 减少评论点赞数
     * @param commentId 评论id
     * @return 是否修改成功
     */
    boolean deleteCommentPraiseNumber(Long commentId);

    /**
     * 删除评论及其子评论
     * @param commentId 评论id
     * @return 是否删除成功
     */
    boolean deleteComment(Long commentId);

    /**
     * 获取最早发布或回复的评论时间
     * @param userId 用户id
     * @return 时间
     */
    LocalDateTime getCommentEarly(Long userId);

    /**
     * 删除该文章下的所有评论
     * @param articleId 文章id
     * @return 是否删除成功
     */
    boolean deleteCommentByArticleId(Long articleId);

    /**
     * 修改评论是否显示
     * @param articleId  文章id
     * @param isShow  是否显示（0：显示，1：不显示）
     * @return 是否修改成功
     */
    boolean updateCommentShow(@Param("articleId") Long articleId,@Param("isShow") Integer isShow);
}
