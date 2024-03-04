package org.wxl.wordTraining.mapper;

import org.apache.ibatis.annotations.Param;
import org.wxl.wordTraining.model.dto.praise.PraiseAddRequest;
import org.wxl.wordTraining.model.entity.Praise;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wxl.wordTraining.model.entity.User;

/**
 * <p>
 *  点赞Mapper 接口
 * </p>
 *
 * @author wxl
 */
public interface PraiseMapper extends BaseMapper<Praise> {

    /**
     * 查看当前id是否被点赞
     * @param praiseAddRequest 当前id
     * @param loginUser 登录用户
     * @return 是否被点赞
     */
    Praise selectPraise(@Param("praiseAddRequest") PraiseAddRequest praiseAddRequest,@Param("loginUser") User loginUser);

    /**
     * 修改当前id为点赞状态
     * @param praiseAddRequest 当前点赞id
     * @param loginUser 当前登录id
     * @return 是否点赞成功
     */
    boolean updatePraise(PraiseAddRequest praiseAddRequest, User loginUser);
}
