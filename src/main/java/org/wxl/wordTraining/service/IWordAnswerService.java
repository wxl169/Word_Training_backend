package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.entity.WordAnswer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 */
public interface IWordAnswerService extends IService<WordAnswer> {

    /**
     * 保存用户答题记录
     * @param wordAnswerMap 答题记录
     * @param userAccount 用户账号
     * @param difficulty 难度等级
     * @return 是否保存成功
     */
    Boolean saveWordAnswer(Map<String, String> wordAnswerMap, String userAccount,Integer difficulty);

}
