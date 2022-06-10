package fer.zavrsni.bioinformatics;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;

import htsjdk.tribble.annotation.Strand;




public class ColorPalette {
	
	//private static Color colorA;
	//private static Color colorC;
	//private static Color colorT;
	//private static Color colorG;
	
	public static Color getBaseColor(char base) {
		Color color;
		//System.out.println(Character.toUpperCase(base));
		switch(Character.toUpperCase(base)) {
		
		case 'A':
			color = new Color(184, 180, 45);
			break;
		case 'C':
			color = new Color(105, 122, 33);
			break;
		case 'T':
			color = new Color(65, 123, 142);
			break;
		case 'G':
			color = new Color(221, 64, 58);
			break;
		case '-':
			color = Color.LIGHT_GRAY;
			break;
		default:
			color = Color.DARK_GRAY;
			
		}
		//System.out.println(color.toString());
		
		return color;
	}
	
	public static Color BEDAnnotaionColor(String string, boolean _gap) {
		if (_gap) {
			return Color.LIGHT_GRAY;
		}
		
		else if (string == "POSITIVE") {
			return new Color(64, 249, 155);
		} else if (string == "NEGATIVE") {
			return new Color(232, 93, 117);
		} else {
			return Color.BLACK;
		}
	}
	
	public static Color getReadColor(boolean reverse, boolean _gap) {
		if(_gap) {
			return Color.LIGHT_GRAY;
		} else if (reverse) {
			return new Color(64, 249, 155);
		} else {
			return new Color(232, 93, 117);
		}
	}
	public static Color getMultiBaseColor(int xPos, int n, int i, String currentSeq) {
	
		int counter[] = new int[5];
		for (int j = 0; j < n; j++) {
			int pos = xPos + i * n + j;
			char currentChar = Character.toUpperCase(currentSeq.charAt(pos));
			switch (currentChar) {
			case 'A':
				counter[0]++;
				break;
			case 'C':
				counter[1]++;
				break;
			case 'T':
				counter[2]++;
				break;
			case 'G':
				counter[3]++;
				break;
			default:
				counter[4]++;
				
				
			}
		}
		int m = 0;
		for (int h = 1; h < 5; h++) {
			if (counter[h] > counter[m]) {
				m = h;
			}
			
		}
		
		if (counter[m] > Arrays.stream(counter).sum() / 2) {
			switch(m) {
			case 0:
				return new Color(184, 180, 45);
			case 1:
				return new Color(105, 122, 33);
			case 2:
				return new Color(65, 123, 142);
			case 3:
				return new Color(221, 64, 58);
			default:
				return Color.BLACK;
			}
		}
		else {
			return Color.LIGHT_GRAY;
		}
	}
	
	
}
