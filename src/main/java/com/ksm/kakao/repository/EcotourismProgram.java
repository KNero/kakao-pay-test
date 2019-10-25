package com.ksm.kakao.repository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
public class EcotourismProgram {
    @Id
    private String id;

    @Column
    private String name;

    @Column
    private String theme;

    @Column(name = "region_code")
    private String regionCode;

    @Column
    private String introduce;

    @Column
    private String detail;

    @ManyToOne(targetEntity = ServiceRegion.class, fetch = FetchType.LAZY)
    @JoinFormula("region_code")
    private ServiceRegion region;

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EcotourismProgram && ((EcotourismProgram) obj).id.equals(id);
    }
}
