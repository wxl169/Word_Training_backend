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
@TableName("tb_task_get")
@ApiModel(value="TaskGet对象", description="")
public class TaskGet implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务执行记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务id")
    private Long taskId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "当前数量")
    private Long nowNumber;

    @ApiModelProperty(value = "状态（0：未完成，1：已完成）")
    private Integer status;

    @ApiModelProperty(value = "任务执行时间")
    private LocalDateTime beginTime;

    @ApiModelProperty(value = "完成时间")
    private LocalDateTime finishTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除")
    private Integer isDelete;


}
