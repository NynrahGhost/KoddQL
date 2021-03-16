package parser;

import java.util.HashMap;
import java.util.List;

import parser.Lexer.Lexeme;

public class Notification {
	
	private Type type;
	private short column;
	private short line;
	private String info;
	
	private static String _at_column = " at: Column ";
	private static String _row = ", Row "; 
	
	public static HashMap<Type, String> presets;

	static {
		presets = new HashMap<Type, String>();
		
		presets.put(Type.WARNING, "WARNING: ");
		presets.put(Type.WARNING_UNUSED_VARIABLE, "WARNING: Unused variable ");
		
		presets.put(Type.ERROR, "ERROR: ");
		presets.put(Type.ERROR_UNDECLARED_VARIABLE, "ERROR: Undeclared variable ");
		presets.put(Type.ERROR_MULTIPLE_ASSIGNS, "ERROR: Multiple assigns in one query ");
		presets.put(Type.ERROR_NOT_NAME_IN_GROUP, "ERROR: Unexpected identifier in group ");
		presets.put(Type.ERROR_NOT_NAME_IN_PROJECTION_OR_EQUIJOIN, "ERROR: Unexpected identifier in projection or equijoin ");
	}
	
	
	public Notification(Type type, int column, int line, String info) {
		this.type = type;
		this.column = (short)column;
		this.line = (short)line;
		this.info = info;
	}
	
	public Notification(Type type, List<Lexeme> list, Lexeme value, String info) {
		this.type = type;
		
		Lexeme tmpValue = list.get(0);
		for(int i = 1; tmpValue != value; ++i) {
			if(tmpValue == (Lexeme.SEMICOLON)) {
				++column;
				++line;
			} else {
				line += tmpValue.toString().length();
			}
			tmpValue = list.get(i);
		}
		
		this.info = info;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(presets.get(type)).append(info).append(_at_column).append(line).append(_row).append(column).toString();
	}
	
	/*public static enum Type {
		WARNING, UNUSED_VARIABLE, 
		
		ERROR, UNDECLARED_VARIABLE, MALFORMED_MULTIPLE_ASSIGNS, ERROR_NOT_NAME_IN_PROJECTION_OR_EQUIJOIN;
	}*/
	
	public static enum Type {
		NOTE(0),
		CAUTION(1),
		WARNING(2), WARNING_UNUSED_VARIABLE(2+4* 1),
		ERROR(3), ERROR_UNDECLARED_VARIABLE(3+4* 1), ERROR_MULTIPLE_ASSIGNS(3+4* 2), ERROR_NOT_NAME_IN_GROUP(3+4* 3), ERROR_NOT_NAME_IN_PROJECTION_OR_EQUIJOIN(3+4* 4);
		
		private int value;

	    Type(int value) {
	        this.value = value;
	    }
	}
}
