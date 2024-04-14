package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.wxl.wordTraining.constant.UserConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.CommentsMapper;
import org.wxl.wordTraining.model.dto.article.*;
import org.wxl.wordTraining.model.entity.TbArticle;
import org.wxl.wordTraining.mapper.ArticleMapper;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.enums.ArticleWriteStatusEnum;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.article.*;
import org.wxl.wordTraining.model.vo.comment.CommentListVO;
import org.wxl.wordTraining.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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
    private final ITagService tagService;
    private final CommentsMapper commentsMapper;

@Autowired
    public ArticleServiceImpl(UserService userService,Gson gson,ArticleMapper articleMapper,ICollectionService collectionService,IPraiseService praiseService,ITagService tagService,CommentsMapper commentsMapper) {
        this.gson = gson;
        this.userService = userService;
        this.articleMapper = articleMapper;
        this.collectionService = collectionService;
        this.praiseService = praiseService;
        this.tagService = tagService;
        this.commentsMapper = commentsMapper;
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
     * 修改文章
     * @param articleUpdateRequest 文章信息
     * @param request 当前登录用户
     * @return 修改是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticle(ArticleUpdateRequest articleUpdateRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        LambdaUpdateWrapper<TbArticle> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TbArticle::getId,articleUpdateRequest.getArticleId())
                .eq(TbArticle::getIsDelete,0)
                .eq(TbArticle::getUserId,loginUser.getId())
                .set(TbArticle::getTitle,articleUpdateRequest.getTitle())
                .set(TbArticle::getContent,articleUpdateRequest.getContent())
                .set(TbArticle::getDescription,articleUpdateRequest.getDescription())
                .set(TbArticle::getPermissions,articleUpdateRequest.getPermissions())
                .set(TbArticle::getStatus,articleUpdateRequest.getStatus());
        if (StringUtils.isNotBlank(articleUpdateRequest.getCoverImage())){
            updateWrapper.set(TbArticle::getCoverImage,articleUpdateRequest.getCoverImage());
        }

        if (!articleUpdateRequest.getTags().isEmpty()){
            String tag = gson.toJson(articleUpdateRequest.getTags());
            updateWrapper.set(TbArticle::getTags,tag);
        }

        //如果该文章存在评论则 隐藏评论
        boolean update = commentsMapper.updateCommentShow(articleUpdateRequest.getArticleId(),1);
        if (!update){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"修改文章状态失败");
        }
        return this.update(updateWrapper);
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
                if (articleListVO.getTags()==null || articleListVO.getTags().isEmpty()){
                    count.getAndDecrement();
                    return false;
                }
                if (new HashSet<>(articleListVO.getTags()).containsAll(articleListRequest.getTags())){
                    return true;
                }
                count.getAndDecrement();
                return false;
            }).collect(Collectors.toList());

            filteredArticleListVOList = filteredArticleListVOList.stream()
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
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticleStatusPass(IdRequest idRequest) {

        //查看该文章是否有评论
        List<CommentListVO> commentListAll = commentsMapper.getCommentListAll(idRequest.getId());
        if (commentListAll != null && !commentListAll.isEmpty()){
            //如果该文章存在评论则 展示评论
            boolean update = commentsMapper.updateCommentShow(idRequest.getId(),0);
            if (!update){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"修改文章状态失败");
            }
        }
        return articleMapper.updateArticleStatusPass(idRequest.getId());
    }

    /**
     * 禁用/解禁文章的状态
     * @param idRequest 当前文章状态
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
        //如果该文章存在评论则 隐藏评论
        boolean update = commentsMapper.updateCommentShow(idRequest.getId(),1);
        if (!update){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"修改文章状态失败");
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
            //如果该文章存在评论则 隐藏评论
            boolean update = commentsMapper.updateCommentShow(articleUpdateReviewOpinionsRequest.getId(),1);
            if (!update){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"修改文章状态失败");
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
                if (articleListVO.getTags() == null){
                    count.getAndDecrement();
                    return false;
                }
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
        ArticleOneVO articleOneMapperVO = articleMapper.selectArticleOne(articleId);
        ArticleOneVO articleOneVO = BeanCopyUtils.copyBean(articleOneMapperVO, ArticleOneVO.class);
        List<String> tagList = gson.fromJson(articleOneMapperVO.getTags(), new TypeToken<List<String>>() {
        }.getType());
        articleOneVO.setTagList(tagList);

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


    /**
     * 我的发布 获取发布文章的数量/标签/时间信息
     * @param request 获取当前登录时间
     * @return 获取文章数量/标签/时间信息
     */
    @Override
    public ArticleNumberVO getArticleNumber(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        //1.先获取当前登录用户发布的所有文章数据
        LambdaQueryWrapper<TbArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbArticle::getUserId,loginUser.getId());
        List<TbArticle> articleList = this.list(queryWrapper);
        ArticleNumberVO articleNumberVO = new ArticleNumberVO();
        //判断当前用户是否发表文章
        articleNumberVO.setAllNumber(0);
        articleNumberVO.setOpenNumber(0);
        articleNumberVO.setPrivateNumber(0);
        articleNumberVO.setConcernNumber(0);
        articleNumberVO.setProcessNumber(0);
        articleNumberVO.setDraftNumber(0);
        articleNumberVO.setRectificationNumber(0);
        articleNumberVO.setBanNumber(0);
        if (!articleList.isEmpty()){
            articleNumberVO.setAllNumber(articleList.size());
            //查看数量
            articleList.forEach(article->{
                //全部可见
                if (article.getPermissions().equals(ArticleConstant.OPEN) && article.getStatus().equals(ArticleConstant.NORMAL_RELEASE)){
                    articleNumberVO.setOpenNumber(articleNumberVO.getOpenNumber() + 1);
                }
                //仅自己可见
                if (article.getPermissions().equals(ArticleConstant.PRIVATE) && article.getStatus().equals(ArticleConstant.NORMAL_RELEASE)){
                    articleNumberVO.setPrivateNumber(articleNumberVO.getPrivateNumber() + 1);
                }
                //仅关注自己好友的可见
                if (article.getPermissions().equals(ArticleConstant.REGARD_BY_MYSELF) && article.getStatus().equals(ArticleConstant.NORMAL_RELEASE)){
                    articleNumberVO.setConcernNumber(articleNumberVO.getConcernNumber() + 1);
                }
                //审核中的
                if (article.getStatus().equals(ArticleConstant.PROCESS)){
                    articleNumberVO.setProcessNumber(articleNumberVO.getProcessNumber() + 1);
                }
                //草稿
                if (article.getStatus().equals(ArticleConstant.DRAFT)){
                    articleNumberVO.setDraftNumber(articleNumberVO.getDraftNumber() + 1);
                }
                //需整改
                if (article.getStatus().equals(ArticleConstant.RECTIFICATION)){
                    articleNumberVO.setRectificationNumber(articleNumberVO.getRectificationNumber() + 1);
                }
                //已封禁
                if (article.getStatus().equals(ArticleConstant.BAN)){
                    articleNumberVO.setBanNumber(articleNumberVO.getBanNumber() + 1);
                }
            });
        }
        //获取最早发布的文章数据
        Set<Integer> timeSet = new HashSet<>();
        int nowYear = LocalDateTime.now().getYear();
        Optional<TbArticle> minCreateTimeArticle  = articleList.stream().min(Comparator.comparing(TbArticle::getCreateTime));
        if (minCreateTimeArticle.isPresent()) {
            TbArticle article = minCreateTimeArticle.get();
            // 处理具有最小 createTime 的 article 对象
            LocalDateTime createTime = article.getCreateTime();
            int oldYear = createTime.getYear();
            //当前时间
            int year = nowYear - oldYear;
            for (int i = 0; i <= year; i++){
                timeSet.add(oldYear++);
            }
        } else {
            // articleList 为空
            timeSet.add(nowYear);
        }
        articleNumberVO.setTimeSet(timeSet);
        //获取标签数据
        Set<String> tagSet = tagService.getTagAll();
        articleNumberVO.setTagSet(tagSet);

        //获取首页列表信息
        articleNumberVO.setPageVO(getArticlePageByUserId(loginUser.getId()));
        //获取首页文章数据
        return articleNumberVO;
    }


    /**
     * 根据用户选择的条件查询用户发布的文章
     * @param articleUserWriteDTO 用户筛选条件
     * @param request 获取当前登录用户
     * @return 用户发布的文章
     */
    @Override
    public PageVO getArticleByUserWrite(ArticleUserWriteDTO articleUserWriteDTO, HttpServletRequest request) {
        //获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);
        LambdaQueryWrapper<TbArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbArticle::getUserId,loginUser.getId())
                .eq(TbArticle::getIsDelete,0);
        //判断是否选择年份
        if (articleUserWriteDTO.getYear() != null && articleUserWriteDTO.getYear() != 0){
            queryWrapper.apply("YEAR(create_time) = {0}", articleUserWriteDTO.getYear());
            //判断是否选择月份
            if (articleUserWriteDTO.getMonth() != null && articleUserWriteDTO.getMonth() != 0){
                queryWrapper.apply("MONTH(create_time) = {0}", articleUserWriteDTO.getMonth());
            }
        }
        //如果输入了关键字，则根据文章的标题和描述搜索文章
        if (StringUtils.isNotBlank(articleUserWriteDTO.getContent())){
            String content = articleUserWriteDTO.getContent();
            queryWrapper.like(TbArticle::getTitle,content)
                    .or()
                    .like(TbArticle::getDescription,content);
        }
        //获取用户筛选的状态
        ArticleWriteStatusEnum enumByValue = ArticleWriteStatusEnum.getEnumByValue(articleUserWriteDTO.getStatus());
        assert enumByValue != null;
        String status = enumByValue.getText();
        if (status.equals(ArticleWriteStatusEnum.ALL_SHOW.getText())){
            //查看所有正常发布且公开的文章
            queryWrapper.eq(TbArticle::getStatus,ArticleConstant.NORMAL_RELEASE)
                    .eq(TbArticle::getPermissions,ArticleConstant.OPEN);
        }else if (status.equals(ArticleWriteStatusEnum.PRIVATE_SHOW.getText())){
            //查看所有正常发布且私有的的文章
            queryWrapper.eq(TbArticle::getStatus,ArticleConstant.NORMAL_RELEASE)
                    .eq(TbArticle::getPermissions,ArticleConstant.PRIVATE);
        }else if(status.equals(ArticleWriteStatusEnum.PRIVATE_CONCERN_SHOW.getText())){
            //查看所有正常发布且仅关注自己的用户可以查看的文章
            queryWrapper.eq(TbArticle::getStatus,ArticleConstant.NORMAL_RELEASE)
                    .eq(TbArticle::getPermissions,ArticleConstant.REGARD_BY_MYSELF);
        }else if (status.equals(ArticleWriteStatusEnum.DRAFT_ARTICLE.getText())){
            //查看状态为草稿中的文章
            queryWrapper.eq(TbArticle::getStatus,ArticleConstant.DRAFT);
        }else if (status.equals(ArticleWriteStatusEnum.UNDER_REVIEW.getText())){
            //查看状态为审核中的文章
            queryWrapper.eq(TbArticle::getStatus,ArticleConstant.PROCESS);
        }else if (status.equals(ArticleWriteStatusEnum.RECTIFICATION_REQUIRED.getText())){
            //查看状态为整改中的文章
            queryWrapper.eq(TbArticle::getStatus,ArticleConstant.RECTIFICATION);
        }else if (status.equals(ArticleWriteStatusEnum.BAN_ARTICLE.getText())){
            //查看状态为被封禁的文章
            queryWrapper.eq(TbArticle::getStatus,ArticleConstant.BAN);
        }
        List<ArticleByUserIdVO> articleByUserIdVOS;
        AtomicLong total = new AtomicLong();
        //如果标签不为空
        if (!articleUserWriteDTO.getTagName().isEmpty()){
            List<TbArticle> articleList = this.list(queryWrapper);
            total.set(articleList.size());
            articleList = articleList.stream().filter(article->{
                if (article.getTags() == null || article.getTags().isEmpty()){
                    total.getAndDecrement();
                    return false;
                }
                List<String> tagList = gson.fromJson(article.getTags(),new TypeToken<List<String>>(){}.getType());
                if (new HashSet<>(tagList).containsAll(articleUserWriteDTO.getTagName())) {
                    return true;
                }
                total.getAndDecrement();
                return false;
            }).collect(Collectors.toList());
            articleList = articleList.stream().skip((articleUserWriteDTO.getCurrent() - 1) * 3L)
                    .limit(3).collect(Collectors.toList());
            articleByUserIdVOS = BeanCopyUtils.copyBeanList(articleList, ArticleByUserIdVO.class);
        }else{
            Page<TbArticle> page = new Page<>(articleUserWriteDTO.getCurrent(),3);
            page(page,queryWrapper);
            articleByUserIdVOS = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleByUserIdVO.class);
            total.set(page.getTotal());
        }

        articleByUserIdVOS = articleByUserIdVOS.stream().peek(article->{
            if (StringUtils.isNotBlank(article.getTags())){
                List<String> tagList = gson.fromJson(article.getTags(), new TypeToken<List<String>>() {
                }.getType());
                article.setTagList(tagList);
            }
        }).collect(Collectors.toList());
        return new PageVO(articleByUserIdVOS,total.get());
    }

    /**
     * 根据文章id删除文章信息
     * @param articleId 文章id
     * @param httpServletRequest 当前登录用户
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticle(Long articleId, HttpServletRequest httpServletRequest) {
        TbArticle article = this.getById(articleId);
        User loginUser = userService.getLoginUser(httpServletRequest);
        if (!article.getUserId().equals(loginUser.getId()) || !loginUser.getRole().equals(UserConstant.ADMIN_ROLE)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"文章只能由管理员或本人删除");
        }

        //删除文章下的评论
        boolean delete =  commentsMapper.deleteCommentByArticleId(articleId);
        if (!delete){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"删除评论失败");
        }
        return this.removeById(articleId);
    }

    /**
     * 根据文章id获取文章修改信息
     * @param articleId 文章id
     * @return 返回的文章信息
     */
    @Override
    public ArticleByUserIdVO getArticleUpdateById(Long articleId) {
        TbArticle article = this.getById(articleId);
        if (article == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"暂无该文章");
        }
        ArticleByUserIdVO articleByUserIdVO = BeanCopyUtils.copyBean(article, ArticleByUserIdVO.class);
        if (StringUtils.isNotBlank(articleByUserIdVO.getTags())){
            List<String> tagList = gson.fromJson(articleByUserIdVO.getTags(), new TypeToken<List<String>>() {
            }.getType());
            articleByUserIdVO.setTagList(tagList);
        }
        return articleByUserIdVO;
    }


    /**
     * 根据用户id 获取文章首页列表信息
     * @param userId 用户id
     * @return 首页列表信息
     */
    private  PageVO getArticlePageByUserId(Long userId) {
        LambdaQueryWrapper<TbArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbArticle::getUserId,userId)
                .eq(TbArticle::getIsDelete,0);
        Page<TbArticle> page = new Page<>(1,ArticleConstant.MIN_CURRENT);
        page(page,queryWrapper);
        List<ArticleByUserIdVO> articleByUserIdVOS = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleByUserIdVO.class);
        articleByUserIdVOS = articleByUserIdVOS.stream().peek(article->{
            if (StringUtils.isNotBlank(article.getTags())){
                List<String> tagList = gson.fromJson(article.getTags(), new TypeToken<List<String>>() {
                }.getType());
                article.setTagList(tagList);
            }
        }).collect(Collectors.toList());
        return new PageVO(articleByUserIdVOS, page.getTotal());
    }

}
