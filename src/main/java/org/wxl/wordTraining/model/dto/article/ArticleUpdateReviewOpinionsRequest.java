package org.wxl.wordTraining.model.dto.article;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class ArticleUpdateReviewOpinionsRequest implements Serializable {
    private static final long serialVersionUID = -107244533975556889L;

    private Long id;
    private String reviewOpinions;
}
