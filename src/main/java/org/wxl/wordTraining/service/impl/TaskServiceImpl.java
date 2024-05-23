package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.TaskGetMapper;
import org.wxl.wordTraining.model.dto.task.TaskAddRequest;
import org.wxl.wordTraining.model.entity.Medal;
import org.wxl.wordTraining.model.entity.Task;
import org.wxl.wordTraining.mapper.TaskMapper;
import org.wxl.wordTraining.model.entity.TaskGet;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.task.TaskSelectVO;
import org.wxl.wordTraining.service.ITaskGetService;
import org.wxl.wordTraining.service.ITaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {
private final UserService userService;
private final ITaskGetService iTaskGetService;
@Autowired
    public TaskServiceImpl(UserService userService, ITaskGetService iTaskGetService) {
        this.userService = userService;
        this.iTaskGetService = iTaskGetService;
    }
    @Override
    public Boolean addTask(TaskAddRequest taskAddRequest) {
        if (StringUtil.isBlank(taskAddRequest.getTitle())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"任务标题不能为空");
        }
        if (StringUtil.isBlank(taskAddRequest.getDescription())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"任务描述不能为空");
        }
        if (StringUtil.isBlank(taskAddRequest.getConditionShow())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"任务达成条件展示不能为空");
        }
        if (StringUtil.isBlank(taskAddRequest.getCondition())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"任务达成条件不能为空");
        }
        if (taskAddRequest.getType() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"任务类型不能为空");
        }
        if (StringUtil.isBlank(taskAddRequest.getReward())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"任务奖励不能为空");
        }
        Task task = BeanCopyUtils.copyBean(taskAddRequest, Task.class);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        task.setIsDelete(0);
        return this.save(task);
    }

    @Override
    public List<TaskSelectVO> selectTask(HttpServletRequest request) {
        return null;
    }



}
