package org.wxl.wordTraining.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wxl
 */
@Data
public class UserUpdateByUserRequest implements Serializable {
    private static final long serialVersionUID = 7750372188363245737L;

    private Long id;
    private String username;
    private String birthday;
    private Integer gender;
    private String phone;
    private String email;
    private String oldPassword;
    private String newPassword;
    private String sureNewPassword;

}
