package org.wxl.wordTraining.model.vo.tag;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wxl
 */
@Data
public class TagListVO implements Serializable {
    private static final long serialVersionUID = -6415011117817500287L;

    private Long id;
    private String tagName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
