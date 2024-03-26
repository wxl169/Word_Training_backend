package org.wxl.wordTraining.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户发布的文章的状态或权限
 *
 * @author wxl
 */

public enum ArticleWriteStatusEnum {
    ALL_ARTICLE("全部", 0),
    ALL_SHOW("全部可见", 1),
    PRIVATE_SHOW("仅自己可见", 2),
    PRIVATE_CONCERN_SHOW("仅关注自己的用户可见",3),
    DRAFT_ARTICLE("草稿",4),
    UNDER_REVIEW("审核中",5),
    RECTIFICATION_REQUIRED("需整改",6),
    BAN_ARTICLE("已被封禁",7);

    private final String text;

    private final Integer value;

    ArticleWriteStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ArticleWriteStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ArticleWriteStatusEnum anEnum : ArticleWriteStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
