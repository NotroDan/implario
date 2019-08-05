package net.minecraft.util.byteable;

import lombok.Getter;

class ByteSet {
    public ByteSet(){
        node = new Node();
        endNode = node;
    }

    private Node node;
    private Node endNode;
    @Getter
    private int size;

    public void write(byte b){
        size++;
        endNode = (endNode.next = new Node(b));
    }

    public byte[] generate(){
        byte array[] = new byte[size];
        Node current = node;
        for(int i = 0; i < size; i++){
            current = current.next;
            array[i] = current.value;
        }
        return array;
    }

    public byte[] generate(byte array[], int offset){
        Node current = node;
        for(int i = 0; i < size; i++){
            current = current.next;
            array[offset + i] = current.value;
        }
        return array;
    }

    private static class Node{
        public Node(){}

        public Node(byte b){
            value = b;
        }

        byte value;
        Node next;
    }
}
