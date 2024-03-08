package org.wxl.wordTraining.mapper;

import org.apache.ibatis.annotations.Param;
import org.wxl.wordTraining.model.entity.Comments;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wxl.wordTraining.model.vo.comment.CommentListVO;

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
}
