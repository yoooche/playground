package com.eight.demo.module.to;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnMappingTO {

    private String brandId;
    private String filePath;
    private List<MappingRuleTO> mappings;
}
