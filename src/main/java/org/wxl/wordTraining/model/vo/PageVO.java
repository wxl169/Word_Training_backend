package org.wxl.wordTraining.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wxl
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO {

    private List rows;

    private Long total;

}
