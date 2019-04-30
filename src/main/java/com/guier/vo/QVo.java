package com.guier.vo;

import com.guier.pojo.Question;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QVo {
    private Question question;
    private double tfidf;
}
