# Protos
## A level 1 factored Interactive POMDP (I-POMDP) solver

Protos is a level 1 factored I-POMDP solver developed at [THINC Lab @ UGA](http://thinc.cs.uga.edu/). It uses Jesse Hoey's implementation of the Symbolic Perseus POMDP solver and extends it to solve IPOMDPs. 
The pre built JAR file is in the `Protos/build/` directory.

******

### Domain file format
The I-POMDP domain file follows the SPUDD format for representing ADDs in plaintext. Using the original SPUDD parser, I have hacked together some functionality to parse I-POMDP domains (the parser does not recursively parse I-POMDP strategy levels hence restricted to level 1 I-POMDPs). For POMDPs, the parser works exactly like the symbolic Perseus parser. The following grammar is from [Dr. Pascal Poupart's symbolic Perseus](https://cs.uwaterloo.ca/~ppoupart/software/symbolicPerseus/problems/SYNTAX.txt) with a few changes.
For specifying I-POMDP domains the format is as follows,

```
<problem> ::= <decl_list> 

<decl_list> ::= <decl> <decl_list> | NIL

<decl> ::= <state_var_decl> | <obs_var_decl> | <unnormalized_decl> | <dd_decl> | <action_decl> | <reward_decl> | <discount_decl> | <tolerance_decl> 

<state_var_decl> ::= "(" "variables" <var_def_list> ")"

<obs_var_decl> ::= "(" "observations" <var_def_list> ")"

<var_def_list> ::= <var_def> <var_def_list> | NIL

<var_def> ::= "(" <var_name> <val_name_list> ")"

<var_name> ::= STRING

<val_name_list> ::= <val_name> <val_name_list> | NIL

<val_name> ::= STRING

<unnormalized_decl> ::= "unnormalized" | "unnormalised"


%%%%%%%%%%%%%%%%% decision diagram %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% examples:
%%
%%     // example of a constant: p1 = 5
%%     dd p1 
%%         (5) 
%%     enddd
%%
%%     // example of a tree
%%     dd cpt1 
%%         (weather_report (rainy (0.1)) 
%%                         (sunny (0.3)) 
%%                         (cloudy (0.6))) 
%%     enddd
%%
%%     // example of artithmetic operations
%%     dd cpt2 
%%         (actual_weather' (rainy [* (p2) (0.2)])
%%                          (sunny [* (p2) (0.3)])
%%                          (cloudy [* (p2) (0.5)]))
%%     enddd
%%
%%     // another tree
%%     dd rewardFn 
%%         (actual_weather (rainy (-5)) 
%%                         (sunny (5)) 
%%                         (cloudy (-1)))
%%     enddd
%%
%%     // example for the short hand "SAME<var_name>" which is equivalent
%%     // to the identity function.  The following two dds are equivalent:
%%     dd identity1
%%         (SAMEvar)
%%     end
%%     dd identity2 
%%         (var (true  (var' (true (1))
%%                           (false (0))))
%%              (false (var' (true (0))
%%                           (false (1)))))
%%     enddd
%%
%%     // example for the short hand "<var_name><val_name>", 
%%     // which is equivalent to the deterministic function 
%%     // that assigns 1 to val_name and probability 0 to 
%%     // all other values.  The following two dds are equivalent:
%%     dd deterministic1
%%         (vartrue)
%%     enddd
%%     dd deterministic2
%%         (var (true (1)) (false (0)))
%%     enddd
%%    
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

<dd_decl> ::= "dd" <dd_name> <dd> "enddd"

<dd_name> ::= STRING

<dd> ::= "(" <dd_name> ")" | "(" <variable_node> ")" | "(" identity_mapping ")" | "(" <value_mapping> ")" | "(" NUMBER ")" | "[" <op_node> "]"

<variable_node> ::= <var_name> <child_list>

<child_list> ::= <child> <child_list> | NIL

<child> ::= "(" <val_name> <dd> ")"

<identity_mapping> ::= SAME<var_name>

<value_mapping> ::= <var_name><val_name>

<op_node> ::= <normalize_op_node> | <nary_op_node>

<normalize_op_node> ::= "#" <var_name> <dd>

<nary_op_node> ::= <nary_op> <dd_list>

<nary_op> ::= "+" | "*"

<dd_list> ::= <dd> <dd_list> | NIL
                                                              
<action_decl> ::= "action" <act_name> <def_list> "endaction"

<act_name> ::= STRING

<def_list> ::= <def> <def_list> | NIL

<def> ::= <state_cpt_def> | <obs_fn_def> | <cost_def>

<state_cpt_def> ::= <var_name> <dd>

<obs_fn_def> ::= "observe" <obs_cpt_def_list> "endobserve"

<obs_cpt_def_list> ::= <obs_cpt_def> <obs_cpt_def_list> | NIL

<obs_cpt_def> ::= <var_name> <dd>

<cost_def> ::= "cost" <dd>

<reward_decl> ::= "reward" <dd>

<discount_def> ::= "discount" <dd>

<tolerance_def> ::= "tolerance" <dd>

<comment> ::= "//" <string_list> EOL

<string_list> ::= STRING <string_list> | NIL

```

### Online IPOMDP simulator
Simulates a 2 agent interaction between the I-POMDP agent and a randomly sampled POMDP agent from the I-POMDP frames. The I-POMDP is solved online.

#### Usage

```
java -Xms50g -Xmx55g -cp Protos.jar thinclab.executables.RunSimulations

 -b <arg>                    number of backups in each round
 -d <arg>                    path to the domain file
 -e,--ssga                   use SSGA expansion? (5 perseus rounds and 30
                             iterations of exploration)
 -i,--ipomdp                 set if the domain is a IPOMDP domain
 -j,--default-policy <arg>   use default policy for L1?
 -k,--random-policy          use random policy for L1?
 -l                          log to file in results dir?
 -n <arg>                    look ahead for IPOMDPs / SSGA depth for
                             POMDPs
 -p,--pomdp                  set if the domain is a POMDP domain
 -q,--cyberdec-reactive      use reactive solver (only for cyber
                             deception domain)
 -r <arg>                    number of symbolic perseus rounds (always 1
                             for IPOMDPs)
 -s,--output <arg>           directory where result files are to be
                             stored. (Should be an existing dir)
 -x,--sim <arg>              run stochastic simulation for given
                             length
 -y <arg>                    number of simulation trials
```

#### Example

Here is the solver simulating the multi-agent tiger problem for 10 steps.

Run:

```
java -Xms55g -Xmx60g -cp Protos.jar thinclab.executables.RunSimulations -b 100 
-d /path/to/domain_file/tiger.L1.F3.agnostic.domain -s /path/to/results/dir/ 
-n 5 --ipomdp --sim 10 -y 1 -ssga
```

The output directory will contain:
- policy graph of every solved POMDP frame. (dot file)
- policy tree of every solved POMDP frame (not all POMDP policies can be compressed into finite policy graphs).
- human readable summary of the simulation
- complete trace of the simulation in json format.

Example of the summary file:
```
Interaction step 1 summary:

State is,
tiger-location is tiger-right

Agent i at L1 believes,
Theta_j is likely theta/1 with probability 0.33333334
tiger-location is likely tiger-left with probability 0.5
i believes that j believes tiger-location is likely tiger-left with probability 0.5 with probability 0.33333334
This leads agent i to believe that agent j will perfrom action listen with a probability 0.33333334

Agent i takes action listen and observes [growl-right, silence]
i expects average discounted reward 0.8530685000000006

Agent j at L0 believes,
tiger-location is likely tiger-left with probability 0.5

Agent j takes action listen and observes [growl-right]
j expects average discounted reward -1.7139059999999995

Interaction ends

Interaction step 2 summary:

State is,
tiger-location is tiger-right

Agent i at L1 believes,
Theta_j is likely theta/0 with probability 0.33333334
tiger-location is likely tiger-right with probability 0.85
i believes that j believes tiger-location is likely tiger-right with probability 0.85 with probability 0.24833333
This leads agent i to believe that agent j will perfrom action listen with a probability 0.24833333

Agent i takes action listen and observes [growl-right, silence]
i expects average discounted reward 4.0824584

Agent j at L0 believes,
tiger-location is likely tiger-right with probability 0.75

Agent j takes action listen and observes [growl-right]
j expects average discounted reward -0.8806412874999993

Interaction ends
.
.
.
```
The complete simulation trace is captured in JSON format in the simX.json file where X is the trial number.
The schema for the json trace is:
```
[
    {
        "beliefI": {

            # Agent i's beliefs over models of agent j
            "M_j": [
                {
                    "model": {
                        "belief_j": {
                            "<state variable 1>": {
                                "<state value 1>": <probability>,
                                "<state value 2>": <probability>
                            }
                        },

                        "A_j": "<agent j's optimal action>",
                        "Theta_j": "<frame of agent j>"
                    },
                    
                    "prob": <agent i's belief over this M_j>
                },
                {
                    "model": {
                        "belief_j": {
                            "<state variable 1>": {
                                "<state value 1>": <probability>,
                                "<state value 2>": <probability>
                            }
                        },

                        "A_j": "<agent j's optimal action>",
                        "Theta_j": "<frame of agent j>"
                    },
                    
                    "prob": <agent i's belief over this M_j>
                }
            ],

            # Agent i's belief over theta_j
            "Theta_j": {
                "theta/n": <i's belief over frame n of agent j>,
                .
                .
                .
                "theta/1": <i's belief over frame 1 of agent j>,
                "theta/0": <i's belief over frame 0 of agent j>
            },

            # Agent i's belief over S
            "<state variable 1>": {
                "<state value 1>": <probability>,
                "<state value 2>": <probability>
            }
        },

        # Agent j's belief over S
        "beliefJ": {
            "<state variable 1>": {
                "<state value 1>": <probability>,
                "<state value 2>": <probability>
            }
        },
        
        # Actual state
        "state": {
            "<state variable 1>": {
                "<state value 1>": <probability>,
                "<state value 2>": <probability>
            }
        },

        "Theta_j": <true frame of agent j>,
        "Ai": "<action taken by agent i>",
        "Aj": "<action taken by agent j>",
        "Oi": "i's observation",
        "Oj": "j's observation",
        "ERi": <i's expected reward>,
        "ERj": <j's expected reward>
    },

    {
        <belief traces for next interaction step>
    },

    {
        <belief traces for next interaction step>
    },

    .
    .
    .
]
```

For the tiger problem example above, the json trace look like this:
```json
[
  {
    "beliefI": {
      "M_j": [
        {
          "model": {
            "belief_j": {
              "tiger-location": {
                "tiger-left": 0.5,
                "tiger-right": 0.5
              }
            },
            "A_j": "listen",
            "Theta_j": "theta/1"
          },
          "prob": 0.33333334
        },
        {
          "model": {
            "belief_j": {
              "tiger-location": {
                "tiger-left": 0.5,
                "tiger-right": 0.5
              }
            },
            "A_j": "listen",
            "Theta_j": "theta/2"
          },
          "prob": 0.33333334
        },
        {
          "model": {
            "belief_j": {
              "tiger-location": {
                "tiger-left": 0.5,
                "tiger-right": 0.5
              }
            },
            "A_j": "listen",
            "Theta_j": "theta/0"
          },
          "prob": 0.33333334
        }
      ],
      "Theta_j": {
        "theta/1": 0.33333334,
        "theta/2": 0.33333334,
        "theta/0": 0.33333334
      },
      "tiger-location": {
        "tiger-left": 0.5,
        "tiger-right": 0.5
      }
    },
    "beliefJ": {
      "tiger-location": {
        "tiger-left": 0.5,
        "tiger-right": 0.5
      }
    },
    "state": {
      "tiger-location": {
        "tiger-left": 0.0,
        "tiger-right": 1.0
      }
    },
    "Theta_j": 2,
    "Ai": "listen",
    "Aj": "listen",
    "Oi": "[growl-right, silence]",
    "Oj": "[growl-right]",
    "ERi": 0.8530685000000006,
    "ERj": -1.7139059999999995
  },

  {
      <belief traces for next interaction step>
  },

  {
      <belief traces for next interaction step>
  },

  .
  .
  .
]
```