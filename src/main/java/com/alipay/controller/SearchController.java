package com.alipay.controller;

import com.alipay.pojo.Result;
import com.alipay.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping("/list")
@Slf4j
public class SearchController {
    @Resource
    private SearchService searchService;

    /**
     * 搜索
     * @param queryString
     * @param price
     * @param page
     * @return
     */
    @RequestMapping
    public String query(String queryString, String price, Integer page, Model model){
        log.info("query({}, {}, {}, {})", queryString, price, page, model);
        //处理当前页
        if(page == null || page <= 0){
            page = 1;
        }

        //调用service查询
        Result<?> result = null;
        try {
            result = searchService.query(queryString, price, page);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return null;
        }
        model.addAttribute("result", result);

        //查询条件回显
        model.addAttribute("queryString", queryString);
        model.addAttribute("price", price);
        model.addAttribute("page", page);
        return "search";
    }
}
