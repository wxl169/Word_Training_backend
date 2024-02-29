package org.wxl.wordTraining.model.vo.word;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author wxl
 */

@Data
public class WordBankListVO implements Serializable {
    private static final long serialVersionUID = -4867749647263185994L;

    @ApiModelProperty(value = "单词id")
    private Long id;

    @ApiModelProperty(value = "单词")
    private String word;

    @ApiModelProperty(value = "翻译")
    private List<String> translation;

    @ApiModelProperty(value = "类型")
    private List<String> type;

    @ApiModelProperty(value = "英式发音")
    private String pronounceEnglish;

    @ApiModelProperty(value = "美式发音")
    private String pronounceAmerica;
    @ApiModelProperty(value = "是否收藏 (ture：收藏，false:未收藏)")
    private Boolean isCollection;

}
