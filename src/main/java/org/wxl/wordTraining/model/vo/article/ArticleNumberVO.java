package org.wxl.wordTraining.model.vo.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.wxl.wordTraining.model.vo.PageVO;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author wxl
 */
@Data
public class ArticleNumberVO implements Serializable {
    private static final long serialVersionUID = 6667422393246812745L;
    @ApiModelProperty("全部数量")
    private Integer allNumber;
    @ApiModelProperty("全部可见的文章数量")
    private Integer openNumber;
    @ApiModelProperty("仅自己可见的文章数量")
    private Integer privateNumber;
    @ApiModelProperty("仅关注自己的用户可见的文章数量")
    private Integer  concernNumber;
    @ApiModelProperty("审核中的文章数量")
    private Integer  processNumber;
    @ApiModelProperty("草稿数量")
    private Integer draftNumber;
    @ApiModelProperty("需整改的文章数量")
    private Integer rectificationNumber;
    @ApiModelProperty("已被封禁的文章数量")
    private Integer banNumber;
    @ApiModelProperty("当前用户最早发布文章的时间")
    private Set<Integer> timeSet;
    @ApiModelProperty("文章的标签列表")
    private Set<String> tagSet;

    @ApiModelProperty("文章数据")
    private PageVO pageVO;
}
