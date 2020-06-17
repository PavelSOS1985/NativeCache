import java.lang.reflect.Array;

class NativeCache<T> {
    public int size;
    public String[] slots;
    public T[] values;
    public int[] hits;

    public NativeCache(int sz, Class clazz) {
        size = sz;
        slots = new String[size];
        values = (T[]) Array.newInstance(clazz, this.size);
        hits = new int[size];
    }

    // находит индекс пустого слота для значения, или -1
    public int seekSlot(String value) {
        int indexSlot = this.hashFun(value);                        // находим индекс элемента
        int countPasses = 0;                                        // вспомогательная переменная отвечающая за количество проходов по слотам
        while (slots[indexSlot] != null) {
            if (slots[indexSlot].equals(value))
                return indexSlot;                                   // если слот содержит такой же элемент то возвращать индекс этого слота
            indexSlot += 1;                                      // изменение индекса слота на шаг
            if (indexSlot >= size && countPasses < 1) {          // если индекс выходит за пределы таблицы и проходов меньше чем необходимо
                indexSlot = countPasses;                            // начинаем очередной проход сначала таблицы
                countPasses++;
            }
            if (indexSlot >= size && countPasses >= 1) {         // если вышли за пределы и прошли все слоты возвращаем -1
                return -1;
            }
        }
        return indexSlot;
    }

    // всегда возвращает корректный индекс слота
    public int hashFun(String key) {
        byte[] arrBytesValue = key.getBytes();      // записываем байты элемента в массив
        int sum = 0;
        for (byte i :
                arrBytesValue) {
            sum += i;                               // считаем сумму байт
        }
        return sum % size;                          // вычисляем индекс
    }

    // возвращает true если ключ имеется,
    // иначе false
    public boolean isKey(String key) {
        int indexSlot = this.seekSlot(key);
        if (indexSlot == -1) return false;
        if (slots[indexSlot] == null) return false;
        else {
            hits[indexSlot]++;
            return true;
        }
    }

    // гарантированно записываем
    // значение value по ключу key
    public void put(String key, T value) {
        int indexSlot = this.seekSlot(key);
        if (indexSlot == -1) {
            int min = hits[0];
            for (int i = 0; i < size; i++) {
                if (hits[i] <= min) {
                    min = hits[i];
                    indexSlot = i;
                }
            }
            hits[indexSlot] = 0;
        }
        slots[indexSlot] = key;
        values[indexSlot] = value;
    }

    // возвращает value для key,
    // или null если ключ не найден
    public T get(String key) {
        if (this.isKey(key)) {
            int indexSlot = this.seekSlot(key);
            return values[indexSlot];
        }
        return null;
    }

    // внутренняя функция удаления (пока что не понаобилась)
    private void del(int i) {
        slots[i] = null;
        values[i] = null;
        hits[i] = 0;
    }
}