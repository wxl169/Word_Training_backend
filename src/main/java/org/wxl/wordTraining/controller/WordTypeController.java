package org.wxl.wordTraining.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wxl.wordTraining.annotation.AuthCheck;
import org.wxl.wordTraining.annotation.JwtToken;
import org.wxl.wordTraining.common.*;
import org.wxl.wordTraining.constant.UserConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.exception.ThrowUtils;
import org.wxl.wordTraining.model.dto.word.wordType.WordTypeAddRequest;
import org.wxl.wordTraining.model.dto.word.wordType.WordTypeListRequest;
import org.wxl.wordTraining.model.dto.word.wordType.WordTypeUpdateRequest;
import org.wxl.wordTraining.model.entity.WordType;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.word.word_type.WordTypeListVO;
import org.wxl.wordTraining.model.vo.word.word_type.WordTypeVO;
import org.wxl.wordTraining.service.IWordTypeService;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 单词类型
 * @author wxl
 */
@RestController
@RequestMapping("/word_type")
@Slf4j
public class WordTypeController {
    private IWordTypeService wordTypeService;
    @Autowired
    public WordTypeController(IWordTypeService wordTypeService){
        this.wordTypeService = wordTypeService;
    }

    /**
     * 分页获取单词类型列表
     *
     * @param wordTypeListRequest 分页筛选条件
     * @param request 获取登录信息
     * @return 脱敏单词类型列表数据
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PageVO> listWordTypeVOByPage(@RequestBody WordTypeListRequest wordTypeListRequest,
                                                 HttpServletRequest request) {
        if (wordTypeListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PageVO wordTypeList = wordTypeService.getWordTypeList(wordTypeListRequest);
        return ResultUtils.success(wordTypeList);
    }

    /**
     * 获取单词类型列表
     *
     * @param request 获取登录信息
     * @return 脱敏单词类型列表数据
     */
    @GetMapping("/list")
    @JwtToken
    public BaseResponse<List<WordTypeVO>> listWordTypeVO(HttpServletRequest request) {
        List<WordType> list = wordTypeService.list();
        List<WordTypeVO> wordListVOS = BeanCopyUtils.copyBeanList(list, WordTypeVO.class);
        return ResultUtils.success(wordListVOS);
    }


    /**
     * 删除单词类型
     *
     * @param deleteRequest 单词类型id
     * @param request 用户信息
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteWordType(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = wordTypeService.deleteWordType(deleteRequest.getId());
//        boolean b = wordTypeService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 管理员新增单词类型
     * @param wordTypeAddRequest 新增单词类型信息
     * @param request 用户信息
     * @return 是否新增成功
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> addWordType(@RequestBody WordTypeAddRequest wordTypeAddRequest,
                                             HttpServletRequest request) {
        if (StringUtils.isBlank(wordTypeAddRequest.getTypeName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = wordTypeService.addWordType(wordTypeAddRequest);
        return   ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param wordTypeUpdateRequest 单词类型需要修改的数据
     * @param request 当前用户信息
     * @return 是否修改成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateWordType(@RequestBody WordTypeUpdateRequest wordTypeUpdateRequest,
                                            HttpServletRequest request) {
        if (wordTypeUpdateRequest == null || wordTypeUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(wordTypeUpdateRequest.getTypeName())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean update = wordTypeService.updateWordType(wordTypeUpdateRequest);
        return ResultUtils.success(update);
    }


    /**
     * 获取英语单词列表信息
     * @return
     */
    @GetMapping("/get")
    @JwtToken
    public BaseResponse<List<WordTypeListVO>> getWordTypeListVO(){
        return  ResultUtils.success(wordTypeService.getWordTypeListVO());
    }


    /**
     * 获取所有的类型组
     * @return
     */
    @GetMapping("/get/group")
    public BaseResponse<List<WordType>> getWordTypeGroupAll(){
        LambdaQueryWrapper<WordType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WordType::getIsGroupName,1);
        return ResultUtils.success(wordTypeService.list(queryWrapper));
    }

    @PostMapping("/get/one")
    public BaseResponse<WordType> getWordTypeOne(@RequestBody IdRequest idRequest){
        if (idRequest == null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        WordType wordType = wordTypeService.getById(idRequest.getId());
        if (wordType.getIsGroupName().equals(1)){
            wordType.setTypeName(wordType.getTypeGroupName());
        }
        return ResultUtils.success(wordType);
    }

}
