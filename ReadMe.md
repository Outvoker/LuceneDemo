# Lucene

## 搜索技术理论基础

### 原始搜索引擎对比

![20210603165206](http://ruiimg.hifool.cn/20210603165206.png)

![20210603165235](http://ruiimg.hifool.cn/20210603165235.png)

### 数据查询方法

> 顺序扫描法

- 算法描述：
  - 一个一个文档顺序查找，从头到尾，直到扫描完所有的文件。
- 优点：
  - 查询准确率高
- 缺点：
  - 查询速度会随着查询数据量增大，越来越慢
- 使用场景：
  - 数据库中的like关键字模糊查询
  - 文本编辑器的Ctrl+F查询功能

> 倒排索引

- 算法描述：
  - 查询前会先将查询内容提取出来组成文档（正文），对文档进行切分词组成索引（目录），索引和文档有关联关系，查询的时候先查询索引，通过索引找文档的这个过程叫全文索引
- 优点：
  - 查询准确率高
  - 查询速度快，并且不会因为查询内容量的增加，而使查询速度逐渐变慢
- 缺点：
  - 索引文件会占用额外的磁盘空间，也就是占用磁盘量会增大
- 使用场景：
  - 站内搜索
  - 垂直领域的搜索
  - 专业搜索引擎公司

## Lucene介绍

### 全文检索

计算机索引程序通过扫描文章中的每一个词，对每一个词建立一个索引，指明该词在文章中出现的次数和位置，当用户查询时，检索程序就根据实现建立的索引进行查找，并将查找的结果反馈给用户的检索方式。

### Lucene

官网： lucene.apache.org

## Lucene全文检索流程

### 索引和搜索流程图

![20210603172840](http://ruiimg.hifool.cn/20210603172840.png)

### 索引流程

#### 创建文档

![20210603173430](http://ruiimg.hifool.cn/20210603173430.png)

- Lucene为每个Document分配唯一ID
- 每个Document可以有多个Field
- 同一个Document可以有相同的Field（域名和域值都相同）

#### 索引文档

- 对所有文档分析得出的语汇但愿进行索引，索引的目的是为了搜索，最终要实现只搜索被索引的语汇但愿从而找到Document
- 创建索引是对语汇单元索引，通过词语找文档，这种索引的结构叫倒排索引结构。
- 倒排索引结构是根据词汇找文档![20210603174203](http://ruiimg.hifool.cn/20210603174203.png)

#### Lucene底层存储结构

![20210603174311](http://ruiimg.hifool.cn/20210603174311.png)

### 搜索流程

分词 -> 查询

## Field域类型

### Field常用类型

| Field类 | 数据类型 | Analyzed是否分词 | Indexed是否索引 | Stored是否存储 | 说明 |
| ---- | ---- | ---- | ---- | ---- | ---- |
| StringField(FieldName, FieldValue, Store.YES) | 字符串 | N | Y | Y或N | 用来构建一个字符串Field，但是不会进行分词，会将整个串存储在索引中，比如（订单号，身份证号等）是否存储在文档中用Store.YES或Store.NO决定 |
| FloatPoint(FieldName, FieldName) | Float型 | Y | Y | N | 用来构建一个Float数字型Field，进行分词和索引，不存储，比如（价格）|
| DoublePoint(FieldName, FieldValue) | Double型 | Y | Y | N |  |
| LongPoint(FiledName, FieldValue) | Long型 | Y | Y | N |  |
| IntPoint | Integer型 | Y | Y | N |  |
| StoredField(FieldName, FieldValue) | 重载方法，支持多种类型 | N | N | Y |
| TextField(FieldName, FieldValue) 或 TextFiled(FieldName, reader) | Y | Y | Y或N | 如果是一个Reader，lucene猜测内容比较多会采用Unstored的策略 |
| NumericDocValuesField(FieldName, FieldValue) | 数值 | - | - | - | 配合其他域排序使用 |

## Lucene原生分词器

### StandardAnalyzer

- 特点：
  - Lucene提供的标准分词器，可以对英文进行分词，对中文是单字分词，一个字就认为是一个词
  ```java
    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
      final StandardTokenizer src = new StandardTokenizer();
      src.setMaxTokenLength(maxTokenLength);
      TokenStream tok = new LowerCaseFilter(src);
      tok = new StopFilter(tok, stopwords);
      return new TokenStreamComponents(src, tok) {
        @Override
        protected void setReader(final Reader reader) {
          // So that if maxTokenLength was changed, the change takes
          // effect next time tokenStream is called:
          src.setMaxTokenLength(StandardAnalyzer.this.maxTokenLength);
          super.setReader(reader);
        }
      };
    }
  ```

  ![20210607153737](http://ruiimg.hifool.cn/20210607153737.png)

  ### WhitespaceAnalyzer

  - 特点：
    - 仅仅去掉了空格，没有任何操作，不支持中文。

### SimpleAnalyzer

- 特点：
  - 将除了字母以外的符号全部去除，并且所有字母变为小写，数字也去除，不支持中文。

### CJKAnalyzer

- 特点：
  - 中日韩文字，对中文是二分法分词，去掉空格，去掉标点符号。对中文支持效果一般
  - 

### 第三方分词器

- paoding 停更
- mmseg4j 不支持扩展
- IK-analyzer 支持扩展词典，停用词典
- ansj_seg
- imdict-chinese-analyzer
- jcseg

### IKAnalyzer

- 扩展辞典
  - 放专有名词
  - 强制将某一些词分成一个词
- 停用词典
  - 停用词典中的词会被停用

## Lucene高级搜索

### 文本搜索

```java
  // 创建分词器
  Analyzer analyzer = new IKAnalyzer();
  // 创建搜索解析器，第一个参数:默认Field域，第二个参数:分词器
  QueryParser queryParser = new QueryParser("brandName", analyzer);
  // 创建搜索对象
  Query query = queryParser.parse("name:华为手机");
```

### 数值范围搜索

```java
  Query query = FloatPoint.newRangeQuery("price", 100, 1000);
```

### 组合搜索

- 需求描述 : 查询价格大于等于100, 小于等于1000, 并且名称中不包含华为手机关键字的商品 BooleanClause.Occur.MUST 必须 相当于and, 并且
- BooleanClause.Occur.MUST_NOT 不必须 相当于not, 非
- BooleanClause.Occur.SHOULD 应该 相当于or, 或者

```java
  // 创建分词器
  Analyzer analyzer = new IKAnalyzer();
  // 创建数值范围搜索对象
  Query query1 = FloatPoint.newRangeQuery("price", 100, 1000)
  QueryParser queryParser = new QueryParser("name", analyzer); // 创建搜索对象
  Query query2 = queryParser.parse("华为手机");
  //创建组合搜索对象
  BooleanQuery.Builder builder = new BooleanQuery.Builder(); builder.add(new BooleanClause(query1, BooleanClause.Occur.MUST)); builder.add(new BooleanClause(query2, BooleanClause.Occur.MUST_NOT));
```

> 如果查询条件都是MUST_NOT，则查询不出任何数据，这里是搜索引擎的一种保护机制。

## Lucene底层存储结构（高级）

![20210607191931](http://ruiimg.hifool.cn/20210607191931.png)

- 多个线程同时智能操作一个段，会使用`write.lock`来保证。
- 修改时，删除原文档，直接加在最后
  - 好处：减少随机io的次数

### 索引库文件扩展名对照表

![20210607200045](http://ruiimg.hifool.cn/20210607200045.png)

### 词典的构建

#### 词典数据结构对比

| 数据结构 | 优缺点 |
| ---- | ---- |
| 跳跃表 | 占用内存小，且可调，但是对模糊查询支持不好 |
| 排序列表Array/List | 使用二分法查找，不平衡 |
| 字典树 | 查询效率跟字符串长度有关，只适合英文词典 |
| 哈希表 | 性能高，内存消耗大，几乎是元数据的三倍 |
| 双数组字典树 | 适合做中文词典，内存占用小，很多分词工具均采用此种算法 |
| Finite State Transducers(FST) | 一种有限状态转移机，Lucene4有开源实现，并大量使用 |
| B树 | 磁盘索引，更新方便，但检索速度慢，多用于数据库 |

- 目前使用FST

#### 跳跃表

- 优点：结构简单、跳跃间隔、级数可控，Lucene3.0之前使用的也是跳跃表结构，但跳跃表在Lucene其他地方还有应用如倒排表合并和文档号索引。
- 缺点：模糊查询支持不好。

![20210607202755](http://ruiimg.hifool.cn/20210607202755.png)

#### FST

- Lucene现采用的数据结构为FST，它的特点就是：
  - 优点：内存占用率低，压缩率一般在3～20倍之间、模糊查询支持好、查询快
  - 缺点：结构复杂、输入要求有序、更新不易

- 已知FST要求输入有序，所以Lucene会将解析出来的文档单词预先排序，然后构建FST，我们假设输入
为abd,abe,acf,acg，那么整个构建过程如下：![20210609102654](http://ruiimg.hifool.cn/20210609102654.png)
- 输入数据：
  ```java
  String inputValues[] = {"hei","ma","cheng","xu","yuan","good"};
  long outputValues[] = {0,1,2,3,4,5};
  ```
- 输入的数据如下: `hei/0 ma/1 cheng/2 xu/3 yuan/4 good/5`
- 存储结果如下:
![20210609103020](http://ruiimg.hifool.cn/20210609103020.png)

## Lucene优化（高级）

### 解决大量磁盘IO

- config.setMaxBufferedDocs(100000); 
  - 控制写入一个新的segment前内存中保存的document的数目，设置较大的数目可以加快建索引速度。
  - 数值越大索引速度越快, 但是会消耗更多的内存
- indexWriter.forceMerge(文档数量); 
  - 设置N个文档合并为一个段
  - 数值越大索引速度越快, 搜索速度越慢; 值越小索引速度越慢, 搜索速度越快
  - 更高的值意味着索引期间更低的段合并开销，但同时也意味着更慢的搜索速度，因为此时的索引通常会包含更多的段。如果该值设置的过高，能获得更高的索引性能。但若在最后进行索引优化，那么较低的值会带来更快的搜索速度，因为在索引操作期间程序会利用并发机制完成段合并操作。故建议对程序分别进行高低多种值的测试，利用计算机的实际性能来告诉你最优值。

### 选择合适的位置存放索引库

| 类 | 写操作 | 读操作 | 特点 |
| ---- | ---- | ---- | ---- |
| SimpleFSDirectory | java.io.RandomAccessFile | java.io.RandomAccessFile | 简单实现，并发能力差 |
| NIOFSDirectory | java.nio.FileChannel | FSDirectory.FSIndexOutput | 并发能力强，windows平台下有bug |
| MMapDirectory | 内存映射 | FSDirectory.FSIndexOutput | 读取基于内存，第一次从磁盘中查询出来之后，存入内存当中，加快下一次查询的速度，FSDirectory默认使用的为MMapDirectory |

### 搜索api选择

1. 尽量使用TermQuery代替QueryParser
2. 尽量变大范围的日期查询

## 相关度排序

### 如何打分

1. 计算出词（Term）的权重
2. 根据词的权重值，计算文档相关度得分。

### 词的权重

- 明确索引的最小单位是一个Term(索引词典中的一个词)，搜索也是要从Term中搜索，再根据Term找到
文档，Term对文档的重要性称为权重，影响Term权重有两个因素：
  - Term Frequency (tf)： 
    - 指此Term在此文档中出现了多少次。tf 越大说明越重要。 词(Term)在文档中出现的次数越多，说明此词(Term)对该文档越重要，如“Lucene”这个词，在文档中出现的次数很多，说明该文档主要就是讲Lucene技术的。
  - Document Frequency (df)：
    - 指有多少文档包含次Term。df 越大说明越不重要。 比如，在一篇英语文档中，this出现的次数更多，就说明越重要吗？不是的，有越多的文档包含此词(Term), 说明此词(Term)太普通，不足以区分这些文档，因而重要性越低。

## Lucene注意事项

- 关键词区分大小写
  - OR AND TO等关键词是区分大小写的，lucene只认大写的，小写的当做普通单词。
- 读写互斥性
  - 同一时刻只能有一个对索引的写操作，在写的同时可以进行搜索
- 文件锁
  - 在写索引的过程中强行退出将在tmp目录留下一个lock文件，使以后的写操作无法进行，可以将其手工删除
- 时间格式
  - lucene只支持一种时间格式yyMMddHHmmss，所以你传一个yy-MM-dd HH:mm:ss的时间给lucene它是不会当作时间来处理的
- 设置boost
  - 有些时候在搜索时某个字段的权重需要大一些，例如你可能认为标题中出现关键词的文章比正文中出现关键词的文章更有价值，你可以把标题的boost设置的更大，那么搜索结果会优先显示标题中出现关键词的文章.

## DocValue

<https://cloud.tencent.com/developer/article/1122277>

## Collector

<https://blog.csdn.net/sc736031305/article/details/84712013>