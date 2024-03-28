package org.wxl.wordTraining.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author wxl
 * @since 2024-01-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user")
@ApiModel(value="User对象", description="用户表")
public class User implements Serializable {

    private static final long serialVersionUID = -1199773712390309699L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "密码")
    private String userPassword;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

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

    @ApiModelProperty(value = "关注人")
    private String concern;

    @ApiModelProperty("每日挑战次数默认为5")
    private Integer challengeNum;

    @ApiModelProperty(value = "连续在线天数")
    private Integer coiledDay;

    @ApiModelProperty(value = "在线天数")
    private Integer onlineDay;

    @ApiModelProperty(value = "最近在线时间")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除（0：未删除，1：已删除）")
    @TableLogic
    private Integer isDelete;


}
