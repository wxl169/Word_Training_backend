package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.tag.TagListRequest;
import org.wxl.wordTraining.model.entity.Tag;
import org.wxl.wordTraining.mapper.TagMapper;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.tag.TagListVO;
import org.wxl.wordTraining.service.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {
    /**
     * 根据标签名查询标签列表
     * @param tagListRequest 查询条件
     * @return 列表数据
     */
    @Override
    public PageVO getTagList(TagListRequest tagListRequest) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Tag::getTagName, tagListRequest.getTagName());

        Page<Tag> page = new Page<>(tagListRequest.getCurrent(), tagListRequest.getPageSize());
        this.page(page, queryWrapper);
        List<TagListVO> tagListVOS = BeanCopyUtils.copyBeanList(page.getRecords(), TagListVO.class);
        return  new PageVO(tagListVOS, page.getTotal());
    }

    /**
     * 根据标签名新增标签
     * @param tagName 标签名
     * @return 是否新增成功
     */
    @Override
    public Boolean addTag(String tagName) {
        //查询标签是否已存在
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(tagName),Tag::getTagName,tagName);
        long count = this.count(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"该标签名已存在");
        }
        Tag tag = new Tag();
        tag.setTagName(tagName);
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        return this.save(tag);
    }
}
