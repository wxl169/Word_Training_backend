package org.wxl.wordTraining.model.dto.word;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class WordUpdateRequest implements Serializable {
    private static final long serialVersionUID = -3834316348461287584L;
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "单词")
    private String word;

    @ApiModelProperty(value = "翻译")
    private String translation;

    @ApiModelProperty(value = "类型")
    private List<String> type;

    @ApiModelProperty(value = "图片")
    private String image;

    @ApiModelProperty(value = "例句")
    private String example;

    @ApiModelProperty(value = "英式发音")
    private String pronounceEnglish;

    @ApiModelProperty(value = "美式发音")
    private String pronounceAmerica;

    @ApiModelProperty(value = "同义词")
    private String synonym;

    @ApiModelProperty(value = "反义词")
    private String antonym;

    @ApiModelProperty(value = "时态复数变化，使用 /分割不同项目")
    private String exchange;


}
