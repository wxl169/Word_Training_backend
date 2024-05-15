package org.wxl.wordTraining.model.vo.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class UserPointRankVO implements Serializable {
    private static final long serialVersionUID = -2690995212214319417L;
    private String username;
    private Long pointNumber;
    private String avatar;
}
