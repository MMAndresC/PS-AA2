package com.svalero.psaa2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseLocations {
    private List<Location> items;
    private Paging paging;
}
