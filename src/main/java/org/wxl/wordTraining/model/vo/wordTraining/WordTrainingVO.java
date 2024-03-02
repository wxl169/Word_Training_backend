package org.wxl.wordTraining.model.vo.wordTraining;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class WordTrainingVO implements Serializable {
    private static final long serialVersionUID = 6274293789574853714L;

    @ApiModelProperty(value = "单词")
    private String word;
    @ApiModelProperty(value = "翻译")
    private String translation;
    @ApiModelProperty(value = "英式发音")
    private String pronounceEnglish;
    @ApiModelProperty(value = "美式发音")
    private String pronounceAmerica;
}
