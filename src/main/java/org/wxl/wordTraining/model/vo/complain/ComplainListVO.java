package org.wxl.wordTraining.model.vo.complain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class ComplainListVO implements Serializable {

    private static final long serialVersionUID = 5573841279477163759L;
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "被投诉对象id")
    private Long complainId;

    @ApiModelProperty(value = "投诉类型")
    private Integer type;

    @ApiModelProperty(value = "投诉原因")
    private String complainContent;

    @ApiModelProperty(value = "投诉量")
    private Integer complainNumber;

    @ApiModelProperty(value = "投诉状态")
    private Integer status;

    @ApiModelProperty(value = "投诉人id")
    private Long complainUserId;

    @ApiModelProperty(value = "投诉人账号")
    private String complainUserAccount;

    @ApiModelProperty(value = "被投诉人id")
    private Long isComplainUserId;

    @ApiModelProperty(value = "被投诉人账号")
    private String isComplainUserAccount;

    @ApiModelProperty(value = "投诉时间")
    private LocalDateTime complainTime;

    @ApiModelProperty(value = "审核时间")
    private LocalDateTime reviewTime;

    @ApiModelProperty(value = "投诉人类")
    private List<ComplainListVO> childerComplainList;
}
