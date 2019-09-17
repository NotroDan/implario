package net.minecraft.util;

public class IntDoubleMap<T, V> {
    private Object[] valuesT;
    private Object[] valuesV;

    public IntDoubleMap(int capacityDefault){
        valuesT = new Object[capacityDefault];
        valuesV = new Object[capacityDefault];
    }

    @SuppressWarnings("unchecked")
    public V get(int i) {
        return (V) (valuesV.length <= i ? null : valuesV[i]);
    }

    public int get(T value){
        for(int i = 0; i < valuesT.length; i++)
            if(valuesT[i] == value)return i;
        return -1;
    }

    public void clear(int id){
        valuesT[id] = null;
        valuesV[id] = null;
    }

    public void put(int id, T valueT, V valueV){
        if(id >= valuesV.length)grow((id - valuesV.length) + 11);
        valuesT[id] = valueT;
        valuesV[id] = valueV;
    }

    public void grow(int size){
        int length = valuesT.length;
        Object[] valuesT = this.valuesT, valuesV = this.valuesV,
                newValuesT = new Object[length + size],
                newValuesV = new Object[length + size];
        System.arraycopy(valuesT, 0, newValuesT, 0, length);
        System.arraycopy(valuesV, 0, newValuesV, 0, length);
        this.valuesT = newValuesT;
        this.valuesV = newValuesV;
    }
}
