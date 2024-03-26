package org.wxl.wordTraining.model.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.sql.Blob;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wxl
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_comments")
@ApiModel(value="Comments对象", description="")
public class Comments implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评论id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "评论用户id")
    private Long userId;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "评论时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "父评论id")
    private Long parentId;

    @ApiModelProperty(value = "顶层评论id")
    private Long topId;

    @ApiModelProperty(value = "被评论文章id")
    private Long articleId;

    @ApiModelProperty(value = "是否为顶层评论（0：是，1：不是）")
    private Integer isTop;

    @ApiModelProperty(value = "是否置顶(0:不置顶，1：置顶)")
    private Integer isSticky;
    @ApiModelProperty(value = "是否显示(0:显示，1：不显示)")
    private Integer isShow;
    @ApiModelProperty(value = "是否违禁(0:无，1：有)")
    private Integer isComplain;
    @ApiModelProperty(value = "点赞数")
    private Integer praiseNumber;

    @ApiModelProperty(value = "审核意见")
    private String reviewOpinions;

    @ApiModelProperty(value = "逻辑删除（0：未删除，1：已删除）")
    @TableLogic
    private Integer isDelete;


}
