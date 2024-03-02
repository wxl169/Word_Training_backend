package org.wxl.wordTraining.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.wxl.wordTraining.annotation.AuthCheck;
import org.wxl.wordTraining.annotation.JwtToken;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.DeleteRequest;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.constant.UserConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.tag.TagListRequest;
import org.wxl.wordTraining.model.dto.tag.TagUpdateRequest;
import org.wxl.wordTraining.model.entity.Tag;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.tag.TagListVO;
import org.wxl.wordTraining.model.vo.tag.TagVO;
import org.wxl.wordTraining.service.ITagService;
import org.wxl.wordTraining.utils.BeanCopyUtils;

import java.util.List;

/**
 * <p>
 *  标签前端控制器
 * </p>
 *
 * @author wxl
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    private final ITagService tagService;
    @Autowired
    public TagController(ITagService tagService){
        this.tagService = tagService;
    }

    @PostMapping("/page")
    @JwtToken
    public BaseResponse<PageVO> getTagList(@RequestBody TagListRequest tagListRequest){
        if (tagListRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (tagListRequest.getCurrent() == null){
            tagListRequest.setCurrent(1);
        }
        if (tagListRequest.getPageSize() == null){
            tagListRequest.setPageSize(5);
        }
        return ResultUtils.success(tagService.getTagList(tagListRequest));
    }


    /**
     * 根据id删除标签
     * @param deleteRequest 标签id
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteTag(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(tagService.removeById(deleteRequest.getId()));
    }

    /**
     * 根据标签id修改标签名
     * @param tagUpdateRequest 标签修改信息
     * @return 是否修改成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateTag(@RequestBody TagUpdateRequest tagUpdateRequest) {
        if (tagUpdateRequest == null || tagUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaUpdateWrapper<Tag> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(StringUtils.isNotBlank(tagUpdateRequest.getTagName()),Tag::getTagName,tagUpdateRequest.getTagName())
                .eq(Tag::getId,tagUpdateRequest.getId());
        return ResultUtils.success(tagService.update(updateWrapper));
    }

    /**
     * 根据标签名新增标签
     * @param tagName 标签名
     * @return 是否修改成功
     */
    @GetMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> addTag(@RequestParam("tagName") String tagName) {
        if (StringUtils.isBlank(tagName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(tagService.addTag(tagName));
    }


    /**
     * 获取所有的标签信息
     * @return 标签数据列表
     */
    @GetMapping("/list")
    @JwtToken
    public BaseResponse<List<TagVO>> getTagListAll(){
        List<Tag> list = tagService.list();
        return ResultUtils.success(BeanCopyUtils.copyBeanList(list,TagVO.class));
    }

}
