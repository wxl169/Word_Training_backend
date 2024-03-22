package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.IdRequest;
import org.wxl.wordTraining.constant.ArticleConstant;
import org.wxl.wordTraining.constant.CollectionConstant;
import org.wxl.wordTraining.constant.PraiseConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.article.ArticleAddRequest;
import org.wxl.wordTraining.model.dto.article.ArticleAllRequest;
import org.wxl.wordTraining.model.dto.article.ArticleListRequest;
import org.wxl.wordTraining.model.dto.article.ArticleUpdateReviewOpinionsRequest;
import org.wxl.wordTraining.model.entity.TbArticle;
import org.wxl.wordTraining.mapper.ArticleMapper;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.article.*;
import org.wxl.wordTraining.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.service.ICollectionService;
import org.wxl.wordTraining.service.IPraiseService;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
private final ArticleMapper articleMapper;
    /**
     * 收藏
     */
    private final ICollectionService collectionService;
    /**
     * 点赞
     */
    private final IPraiseService praiseService;
@Autowired
    public ArticleServiceImpl(UserService userService,Gson gson,ArticleMapper articleMapper,ICollectionService collectionService,IPraiseService praiseService) {
        this.gson = gson;
        this.userService = userService;
        this.articleMapper = articleMapper;
        this.collectionService = collectionService;
        this.praiseService = praiseService;
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
        article.setCollectionNumber(0);
        article.setCommentNumber(0);
        article.setStatus(articleAddRequest.getStatus());
        article.setPraiseNumber(0);
        article.setPermissions(articleAddRequest.getPermissions());
        return this.save(article);
    }

    /**
     * 根据查询条件查询文章列表数据
     * @param articleListRequest 查询条件
     * @return 返回文章列表数据
     */
    @Override
    public PageVO selectArticleList(ArticleListRequest articleListRequest) {
        //根据条件获取文章列表信息
        List<ArticleVO> articleVOList;
        //计算共有多少条数据
        AtomicInteger count = new AtomicInteger(articleMapper.getArticleCount(articleListRequest));
        List<ArticleListVO> filteredArticleListVOList;
        if (articleListRequest.getTags().isEmpty()){
            //如果没有选择根据标签筛选数据
            //筛选出数据直接返回
             articleVOList = articleMapper.selectArticleList(articleListRequest);
        }else{
            //如果选择了文章标签筛选数据
            // 定义标签过滤的谓词
            articleVOList = articleMapper.selectArticleListAll(articleListRequest);
        }
        //整理标签数据
        filteredArticleListVOList = articleVOList.parallelStream().map(articleVO -> {
            ArticleListVO articleListVO = BeanCopyUtils.copyBean(articleVO, ArticleListVO.class);
            List<String> tagList = gson.fromJson(articleVO.getTags(), new TypeToken<List<String>>(){}.getType());
            articleListVO.setTags(tagList);
            return articleListVO;
        }).collect(Collectors.toList());

        if (!articleListRequest.getTags().isEmpty()){
            // 映射和过滤文章列表
            filteredArticleListVOList =  filteredArticleListVOList.stream().filter(articleListVO -> {
                        if (new HashSet<>(articleListVO.getTags()).containsAll(articleListRequest.getTags())){
                            return true;
                        }
                        count.getAndDecrement();
                        return false;
                    })
                    .skip((long) (articleListRequest.getCurrent() - 1) * articleListRequest.getPageSize())
                    .limit(articleListRequest.getPageSize())
                    .collect(Collectors.toList());
        }
        // 创建并返回PageVO对象
        return new PageVO(filteredArticleListVOList,count.longValue());
    }

    /**
     * 文章审核通过
     * @param idRequest 当前文章id
     * @return 是否修改成功
     */
    @Override
    public boolean updateArticleStatusPass(IdRequest idRequest) {
        return articleMapper.updateArticleStatusPass(idRequest.getId());
    }

    /**
     * 禁用/解禁文章的状态
     * @param idRequest 当前文章状态
     * @return 是否修改成功
     */
    @Override
    public boolean updateArticleStatus(IdRequest idRequest) {
        if (idRequest.getId() == null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        TbArticle article = this.getById(idRequest.getId());
        if (article == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"不存在该文章");
        }
        Integer status = article.getStatus();
        //如果当前文章状态处于正常/整改中/审核中 则该为已封禁
        if (status == null || status == ArticleConstant.NORMAL_RELEASE || status == ArticleConstant.RECTIFICATION || status == ArticleConstant.PROCESS){
            //TODO:通知用户该文章被禁用。
            article.setStatus(ArticleConstant.BAN);
        }else if(status == ArticleConstant.BAN){
            //TODO:通知用户该文章已解禁。
            //如果当前文章处于封禁状态
            //判断审核意见中是否有内容
            if (StringUtils.isNotBlank(article.getReviewOpinions())){
                //该为整改状态
                article.setStatus(ArticleConstant.RECTIFICATION);
            }else{
                article.setStatus(ArticleConstant.PROCESS);
            }
        }
        return this.updateById(article);
    }

    /**
     * TODO 上传文章审核意见
     * @param articleUpdateReviewOpinionsRequest 审核意见
     * @return 是否修改成功
     */
    @Override
    public boolean updateArticleReviewOpinions(ArticleUpdateReviewOpinionsRequest articleUpdateReviewOpinionsRequest) {
        TbArticle article = this.getById(articleUpdateReviewOpinionsRequest.getId());
        Integer status = article.getStatus();
        if (status != ArticleConstant.NORMAL_RELEASE && status != ArticleConstant.PROCESS && status != ArticleConstant.RECTIFICATION){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"文章已被封禁");
        }
        LambdaUpdateWrapper<TbArticle> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(TbArticle::getReviewOpinions,articleUpdateReviewOpinionsRequest.getReviewOpinions())
                .eq(TbArticle::getId,articleUpdateReviewOpinionsRequest.getId());
        //如果文章状态为正常状态
        if (status == ArticleConstant.NORMAL_RELEASE || status == ArticleConstant.PROCESS){
            if (StringUtils.isNotBlank(articleUpdateReviewOpinionsRequest.getReviewOpinions())){
                updateWrapper.set(TbArticle::getStatus,ArticleConstant.RECTIFICATION);
            }
        }

        boolean update = this.update(updateWrapper);
        if (update){
            //TODO 向用户发送信息
            return true;
        }
        return false;
    }


    /**
     * 用户查询文章列表信息
     * @param articleAllRequest 查询条件
     * @return 文章列表信息
     */
    @Override
    public PageVO selectArticleListAll(ArticleAllRequest articleAllRequest, HttpServletRequest request) {
        List<ArticleAllVO> articleAllVOList;
        List<ArticleListAllVO> articleListVOList;

        // 判断当前用户是否登录
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        if (loginUserPermitNull != null) {
            // 关注人
            String concern = loginUserPermitNull.getConcern();
            List<Long> concernList = new ArrayList<>();
            if (StringUtils.isNotBlank(concern)) {
                concernList = gson.fromJson(concern, new TypeToken<List<Long>>() {}.getType());
            }
            concernList.add(loginUserPermitNull.getId());
            articleAllVOList = articleMapper.selectArticleLogin(articleAllRequest, concernList);
        } else {
            articleAllVOList = articleMapper.selectArticleAll(articleAllRequest);
        }

        // 将标签信息转换为List集合
        articleListVOList = articleAllVOList.stream()
                .map(articleVO -> {
                    ArticleListAllVO articleListVO = BeanCopyUtils.copyBean(articleVO, ArticleListAllVO.class);
                    List<String> tagList = gson.fromJson(articleVO.getTags(), new TypeToken<List<String>>() {}.getType());
                    articleListVO.setTags(tagList);
                    return articleListVO;
                }).collect(Collectors.toList());

        //文章数量
        AtomicInteger count = new AtomicInteger(articleListVOList.size());
        if (articleAllRequest.getTagName() != null && !articleAllRequest.getTagName().isEmpty()){
            articleListVOList =  articleListVOList.stream().filter(articleListVO -> {
                if (new HashSet<>(articleListVO.getTags()).containsAll(articleAllRequest.getTagName())){
                    return true;
                }
                count.getAndDecrement();
                return false;
            }).collect(Collectors.toList());
        }
        //分页
        articleListVOList = articleListVOList.stream()
                .skip((long) (articleAllRequest.getCurrent() - 1) * ArticleConstant.MIN_CURRENT)
                    .limit(ArticleConstant.MIN_CURRENT).collect(Collectors.toList());
        // 查询每位用户的勋章信息
        articleListVOList.forEach(articleListAllVO -> {
            Long userId = articleListAllVO.getUserId();
            // 查询勋章信息
        });

        if (loginUserPermitNull != null) {
            //判断当前用户是否收藏、点赞
            articleListVOList =  articleListVOList.stream().map(articleListAllVO -> {
                //判断当前用户是否收藏
                boolean collection = collectionService.judgeCollection(articleListAllVO.getId(), loginUserPermitNull.getId(), CollectionConstant.ARTICLE_TYPE);
                if (collection){
                    articleListAllVO.setIsCollection(1);
                }
                //判断当前用户是否点赞
                boolean praise = praiseService.judgePraise(articleListAllVO.getId(),loginUserPermitNull.getId(), PraiseConstant.ARTICLE_TYPE);
                if (praise){
                    articleListAllVO.setIsPraise(1);
                }
                return articleListAllVO;
            }).collect(Collectors.toList());
        }
        return new PageVO(articleListVOList, count.longValue());
    }

    /**
     * 根据文章id获取文章详细信息
     * @param articleId 文章id
     * @return 文章详细信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleOneVO selectArticleOne(Long articleId,HttpServletRequest request) {
        ArticleOneMapperVO articleOneMapperVO = articleMapper.selectArticleOne(articleId);
        ArticleOneVO articleOneVO = BeanCopyUtils.copyBean(articleOneMapperVO, ArticleOneVO.class);
        List<String> tagList = gson.fromJson(articleOneMapperVO.getTags(), new TypeToken<List<String>>() {
        }.getType());
        articleOneVO.setTags(tagList);

        //TODO：获取发表用户的勋章信息

        //判断当前用户是否登录
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        if (loginUserPermitNull != null){
            //判断当前用户是否收藏
            boolean collection = collectionService.judgeCollection(articleId, loginUserPermitNull.getId(), CollectionConstant.ARTICLE_TYPE);
            if (collection){
                articleOneVO.setIsCollection(1);
            }
            //判断当前用户是否点赞
            boolean praise = praiseService.judgePraise(articleId,loginUserPermitNull.getId(), PraiseConstant.ARTICLE_TYPE);
            if (praise){
                articleOneVO.setIsPraise(1);
            }
            //判断当前用户是否关注
            String concern = loginUserPermitNull.getConcern();
            if (StringUtils.isNotBlank(concern)){
                Set<Long> concernSet = gson.fromJson(concern, new TypeToken<Set<Long>>() {
                }.getType());
                if (concernSet != null && !concernSet.isEmpty() && (concernSet.contains(articleOneVO.getUserId()))){
                        articleOneVO.setIsAddUser(1);
                }
            }
        }

        //文章浏览量 + 1
        boolean updateVisitNumber = articleMapper.addArticleVisitNumber(articleId);
        if (!updateVisitNumber){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"浏览量自增失败");
        }
        return articleOneVO;
    }



}
