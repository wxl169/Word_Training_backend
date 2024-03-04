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
public class ArticleAllVO implements Serializable {
    private static final long serialVersionUID = 3492324130829559173L;
    @ApiModelProperty(value = "文章id")
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "发布人id")
    private Long userId;

    @ApiModelProperty(value = "发布人用户名")
    private String username;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "浏览量")
    private Long visitNumber;

    @ApiModelProperty(value = "点赞数")
    private Long praiseNumber;

    @ApiModelProperty(value = "评论数")
    private Long commentNumber;

    @ApiModelProperty(value = "收藏数")
    private Long collectionNumber;

    @ApiModelProperty(value = "标签")
    private String tags;

    @ApiModelProperty(value = "封面图")
    private String coverImage;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime createTime;


}
