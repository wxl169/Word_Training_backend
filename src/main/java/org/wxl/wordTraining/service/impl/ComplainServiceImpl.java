package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.CommonConstant;
import org.wxl.wordTraining.constant.ComplainConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.exception.ThrowUtils;
import org.wxl.wordTraining.mapper.ArticleMapper;
import org.wxl.wordTraining.mapper.CommentsMapper;
import org.wxl.wordTraining.mapper.UserMapper;
import org.wxl.wordTraining.model.dto.complain.ComplainAddRequest;
import org.wxl.wordTraining.model.dto.complain.ComplainAditingRequest;
import org.wxl.wordTraining.model.dto.complain.ComplainListRequest;
import org.wxl.wordTraining.model.entity.*;
import org.wxl.wordTraining.mapper.ComplainMapper;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.complain.ComplainListVO;
import org.wxl.wordTraining.service.IComplainService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class ComplainServiceImpl extends ServiceImpl<ComplainMapper, Complain> implements IComplainService {

    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final CommentsMapper commentsMapper;
@Autowired
    public ComplainServiceImpl(UserMapper userMapper,ArticleMapper articleMapper,CommentsMapper commentsMapper) {
        this.userMapper = userMapper;
        this.articleMapper = articleMapper;
        this.commentsMapper = commentsMapper;
    }
    /**
     * 投诉
     * @param complainAddRequest 投诉信息
     * @param loginUser 当前登录用户
     * @return 是否投诉成功
     */
    @Override
    public boolean addComplain(ComplainAddRequest complainAddRequest, User loginUser) {
        if(loginUser.getId().equals(complainAddRequest.getIsComplainUserId())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不能投诉自己");
        }
        //被投诉的用户是否存在
        User user = userMapper.selectById(complainAddRequest.getIsComplainUserId());
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"被投诉的用户不存在");
        }
        //被投诉的类别是否存在
        if(complainAddRequest.getType().equals(ComplainConstant.ARTICLE_TYPE)){
            //投诉文章是否存在
            if(articleMapper.selectById(complainAddRequest.getComplainId()) == null) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "投诉的文章不存在");
            }
        }else if(complainAddRequest.getType().equals(ComplainConstant.COMMENTS_TYPE)){
            if (commentsMapper.selectById(complainAddRequest.getComplainId()) == null){
                throw new BusinessException(ErrorCode.NULL_ERROR, "投诉的评论不存在");
            }
        }
        //查看当前用户是否投诉过该类别，若投诉过则覆盖投诉内容，更新状态和投诉时间、审核时间
        Complain oldComplain =
                this.lambdaQuery().eq(Complain::getComplainId,complainAddRequest.getComplainId())
               .eq(Complain::getType,complainAddRequest.getType())
               .eq(Complain::getComplainUserId,loginUser.getId())
               .eq(Complain::getIsComplainUserId,complainAddRequest.getIsComplainUserId())
               .one();
        if(oldComplain!= null) {
            oldComplain.setComplainContent(complainAddRequest.getComplainContent());
            oldComplain.setStatus(0);
            oldComplain.setComplainTime(LocalDateTime.now());
            oldComplain.setReviewTime(null);
            return this.updateById(oldComplain);
        }


        Complain complain = new Complain();
        complain.setComplainId(complainAddRequest.getComplainId());
        complain.setType(complainAddRequest.getType());
        complain.setComplainContent(complainAddRequest.getComplainContent());
        complain.setComplainUserId(loginUser.getId());
        complain.setIsComplainUserId(complainAddRequest.getIsComplainUserId());
        complain.setStatus(0);
        complain.setComplainTime(LocalDateTime.now());
        return this.save(complain);
    }

    /**
     * 获取投诉列表
     * @param complainListRequest 投诉列表请求
     * @param request         请求对象
     * @return  投诉列表
     */
    @Override
    public PageVO getComplainListByPage(ComplainListRequest complainListRequest, HttpServletRequest request) {
        Integer current = complainListRequest.getCurrent();
        Integer pageSize = complainListRequest.getPageSize();

        if (current == null || current <= 0){
            current = CommonConstant.PAGE_NUM;
        }
        if (pageSize == null || pageSize <= 0){
            pageSize = CommonConstant.PAGE_SIZE;
        }
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);

        LambdaQueryWrapper<Complain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(complainListRequest.getType() != null, Complain::getType, complainListRequest.getType())
                .eq(Complain::getIsDelete, 0)
                .select(Complain::getComplainId,Complain::getType);

        if (complainListRequest.getIsComplainUserAccount() != null) {
            Set<Long> userIdSet = userMapper.selectUserId(complainListRequest.getIsComplainUserAccount());
            if (!userIdSet.isEmpty()) {
                queryWrapper.in(Complain::getIsComplainUserId, userIdSet);
            }
        }

        if (complainListRequest.getComplainObject() != null) {
            Set<Long> articleIdSet = articleMapper.selectArticleId(complainListRequest.getComplainObject());
            Set<Long> commentIdSet = commentsMapper.selectCommentId(complainListRequest.getComplainObject());

            Set<Long> combinedIds = new HashSet<>();
            combinedIds.addAll(articleIdSet);
            combinedIds.addAll(commentIdSet);
            if (!combinedIds.isEmpty()) {
                queryWrapper.in(Complain::getComplainId, combinedIds);
            }
        }

