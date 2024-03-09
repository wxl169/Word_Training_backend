package org.wxl.wordTraining.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class CommentDeleteRequest implements Serializable {
    private static final long serialVersionUID = -5552969364281290234L;
    private Long commentId;
    private Long userId;
}
