package org.wxl.wordTraining.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.CommonConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.exception.ThrowUtils;
import org.wxl.wordTraining.mapper.WordMapper;
import org.wxl.wordTraining.model.dto.word.wordType.WordTypeAddRequest;
import org.wxl.wordTraining.model.dto.word.wordType.WordTypeListRequest;
import org.wxl.wordTraining.model.dto.word.wordType.WordTypeUpdateRequest;
import org.wxl.wordTraining.model.entity.Word;
import org.wxl.wordTraining.model.entity.WordType;
import org.wxl.wordTraining.mapper.WordTypeMapper;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.word.word_type.WordTypeListAllVO;
import org.wxl.wordTraining.model.vo.word.word_type.WordTypeListVO;
import org.wxl.wordTraining.service.IWordService;
import org.wxl.wordTraining.service.IWordTypeService;

import org.wxl.wordTraining.utils.BeanCopyUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  单词类型服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class WordTypeServiceImpl extends ServiceImpl<WordTypeMapper, WordType> implements IWordTypeService {
    private WordTypeMapper wordTypeMapper;
    private IWordService wordService;
    private WordMapper wordMapper;
    private RedisTemplate<String,Object> redisTemplate;
    private static  final  String REDIS_KEY = "wordTraining:wordType";
    @Autowired
    public WordTypeServiceImpl(WordTypeMapper wordTypeMapper, RedisTemplate<String,Object> redisTemplate,WordMapper wordMapper,IWordService wordService){
        this.wordTypeMapper = wordTypeMapper;
        this.redisTemplate = redisTemplate;
        this.wordMapper = wordMapper;
        this.wordService = wordService;
    }

    /**
     * 分页获取单词类型列表
     * @param wordTypeListRequest 分页筛选条件
     * @return 脱敏单词类型列表数据
     */
    @Override
    public PageVO getWordTypeList(WordTypeListRequest wordTypeListRequest) {
        Integer current = wordTypeListRequest.getCurrent();
        Integer pageSize = wordTypeListRequest.getPageSize();

        if (current == null || current <= 0){
            current = CommonConstant.PAGE_NUM;
        }
        if (pageSize == null || pageSize <= 0){
            pageSize = CommonConstant.PAGE_SIZE;
        }
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<WordType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WordType::getIsGroupName,1);

        //是否选择查询条件
        if (StringUtils.isNotBlank(wordTypeListRequest.getTypeName())){
            LambdaQueryWrapper<WordType> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.like(WordType::getTypeGroupName,wordTypeListRequest.getTypeName())
                    .or()
                        .like(WordType::getTypeName,wordTypeListRequest.getTypeName());
            List<WordType> wordTypes = wordTypeMapper.selectList(queryWrapper1);

            Set<Long> wordTypeGroupId = wordTypes.stream().map(wordType -> {
                //是类型组，则直接加入set集合
                if (wordType.getIsGroupName().equals(1)) {
                    return wordType.getId();
                } else {
                    return wordType.getTypeGroupId();
                }
            }).collect(Collectors.toSet());

            if (!wordTypeGroupId.isEmpty()){
                queryWrapper.in(WordType::getId,wordTypeGroupId);
            }
        }

        Page<WordType> page = new Page<>(current,pageSize);
        page(page,queryWrapper);
        List<WordTypeListAllVO> wordTypeListAllVOS = page.getRecords().stream().map(wordType -> {
            WordTypeListAllVO wordTypeListAllVO = new WordTypeListAllVO();
            wordTypeListAllVO.setId(wordType.getId());
            wordTypeListAllVO.setTypeName(wordType.getTypeGroupName());
            wordTypeListAllVO.setChilderWordTypeList(getWordTypeVOListAll(wordType.getId()));
            wordTypeListAllVO.setCreateTime(wordType.getCreateTime());
            wordTypeListAllVO.setUpdateTime(wordType.getUpdateTime());
            return wordTypeListAllVO;
        }).collect(Collectors.toList());
        return new PageVO(wordTypeListAllVOS,page.getTotal());
    }

    /**
     * 获取所有类型组名及其子类型名
     * @return 数据
     */
    @Override
    public List<WordTypeListVO> getWordTypeListVO() {
        LambdaQueryWrapper<WordType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WordType::getIsGroupName,1);
        //所有类型组名
        List<WordType> wordTypes = wordTypeMapper.selectList(queryWrapper);
        // 将类型名 放置在类型组件名下
        return  wordTypes.stream().map(wordType -> {
            WordTypeListVO wordTypeListVO = new WordTypeListVO();
            wordTypeListVO.setId(wordType.getId());
            wordTypeListVO.setTypeName(wordType.getTypeGroupName());
            wordTypeListVO.setChilderWordTypeList(getWordTypeVOList(wordType.getId()));
            return wordTypeListVO;
        }).collect(Collectors.toList());
    }

    /**
     * 删除单词类型
     *
     * @param id 类型Id
     * @return 是否删除成功
     */
    @Override
    public boolean deleteWordType(Long id) {
        LambdaQueryWrapper<WordType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WordType::getId,id);
        WordType wordType = wordTypeMapper.selectOne(queryWrapper);
        if (wordType == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"该类型不存在");
        }
        int delete = 0;
        //如果是类型组名，则删除所有子类型
        if (wordType.getIsGroupName().equals(1)){
            //1.获取所有子类型
            List<WordType> wordTypes = wordTypeMapper.selectList(new LambdaQueryWrapper<WordType>()
                    .eq(WordType::getTypeGroupId,id).eq(WordType::getIsGroupName,0).eq(WordType::getIsDelete,0));
            //2.查询子类型是否关联单词
            int count = 0;
            for (WordType wordType1 : wordTypes) {
                count = wordMapper.selectWordByWordTypeCount(wordType1.getTypeName());
                if (count > 0) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, wordType1.getTypeName() + "类型下有单词，请先删除该类型关联的单词");
                }
            }
            //删除所有关联子类型
            LambdaQueryWrapper<WordType> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(WordType::getTypeGroupId,id)
                    .or()
                    .eq(WordType::getId,id);
             delete = wordTypeMapper.delete(deleteWrapper);
        }else{
            //如果是子类型检查是否有关联的单词
            //1.获取子类型的名称
            String typeName = wordType.getTypeName();
            //2.查询该类型下是否有单词
            int count = wordMapper.selectWordByWordTypeCount(typeName);
            if (count > 0){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"请先删除该类型下的单词");
            }
             delete = wordTypeMapper.deleteById(id);
        }
        if (delete > 0){
            //删除Redis缓存
            redisTemplate.delete(REDIS_KEY);
            return true;
        }
        return false;
    }

    /**
     * 新增单词类型信息
     * @param wordTypeAddRequest 单词类型信息
     * @return 是否成功
     */
    @Override
    public boolean addWordType(WordTypeAddRequest wordTypeAddRequest) {
        LambdaQueryWrapper<WordType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WordType::getTypeName,wordTypeAddRequest.getTypeName())
                .or()
                .eq(WordType::getTypeGroupName,wordTypeAddRequest.getTypeName());
        List<WordType> wordTypes = wordTypeMapper.selectList(queryWrapper);
        if (!wordTypes.isEmpty()){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"该单词类型已存在");
        }

        WordType wordType = new WordType();
        //新增单词
        if (wordTypeAddRequest.getIsGroupName() == 1){
            //如果等于1 则是新增的类型组名
            wordType.setTypeGroupName(wordTypeAddRequest.getTypeName());
            wordType.setIsGroupName(1);
        }else{
            //否则新增的类型名
            if (wordTypeAddRequest.getId()  <= 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            wordType.setTypeName(wordTypeAddRequest.getTypeName());
            wordType.setTypeGroupId(wordTypeAddRequest.getId());
            wordType.setIsGroupName(0);
        }
        wordType.setCreateTime(LocalDateTime.now());
        wordType.setUpdateTime(LocalDateTime.now());
        int insert = wordTypeMapper.insert(wordType);
        if (insert <= 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"新增单词失败");
        }
        //删除Redis缓存
        redisTemplate.delete(REDIS_KEY);
        return true;
    }

    /**
     * 修改单词数据
     * @param wordTypeUpdateRequest 需要修改的数据
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateWordType(WordTypeUpdateRequest wordTypeUpdateRequest) {
        WordType wordType = new WordType();
        wordType.setId(wordTypeUpdateRequest.getId());
        WordType wordTypeByOne = wordTypeMapper.selectById(wordTypeUpdateRequest.getId());

        if (wordTypeUpdateRequest.getId().equals(wordTypeUpdateRequest.getTypeGroupId())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能将类型组名设置为自己的子类型");
        }

        //判断新类型名是否与原类型名一致
        if ((StringUtils.isNotBlank(wordTypeByOne.getTypeName()) && !wordTypeByOne.getTypeName().equals(wordTypeUpdateRequest.getTypeName()))
                || (StringUtils.isNotBlank(wordTypeByOne.getTypeGroupName()) && !wordTypeByOne.getTypeGroupName().equals(wordTypeUpdateRequest.getTypeName()))){
            LambdaQueryWrapper<WordType> queryWrapper = new LambdaQueryWrapper<>();
            //查看该名是否已存在
            queryWrapper.eq(WordType::getTypeName,wordTypeUpdateRequest.getTypeName())
                    .or()
                    .eq(WordType::getTypeGroupName,wordTypeUpdateRequest.getTypeName());
            WordType wordType1 = this.getOne(queryWrapper);
            if (wordType1 != null){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"该类型名已存在");
            }
        }

        //如果单词类型原是类型组，则将其子类型全部移入更改后的类型组
        if (wordTypeByOne.getIsGroupName().equals(1)){
            //将类型组转换为其他类型组中的单词类型
            //先查看是否有子类型
            LambdaQueryWrapper<WordType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WordType::getTypeGroupId,wordTypeByOne.getId())
                    .eq(WordType::getIsGroupName,0)
                    .eq(WordType::getIsDelete,0);
            List<WordType> wordTypeList = wordTypeMapper.selectList(queryWrapper);
            if (wordTypeUpdateRequest.getIsGroupName().equals(0) && wordTypeList.size() > 0){
                LambdaUpdateWrapper<WordType> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(WordType::getTypeGroupId,wordTypeByOne.getId())
                        .set(WordType::getTypeGroupId,wordTypeUpdateRequest.getTypeGroupId());
                boolean update = this.update(updateWrapper);
                if (!update){
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,"将类型组转换为单词类型失败！");
                }
            }
        }

        //如果是单词类型转换为类型组，则先判断它是否关联单词，如果关联了单词请先取消关联所有单词，在进行转换
        if (wordTypeByOne.getIsGroupName().equals(0) && wordTypeUpdateRequest.getIsGroupName().equals(1)){
            int count = wordMapper.selectWordByWordTypeCount(wordTypeByOne.getTypeName());
            if (count > 0){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"请先取消关联其他单词再转换为类型组！");
            }
        }
        Gson gson = new Gson();
        //如果是单词类型修改名字，则修改所有关联他的单词类型
        if (wordTypeByOne.getIsGroupName().equals(0) && wordTypeUpdateRequest.getIsGroupName().equals(0)){
            //1.获取原类型名
            String oldTypeName = wordTypeByOne.getTypeName();
            //2.新类型名
            String newTypeName = wordTypeUpdateRequest.getTypeName();
            List<Word> wordList =  wordMapper.selectWordByWordType(oldTypeName);
            wordList = wordList.stream().filter(word ->{
                String type = word.getType();
                Set<String> wordTypeList = gson.fromJson(type, new TypeToken<Set<String>>() {
                }.getType());
                //如果存在该单词类型
                if (wordTypeList.contains(oldTypeName)){
                    //替换为新的单词类型
                    wordTypeList.remove(oldTypeName);
                    wordTypeList.add(newTypeName);
                    word.setType(gson.toJson(wordTypeList));
                    return  true;
                }
                return false;
            }).collect(Collectors.toList());
            boolean b = wordService.updateBatchById(wordList);
            if (!b){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"修改单词类型失败");
            }
        }

        if (wordTypeUpdateRequest.getIsGroupName().equals(1)){
            //如果是类型组
            wordType.setTypeGroupName(wordTypeUpdateRequest.getTypeName());
            wordType.setIsGroupName(1);
            wordType.setTypeName("");
            wordType.setTypeGroupId(0L);
        }else{
            //不是类型组
            wordType.setIsGroupName(0);
            wordType.setTypeGroupId(wordTypeUpdateRequest.getTypeGroupId());
            wordType.setTypeName(wordTypeUpdateRequest.getTypeName());
            wordType.setTypeGroupName("");
        }
        wordType.setUpdateTime(LocalDateTime.now());
        int i = wordTypeMapper.updateById(wordType);

        if (i <= 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"修改单词类型失败");
        }
        redisTemplate.delete(REDIS_KEY);
        return true;
    }

    /**
     * 获取单词列表信息
     * @return
     */
    @Override
    public List<WordType> listWordTypeVO() {
        List<WordType> wordTypeList;
        //1.查看Redis中是否存在该Key的数据
        wordTypeList = (List<WordType>)redisTemplate.opsForValue().get(REDIS_KEY);
        if (wordTypeList != null){
            return wordTypeList;
        }
        //2.如果Redis中不存在该Key的数据，则查询数据库，并将数据存入Redis
        LambdaQueryWrapper<WordType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WordType::getIsGroupName,0)
                .eq(WordType::getIsDelete,0);
        wordTypeList = wordTypeMapper.selectList(queryWrapper);
        //3.将数据存入Redis
        redisTemplate.opsForValue().set(REDIS_KEY,wordTypeList,60 * 60 * 6, TimeUnit.SECONDS);
        return wordTypeList;
    }


    /**
     * 获取当前类型组名的子类型名列表
     * @param groupId 类型组件名的id
     * @return 子类型名数据
     */
    private List<WordTypeListVO> getWordTypeVOList(Long groupId){
        if (groupId == null || groupId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<WordType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WordType::getTypeGroupId,groupId);
        List<WordType> wordTypes = wordTypeMapper.selectList(queryWrapper);
        return BeanCopyUtils.copyBeanList(wordTypes,WordTypeListVO.class);
    }

    /**
     * 获取当前类型组名的子类型名列表
     * @param groupId 类型组件名的id
     * @return 子类型名数据
     */
    private List<WordTypeListAllVO> getWordTypeVOListAll(Long groupId){
        if (groupId == null || groupId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<WordType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WordType::getTypeGroupId,groupId);
        List<WordType> wordTypes = wordTypeMapper.selectList(queryWrapper);
        return BeanCopyUtils.copyBeanList(wordTypes,WordTypeListAllVO.class);
    }
}
