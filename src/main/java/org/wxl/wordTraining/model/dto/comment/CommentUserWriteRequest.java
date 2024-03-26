package org.wxl.wordTraining.model.dto.comment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxl
 */
@Data
public class CommentUserWriteRequest implements Serializable {
    private static final long serialVersionUID = -1260375167202677693L;

    @ApiModelProperty("权限：0：我发表的评论，1：我回复的评论，2：回复我的评论，3：被封禁的评论")
    private Integer status;

    @ApiModelProperty("年份")
    private Integer year;

    @ApiModelProperty("月份")
    private Integer month;

    @ApiModelProperty("关键字")
    private String content;

    @ApiModelProperty("页数")
    private Integer current;
}
