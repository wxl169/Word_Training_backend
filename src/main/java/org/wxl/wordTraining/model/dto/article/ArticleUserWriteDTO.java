package org.wxl.wordTraining.model.dto.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class ArticleUserWriteDTO implements Serializable {
    private static final long serialVersionUID = 4539137910520140982L;
    @ApiModelProperty("文章的状态或权限")
    private Integer status;
    @ApiModelProperty("年份")
    private Integer year;
    @ApiModelProperty("月份")
    private Integer month;
    @ApiModelProperty("关键字")
    private String content;
    @ApiModelProperty("标签")
    private List<String> tagName;
    @ApiModelProperty("页码")
    private Integer current;

}
