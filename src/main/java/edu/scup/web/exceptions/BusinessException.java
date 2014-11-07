package edu.scup.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 2814483140143382224L;

    public BusinessException(String msg) {
        super(msg);
    }
}
