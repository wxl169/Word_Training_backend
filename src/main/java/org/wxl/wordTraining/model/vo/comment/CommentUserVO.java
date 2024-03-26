package org.wxl.wordTraining.model.vo.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author wxl
 */
@Data
public class CommentUserVO implements Serializable {
    private static final long serialVersionUID = -1857552014345925492L;

    @ApiModelProperty(value = "评论id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @ApiModelProperty(value = "评论用户id")
    private Long userId;
    @ApiModelProperty(value = "被评论用户id")
    private Long receiveUserId;
    @ApiModelProperty("被评论用户名")
    private String username;
    @ApiModelProperty(value = "回复的评论内容")
    private String content;
    @ApiModelProperty(value = "接受的评论内容")
    private String receiveContent;
    @ApiModelProperty(value = "评论时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "父评论id")
    private Long parentId;
    @ApiModelProperty(value = "被评论文章id")
    private Long articleId;
    @ApiModelProperty(value = "被评论文章作者id")
    private Long articleUserId;
    @ApiModelProperty(value = "被评论文章作者用户名")
    private String articleUsername;
    @ApiModelProperty(value = "被评论文章标题")
    private String title;
    @ApiModelProperty(value = "点赞数")
    private Integer praiseNumber;
    @ApiModelProperty(value = "审核意见")
    private String reviewOpinions;
    @ApiModelProperty("0：代表我是发送方，1：代表我是接收方")
    private Integer role;
    @ApiModelProperty("权限：0：我发表的评论，1：我回复的评论，2：回复我的评论，3：被封禁的评论")
    private Integer status;

    @ApiModelProperty("是否显示回复框")
    private boolean showForm;
}
