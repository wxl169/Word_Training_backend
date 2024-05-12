package org.wxl.wordTraining.model.vo.wordTraining;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wxl
 */
@Data
public class WordTrainingTotalVO   {
    private WordTrainingVO wordTrainingVO;

    @ApiModelProperty("总条数")
    private Integer total;

    @ApiModelProperty("生命值")
    private Integer healthValue;
}
