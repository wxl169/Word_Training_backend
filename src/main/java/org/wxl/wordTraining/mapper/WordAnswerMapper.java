package org.wxl.wordTraining.mapper;

import io.lettuce.core.dynamic.annotation.Param;
import org.wxl.wordTraining.model.entity.WordAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wxl.wordTraining.model.vo.wordAnswer.WordAnswerVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxl
 */
public interface WordAnswerMapper extends BaseMapper<WordAnswer> {

    /**
     * 根据单词id查询单词记录
     * @param wordId 单词id
     * @param userId 用户id
     * @return WordAnswerVO
     */
    WordAnswerVO selectWordAnswerById(@Param("wordId") Long wordId, @Param("userId") Long userId);

    /**
     * 统计错误和正确个数
     * @param wordId 单词Id
     * @param userId 用户Id
     * @return WordAnswerVO
     */
    WordAnswerVO selectNumber(@Param("wordId") Long wordId, @Param("userId") Long userId);
    /**
     * 根据单词id查询单词记录
     * @param wordId 单词id
     * @param userId 用户id
     * @return WordAnswerVO
     */
    WordAnswerVO selectWordAnswerByIdEver(@Param("wordId") Long wordId, @Param("userId") Long userId);
}
