package org.wxl.wordTraining.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wxl.wordTraining.constant.UserConstant;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.service.UserService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 重置每个用户的挑战次数
 * @author wxl
 */
@Component
@Slf4j
public class ResetNumberJob {
    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Scheduled(cron = "0 0 0 * * *")
    public void resetNumber(){
        RLock lock = redissonClient.getLock("wordTraining:ResetNumberJob:resetNumber:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
               //修改所有用户的挑战次数
                LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(User::getChallengeNum, UserConstant.CHALLENGE_NUMBER)
                        .ne(User::getChallengeNum, UserConstant.CHALLENGE_NUMBER)
                        .ne(User::getRole, UserConstant.BAN_ROLE);
                boolean update = userService.update(updateWrapper);
                if (!update){
                    log.info("当前时间: " + LocalDateTime.now() + "更新挑战次数失败");
                }
                log.info("当前时间: " + LocalDateTime.now() + "更新挑战次数成功");
            }
        } catch (InterruptedException e) {
            log.error("doResetNumber error", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}


