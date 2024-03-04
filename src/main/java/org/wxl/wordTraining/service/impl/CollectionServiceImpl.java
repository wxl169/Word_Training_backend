package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.CollectionConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.ArticleMapper;
import org.wxl.wordTraining.model.dto.collection.CollectionRequest;
import org.wxl.wordTraining.model.entity.TbArticle;
import org.wxl.wordTraining.model.entity.TbCollection;
import org.wxl.wordTraining.mapper.CollectionMapper;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.service.IArticleService;
import org.wxl.wordTraining.service.ICollectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, TbCollection> implements ICollectionService {
private final CollectionMapper collectionMapper;
private final RedissonClient redissonClient;
private final ArticleMapper articleMapper;
@Autowired
public CollectionServiceImpl(CollectionMapper collectionMapper,RedissonClient redissonClient,ArticleMapper articleMapper){
    this.collectionMapper = collectionMapper;
    this.redissonClient = redissonClient;
    this.articleMapper = articleMapper;
}


    /**
     * 添加收藏
     * @param collectionAddRequest 收藏物id和类型（0：文章，1：单词）
     * @param loginUser 当前登录用户
     * @return 是否收藏成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCollection(CollectionRequest collectionAddRequest, User loginUser) {
        RLock userLock = redissonClient.getLock("wordTraining:collection:addCollection:user:" + loginUser.getId());
        boolean add = false;
        try {
            while (true){
                boolean tryLock = userLock.tryLock(0,-1, TimeUnit.SECONDS);
                if (tryLock){
                    //查看当前物品是否收藏
                    TbCollection tbCollection = collectionMapper.selectCollection(collectionAddRequest,loginUser);
                    //判断是否收藏该物品
                    if (tbCollection != null) {
                        //如果已经删除，则修改为0
                        if (tbCollection.getIsDelete() == 1){
                            //修改数据
                            add = collectionMapper.updateCollection(collectionAddRequest,loginUser);
                        }else{
                           throw new BusinessException(ErrorCode.OPERATION_ERROR,"请勿重复点击收藏");
                        }
                    }else{
                        TbCollection collection = new TbCollection();
                        collection.setCollectionId(collectionAddRequest.getId());
                        collection.setType(collectionAddRequest.getType());
                        collection.setUserId(loginUser.getId());
                        collection.setCollectionTime(LocalDateTime.now());
                        add = this.save(collection);
                    }
                    if (add){
                        //修改收藏数量
                        if (collectionAddRequest.getType().equals(CollectionConstant.ARTICLE_TYPE)){
                            boolean update = articleMapper.addArticleCollectionNumber(collectionAddRequest.getId());
                            if (!update){
                                throw new BusinessException(ErrorCode.OPERATION_ERROR,"修改文章收藏量失败");
                            }
                        }
                        return true;
                    }else {
                        throw new BusinessException(ErrorCode.OPERATION_ERROR,"收藏失败");
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
     * 取消收藏
     * @param collectionDeleteRequest 收藏物id和类型（0：文章，1：单词）
     * @param loginUser 当前登录用户
     * @return 是否取消成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCollection(CollectionRequest collectionDeleteRequest, User loginUser) {
        LambdaQueryWrapper<TbCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbCollection::getCollectionId,collectionDeleteRequest.getId())
                .eq(TbCollection::getType,collectionDeleteRequest.getType())
                .eq(TbCollection::getUserId,loginUser.getId());
        TbCollection collection = this.getOne(queryWrapper);
        if (collection == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"还没有收藏该物品");
        }else{
            //取消收藏
            boolean b = this.removeById(collection.getId());
            if (b){
                //文章收藏量减1
                if (collectionDeleteRequest.getType().equals(CollectionConstant.ARTICLE_TYPE)){
                  boolean update = articleMapper.deleteArticleCollectionNumber(collectionDeleteRequest.getId());
                  if (!update){
                      throw new BusinessException(ErrorCode.OPERATION_ERROR,"减少文章收藏量失败");
                  }
                }
                return true;
            }else{
               throw new BusinessException(ErrorCode.OPERATION_ERROR,"删除收藏记录失败");
            }
        }
    }

    /**
     * 判断当前用户是否收藏该物品
     * @param collectionId 收藏物id
     * @param userId 当前用户id
     * @param type 类型id
     * @return 是否收藏
     */
    @Override
    public boolean judgeCollection(Long collectionId, Long userId, Integer type) {
        if (collectionId == null || collectionId <= 0){
            return false;
        }
        if (userId == null || userId <= 0){
            return false;
        }
        if (type == null){
            return false;
        }

        LambdaQueryWrapper<TbCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbCollection::getCollectionId,collectionId)
                .eq(TbCollection::getType,type)
                .eq(TbCollection::getUserId,userId);
        TbCollection collection = this.getOne(queryWrapper);
        return collection != null;
    }


}
