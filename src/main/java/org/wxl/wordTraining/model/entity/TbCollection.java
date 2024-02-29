package org.wxl.wordTraining.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
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
 * @since 2024-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_collection")
@ApiModel(value="Collection对象", description="")
public class TbCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "收藏id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "被收藏对象id")
    private Long collectionId;

    @ApiModelProperty(value = "收藏对象类型（0：文章，1：单词）")
    private Integer type;

    @ApiModelProperty(value = "收藏用户id")
    private Long userId;

    @ApiModelProperty(value = "收藏时间")
    private LocalDateTime collectionTime;

    @ApiModelProperty(value = "逻辑删除（0：未删除，1：已删除）")
    @TableLogic
    private Integer isDelete;


}
