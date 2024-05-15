package org.wxl.wordTraining.model.vo.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class ArticleOtherVO implements Serializable {
    private static final long serialVersionUID = 4576878194406946547L;
    @ApiModelProperty(value = "推荐文章")
    private List<ArticleVO> recommendArticleList;
    @ApiModelProperty(value = "该作者其他文章")
    private List<ArticleVO> otherArticleList;

}
