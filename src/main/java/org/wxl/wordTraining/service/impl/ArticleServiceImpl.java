package org.wxl.wordTraining.service.impl;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.wxl.wordTraining.model.dto.article.ArticleAddRequest;
import org.wxl.wordTraining.model.entity.TbArticle;
import org.wxl.wordTraining.mapper.ArticleMapper;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, TbArticle> implements IArticleService {
private final UserService userService;
private final Gson gson;
@Autowired
    public ArticleServiceImpl(UserService userService,Gson gson) {
        this.gson = gson;
        this.userService = userService;
    }
    /**
     * 上传文章信息
     * @param articleAddRequest 文章信息
     * @param request 当前登录用户
     * @return 上传是否成功
     */
    @Override
    public boolean addArticle(ArticleAddRequest articleAddRequest, HttpServletRequest request) {
        //获取登录用户信息
        User loginUser = userService.getLoginUser(request);
        TbArticle article = new TbArticle();
        article.setTitle(articleAddRequest.getTitle());
        article.setContent(articleAddRequest.getContent());
        article.setDescription(articleAddRequest.getDescription());
        article.setUserId(loginUser.getId());
        String tags = gson.toJson(articleAddRequest.getTags());
        article.setTags(tags);
        if (StringUtils.isNotBlank(articleAddRequest.getCoverImage())){
            article.setCoverImage(articleAddRequest.getCoverImage());
        }
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setVisitNumber(0L);
        article.setCollectionNumber(0L);
        article.setCommentNumber(0L);
        article.setStatus(0);
        article.setPraiseNumber(0L);
        article.setPermissions(articleAddRequest.getPermissions());
        return this.save(article);
    }
}
