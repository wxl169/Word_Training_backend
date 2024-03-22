package org.wxl.wordTraining.model.vo.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wxl
 */
@Data
public class ArticleVO implements Serializable {
    private static final long serialVersionUID = 7988988861069724434L;
    @ApiModelProperty(value = "文章id")
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "发布人id")
    private Long userId;

    @ApiModelProperty(value = "发布人账号")
    private String userAccount;
    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "浏览量")
    private Long visitNumber;

    @ApiModelProperty(value = "点赞数")
    private Integer praiseNumber;

    @ApiModelProperty(value = "评论数")
    private Integer commentNumber;

    @ApiModelProperty(value = "收藏数")
    private Integer collectionNumber;

    @ApiModelProperty(value = "标签")
    private String tags;

    @ApiModelProperty(value = "封面图")
    private String coverImage;

    @ApiModelProperty(value = "状态（0：正常发布，1：整改中，2：已封禁）")
    private Integer status;

    @ApiModelProperty(value = "权限（0：公开，1：私有，2：仅关注自己的用户，3：仅自己关注的用户）")
    private Integer permissions;

    @ApiModelProperty(value = "审核意见")
    private String reviewOpinions;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;
}
