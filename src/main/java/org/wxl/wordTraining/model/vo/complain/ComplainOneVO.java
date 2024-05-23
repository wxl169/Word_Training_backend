package org.wxl.wordTraining.model.vo.complain;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class ComplainOneVO implements Serializable {
    private static final long serialVersionUID = 725973317905351086L;
    @ApiModelProperty(value = "投诉id")
    private Long id;
    @ApiModelProperty(value = "投诉内容")
    private String reviewComment;

}
