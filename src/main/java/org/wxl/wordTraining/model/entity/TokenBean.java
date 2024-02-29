package org.wxl.wordTraining.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Token实体类
 * @author wxl
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TokenBean {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户类型
     */
    private String userRole;
}