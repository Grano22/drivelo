package io.github.grano22.carfleetapp.shared;

import io.github.grano22.carfleetapp.shared.domain.InvalidDataGivenForOperation;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(InvalidDataGivenForOperation.class)
    public ProblemDetail handleInvalidDataGivenForOperation(InvalidDataGivenForOperation ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(412), ex.getMessage());

        problem.setTitle("Precondition Failed");

        return problem;
    }
}
