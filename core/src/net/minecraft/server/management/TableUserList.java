package net.minecraft.server.management;

import lombok.RequiredArgsConstructor;
import net.minecraft.database.Table;

import java.util.List;

@RequiredArgsConstructor
public class TableUserList {
    private final Table table;
    private List<String> list;


}
