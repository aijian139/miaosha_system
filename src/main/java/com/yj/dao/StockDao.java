package com.yj.dao;

import com.yj.entity.Stock;

public interface StockDao {
    // 根据id 检查库存
    Stock checkStock(Integer id);
    // 更新库存
    int updateStock(Stock stock);
}
