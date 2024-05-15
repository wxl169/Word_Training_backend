package org.wxl.wordTraining.service;

import org.wxl.wordTraining.common.IdRequest;
import org.wxl.wordTraining.model.dto.article.*;
import org.wxl.wordTraining.model.entity.TbArticle;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.article.ArticleByUserIdVO;
import org.wxl.wordTraining.model.vo.article.ArticleNumberVO;
import org.wxl.wordTraining.model.vo.article.ArticleOneVO;
import org.wxl.wordTraining.model.vo.article.ArticleOtherVO;

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
     * 修改文章
     * @param articleUpdateRequest 文章信息
     * @param request 当前登录用户
     * @return 修改是否成功
     */
    boolean updateArticle(ArticleUpdateRequest articleUpdateRequest, HttpServletRequest request);

    /**
     * 根据查询条件查询文章列表数据
     * @param articleListRequest 查询条件
     * @return 返回文章列表数据
     */
    PageVO selectArticleList(ArticleListRequest articleListRequest);

    /**
     * 文章审核通过
     * @param idRequest 当前文章id
     * @return 是否修改成功
     */
    boolean updateArticleStatusPass(IdRequest idRequest);

    /**
     * 禁用/解禁文章的状态
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
    /**
     * 用户查询文章列表信息
     * @param articleAllRequest 查询条件
     * @return 文章列表信息
     */
    PageVO selectArticleListAll(ArticleAllRequest articleAllRequest,HttpServletRequest request);

    /**
     * 根据文章id获取文章详细信息
     * @param articleId 文章id
     * @param request 判断是否登录
     * @return 文章详细信息
     */
    ArticleOneVO selectArticleOne(Long articleId,HttpServletRequest request);

    /**
     * 我的发布 获取发布文章的数量/标签/时间信息
     * @param request 获取当前登录时间
     * @return 获取文章数量/标签/时间信息
     */
    ArticleNumberVO getArticleNumber(HttpServletRequest request);

    /**
     * 根据用户选择的条件查询用户发布的文章
     * @param articleUserWriteDTO 用户筛选条件
     * @param request 获取当前登录用户
     * @return 用户发布的文章
     */
    PageVO getArticleByUserWrite(ArticleUserWriteDTO articleUserWriteDTO, HttpServletRequest request);

    /**
     * 根据文章id删除文章信息
     * @param articleId 文章id
     * @param httpServletRequest 当前登录用户
     * @return 是否删除成功
     */
    boolean deleteArticle(Long articleId, HttpServletRequest httpServletRequest);

    /**
     * 根据文章id获取文章修改信息
     * @param articleId 文章id
     * @return 返回的文章信息
     */
    ArticleByUserIdVO getArticleUpdateById(Long articleId);

    /**
     * 获取其他相关文章:推荐文章，该文章作者其他文章
     * @param articleId 文章id
     * @param request 获取登录用户
     * @return 推荐文章，该文章作者其他文章列表
     */
    ArticleOtherVO getArticleOther(Long articleId,HttpServletRequest request);
}
