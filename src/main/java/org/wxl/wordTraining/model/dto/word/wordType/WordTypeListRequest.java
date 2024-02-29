package org.wxl.wordTraining.model.dto.word.wordType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 单词类型列表搜索请求参数
 * @author wxl
 */
@Data
public class WordTypeListRequest implements Serializable {
    private static final long serialVersionUID = -6911602613307976274L;
    @ApiModelProperty(value = "页码")
    private Integer current;
    @ApiModelProperty(value = "每页数据量")
    private Integer pageSize;


    @ApiModelProperty(value = "类型名")
    private String typeName;
}
