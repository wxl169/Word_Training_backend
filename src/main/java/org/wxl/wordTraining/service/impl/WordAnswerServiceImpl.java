package org.wxl.wordTraining.service.impl;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.WordTrainingConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.UserMapper;
import org.wxl.wordTraining.model.entity.User;
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
    private final RedisTemplate redisTemplate;
    @Autowired
    public WordAnswerServiceImpl(UserMapper userMapper,RedisTemplate redisTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }
    /**
     * 保存用户答题记录
     * @param wordAnswerMap 答题记录
     * @param userAccount 用户账号
     * @param difficulty 难度等级
     * @return 是否保存成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
        int pointAll =  0;
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
                if (difficulty.equals(WordTrainingConstant.CHALLENGE)){
                    //用户的积分总数
                    pointAll++;
                }
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
        if (difficulty.equals(WordTrainingConstant.CHALLENGE)) {
            //更新用户的积分总数
            boolean updatePointAll = userMapper.updatePointAll(userId, pointAll);
            if (!updatePointAll) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新用户积分失败");
            }
            String redisKey = "wordTraining:user:pointsRanking";
             //如果存在，则更新redis中的数据
            Double score = redisTemplate.opsForZSet().score(redisKey, userId);
            if (score == null) {
                User user = userMapper.selectById(userId);
                redisTemplate.opsForZSet().add(redisKey, userId, user.getPointNumber());
            }else{
                redisTemplate.opsForZSet().incrementScore(redisKey, userId, pointAll);
            }

        }

        return true;
    }
}
