import lombok.RequiredArgsConstructor;

import java.io.PrintStream;
import java.util.Scanner;

@RequiredArgsConstructor
public class Console {
	
	private static final String ESC = "\u001b[";
	
	private final PrintStream stream;
	
	public void print(String s) {
		char[] charArray = s.toCharArray();
		for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
			char c = charArray[i];
			if (c != '&') {
				stream.print(c);
				continue;
			}
			if (charArray.length <= i - 1) break;
			char r = charArray[++i];
			
			int d = "f42615378cae9db0".indexOf(r);
			boolean bright = d >= 8;
			if (bright) d -= 8;
			r = (char) (d + '0');
			char[] chars = d == -1 ?
					new char[] {'\u001b', '[', '0',    'm'} : bright ?
					new char[] {'\u001b', '[', '9', r, 'm'} :
					new char[] {'\u001b', '[', '3', r, 'm'} ;
			stream.print(chars);
			
		}
	}
	
	public static void main(String[] args) {
		Console console = new Console(System.out);
//		for (int i = 0; i < 16; i++) {
//			char c = "0123456789abcdef".charAt(i);
//			console.print("&r§" + c + " &" + c + "Привет, ютуб\n");
//		}
		
		console.print("&3[Гл. Админ] Lucy&7: &fДелфик няшка\n&b[P] DelfikPro&7: &fНе поспоришь");
		
		Scanner scanner = new Scanner(System.in);
		scanner.useDelimiter("\n");
		while (true) {
			String s = scanner.next();
			console.print(s + "\n");
		}
	}
	
	
}
