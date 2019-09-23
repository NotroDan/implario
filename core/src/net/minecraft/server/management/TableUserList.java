package net.minecraft.server.management;

import lombok.RequiredArgsConstructor;
import net.minecraft.database.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class TableUserList {
    private final Table table;
    private final String write;
    private List<String> list = new ArrayList<>();

    public void add(String nick){
        list.add(nick.toLowerCase());
    }

    public void remove(String nick){
        list.remove(nick.toLowerCase());
    }

    public boolean contains(String nick){
        return list.contains(nick.toLowerCase());
    }

    public List<String> values(){
        return list;
    }

    public void save(){
        StringBuilder buffer = new StringBuilder();
        for(String nick : list)
            buffer.append(nick).append('\n');
        table.write(write, buffer.toString().getBytes());
    }

    public void read(){
        byte[] read = table.read(write);
        if(read == null)return;
        Collections.addAll(list, new String(read).split("\n"));
    }
}
