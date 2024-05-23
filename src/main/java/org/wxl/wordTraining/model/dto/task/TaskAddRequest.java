package org.wxl.wordTraining.model.dto.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class TaskAddRequest implements Serializable {
    private static final long serialVersionUID = 9002699992777205204L;
    @ApiModelProperty(value = "任务标题")
    private String title;
    @ApiModelProperty(value = "任务描述")
    private String description;
    @ApiModelProperty(value = "任务达成条件展示")
    private String conditionShow;
    @ApiModelProperty(value = "任务达成条件")
    private String condition;
    @ApiModelProperty(value = "任务达成的区域")
    private Integer region;
    @ApiModelProperty(value = "任务达成数量")
    private Long number;
    @ApiModelProperty(value = "任务奖励")
    private String reward;
    @ApiModelProperty(value = "任务类型")
    private Integer type;
    @ApiModelProperty(value = "任务开始时间")
    private String beginTime;
    @ApiModelProperty(value = "任务结束时间")
    private String endTime;


}
