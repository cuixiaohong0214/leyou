package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "tb_spu")
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    //1级类目
    private Long cid1;
    //2级类目
    private Long cid2;
    //3级类目
    private Long cid3;
    private String title;
    private String subTitle;
    //是否上架
    private Boolean saleable;
    @JsonIgnore
    private Boolean valid;
    private Date createTime;
    @JsonIgnore
    private Date lastUpdateTime;

    @Transient
    private String cname;
    @Transient
    private String bname;

    @Transient
    private List<Sku> skus;
    @Transient
    private SpuDetail spuDetail;
}
