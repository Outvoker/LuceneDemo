package com.alipay.service.impl;

import com.alipay.pojo.Result;
import com.alipay.pojo.Sku;
import com.alipay.service.SearchService;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    public final static Integer PAGE_SIZE = 20;

    @Override
    public Result<?> query(String queryString, String price, Integer page) throws ParseException, IOException {
        //1. 需要使用的对象封装
        Result<Sku> result = new Result<>();
        //从第几条开始查询
        int start = (page - 1) * PAGE_SIZE;
        //查询多少条
        int end = page * PAGE_SIZE;
        //创建分词器
        Analyzer analyzer = new IKAnalyzer();
        //创建组合查询对象
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        //2. 根据查询关键字封装对象
        QueryParser queryParser = new QueryParser("name", analyzer);
        Query query1 = null;
        if(StringUtils.isEmpty(queryString)){
            query1 = queryParser.parse("*:*");
        }else{
            query1 = queryParser.parse(queryString);
        }
        builder.add(query1, BooleanClause.Occur.MUST);

        //3. 根据价格范围封装查询对象
        if(!StringUtils.isEmpty(price)){
            String[] split = price.split("-");
            Query query2 = IntPoint.newRangeQuery("price", Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            builder.add(query2, BooleanClause.Occur.MUST);
        }

        //4. 创建Directory目录对象，指定索引库的位置
        Directory dir = FSDirectory.open(Paths.get("/Users/linglan/ali/lucene/dir"));
        //5. 创建输入流对象
        IndexReader reader = DirectoryReader.open(dir);
        //6. 创建搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //7. 搜索并获取搜索结果
        TopDocs topDocs = indexSearcher.search(builder.build(), end);
        //8. 获取查询到的总条数
        result.setRecordCount(topDocs.totalHits);
        //9. 获取查询到的结果集
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        //10. 遍历结果集封装返回的数据
        List<Sku> list = new ArrayList<>();
        if(scoreDocs != null){
            for (int i = start; i < end; i++) {
                Document document = reader.document(scoreDocs[i].doc);
                //封装sku
                Sku sku = new Sku();
                sku.setId(document.get("id"));
                sku.setPrice(Integer.valueOf(document.get("price")));
                sku.setImage(document.get("image"));
                sku.setName(document.get("name"));
                sku.setBrandName(document.get("categoryName"));

                list.add(sku);
            }
        }

        //封装result
        result.setList(list);
        result.setCurPage((long) page);
        Long pageCount = topDocs.totalHits % PAGE_SIZE > 0 ? topDocs.totalHits / PAGE_SIZE + 1 : topDocs.totalHits / PAGE_SIZE;
        result.setPageCount(pageCount);
        return result;
    }
}
