package org.wxl.wordTraining.model.vo.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author WXL
 */
@Data
public class CommentListVO implements Serializable {
    private static final long serialVersionUID = 1806346028078000583L;

    @ApiModelProperty(value = "评论id")
    private Long id;
    @ApiModelProperty(value = "评论用户id")
    private Long userId;
    @ApiModelProperty(value = "评论用户名")
    private String username;
    @ApiModelProperty(value = "评论用户头像")
    private String avatar;
    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "评论时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "父评论id")
    private Long parentId;

    @ApiModelProperty(value = "顶层评论id")
    private Long topId;

    @ApiModelProperty(value = "是否置顶(0:不置顶，1：置顶)")
    private Integer isSticky;
    @ApiModelProperty(value = "点赞数")
    private Long praiseNumber;
    @ApiModelProperty("子评论")
    private List<CommentListVO> commentChildList;
    @ApiModelProperty("是否显示回复框")
    private boolean showForm;
}
