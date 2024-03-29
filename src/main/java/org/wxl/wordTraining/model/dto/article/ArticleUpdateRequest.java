package org.wxl.wordTraining.model.dto.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class ArticleUpdateRequest implements Serializable {
    private static final long serialVersionUID = -371204412455009529L;
    @ApiModelProperty(value = "文章id")
    private Long articleId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "标签")
    private List<String> tags;

    @ApiModelProperty(value = "封面图")
    private String coverImage;

    @ApiModelProperty(value = "权限（0：公开，1：私有，2：仅关注自己的用户，3：仅自己关注的用户）")
    private Integer permissions;

    @ApiModelProperty(value = "状态(3:草稿)")
    private Integer status;

}
