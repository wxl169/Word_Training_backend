package org.wxl.wordTraining.service.impl;

import org.wxl.wordTraining.model.dto.complain.ComplainAddRequest;
import org.wxl.wordTraining.model.entity.Complain;
import org.wxl.wordTraining.mapper.ComplainMapper;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.service.IComplainService;
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
public class ComplainServiceImpl extends ServiceImpl<ComplainMapper, Complain> implements IComplainService {

    /**
     * 投诉
     * @param complainAddRequest 投诉信息
     * @param loginUser 当前登录用户
     * @return 是否投诉成功
     */
    @Override
    public boolean addComplain(ComplainAddRequest complainAddRequest, User loginUser) {
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


}
