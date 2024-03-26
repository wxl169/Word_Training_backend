package org.wxl.wordTraining.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户发布的评论的权限
 *
 * @author wxl
 */
public enum CommentWriteStatusEnum {
    MY_PUBLISH("我发表的评论", 0),
    MY_REPLY("我回复的评论", 1),
    REPLY_TO_ME("回复我的评论", 2),
    BAN("被封禁的评论", 3);

    private final String text;

    private final Integer value;

    CommentWriteStatusEnum(String text, Integer value) {
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
    public static CommentWriteStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (CommentWriteStatusEnum anEnum : CommentWriteStatusEnum.values()) {
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
