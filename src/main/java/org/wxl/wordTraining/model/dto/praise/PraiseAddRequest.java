package org.wxl.wordTraining.model.dto.praise;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class PraiseAddRequest implements Serializable {
    private static final long serialVersionUID = -6101257621714703651L;

    private Long id;
    private Integer type;
}
