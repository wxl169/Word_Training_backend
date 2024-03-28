package org.wxl.wordTraining.model.vo.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 已登录用户视图（脱敏）
 *
 * @author wxl
 *
 */
@Data
@ApiModel(value="当前登录用户对象")
public class LoginUserVO implements Serializable {

    private static final long serialVersionUID = 6459865455709642105L;
    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "昵称")
    private String username;

    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "生日")
    private LocalDateTime birthday;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty(value = "性别(0:男，1：女)")
    private Integer gender;
    @ApiModelProperty(value = "角色（user:普通用户，admin：管理员，ban：封号）")
    private String role;

    @ApiModelProperty(value = "积分数")
    private Long pointNumber;
    @ApiModelProperty("每日挑战次数")
    private Integer challengeNum;

    @ApiModelProperty(value = "连续在线天数")
    private Integer coiledDay;

    @ApiModelProperty(value = "在线天数")
    private Integer onlineDay;
    @ApiModelProperty(value = "最近在线时间")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty(value = "token")
    private String token;

}