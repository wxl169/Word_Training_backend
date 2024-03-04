package org.wxl.wordTraining.mapper;

import org.apache.ibatis.annotations.Param;
import org.wxl.wordTraining.model.dto.collection.CollectionRequest;
import org.wxl.wordTraining.model.entity.TbCollection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wxl.wordTraining.model.entity.User;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxl
 */
public interface CollectionMapper extends BaseMapper<TbCollection> {

    /**
     * 查询该物品是否收藏
     * @param collectionAddRequest 物品信息
     * @param loginUser 当前登录用户
     * @return 收藏信息
     */
    TbCollection selectCollection(@Param("collectionAddRequest") CollectionRequest collectionAddRequest, @Param("loginUser") User loginUser);

    /**
     * 修改改物品信息
     * @param collectionAddRequest 物品信息
     * @param loginUser 当前登录用户
     * @return 是否修改成功
     */
    boolean updateCollection(@Param("collectionAddRequest")CollectionRequest collectionAddRequest,@Param("loginUser") User loginUser);
}
