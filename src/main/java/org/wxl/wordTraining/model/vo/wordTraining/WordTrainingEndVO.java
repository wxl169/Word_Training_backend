package org.wxl.wordTraining.model.vo.wordTraining;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wxl
 */
@Data
public class WordTrainingEndVO implements Serializable {
    private static final long serialVersionUID = 8913980735256021783L;
    @ApiModelProperty("总数据量")
    private Integer total;
    @ApiModelProperty("正确数量")
    private Integer correctNum;
    @ApiModelProperty("正确单词集合")
    private Map<Long,String> correctWordMap;
    @ApiModelProperty("错误数量")
    private Integer errorNum;
    @ApiModelProperty("错误单词集合")
    private Map<Long,String> errorWordMap;
    @ApiModelProperty("积分数")
    private Integer score;
    @ApiModelProperty("完成的题目数量")
    private Integer finishNum;

}
