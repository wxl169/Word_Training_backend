package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.CollectionConstant;
import org.wxl.wordTraining.constant.PraiseConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.ArticleMapper;
import org.wxl.wordTraining.mapper.CollectionMapper;
import org.wxl.wordTraining.model.dto.praise.PraiseAddRequest;
import org.wxl.wordTraining.model.dto.praise.PraiseDeleteRequest;
import org.wxl.wordTraining.model.entity.Praise;
import org.wxl.wordTraining.mapper.PraiseMapper;
import org.wxl.wordTraining.model.entity.TbCollection;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.service.IPraiseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  点赞服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class PraiseServiceImpl extends ServiceImpl<PraiseMapper, Praise> implements IPraiseService {
    private final PraiseMapper praiseMapper;
    private final RedissonClient redissonClient;
    private final ArticleMapper articleMapper;
    @Autowired
    public PraiseServiceImpl(PraiseMapper praiseMapper,RedissonClient redissonClient,ArticleMapper articleMapper){
        this.praiseMapper = praiseMapper;
        this.redissonClient = redissonClient;
        this.articleMapper = articleMapper;
    }
    /**
     * 判断当前用户是否点赞该物品
     * @param praiseId 点赞物id
     * @param userId 当前用户id
     * @param type 类型id
     * @return 是否点赞
     */
    @Override
    public boolean judgePraise(Long praiseId, Long userId, Integer type) {
        if (praiseId == null || praiseId <= 0){
            return false;
        }
        if (userId == null || userId <= 0){
            return false;
        }
        if (type == null){
            return false;
        }

        LambdaQueryWrapper<Praise> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Praise::getPraiseId,praiseId)
                .eq(Praise::getType,type)
                .eq(Praise::getUserId,userId);
        Praise praise = this.getOne(queryWrapper);
        return praise != null;
    }

    /**
     * 当前用户点赞单词
     * @param praiseAddRequest 点赞物id和类型（0：文章，1：评论）
     * @param loginUser 获取当前登录信息
     * @return 是否添加成功
     */
    @Override
    public Boolean addPraise(PraiseAddRequest praiseAddRequest, User loginUser) {
        RLock userLock = redissonClient.getLock("wordTraining:praise:addPraise:user:" + loginUser.getId());
        boolean add = false;
        try {
            while (true){
                boolean tryLock = userLock.tryLock(0,-1, TimeUnit.SECONDS);
                if (tryLock){
                    //查看当前物品是否点赞
                    Praise praise = praiseMapper.selectPraise(praiseAddRequest,loginUser);
                    //判断是否收藏该物品
                    if (praise != null) {
                        //如果已经删除，则修改为0
                        if (praise.getIsDelete() == 1){
                            //修改数据
                            add = praiseMapper.updatePraise(praiseAddRequest,loginUser);
                        }else{
                            throw new BusinessException(ErrorCode.OPERATION_ERROR,"请勿重复点击点赞");
                        }
                    }else{
                        Praise praise1 = new Praise();
                        praise1.setPraiseId(praiseAddRequest.getId());
                        praise1.setType(praiseAddRequest.getType());
                        praise1.setUserId(loginUser.getId());
                        praise1.setPraiseTime(LocalDateTime.now());
                        add = this.save(praise1);
                    }
                    if (add){
                        //修改收藏数量
                        if (praiseAddRequest.getType().equals(PraiseConstant.ARTICLE_TYPE)){
                            boolean update = articleMapper.addArticlePraiseNumber(praiseAddRequest.getId());
                            if (!update){
                                throw new BusinessException(ErrorCode.OPERATION_ERROR,"修改文章点赞量失败");
                            }
                        }
                        return true;
                    }else {
                        throw new BusinessException(ErrorCode.OPERATION_ERROR,"点赞失败");
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCollection error", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"收藏失败");
        } finally {
            userLock.unlock();
        }
    }

    /**
     * 当前用户取消点赞单词
     * @param praiseDeleteRequest 点赞物id和类型（0：文章，1：评论）
     * @param loginUser 获取当前登录信息
     * @return 是否取消成功
     */
    @Override
    public Boolean deletePraise(PraiseDeleteRequest praiseDeleteRequest, User loginUser) {
        LambdaQueryWrapper<Praise> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Praise::getPraiseId,praiseDeleteRequest.getId())
                .eq(Praise::getType,praiseDeleteRequest.getType())
                .eq(Praise::getUserId,loginUser.getId());
        Praise praise = this.getOne(queryWrapper);
        if (praise == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"还没有收藏该物品");
        }else{
            //取消收藏
            boolean b = this.removeById(praise.getId());
            if (b){
                //文章收藏量减1
                if (praiseDeleteRequest.getType().equals(CollectionConstant.ARTICLE_TYPE)){
                    boolean update = articleMapper.deleteArticlePraiseNumber(praiseDeleteRequest.getId());
                    if (!update){
                        throw new BusinessException(ErrorCode.OPERATION_ERROR,"减少文章点赞量失败");
                    }
                }
                return true;
            }else{
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"删除点赞记录失败");
            }
        }
    }
}
