package org.wxl.wordTraining.model.dto.word;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author 16956
 */
@Data
public class WordAddRequest implements Serializable {
    private static final long serialVersionUID = -4532168042284754690L;

    @ExcelProperty(value = "单词",index = 0)
    private String word;
    @ExcelProperty(value = "翻译",index = 1)
    private String translation;
    @ExcelProperty(value = "类型",index = 2)
    private String type;
    @ExcelProperty(value = "图片",index = 3)
    private String image;
    @ExcelProperty(value = "例句",index = 4)
    private String example;
    @ExcelProperty(value = "英式发音",index = 5)
    private String pronounceEnglish;
    @ExcelProperty(value = "美式发音",index = 6)
    private String pronounceAmerica;
    @ExcelProperty(value = "同义词",index = 7)
    private String synonym;
    @ExcelProperty(value = "反义词",index = 8)
    private String antonym;
    @ExcelProperty(value = "时态复数",index = 9)
    private String exchange;
}
