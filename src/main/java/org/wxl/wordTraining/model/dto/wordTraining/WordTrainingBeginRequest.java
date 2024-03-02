package org.wxl.wordTraining.model.dto.wordTraining;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class WordTrainingBeginRequest implements Serializable {
    private static final long serialVersionUID = 4402061946570687421L;

    @ApiModelProperty("模式（0：英语选义，1：中文选义，2：填空拼写）")
    private Integer mode;
    @ApiModelProperty("难度（0：训练模式，1：挑战模式）")
    private Integer difficulty;
    @ApiModelProperty("单词类型")
    private List<String> wordTypeList;

}
