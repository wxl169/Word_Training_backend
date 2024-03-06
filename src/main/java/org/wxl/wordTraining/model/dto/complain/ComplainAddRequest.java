package org.wxl.wordTraining.model.dto.complain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class ComplainAddRequest implements Serializable {
    private static final long serialVersionUID = 1837417631253275627L;
    @ApiModelProperty(value = "投诉对象id")
    private Long complainId;

    @ApiModelProperty(value = "投诉类型（0：文章，1：评论）")
    private Integer type;

    @ApiModelProperty(value = "投诉内容")
    private String complainContent;

    @ApiModelProperty(value = "被投诉人id")
    private Long isComplainUserId;

}
