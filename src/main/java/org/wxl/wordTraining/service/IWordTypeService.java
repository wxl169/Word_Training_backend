package org.wxl.wordTraining.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.dto.word.wordType.WordTypeAddRequest;
import org.wxl.wordTraining.model.dto.word.wordType.WordTypeListRequest;
import org.wxl.wordTraining.model.dto.word.wordType.WordTypeUpdateRequest;
import org.wxl.wordTraining.model.entity.WordType;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.word.word_type.WordTypeListVO;
import org.wxl.wordTraining.model.vo.word.word_type.WordTypeVO;

import java.util.List;

/**
 * <p>
 *  单词类型服务类
 * </p>
 *
 * @author wxl
 */
public interface IWordTypeService extends IService<WordType> {

    /**
     * 分页获取单词类型列表
     * @param wordTypeListRequest 分页筛选条件
     * @return 脱敏单词类型列表数据
     */
    PageVO getWordTypeList(WordTypeListRequest wordTypeListRequest);

    /**
     * 获取所有类型组名及其子类型名
     * @return 数据
     */
    List<WordTypeListVO> getWordTypeListVO();

    /**
     * 删除单词类型
     *
     * @param id 类型Id
     * @return 是否删除成功
     */
    boolean deleteWordType(Long id);

    /**
     * 新增单词类型
     * @param wordTypeService 单词类型信息
     * @return 是否成功
     */
    boolean addWordType(WordTypeAddRequest wordTypeService);

    /**
     * 修改单词类型数据
     * @param wordTypeUpdateRequest 需要修改的数据
     * @return 是否修改成功
     */
    Boolean updateWordType(WordTypeUpdateRequest wordTypeUpdateRequest);

    /**
     * 获取单词类型列表
     * @return 单词类型列表信息
     */
    List<WordType> listWordTypeVO();
}
