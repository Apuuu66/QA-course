package com.guier.pojo;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

@Data
public class Product {
    // 商品编号
    @Field("id")
    private String pid;
    // 商品名称
    @Field("product_name")
    private String name;
    // 商品分类名称
    @Field("product_catalog_name")
    private String catalog_name;
    // 价格
    @Field("product_price")
    private float price;
    // 商品描述
    private String description;
    // 图片名称
    @Field("product_picture")
    private String picture;


}

