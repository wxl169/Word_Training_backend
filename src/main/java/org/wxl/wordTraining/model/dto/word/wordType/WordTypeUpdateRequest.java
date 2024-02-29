package org.wxl.wordTraining.model.dto.word.wordType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 单词类型需要修改的数据
 * @author wxl
 */
@Data
public class WordTypeUpdateRequest implements Serializable {
    private static final long serialVersionUID = -9198203725182355105L;
    private Long id;
    private String typeName;

    @ApiModelProperty(value = "是否为类型组名（0：不是，1:是）")
    private Integer isGroupName;

    @ApiModelProperty(value = "属于类型组id")
    private Long typeGroupId;



}
