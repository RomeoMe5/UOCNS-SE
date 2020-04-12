package ru.stepanov.uocns.common.exceptions;

public class BadRequestException extends CommonException {

    public BadRequestException(String parametername, String parameterValue) {
        super(String.format("Неверный аргумент. Название: %s. Значение: %s", parametername, parameterValue));
    }

    public BadRequestException(String parametername) {
        super(String.format("Неверный аргумент. Название: %s. Значение: null", parametername));
    }

    @Override
    public String staticMessage(){
        return "bad_request_error";
    }
}
