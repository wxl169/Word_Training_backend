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
 * @since 2024-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_medal")
@ApiModel(value="Medal对象", description="")
public class Medal implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "成就id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "勋章名")
    private String name;

    @ApiModelProperty(value = "勋章描述")
    private String description;

    @ApiModelProperty(value = "勋章获得条件展示（使用文字描述获取条件，展示给用户）")
    private String conditionShow;

    @ApiModelProperty(value = "勋章获得条件（使用特定的描述获取条件，由任务发布者设定）")
    private String condition;

    @ApiModelProperty(value = "勋章获得的区域（0：登录，1：单词，2：社区）")
    private Integer region;

    @ApiModelProperty(value = "勋章获得需求数量")
    private Long number;

    @ApiModelProperty(value = "勋章类型（0：普通成就，1：限定成就）")
    private Integer type;

    @ApiModelProperty(value = "勋章图标")
    private String logo;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime beginTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除（0：未删除，1：已删除）")
    private Integer isDelete;


}
