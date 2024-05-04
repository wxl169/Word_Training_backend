package org.wxl.wordTraining.mapper;

import org.wxl.wordTraining.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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
}
