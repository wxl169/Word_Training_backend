package org.wxl.wordTraining.service;

import cn.hutool.core.io.resource.ClassPathResource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.bytebuddy.description.method.MethodDescription;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.wxl.wordTraining.model.entity.Word;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SpringBootTest
public class TimeService {
@Resource
private IWordService wordService;

    public boolean isSameDay(LocalDate date1, LocalDate date2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date1.format(formatter).equals(date2.format(formatter));
    }

    @Test
    public void test1(){
        LocalDateTime a = LocalDateTime.of(2024, 3, 21, 24, 00);
        LocalDateTime b = LocalDateTime.now();
        System.out.println(isSameDay(LocalDate.now(), LocalDate.from(a)));
    }


    @Test
    public void test2() {
        Random random = new Random();
        // 生成随机数，范围是0到3
        for(int i = 0 ; i < 10 ; i++){
            int randomNumber = random.nextInt(4);
            System.out.println("随机数是：" + randomNumber);
        }

    }

//    @Test
//    public void test2(){
//        Word byId = wordService.getById(1);
//        String translation = byId.getTranslation();
//        System.out.println(translation);
//        Gson gson = new Gson();
//        Map<String, String> stringStringMap = gson.fromJson(translation, new TypeToken<Map<String, String>>() {});
//        for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }
//    }

//        @Test
//    public void test3(){
//        Word byId = wordService.getById(1);
//        String example = byId.getExample();
//        System.out.println(example);
//        Gson gson = new Gson();
//        Map<String, String[]> stringStringMap = gson.fromJson(example, new TypeToken<Map<String, String[]>>() {});
//            for (Map.Entry<String, String[]> entry : stringStringMap.entrySet()) {
//                String key = entry.getKey();
//                String[] values = entry.getValue();
//                System.out.println("Key: " + key);
//                System.out.println("Values:");
//                for (String value : values) {
//                    System.out.println("  " + value);
//                }
//            }
//    }

    @Test
    public void test4(){
        Word byId = wordService.getById(3);
        String translation = byId.getTranslation();
//        System.out.println(translation);

        Gson gson = new Gson();
        Map<String,String> translationList = gson.fromJson(translation, new TypeToken<Map<String,String>>() {
        }.getType());

        for (Map.Entry<String,String> entry: translationList.entrySet()){
            String key = entry.getKey();
            String values = entry.getValue();
            System.out.println(key + ":" + values);
        }

    }


    @Test
    public void test5(){
        System.out.println(LocalDateTime.now().getDayOfMonth() );
    }

}
