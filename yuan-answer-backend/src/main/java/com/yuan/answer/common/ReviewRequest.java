package com.yuan.answer.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 审核请求
 *
 * @author <a href="https://github.com/zcyg0306">Garfield</a>
 * @from <a href="https://github.com/zcyg0306">GitHub</a>
 */
@Data
public class ReviewRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 审核状态 0-未审核 1-审核通过 2-审核不通过
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    private static final long serialVersionUID = 1L;
}