package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.collection.CollectionGetRequest;
import org.wxl.wordTraining.model.dto.collection.CollectionRequest;
import org.wxl.wordTraining.model.entity.TbCollection;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.PageVO;

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

    /**
     * 判断当前用户是否收藏该物品
     * @param collectionId 收藏物id
     * @param userId 当前用户id
     * @param type 类型id
     * @return 是否收藏
     */
    boolean judgeCollection(Long collectionId, Long userId,Integer type);

    /**
     * 获取当前登录用户收藏的内容信息
     * @param collectionGetRequest 用户选择展示的内容
     * @param loginUser 获取当前登陆用户信息
     * @return 内容信息
     */
    PageVO getCollectionByUserId(CollectionGetRequest collectionGetRequest, User loginUser);
}
