package com.yuan.answer.model.dto.question;

import lombok.Data;

/**
 * 题目答案封装类（用户AI评分）
 *
 * @author <a href="https://github.com/zcyg0306">Garfield</a>
 * @from <a href="https://github.com/zcyg0306">GitHub</a>
 */
@Data
public class QuestionAnswerDTO {
    /**
     * 题目
     */
    private String title;

    /**
     * 用户答案
     */
    private String userAnswer;

}
