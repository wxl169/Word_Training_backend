package org.wxl.wordTraining.model.vo.tag;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class TagVO implements Serializable {
    private static final long serialVersionUID = -2938850867437803983L;
    private Long id;
    private String tagName;
}
