package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.CommonConstant;
import org.wxl.wordTraining.constant.WordTrainingConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.UserMapper;
import org.wxl.wordTraining.mapper.WordMapper;
import org.wxl.wordTraining.model.dto.wordAnswer.WordAnswerListRequest;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.entity.Word;
import org.wxl.wordTraining.model.entity.WordAnswer;
import org.wxl.wordTraining.mapper.WordAnswerMapper;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.wordAnswer.WordAnswerVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingVO;
import org.wxl.wordTraining.service.IWordAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Action;
import java.util.*;
import java.util.stream.Collectors;

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
    private final WordMapper wordMapper;
    private final WordAnswerMapper wordAnswerMapper;
    @Autowired
    public WordAnswerServiceImpl(UserMapper userMapper,RedisTemplate redisTemplate,WordMapper wordMapper,WordAnswerMapper wordAnswerMapper) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.wordMapper = wordMapper;
        this.wordAnswerMapper = wordAnswerMapper;
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

    /**
     * 根据条件获取单词答题记录
     * @param wordAnswerListRequest 搜索条件
     * @param loginUser 获取当前登录用户信息
     * @return 单词答题记录
     */
    @Override
    public PageVO getWordAnswerList(WordAnswerListRequest wordAnswerListRequest, User loginUser) {
        Integer current = wordAnswerListRequest.getCurrent();
        Integer pageSize = wordAnswerListRequest.getPageSize();
        if (current == null || current <= 0){
            current = CommonConstant.PAGE_NUM;
        }
        if (pageSize == null || pageSize <= 0){
            pageSize = CommonConstant.PAGE_SIZE;
        }
        LambdaQueryWrapper<WordAnswer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(WordAnswer::getWordId)
                .eq(WordAnswer::getUserId,loginUser.getId());
        if (StringUtils.isNotBlank(wordAnswerListRequest.getStartTime()) && StringUtils.isNotBlank(wordAnswerListRequest.getEndTime())){
                //开始时间不能再结束时间之后
                if (wordAnswerListRequest.getStartTime().compareTo(wordAnswerListRequest.getEndTime()) > 0) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "开始时间不能大于结束时间");
                }
                queryWrapper.between(WordAnswer::getCreateTime,wordAnswerListRequest.getStartTime(),wordAnswerListRequest.getEndTime());
        } else if (StringUtils.isNotBlank(wordAnswerListRequest.getStartTime())) {
            queryWrapper.ge(WordAnswer::getCreateTime,wordAnswerListRequest.getStartTime());
        }else if (StringUtils.isNotBlank(wordAnswerListRequest.getEndTime())) {
            queryWrapper.le(WordAnswer::getCreateTime,wordAnswerListRequest.getEndTime());
        }

        Set<Long> wordIdSet = new HashSet<>();
        if (StringUtils.isNotBlank(wordAnswerListRequest.getWord())){
            wordIdSet = wordMapper.selectWordIdList(wordAnswerListRequest.getWord());
        }
        if (StringUtils.isNotBlank(wordAnswerListRequest.getTranslation())){
            wordIdSet.addAll(wordMapper.selectWordIdByTranslation(wordAnswerListRequest.getTranslation()));
        }
        if (wordAnswerListRequest.getTypeSet() != null && wordAnswerListRequest.getTypeSet().size() > 0){
            wordIdSet.addAll(wordMapper.selectWordIdByType(wordAnswerListRequest.getTypeSet()));
        }

        queryWrapper.eq(WordAnswer::getIsDelete,0);
        queryWrapper.eq(WordAnswer::getIsShow,0);
        if (wordIdSet != null && !wordIdSet.isEmpty()){
            queryWrapper.in(WordAnswer::getWordId,wordIdSet);
        }

        queryWrapper.groupBy(WordAnswer::getWordId);

        Page<WordAnswer> page = new Page<>(current, pageSize);
        page(page,queryWrapper);
        Gson gson = new Gson();
        //处理返回数据
        List<WordAnswerVO> collect = page.getRecords().stream().map(wordAnswer -> {
            WordAnswerVO wordAnswerVO = wordAnswerMapper.selectWordAnswerById(wordAnswer.getWordId(),loginUser.getId());
            if (wordAnswerVO == null){
                wordAnswerVO = wordAnswerMapper.selectWordAnswerByIdEver(wordAnswer.getWordId(),loginUser.getId());
            }
            WordAnswerVO wordAnswerNumber = wordAnswerMapper.selectNumber(wordAnswer.getWordId(),loginUser.getId());
            wordAnswerVO.setCorrectCount(wordAnswerNumber.getCorrectCount());
            wordAnswerVO.setErrorCount(wordAnswerNumber.getErrorCount());

            Word word = wordMapper.selectById(wordAnswer.getWordId());
            List<String> typeList = gson.fromJson(word.getType(), new TypeToken<List<String>>() {
            }.getType());
            wordAnswerVO.setType(typeList);
            wordAnswerVO.setWordId(word.getId());
            Map<String, String> translationMap = gson.fromJson(word.getTranslation(), new TypeToken<Map<String, String>>() {
            }.getType());
            List<String> translationList = translationMap.entrySet().stream()
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .collect(Collectors.toList());
            wordAnswerVO.setTranslation(translationList);
            wordAnswerVO.setWord(word.getWord());
            wordAnswerVO.setPronounceAmerica(word.getPronounceAmerica());
            wordAnswerVO.setPronounceEnglish(word.getPronounceEnglish());

            return wordAnswerVO;
        }).collect(Collectors.toList());

        PageVO pageVO = new PageVO();
        pageVO.setTotal(page.getTotal());
        pageVO.setRows(collect);
        return pageVO;
    }
}
