package com.alipay.service;

import com.alipay.pojo.Result;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.ui.Model;

import java.io.IOException;

public interface SearchService {
    Result<?> query(String queryString, String price, Integer page) throws ParseException, IOException;
}
