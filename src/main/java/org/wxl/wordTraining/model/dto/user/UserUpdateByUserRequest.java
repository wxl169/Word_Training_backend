package org.wxl.wordTraining.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 16956
 */
@Data
public class UserUpdateByUserRequest implements Serializable {
    private static final long serialVersionUID = 7750372188363245737L;

    private Long id;
    private String username;
    private String birthday;
    private Integer gender;

}
