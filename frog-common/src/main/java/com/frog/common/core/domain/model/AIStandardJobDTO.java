package com.frog.common.core.domain.model;

public class AIStandardJobDTO {

    private Long germplasmId;

    private String name;

    private String typeName;

    private Integer type;

    public Long getGermplasmId() {
        return germplasmId;
    }

    public void setGermplasmId(Long germplasmId) {
        this.germplasmId = germplasmId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
