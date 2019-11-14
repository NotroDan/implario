public class SupportTest {
    public static void main(String[] args) {
        int a = 50;
        int b = 100;
        a(a, b);
    }

    public static void a(int i, Object l){
        System.out.println(i + " " + l + " first");
    }

    public static void a(long i, int l){
        System.out.println(i + " " + l + " two");
    }
}
