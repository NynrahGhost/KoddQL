package parser;

import java.util.ArrayList;

public class Lexer {
	
	public static ArrayList<Lexeme> lex (ArrayList<String> list) {
		
		ArrayList<Lexeme> result = new ArrayList<Lexeme>(list.size());
		String str = "";
		
		for(int i = 0; i < list.size(); ++i)
		{
			str = list.get(i);
			
			switch(str) {
			case "(":
				result.add(Lexeme.ROUND_BR_L); break;
			case ")":
				result.add(Lexeme.ROUND_BR_R); break;
			case "[":
				result.add(Lexeme.SQUARE_BR_L); break;
			case "]":
				result.add(Lexeme.SQUARE_BR_R); break;
			
			case "AND":
			case "&":
				result.add(Lexeme.AND); break;
			case "OR":
			case "|":
				result.add(Lexeme.OR); break;
			case "MINUS":
			case "\\":
				result.add(Lexeme.DIVISION); break;
				
			case "<":
				if(list.get(i+1).equals("=")) { //TODO: consider switch
					result.add(Lexeme.OP_LESS_EQUAL);
					++i;
				} else if(list.get(i+1).equals(">")) {
					result.add(Lexeme.OP_NOT_EQUAL);
					++i;
				} else
					result.add(Lexeme.OP_LESS);
				break;
			case ">":
				if(list.get(i+1).equals("=")) {
					result.add(Lexeme.OP_MORE_EQUAL);
					++i;
				} else 
					result.add(Lexeme.OP_MORE); 
				break;
			case "=":
				result.add(Lexeme.OP_EQUAL); break;
			case "NOT":
			case "!":
				result.add(Lexeme.OP_NOT); break;
			case "LIKE":
				result.add(Lexeme.OP_LIKE); break;
			case "BETWEEN":
				result.add(Lexeme.OP_BETWEEN); break;
			case "~":
				if(Character.isDigit(list.get(i+1).charAt(0)))
					result.add(Lexeme.OP_BETWEEN);
				else
					result.add(Lexeme.OP_LIKE);
				break;
			case "IN":
			case "E":
				result.add(Lexeme.OP_IN); break;
			case "DIVISION":
			case ":":
				result.add(Lexeme.OP_DIVISION); break;
			case "-":
				if(list.get(i+1).equals(">")) {
					result.add(Lexeme.ASSIGN);
					++i;
				} //else
					//throw new Exception("Syntax error! Incorrect use of \"-\" at: " + i);
				break;
			case ",":
				result.add(Lexeme.COMMA); break;
			case "/n":
			case ";":
				result.add(Lexeme.SEMICOLON); break;
			default:
				switch(str.charAt(0)) {
				case '"':
				case '\'':
					result.add(new Lexeme.Data(LexemeType.TEXT, str.substring(1, str.length()-1))); break;
				case '`':
					result.add(new Lexeme.Data(LexemeType.NAME, str.substring(1, str.length()-1))); break;
				case '_':
					result.add(new Lexeme.Data(LexemeType.VARIABLE, str.substring(0, str.length()))); break;
				case '-':
					if(str.contains("."))
						result.add(new Lexeme.Data(LexemeType.DECIMAL, Double.parseDouble(str)));
					else
						result.add(new Lexeme.Data(LexemeType.INTEGER, Long.parseLong(str)));
					break;
				default:
					if(Character.isDigit(str.charAt(0))) {
						if(str.contains("."))
							result.add(new Lexeme.Data(LexemeType.DECIMAL, Double.parseDouble(str)));
						else
							result.add(new Lexeme.Data(LexemeType.INTEGER, Long.parseLong(str)));
					} else {
						result.add(new Lexeme.Data(LexemeType.NAME, str));
					}
					break;
				}
			}
		}
		
		System.out.println(result);
		System.out.println();
		
		return result;
	}
	
	public static class Lexeme {
		
		// No need to have multiple instances of identical objects
		public final static Lexeme ROUND_BR_L = new Lexeme(LexemeType.ROUND_BR_L);
		public final static Lexeme ROUND_BR_R = new Lexeme(LexemeType.ROUND_BR_R);
		public final static Lexeme SQUARE_BR_L = new Lexeme(LexemeType.SQUARE_BR_L);
		public final static Lexeme SQUARE_BR_R = new Lexeme(LexemeType.SQUARE_BR_R);
		
