package com.lk.mall.product.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "prop")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Prop {
    
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long directoryId;
    private String name;
    @Transient
    private List<PropOption> optionList;
    
    public List<PropOption> getOptionList(){
        if(null==optionList) {
            return new ArrayList<>();
        }
        return optionList;
    }

}
