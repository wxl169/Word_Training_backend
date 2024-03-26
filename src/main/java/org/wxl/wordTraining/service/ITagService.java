package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.tag.TagListRequest;
import org.wxl.wordTraining.model.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.vo.PageVO;

import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 */
public interface ITagService extends IService<Tag> {


    /**
     * 根据标签名查询标签列表
     * @param tagListRequest 查询条件
     * @return 列表数据
     */
    PageVO getTagList(TagListRequest tagListRequest);

    /**
     * 根据标签名新增标签
     * @param tagName 标签名
     * @return
     */
    Boolean addTag(String tagName);

    /**
     * 获取所有的标签列表
     * @return 标签列表数据
     */
    Set<String> getTagAll();
}
