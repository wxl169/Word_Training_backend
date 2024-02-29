package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.collection.CollectionRequest;
import org.wxl.wordTraining.model.entity.TbCollection;
import org.wxl.wordTraining.mapper.CollectionMapper;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.service.ICollectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
@Autowired
public CollectionServiceImpl(CollectionMapper collectionMapper){
    this.collectionMapper = collectionMapper;
}


    /**
     * 添加收藏
     * @param collectionAddRequest 收藏物id和类型（0：文章，1：单词）
     * @param loginUser 当前登录用户
     * @return 是否收藏成功
     */
    @Override
    public boolean addCollection(CollectionRequest collectionAddRequest, User loginUser) {
        LambdaQueryWrapper<TbCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbCollection::getCollectionId,collectionAddRequest.getId())
                .eq(TbCollection::getType,collectionAddRequest.getType())
                .eq(TbCollection::getUserId,loginUser.getId());
        Long count = collectionMapper.selectCount(queryWrapper);
        if (count == 0){
            TbCollection collection = new TbCollection();
            collection.setCollectionId(collectionAddRequest.getId());
            collection.setType(collectionAddRequest.getType());
            collection.setUserId(loginUser.getId());
            collection.setCollectionTime(LocalDateTime.now());
            int insert = collectionMapper.insert(collection);
            return insert > 0;
        }else{
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"请勿重复点击收藏");
        }
    }

    /**
     * 取消收藏
     * @param collectionDeleteRequest 收藏物id和类型（0：文章，1：单词）
     * @param loginUser 当前登录用户
     * @return 是否取消成功
     */
    @Override
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
            return this.removeById(collection.getId());
        }
    }


}
