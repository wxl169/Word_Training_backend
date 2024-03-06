package org.wxl.wordTraining.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 *  投诉
 * </p>
 *
 * @author wxl
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_complain")
@ApiModel(value="Complain对象", description="")
public class Complain implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "投诉id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "投诉对象id")
    private Long complainId;

    @ApiModelProperty(value = "投诉类型（0：文章，1：评论）")
    private Integer type;

    @ApiModelProperty(value = "投诉内容")
    private String complainContent;

    @ApiModelProperty(value = "投诉人id")
    private Long complainUserId;

    @ApiModelProperty(value = "被投诉人id")
    private Long isComplainUserId;

    @ApiModelProperty(value = "状态（0：未处理，1：已处理）")
    private Integer status;

    @ApiModelProperty(value = "审核批注")
    private String reviewComment;

    @ApiModelProperty(value = "投诉时间")
    private LocalDateTime complainTime;

    @ApiModelProperty(value = "审核时间")
    private LocalDateTime reviewTime;

    @ApiModelProperty(value = "逻辑删除（0：未删除，1：已删除）")
    @TableField
    private Integer isDelete;


}
