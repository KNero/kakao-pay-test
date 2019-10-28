package com.ksm.kakao.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
public class ServiceRegion {
    @Id
    private String code;

    @Column
    private String name;

    @Column(name = "parent_code")
    private String parentCode;

    @ManyToOne(targetEntity = ServiceRegion.class, fetch = FetchType.LAZY)
    @JoinFormula("parent_code")
    private ServiceRegion parent;

    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        makeAddress(this, address);

        return address.toString();
    }

    private static void makeAddress(ServiceRegion regionDto, StringBuilder address) {
        if (regionDto != null) {
            if (address.length() > 0) {
                address.insert(0, " ");
            }

            address.insert(0, regionDto.getName());
            makeAddress(regionDto.getParent(), address);
        }
    }
}
