package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.ArticleMapper;
import org.wxl.wordTraining.model.dto.tag.TagListRequest;
import org.wxl.wordTraining.model.dto.tag.TagUpdateRequest;
import org.wxl.wordTraining.model.entity.Tag;
import org.wxl.wordTraining.mapper.TagMapper;
import org.wxl.wordTraining.model.entity.TbArticle;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.tag.TagListVO;
import org.wxl.wordTraining.service.IArticleService;
import org.wxl.wordTraining.service.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    private ArticleMapper articleMapper;

    @Lazy
    @Autowired
    private IArticleService articleService;

    @Autowired
    public TagServiceImpl(ArticleMapper articleMapper){
        this.articleMapper = articleMapper;
    }
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

    /**
     * 获取所有的标签数据
     * @return 标签名数据
     */
    @Override
    public Set<String> getTagAll() {
        List<Tag> tagList = this.list();
        if (!tagList.isEmpty()){
            return tagList.stream().map(Tag::getTagName).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    /**
     * 根据标签Id删除标签
     * @param tagId 标签Id
     * @return
     */
    @Override
    public boolean deleteTag(Long tagId) {
        //判断是否有该标签
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getId,tagId);
        Tag tag = this.getOne(queryWrapper);
        if (tag == null || StringUtils.isBlank(tag.getTagName())) {
           throw new BusinessException(ErrorCode.NULL_ERROR,"该标签不存在");
        }
        //判断是否有文章关联该标签
        int count = articleMapper.selectArticleByTagCount(tag.getTagName());
        if (count > 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"请先取消关联该标签的文章后再删除标签");
        }
        //删除标签
        return this.removeById(tagId);
    }

    /**
     * 根据传入的修改信息修改标签信息
     * @param tagUpdateRequest 修改信息
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTag(TagUpdateRequest tagUpdateRequest) {
        //判断是否有该标签
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getId,tagUpdateRequest.getId());
        Tag tag = this.getOne(queryWrapper);
        if (tag == null || StringUtils.isBlank(tag.getTagName())) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"该标签不存在");
        }
        //找出关联该标签的文章数据
        List<TbArticle> articleList = articleMapper.selectArticleByTag(tag.getTagName());
        Gson gson = new Gson();
        articleList = articleList.stream().filter(article ->{
            String tags = article.getTags();
            Set<String> tagSet = gson.fromJson(tags, new TypeToken<Set<String>>() {
            }.getType());
            if (tagSet.contains(tag.getTagName())){
                tagSet.remove(tag.getTagName());
                tagSet.add(tagUpdateRequest.getTagName());
                article.setTags(gson.toJson(tagSet));
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        boolean b = articleService.updateBatchById(articleList);
        if (!b){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"修改文章标签失败");
        }

        LambdaUpdateWrapper<Tag> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Tag::getId,tagUpdateRequest.getId());
        updateWrapper.set(Tag::getTagName,tagUpdateRequest.getTagName());
        return this.update(updateWrapper);
    }
}
