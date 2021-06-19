grammar SpuddX;

/*
	Parser
*/

domain: (var_decls)+
		(dd_decls)*
		(env_def)?
		EOF 
		;

var_decls : LP 'variables' (rv_decl)+ RP ; 		# StateVarDecls
		  | LP 'observations' (rv_decl)+ RP ;	# ObsVarDecls
		  | LP 'actions' (rv_decl)+ RP ; 		# ActionVarDecls

dd_decls : LP 'dd' dd_name dd_expr RP ;

dd_expr : dd_decl 											# AtomicExpr
		| op=(OP_ADD | OP_SUB) term=dd_expr 				# NegExpr
		| LP dd_expr RP										# ParenExpr
		| left=dd_expr op=(OP_MUL | OP_DIV) right=dd_expr	# MultDivExpr
		| left=dd_expr op=(OP_ADD | OP_SUB) right=dd_expr	# AddSubExpr
		;
		
dd_decl : LP variable_name (dd_child)+ RP 	# DDDecl
		| dd_leaf 							# DDleaf
		| same_dd_decl						# SameDD
		| dd_ref							# DDRef
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

env_def : LP 'env' (actiondbn_def)+ RP ;

actiondbn_def : LP 'actiondbn' actions (cpd_def)* RP ;
actions : (IDENTIFIER)+ ;

cpd_def : LP variable_name dd_decl RP ;

dd_name : IDENTIFIER;
variable_name : IDENTIFIER;
var_value : IDENTIFIER ;

OP_ADD : '+' ;
OP_SUB : '-' ;
OP_MUL : '*' ;
OP_DIV : '/' ;

IDENTIFIER: [_]?[a-zA-Z][a-zA-Z0-9_']* ;
FLOAT_NUM: [0-9]*'.'[0-9]+ ;

LP : '(' ;
RP : ')' ;

WS : [ \t\r\n]+ -> skip ;