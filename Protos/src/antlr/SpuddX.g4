grammar SpuddX;

/*
	Parser
*/

domain: var_decls
		(dd_decls)*
		(model_decl)*
		EOF 
		;

var_decls : LP 'variables' (rv_decl)+ RP ;

model_decl : pomdp_def 		# POMDPDef
		   | dbn_def 		# DBNDef
		   ;
		   
pomdp_def : LP 'model' POMDP agent_name
			 var_list
			 actions_list
			 RP
			;
			
var_list : LP 'variables' (IDENTIFIER)+ RP ;
actions_list : RP 'actions' (IDENTIFIER)+ RP ;
agent_name : IDENTIFIER ;

dd_decls : LP 'dd' dd_name dd_expr RP ;

dd_expr : dd_decl 											# AtomicExpr
		| op=(OP_ADD | OP_SUB) term=dd_expr 				# NegExpr
		| LP dd_expr RP										# ParenExpr
		| left=dd_expr op=(OP_MUL | OP_DIV) right=dd_expr	# MultDivExpr
		| left=dd_expr op=(OP_ADD | OP_SUB) right=dd_expr	# AddSubExpr
		;
		
dd_decl : LP variable_name (LP var_value dd_expr RP)+ RP 	# DDDecl
		| dd_leaf 											# DDleaf
		| same_dd_decl										# SameDD
		| dd_ref											# DDRef
		| variable_name var_value 							# DDDeterministic
		| variable_name UNIFORM 							# DDUniform
		;
		
dd_ref : dd_name
	   | LP dd_name RP
	   ;

dd_leaf : FLOAT_NUM 
		| LP FLOAT_NUM RP
		;
		
same_dd_decl : LP 'SAME' variable_name RP;

rv_decl : LP variable_name (var_value)+ RP 
		;

dbn_def : LP 'dbn' dbn_name (cpd_def)* RP ;
cpd_def : LP variable_name dd_expr RP ;

dbn_name : IDENTIFIER ;
dd_name : IDENTIFIER;
variable_name : IDENTIFIER;
var_value : IDENTIFIER ;

POMDP : 'POMDP' ;
UNIFORM : 'uniform' | 'UNIFORM' ;
OP_ADD : '+' ;
OP_SUB : '-' ;
OP_MUL : '*' ;
OP_DIV : '/' ;

IDENTIFIER: [_]?[a-zA-Z][a-zA-Z0-9_']* ;
FLOAT_NUM: [0-9]*'.'[0-9]+ ;

LP : '(' ;
RP : ')' ;

WS : [ \t\r\n]+ -> skip ;