package org.wxl.wordTraining.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.CollectionConstant;
import org.wxl.wordTraining.constant.CommonConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.exception.ThrowUtils;
import org.wxl.wordTraining.mapper.CollectionMapper;
import org.wxl.wordTraining.model.dto.word.WordAddRequest;
import org.wxl.wordTraining.model.dto.word.WordListRequest;
import org.wxl.wordTraining.model.dto.word.WordUpdateRequest;
import org.wxl.wordTraining.model.entity.TbCollection;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.entity.Word;
import org.wxl.wordTraining.mapper.WordMapper;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.word.WordBankListVO;
import org.wxl.wordTraining.model.vo.word.WordListVO;
import org.wxl.wordTraining.service.IWordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wxl
 * @since 2024-02-20
 */
@Service
@Slf4j
public class WordServiceImpl extends ServiceImpl<WordMapper, Word> implements IWordService {

    private final WordMapper wordMapper;
    private final CollectionMapper collectionMapper;
    private final UserService userService;

    @Autowired
    public WordServiceImpl(WordMapper wordMapper,CollectionMapper collectionMapper,UserService userService) {
        this.wordMapper = wordMapper;
        this.collectionMapper = collectionMapper;
        this.userService = userService;
    }

    private static final Gson gson = new Gson();


