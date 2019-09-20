package net.minecraft.resources.event;

import net.minecraft.resources.Domain;
import net.minecraft.resources.event.events.Cancelable;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.function.Consumer;

public class EventManager<T extends Event> {
	private Listener<T>[] array;

    @SuppressWarnings("unchecked")
	public void add(Listener<T> listener) {
		Listener<T>[] array = new Listener[this.array == null ? 1 : this.array.length + 1];
		if (this.array != null) {
		    int i = 0;
		    for(; i < this.array.length; i++){
		        if(listener.priority() <= this.array[i].priority()){
		            array[i] = listener;
		            i++;
		            break;
                }else{
		            array[i] = this.array[i];
                }
            }
            System.out.println(i + " " + array.length + " " + listener + " " + this.array.length);
		    if(i == this.array.length){
		        array[this.array.length] = listener;
            }else{
                System.arraycopy(this.array, i - 1, array, i, this.array.length - i);
            }
            if(i == 1){
                System.out.println(array[0]);
                System.out.println(array[1]);
            }
        }else {
            array[array.length - 1] = listener;
        }
        this.array = array;
	}

	public void add(Consumer<T> consumer, Domain domain, boolean ignoreCancelled, int priority){
	    add(new Listener<T>() {
            @Override
            public void process(T event) {
                consumer.accept(event);
            }

            @Override
            public Domain domain() {
                return domain;
            }

            @Override
            public boolean ignoreCancelled() {
                return ignoreCancelled;
            }

            @Override
            public int priority() {
                return priority;
            }
        });
    }

    public void add(Consumer<T> consumer, Domain domain, int priority){
        add(consumer, domain, false, priority);
    }

    public void add(Consumer<T> consumer, Domain domain, boolean ignoreCancelled){
	    add(consumer, domain, ignoreCancelled, 0);
    }

	public void add(Consumer<T> consumer, Domain domain){
		add(consumer, domain, false);
	}

	@SuppressWarnings("unchecked")
	public void remove(Domain domain) {
		if (array == null) return;
		int newArraySize = array.length;
		for (int i = 0; i < array.length; i++)
		    if (array[i].domain() == domain) newArraySize--;
		Listener<T> newArray[] = (Listener<T>[]) new Listener[newArraySize];
		for(int i = 0, j = 0; i < newArraySize;) {
		    Listener<T> listener = array[i + j];
            if(listener.domain() == domain)
                j++;
            else{
                newArray[i] = array[i + j];
                i++;
            }
        }
		this.array = newArray;
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
