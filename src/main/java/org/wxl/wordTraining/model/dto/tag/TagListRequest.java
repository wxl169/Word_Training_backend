package org.wxl.wordTraining.model.dto.tag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */

@Data
public class TagListRequest implements Serializable {
    private static final long serialVersionUID = -9009960318922359321L;
    @ApiModelProperty(value = "页码")
    private Integer current;
    @ApiModelProperty(value = "每页数据量")
    private Integer pageSize;
    @ApiModelProperty(value = "标签名")
    private String tagName;
}
