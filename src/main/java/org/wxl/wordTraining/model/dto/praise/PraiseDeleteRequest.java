package org.wxl.wordTraining.model.dto.praise;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class PraiseDeleteRequest implements Serializable {
    private static final long serialVersionUID = -8969767332469487056L;
    private Long id;
    private Integer type;
}
