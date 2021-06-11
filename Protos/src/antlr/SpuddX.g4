grammar SpuddX;

/*
	Parser
*/

domain: state_var_decl
		(obs_var_decl)?
		(actions_decl)?
		(dd_decls)*
		(model_defs)*
		EOF 
		;

state_var_decl : LP 'variables' (rv_decl)+ RP ;
obs_var_decl : LP 'observations' (rv_decl)+ RP ;
actions_decl : LP 'actions' (rv_decl)+ RP ;

dd_decls : LP 'dd' dd_name dd_decl RP ;
dd_decl : LP variable_name (dd_child)+ RP
		| dd_leaf
		| same_dd_decl
		| dd_ref
		;
		
dd_ref : dd_name
	   | LP dd_name RP
	   ;

dd_child : LP var_value dd_decl RP ;
dd_leaf : FLOAT_NUM 
		| LP FLOAT_NUM RP
		;
		
same_dd_decl : LP 'SAME' variable_name RP;

rv_decl : LP variable_name (var_value)+ RP 
		;
			
model_defs : bn_def ;

bn_def : LP 'BN' variable_name (cpd)+ RP ;		
cpd : LP variable_name dd_decl RP ; 

dd_name : IDENTIFIER;
variable_name : IDENTIFIER;
var_value : IDENTIFIER ;

IDENTIFIER: [_]?[a-zA-Z][a-zA-Z0-9_']* ;
FLOAT_NUM: [0-9]*'.'[0-9]+ ;

LP : '(' ;
RP : ')' ;

WS : [ \t\r\n]+ -> skip ;