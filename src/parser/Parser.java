package parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Parser {
	
	public static ArrayList<String> parse(InputStream stream) {
		
		ArrayList<String> result = new ArrayList<String>();
		StringBuilder stringBuilder = new StringBuilder();
		int character = 0;
		//State state = State.NAME;
		
		
		//Initial character value
		try {
			character = stream.read();
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		
		//Check if stream has ended
		while(character != -1) {
			switch((char) character) {
			case '[':
			case ']':
			case '(':
			case ')':
			case '<':
			case '>':
			case '=':
			case ':':
			case '!':
			case '&':
			case '|':
			case '/':
			case '-':
			case ',':
			case ';':
			case '\n':
			//case ' ':
				if(stringBuilder.length() != 0)
				{
					result.add(stringBuilder.toString());
					stringBuilder.setLength(0);
				}
				result.add(Character.toString((char)character));
				break;
			case ' ':
				if(stringBuilder.length() != 0)
				{
					result.add(stringBuilder.toString());
					stringBuilder.setLength(0);
				}
				break;
//SingleQuotes			
			case '\'':
				if(stringBuilder.length() != 0)
				{
					result.add(stringBuilder.toString());
					stringBuilder.setLength(0);
				}
				try {
					character = stream.read();
				} catch (IOException e) { 
					e.printStackTrace(); 
				}
				stringBuilder.append('\'');
				while((character != -1) & (character != (int)'\'')) {
					stringBuilder.append((char) character);
					try {												//TODO: Proper catch for this exception
						character = stream.read();
					} catch (IOException e) { 
						e.printStackTrace(); 
					}
				}
				stringBuilder.append('\'');
				result.add(stringBuilder.toString());
				stringBuilder.setLength(0);
				break;
//DoubleQuotes			
			case '"':
				if(stringBuilder.length() != 0)
				{
					result.add(stringBuilder.toString());
					stringBuilder.setLength(0);
				}
				try {
					character = stream.read();
				} catch (IOException e) { 
					e.printStackTrace(); 
				}
				stringBuilder.append('"');
				while((character != -1) & (character != (int)'"')) {
					stringBuilder.append((char) character);
					try {												//TODO: Proper catch for this exception
						character = stream.read();
					} catch (IOException e) { 
						e.printStackTrace(); 
					}
				}
				stringBuilder.append('"');
				result.add(stringBuilder.toString());
				stringBuilder.setLength(0);
				break;
//GravisQuotes			
			case '`':
				if(stringBuilder.length() != 0)
				{
					result.add(stringBuilder.toString());
					stringBuilder.setLength(0);
				}
				try {
					character = stream.read();
				} catch (IOException e) { 
					e.printStackTrace(); 
				}
				stringBuilder.append('`');
				while((character != -1) & (character != (int)'`')) {
					stringBuilder.append((char) character);
					try {												//TODO: Proper catch for this exception
						character = stream.read();
					} catch (IOException e) { 
						e.printStackTrace(); 
					}
				}
				stringBuilder.append('`');
				result.add(stringBuilder.toString());
				stringBuilder.setLength(0);
				break;
				
			default:
				stringBuilder.append((char) character);
				break;
			}
		
			try {
				character = stream.read();
			} catch (IOException e) { 
				e.printStackTrace(); 
			}
		}
		
		if(stringBuilder.length() != 0)
			result.add(stringBuilder.toString());
		if(!result.get(result.size()-1).equals(";"))
			result.add(";");
		
		System.out.println(result);
		System.out.println();
		
		return result;
	}
	
	public enum State {
		NAME, STRING_SINGLE, STRING_DOUBLE, STRING_GRAVE
	}
}
