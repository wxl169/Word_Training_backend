package org.wxl.wordTraining.model.dto.collection;


import lombok.Data;

import java.io.Serializable;

/**
 * 新增收藏物
 * @author wxl
 */
@Data
public class CollectionRequest implements Serializable {
    private static final long serialVersionUID = 5135125332886103673L;

    private Long id;
    private Integer type;
}
