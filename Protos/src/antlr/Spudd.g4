grammar Spudd;

@header {
	package thinclab.spuddx_parser;
}

/*
	Parser
*/

domain: state_var_decl
		obs_var_decl
		EOF 
		;

state_var_decl : LPAREN 'variables' rv_decl RPAREN ;
obs_var_decl : LPAREN 'observations' rv_decl RPAREN ;

rv_decl : LPAREN VARNAME var_values RPAREN ;

var_values : var_value var_values
			|
			;
			
var_value : VARVAL ;


VARNAME: [A-Z][a-zA-Z0-9]+ ;
VARVAL: [a-z][a-z0-9]+ ;
LPAREN : '(' ;
RPAREN : ')' ;