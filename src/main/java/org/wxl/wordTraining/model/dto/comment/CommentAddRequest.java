package org.wxl.wordTraining.model.dto.comment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDateTime;

/**
 * @author wxl
 */
@Data
public class CommentAddRequest implements Serializable {
    private static final long serialVersionUID = -902682528866174145L;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "父评论id")
    private Long parentId;

    @ApiModelProperty(value = "顶层评论id")
    private Long topId;

    @ApiModelProperty(value = "被评论文章id")
    private Long articleId;

    @ApiModelProperty(value = "是否为顶层评论（0：是，1：不是）")
    private Integer isTop;

}
