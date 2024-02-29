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
 * @since 2024-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_word")
@ApiModel(value="Word对象", description="")
public class Word implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "单词id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "单词")
    private String word;

    @ApiModelProperty(value = "翻译")
    private String translation;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "图片")
    private String image;

    @ApiModelProperty(value = "例句")
    private String example;

    @ApiModelProperty(value = "英式发音")
    private String pronounceEnglish;

    @ApiModelProperty(value = "美式发音")
    private String pronounceAmerica;

    @ApiModelProperty(value = "同义词")
    private String synonym;

    @ApiModelProperty(value = "反义词")
    private String antonym;

    @ApiModelProperty(value = "时态复数变化，使用 /分割不同项目")
    private String exchange;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除（0：未删除，1：已删除）")
    @TableLogic
    private Boolean isDelete;


}
