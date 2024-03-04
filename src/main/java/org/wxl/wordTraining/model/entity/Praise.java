package org.wxl.wordTraining.model.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * 点赞
 * </p>
 *
 * @author wxl
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_praise")
@ApiModel(value="Praise对象", description="")
public class Praise implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "点赞id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "点赞对象id")
    private Long praiseId;

    @ApiModelProperty(value = "点赞类型(0:文章，1：评论）")
    private Integer type;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "点赞时间")
    private LocalDateTime praiseTime;

    @ApiModelProperty(value = "逻辑删除(0:未删除，1：已删除）")
    @TableLogic
    private Integer isDelete;


}
