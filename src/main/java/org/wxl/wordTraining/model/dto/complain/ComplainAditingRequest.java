package org.wxl.wordTraining.model.dto.complain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class ComplainAditingRequest implements Serializable {
    private static final long serialVersionUID = -6657600449777803377L;
    @ApiModelProperty("投诉Id")
    private Long id;
    @ApiModelProperty("审核结果")
    private String reviewComment;
}
