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
}
