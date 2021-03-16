package parser;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import parser.Lexer.Lexeme;
import parser.Utilities.IntRef;

public class KoddQL {
	
	public HashMap<String, Node> variables = new HashMap<String, Node>();
	public ArrayList<String> queries = new ArrayList<String>();
	public ArrayList<Notification> _warnings = new ArrayList<Notification>();
	public ArrayList<Notification> _errors = new ArrayList<Notification>();
	public DBMS dbms;
	
	
	public void toSQL(String code) {
		Syntaxer.syntaxRoot(Lexer.lex( Parser.parse(new ByteArrayInputStream(code.getBytes()))), this);
	}
	
	public void toSQL(String[] code) {
		for(String line : code) {
			Syntaxer.syntaxRoot(Lexer.lex( Parser.parse(new ByteArrayInputStream(line.getBytes()))), this);
		}
	}
	
	// Diversion from enumeration naming conventions is intended.
	public enum DBMS {
		Oracle, MySQL, PostgreSQL, MS_Access
	}
	
	
	//TODO: variables formatting 
	//TODO: fix AND
	public static void main(String[] args) throws Exception {
		KoddQL instance = new KoddQL();
		instance.toSQL(
						//"database[number ~ 10 & 50]customers -> RESULT;" 
				"(data, wiki, table)[field, field2] & table -> RESULT"
				);
		for(String str : instance.queries)
			System.out.println(str);
		
		System.out.println();
		
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String,Node> entry : instance.variables.entrySet()) {
			entry.getValue().toSQL(sb);
			sb.append(" -> ");
			sb.append(entry.getKey());
			System.out.println(sb.toString());
		}
		
		System.out.println();
		
		for(Notification notice : instance._warnings) {
			System.out.println(notice.toString());
		}
		
		System.out.println();
		
		for(Notification notice : instance._errors) {
			System.out.println(notice.toString());
		}
	}
}

// frequentCustomers & (customers[id, name, surname]table[homeAddress, phoneNumber]) -> RESULT;