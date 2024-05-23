package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.wordAnswer.WordAnswerListRequest;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.entity.WordAnswer;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.vo.PageVO;

import javax.servlet.http.HttpServletRequest;
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
    Boolean saveWordAnswer(Map<String, Object> wordAnswerMap, String userAccount,Integer difficulty);

    /**
     * 根据条件获取单词答题记录
     * @param wordAnswerListRequest 搜索条件
     * @param loginUser 获取当前登录用户信息
     * @return 单词答题记录
     */
    PageVO getWordAnswerList(WordAnswerListRequest wordAnswerListRequest, User loginUser);
}