    /**
     * 返回单词列表
     *
     * @param wordListRequest 搜索单词的参数
     * @return 返回参数列表
     */
    @Override
    public PageVO getWordListPage(WordListRequest wordListRequest) {
        Integer current = wordListRequest.getCurrent();
        Integer pageSize = wordListRequest.getPageSize();
        if (current == null || current <= 0) {
            current = CommonConstant.PAGE_NUM;
            wordListRequest.setCurrent(current);
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = CommonConstant.PAGE_SIZE;
            wordListRequest.setPageSize(pageSize);
        }
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        //筛选单词
        LambdaQueryWrapper<Word> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(wordListRequest.getWord()), Word::getWord, wordListRequest.getWord())
                .select(Word::getId, Word::getWord, Word::getTranslation, Word::getType, Word::getImage, Word::getExample,
                        Word::getPronounceEnglish, Word::getPronounceAmerica, Word::getSynonym, Word::getAntonym, Word::getExchange)
                .like(StringUtils.isNotBlank(wordListRequest.getTranslation()), Word::getTranslation, wordListRequest.getTranslation());
        PageVO pageVO = new PageVO();
        List<WordListVO> wordListVOS;
        //如果根据类型查看单词数据
        if (wordListRequest.getType() != null && !wordListRequest.getType().isEmpty()){
            List<Word> wordList = this.list(queryWrapper);
            //获取单词列表
            AtomicReference<Long> total = new AtomicReference<>((long) wordList.size());
            wordList = wordList.stream().filter(word -> {
                //获取每个单词的类型
                String type = word.getType();
                List<String> typeListAll = gson.fromJson(type, new TypeToken<List<String>>() {
                }.getType());
                //比较是否含有用户选择的字符串类型。
                if (new HashSet<>(typeListAll).containsAll(wordListRequest.getType())) {
                    return true;
                }
                total.getAndSet(total.get() - 1);
                return false;
            }).collect(Collectors.toList());
            wordListVOS = getWordListVO(wordList);
            wordListVOS = wordListVOS.stream().skip((wordListRequest.getCurrent() - 1) * wordListRequest.getPageSize())
                    .limit(wordListRequest.getPageSize())
                    .collect(Collectors.toList());
            pageVO.setRows(wordListVOS);
            pageVO.setTotal(total.get());
        } else {
            Page<Word> page = new Page<>(current, pageSize);
            page(page, queryWrapper);
            wordListVOS = getWordListVO(page.getRecords());
            pageVO.setRows(wordListVOS);
            pageVO.setTotal(page.getTotal());
        }
        return pageVO;
    }


    /**
     * 管理员下载单词模板
     *
     * @param response
     * @return 是否下载成功
     */
    @Override
    public ResponseEntity downloadWordFile(HttpServletResponse response) {
        FileInputStream fis = null;
        try {
            String fileName = "wordDownload.csv";
            String path = "/file/" + fileName;
            fis = new FileInputStream(new ClassPathResource(path).getFile());
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Disposition", "attachment;filename=" + fileName);
            ResponseEntity res = new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("模板下载失败！");
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 排除已存在的数据
     *
     * @param cachedDataList 数据列表
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void excludeWord(List<WordAddRequest> cachedDataList) {
        //处理需要上传的数据
        if (cachedDataList == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请检查Excel表中是否有数据");
        }
        //先排除cachedDataList中一致的数据
        // 用于记录已经出现过的 word 值
        Set<String> wordSet = new HashSet<>();
        Iterator<WordAddRequest> iterator = cachedDataList.iterator();
        while (iterator.hasNext()) {
            WordAddRequest data = iterator.next();
            if (wordSet.contains(data.getWord())) {
                // 如果 word 已经存在，则从列表中删除这个数据
                iterator.remove();
            } else {
                // 否则将 word 添加到 set 中
                wordSet.add(data.getWord());
            }
        }

        //查看数据库中是否有相同的数据
        LambdaQueryWrapper<Word> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Word::getWord, Word::getId);
        queryWrapper.in(Word::getWord, wordSet);
        //已存在的单词数据
        List<Word> wordList = this.list(queryWrapper);

        //这是需要覆盖的数据
        List<Word> wordUpdateList = new ArrayList<>();

        // 获取数据库中存在的单词
        Map<String, Long> wordMap = wordList.stream().collect(Collectors.toMap(Word::getWord, Word::getId));
        List<Word> words = BeanCopyUtils.copyBeanList(cachedDataList, Word.class);

        //筛选数据
        words.stream().map(word -> {
                    // 如果当前数据存在于数据库中，则返回对应的 WordAddRequest，否则返回 null
                    if (wordMap.containsKey(word.getWord())) {
                        word.setId(wordMap.get(word.getWord()));
                        return word;
                    } else {
                        return null;
                    }
                    // 过滤掉为 null 的元素，即存在于数据库中的数据
                }).filter(Objects::nonNull)
                // 将存在于数据库中的数据添加到更新列表中
                .forEach(wordUpdateList::add);
// 过滤掉已存在于数据库中的数据,将需要新增的数据添加到新增列表中
        List<Word> wordAddList = words.stream()
                .filter(word -> !wordMap.containsKey(word.getWord()))
                .collect(Collectors.toList());

        //如果需要新增的数据大于 0 则 进行新增
        if (!wordAddList.isEmpty()) {
            log.info("{}条数据，开始存储数据库！", wordAddList.size());
            this.saveByExcel(wordAddList);
            log.info("将Excel中的数据存储数据库成功");

        }
        //如果需要修改的数据大于0 则进行修改
        if (!wordUpdateList.isEmpty()) {
            log.info("{}条数据，开始修改数据库！", wordUpdateList.size());
            this.updateByExcel(wordUpdateList);
            log.info("将Excel中的数据更新到数据库成功");
        }
    }


    /**
     * 保存excel中的数据
     *
     * @param cachedDataList
     */
    @Override
    public void saveByExcel(List<Word> cachedDataList) {
        List<Word> words = BeanCopyUtils.copyBeanList(cachedDataList, Word.class);
        boolean b = this.saveBatch(words);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增单词数据失败");
        }
    }

    /**
     * 修改Excel中的数据
     *
     * @param cachedDataList
     */
    @Override
    public void updateByExcel(List<Word> cachedDataList) {
        List<Word> words = BeanCopyUtils.copyBeanList(cachedDataList, Word.class);
        boolean b = this.updateBatchById(words);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新单词数据失败");
        }

    }


    /**
     * 修改单词信息
     * @param wordUpdateRequest 需要修改的单词信息
     * @return 是否修改成功
     */
    @Override
    public boolean updateWord(WordUpdateRequest wordUpdateRequest) {
        Long id = wordUpdateRequest.getId();
        if (id == null || id < 1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String word = wordUpdateRequest.getWord();
        String translation = wordUpdateRequest.getTranslation();
        if (wordUpdateRequest.getType() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择单词类型");
        }
        String pronounceEnglish = wordUpdateRequest.getPronounceEnglish();
        String pronounceAmerican = wordUpdateRequest.getPronounceAmerica();
        if (StringUtils.isAnyBlank(word,translation,pronounceEnglish,pronounceAmerican)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"检查是否有必填参数为空");
        }

        String wordType = gson.toJson(wordUpdateRequest.getType());

        LambdaUpdateWrapper<Word> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Word::getId,wordUpdateRequest.getId())
                .set(Word::getWord,wordUpdateRequest.getWord())
                .set(Word::getTranslation,wordUpdateRequest.getTranslation())
                .set(Word::getType,wordType)
                .set(Word::getPronounceAmerica,wordUpdateRequest.getPronounceAmerica())
                .set(Word::getPronounceEnglish,wordUpdateRequest.getPronounceEnglish())
                .set(StringUtils.isNotBlank(wordUpdateRequest.getExample()),Word::getExample,wordUpdateRequest.getExample())
                .set(StringUtils.isNotBlank(wordUpdateRequest.getExchange()),Word::getExchange,wordUpdateRequest.getExchange())
                .set(StringUtils.isNotBlank(wordUpdateRequest.getSynonym()),Word::getSynonym,wordUpdateRequest.getSynonym())
                .set(StringUtils.isNotBlank(wordUpdateRequest.getAntonym()),Word::getAntonym,wordUpdateRequest.getAntonym());
        return this.update(updateWrapper);
    }


    /**
     * 根据单词名获取单词信息
     * @param id 单词id
     * @return 单词信息
     */
    @Override
    public WordListVO getWordDetails(Long id) {
        Word word = wordMapper.selectById(id);
        if(word == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"暂无该单词");
        }
        return convertToWordListVO(word);
    }


    /**
     * 普通用户查询单词库
     * @param wordListRequest 查询单词条件
     * @return 返回数据
     */
    @Override
    public PageVO getWordBankListPage(WordListRequest wordListRequest, HttpServletRequest request) {
        Integer current = wordListRequest.getCurrent();
        Integer pageSize = wordListRequest.getPageSize();
        if (current == null || current <= 0) {
            current = CommonConstant.PAGE_NUM;
            wordListRequest.setCurrent(current);
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = CommonConstant.PAGE_SIZE;
            wordListRequest.setPageSize(pageSize);
        }
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        //筛选单词
        LambdaQueryWrapper<Word> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(wordListRequest.getWord()), Word::getWord, wordListRequest.getWord())
                .select(Word::getId, Word::getWord, Word::getTranslation, Word::getType,Word::getPronounceEnglish,Word::getPronounceAmerica)
                .like(StringUtils.isNotBlank(wordListRequest.getTranslation()), Word::getTranslation, wordListRequest.getTranslation());
        //需要返回的单词类型信息
        List<WordBankListVO> wordBankListVOS;
        PageVO pageVO = new PageVO();
        //如果根据类型查看单词数据
        if (wordListRequest.getType() != null && !wordListRequest.getType().isEmpty()) {
            //查询符合条件的所有单词数据
            List<Word> wordList = this.list(queryWrapper);
            //统计当前数据条数
            AtomicReference<Integer> total = new AtomicReference<>(wordList.size());
            //筛选出符合标签的单词数据
            wordList = wordList.stream().filter(word -> {
                //获取每个单词的类型
                String type = word.getType();
                List<String> typeListAll = gson.fromJson(type, new TypeToken<List<String>>() {
                }.getType());
                //比较是否含有用户选择的字符串类型。
                if (new HashSet<>(typeListAll).containsAll(wordListRequest.getType())) {
                    return true;
                }
                total.getAndSet(total.get() - 1);
                return false;
            }).collect(Collectors.toList());
            wordBankListVOS = this.convertToWordBankVOList(wordList);
            wordBankListVOS = wordBankListVOS.stream().skip((wordListRequest.getCurrent() - 1) * wordListRequest.getPageSize())
                    .limit(wordListRequest.getPageSize())
                    .collect(Collectors.toList());
            pageVO.setTotal(Long.valueOf(total.get()));
        } else {
            Page<Word> page = new Page<>(current, pageSize);
            page(page, queryWrapper);
            wordBankListVOS = this.convertToWordBankVOList(page.getRecords());
            pageVO.setTotal(page.getTotal());
        }

        //首先判断当前用户是否登录
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        //如果当前用户没有登录则直接返回
        if (loginUserPermitNull != null){
            //如果当前用户已经登录
            //获取当前用户已收藏的单词
            LambdaQueryWrapper<TbCollection> queryCollectionWrapper = new LambdaQueryWrapper<>();
            queryCollectionWrapper.eq(TbCollection::getUserId,loginUserPermitNull.getId())
                    .eq(TbCollection::getType, CollectionConstant.WORD_TYPE)
                    .select(TbCollection::getCollectionId);
            List<TbCollection> tbCollections = collectionMapper.selectList(queryCollectionWrapper);
            //筛选出已关注的单词类型，修改WordBankVO中isCollection的值
            wordBankListVOS = wordBankListVOS.stream()
                    .peek(wordBankListVO -> {
                        boolean isCollection = tbCollections.stream()
                                .anyMatch(tbCollection -> tbCollection.getCollectionId().equals(wordBankListVO.getId()));
                        wordBankListVO.setIsCollection(isCollection);
                    })
                    .collect(Collectors.toList());
        }
        pageVO.setRows(wordBankListVOS);
        return pageVO;
    }

    /**
     * 将 Word对象转换为WordListVO对象
     * @param  list 数据列表
     * @return 返回处理后的结果
     */
    private List<WordListVO> getWordListVO(List<Word> list) {
        return list.parallelStream().map(this::convertToWordListVO).collect(Collectors.toList());
    }

    private WordListVO convertToWordListVO(Word word) {
        WordListVO wordListVO = new WordListVO();
        wordListVO.setId(word.getId());
        wordListVO.setWord(word.getWord());

        // 翻译
        Map<String, String> translationMap = gson.fromJson(word.getTranslation(), new TypeToken<Map<String, String>>() {}.getType());
        List<String> translationList = translationMap.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.toList());
        wordListVO.setTranslation(translationList);

        // 单词类型
        List<String> typeList = gson.fromJson(word.getType(), new TypeToken<List<String>>() {}.getType());
        wordListVO.setType(typeList);
        //美式发音
        wordListVO.setPronounceAmerica(word.getPronounceAmerica());
        //英式发音
        wordListVO.setPronounceEnglish(word.getPronounceEnglish());

        // 图片处理 未完成
        if (StringUtils.isNotBlank(word.getImage())) {
            // TODO: 待完成
            wordListVO.setImage(word.getImage());
        }

        // 例句
        if (StringUtils.isNotBlank(word.getExample())) {
            Map<String, String> exampleMap = gson.fromJson(word.getExample(), new TypeToken<Map<String, String>>() {}.getType());
//            List<String> exampleList = exampleMap.entrySet().stream()
//                    .map(entry -> entry.getKey() + "\n" + entry.getValue())
//                    .collect(Collectors.toList());
            wordListVO.setExample(exampleMap);
        }

        // 同义词
        if (StringUtils.isNotBlank(word.getSynonym())){
            List<String> synonymList = gson.fromJson(word.getSynonym(), new TypeToken<List<String>>() {}.getType());
            wordListVO.setSynonym(synonymList);
        }
        // 反义词
        if (StringUtils.isNotBlank(word.getAntonym())){
            List<String> antonymList = gson.fromJson(word.getAntonym(), new TypeToken<List<String>>() {}.getType());
            wordListVO.setAntonym(antonymList);
        }
        // 时态复数
        if (StringUtils.isNotBlank(word.getExchange())) {
            Map<String, String> exchangeMap = gson.fromJson(word.getExchange(), new TypeToken<Map<String, String>>() {}.getType());
//            List<String> exchangeList = exchangeMap.entrySet().stream()
//                    .map(entry -> entry.getKey() + ":" + entry.getValue())
//                    .collect(Collectors.toList());
            wordListVO.setExchange(exchangeMap);
        }

        return wordListVO;
    }

    /**
     * 将Word数据集合转换为WordBankListVO类型
     * @param wordList 数据
     * @return
     */
    private List<WordBankListVO> convertToWordBankVOList(List<Word> wordList){
        return wordList.parallelStream().map(word -> {
            WordBankListVO wordBankListVO = new WordBankListVO();
            wordBankListVO.setId(word.getId());
            wordBankListVO.setWord(word.getWord());
//        翻译
            Map<String, String> translationMap = gson.fromJson(word.getTranslation(), new TypeToken<Map<String, String>>() {
            }.getType());
            List<String> translationList = translationMap.entrySet().stream()
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .collect(Collectors.toList());
            wordBankListVO.setTranslation(translationList);
            // 单词类型
            List<String> typeList = gson.fromJson(word.getType(), new TypeToken<List<String>>() {
            }.getType());
            wordBankListVO.setType(typeList);
            //美式发音
            wordBankListVO.setPronounceAmerica(word.getPronounceAmerica());
            //英式发音
            wordBankListVO.setPronounceEnglish(word.getPronounceEnglish());
            //是否收藏
            wordBankListVO.setIsCollection(false);
            return wordBankListVO;
        }).collect(Collectors.toList());
    }


}
