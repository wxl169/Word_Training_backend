package org.wxl.wordTraining.model.vo.user;

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
public class UserListVO implements Serializable {

    private static final long serialVersionUID = 2298022024022478698L;
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "昵称")
    private String username;

    @ApiModelProperty(value = "生日")
    private LocalDateTime birthday;

    @ApiModelProperty(value = "性别(0:男，1：女)")
    private Integer gender;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "角色（user:普通用户，admin：管理员，ban：封号）")
    private String role;

    @ApiModelProperty(value = "积分数")
    private Long pointNumber;

    @ApiModelProperty(value = "连续在线天数")
    private Long coiledDay;

    @ApiModelProperty(value = "在线天数")
    private Long onlineDay;

    @ApiModelProperty(value = "最近在线时间")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
