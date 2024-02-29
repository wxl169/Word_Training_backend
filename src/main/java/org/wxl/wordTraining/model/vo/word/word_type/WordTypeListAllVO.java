package org.wxl.wordTraining.model.vo.word.word_type;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @author 16956
 */
@Data
public class WordTypeListAllVO implements Serializable {
    private static final long serialVersionUID = -7748651112816586855L;
    @ApiModelProperty("类型组名id")
    private Long id;
    @ApiModelProperty("类型组名")
    private String typeName;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("修改时间")
    private LocalDateTime updateTime;
    @ApiModelProperty("类型组名的子类型名")
    private List<WordTypeListAllVO> childerWordTypeList;
}
