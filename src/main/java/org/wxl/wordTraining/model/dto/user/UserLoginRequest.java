package org.wxl.wordTraining.model.dto.user;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录实体类
 * @author wxl
 */
@Data
@ApiModel(value="用户注册实体类")
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 6990202287391774026L;
    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "密码")
    private String userPassword;
}
