package org.wxl.wordTraining.service;

import org.wxl.wordTraining.common.IdRequest;
import org.wxl.wordTraining.model.dto.article.ArticleAddRequest;
import org.wxl.wordTraining.model.dto.article.ArticleListRequest;
import org.wxl.wordTraining.model.dto.article.ArticleUpdateReviewOpinionsRequest;
import org.wxl.wordTraining.model.entity.TbArticle;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.vo.PageVO;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 */
public interface IArticleService extends IService<TbArticle> {

    /**
     * 上传文章
     * @param articleAddRequest 文章信息
     * @param request 当前登录用户
     * @return 上传是否成功
     */
    boolean addArticle(ArticleAddRequest articleAddRequest, HttpServletRequest request);

    /**
     * 根据查询条件查询文章列表数据
     * @param articleListRequest 查询条件
     * @return 返回文章列表数据
     */
    PageVO selectArticleList(ArticleListRequest articleListRequest);

    /**
     * 修改文章的状态
     * @param idRequest 当前文章id
     * @return 是否修改成功
     */
    boolean updateArticleStatus(IdRequest idRequest);
    /**
     * 上传文章审核意见
     * @param articleUpdateReviewOpinionsRequest 审核意见
     * @return 是否修改成功
     */
    boolean updateArticleReviewOpinions(ArticleUpdateReviewOpinionsRequest articleUpdateReviewOpinionsRequest);
}
