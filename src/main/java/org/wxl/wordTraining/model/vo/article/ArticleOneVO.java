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
public class ArticleOneVO implements Serializable {
    private static final long serialVersionUID = 1681828710591121522L;
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
    private Integer praiseNumber;
    @ApiModelProperty("评论数")
    private Integer commentNumber;
    @ApiModelProperty("收藏量")
    private Integer collectionNumber;
    @ApiModelProperty("标签")
    private String tags;
    @ApiModelProperty("标签")
    private List<String> tagList;
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
    @ApiModelProperty("是否关注")
    private Integer isAddUser;

}
