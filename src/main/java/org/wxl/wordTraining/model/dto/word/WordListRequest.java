package org.wxl.wordTraining.model.dto.word;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class WordListRequest implements Serializable {
    private static final long serialVersionUID = 3268951097627643262L;

    @ApiModelProperty(value = "页码")
    private Integer current;
    @ApiModelProperty(value = "每页数据量")
    private Integer pageSize;
    @ApiModelProperty(value = "单词")
    private String word;
    @ApiModelProperty(value = "翻译")
    private String translation;
    @ApiModelProperty(value =  "单词类型")
    private List<String> type;

}
