package org.wxl.wordTraining.model.vo.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wxl
 */
@Data
public class ArticleOneMapperVO implements Serializable {
    @ApiModelProperty("文章id")
    private Long id;
    @ApiModelProperty("文章标题")
    private String title;
    @ApiModelProperty("文章内容")
    private String content;
    @ApiModelProperty("发布人id")
    private Long userId;
    @ApiModelProperty("发布人姓名")
    private String username;
    @ApiModelProperty("文章描述")
    private String description;
    @ApiModelProperty("浏览量")
    private Long visitNumber;
    @ApiModelProperty("点赞数")
    private Long praiseNumber;
    @ApiModelProperty("评论数")
    private Long commentNumber;
    @ApiModelProperty("收藏量")
    private Long collectionNumber;
    @ApiModelProperty("标签")
    private String tags;
    @ApiModelProperty("封面")
    private String coverImage;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("用户头像")
    private String avatar;
    @ApiModelProperty("勋章名")
    private String achievementName;
    @ApiModelProperty("勋章logo")
    private String achievementLogo;
    @ApiModelProperty("是否点赞")
    private Integer isPraise;
    @ApiModelProperty("是否收藏")
    private Integer isCollection;
}
