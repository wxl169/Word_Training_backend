package org.wxl.wordTraining.mapper;

import org.wxl.wordTraining.model.dto.article.ArticleListRequest;
import org.wxl.wordTraining.model.entity.TbArticle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
}
