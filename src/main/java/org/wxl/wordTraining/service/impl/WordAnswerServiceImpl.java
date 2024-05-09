package org.wxl.wordTraining.service.impl;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.WordTrainingConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.UserMapper;
import org.wxl.wordTraining.model.entity.WordAnswer;
import org.wxl.wordTraining.mapper.WordAnswerMapper;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingVO;
import org.wxl.wordTraining.service.IWordAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.service.UserService;

import javax.xml.ws.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class WordAnswerServiceImpl extends ServiceImpl<WordAnswerMapper, WordAnswer> implements IWordAnswerService {

    private final UserMapper userMapper;
    @Autowired
    public WordAnswerServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    /**
     * 保存用户答题记录
     * @param wordAnswerMap 答题记录
     * @param userAccount 用户账号
     * @param difficulty 难度等级
     * @return 是否保存成功
     */
    @Override
    public Boolean saveWordAnswer(Map<String, Object> wordAnswerMap, String userAccount,Integer difficulty) {
        if (wordAnswerMap == null || wordAnswerMap.isEmpty()){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"题库数据不能为空");
        }
        if (StringUtils.isBlank(userAccount)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"用户账号不能为空");
        }
        Gson gson = new Gson();
        Long points = 0L;
        if (difficulty == 1L){
            points = 1L;
        }
        //查询用户id
        Long userId = userMapper.selectByUserAccount(userAccount);
        List<WordAnswer> wordAnswerList = new ArrayList<>();
        for (Map.Entry<String, Object> wordAnswer : wordAnswerMap.entrySet()) {
            WordTrainingVO wordTrainingVO = gson.fromJson((String) wordAnswer.getValue(), WordTrainingVO.class);
            if (wordTrainingVO.getIsTrue().equals(0)){
                continue;
            }
            WordAnswer answer = new WordAnswer();
            answer.setUserId(userId);
            answer.setWordId(wordTrainingVO.getWordId());
            answer.setPoints(points);
            if (wordTrainingVO.getIsTrue().equals(1)) {
                answer.setIsTrue(0);
            }else if(wordTrainingVO.getIsTrue().equals(2)){
                answer.setIsTrue(1);
            }
            answer.setErrorCause(wordTrainingVO.getErrorCause());
            answer.setIsShow(0);
            answer.setStatus(0);
            answer.setIsDelete(0);
            wordAnswerList.add(answer);
        }
        boolean b = this.saveBatch(wordAnswerList);
        if (!b){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"保存答题记录失败");
        }
        return true;
    }
}
