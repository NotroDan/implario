package net.minecraft.util;

public class Exceptions {
    public static void runThrowsEx(RunOutException run, boolean ignoreEx) {
        getThrowsEx(() -> {
            run.run();
            return null;
        }, ignoreEx);
    }

    public static void runThrowsEx(RunOutException run){
        runThrowsEx(run, true);
    }

    public static <T> T getThrowsEx(GetOutException<T> get, boolean ignoreEx){
        try{
            return get.run();
        }catch (Exception ex){
            if(ignoreEx)return null;
            throw new IllegalArgumentException(ex);
        }
    }

    public static <T> T getThrowsEx(GetOutException<T> get){
        return getThrowsEx(get, true);
    }

    public interface RunOutException {
        void run() throws Exception;
    }

    public interface GetOutException<T>{
        T run() throws Exception;
    }
}
