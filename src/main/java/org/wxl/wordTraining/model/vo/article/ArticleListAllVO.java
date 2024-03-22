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
public class ArticleListAllVO implements Serializable {
    private static final long serialVersionUID = -5536645549229353263L;
    @ApiModelProperty(value = "文章id")
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "发布人id")
    private Long userId;

    @ApiModelProperty(value = "勋章名")
    private String achievementName;
    @ApiModelProperty(value = "勋章图表")
    private String achievementLogo;

    @ApiModelProperty(value = "发布人用户名")
    private String username;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "浏览量")
    private Long visitNumber;

    @ApiModelProperty(value = "点赞数")
    private Integer praiseNumber;
    @ApiModelProperty(value = "是否点赞 0:未点赞，1：已点赞")
    private Integer isPraise;

    @ApiModelProperty(value = "收藏数")
    private Integer collectionNumber;

    @ApiModelProperty(value = "是否收藏 0:未收藏，1：已收藏")
    private Integer isCollection;

    @ApiModelProperty(value = "评论数")
    private Integer commentNumber;

    @ApiModelProperty(value = "标签")
    private List<String> tags;

    @ApiModelProperty(value = "封面图")
    private String coverImage;
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime createTime;
}
