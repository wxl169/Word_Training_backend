package org.wxl.wordTraining.model.vo.wordTraining;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author wxl
 */
@Data
public class WordTrainingVO implements Serializable {
    private static final long serialVersionUID = 6274293789574853714L;
    @ApiModelProperty(value = "题号")
    private Integer questionNumber;

    @ApiModelProperty(value = "单词")
    private String word;

    @ApiModelProperty(value = "翻译")
    private String translation;

    @ApiModelProperty(value = "翻译集合")
    private List<String> translationList;

    @ApiModelProperty(value = "英式发音")
    private String pronounceEnglish;

    @ApiModelProperty(value = "美式发音")
    private String pronounceAmerica;

    @ApiModelProperty(value = "题目A")
    private String questionA;

    @ApiModelProperty(value = "题目B")
    private String questionB;

    @ApiModelProperty(value = "题目C")
    private String questionC;

    @ApiModelProperty(value = "题目D")
    private String questionD;

    @ApiModelProperty("填空拼写选项集合")
    private Set<String> questionSet;

    @ApiModelProperty("是否正确(0:未开始，1：正确，2：错误)")
    private Integer isTrue;

    @ApiModelProperty("错误原因")
    private String errorCause;

    @ApiModelProperty("答案")
    private String answer;

    @ApiModelProperty("正确单词Id")
    private Long wordId;

}
