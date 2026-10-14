package queueup.api.shared.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import queueup.api.shared.apiResponse.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<Object> businessExceptionHandler(BusinessException exception) {
                return ResponseEntity.status(exception.getHttpStatus())
                                .body(ApiResponse.error(
                                                exception.getMessage(),
                                                exception.getHttpStatus(),
                                                exception.getClass().getSimpleName()));
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<Object> dataIntegrityViolationHandler(DataIntegrityViolationException exception) {
                return ResponseEntity.status(
                                HttpStatus.CONFLICT).body(
                                                ApiResponse.error("Resource already registered or integrity violation.",
                                                                HttpStatus.CONFLICT,
                                                                exception.getClass().getSimpleName()));

        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Object> genericHandler(Exception exception) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(
                                                "An unexpected error occurred.",
                                                HttpStatus.INTERNAL_SERVER_ERROR,
                                                exception.getClass().getSimpleName()));
        }
        /*
         * @ExceptionHandler(BadCredentialsException.class)
         * public ResponseEntity<Object> BadCredentialsException(BadCredentialsException
         * exception) {
         * return ResponseEntity.status(
         * HttpStatus.UNAUTHORIZED).body(
         * ApiResponse.error("Incorrect Credentials.",
         * HttpStatus.UNAUTHORIZED,
         * exception.getClass().getSimpleName()));
         * 
         * }
         */
}
