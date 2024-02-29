package org.wxl.wordTraining.model.entity;

import com.baomidou.mybatisplus.annotation.*;

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
@TableName("tb_word_type")
@ApiModel(value="WordType对象", description="")
public class WordType implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "单词类型id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "类型组名")
    private String typeGroupName;

    @ApiModelProperty(value = "类型名")
    private String typeName;

    @ApiModelProperty(value = "是否为类型组名（0：不是，1:是）")
    private Integer isGroupName;

    @ApiModelProperty(value = "属于类型组id")
    private Long typeGroupId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除（0：未删除，1：已删除）")
    @TableLogic
    private Integer isDelete;


}
