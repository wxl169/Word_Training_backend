package org.wxl.wordTraining.controller;


import org.elasticsearch.rest.RestUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.constant.CollectionConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.collection.CollectionGetRequest;
import org.wxl.wordTraining.model.dto.collection.CollectionRequest;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.service.ICollectionService;
import org.wxl.wordTraining.service.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 收藏夹控制器
 * </p>
 *
 * @author wxl
 */
@RestController
@RequestMapping("/collection")
public class CollectionController {
    private final ICollectionService collectionService;
    private final UserService userService;

    @Autowired
    public CollectionController(ICollectionService collectionService,UserService userService){
        this.collectionService = collectionService;
        this.userService = userService;
    }

    /**
     * 当前用户收藏单词
     * @param collectionAddRequest 收藏物id和类型（0：文章，1：单词）
     * @param request 获取当前登录信息
     * @return 是否添加成功
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addCollection(@RequestBody CollectionRequest collectionAddRequest, HttpServletRequest request){
        if (collectionAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (collectionAddRequest.getId() == null || collectionAddRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选中收藏物");
        }
        if (collectionAddRequest.getType() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请注明收藏物类型");
        }
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(collectionService.addCollection(collectionAddRequest,loginUser));
    }

    /**
     * 当前用户取消收藏单词
     * @param collectionDeleteRequest 收藏物id和类型（0：文章，1：单词）
     * @param request 获取当前登录信息
     * @return 是否取消成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean>  deleteCollection(@RequestBody CollectionRequest collectionDeleteRequest, HttpServletRequest request){
        if (collectionDeleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (collectionDeleteRequest.getId() == null || collectionDeleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选中收藏物");
        }
        if (collectionDeleteRequest.getType() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请注明收藏物类型");
        }
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(collectionService.deleteCollection(collectionDeleteRequest,loginUser));
    }

    /**
     * 获取当前登录用户收藏的内容信息
     * @param collectionGetRequest 用户选择展示的内容
     * @param httpServletRequest 获取当前登陆用户信息
     * @return 内容信息
     */
    @PostMapping("/get")
    public BaseResponse<PageVO> getCollecionByUserId(@RequestBody CollectionGetRequest collectionGetRequest, HttpServletRequest httpServletRequest){
        if (collectionGetRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择展示内容的类型");
        }
        if (collectionGetRequest.getType() == null){
            collectionGetRequest.setType(CollectionConstant.ARTICLE_TYPE);
        }
        if (collectionGetRequest.getCurrent() == null ||collectionGetRequest.getCurrent() <= 0){
            collectionGetRequest.setCurrent(1);
        }
        if (collectionGetRequest.getPageSize() == null || collectionGetRequest.getPageSize() <= 0){
            collectionGetRequest.setPageSize(5);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        PageVO pageVO = collectionService.getCollectionByUserId(collectionGetRequest,loginUser);
        return ResultUtils.success(pageVO);
    }


}
