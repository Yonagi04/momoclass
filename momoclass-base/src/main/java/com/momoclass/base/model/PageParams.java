package com.momoclass.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PageParams {
    // 页码
    @ApiModelProperty("当前页码")
    private Long pageNo = 1L;
    // 每页记录数
    @ApiModelProperty("每页记录数")
    private Long pageSize = 10L;

    public PageParams() {
    }

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
