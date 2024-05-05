package org.wxl.wordTraining.model.vo.wordTraining;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WordTrainingTotalVO   {
    private WordTrainingVO wordTrainingVO;
    @ApiModelProperty("总条数")
    private Integer total;
}
