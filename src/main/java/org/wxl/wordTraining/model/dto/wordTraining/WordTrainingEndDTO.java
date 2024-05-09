package org.wxl.wordTraining.model.dto.wordTraining;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class WordTrainingEndDTO implements Serializable {
    private static final long serialVersionUID = -336494354203365055L;
    @ApiModelProperty(value = "用户账号")
    private String temporaryUserAccount;
    @ApiModelProperty(value = "模式")
    private Integer mode;
    @ApiModelProperty(value = "难度")
    private Integer difficulty;

}
