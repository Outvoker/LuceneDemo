package com.alipay.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Result<T> {
    private List<T> list;
    private Long recordCount;
    private Long pageCount;
    private Long curPage;
}
