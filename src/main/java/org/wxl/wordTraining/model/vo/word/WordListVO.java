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
public class WordListVO implements Serializable {
    private static final long serialVersionUID = -1694983412037021337L;


    @ApiModelProperty(value = "单词id")
    private Long id;

    @ApiModelProperty(value = "单词")
    private String word;

    @ApiModelProperty(value = "翻译")
    private List<String> translation;

    @ApiModelProperty(value = "类型")
    private List<String> type;

    @ApiModelProperty(value = "图片")
    private String image;

    @ApiModelProperty(value = "例句")
    private Map<String,String> example;

    @ApiModelProperty(value = "英式发音")
    private String pronounceEnglish;

    @ApiModelProperty(value = "美式发音")
    private String pronounceAmerica;

    @ApiModelProperty(value = "同义词")
    private List<String> synonym;

    @ApiModelProperty(value = "反义词")
    private List<String> antonym;

    @ApiModelProperty(value = "时态复数变化，使用 /分割不同项目")
    private Map<String,String> exchange;
}
