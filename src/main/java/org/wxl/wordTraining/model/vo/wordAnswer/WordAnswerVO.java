package org.wxl.wordTraining.model.vo.wordAnswer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class WordAnswerVO implements Serializable {
    private static final long serialVersionUID = 1302220490733580630L;
    @ApiModelProperty(value = "单词")
    private String word;
    @ApiModelProperty(value = "单词Id")
    private Long wordId;
    @ApiModelProperty(value = "翻译")
    private List<String> translation;
    @ApiModelProperty(value = "类型")
    private List<String> type;
    @ApiModelProperty(value = "英式发音")
    private String pronounceEnglish;
    @ApiModelProperty(value = "美式发音")
    private String pronounceAmerica;
    @ApiModelProperty(value = "错误原因")
    private String errorCause;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "最近错误时间")
    private LocalDateTime lastErrorTime;
    @ApiModelProperty(value = "正确次数")
    private Integer correctCount;
    @ApiModelProperty(value = "错误次数")
    private Integer errorCount;

}
