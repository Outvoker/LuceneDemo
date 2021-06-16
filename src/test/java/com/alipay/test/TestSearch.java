package com.alipay.test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 测试搜索过程
 */
public class TestSearch {
    @Test
    public void testSearch(){
        //1. 创建分词器（对搜索的关键词进行分词使用） 注意：分词器要和创建索引的使用的分词器一模一样
        Analyzer analyzer = new StandardAnalyzer();
        //2. 创建查询对象
        QueryParser queryParser = new QueryParser("name", analyzer);
        //3. 设置搜索关键词
        Query query = null;
        try {
            query = queryParser.parse("id:100000003145");
//        queryParser.parse("brandName:华为手机");
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        IndexReader indexReader= null;
        try {
            //4. 创建Directory目录对象，指定索引库位置
            Directory dir = FSDirectory.open(Paths.get("/Users/linglan/ali/lucene/dir"));
            //5. 创建输入流对象
            indexReader = DirectoryReader.open(dir);
            //6. 创建搜索对象
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            //7. 搜索，并返回结果
            TopDocs topDocs = indexSearcher.search(query, 10);

            //获取查询到的结果集的总数
            System.out.println("totalHits: " + topDocs.totalHits);
            //8. 获取结果集
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            //9. 遍历结果集
            if(scoreDocs != null){
                System.out.println("scoreDoc.length: " + scoreDocs.length);
                for (ScoreDoc scoreDoc : scoreDocs) {
                    //获取查询到的文档的唯一标识，文档id，这个id是lucene在创建文档的时候自动分配的
                    int docID = scoreDoc.doc;
                    //通过文档id，读取文档
                    Document doc = indexSearcher.doc(docID);
                    System.out.println("=========================================");
                    //通过域名，从文档中获取域值
                    System.out.println("id: " + doc.get("id"));
                    System.out.println("name: " + doc.get("name"));
                    System.out.println("num: " + doc.get("num"));
                    System.out.println("image: " + doc.get("image"));
                    System.out.println("categoryName: " + doc.get("categoryName"));
                    System.out.println("brandName: " + doc.get("brandName"));
                    System.out.println("spec: " + doc.get("spec"));
                    System.out.println("saleNum: " + doc.get("saleNum"));

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //10. 释放资源
            if(indexReader != null) {
                try {
                    indexReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testRangeSearch(){
        Query query = IntPoint.newRangeQuery("price", 100, 1000);

        IndexReader indexReader= null;
        try {
            //4. 创建Directory目录对象，指定索引库位置
            Directory dir = FSDirectory.open(Paths.get("/Users/linglan/ali/lucene/dir"));
            //5. 创建输入流对象
            indexReader = DirectoryReader.open(dir);
            //6. 创建搜索对象
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            //7. 搜索，并返回结果
            TopDocs topDocs = indexSearcher.search(query, 10);

            //获取查询到的结果集的总数
            System.out.println("totalHits: " + topDocs.totalHits);
            //8. 获取结果集
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            //9. 遍历结果集
            if(scoreDocs != null){
                System.out.println("scoreDoc.length: " + scoreDocs.length);
                for (ScoreDoc scoreDoc : scoreDocs) {
                    //获取查询到的文档的唯一标识，文档id，这个id是lucene在创建文档的时候自动分配的
                    int docID = scoreDoc.doc;
                    //通过文档id，读取文档
                    Document doc = indexSearcher.doc(docID);
                    System.out.println("=========================================");
                    //通过域名，从文档中获取域值
                    System.out.println("id: " + doc.get("id"));
                    System.out.println("name: " + doc.get("name"));
                    System.out.println("num: " + doc.get("num"));
                    System.out.println("image: " + doc.get("image"));
                    System.out.println("categoryName: " + doc.get("categoryName"));
                    System.out.println("brandName: " + doc.get("brandName"));
                    System.out.println("spec: " + doc.get("spec"));
                    System.out.println("saleNum: " + doc.get("saleNum"));

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //10. 释放资源
            if(indexReader != null) {
                try {
                    indexReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testBooleanSearch(){
        //组合查询1
        Query query1 = IntPoint.newRangeQuery("price", 100, 1000);

        //1. 创建分词器（对搜索的关键词进行分词使用） 注意：分词器要和创建索引的使用的分词器一模一样
        Analyzer analyzer = new IKAnalyzer();
        //2. 创建查询对象
        QueryParser queryParser = new QueryParser("name", analyzer);
        //3. 设置搜索关键词
        Query query2 = null;
        try {
            query2 = queryParser.parse("华为手机");
//        queryParser.parse("brandName:华为手机");
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        BooleanQuery.Builder query = new BooleanQuery.Builder();

        query.add(query1, BooleanClause.Occur.MUST_NOT);
        query.add(query2, BooleanClause.Occur.MUST);

        IndexReader indexReader= null;
        try {
            //4. 创建Directory目录对象，指定索引库位置
            Directory dir = FSDirectory.open(Paths.get("/Users/linglan/ali/lucene/dir"));
            //5. 创建输入流对象
            indexReader = DirectoryReader.open(dir);
            //6. 创建搜索对象
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            //7. 搜索，并返回结果
            TopDocs topDocs = indexSearcher.search(query.build(), 10);

            //获取查询到的结果集的总数
            System.out.println("totalHits: " + topDocs.totalHits);
            //8. 获取结果集
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            //9. 遍历结果集
            if(scoreDocs != null){
                System.out.println("scoreDoc.length: " + scoreDocs.length);
                for (ScoreDoc scoreDoc : scoreDocs) {
                    //获取查询到的文档的唯一标识，文档id，这个id是lucene在创建文档的时候自动分配的
                    int docID = scoreDoc.doc;
                    //通过文档id，读取文档
                    Document doc = indexSearcher.doc(docID);
                    System.out.println("=========================================");
                    //通过域名，从文档中获取域值
                    System.out.println("id: " + doc.get("id"));
                    System.out.println("name: " + doc.get("name"));
                    System.out.println("num: " + doc.get("num"));
                    System.out.println("image: " + doc.get("image"));
                    System.out.println("categoryName: " + doc.get("categoryName"));
                    System.out.println("brandName: " + doc.get("brandName"));
                    System.out.println("spec: " + doc.get("spec"));
                    System.out.println("saleNum: " + doc.get("saleNum"));

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //10. 释放资源
            if(indexReader != null) {
                try {
                    indexReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
