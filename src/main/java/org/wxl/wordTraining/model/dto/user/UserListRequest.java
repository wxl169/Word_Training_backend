package org.wxl.wordTraining.model.dto.user;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 获取用户数据列表
 * @author wxl
 */
@Data
@ApiModel(value="用户数据列表")
public class UserListRequest implements Serializable {


    private static final long serialVersionUID = 5138277277483509978L;
    @ApiModelProperty(value = "页码")
    private Integer current;
    @ApiModelProperty(value = "每页数据量")
    private Integer pageSize;


    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "昵称")
    private String username;

    @ApiModelProperty(value = "性别(0:男，1：女)")
    private Integer gender;

    @ApiModelProperty(value = "角色（user:普通用户，admin：管理员，ban：封号）")
    private String role;

}