// 可能需要根据具体需求调整分组和排序方式
        queryWrapper.groupBy(Complain::getComplainId,Complain::getType);


        Page<Complain> page = new Page<>(current,pageSize);
        page(page,queryWrapper);
        AtomicReference<Long> i = new AtomicReference<>(0L);
        List<ComplainListVO> complainListVOS = page.getRecords().stream().map(complain -> {
            ComplainListVO complainListVO = new ComplainListVO();
            complainListVO.setComplainId(complain.getComplainId());
            complainListVO.setType(complain.getType());
            Long isComplainUserId = 0L;
            if (complain.getType().equals(ComplainConstant.ARTICLE_TYPE)){
                //根据id查文章标题
                TbArticle tbArticle = articleMapper.selectById(complain.getComplainId());
                isComplainUserId = tbArticle.getUserId();
            }

            if (complain.getType().equals(ComplainConstant.COMMENTS_TYPE)){
                Comments comments = commentsMapper.selectById(complain.getComplainId());
                isComplainUserId = comments.getUserId();
            }
            if (isComplainUserId == 0){
                throw new BusinessException(ErrorCode.NULL_ERROR,"投诉对象不存在");
            }
            complainListVO.setId(i.getAndSet(i.get() + 1));
            complainListVO.setIsComplainUserId(isComplainUserId);
            //根据id查用户账号
            User user = userMapper.selectById(isComplainUserId);
            complainListVO.setIsComplainUserAccount(user.getUserAccount());
            List<ComplainListVO> childComplainList = this.getChildComplainList(complain.getComplainId(), complain.getType());
            if (childComplainList != null && childComplainList.size() > 0) {
                complainListVO.setChilderComplainList(childComplainList);
                complainListVO.setComplainNumber(childComplainList.size());
            }
            return complainListVO;
        }).collect(Collectors.toList());
        PageVO pageVO = new PageVO();
        pageVO.setTotal(page.getTotal());
        pageVO.setRows(complainListVOS);
        return pageVO;
    }

    /**
     * 投诉审核
     * @param complainAditingRequest 投诉审核请求
     * @param request 请求对象
     * @return 是否审核成功
     */
    @Override
    public Boolean updateComplainAditing(ComplainAditingRequest complainAditingRequest, HttpServletRequest request) {
        LambdaQueryWrapper<Complain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Complain::getId,complainAditingRequest.getId());
        Complain complain = this.getOne(queryWrapper);
        if(complain == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"投诉不存在");
        }
        complain.setReviewComment(complainAditingRequest.getReviewComment());
        complain.setStatus(1);
        complain.setReviewTime(LocalDateTime.now());
        boolean b = this.saveOrUpdate(complain);
        if (!b){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"审核失败");
        }

        return true;
    }

    /**
     * 获取子投诉列表
     * @param complainId 投诉对象Id
     * @param type 投诉类型
     * @return
     */
    private List<ComplainListVO> getChildComplainList(Long complainId,Integer type){
        LambdaQueryWrapper<Complain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Complain::getComplainId,complainId)
               .eq(Complain::getType,type)
               .eq(Complain::getIsDelete,0);
        List<Complain> complainList = this.list(queryWrapper);
        if(complainList != null && complainList.size() > 0) {
            return complainList.stream().map(complain -> {
                ComplainListVO complainListVO = new ComplainListVO();
                complainListVO.setId(complain.getId());
                complainListVO.setComplainContent(complain.getComplainContent());
                complainListVO.setComplainTime(complain.getComplainTime());
                complainListVO.setReviewTime(complain.getReviewTime());
                complainListVO.setStatus(complain.getStatus());
                complainListVO.setComplainUserId(complain.getComplainUserId());
                //根据id查用户账号
                User user = userMapper.selectById(complain.getComplainUserId());
                complainListVO.setComplainUserAccount(user.getUserAccount());
                return complainListVO;
            }).collect(Collectors.toList());
        }
        return null;
    }

}
