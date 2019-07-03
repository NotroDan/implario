public class NativeTest {
	
	public static native int some(int i);
	
	public static void main(String[] args) {
		System.out.println(some(1));
	}
	
}
