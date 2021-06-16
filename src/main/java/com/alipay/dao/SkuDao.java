package com.alipay.dao;

import com.alipay.pojo.Sku;

import java.util.List;

public interface SkuDao {
    /**
     * 查询所有的Sku数据 * @return
     **/
    public List<Sku> querylist();
}
