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
@TableName("tb_medal_get")
@ApiModel(value="MedalGet对象", description="")
public class MedalGet implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "勋章获取进度id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "勋章id")
    private Long achievementId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "当前数量")
    private Long nowNumber;

    @ApiModelProperty(value = "状态（0：未完成，1：已完成）")
    private Integer status;

    @ApiModelProperty(value = "是否佩戴（0：未佩戴，1：已佩戴）")
    private Integer isWear;

    @ApiModelProperty(value = "完成时间")
    private LocalDateTime finishTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除（0：未删除，1：已删除）")
    private Integer isDelete;


}
