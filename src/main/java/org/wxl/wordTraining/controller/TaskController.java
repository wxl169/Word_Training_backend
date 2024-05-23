package org.wxl.wordTraining.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wxl.wordTraining.annotation.AuthCheck;
import org.wxl.wordTraining.annotation.JwtToken;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.constant.UserConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.task.TaskAddRequest;
import org.wxl.wordTraining.model.vo.task.TaskSelectVO;
import org.wxl.wordTraining.service.ITaskService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wxl
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    private final ITaskService taskService;
    @Autowired
    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 添加任务
     * @param taskAddRequest 任务添加请求
     * @return 添加结果
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> addTask (@RequestBody TaskAddRequest taskAddRequest){
        if (taskAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        Boolean add = taskService.addTask(taskAddRequest);
        if (add){
            return ResultUtils.success(true);
        }else{
            return ResultUtils.error(ErrorCode.OPERATION_ERROR,"添加失败");
        }

    }


    @PostMapping("/get")
    @JwtToken
    public BaseResponse<List<TaskSelectVO>> selectTask (HttpServletRequest request){
        return ResultUtils.success(taskService.selectTask(request));
    }

}
