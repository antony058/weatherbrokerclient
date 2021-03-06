package ru.bellintegrator.weatherbroker.server.exceptionhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.bellintegrator.weatherbroker.client.exception.NotFoundException;

@ControllerAdvice
@RestController
public class ExceptionHandlingController {
    private Logger log = LoggerFactory.getLogger(ExceptionHandlingController.class);

    @ResponseBody
    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<String> handleThrowable(Throwable ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
