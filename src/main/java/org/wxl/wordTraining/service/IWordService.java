package org.wxl.wordTraining.service;

import org.springframework.http.ResponseEntity;
import org.wxl.wordTraining.model.dto.word.WordAddRequest;
import org.wxl.wordTraining.model.dto.word.WordListRequest;
import org.wxl.wordTraining.model.dto.word.WordUpdateRequest;
import org.wxl.wordTraining.model.entity.Word;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.word.WordListVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  单词服务类
 * </p>
 *
 * @author wxl
 * @since 2024-02-20
 */
public interface IWordService extends IService<Word> {

    /**
     * 获取单词列表
     * @param wordListRequest 搜索单词的参数
     * @return 返回单词列表
     */
    PageVO getWordListPage(WordListRequest wordListRequest);

    /**
     * 管理员下载单词模板
     * @param response 当前用户信息
     * @return 是否下载成功
     */
    ResponseEntity downloadWordFile(HttpServletResponse response);

    /**
     * 排除已存在的数据
     * @param cachedDataList 数据列表
     * @return 需要存储的数据
     */
    void excludeWord(List<WordAddRequest> cachedDataList);

    /**
     * 保存从excel导入的数据
     * @param cachedDataList
     */
    void saveByExcel(List<Word> cachedDataList);


    /**
     * 修改从excel导入的数据到数据库
     */
    void updateByExcel(List<Word> cacheDataList);

    /**
     * 修改单词信息
     * @param wordUpdateRequest 需要修改的单词信息
     * @return 是否修改成功
     */
    boolean updateWord(WordUpdateRequest wordUpdateRequest);

    /**
     * 根据单词名获取单词信息
     * @param id 单词id
     * @return 单词信息
     */
    WordListVO getWordDetails(Long id);

    /**
     * 普通用户获取单词库
     * @param wordListRequest 查询单词条件
     * @return 返回数据
     */
    PageVO getWordBankListPage(WordListRequest wordListRequest, HttpServletRequest request);
}
