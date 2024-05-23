package org.wxl.wordTraining.model.dto.complain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wxl
 */
@Data
public class ComplainListRequest implements java.io.Serializable {
    private static final long serialVersionUID = -6290495268521900078L;
    @ApiModelProperty(value = "页码")
    private Integer current;

    @ApiModelProperty(value = "每页数据量")
    private Integer pageSize;

    @ApiModelProperty(value = "被投诉类别")
    private Integer type;

    @ApiModelProperty(value = "被投诉对象")
    private String complainObject;

    @ApiModelProperty(value = "被投诉人账号")
    private String isComplainUserAccount;

}
