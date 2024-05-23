package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.task.TaskAddRequest;
import org.wxl.wordTraining.model.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.entity.TaskGet;
import org.wxl.wordTraining.model.vo.task.TaskSelectVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 */
public interface ITaskService extends IService<Task> {

    /**
     * 添加任务
     * @param taskAddRequest 任务添加请求
     * @return 添加成功返回true，否则返回false
     */
    Boolean addTask(TaskAddRequest taskAddRequest);

    List<TaskSelectVO> selectTask(HttpServletRequest request);

}
