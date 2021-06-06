grammar SpuddX;

/*
	Parser
*/

domain: state_var_decl
		obs_var_decl
		EOF 
		;

state_var_decl : LPAREN 'variables' (rv_decl)+ RPAREN ;
obs_var_decl : LPAREN 'observations' (rv_decl)+ RPAREN ;

rv_decl : LPAREN VARNAME (var_value)+ RPAREN 
		;
			
var_value : VARVAL ;


VARNAME: [A-Z][a-zA-Z0-9_]* ;
VARVAL: [a-z][a-z0-9_]+ ;
LPAREN : '(' ;
RPAREN : ')' ;
WS : [ \t\r\n]+ -> skip ;