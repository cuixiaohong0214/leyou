package com.leyou.search.pojo;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Data
@Document(indexName = "goods",type = "docs",shards = 1,replicas = 0)
public class Goods {
    @Id
    private Long id;

    // 所有需要被搜素的信息，包含标题，分类，甚至品牌
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String all;

    // 卖点
    @Field(type = FieldType.Keyword,index = false)
    private String subTitle;
    private Long brandId;
    private Long cid1;
    private Long cid2;
    private Long cid3;
    private Date createTime;
    private Set<Long> price;

    // sku信息的json结构
    @Field(type = FieldType.Keyword,index = false)
    private String skus;
    // 可搜索的规格参数，key是参数名，值是参数值
    private Map<String,Object> specs;
}
