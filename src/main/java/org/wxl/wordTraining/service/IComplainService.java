package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.complain.ComplainAddRequest;
import org.wxl.wordTraining.model.dto.complain.ComplainAditingRequest;
import org.wxl.wordTraining.model.dto.complain.ComplainListRequest;
import org.wxl.wordTraining.model.entity.Complain;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.PageVO;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 获取投诉列表
     * @param complainListRequest 投诉列表请求
     * @param request         请求对象
     * @return 投诉列表
     */
    PageVO getComplainListByPage(ComplainListRequest complainListRequest, HttpServletRequest request);

    /**
     * 更新投诉处理状态
     * @param complainAditingRequest
     * @param request
     * @return
     */
    Boolean updateComplainAditing(ComplainAditingRequest complainAditingRequest, HttpServletRequest request);
}
