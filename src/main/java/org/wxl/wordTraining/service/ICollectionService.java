package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.collection.CollectionRequest;
import org.wxl.wordTraining.model.entity.TbCollection;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 */
public interface ICollectionService extends IService<TbCollection> {

    /**
     * 添加收藏
     * @param collectionAddRequest 收藏物id和类型（0：文章，1：单词）
     * @param loginUser 当前登录用户
     * @return 是否收藏成功
     */
    boolean addCollection(CollectionRequest collectionAddRequest, User loginUser);
    /**
     * 取消收藏
     * @param collectionDeleteRequest 收藏物id和类型（0：文章，1：单词）
     * @param loginUser 当前登录用户
     * @return 是否取消收藏成功
     */
    boolean deleteCollection(CollectionRequest collectionDeleteRequest, User loginUser);
}
