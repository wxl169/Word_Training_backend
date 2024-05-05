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
@TableName("tb_word_answer")
@ApiModel(value="WordAnswer对象", description="")
public class WordAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "答题记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "单词id")
    private Long wordId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "积分")
    private Long points;

    @ApiModelProperty(value = "是否正确(0:正确,1:不正确)")
    private Integer isTrue;

    @ApiModelProperty(value = "错误原因")
    private String errorCause;

    @ApiModelProperty(value = "是否展示(0:展示，1：不展示")
    private Integer isShow;

    @ApiModelProperty(value = "状态(0：未纠错，1：已纠错）")
    private Integer status;

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
