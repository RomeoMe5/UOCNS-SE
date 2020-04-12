package ru.stepanov.uocns.common.exceptions;

public class ObjectNotFoundException extends CommonException {

    public ObjectNotFoundException(String objectType, String objectId) {
        super(String.format("Не удалось найти объект. Тип: %s. Параметры поиска: %s", objectType, objectId));
    }

    @Override
    public String staticMessage(){
        return "object_not_found_error";
    }
}
