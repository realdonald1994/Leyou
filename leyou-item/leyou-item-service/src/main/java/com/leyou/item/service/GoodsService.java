package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Spu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据条件分页查询spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //添加查询条件
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //添加上下架的过滤条件
        if(saleable !=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //添加分页
        PageHelper.startPage(page,rows);
        //执行查询，获取spu集合
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        //spu集合转化成spubo集合
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            //复制一个对象的属性到另一个对象
            BeanUtils.copyProperties(spu, spuBo);
            //查询分类名称
            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "-"));
            //查询品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;
        }).collect(Collectors.toList());
        //返回pageResult<SpuBo>
        return new PageResult<>(pageInfo.getTotal(),spuBos);


    }
}
