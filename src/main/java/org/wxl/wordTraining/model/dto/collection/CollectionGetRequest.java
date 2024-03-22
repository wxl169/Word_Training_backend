package org.wxl.wordTraining.model.dto.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionGetRequest implements Serializable {
    private static final long serialVersionUID = 7177300331765805573L;
    private Integer type;
    private Integer pageSize;
    private Integer current;
}
