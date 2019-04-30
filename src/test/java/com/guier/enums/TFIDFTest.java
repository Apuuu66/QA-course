package com.guier.enums;

import com.guier.pojo.Question;
import com.guier.tfidf.TFIDF;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TFIDFTest {

    @Test
    public void call() throws IOException {
        TFIDF tfidf = new TFIDF();

        List<Question> questions = Arrays.asList(
                new Question(1, "为什么男装纽扣在右，而女装纽扣在左？", "女士衬衣上的扣子钉在左边，极大地方便了伺候女主人的仆人们(现在方便男人脱)。"),
                new Question(2, "为什么是上厕所、下厨房？", ""),
                new Question(3, "俗语为什么是不三不四？", ""),
                new Question(1, "为什么男装纽扣在右？", "女士衬衣上"));

        tfidf.call("为什么男装纽扣在右，而女装纽扣在左？", questions);
        tfidf.getQueue().forEach(System.out::println);
    }
}