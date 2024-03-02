package org.wxl.wordTraining.model.dto.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class ArticleAddRequest implements Serializable {
    private static final long serialVersionUID = 8314397886653001813L;
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

}
