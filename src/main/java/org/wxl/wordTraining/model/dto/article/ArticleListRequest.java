package org.wxl.wordTraining.model.dto.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wxl
 */
@Data
public class ArticleListRequest implements Serializable {
    private static final long serialVersionUID = 3650464652040060666L;

    @ApiModelProperty(value = "用户账号")
    private String userAccount;
    @ApiModelProperty(value = "搜索内容（匹配标题、描述、内容）")
    private String content;
    @ApiModelProperty(value = "标签")
    private List<String> tags;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "权限")
    private Integer permissions;
    @ApiModelProperty(value = "当前页码")
    private Integer current;
    @ApiModelProperty(value = "每页数据量")
    private Integer pageSize;

}
