/*
 Navicat Premium Data Transfer

 Source Server         : wxl
 Source Server Type    : MySQL
 Source Server Version : 80033 (8.0.33)
 Source Host           : localhost:3306
 Source Schema         : word_training

 Target Server Type    : MySQL
 Target Server Version : 80033 (8.0.33)
 File Encoding         : 65001

 Date: 15/04/2024 03:18:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_article
-- ----------------------------
DROP TABLE IF EXISTS `tb_article`;
CREATE TABLE `tb_article`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文章id',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `user_id` bigint NOT NULL COMMENT '发布人id',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `visit_number` bigint NOT NULL DEFAULT 0 COMMENT '浏览量',
  `praise_number` bigint NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_number` bigint NOT NULL DEFAULT 0 COMMENT '评论数',
  `collection_number` bigint NOT NULL DEFAULT 0 COMMENT '收藏数',
  `tags` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0：正常发布，1：审核中，2：整改中，3：草稿，4：已封禁',
  `permissions` tinyint NOT NULL DEFAULT 0 COMMENT '权限（0：公开，1：私有，2：仅关注自己的用户）',
  `review_opinions` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_article
-- ----------------------------
INSERT INTO `tb_article` VALUES (1, '英语四六级备考经验分享', '<p style=\"text-align: start;\">我本人对英语并不是特别感兴趣，毕竟英语对我来说只是阅读一些外国教材、书籍或者听外国课程视频的辅助工具。不过既然是工具，那就必须熟练运用。</p><ol><li style=\"text-align: start;\">我认为，英语学习是一个偏积累的过程，短期的高强度学习并不能有较大的提升。在20年下半年的四级考试和六级考试前，我大概都只留出了不到一周的时间专门复习熟练题型（因为学校有其他考试），但是最后的发挥都还算稳定。因此我认为，对于想以高分考过四六级的同学来说，应该在平时进行大量的基础积累（词汇、语句）。</li><li style=\"text-align: start;\"><strong>关于难度定位</strong>。我认为，并不能说在考四级时最难只做过四级的题（甚至只做过高考题），到考六级时最难只做过六级的题目。正如电子元器件一样，我们最好给自己留一点冗余的能力来应付考试的应急情况（例如我在四级考试时就差点涂错了听力答题卡，造成了之后15分钟左右的心有余悸）。因此，我个人建议在四级考试2~3个月前可以试着做几套六级的题目，而在六级考试前试一试托福雅思的题目（尤其建议托福听力）。这样的更高难度的适应性挑战能够使你在应付四六级考试时能够拥有相对从容的心态。</li><li style=\"text-align: start;\">关于临阵磨枪。正如前文所述，我在四六级考试均只有不到一周的时间进行备考，不过我认为这一周的练习还是非常有效的。我的个人经验是一天完成1~2本星火英语的四六级真题（一套里面有三张卷子），然后晚上进行稍微的整理和总结。总之，把自己的心态调整到英语状态即可。</li><li style=\"text-align: start;\"><strong>关于应试技巧。</strong>这一点我也有一些心得。以下是本人考试时的技巧：</li></ol><p style=\"text-align: start;\"><strong>(a)听力：</strong>在听力开始前，有足足3~5分钟的题干阅读、考试规则等等信息的朗读，千万别听！这段时间完全可以阅读Section A的选项，并且在每个选项旁写几个关键词。这一点可以极高地提升听力的正确率、省略大量犹豫的时间，从容地面对正常考试。没听到的题目直接扔掉，听力结束后凭印象快速蒙一个上去。<strong>(b)阅读部分的填词：</strong>先别看文章，直接把15个词的中文大意写在单词的旁边，这样基本能够熟悉所有的单词，一般能够在8分钟内完成该题。这一题是省时间给第二题的，所以尽量收缩答题时间。</p><p style=\"text-align: start;\"><strong>(c)阅读部分的选句：</strong>同样的，先不要看文章（估计也看不了，太长了），直接浏览10个句子，了解大意（或者极简单地用中文写在旁边），然后快速在原文中搜寻。同时，浏览时注意圈出关键词，如人名、公司名、地名等特殊名词，这个可以非常有效的控制搜索范围，大大提高效率。此题尽量控制在15分钟内。<br><strong>(d)两篇典型阅读：</strong>如果有20分钟以上的时间，基本是足够仔细读完全文并且做题的（这个的准确度明显是高于略读的）。不过我一般的习惯还是先读题，然后回文章里找，这样可以加快速度。不过记住，一定要把“证据”所在的句子勾勒出来，有利于对比检查。阅读部分争取留出3~5分钟快速检查整个部分不太确定的题目。<br><strong>(e)翻译和作文：</strong>这一点我也并没有太好的取巧办法。当然，把句子写流畅、适当用复杂一些的单词和结构是比较好的，但是也一定要注意准确性。大家如果想提高可以适当背诵一些模板。最后，适当练字，这个功在平时，我的英文字写的也不算好看（下面有附图片，见笑了），不过还算清晰，这个在实践中一般会让改卷老师有同情分（或喜爱分）。</p>', 1, '这篇文章主要是作为英语四六级考试的经验分享。本人是一名工科大二学生，因此我的学习经验可能会偏实用主义一些，大家可以作为一个参考。文章中推荐的书籍和题集全部都是我个人阅读和使用的，并不是广告。如果有任何疏漏，欢迎大家指正交流！', 3, 1, 1, 1, '[\"四级考试\",\"六级考试\"]', 'https://image.cqiewxl.cn/2024/04/14/b9985ec319984d31a10a8970e5e3525a.jpg', 0, 0, NULL, '2024-04-14 02:15:04', '2024-04-14 02:15:04', 0);

-- ----------------------------
-- Table structure for tb_collection
-- ----------------------------
DROP TABLE IF EXISTS `tb_collection`;
CREATE TABLE `tb_collection`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏id',
  `collection_id` bigint NOT NULL COMMENT '被收藏对象id',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '收藏对象类型（0：文章，1：单词）',
  `user_id` bigint NOT NULL COMMENT '收藏用户id',
  `collection_time` datetime NOT NULL COMMENT '收藏时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_collection
-- ----------------------------
INSERT INTO `tb_collection` VALUES (1, 1, 0, 1, '2024-04-14 03:21:11', 0);

-- ----------------------------
-- Table structure for tb_comments
-- ----------------------------
DROP TABLE IF EXISTS `tb_comments`;
CREATE TABLE `tb_comments`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论id',
  `user_id` bigint NOT NULL COMMENT '评论用户id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NOT NULL COMMENT '评论时间',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父评论id',
  `top_id` bigint NULL DEFAULT NULL COMMENT '顶层评论id',
  `article_id` bigint NOT NULL COMMENT '被评论文章id',
  `is_top` tinyint NOT NULL DEFAULT 0 COMMENT '是否为顶层评论（0：是，1：不是）',
  `is_sticky` tinyint NULL DEFAULT 0 COMMENT '是否置顶（0：不置顶，1：置顶）',
  `is_show` tinyint NOT NULL DEFAULT 0 COMMENT '是否显示（0：显示，1：不显示）',
  `is_complain` tinyint NOT NULL COMMENT '是否违禁（0：无，1：有）',
  `praise_number` bigint NOT NULL DEFAULT 0 COMMENT '点赞数',
  `review_opinions` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_comments
-- ----------------------------
INSERT INTO `tb_comments` VALUES (1, 1, '<p>第一次😁</p>', '2024-04-14 03:21:37', NULL, NULL, 1, 0, 0, 0, 0, 1, NULL, 0);

-- ----------------------------
-- Table structure for tb_complain
-- ----------------------------
DROP TABLE IF EXISTS `tb_complain`;
CREATE TABLE `tb_complain`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '投诉id',
  `complain_id` bigint NOT NULL COMMENT '投诉对象id',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '投诉类型（0：文章，1：评论）',
  `complain_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投诉内容',
  `complain_user_id` bigint NOT NULL COMMENT '投诉人id',
  `is_complain_user_id` bigint NOT NULL COMMENT '被投诉人id',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0：未处理，1：已处理）',
  `review_comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核批注',
  `complain_time` datetime NOT NULL COMMENT '投诉时间',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_complain
-- ----------------------------

-- ----------------------------
-- Table structure for tb_medal
-- ----------------------------
DROP TABLE IF EXISTS `tb_medal`;
CREATE TABLE `tb_medal`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '成就id',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '勋章名',
  `description` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '勋章描述',
  `condition_show` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '勋章获得条件展示（使用文字描述获取条件，展示给用户）',
  `condition` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '勋章获得条件（使用特定的描述获取条件，由任务发布者设定）',
  `region` tinyint NOT NULL DEFAULT 0 COMMENT '勋章获得的区域（0：登录，1：单词，2：社区）',
  `number` bigint NULL DEFAULT NULL COMMENT '勋章获得需求数量',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '勋章类型（0：普通成就，1：限定成就）',
  `logo` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '勋章图标',
  `begin_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_medal
-- ----------------------------

-- ----------------------------
-- Table structure for tb_medal_get
-- ----------------------------
DROP TABLE IF EXISTS `tb_medal_get`;
CREATE TABLE `tb_medal_get`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '勋章获取进度id',
  `achievement_id` bigint NOT NULL COMMENT '勋章id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `now_number` bigint NOT NULL DEFAULT 0 COMMENT '当前数量',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0：未完成，1：已完成）',
  `is_wear` tinyint NOT NULL DEFAULT 0 COMMENT '是否佩戴（0：未佩戴，1：已佩戴）',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_medal_get
-- ----------------------------

-- ----------------------------
-- Table structure for tb_praise
-- ----------------------------
DROP TABLE IF EXISTS `tb_praise`;
CREATE TABLE `tb_praise`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '点赞id',
  `praise_id` bigint NOT NULL COMMENT '点赞对象id',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '点赞类型(0:文章，1：评论）',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `praise_time` datetime NOT NULL COMMENT '点赞时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_praise
-- ----------------------------
INSERT INTO `tb_praise` VALUES (1, 1, 0, 1, '2024-04-14 03:21:10', 0);
INSERT INTO `tb_praise` VALUES (2, 1, 1, 1, '2024-04-14 03:21:42', 0);

-- ----------------------------
-- Table structure for tb_tag
-- ----------------------------
DROP TABLE IF EXISTS `tb_tag`;
CREATE TABLE `tb_tag`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `tag_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_tag
-- ----------------------------
INSERT INTO `tb_tag` VALUES (1, '四级考试', '2024-04-14 02:04:36', '2024-04-14 02:04:36', 0);
INSERT INTO `tb_tag` VALUES (2, '六级考试', '2024-04-14 02:04:46', '2024-04-14 02:04:46', 0);

-- ----------------------------
-- Table structure for tb_task
-- ----------------------------
DROP TABLE IF EXISTS `tb_task`;
CREATE TABLE `tb_task`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务标题',
  `description` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务描述',
  `condition_show` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务达成条件展示（使用文字描述获取条件，展示给用户）',
  `condition` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务达成条件（使用特定的描述获取条件，由任务发布者设定）',
  `region` tinyint NULL DEFAULT 0 COMMENT '任务达成的区域（0：登录，1：单词，2：社区）',
  `number` bigint NULL DEFAULT 0 COMMENT '任务达成数量',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '任务类型',
  `reward` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务奖励',
  `begin_time` datetime NULL DEFAULT NULL COMMENT '任务开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '任务结束时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_task
-- ----------------------------

-- ----------------------------
-- Table structure for tb_task_get
-- ----------------------------
DROP TABLE IF EXISTS `tb_task_get`;
CREATE TABLE `tb_task_get`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务执行记录id',
  `task_id` bigint NOT NULL COMMENT '任务id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `now_number` bigint NOT NULL DEFAULT 0 COMMENT '当前数量',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0：未完成，1：已完成）',
  `begin_time` datetime NOT NULL COMMENT '任务执行时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_task_get
-- ----------------------------

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_account` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `user_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `phone` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '昵称',
  `birthday` datetime NULL DEFAULT NULL COMMENT '生日',
  `gender` tinyint NOT NULL DEFAULT 0 COMMENT '性别(0:男，1：女)',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '头像',
  `role` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'user' COMMENT '角色（user:普通用户，admin：管理员，ban：封号）',
  `point_number` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '积分数',
  `concern` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关注人',
  `challenge_num` int NOT NULL DEFAULT 5 COMMENT '每日挑战次数默认为5',
  `coiled_day` bigint NOT NULL DEFAULT 0 COMMENT '连续在线天数',
  `online_day` bigint NOT NULL DEFAULT 0 COMMENT '在线天数',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最近在线时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1, '209000243', '972cd1dc188a0d44d4ba9b628c0135b2', '18184051285', 'wxl20151122@163.com', '用户209000243', '2001-05-05 00:00:00', 0, 'https://image.cqiewxl.cn/2023/09/26/97dbdd68b48e4027988d2ce90965cdd5.jpg', 'admin', 0, NULL, 5, 1, 1, '2024-04-14 17:34:01', '2024-04-14 01:48:47', '2024-04-14 01:49:47', 0);
INSERT INTO `tb_user` VALUES (2, '209000200', '972cd1dc188a0d44d4ba9b628c0135b2', NULL, NULL, '用户209000200', NULL, 0, 'https://image.cqiewxl.cn/2023/09/26/97dbdd68b48e4027988d2ce90965cdd5.jpg', 'admin', 0, NULL, 5, 0, 0, NULL, '2024-04-14 03:22:44', '2024-04-14 03:23:12', 0);

-- ----------------------------
-- Table structure for tb_word
-- ----------------------------
DROP TABLE IF EXISTS `tb_word`;
CREATE TABLE `tb_word`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '单词id',
  `word` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '单词',
  `translation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '翻译',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型',
  `image` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `example` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '例句',
  `pronounce_english` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英式发音',
  `pronounce_america` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '美式发音',
  `synonym` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '同义词',
  `antonym` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '反义词',
  `exchange` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '时态复数变化，使用 \"/\" 分割不同项目',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `is_delete` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_word
-- ----------------------------
INSERT INTO `tb_word` VALUES (1, 'abandon', '{\n    \"vt.\": \"丢弃 放弃\",\n    \" n.\": \"放纵vt.抛弃\"\n}', '[\"四级\"]', NULL, '{\n    \"Their decision to abandon the trip was made because of financial constraints\": \"他们决定放弃这次出游是因为财力有限。\",\n    \"We had no option but to abandon the meeting.\": \"我们别无选择，只有放弃这次会面。\"\n}', '/əˈbændən/', '/əˈbændən/', '[\"desert\",\"leave\",\"quit\",\"break\",\"offgive\" ]', '[\"stay\",\"continue\"]', '{\n    \"第三人称单数\": \"abandons\",\n    \"现在分词\": \"abandoning\",\n    \"过去式\": \"abandoned\",\n    \"过去分词\": \"abandoned\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (2, 'inter', '{\n    \"vt.\":\"埋葬(遗体)\"\n}', '[\"四级\"]', NULL, '{\n    \"The bitter disappointments to follow did not inter this thought. \":\"后来的痛苦的失望并没有埋葬这种思想。\"\n}', '/ɪnˈtɜː(r)/', '/ɪnˈtɜːr/', '[\n    \"entomb\",\n    \"sepulchre\",\n    \"bury\",\n    \"hearse\"\n]', NULL, '{\n    \"第三人称单数\":\"inters\",\n    \"现在分词\":\"interring\",\n    \"过去式\":\"interred\",\n    \"过去分词\":\"interred\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (3, 'consistent', '{\n    \"adj.\": \"一致的;与…一致的;符合的;持续的;相符的;连续的;始终如一的;不矛盾的;相互连贯的\"\n}', '[\"六级\"]', NULL, '{\n    \"These actions are consistent with his principles. \": \"这些行为与他的原则是一致的。\",\n    \"The results are entirely consistent with our earlier research. \": \"这些结果与我们早些时候的研究完全吻合。\"\n}', '/kənˈsɪstənt/', '/kənˈsɪstənt/', NULL, NULL, '{\n    \"比较级\": \"more consistent\",\n    \"最高级\": \"most consistent\",\n    \"派生词\": \"consistently\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (4, 'battery', '{\n    \"n.\": \"电池;一组，一系列，一群，一套;殴打罪;排炮;（常用作形容词）层架式鸡笼;（棒球队的）投手和接手\"\n}', '[\"六级\"]', NULL, '{\n    \"Before use, the battery must be charged. \": \"电池使用前必须充电。\",\n    \"The battery is flat. \": \"电池没电了。\"\n}', '/ˈbætri/', '/ˈbætəri/', '[\n    \"bundle\",\n    \"constellation\",\n    \"succession\",\n    \"gaggle\",\n    \"clutch\"\n]', NULL, '{\n    \"复数\": \"batteries\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (5, 'competent', '{\"adj.\":\"有能力的;合格的;称职的;不错的;足以胜任的;有决定权的;尚好的\"}', '[\"六级\"]', NULL, '{\n    \"The most competent adults are those who know how to do this. \": \"最有能力的成年人是那些知道怎么做的人。\",\n    \"He\'s very competent in his work. \": \"他非常胜任自己的工作。\"\n}', '/ˈkɒmpɪtənt/', '/ˈkɑːmpɪtənt/', '[\"capable\", \"reasonable\" ,\"adequate\"]', NULL, '{\n    \"派生词\": \"competently\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (6, 'preserve', '{\n    \"vt.\": \"保存;保护;保留;维护;保鲜;保养;贮存;维持…的原状;使继续存活\",\n    \"n.\": \"果酱;泡菜;腌菜;(某人或群体活动、工作等的)专门领域;私人渔猎场（或保留地）\"\n}', '[\"六级\"]', NULL, '{\n    \"We need to preserve the forest \": \"我们需要保护森林。\",\n    \"Is he really 60? He\'s remarkably well preserved. \": \"他真有60岁了吗？他真会保养啊。\"\n}', '/prɪˈzɜːv/', '/prɪˈzɜːrv/', '[\n    \"maintain\",\n    \"conserve\",\n    \"store\"\n]', NULL, '{\n    \"第三人称单数\": \"preserves\",\n    \"现在分词\": \"preserving\",\n    \"过去式\": \"preserved\",\n    \"过去分词\": \"preserved\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (7, 'possession', '{\"n.\":\"具有;拥有;殖民地;个人财产;私人物品;持有违禁物;控球状态;鬼魂缠身\"}', '[\"六级\"]', NULL, '{\n    \"They were imprisoned for possession of drugs. \": \"他们因拥有毒品而被监禁。\",\n    \"The possession of a passport is essential for foreign travel. \": \"出国旅行必须持有护照。\"\n}', '/pəˈzeʃn/', '/pəˈzeʃn/', '[\n    \"estate\",\n    \"colony\"\n]', NULL, '{\n    \"复数\": \"possessions\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (8, 'wildfire', '{\n    \"n.\": \"野火\"\n}', '[\"六级\"]', NULL, NULL, '/ˈwaɪldfaɪə(r)/', '/ˈwaɪldfaɪər/', '[\"bush\", \"fire\"]', NULL, '{\n    \"复数\": \"wildfires\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (9, 'compact', '{\n    \"n.\": \"契约;协定;协议;合约;小汽车;带镜小粉盒\",\n    \"adj.\": \"紧凑的;小型的;紧密的;体积小的;袖珍的;坚实的;矮小而健壮的\",\n    \"vt.\": \"把…紧压在一起(或压实)\"\n}', '[\"六级\"]', NULL, '{\n    \"What Is the Basis of a New Social Compact? \": \"新社会契约的基础是什么？\",\n    \"He called for a new fiscal compact, or financial agreement. \": \"他呼吁出台一份新的财政契约或协议。\"\n}', '/kəmˈpækt , ˈkɒmpækt/', '/ˈkɑːmpækt , kəmˈpækt/', '[\r\n    \"diddy\",\r\n    \"pocket\",\r\n    \"miniature\"\r\n]', NULL, '{\n    \"第三人称单数\": \"compacts\",\n    \"现在分词\": \"compacting\",\n    \"过去式\": \"compacted\",\n    \"过去分词\":\"compacted\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (10, 'defy', '{\n    \"vt.\": \"违抗;藐视;反抗;不可能，无法(相信、解释、描绘等);蔑视;顶住;经受住;抗住\"\n}', '[\"六级\"]', NULL, '{\n    \"G Defy the park ranger. \": \"公然藐视国家公园管理员。\",\n    \"I defy anyone not to cry at the end of the film. \": \"我倒要看看有谁在电影结尾时不哭。\"\n}', '/dɪˈfaɪ/', '/dɪˈfaɪ/', '[\n    \"disobey\",\n    \"flout\"\n]', NULL, '{\n    \"第三人称单数\": \"defies\",\n    \"现在分词\": \"defying\",\n    \"过去式\": \"defied\",\n    \"过去分词\": \"defied\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (11, 'absolutely', '{\n    \"adv.\": \"(强调真实无误)绝对地，完全地;当然，对极了;极其\"\n}', '[\"六级\"]', NULL, '{\n    \"Only use your car when absolutely necessary. \": \"非用不可的时候再用你的汽车。\",\n    \"Go and wash your hands; they\'re absolutely black! \": \"洗洗手去，你的手脏极了！\"\n}', '/ˈæbsəluːtli/', '/ˈæbsəluːtli/', '[\n    \"certainly\",\n    \"surely\",\n    \"deeply\"\n]', NULL, NULL, '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (12, 'filter', '{\n    \"n.\": \"滤器;过滤器;滤光器;滤波器;筛选(过滤)程序;滤声器;分流指示灯\",\n    \"v.\":\"过滤;(用程序)筛选;渗入;慢慢传开;缓行;仅可左转行驶\"\n}', '[\"四级\",\"考研\",\"六级\"]', NULL, '{\n    \"Put the coffee in the filter and let the water run through. \": \"把咖啡放入过滤器里让水流过。\",\n    \"My secretary is very good at filtering my calls. \": \"我的秘书很会替我推掉不相干的电话。\"\n}', '/ˈfɪltə(r)/', '/ˈfɪltə(r)/', '[\n    \"leach\",\n    \"filtrate\"\n]', NULL, '{\n    \"第三人称单数\": \"filters\",\n    \"复数\":\"filters\",\n    \"现在分词\": \"filtering\",\n    \"过去式\": \" filtered\",\n    \"过去分词\": \"filtered\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (13, 'server', '{\n    \"n. \": \"服务器;侍者;发球者;助祭;上菜用具(往各人盘子里盛食物的叉、铲、勺等)\"\n}', '[\"六级\"]', NULL, '{\n    \"The server is designed to store huge amounts of data. \": \"该服务器是为存储大量数据设计的。\",\n    \"My server is having problems this morning. \": \"我的服务器今天早上出了问题。\"\n}', '/ˈsɜːvə(r)/', '/ˈsɜːrvər/', NULL, NULL, '{\"复数\":\"servers\"}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (14, 'spoil', '{\n    \"n.\": \"(开掘等时挖出的)弃土，废石方;战利品;赃物;掠夺物;成功所带来的好处;权力地位的连带利益\",\n    \"v.\": \"破坏;宠坏;溺爱;糟蹋;毁掉;变坏;娇惯;搞坏;善待;格外关照\"\n}', '[\"四级\",\"考研\",\"六级\"]', NULL, '{\n    \"I don\'t think it would spoil the dimensions of the room. \": \"我不认为它会破坏房间的大小。\",\n    \"Stop acting like spoilt children!  \": \"别再像惯坏的孩子那样胡闹了！\"\n}', '/spɔɪl/', '/spɔɪl/', '[\n    \"ruin\",\n    \"ravage\"\n]', NULL, '{\n    \"第三人称单数\": \"spoils\",\n    \"现在分词\": \"spoiling\",\n    \"过去式\": \" spoilt spoiled\",\n    \"过去分词\": \"spoilt spoiled\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (15, 'rustle', '{\n    \"n.\": \"轻轻的摩擦声;沙沙声\",\n    \"v.\": \"沙沙;偷窃(牲口);(使)发出轻轻的摩擦声\"\n}', '[\"六级\"]', NULL, '{\n    \"There was a rustle of paper as people turned the pages. \": \"人们翻动书页时发出沙沙的声响。\",\n    \"She\'s trying to rustle up some funding for the project. \": \"她正设法尽快为这个项目筹集一些资金。\"\n}', '/ˈrʌsl/', '/ˈrʌsl/', '[\n    \"nick\",\n    \"boost\"\n]', NULL, '{\n    \"第三人称单数\": \"rustles\",\n    \"复数\":\"rustles\",\n    \"现在分词\": \"rustling\",\n    \"过去式\": \"rustled\",\n    \"过去分词\": \"rustled\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (16, 'diversion', '{\n    \"n.\": \"转移;改道;转向;消遣;偏离;临时绕行路;临时支路;转移视线(或注意力)的事物\"\n}', '[\"六级\",\"考研\"]', NULL, '{\n    \"We had to turn back because of traffic diversion. \": \"由于车辆改道，我们只能返回。\",\n    \"he city is full of diversions. \": \"城市里各种娱乐活动比比皆是。\"\n}', '/daɪˈvɜːʃn/', '/daɪˈvɜːrʒn/', '[\n    \"relaxation\",\n    \"movement\"\n]', NULL, '{\n    \"复数\": \"diversions\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (17, 'complaint', '{\n    \"n.\": \"抱怨;投诉;不满;控告;(尤指不严重、常影响身体某部位的)疾病;埋怨;不满的原因\"\n}', '[\"六级\"]', NULL, '{\n    \"I can see no grounds for complaint. \": \"我看没理由抱怨。\",\n    \"You have no grounds for complaint. \": \"你没有理由抱怨。 \"\n}', '/kəmˈpleɪnt/', '/kəmˈpleɪnt/', '[\n    \"discontent\",\n    \"impeachment\"\n]', NULL, '{\n    \"复数\": \"complaints\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (18, 'merge', '{\n\n    \"v.\": \"合并;(使)结合;并入;融入;相融;渐渐消失在某物中\"\n}', '[\"六级\",\"考研\"]', NULL, '{\n    \"is department will merge with mine. \":\"他的部门将和我的合并。 \"\n}', '/mɜːdʒ/', '/mɜːrdʒ/', '[\n    \"coalesce\",\n    \"conflate\"\n]', NULL, '{\n    \"第三人称单数\": \" merges\",\n    \"现在分词\": \"merging\",\n    \"过去式\": \"merged\",\n    \"过去分词\": \"merged\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (19, 'priceless', '{\n    \"adj.\":\"无价的;极珍贵的;极重要的;极有趣的\"\n}', '[\"六级\"]', NULL, '{\n    \"Most give us a different priceless treasure& history. \": \"多数沉船提供给我们的是另一种无价的财富历史。\"\n}', '/ˈpraɪsləs/', '/ˈpraɪsləs/', NULL, NULL, NULL, '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (20, 'honorary', '{\n    \"adj.\":\"荣誉的;名誉的;义务的;无报酬的;被待作…成员的\",\n    \"n.\":\"名誉学位;名誉团体;获名誉学位者;同“honorarium”多数沉船提供给我们的是另一种无价的财富历史。\"\n}', '[\"六级\"]', NULL, '{\n    \"Many pass their honorary Cockney titles on from parent to child.\": \"许多小贩将这种象征荣誉的考克尼人头衔传给下一代。  \"\n}', '/ˈɒnərəri/', '/ˈɑːnəreri/', '[\n    \"unpaid\",\n    \"unwaged\"\n]', NULL, NULL, '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (21, 'chronic', '{\n    \"adj.\": \"慢性的;长期的;长期患病的;难以治愈(或根除)的;糟透的;拙劣的\",\n    \"n.\": \"慢性病人\"\n}', '[\"六级\"]', NULL, '{\n    \"The film was just chronic. \": \"这部电影简直糟透了。\",\n    \"He was suffering from chronic bronchitis. \": \"他患有慢性支气管炎.\"\n}', '/ˈkrɒnɪk/', '/ˈkrɑːnɪk/', '[\n    \"abysmal\",\n    \"deplorable\"\n]', NULL, '{\n    \"派生词\": \"chronically\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (22, 'extracurricular', '{\n \"adj.\": \"课外的;工作之余的\"\n}', '[\"六级\"]', NULL, '{\n \"Freedom to pursue extracurricular activities is totally unrestricted \": \"参加课外活动的自由完全不受限制。\"\n}', '/ˌekstrəkə\'rɪkjʊlə(r)/', '/ˌekstrəkə\'rɪkjələr/', NULL, NULL, NULL, '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (23, 'lobby', '{\n \"n.\": \"大堂;游说;(公共建筑物进口处的)门厅，前厅，大厅;(就某问题企图影响从政者的)游说团体;(英国议会的)民众接待厅\",\n \"v.\": \"游说(从政者或政府)\"\n}', '[\"六级\"]', NULL, '{\n \"Farmers will lobby Congress for higher subsidies. \": \"农民将游说国会提高对农业的补贴。\"\n}', '/ˈlɒbi/', '/ˈlɑːbi/', '[\"canvass\"]', NULL, '{\n \"第三人称单数\": \"lobbies\",\n \"复数\": \" lobbies\",\n \"现在分词\": \"lobbying\",\n \"过去式\": \"lobbied\",\n \"过去分词\": \"lobbied\",\n \"派生词\": \"lobbyist\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (24, 'inexhaustible', '{\n \"adj.\": \"取之不尽;用之不竭的;无穷无尽的\"\n}', '[\"六级\"]', NULL, '{\n \"Her energy is inexhaustible. \": \"她有无穷的精力。\",\n \"The wisdom of the masses is inexhaustible. \": \"群众的智慧是无穷的。\"\n}', '/ˌɪnɪɡˈzɔːstəbl/', '/ˌɪnɪɡˈzɔːstəbl/', NULL, NULL, NULL, '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (25, 'inevitable', '{\n \"adj.\": \"不可避免的;不可避免的事;惯常的;必然发生的事;不能防止的;总会发生的;照例必有的\",\n \"n.\": \"必然发生的事；不可避免的事\"\n}', '[\n    \"四级\",\n    \"考研\",\"六级\"\n]', NULL, '{\n \"Compromise is an inevitable part of life. \": \"妥协是生活不可避免的一部分。\",\n \"A rise in the interest rates seems inevitable. \": \"提高利率似乎是不可避免的。\"\n}', '/ɪnˈevɪtəbl/', '/ɪnˈevɪtəbl/', '[\n    \"ineluctable\",\n    \"inescapable\"\n]', NULL, '{\n    \"派生词\": \"inevitability\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (26, 'contamination', '{\n \"n.\": \"污染;污秽;(文章,故事等的)混合;(语言的)交感\"\n}', '[\"六级\"]', NULL, '{\n \"By bathing in unclean water, they expose themselves to contamination. \": \"在不干净的水中洗澡，他们可能会受到感染。\"\n}', '/kənˌtæmɪˈneɪʃn/', '/kənˌtæmɪˈneɪʃn/', '[\"pollution\" ,\"filth\"]', NULL, NULL, '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (27, 'suspend', '{\n \"vt.\": \"暂停;悬浮;中止;悬;吊;挂;暂缓;推迟;延缓;使暂时停职（或停学等）;使暂停发挥作用(或使用等)\"\n}', '[\"六级\"]', NULL, '{\n \"Her body was found suspended by a rope. \": \"人们发现她的尸体吊在绳子上。\"\n}', '/səˈspend/', '/səˈspend/', '[\"hang\", \"dangle\"]', NULL, '{\n \"第三人称单数\": \"suspends\",\n \"现在分词\": \"suspending\",\n \"过去式\": \"suspended\",\n \"过去分词\": \"suspended\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (28, 'imminent', '{\n \"adj.\": \"迫在眉睫的;即将发生的;临近的\"\n}', '[\"六级\"]', NULL, '{\n \"he system is in imminent danger of collapse. \": \"这个体制面临着崩溃的危险。\"\n}', '/ˈɪmɪnənt/', '/ˈɪmɪnənt/', '[\"prospective\"]', NULL, '{\n \"派生词\": \"imminence\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (29, 'obesity', '{\n \"n.\": \"（过度）肥胖;<医>肥胖（症）\"\n}', '[\"六级\"]', NULL, '{\n \"Obesity can increase the risk of heart disease. \": \"肥胖会增加患心脏病的危险。\"\n}', '/əʊˈbiːsɪti/', '/oʊˈbisəti/', NULL, NULL, NULL, '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (30, 'aid', '{\n \"n.\": \"帮助;援助;(完成某工作所需的)助手;辅助设备;救援物资;辅助物;援助款项\",\n \"v.\": \"援助;帮助\"\n}', '[\"六级\"]', NULL, '{\n \"They made a request for further aid. \": \"他们要求再给一些帮助。\"\n}', '/eɪd/', '/eɪd/', '[\"help \",\"assist\"]', NULL, '{\n \"第三人称单数\": \" aids\",\n \"现在分词\": \" aids\",\n \"过去式\": \"aided\",\n \"过去分词\": \"aided\",\n \"复数\": \"aids\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (31, 'solution', '{\n \"n.\": \"解决方案;溶液;解;解决办法;溶解（过程）;答案;谜底;处理手段\"\n}', '[\n    \"四级\",\n    \"考研\",\"六级\"\n]', NULL, '{\n \"It was recognized that this solution could only be temporary. \": \"人们意识到这只是个临时的解决方案。\"\n}', '/səˈluːʃn/', '/səˈluːʃn/', '[\"key\", \"answer\"]', NULL, '{\n \"复数\": \"solutions\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (32, 'tuition', '{\n \"n.\": \"(尤指大专院校的)学费;(尤指对个人或小组的)教学，讲授，指导\"\n}', '[\"六级\"]', NULL, '{\n \"Your parents will have to cover your tuition fees. \": \"你的父母得支付你的学费。\"\n}', '/tjuˈɪʃn/', '/tuˈɪʃn/', NULL, NULL, NULL, '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (33, 'esteem', '{\n \"n.\": \"尊重;敬重;好评\",\n \"vt.\": \"尊重;认为;敬重;把…看作\"\n}', '[\"六级\",\"考研\"]', NULL, '{\n \"I esteem his work highly. \": \"我非常尊重他的工作。\"\n}', '/ɪˈstiːm/', '/ɪˈstiːm/', '[\n    \"respect\",\n    \"admiration\"\n]', NULL, '{\n \"第三人称单数\": \"esteems\",\n \"现在分词\": \"esteeming\",\n \"过去式\": \"esteemed\",\n \"过去分词\": \"esteemed\",\n \"复数\": \"esteems\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (34, 'purchase', '{\n \"n.\": \"购买;采购;购买项目;购买的东西;抓牢;握紧;蹬稳\",\n \"vt.\": \"购买;采购;买\"\n}', '[\n    \"四级\",\n    \"考研\",\"六级\"\n]', NULL, '{\n \"It was decided the school should purchase new software. \": \"已经决定学校要购买新软件。\"\n}', '/ˈpɜːtʃəs/', '/ˈpɜːrtʃəs/', '[\n    \"buy\",\n    \"get\"\n]', NULL, '{\n \"第三人称单数\": \"purchases\",\n \"现在分词\": \"purchasing\",\n \"过去式\": \"purchased\",\n \"过去分词\": \"purchased\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (35, 'incorporate', '{\n \"adj.\": \"无形(体)的;具体化了的;法定组织的;精神上的\",\n \"vt.\": \"使并入;包含;合并;注册成立;吸收;将…包括在内\"\n}', '[\"六级\",\"考研\"]', NULL, '{\n \"We had to incorporate the company for tax reasons. \": \"由于纳税的原因，我们不得不把那家公司合并了。\"\n}', '/ɪnˈkɔːpəreɪt/', '/ɪnˈkɔːrpəreɪt/', '[\n    \"involve\",\n    \"contain\"\n]', NULL, '{\n \"第三人称单数\": \"incorporates\",\n \"现在分词\": \"incorporating\",\n \"过去式\": \"incorporated\",\n \"过去分词\": \" incorporated\",\n \"派生词\": \"incorporation\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (36, 'prevail', '{\n \"vi.\": \"盛行;流行;普遍存在;(尤指长时间斗争后)战胜，挫败;战胜;被接受;压倒\"\n}', '[\n    \"四级\",\n    \"考研\",\"六级\"\n]', NULL, '{\n \"Different doctrines prevail in different times. \": \"不同时期流行不同的学说。\"\n}', '/prɪˈveɪl/', '/prɪˈveɪl/', '[\n    \"win\",\n    \"conquer\"\n]', NULL, '{\n \"第三人称单数\": \"prevails\",\n \"现在分词\": \"prevailing\",\n \"过去式\": \"prevailed\",\n \"过去分词\": \"prevailed\",\n \"派生词\": \"incorporation\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (37, 'irrational', '{\n \"adj.\": \"不合理的;不合逻辑的;没有道理的\",\n \"n.\": \"无理数;无理性的生物\"\n}', '[\"六级\"]', NULL, '{\n \"The irrational rules must be changed. \": \"不合理的规定必须加以改革。\"\n}', '/ɪˈræʃənl/', '/ɪˈræʃənl/', '[\n    \"unwarranted\", \"immoderate\"\n]', NULL, '{\n \"复数\": \"irrationals\",\n \"派生词\": \"irrationality\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (38, 'inherent', '{\n \"adj.\": \"固有的;内在的\"\n}', '[\"六级\",\"考研\"]', NULL, '{\n \"Violence is inherent in our society. \": \"在我们的社会中暴力是难免的。\",\n \"Stress is an inherent part of dieting.\": \"节食必定会带来压力。\"\n}', '/ɪnˈherənt/', '/ɪnˈhɪrənt/', '[\"intrinsic\", \"immanent\"]', NULL, '{\n \"派生词\": \"inherently\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (39, 'overexcited', '{\n \"adj.\": \"过度兴奋的;兴奋得忘乎所以的\",\n \"v.\": \"使过于激动(或兴奋）；过度刺激\"\n}', '[\"六级\"]', NULL, '{\n \"I know she got a little overexcited \": \"她有点兴奋过头\"\n}', '/ˌəʊvərɪkˈsaɪtɪd/', '/ˌoʊvərɪkˈsaɪtɪd/', NULL, NULL, NULL, '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (40, 'preference', '{\n \"n.\": \"偏爱;喜爱;爱好;偏爱的事物;最喜爱的东西\"\n}', '[\"六级\"]', NULL, '{\n \"I have a preference for country life. \": \"我偏爱乡村生活。\"\n}', '/ˈprefrəns/', '/ˈprefrəns/', '[\n    \"bias\",\n    \"aptitude\"\n]', NULL, '{\n    \"复数\":\"preferences\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (41, 'evolution', '{\n    \"n.\": \"演变;进化;发展;渐进\"\n}', '[\n    \"四级\",\n    \"考研\",\"六级\"\n]', NULL, '{\n    \"His long life comprised a series of evolutions.\": \"他漫长的一生包括一系列的发展变化。\"\n}', '/ˌiːvəˈluːʃn/', '/ˌiːvəˈluːʃn/', '[\n    \"development\",\n    \"advance\"\n]', NULL, '{\n    \"复数\": \"evolutions\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (42, 'exotic', '{\n \"adj.\": \"异国情调的;奇异的;异国风味的;来自异国(尤指热带国家)的\",\n \"n.\": \"舶来品，外来物，外来品种;外来语\"\n}', '[\"六级\",\"考研\"]', NULL, '{\"Exotic pets are the in thing right now.\" \n:\n\"奇异的宠物眼下很时髦。\"}', '/ɪɡˈzɒtɪk/', '/ɪɡˈzɑːtɪk/', '[\n    \"alien\",\n    \"foreign\"\n]', NULL, NULL, '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (43, 'stockbroker', '{\"n.\":\n\"股票经纪人;证券(或股票)经纪人;证券(或股票)经纪商\"}', '[\"六级\"]', NULL, '{\"Before you go near a stockbroker, do your homework.\" \n:\n\"找股票经纪人之前，先要做好必要的准备工作。\"}', '/ˈstɒkbrəʊkə(r)/', '/ˈstɑːkbroʊkər/', NULL, NULL, '{\n    \"复数\": \"stockbrokers\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (44, 'misinterpret', '{\"vt.\":\n\"误解;曲解;误释\"}', '[\"六级\"]', NULL, '{\"It would be easy to misinterpret results from such a small sample.\" \n:\n\"从这样小的一份抽样出发容易得出曲解的结论。\"}', '/ˌmɪsɪnˈtɜːprət/', '/ˌmɪsɪnˈtɜːrprət/', '[\n    \"misunderstand\",\n    \"misconceive\"\n]', NULL, '{\n \"第三人称单数\": \"misinterprets\",\n \"现在分词\": \"misinterpreting\",\n \"过去式\": \" misinterpreted\",\n \"过去分词\": \"misinterpreted\",\n \"派生词\": \"misinterpretation\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (45, 'prosperity', '{\"n.\":\n\"繁荣;成功;兴旺;昌盛\"}', '[\n    \"四级\",\n    \"考研\",\"六级\"\n]', NULL, '{\"Everywhere is a scene of prosperity.\": \n\"到处呈现一片兴旺景象。\"}', '/prɒˈsperəti/', '/prɑːˈsperəti/', '[\n    \"upbeat\",\n    \"joy\"\n]', NULL, '{\n    \"复数\": \"prosperities\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (46, 'tank', '{\n \"v.\": \"(尤指故意)输掉(比赛);倒闭;破产;彻底失败\",\n \"n.\": \"(贮放液体或气体的)箱，槽，罐;坦克;（人工）水池，湖，水库;箱(或桶等)所装之物;一箱(或一桶等)的量\"\n}', '[\n    \"四级\",\n    \"考研\",\"六级\"\n]', NULL, '{\n \"I fill up the tank with gasoline about once a week. \": \"大约每个星期我加满一箱汽油。\",\n \"The tank had leaked a small amount of water. \": \"水箱渗漏出少量的水。\"\n}', '/tæŋk/', '/tæŋk/', NULL, NULL, '{\n \"第三人称单数\": \"tanks\",\n \"现在分词\": \"tanking\",\n \"过去式\": \"tanked\",\n \"过去分词\": \"tanked\",\n \"复数\": \"tanks\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (47, 'outdated', '{\n \"v.\": \"使过时；使落伍；使陈旧\",\n \"adj.\": \"过时的;陈旧的\"\n}', '[\"六级\"]', NULL, '{\"These figures are now outdated.\" \n:\n\"这些数字现在已经过时。\"}', '/ˌaʊtˈdeɪtɪd/', '/ˌaʊtˈdeɪtɪd/', NULL, NULL, NULL, '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (48, 'outnumber', '{\"vt.\":\n\"多于;(在数量上)压倒，比…多\"}', '[\"六级\"]', NULL, '{\"Men still outnumber women in the paid workforce.\" \n:\n\"在上班挣钱的人口中，男性仍然多于女性。\"}', '/ˌaʊtˈnʌmbə(r)/', '/ˌaʊtˈnʌmbər/', NULL, NULL, '{\n \"第三人称单数\": \"outnumbers\",\n \"现在分词\": \"outnumbering\",\n \"过去式\": \"outnumbered\",\n \"过去分词\": \"outnumbered\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (49, 'supervisor', '{\"n.\":\n\"监督人;主管人;指导者\"}', '[\"六级\"]', NULL, '{\"I have a meeting with my supervisor about my research topic.\" \n:\n\"我要就我的研究课题同导师见一次面。\"}', '/ˈsuːpəvaɪzə(r)/', '/ˈsuːpərvaɪzər/', '[\n    \"taskmaster\",\n    \"superintendent\",\n    \"guide\"\n]', NULL, '{\n \"复数\": \"supervisors\",\n \"派生词\": \"supervisory \"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (50, 'fluctuate', '{\"vi.\":\n\"(大小、数量、质量等)波动;(在…之间)起伏不定\"}', '[\"六级\",\"考研\"]', NULL, '{\"Body temperature can fluctuate if you are ill. \"\n:\n\"人患病后体温可能会上下波动。\"}', '/ˈflʌktʃueɪt/', '/ˈflʌktʃueɪt/', '[\n    \"oscillate\",\n    \"undulate\"\n]', NULL, '{\n \"第三人称单数\": \"fluctuates\",\n \"现在分词\": \"fluctuating\",\n \"过去式\": \" fluctuated\",\n \"过去分词\": \" fluctuated\",\n \"派生词\": \"fluctuation\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');
INSERT INTO `tb_word` VALUES (51, 'substance', '{\"n.\":\n\"物质;实质;根据;物品;东西;重要性;主旨;要点;基本内容;事实基础\"}', '[\n    \"四级\",\n    \"考研\",\"六级\"\n]', NULL, '{\"The substance polymerizes to form a hard plastic.\" \n:\n\"这种物质聚合形成坚硬的塑料。\"}', '/ˈsʌbstəns/', '/ˈsʌbstəns/', '[\n    \"material\",\n    \"matter\"\n]', NULL, '{\n    \"复数\": \"substances\"\n}', '2024-04-14 01:50:38', '2024-04-14 01:50:38', b'0');

-- ----------------------------
-- Table structure for tb_word_answer
-- ----------------------------
DROP TABLE IF EXISTS `tb_word_answer`;
CREATE TABLE `tb_word_answer`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '答题记录id',
  `word_id` bigint NOT NULL COMMENT '单词id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `points` bigint NOT NULL DEFAULT 0 COMMENT '积分',
  `is_true` tinyint NOT NULL DEFAULT 0 COMMENT '是否正确(0:正确,1:不正确)',
  `error_cause` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误原因',
  `is_show` tinyint NOT NULL COMMENT '是否展示(0:展示，1：不展示',
  `status` tinyint NULL DEFAULT NULL COMMENT '状态(0：未纠错，1：已纠错）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_word_answer
-- ----------------------------

-- ----------------------------
-- Table structure for tb_word_type
-- ----------------------------
DROP TABLE IF EXISTS `tb_word_type`;
CREATE TABLE `tb_word_type`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '单词类型id',
  `type_group_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型组名',
  `type_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型名',
  `is_group_name` tinyint NOT NULL DEFAULT 0 COMMENT '是否为类型组名（0：不是，1:是）',
  `type_group_id` bigint NULL DEFAULT NULL COMMENT '属于类型组id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_word_type
-- ----------------------------
INSERT INTO `tb_word_type` VALUES (1, '英语等级', NULL, 1, NULL, '2024-04-14 01:56:00', '2024-04-14 01:56:00', 0);
INSERT INTO `tb_word_type` VALUES (2, NULL, '四级', 0, 1, '2024-04-14 01:57:30', '2024-04-14 01:57:30', 0);
INSERT INTO `tb_word_type` VALUES (3, NULL, '六级', 0, 1, '2024-04-14 01:57:35', '2024-04-14 01:57:35', 0);
INSERT INTO `tb_word_type` VALUES (4, NULL, '考研', 0, 1, '2024-04-14 01:57:43', '2024-04-14 01:57:43', 0);
INSERT INTO `tb_word_type` VALUES (5, '专业', NULL, 1, NULL, '2024-04-14 01:58:37', '2024-04-14 01:58:37', 0);
INSERT INTO `tb_word_type` VALUES (6, NULL, '计算机类', 0, 5, '2024-04-14 01:58:58', '2024-04-14 01:58:58', 0);
INSERT INTO `tb_word_type` VALUES (7, NULL, '医学类', 0, 5, '2024-04-14 01:59:04', '2024-04-14 01:59:04', 0);
INSERT INTO `tb_word_type` VALUES (8, '功能', NULL, 1, NULL, '2024-04-14 02:03:11', '2024-04-14 02:03:11', 1);
INSERT INTO `tb_word_type` VALUES (9, NULL, '托福', 0, 1, '2024-04-14 02:03:29', '2024-04-14 02:03:29', 0);

SET FOREIGN_KEY_CHECKS = 1;
