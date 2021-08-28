package com.teamherb.bookstoreback.common;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {

    @NotNull
    private Integer page;

    @NotNull
    private Integer size;
}
