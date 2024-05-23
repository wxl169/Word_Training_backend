package org.wxl.wordTraining.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wxl
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_task")
@ApiModel(value="Task对象", description="")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务标题")
    private String title;

    @ApiModelProperty(value = "任务描述")
    private String description;

    @ApiModelProperty(value = "任务达成条件展示（使用文字描述获取条件，展示给用户）")
    private String conditionShow;

    @ApiModelProperty(value = "任务达成条件（使用特定的描述获取条件，由任务发布者设定）")
    private String condition;

    @ApiModelProperty(value = "任务达成的区域（0：登录，1：单词，2：社区）")
    private Integer region;

    @ApiModelProperty(value = "任务达成数量")
    private Long number;

    @ApiModelProperty(value = "任务类型")
    private Integer type;

    @ApiModelProperty(value = "任务奖励")
    private String reward;

    @ApiModelProperty(value = "任务开始时间")
    private LocalDateTime beginTime;

    @ApiModelProperty(value = "任务结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除")
    private Integer isDelete;


}
