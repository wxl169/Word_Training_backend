package org.wxl.wordTraining.mapper;

import org.wxl.wordTraining.model.entity.Word;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxl
 */
public interface WordMapper extends BaseMapper<Word> {


    /**
     * 随机获取50个单词数据
     * @param wordTypeList 单词类型
     * @return 随机获取50个单词数据
     */
    List<Word> getWordTrainingList(List<String> wordTypeList);

    /**
     * 获取单词总数
     * @param typeName 单词类型
     * @return 单词数量
     */
    int selectWordByWordTypeCount(String typeName);

    /**
     * 获取所有含有该单词类型的单词数据
     * @param oldTypeName 单词类型
     * @return 单词类型列表
     */
    List<Word> selectWordByWordType(String oldTypeName);
}
