package org.wxl.wordTraining.mapper;

import org.apache.ibatis.annotations.Param;
import org.wxl.wordTraining.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxl
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据账号查询用户id
     * @param userAccount 用户账号
     * @return 用户Id
     */
    Long selectByUserAccount(String userAccount);

    /**
     * 更新用户积分总数
     * @param userId 用户Id
     * @param pointAll 积分总数
     * @return 是否更新成功
     */
    boolean updatePointAll(@Param("userId") Long userId, @Param("pointAll") int pointAll);


    /**
     * 根据用户账号模糊匹配符合条件的用户Id
     * @param userAccount 用户账号
     * @return 用户Id集合
     */
    Set<Long> selectUserId(String userAccount);
}
