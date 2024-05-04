package org.wxl.wordTraining.model.vo.word.word_type;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 单词类型列表返回参数
 * @author wxl
 */
@Data
public class WordTypeVO implements Serializable {
    private static final long serialVersionUID = 74787653946333091L;
    @ApiModelProperty(value = "单词类型id")
    private Long id;

    @ApiModelProperty(value = "类型名")
    private String typeName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
