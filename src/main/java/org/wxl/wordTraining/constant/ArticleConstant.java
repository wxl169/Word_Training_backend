package org.wxl.wordTraining.constant;

/**
 * 文章常量
 * @author wxl
 */
public interface ArticleConstant {
    //  --------------------------------------------------  状态 -----------------------------
    /**
     * 正常发布
     */
    int NORMAL_RELEASE = 0;
    /**
     * 整改中
     */
    int RECTIFICATION = 1;

    /**
     * 整改完
     */
    int RECTIFICATION_END = 2;
    /**
     * 已封禁
     */
    int BAN = 3;

    //  --------------------------------------------------  权限 -----------------------------
    /**
     * 公开
     */
    int OPEN = 0;
    /**
     * 私有
     */
    int PRIVATE = 1;
    /**
     * 仅关注自己的用户
     */
    int REGARD_BY_MYSELF  = 2;
    /**
     * 仅自己关注的用户
     */
    int MYSELF_BY_REGARD = 3;

}
