package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.entity.TaskGet;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 */
public interface ITaskGetService extends IService<TaskGet> {


    List<TaskGet> selectTaskByUser(Long id);
}
