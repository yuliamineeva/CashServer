package ru.naumen.mineeva;

/**
 * Главный класс проекта
 * <p>
 * На вход программы подаётся максимальное количество запросов 1 ≤ N ≤ 100 000,
 * которое может быть закэшировано на сервере, количество запросов 1 ≤ M ≤ 100 000
 * и ровно M запросов с идентификаторами 0 ≤ Ri ≤ (2^63-1).
 * Количество различных номеров запросов ограничено и не превосходит 100 000.
 * Необходимо создать алгоритм, чтобы как  можно больше данных было получено из кэша сервера,
 * без обращения к распределённой системе. Требуется вывести одно число: сколько раз пришлось
 * обратиться к распределённой системе за данными, отсутствующими в кэше. В начале работы кэш пуст.
 */
public class Main {
    public static void main(String[] args) {
        final String INPUT_FILENAME = "input.txt";
        final String OUTPUT_FILENAME = "output.txt";

        CashServer cashServer = new CashServer();
        cashServer.readInputFile(INPUT_FILENAME);
        cashServer.processRequest();
        cashServer.writeResultToOutputFile(OUTPUT_FILENAME);
    }
}
