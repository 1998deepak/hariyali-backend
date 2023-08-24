package com.hariyali.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TooManyRequestException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    private String message ;

}
