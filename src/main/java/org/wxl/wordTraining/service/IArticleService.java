package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.article.ArticleAddRequest;
import org.wxl.wordTraining.model.entity.TbArticle;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
