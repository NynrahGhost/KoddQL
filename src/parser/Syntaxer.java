package parser;

import java.util.ArrayList;
import java.util.HashMap;

import parser.Lexer.Lexeme;
import parser.Lexer.LexemeType;
import parser.Node.NodeType;
import parser.Utilities.IntRef;


public class Syntaxer {
	
	// ��������������
	// customers RENAME id, name, surname AS a, b, c
	// customers[id @ param1, name @ param2, surname @ param3] -> 
	
	// ��������� ��������������
	// customers[id, name, surname] -> customers[a,b,c];
	// (customers & table)[a,b,c] -> RESULT[COUNT(a), name, surname]
	
	
	
	// ����������� (ALTER)
	// [ENABLE UNIQUE(id), DROP PRIMARY KEY(id)] -> customers;
	// [RENAME(id,param1)] -> customers;

	// (t1,t2,t3,t4)[t1.count > t2.count] -> RESULT;
	
	
	// ������ (SELECT)
	// customers[id, name, surname] -> RESULT;
	
	// ������� (INSERT INTO)
	// customers AND {id : 1, name : "name", surname : "surname"} -> customers;
	
	// ������� � ���� ���� ����� (INSERT INTO IN)
	// customers AND {id : 1, name : "name", surname : "surname"} -> customers{dataBase.db};
	
	// ������� � ���� ������� (INSERT INTO SELECT)
	// records[country="Ukraine"][id, name, surname] -> customers[id, name, surname];
	
	// ������� (UPDATE)
	// [count, name, surname]{1, "name", "surname"} -> customers[id = 1];
	
	// ��������� (DELETE TABLE)
	// customers[id = 1] -> DELETE;
	
	// �������� ������� (TRUNCATE TABLE)
	// customers[] -> DELETE;
	
	// ��������� ������� (DROP TABLE)
	// customers -> DELETE;
	
	// ��������� ������� (CREATE TABLE)
	// [id INT, name TEXT(20), surname TEXT(20)] -> customers;
	
	// ����������� �������� �������
	// customers[] -> [NOT NULL]
	
	// ������������ ���� ����� (USE DATABASE)
	// {Database} -> USE;
	
	// ��������� ���� ����� (CREATE DATABASE)
	// {Database} -> CREATE;
	
	// ���������� ���� ����� (BACKUP DATABASE)
	// {Database} -> "C:\\backups\Database.db"
	
	
	
	// ���������� �'�������
	// customers[]id

	// ������� �'�������
	// customers[L.id = R.id]table
	
	// ˳�� ������ �'�������
	// customers>[L.id = R.id]table
	
	// ����� ������ �'�������
	// customers[L.id = R.id]<table
	
	// ����� ������ �'�������
	// customers>[L.id = R.id]<table
	
	// ���-�'�������
	// customers[id]table
	
	// ����-�'�������
	// customers[ordersCount > number]table
	
	// ����-���������
	// customers[credit > 1]
	
	
	// GROUP BY
	// -> RESULT^[name]
	
	// ORDER BY
	// -> RESULT[ > name, < surname]
	
	// CASE
	/* (id > 50 : 'One', id ~ 10 & 50 : 'Two', 'Three')
	 * 
	 * (
	 * 		id > 50 : 'One',
	 * 		id ~ 10 & 50 : 'Two',
	 * 		'Three'
	 * );
	*/
	
	// IIF
	// (id > 50 : 'Then', 'Else')
	
	
	// Procedure
	// table[@arg1, @arg2] -> procedure(@arg1, @arg2)
	
	
	
	
	public static Node syntaxRoot(ArrayList<Lexer.Lexeme> list, KoddQL instance) {
		Node root = null;
		IntRef shift = new IntRef(0);
		
		while(shift.value < list.size()) {
			root = syntax(root, list, shift, instance);
			//if(root.getType() == NodeType.ASSIGN)
			//	System.out.println(root);
			++shift.value;
		}
		
		System.out.println(root);
		System.out.println();
		
		return root;
	}
	
