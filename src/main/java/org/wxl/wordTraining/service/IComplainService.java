package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.complain.ComplainAddRequest;
import org.wxl.wordTraining.model.entity.Complain;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 * @since 2024-03-06
 */
public interface IComplainService extends IService<Complain> {

    /**
     * 投诉
     * @param complainAddRequest 投诉信息
     * @param loginUser 当前登录用户
     * @return 是否投诉成功
     */
    boolean addComplain(ComplainAddRequest complainAddRequest, User loginUser);
}
