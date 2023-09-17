package com.hariyali.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequestDTO<T> {

    T data;
    Integer pageSize;
    Integer pageNumber;

}//class
