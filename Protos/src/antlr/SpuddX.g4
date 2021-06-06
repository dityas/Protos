grammar SpuddX;

/*
	Parser
*/

domain: state_var_decl
		obs_var_decl
		actions_decl
		dd_decl_block
		EOF 
		;

state_var_decl : LPAREN 'variables' (rv_decl)+ RPAREN ;
obs_var_decl : LPAREN 'observations' (rv_decl)+ RPAREN ;
actions_decl : LPAREN 'actions' (act_decl)+ RPAREN ;

dd_decl_block : (dd_decls)* ; 
dd_decls : LPAREN 'dd' VARNAME dd_decl RPAREN ;
dd_decl : LPAREN VARNAME (dd_child)+ RPAREN
		| dd_leaf
		;

dd_child : LPAREN VARVAL dd_decl RPAREN ;
dd_leaf : FLOAT_NUM ;

rv_decl : LPAREN VARNAME (var_value)+ RPAREN 
		;
		
act_decl : LPAREN AGENTNAME (var_value)+ RPAREN ;
			
var_value : VARVAL ;


VARNAME: [A-Z][a-zA-Z0-9_]* ;
AGENTNAME: [A-Z][a-zA-Z0-9_]* ;
VARVAL: [a-z][a-z0-9_]+ ;
FLOAT_NUM: [0-9]*'.'[0-9]+ ;

LPAREN : '(' ;
RPAREN : ')' ;

WS : [ \t\r\n]+ -> skip ;