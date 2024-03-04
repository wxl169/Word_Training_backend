package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.praise.PraiseAddRequest;
import org.wxl.wordTraining.model.dto.praise.PraiseDeleteRequest;
import org.wxl.wordTraining.model.entity.Praise;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.entity.User;

/**
 * <p>
 *  点赞服务类
 * </p>
 *
 * @author wxl
 */
public interface IPraiseService extends IService<Praise> {

    /**
     * 判断当前用户是否点赞该物品
     * @param praiseId 点赞物id
     * @param userId 当前用户id
     * @param type 类型id
     * @return 是否点赞
     */
    boolean judgePraise(Long praiseId, Long userId, Integer type);

    /**
     * 当前用户点赞单词
     * @param praiseAddRequest 点赞物id和类型（0：文章，1：评论）
     * @param loginUser 获取当前登录信息
     * @return 是否添加成功
     */
    Boolean addPraise(PraiseAddRequest praiseAddRequest, User loginUser);

    /**
     * 当前用户取消点赞单词
     * @param praiseDeleteRequest 点赞物id和类型（0：文章，1：评论）
     * @param loginUser 获取当前登录信息
     * @return 是否取消成功
     */
    Boolean deletePraise(PraiseDeleteRequest praiseDeleteRequest, User loginUser);
}
