package com.yuan.answer.controller;

import com.yuan.answer.common.BaseResponse;
import com.yuan.answer.common.ErrorCode;
import com.yuan.answer.common.ResultUtils;
import com.yuan.answer.exception.ThrowUtils;
import com.yuan.answer.mapper.UserAnswerMapper;
import com.yuan.answer.model.dto.statistic.AppAnswerCountDTO;
import com.yuan.answer.model.dto.statistic.AppAnswerResultCountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * App统计分析接口
 *
 * @author <a href="https://github.com/zcyg0306">Garfield</a>
 * @from <a href="https://github.com/zcyg0306">GitHub</a>
 */
@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class AppStatisticController {

    @Resource
    private UserAnswerMapper userAnswerMapper;


    /**
     * 热门应用及回答数统计（top 10）
     *
     * @return
     */
    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    /**
     * 某应用回答结果分布统计
     *
     * @param appId
     * @return
     */
    @GetMapping("/answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }

}
