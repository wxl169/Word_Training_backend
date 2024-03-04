package org.wxl.wordTraining.mapper;

import org.apache.ibatis.annotations.Param;
import org.wxl.wordTraining.model.dto.article.ArticleAllRequest;
import org.wxl.wordTraining.model.dto.article.ArticleListRequest;
import org.wxl.wordTraining.model.entity.TbArticle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wxl.wordTraining.model.vo.article.ArticleAllVO;
import org.wxl.wordTraining.model.vo.article.ArticleVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxl
 */
public interface ArticleMapper extends BaseMapper<TbArticle> {

    /**
     * 根据查询条件查询文章列表数据
     * @param articleListRequest 查询条件
     * @return 返回文章列表数据
     */
    List<ArticleVO> selectArticleList(ArticleListRequest articleListRequest);
    /**
     * 用户查询文章列表信息
     * @param articleAllRequest 查询条件
     * @return 文章列表信息
     */
    List<ArticleAllVO> selectArticleAll(ArticleAllRequest articleAllRequest);

    /**
     * 登录用户查询文章列表信息
     * @param articleAllRequest 查询条件
     * @param concernList 好友列表
     * @return 文章列表
     */
    List<ArticleAllVO> selectArticleLogin(@Param("articleAllRequest") ArticleAllRequest articleAllRequest,@Param("concernList") List<Long> concernList);
    /**
     * 修改文章的收藏量
     * @param articleId 文章id
     * @return 是否修改成功
     */
    boolean addArticleCollectionNumber(Long articleId);

    /**
     * 减少文章的收藏量
     * @param articleId 文章id
     * @return 是否修改成功
     */
    boolean deleteArticleCollectionNumber(Long articleId);

    /**
     * 修改文章点赞量
     * @param articleId 文章id
     * @return 是否修改成功
     */
    boolean addArticlePraiseNumber(Long articleId);

    /**
     * 减少文章的点赞量
     * @param articleId 文章id
     * @return 是否修改成功
     */
    boolean deleteArticlePraiseNumber(Long articleId);
}
