package org.wxl.wordTraining.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wxl.wordTraining.annotation.AuthCheck;
import org.wxl.wordTraining.annotation.JwtToken;
import org.wxl.wordTraining.common.*;
import org.wxl.wordTraining.constant.UserConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.listener.WordDataListener;
import org.wxl.wordTraining.model.dto.word.WordAddRequest;
import org.wxl.wordTraining.model.dto.word.WordListRequest;
import org.wxl.wordTraining.model.dto.word.WordUpdateRequest;
import org.wxl.wordTraining.model.entity.Word;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.word.WordByIdVO;
import org.wxl.wordTraining.model.vo.word.WordListVO;
import org.wxl.wordTraining.service.IWordService;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

/**
 * 单词接口
 * @author wxl
 */
@RestController
@RequestMapping("/word")
@Slf4j
public class WordController {
    private IWordService wordService;
    @Autowired
    public WordController(IWordService wordService){
        this.wordService = wordService;
    }


    /**
     * 查询单词库
     *
     * @param wordListRequest 用户查询单词条件
     * @return 脱敏后的单词列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PageVO> getWordList(@RequestBody WordListRequest wordListRequest){
        if (wordListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PageVO pageVO = wordService.getWordListPage(wordListRequest);
        return ResultUtils.success(pageVO);
    }



    /**
     * 删除单词
     *
     * @param deleteRequest 单词id
     * @param request 用户信息
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = wordService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }


    /**
     * 管理员下载单词模版
     *
     * @param response
     * @return
     */
    @GetMapping("/download")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResponseEntity downloadWord(HttpServletResponse response){
        ResponseEntity b = wordService.downloadWordFile(response);
        return b;
    }


    /**
     * 批量上传单词数据
     * @param file 文件
     * @return
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> uploadWord(@RequestParam("file") MultipartFile file){
        if (file == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.toLowerCase().endsWith(".xlsx")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请上传后缀为.xlsx的文件");
        }
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(file.getInputStream());
            EasyExcelFactory.read(in, WordAddRequest.class, new WordDataListener(wordService)).excelType(ExcelTypeEnum.XLSX).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResultUtils.success(true);
    }


    /**
     * 修改单词数据
     * @param wordUpdateRequest 需要修改的数据
     * @return 是否修改成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateWord(@RequestBody WordUpdateRequest wordUpdateRequest) {
        if (wordUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = wordService.updateWord(wordUpdateRequest);
        return ResultUtils.success(b);
    }


    /**
     * 获取某个单词信息
     * @param idRequest 单词id
     * @return 是否修改成功
     */
    @PostMapping("/get/id")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<WordByIdVO> updateWord(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Word byId = wordService.getById(idRequest.getId());
        WordByIdVO wordByIdVO = BeanCopyUtils.copyBean(byId, WordByIdVO.class);
        Gson gson = new Gson();
        List<String> typeList = gson.fromJson(byId.getType(), new TypeToken<List<String>>() {
        }.getType());
        wordByIdVO.setType(typeList);
        return ResultUtils.success(wordByIdVO);
    }

    /**
     * 根据单词名获取单词信息
     * @param idRequest 单词名
     * @return 是否修改成功
     */
    @PostMapping("/get/details")
    @JwtToken
    public BaseResponse<WordListVO> getWordDetails(@RequestBody IdRequest idRequest) {
        if (idRequest == null ||idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        WordListVO word =  wordService.getWordDetails(idRequest.getId());
        return ResultUtils.success(word);
    }

    /**
     * 普通用户查询单词库
     *
     * @param wordListRequest 用户查询单词条件
     * @return 脱敏后的单词列表
     */
    @PostMapping("/list/page/bank")
    @JwtToken
    public BaseResponse<PageVO> getWordBankList(@RequestBody WordListRequest wordListRequest,HttpServletRequest request){
        if (wordListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PageVO pageVO = wordService.getWordBankListPage(wordListRequest,request);
        return ResultUtils.success(pageVO);
    }



}
