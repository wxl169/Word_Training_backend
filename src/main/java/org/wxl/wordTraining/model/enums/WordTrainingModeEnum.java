package org.wxl.wordTraining.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 单词训练模式枚举类
 * @author wxl
 */

public enum WordTrainingModeEnum {
    ENGLISH_SELECTIONS("englishSelections", 0),
    CHINESE_SELECTIONS("chineseSelections", 1),
    WORD_SPELL("wordSpell", 2);

    private final String text;

    private final Integer value;

    WordTrainingModeEnum(String text, Integer value) {
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
    public static WordTrainingModeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (WordTrainingModeEnum anEnum : WordTrainingModeEnum.values()) {
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
