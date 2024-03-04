package org.wxl.wordTraining.model.dto.article;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author wxl
 */
@Data
public class ArticleAllRequest implements Serializable {
    private static final long serialVersionUID = 6019853772644680396L;
    private String content;
    private List<String> tagName;
    private Integer current;

}
