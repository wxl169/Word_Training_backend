package org.wxl.wordTraining.model.vo.word.word_type;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class WordTypeListVO implements Serializable {
    private static final long serialVersionUID = 5157919358832182371L;
    @ApiModelProperty("类型组名id")
    private Long id;
    @ApiModelProperty("类型组名")
    private String typeName;
    @ApiModelProperty("类型组名的子类型名")
    private List<WordTypeListVO> childerWordTypeList;

}
