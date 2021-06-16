package com.alipay.pojo;

import lombok.Data;

@Data
public class Sku {
    //商品主键id
    private String id;
    private String name;
    //价格
    private Integer price;
    //库存数量
    private Integer num;
    //图片
    private String image;
    //分类名称
    private String categoryName;
    //品牌名称
    private String brandName;
    //规格
    private String spec;
    //销量
    private Integer saleNum;
}
