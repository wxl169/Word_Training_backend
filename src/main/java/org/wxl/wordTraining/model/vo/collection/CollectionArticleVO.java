package org.wxl.wordTraining.model.vo.collection;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class CollectionArticleVO implements Serializable {
    private static final long serialVersionUID = 6082602856058462223L;

    private Long collectionId;
    private String title;
    private Integer isCollection;
}
