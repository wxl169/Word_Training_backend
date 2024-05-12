package org.wxl.wordTraining.model.vo.wordTraining;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class WordTrainingJudgementVO implements Serializable {
    private static final long serialVersionUID = 5703918857420777536L;
    @ApiModelProperty("题目")
    private WordTrainingVO wordTrainingVO;
    @ApiModelProperty("模式（0：英语选义，1：中文选义，2：填空拼写）")
    private Integer mode;
    @ApiModelProperty("难度（0：训练模式，1：挑战模式）")
    private Integer difficulty;
    @ApiModelProperty("总题目数")
    private Integer total;
    @ApiModelProperty("是否正确")
    private Boolean isTrue;
    @ApiModelProperty("正确单词Id")
    private Long wordId;
    @ApiModelProperty("生命值")
    private Integer healthValue;
}
