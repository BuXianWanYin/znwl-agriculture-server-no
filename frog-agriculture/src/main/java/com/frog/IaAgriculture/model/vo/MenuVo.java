package com.frog.IaAgriculture.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class MenuVo {
    private Long id;
    private String name;
    private Long parentId;
    private String icon;
    private String path;
    private String authority;
    private Integer sort;
    private List<MenuVo> children;
}
