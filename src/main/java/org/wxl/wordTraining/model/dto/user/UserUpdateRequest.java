package org.wxl.wordTraining.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员修改用户的信息
 * @author wxl
 */

@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = -1278759211807356791L;

    private Long id;
    private String role;
}
