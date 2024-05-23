package org.wxl.wordTraining.model.dto.wordAnswer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author wxl
 */
@Data
public class WordAnswerListRequest implements Serializable {
    private static final long serialVersionUID = 3728218872797984858L;
    @ApiModelProperty(value = "页码")
    private Integer current;
    @ApiModelProperty(value = "每页数据量")
    private Integer pageSize;
    @ApiModelProperty(value = "单词")
    private String word;
    @ApiModelProperty(value = "翻译")
    private String translation;
    @ApiModelProperty(value =  "单词类型")
    private Set<String> typeSet;
    @ApiModelProperty("开始时间")
    private String startTime;
    @ApiModelProperty("结束时间")
    private String endTime;

}
