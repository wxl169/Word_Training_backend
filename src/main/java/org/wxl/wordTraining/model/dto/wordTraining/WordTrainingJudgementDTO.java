package org.wxl.wordTraining.model.dto.wordTraining;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class WordTrainingJudgementDTO implements Serializable {

    private static final long serialVersionUID = 55964737770627349L;
    @ApiModelProperty("答案")
    private String answer;

    @ApiModelProperty("临时用户账号")
    private String temporaryUserAccount;

    @ApiModelProperty("题号")
    private Integer questionNumber;

    @ApiModelProperty("模式（0：英语选义，1：中文选义，2：填空拼写）")
    private Integer mode;
    @ApiModelProperty("难度（0：训练模式，1：挑战模式）")
    private Integer difficulty;
}
