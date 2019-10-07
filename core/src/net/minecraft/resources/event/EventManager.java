package net.minecraft.resources.event;

import net.minecraft.resources.Domain;
import net.minecraft.resources.event.events.Cancelable;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.function.Consumer;

public class EventManager<T extends Event> {
	private Listener<T>[] array;

    @SuppressWarnings("unchecked")
	public int add(Listener<T> listener) {
		Listener<T>[] array = new Listener[this.array == null ? 1 : this.array.length + 1];
		int id = -1;
		if (this.array != null) {
		    int i = 0;
		    for(; i < this.array.length; i++){
		        if(listener.priority() <= this.array[i].priority()){
		            id = i;
		            array[i] = listener;
		            i++;
		            break;
                }else{
		            array[i] = this.array[i];
                }
            }
		    if(i == this.array.length){
		        id = i;
		        array[this.array.length] = listener;
            }else{
                System.arraycopy(this.array, i - 1, array, i, this.array.length - i);
            }
        }else {
		    id = 0;
            array[0] = listener;
        }
        this.array = array;
		return id;
	}



	@SuppressWarnings("unchecked")
	public void remove(int id) {
		if (array == null ) return;
		int newArraySize = array.length - 1;
		if(newArraySize == 0){
		    array = null;
		    return;
        }
		this.array = ArrayUtils.remove(this.array, id);
	}

	public T call(T event) {
		if (array == null) return event;
		for (Listener<T> listener : array){
		    if(listener.ignoreCancelled() && event instanceof Cancelable && ((Cancelable)event).isCanceled())
                continue;
		    listener.process(event);
        }
		return event;
	}

	public boolean isUseful() {
		return array != null && array.length != 0;
	}
}
