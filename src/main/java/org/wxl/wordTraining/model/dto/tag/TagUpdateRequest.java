package org.wxl.wordTraining.model.dto.tag;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class TagUpdateRequest implements Serializable {
    private static final long serialVersionUID = -7993473870553622742L;
    private Long id;
    private String tagName;
}
