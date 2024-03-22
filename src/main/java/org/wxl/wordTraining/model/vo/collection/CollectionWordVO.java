package org.wxl.wordTraining.model.vo.collection;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author wxl
 */
@Data
public class CollectionWordVO implements Serializable {
    private static final long serialVersionUID = 5905761484873923253L;
    private Long collectionId;
    private Integer isCollection;
    private String word;
    private String translationJson;
    private String pronounceEnglish;
    private String pronounceAmerica;
    private List<String> translation;
}
