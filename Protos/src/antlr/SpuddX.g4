grammar SpuddX;

/*
	Parser
*/

domain: var_decls
		(dd_decls)*
		(model_decl)*
		(env_decl)?
		EOF 
		;

var_decls : LP 'variables' (rv_decl)+ RP ;

model_decl : LP model_name model_def RP
		   ;
		   
model_def : LP model_def RP 	# ModelDefParen
		  | pomdp_def 			# POMDPDef
		  | dbn_def				# DBNDef
		  | variable_name 		# ModelRef
		  ;
		   
pomdp_def : LP POMDP
			states_list
			obs_list
			action_var
			dynamics
			initial_belief
			reward
			discount
			RP
			;
			
env_decl : LP env_name env_def RP ;

env_def : LP ENV
		  states_list
		  obs_list
		  actions_list
		  agents_list
		  dynamics
		  RP
		  ;
			
states_list	: LP 'S' LP (variable_name)+ RP RP ;
agents_list : LP 'Agents' LP (variable_name)+ RP RP ;
obs_list 	: LP 'O' LP (variable_name)+ RP RP ;
action_var 	: LP 'A' (variable_name)  RP ;
actions_list 	: LP 'A' LP (variable_name)+ RP RP ;

dynamics : LP 'dynamics' (action_model)+ RP ;
action_model : LP action_name model_def RP ;

initial_belief : LP 'b' dd_expr RP ;

reward : LP 'R' (action_reward)+ RP ;
action_reward : LP action_name dd_expr RP ;
		  
discount : LP 'discount' FLOAT_NUM RP ;

dd_decls : LP dd_name LP DD dd_expr RP RP ;

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

dbn_def : LP DBN (cpd_def)* RP ;
cpd_def : LP variable_name dd_expr RP ;

env_name : IDENTIFIER ;
action_name : IDENTIFIER ;
model_name : IDENTIFIER ;
dd_name : IDENTIFIER;
variable_name : IDENTIFIER;
var_value : IDENTIFIER ;

ENV : 'ENV' | 'env' ;
DD : 'DD' | 'dd' ;
POMDP : 'POMDP' | 'pomdp' ;
DBN : 'DBN' | 'dbn' ;
UNIFORM : 'uniform' | 'UNIFORM' ;
OP_ADD : '+' ;
OP_SUB : '-' ;
OP_MUL : '*' ;
OP_DIV : '/' ;

IDENTIFIER: [_]?[a-zA-Z][a-zA-Z0-9_']* ;
FLOAT_NUM: [0-9]*['.']?[0-9]+ ;

LP : '(' ;
RP : ')' ;

WS : [ \t\r\n]+ -> skip ;