		public final static Lexeme AND = new Lexeme(LexemeType.AND);
		public final static Lexeme OR = new Lexeme(LexemeType.OR);
		public final static Lexeme DIVISION = new Lexeme(LexemeType.DIVISION);
		
		public final static Lexeme OP_LESS = new Lexeme(LexemeType.OP_LESS);
		public final static Lexeme OP_LESS_EQUAL = new Lexeme(LexemeType.OP_LESS_EQUAL);
		public final static Lexeme OP_MORE = new Lexeme(LexemeType.OP_MORE);
		public final static Lexeme OP_MORE_EQUAL = new Lexeme(LexemeType.OP_MORE_EQUAL);
		public final static Lexeme OP_EQUAL = new Lexeme(LexemeType.OP_EQUAL);
		public final static Lexeme OP_NOT_EQUAL = new Lexeme(LexemeType.OP_NOT_EQUAL);
		public final static Lexeme OP_NOT = new Lexeme(LexemeType.OP_NOT);
		public final static Lexeme OP_LIKE = new Lexeme(LexemeType.OP_LIKE);
		public final static Lexeme OP_IN = new Lexeme(LexemeType.OP_IN);
		public final static Lexeme OP_BETWEEN = new Lexeme(LexemeType.OP_BETWEEN);
		public final static Lexeme OP_DIVISION = new Lexeme(LexemeType.OP_DIVISION);
		
		public final static Lexeme COMMA = new Lexeme(LexemeType.COMMA);
		public final static Lexeme ASSIGN = new Lexeme(LexemeType.ASSIGN);
		public final static Lexeme SEMICOLON = new Lexeme(LexemeType.SEMICOLON);
		
		//public final static Lexeme RESULT = new Lexeme(LexemeType.RESULT);
		//public final static Lexeme DELETE = new Lexeme(LexemeType.DELETE);
		
		
		public LexemeType type;
		
		public Lexeme(LexemeType type) {
			this.type = type;
		}
		
		@Override
		public String toString() {
			switch(type) {
			case ROUND_BR_L:
				return "(";
			case ROUND_BR_R:
				return ")";
			case SQUARE_BR_L:
				return "[";
			case SQUARE_BR_R:
				return "]";
				
			case AND:
				return "&";
			case OR:
				return "|";
			case DIVISION:
				return "\\";
				
			case OP_LESS:
				return "<";
			case OP_LESS_EQUAL:
				return "<=";
			case OP_MORE:
				return ">";
			case OP_MORE_EQUAL:
				return ">=";
			case OP_EQUAL:
				return "=";
			case OP_NOT_EQUAL:
				return "<>";
			case OP_LIKE:
				return "~";
			case OP_IN:
				return "E";
			case OP_BETWEEN:
				return "~#";
			case OP_DIVISION:
				return ":";
				
			case COMMA:
				return ",";
			case ASSIGN:
				return "->";
			case SEMICOLON:
				return ";";
			default:
				return "???";
			}
		}
	
	
		public static class Data extends Lexeme {
			public Object data;
			
			public Data(LexemeType type, Object data)  {
				super(type);
				this.data = data;
			}
			
			@Override
			public String toString() {
				switch(type) {
				case NAME:
				case VARIABLE:
				case TEXT:
					return data.toString();
				case INTEGER:
					return Long.toString((long)data);
				case DECIMAL:
					return Double.toString((double)data);
				default:
					return "???";
				}
			}
		}
	}
	
	public enum LexemeType {
		AND, OR, DIVISION, 
		OP_LESS, OP_LESS_EQUAL, OP_MORE, OP_MORE_EQUAL, OP_EQUAL, OP_NOT_EQUAL, OP_NOT, OP_LIKE, OP_IN, OP_BETWEEN, OP_DIVISION,
		ROUND_BR_L, ROUND_BR_R, SQUARE_BR_L, SQUARE_BR_R, COMMA, ASSIGN, SEMICOLON,
		NAME, VARIABLE, TEXT, INTEGER, DECIMAL;
		
		public int getPriority() {
			switch(this) {
			case NAME: return 10;
			case ROUND_BR_L: return 10;
			case SQUARE_BR_L: return 9;
			case DIVISION: return 7;
			case AND: return 7;
			case OR: return 7;
			case ROUND_BR_R: return 0;
			case ASSIGN: return 0;
			default: return -1;
			}
		}
	}
}
