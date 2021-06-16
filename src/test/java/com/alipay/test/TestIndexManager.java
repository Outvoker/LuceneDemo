package com.alipay.test;

import com.alipay.dao.SkuDao;
import com.alipay.dao.impl.SkuDaoImpl;
import com.alipay.pojo.Host;
import com.alipay.pojo.Sku;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 索引库维护
 */
public class TestIndexManager {
    private final static Host host = new Host();

    @Test
    public void test(){


    }

    @Test
    public void testHost() {
        System.out.println(host.getHostAddress());
        System.out.println(host.getHostName());
    }
    /**
     * 创建索引库
     */
    @Test
    public void createIndexTest(){
        //1. 采集数据
        SkuDao skuDao = new SkuDaoImpl();
        List<Sku> list = skuDao.querylist();

        List<Document> docList = new ArrayList<>();
        //2. 创建文档对象
        for (Sku sku : list) {
            Document document = new Document();

            //创建域对象
            document.add(new StringField("id", sku.getId(), Field.Store.YES));
            document.add(new TextField("name", sku.getName(), Field.Store.YES));
            document.add(new IntPoint("price", sku.getPrice()));
            document.add(new StoredField("price", sku.getPrice()));
            document.add(new IntPoint("num", sku.getNum()));
            document.add(new StoredField("num", sku.getNum()));
            document.add(new StoredField("image", sku.getImage()));
            document.add(new StringField("categoryName", sku.getCategoryName(), Field.Store.YES));
            document.add(new StringField("brandName", sku.getBrandName(), Field.Store.YES));
            document.add(new TextField("spec", sku.getSpec(), Field.Store.YES));
            document.add(new IntPoint("saleNum", sku.getSaleNum()));
            document.add(new StoredField("saleNum", sku.getSaleNum()));

            docList.add(document);
        }
        //3. 创建分词器
        Analyzer analyzer = new IKAnalyzer();

        IndexWriter indexWriter = null;
        try {
            //4. 创建Directory目录对象，目录对象表示索引库位置
            Directory dir = FSDirectory.open(Paths.get("/Users/linglan/ali/lucene/dir"));

            //5. 创建IndexWriterConfig对象，制定切分词使用的分词器
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            //6. 创建IndexWriter输出流对象，指定输出的位置和使用config初始化对象
            indexWriter = new IndexWriter(dir, config);
            //7. 写入文档到索引库
            for (Document document : docList) {
                indexWriter.addDocument(document);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //8. 释放资源
            if(indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 索引库修改
     */
    @Test
    public void updateIndexTest(){
        Document document = new Document();

        //创建域对象
        document.add(new StringField("id", "100000003145", Field.Store.YES));
        document.add(new TextField("name", "xxxx", Field.Store.YES));
        document.add(new IntPoint("price", 123));
        document.add(new StoredField("image", "xxx.jpg"));
        document.add(new StringField("categoryName", "手机", Field.Store.YES));
        document.add(new StringField("brandName", "华为", Field.Store.YES));
        //3. 创建分词器
        Analyzer analyzer = new StandardAnalyzer();

        IndexWriter indexWriter = null;
        try {
            //4. 创建Directory目录对象，目录对象表示索引库位置
            Directory dir = FSDirectory.open(Paths.get("/Users/linglan/ali/lucene/dir"));

            //5. 创建IndexWriterConfig对象，制定切分词使用的分词器
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            //6. 创建IndexWriter输出流对象，指定输出的位置和使用config初始化对象
            indexWriter = new IndexWriter(dir, config);
            //7. 修改文档到索引库
            indexWriter.updateDocument(new Term("id", "100000003145"), document);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //8. 释放资源
            if(indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 根据条件删除
     */
    @Test
    public void deleteIndexTest(){

        Analyzer analyzer = new StandardAnalyzer();

        IndexWriter indexWriter = null;
        try {
            //4. 创建Directory目录对象，目录对象表示索引库位置
            Directory dir = FSDirectory.open(Paths.get("/Users/linglan/ali/lucene/dir"));

            //5. 创建IndexWriterConfig对象，制定切分词使用的分词器
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            //6. 创建IndexWriter输出流对象，指定输出的位置和使用config初始化对象
            indexWriter = new IndexWriter(dir, config);
            //7. 删除文档到索引库
            indexWriter.deleteDocuments(new Term("id", "100000003145"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //8. 释放资源
            if(indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
