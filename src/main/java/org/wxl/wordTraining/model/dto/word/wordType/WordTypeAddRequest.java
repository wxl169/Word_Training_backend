package org.wxl.wordTraining.model.dto.word.wordType;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增单词类型
 * @author wxl
 */

@Data
public class WordTypeAddRequest implements Serializable {
    private static final long serialVersionUID = 9028448660403933103L;

    private String typeName;
    private Integer isGroupName;
    private Long id;

}
