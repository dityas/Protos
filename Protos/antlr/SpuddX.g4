grammar SpuddX;

/*
	Parser
*/
/*
domain: (expr)+
		EOF
		;
		
expr: list
	| atom
	| symbol
	| LP expr RP
	;
	
list : LP (expr)* RP ;
atom : FLOAT_NUM ;
symbol : SYMBOL ; 

*/

domain: var_defs
		(all_def)*
		(exec_block)?
		EOF 
		;

var_defs : (var_def)+ ;
var_def : LP 'defvar' var_name LP (var_value)+ RP RP
		;
		   
all_def : model_name			# PreDefModel
		| dd_def 				# DDDef
		| pomdp_def 			# POMDPDef
		| ipomdp_def 			# IPOMDPDef
		| env_def 				# EnvDef
		| dbn_def				# DBNDef
		| pbvi_solv_def 		# PBVISolverDef
		| modelvar_init_def		# ModelVarInitDef
		| LP all_def RP 		# OtherDefParen
		;
 
pbvi_solv_def : LP 'defpbvisolv' type=(POMDP | IPOMDP) solv_name RP;

pomdp_def : LP 'defpomdp' model_name
			states_list
			obs_list
			action_var
			dynamics
			reward
			discount
			RP
			;

ipomdp_def : LP 'defipomdp' model_name
             states_list
             obs_list
             action_var
			 action_j_var
			 model_j_var
			 ec_var
			 frame_def
             dynamics
             reward
             discount
             reachability
             RP
             ;

env_def : LP 'defenv' env_name
		states_list
		obs_list
		dbn_def
		reward
		RP
		;
            
modelvar_init_def : LP 'initmodelvar' var_name
					LP 'frames' var_name RP
					LP (model_init)+ RP 
					RP
				  ;
					
model_init : LP frame_name LP var_value dd_expr RP RP ;
            
states_list	: LP 'S' LP (var_name)+ RP RP ;
obs_list 	: LP 'O' LP (var_name)+ RP RP ;
action_var 	: LP 'A' (var_name)  RP ;
action_j_var 	: LP 'Aj' (var_name)  RP ;
model_j_var 	: LP 'Mj' (var_name)  RP ;
ec_var 	: LP 'EC' (var_name)  RP ;
actions_list 	: LP 'A' LP (var_name)+ RP RP ;
frame_def 	: LP 'Thetaj' var_name (frame_tuple)+ RP ;
frame_tuple : LP var_value model_name RP ;
frame_name: var_value ;

dynamics : LP 'dynamics' (action_model)+ RP ;
action_model : LP action_name all_def RP ;

initial_belief : LP 'b' dd_expr RP ;

reward : LP 'R' (action_reward)+ RP ;
action_reward : LP action_name dd_expr RP ;
		  
discount : LP 'discount' FLOAT_NUM RP ;
reachability : LP 'H' FLOAT_NUM RP ;

dd_def : LP 'defdd' dd_name dd_expr RP ;

dd_expr : dd_decl 											# AtomicExpr
		| op=(OP_ADD | OP_SUB) term=dd_expr 				# NegExpr
		| LP dd_expr RP										# ParenExpr
		| left=dd_expr op=(OP_MUL | OP_DIV) right=dd_expr	# MultDivExpr
		| left=dd_expr op=(OP_ADD | OP_SUB) right=dd_expr	# AddSubExpr
		| '#' LP (var_name)+ RP dd_expr 					# SumoutExpr
		;
		
dd_decl : LP var_name (LP var_value dd_expr RP)+ RP 	# DDDecl
		| dd_leaf 										# DDleaf
		| same_dd_decl									# SameDD
		| dd_ref										# DDRef
		| var_name var_value 							# DDDeterministic
		| var_name UNIFORM 								# DDUniform
		;
		
dd_ref : dd_name
	   | LP dd_name RP
	   ;

dd_leaf : FLOAT_NUM 
		| LP FLOAT_NUM RP
		;
		
same_dd_decl : LP 'SAME' var_name RP;

dbn_def : LP 'defdbn' model_name (cpd_def)* RP ;
cpd_def : LP var_name dd_expr RP ;

exec_block : LP 'run' (exec_expr)* RP;
exec_expr : dd_def													# DDExecDef
		  | 'defpol' policy_name '=' solv_cmd 						# SolvExpr
		  | 'poltree' dd_list model_name policy_name exp_horizon	# PolTreeExpr
		  | LP exec_expr RP 										# ParenExecExpr
		  ;

solv_cmd : 'solve' solv_name dd_list model_name backups exp_horizon;
dd_list : LP (dd_expr)+ RP ;

backups : FLOAT_NUM ;
exp_horizon : FLOAT_NUM ;

env_name : IDENTIFIER ;
policy_name : IDENTIFIER ;
action_name : IDENTIFIER ;
model_name : IDENTIFIER ;
dd_name : IDENTIFIER ;
var_name : IDENTIFIER ;
var_value : IDENTIFIER ;
solv_name : IDENTIFIER ;
pol_name : IDENTIFIER ;

ENV : 'ENV' | 'env' ;
DD : 'DD' | 'dd' ;
POMDP : 'POMDP' | 'pomdp' ;
IPOMDP : 'IPOMDP' | 'ipomdp' ;
DBN : 'DBN' | 'dbn' ;
UNIFORM : 'uniform' | 'UNIFORM' ;
OP_ADD : '+' ;
OP_SUB : '-' ;
OP_MUL : '*' ;
OP_DIV : '/' ;


/* SYMBOL : [a-zA-Z_'+'] ; */

IDENTIFIER: [_]?[a-zA-Z][a-zA-Z0-9_'.]* ;
FLOAT_NUM: [0-9]*['.']?[0-9]+ ;

LP : '(' ;
RP : ')' ;

WS : [ \t\r\n]+ -> skip ;
LINE_COMMENT : '--' ~[\r\n]* -> skip ;

