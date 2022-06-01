package ru.naumen.mineeva;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Класс, описывающий алгоритм работы кэширующего сервера.
 * Чтение данных из файла. Создание списка всех запросов клиентов.
 * Загрузка в кэш сервер с соблюдением условия непревышения максимального размера сервера
 * и передача данных клиенту.
 * Запись результата в файл.
 */
public class CashServer {

    private int resultCount = 0;
    private int sizeOfCash;
    private int sizeOfAllRequests;
    final String EXC_DESCRIPTION = "Данные во входном файле некорректные или отсутствуют";

    HashMap<Long, CustomerRequest> requestsMap = new HashMap<>();
    Long[] arrayOfAllRequests;
    HashMap<Long, CustomerRequest> cashServer = new HashMap<>();
    List<Long> completedRequests = new ArrayList<>();

    public CashServer() {
    }

    /**
     * Проверка на null одного аргумента
     *
     * @param obj         проверяемый объект
     * @param description описание
     * @return boolean
     */
    public boolean isNull(Object obj, String description) {
        if (obj == null) {
            System.out.println(description);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Чтение текста из файла построчно
     * Сохранение всех идентификаторов запросов в массиве всех запросов
     * Проверка на корректность данных во входном файле
     *
     * @param filename откуда читаем
     */
    public void readInputFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line1 = br.readLine();
            if (!isNull(line1, EXC_DESCRIPTION)) {
                extractAndSetArraySizes(line1);
                arrayOfAllRequests = new Long[sizeOfAllRequests];
            }

            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                Long numOfRequest = extractLongNumber(line);

                if (isNull(numOfRequest, "Пустая ссылка на номер запроса")) {
                    changeArrayOfAllRequest(i);
                    break;
                } else {
                    addCustomerRequest(i, numOfRequest);
                    if (sizeOfAllRequests > i) {
                        arrayOfAllRequests[i] = numOfRequest;
                        i++;
                    } else {
                        System.out.println("Некорректно указано количество запросов во входном файле, будет обработано "
                                + sizeOfAllRequests + " запросов");
                        break;
                    }
                }
            }
            if (sizeOfAllRequests > i) {
                changeArrayOfAllRequest(i);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Преобразование строки в числовые значения
     * Сохранение размера кэш сервера и размера массива всех запросов
     *
     * @param line1 первая строка входного файла
     */
    private void extractAndSetArraySizes(String line1) {
        String[] result = line1.split(" ");

        if (result.length == 2) {
            try {
                this.sizeOfCash = Integer.parseInt(result[0]);
                this.sizeOfAllRequests = Integer.parseInt(result[1]);
            } catch (NumberFormatException e) {
                System.out.println(e.toString());
                System.out.println(EXC_DESCRIPTION);
            }
        }
    }

    /**
     * Преобразование строки в числовое значение - идентификатор запроса
     *
     * @param line одна строка (со второй по последнюю) входного файла
     */
    private Long extractLongNumber(String line) {
        Long longNumber = null;
        try {
            longNumber = Long.parseLong(line);
        } catch (NumberFormatException e) {
            System.out.println(e.toString());
            System.out.println(EXC_DESCRIPTION);
        }
        return longNumber;
    }

    /**
     * Изменение размера массива всех запросов в случае
     * неполных данных во входном файле
     *
     * @param i номер по порядку в общем списке запросов
     */
    private void changeArrayOfAllRequest(int i) {
        sizeOfAllRequests = i;
        Long[] tempArray = new Long[sizeOfAllRequests];
        System.arraycopy(arrayOfAllRequests, 0, tempArray, 0, tempArray.length);
        arrayOfAllRequests = tempArray;
        System.out.println("Некорректно указано количество запросов во входном файле," +
                " будет обработано " + i + " запросов");
    }

    /**
     * Создание / обновление объекта CustomerRequest (Запрос клиента)
     * и добавление / обновление его в requestsMap (хранилище всех уникальных запросов)
     *
     * @param i            номер по порядку в общем списке запросов
     * @param numOfRequest идентификатор запроса
     */
    private void addCustomerRequest(int i, Long numOfRequest) {
        CustomerRequest customerRequest;
        if (!requestsMap.containsKey(numOfRequest)) {
            List<Integer> numberInOrderList = new ArrayList<>();
            numberInOrderList.add(i);
            customerRequest = new CustomerRequest(numOfRequest, 1, numberInOrderList);
        } else {
            customerRequest = requestsMap.get(numOfRequest);
            customerRequest.setCounter(customerRequest.getCounter() + 1);
            customerRequest.getNumberInOrderList().add(i);
        }
        requestsMap.put(numOfRequest, customerRequest);
    }

    /**
     * Основной метод проекта, осуществляющий обработку всех запросов по порядку.
     * Проверка наличия запроса в кэш сервере, сравнение текущего размера кэш сервера с максимальным,
     * удаление одного запроса из кэш сервера, если кэш сервер полный.
     * Удаление запроса из кэш сервера, если запрос больше не повторится.
     * Подсчет количества обращений к распределенной системе.
     */
    public void processRequest() {
        if (!isNull(arrayOfAllRequests, "Нет данных для массива всех запросов")) {
            for (Long key : arrayOfAllRequests) {
                int counter;
                if (!cashServer.containsKey(key)) {
                    resultCount += 1;
                    if (cashServer.size() == sizeOfCash) {
                        CustomerRequest customerRequestToRemove = getRequestToRemove(cashServer);
                        cashServer.remove(customerRequestToRemove.getNumberOfRequest()); // удаление одного запроса из кэш сервера, чтобы освободить место
                    }
                    cashServer.put(key, requestsMap.get(key));
                }

                completedRequests.add(key); // отправка данных клиенту
                CustomerRequest customerRequest = cashServer.get(key);
                counter = customerRequest.getCounter() - 1; // обновление счетчика повторений у запроса

                if (counter == 0) {
                    cashServer.remove(key); // удаление запроса из кэш сервера, если он больше не повторится
                } else {
                    customerRequest.setCounter(counter);
                    cashServer.put(key, customerRequest); // обновление запроса в кэш сервере
                }

                requestsMap.put(key, customerRequest); // обновление запроса в общем хранилище запросов
            }
        } else {
            resultCount = 0;
        }
    }

    /**
     * Получение из кэш сервера запроса клиента с максимальным номером следующей ближайшей позиции,
     * когда этот запрос встретится в общем списке запросов
     *
     * @param cashServer хранилище запросов в кэш сервере
     * @return CustomerRequest
     */
    private CustomerRequest getRequestToRemove(HashMap<Long, CustomerRequest> cashServer) {
        List<CustomerRequest> cashServerList = new ArrayList<>(cashServer.values());
        cashServerList.sort(Comparator.comparingInt(CustomerRequest::getNextPosition));
        return cashServerList.get(cashServerList.size() - 1);
    }

    /**
     * Загрузка результата (количества обращений к распределенной системе) в файл
     */
    public void writeResultToOutputFile(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(Integer.toString(resultCount));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}