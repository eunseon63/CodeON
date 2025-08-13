package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_DEPARTMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DEPARTMENT_GENERATOR")
    @SequenceGenerator(
        name = "SEQ_DEPARTMENT_GENERATOR",
        sequenceName = "seq_department",
        allocationSize = 1,
        initialValue = 10
    )
    @Column(name = "department_seq", nullable = false)
    private int departmentSeq;

    @Column(name = "department_name", nullable = false, length = 30)
    private String departmentName;
}