	public static Node syntax(Node previous, ArrayList<Lexer.Lexeme> list, IntRef shift, KoddQL instance) {
		switch(list.get(shift.value).type) {

		case NAME:
			return new Node.Lexeme(list.get(shift.value));
		case VARIABLE:
			String var = list.get(shift.value).toString();
			if(instance.variables.containsKey(var))
				return instance.variables.get(var);
			else {
				instance._errors.add(new Notification(Notification.Type.ERROR_UNDECLARED_VARIABLE, 0, 0, var));
				return new Node.Lexeme(list.get(shift.value));
			}
			
		case SEMICOLON:
			return previous;
			
		case ASSIGN:
			++shift.value;
			
			if(list.get(shift.value).type == LexemeType.VARIABLE) {
				instance.variables.put(list.get(shift.value).toString(), previous);
				return null;
			} else {
				StringBuilder sb = new StringBuilder();
				Node node = syntax(null, list, shift, instance);
				
				node = new Node.Assign(previous, node);
				node.toSQL(sb);
				instance.queries.add(sb.toString());
				
				return node;
			}
			
		case ROUND_BR_L:
			++shift.value;
			if(list.get(shift.value + 1) == Lexeme.COMMA) {
				Node.Group node = new Node.Group();
				
				while(list.get(shift.value + 1) != Lexeme.ROUND_BR_R) {
					if(list.get(shift.value).type != LexemeType.NAME)
						instance._errors.add(new Notification(
								Notification.Type.ERROR_NOT_NAME_IN_GROUP,
								list,
								list.get(shift.value),
								list.get(shift.value).toString()));
					node.nodes.add(new Node.Lexeme(list.get(shift.value)));
					shift.value += 2;
				}
				node.nodes.add(new Node.Lexeme(list.get(shift.value)));
				++shift.value;
				
				return node;
			}
			else
				return syntaxGroup(list, shift, instance);
			
		case ROUND_BR_R:
			return previous;
		
		case SQUARE_BR_L:
			ArrayList<Lexeme> arr = new ArrayList<Lexeme>();
			
			if(list.get(++shift.value + 1) == Lexeme.COMMA) {			// If projection / equi-join
								
				while(list.get(shift.value + 1) != Lexeme.SQUARE_BR_R) {	// If not bracket, then comma
					if(list.get(shift.value).type != LexemeType.NAME)
						instance._errors.add(new Notification(
								Notification.Type.ERROR_NOT_NAME_IN_PROJECTION_OR_EQUIJOIN,
								list,
								list.get(shift.value),
								'"' + list.get(shift.value).toString() + '"'));
					arr.add(list.get(shift.value));
					shift.value += 2;
				}
				arr.add(list.get(shift.value));
				++shift.value;
				
				if(
						(list.get(++shift.value).type == LexemeType.NAME) | 
						(list.get(shift.value).type == LexemeType.VARIABLE) |
						(list.get(shift.value).type == LexemeType.ROUND_BR_L)
				) {
					previous = new Node.EquiJoin(
							previous,
							syntax(previous, list, shift, instance),
							arr
					);
					
					return previous;
				} else {
					previous = new Node.Projection(
							previous,
							arr
					);
					--shift.value;
					return previous;
				}
			} else {													// Else selection / theta-join

				while(list.get(shift.value + 1) != Lexeme.SQUARE_BR_R) {
					arr.add(list.get(shift.value));
					++shift.value;
				}
				arr.add(list.get(shift.value));
				++shift.value;
				
				if(
						(list.get(++shift.value).type == LexemeType.NAME) | 
						(list.get(shift.value).type == LexemeType.VARIABLE) |
						(list.get(shift.value).type == LexemeType.ROUND_BR_L)
				) {
					previous = new Node.Join(
							previous,
							syntax(previous, list, shift, instance),
							arr
					);
					
					return previous;
				} else {
					previous = new Node.Selection(
							previous,
							arr
					);
					--shift.value;
					return previous;
				}
			}
			
		case AND:
			Node.And and = new Node.And();
			
			and.left = previous;
			++shift.value;
			and.right = syntax(null, list, shift, instance);
			
			return and;
			//previous = and;
			//++shift;
			//continue MAIN_LOOP;
			
		case OR:
			Node.Or or = new Node.Or();
			
			or.left = new Node.Lexeme(list.get(shift.value - 1));
			++shift.value;
			or.right = syntax(null, list, shift, instance);
			
			return or;
			
		default:
			//throw new Exception("Invalid syntax! Started with: " + list.get(shift).type.name());
			return previous;
		}
	}
	
	public static Node syntaxGroup(ArrayList<Lexer.Lexeme> list, IntRef shift, KoddQL instance) {
		Node root = null;
		
		// Operations are implemented as static members, so address check is safe
		while(list.get(shift.value) != Lexeme.ROUND_BR_R) {
			root = syntax(root, list, shift, instance);
			++shift.value;
		}
		
		return root;
	}
}





