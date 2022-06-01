package ru.naumen.mineeva;

import java.util.List;

/**
 * Класс, описывающий запрос клиента
 */
public class CustomerRequest {
    /**
     * Номер (идентификатор) запроса клиента
     */
    private Long numberOfRequest;

    /**
     * Количество повторений данного запроса в общем списке
     */
    private int counter;

    /**
     * Список номеров позиций, под которыми данный запрос находится в общем списке запросов
     */
    private List<Integer> numberInOrderList;

    public CustomerRequest(Long numberOfRequest, int counter, List<Integer> numberInOrderList) {
        this.numberOfRequest = numberOfRequest;
        this.counter = counter;
        this.numberInOrderList = numberInOrderList;
    }

    /**
     * Возвращает номер следующей ближайшей позиции, когда этот запрос встретится в общем списке запросов.
     */
    public int getNextPosition(){
        int index = numberInOrderList.size() - counter;
        return numberInOrderList.get(index);
    }

    public Long getNumberOfRequest() {
        return numberOfRequest;
    }

    public void setNumberOfRequest(Long numberOfRequest) {
        this.numberOfRequest = numberOfRequest;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<Integer> getNumberInOrderList() {
        return numberInOrderList;
    }

    public void setNumberInOrderList(List<Integer> numberInOrderList) {
        this.numberInOrderList = numberInOrderList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o.getClass() != this.getClass()) return false;

        CustomerRequest that = (CustomerRequest) o;

        return numberOfRequest != null ? numberOfRequest.equals(that.numberOfRequest) : that.numberOfRequest == null;
    }

    @Override
    public int hashCode() {
        return numberOfRequest != null ? numberOfRequest.hashCode() : 0;
    }
}
