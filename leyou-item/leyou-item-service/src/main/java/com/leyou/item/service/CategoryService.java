package com.leyou.item.service;


import com.leyou.item.pojo.Category;

import java.util.List;

public interface CategoryService {

    /**
     * 根据父节点查询子节点
     * @param pid
     * @return
     */
    List<Category> queryCategoriesByPid(Long pid);


    public List<String> queryNamesByIds(List<Long> ids);

}
