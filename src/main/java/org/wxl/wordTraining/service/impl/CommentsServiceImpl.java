package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.ArticleConstant;
import org.wxl.wordTraining.constant.CommonConstant;
import org.wxl.wordTraining.constant.PraiseConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.ArticleMapper;
import org.wxl.wordTraining.mapper.PraiseMapper;
import org.wxl.wordTraining.model.dto.comment.CommentAddRequest;
import org.wxl.wordTraining.model.dto.comment.CommentDeleteRequest;
import org.wxl.wordTraining.model.dto.comment.CommentUserWriteRequest;
import org.wxl.wordTraining.model.entity.Comments;
import org.wxl.wordTraining.mapper.CommentsMapper;
import org.wxl.wordTraining.model.entity.TbArticle;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.enums.ArticleWriteStatusEnum;
import org.wxl.wordTraining.model.enums.CommentWriteStatusEnum;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.article.ArticleOneVO;
import org.wxl.wordTraining.model.vo.comment.CommentListVO;
import org.wxl.wordTraining.model.vo.comment.CommentUserHomeVO;
import org.wxl.wordTraining.model.vo.comment.CommentUserVO;
import org.wxl.wordTraining.service.ICommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.service.IPraiseService;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements ICommentsService {
private final ArticleMapper articleMapper;
private final  CommentsMapper commentsMapper;
private final IPraiseService praiseService;
private final UserService userService;


@Autowired
public CommentsServiceImpl(ArticleMapper articleMapper,CommentsMapper commentsMapper,IPraiseService praiseService,UserService userService){
    this.articleMapper = articleMapper;
    this.commentsMapper = commentsMapper;
    this.praiseService = praiseService;
    this.userService = userService;
}

    /**
     * 发表评论
     * @param commentAddRequest 评论信息
     * @param loginUser 当前登录用户
     * @return 是否发表成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addComment(CommentAddRequest commentAddRequest, User loginUser) {
        Comments comments = new Comments();
        comments.setArticleId(commentAddRequest.getArticleId());
        comments.setContent(commentAddRequest.getContent());
        comments.setUserId(loginUser.getId());
        comments.setCreateTime(LocalDateTime.now());
        if (commentAddRequest.getParentId() != null && commentAddRequest.getParentId() > 0){
            comments.setParentId(commentAddRequest.getParentId());
        }
        if (commentAddRequest.getTopId() != null && commentAddRequest.getTopId() > 0){
            comments.setTopId(commentAddRequest.getTopId());
        }
        comments.setIsTop(commentAddRequest.getIsTop());
        comments.setPraiseNumber(0);
        comments.setIsSticky(0);
        comments.setIsShow(0);
        comments.setIsComplain(0);
        boolean save = this.save(comments);
        if (save){
            //评论数加1
            boolean add = articleMapper.addCommentNumber(commentAddRequest.getArticleId());
            if (!add){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"自增评论数失败");
            }
            return true;
        }
        return false;
    }

    /**
     * 根据文章id获取评论信息
     * @param articleId 文章id
     * @return 评论列表信息
     */
    @Override
    public List<CommentListVO> getCommentListAll(Long articleId, HttpServletRequest request) {
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        //获取顶层评论信息
        List<CommentListVO> commentListVOList = commentsMapper.getCommentListAll(articleId);
        commentListVOList = commentListVOList.stream().peek(commentListVO -> {
            //通过评论id获取子评论信息
            List<CommentListVO> commentChildVOS = getCommentChildList(commentListVO.getId(),commentListVO.getId(),loginUserPermitNull);
            if (commentChildVOS != null && !commentChildVOS.isEmpty()){
                commentListVO.setCommentChildList(commentChildVOS);
            }
            //判断是否点赞
            if(loginUserPermitNull != null){
                //判断当前用户是否点赞
                boolean praise = praiseService.judgePraise(commentListVO.getId(),loginUserPermitNull.getId(), PraiseConstant.COMMENT_TYPE);
                if (praise){
                    commentListVO.setIsPraise(1);
                }
            }
        }).collect(Collectors.toList());
        return commentListVOList;
    }

    /**
     * 删除评论及其子评论
     * @param commentDeleteRequest 评论id
     * @param loginUser 获取当前用户信息
     * @return 是否删除成功
     */
    @Override
    public boolean deleteComment(CommentDeleteRequest commentDeleteRequest, User loginUser) {
        //判断是否是当前用户发送的评论
        if (!commentDeleteRequest.getUserId().equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不是本人发送的评论");
        }
        return commentsMapper.deleteComment(commentDeleteRequest.getCommentId());
    }

    /**
     * 获取用户发布的评论信息和回复用户评论的
     * @param loginUser 获取当前登录用户信息
     * @return 返回评论首页信息
     */
    @Override
    public CommentUserHomeVO getCommentUserHome(User loginUser) {
        LocalDateTime createTime = commentsMapper.getCommentEarly(loginUser.getId());
        //获取当前用户收到或发布最早的评论数据
        Set<Integer> timeSet = new HashSet<>();
        int nowYear = LocalDateTime.now().getYear();
        if (createTime != null){
            int oldYear = createTime.getYear();
            //当前时间
            int year = nowYear - oldYear;
            for (int i = 0; i <= year; i++){
                timeSet.add(oldYear++);
            }
        }else{
            timeSet.add(nowYear);
        }

        CommentUserWriteRequest commentUserWriteRequest = new CommentUserWriteRequest();
        commentUserWriteRequest.setStatus(CommentWriteStatusEnum.MY_PUBLISH.getValue());
        commentUserWriteRequest.setCurrent(1);
        //获取首页数据
        PageVO pageVO = this.getCommentUserVoList(commentUserWriteRequest, loginUser);
        CommentUserHomeVO commentUserHomeVO = new CommentUserHomeVO();
        commentUserHomeVO.setTimeSet(timeSet);
        commentUserHomeVO.setPageVO(pageVO);
        return commentUserHomeVO;
    }


    /**
     * 根据条件获取有关用户的评论信息列表
     * @param commentUserWriteRequest 筛选条件
     * @param loginUser 当前登录用户
     * @return 关于用户的评论信息列表
     */
    @Override
    public PageVO getCommentUserVoList(CommentUserWriteRequest commentUserWriteRequest, User loginUser){
        if (commentUserWriteRequest.getStatus() == null || commentUserWriteRequest.getStatus() < 0){
            commentUserWriteRequest.setStatus(CommentWriteStatusEnum.MY_PUBLISH.getValue());
        }
        if (commentUserWriteRequest.getCurrent() == null || commentUserWriteRequest.getCurrent() <= 0){
            commentUserWriteRequest.setCurrent(1);
        }
        LambdaQueryWrapper<Comments> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comments::getIsDelete,0)
                    .eq(Comments::getIsShow,0)
                .orderByDesc(Comments::getCreateTime);
        //判断是否选择年份
        if (commentUserWriteRequest.getYear() != null && commentUserWriteRequest.getYear() != 0){
            queryWrapper.apply("YEAR(create_time) = {0}", commentUserWriteRequest.getYear());
            //判断是否选择月份
            if (commentUserWriteRequest.getMonth() != null && commentUserWriteRequest.getMonth() != 0){
                queryWrapper.apply("MONTH(create_time) = {0}", commentUserWriteRequest.getMonth());
            }
        }
        //如果输入了关键字，则根据评论内容筛选
        if (StringUtils.isNotBlank(commentUserWriteRequest.getContent())){
            String content = commentUserWriteRequest.getContent();
            queryWrapper.like(Comments::getContent,content);
        }
        Page<Comments> page = new Page<>(commentUserWriteRequest.getCurrent(), CommonConstant.PAGE_SIZE);
        //获取用户筛选的状态
        CommentWriteStatusEnum enumByValue = CommentWriteStatusEnum.getEnumByValue(commentUserWriteRequest.getStatus());
        assert enumByValue != null;
        String status = enumByValue.getText();
        //要返回的数据
        List<CommentUserVO> commentUserVOList = null;
        //总数据条数
        long total;

        if (status.equals(CommentWriteStatusEnum.MY_PUBLISH.getText())){
            //查看所有我发表的评论
            queryWrapper.eq(Comments::getIsTop,0)
                    .eq(Comments::getUserId,loginUser.getId())
                    .eq(Comments::getIsShow,0);
            page(page,queryWrapper);
            //处理评论返回数据
            commentUserVOList = page.getRecords().stream().map(comments -> handleCommentInfo(comments,CommentWriteStatusEnum.MY_PUBLISH.getValue())).collect(Collectors.toList());
        }else if (status.equals(CommentWriteStatusEnum.MY_REPLY.getText())){
            //查看所有我回复的评论
            queryWrapper.eq(Comments::getIsTop ,1)
                    .eq(Comments::getUserId,loginUser.getId())
                    .eq(Comments::getIsShow,0);
            page(page,queryWrapper);
            //处理评论返回数据
            commentUserVOList = page.getRecords().stream().map(comments -> handleCommentInfo(comments,CommentWriteStatusEnum.MY_REPLY.getValue())).collect(Collectors.toList());
        }else if(status.equals(CommentWriteStatusEnum.REPLY_TO_ME.getText())){
            //查看所有回复我的评论
            queryWrapper.apply("parent_id in (SELECT id FROM tb_comments WHERE user_id = {0}  AND is_delete = 0 AND is_show = 0)",loginUser.getId());
            page(page,queryWrapper);
            //处理评论返回数据
            commentUserVOList = page.getRecords().stream().map(comments -> handleCommentInfo(comments,CommentWriteStatusEnum.REPLY_TO_ME.getValue())).collect(Collectors.toList());
        }else if (status.equals(CommentWriteStatusEnum.BAN.getText())){
            //被封禁的评论
            queryWrapper.eq(Comments::getIsComplain,1)
                    .eq(Comments::getUserId,loginUser.getId());
            page(page,queryWrapper);
            //处理评论返回数据
            commentUserVOList = page.getRecords().stream().map(comments -> handleCommentInfo(comments,CommentWriteStatusEnum.BAN.getValue())).collect(Collectors.toList());
        }
        total = page.getTotal();
        return new PageVO(commentUserVOList,total);
    }

    /**
     * 在用户个人评论区回复评论
     * @param commentAddRequest 评论内容
     * @param loginUser 获取当前登录用户
     * @return 是否评论成功
     */
    @Override
    public boolean addCommentReply(CommentAddRequest commentAddRequest, User loginUser) {
        //根据父评论获取文章信息
        Comments parentComment = this.getById(commentAddRequest.getParentId());

        Comments comments = new Comments();
        comments.setArticleId(commentAddRequest.getArticleId());
        comments.setContent(commentAddRequest.getContent());
        comments.setUserId(loginUser.getId());
        comments.setCreateTime(LocalDateTime.now());
        if (commentAddRequest.getParentId() != null && commentAddRequest.getParentId() > 0){
            comments.setParentId(commentAddRequest.getParentId());
        }
        if (parentComment.getTopId() != null && parentComment.getTopId() > 0){
            comments.setTopId(parentComment.getTopId());
        }else{
            comments.setTopId(parentComment.getId());
        }
        comments.setIsTop(commentAddRequest.getIsTop());
        comments.setPraiseNumber(0);
        comments.setIsSticky(0);
        comments.setIsShow(0);
        boolean save = this.save(comments);
        if (save){
            //评论数加1
            boolean add = articleMapper.addCommentNumber(commentAddRequest.getArticleId());
            if (!add){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"自增评论数失败");
            }
            return true;
        }
        return false;
    }


    /**
     * 获取子评论
     * @param parentId 父评论id
     * @param  topId 顶层评论id
     * @return 子评论列表
     */
    private List<CommentListVO> getCommentChildList(Long parentId,Long topId,User loginUser){
        if (parentId == null || parentId <= 0){
            return null;
        }
        if (topId == null || topId <= 0){
            return  null;
        }
        List<CommentListVO> commentChildVOS = commentsMapper.getCommentChildList(parentId,topId);
        commentChildVOS = commentChildVOS.stream().peek(commentChildVO -> {
            //通过评论id获取子评论信息
            List<CommentListVO> commentChildVOList = getCommentChildList(commentChildVO.getId(),commentChildVO.getTopId(),loginUser);
            if (commentChildVOList != null && !commentChildVOList.isEmpty()){
                commentChildVO.setCommentChildList(commentChildVOList);
            }
            //判断是否点赞
            if(loginUser != null){
                //判断当前用户是否点赞
                boolean praise = praiseService.judgePraise(commentChildVO.getId(),loginUser.getId(), PraiseConstant.COMMENT_TYPE);
                if (praise){
                    commentChildVO.setIsPraise(1);
                }
            }
        }).collect(Collectors.toList());
        return commentChildVOS;
    }


    /**
     * 处理我发表的评论信息
     * @param comments 评论信息
     * @return 返回处理后结果
     */
    private CommentUserVO handleCommentInfo(Comments comments,Integer status){
        CommentUserVO commentUserVO = null;
        if (status.equals(CommentWriteStatusEnum.MY_REPLY.getValue())){
            //我回复的评论
            commentUserVO = BeanCopyUtils.copyBean(comments, CommentUserVO.class);
            Comments parentComments = this.getById(comments.getParentId());
            commentUserVO.setReceiveUserId(parentComments.getUserId());
            User user = userService.getById(parentComments.getUserId());
            commentUserVO.setUsername(user.getUsername());
            commentUserVO.setReceiveContent(parentComments.getContent());
            commentUserVO.setRole(0);
        }else  if (status.equals(CommentWriteStatusEnum.REPLY_TO_ME.getValue())){
            commentUserVO = new CommentUserVO();
            //回复我的评论
            commentUserVO.setId(comments.getId());
            commentUserVO.setArticleId(comments.getArticleId());
            commentUserVO.setUserId(commentUserVO.getUserId());
            User user = userService.getById(comments.getUserId());
            commentUserVO.setUsername(user.getUsername());
            commentUserVO.setContent(comments.getContent());
            Comments parentComments = this.getById(comments.getParentId());
            commentUserVO.setReceiveUserId(parentComments.getUserId());
            commentUserVO.setReceiveContent(parentComments.getContent());
            commentUserVO.setCreateTime(comments.getCreateTime());
            commentUserVO.setRole(1);
        }else if (status.equals(CommentWriteStatusEnum.BAN.getValue())){
            //被封禁的评论
            commentUserVO = BeanCopyUtils.copyBean(comments,CommentUserVO.class);
            if (comments.getParentId() != null ){
                Comments parentComments = this.getById(comments.getParentId());
                commentUserVO.setReceiveUserId(parentComments.getUserId());
                User user = userService.getById(parentComments.getUserId());
                commentUserVO.setUsername(user.getUsername());
                commentUserVO.setReceiveContent(parentComments.getContent());
            }
            //我被封禁的评论
            commentUserVO.setRole(0);
        }else {
            commentUserVO = BeanCopyUtils.copyBean(comments, CommentUserVO.class);
            //我发布的评论
            commentUserVO.setRole(0);
        }

        ArticleOneVO articleOneVO = articleMapper.selectArticleOne(comments.getArticleId());
        commentUserVO.setArticleUserId(articleOneVO.getUserId());
        commentUserVO.setArticleUsername(articleOneVO.getUsername());
        commentUserVO.setTitle(articleOneVO.getTitle());
        commentUserVO.setStatus(status);
        return commentUserVO;
    }



}
