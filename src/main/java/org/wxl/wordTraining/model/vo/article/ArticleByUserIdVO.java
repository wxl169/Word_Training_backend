package org.wxl.wordTraining.model.vo.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class ArticleByUserIdVO implements Serializable {
    private static final long serialVersionUID = 2848030024565734725L;

    @ApiModelProperty(value = "文章id")
    private Long id;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "内容")
    private String content;
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

    @ApiModelProperty(value = "标签集合")
    private List<String> tagList;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "权限")
    private Integer permissions;
    @ApiModelProperty(value = "封面图")
    private String coverImage;

    @ApiModelProperty(value = "审核意见")
    private String reviewOpinions;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}
