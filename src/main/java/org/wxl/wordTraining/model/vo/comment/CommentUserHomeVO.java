package org.wxl.wordTraining.model.vo.comment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.wxl.wordTraining.model.vo.PageVO;

import java.io.Serializable;
import java.util.Set;

/**
 * @author wxl
 */
@Data

public class CommentUserHomeVO implements Serializable {
    private static final long serialVersionUID = 8999699005383217535L;
    @ApiModelProperty("当前用户最早发布文章的时间")
    private Set<Integer> timeSet;
    @ApiModelProperty("评论数据")
    private PageVO pageVO;

}
