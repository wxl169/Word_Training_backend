package org.wxl.wordTraining.service.impl;

import org.wxl.wordTraining.model.entity.TaskGet;
import org.wxl.wordTraining.mapper.TaskGetMapper;
import org.wxl.wordTraining.service.ITaskGetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class TaskGetServiceImpl extends ServiceImpl<TaskGetMapper, TaskGet> implements ITaskGetService {

    @Override
    public List<TaskGet> selectTaskByUser(Long id) {
        return null;
    }
}
