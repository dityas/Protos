# Protos
## A factored I-POMDP solver

Protos is a factored IPOMDP solver developed at [THINC Lab @ UGA](http://thinc.cs.uga.edu/). It uses Jesse Hoey's implementation of the symbolic Perseus and with some modifications, extends it to I-POMDPs.

:heavy_exclamation_mark: The solver has only yet been tested on Linux systems.
Running it on Windows might cause problems because the paths are hardcoded
in Linux format.

:heavy_exclamation_mark: The solver works on Java 15 and above. Please use a
Coretto VM or a GraalVM build of JVM. This might seriously help with
performance. I haven't performed any formal benchmarks though.

:heavy_exclamation_mark: Protos is constantly being developed and likely to
undergo major changes as new features are added. If you are using this solver
for research work, I would highly recommend cloning or forking the repo and having a local copy.

**If you use this solver in your research, please cite the following paper.**
```
Shinde, Aditya, Prashant Doshi, and Omid Setayeshfar. "Cyber Attack Intent
Recognition and Active Deception using Factored Interactive POMDPs." Proceedings
of the 20th International Conference on Autonomous Agents and MultiAgent
Systems. 2021.
```
```
@inproceedings{shinde2021cyber,
    title={Cyber Attack Intent Recognition and Active Deception using Factored
    Interactive POMDPs},
    author={Shinde, Aditya and Doshi, Prashant and Setayeshfar, Omid},
    booktitle={Proceedings of the 20th International Conference on Autonomous
    Agents and MultiAgent Systems},
    pages={1200--1208},
    year={2021}
}
```

******


### Usage
#### Building the solver

Run the gradle wrapper
```
$ ./gradlew shadowJar
```

The solver should build to a JAR file  `build/libs/Protos-all.jar` 

#### Running the solver
```
java -jar Protos-all.jar -d <domain_file>
```

******

### Domain file format
The I-POMDP domain file follows an extension of the SPUDD format for representing ADDs in plaintext. Currently, the solver can only solve 2-agent interactions.


#### Example

Here is the domain file for a level 2 multi-agent tiger problem. The file
can be found in the `domains` directory and run directly to get a policy tree
for the L2 tiger I-POMDP. The policy tree will be written to a file in dot
format for graphviz

```
-- Lines beginning with '--' are single line comments

-- Any domain file should first define all the random variables that are going
-- to be used for the problem.
-- In this case, for the L2 multi-agent tiger problem, we will begin by defining
-- the physical state variable for the tiger location states.
(defvar TigerLoc (TL TR))

-- In the multi-agent tiger problem, there are two agents. Let's call them agent
-- I and agent J. Let's assume that J is at level 0 and level 2 and I at level 1
-- Let's define a model variable for agent I at L1 modeling J at L0. Let's begin
-- with 3 initial models. We will define these models later.
(defvar agent_j (m0 m1 m2))

-- Now, let's define the model variable for J at L2 modeling I at L1.
(defvar agent_il1 (m0 m1))

-- I-POMDPs allow agents to model opponents with different frames. These frames
-- can also be represented as random variables. In this example, we will assume
-- only a single frame. However, the solver does support multiple frames at all
-- levels.
(defvar Frame_jl1 (frame1))
(defvar Frame_il2 (frame1))

-- Add a random variable for agent I's actions
(defvar Agent_actions (OL OR L))

-- Another one for agent J's actions
(defvar Agentj_actions (OL OR L))

-- Random variable for J's observations
(defvar Growl_j (GL GR))
(defvar Creak_j (CL CR SL))

-- Agent I's observations
(defvar Growl (GL GR))
(defvar Creak (CL CR SL))


-- Initialize agent I's L1 model variable representing J's L0 models
-- The three models that we defined on top can now be populated with actual
-- beliefs. In this case, we start we 3 point beliefs. One uniform distribution,
-- and 2 beliefs with 85% probability of the tiger being on either side.
-- The format for defining models is,
-- (<frame_name>        (<model_name>   (<DD>)))
-- more details and tutorial will be provided in an accompanying tutorial. But
-- for now, here's the initialization of the L1 model variable.
(initmodelvar agent_j
    (frames Frame_jl1)
    (
	    (frame1 	(m0 	(0.5)))

	    (frame1 	(m1 	(
		    (TigerLoc 
			    (TL 	(0.85))
			    (TR 	(0.15))
		    )		
		)))

	    (frame1 	(m2 	(
                (TigerLoc
                    (TL         (0.15))
                    (TR         (0.85))
                )
            )))
	)
)


-- DD's for transitions, observations, beliefs and rewards
-- Common initial belief for all agents
(defdd initLoc
    (TigerLoc 	UNIFORM)
)

-- Transition if anyone opens door
(defdd openDoorDD
    (TigerLoc' UNIFORM)
)

-- J's L0 assumption over I's action distribution.
-- Since agent J at L0 cannot directly model agent I at L1, it will assume a
-- static distribution over I's possible actions.
(defdd aIDist
    (Agent_actions
	(OL 	(0.005))
	(OR 	(0.005))
	(L 	(0.99))
    )
)

-- J's observation function for listening to growls
(defdd growlObsDD
    (TigerLoc'
	(TL
    	    (Growl_j'
		(GL 	(0.85))
		(GR 	(0.15))
            )
	)
			
	(TR
	    (Growl_j'
		(GL 	(0.15))
		(GR 	(0.85))
	    )
	)
    )
)

-- I's observation function for listening to creaks
(defdd iListensCreak
    (Agentj_actions
	(OL
    	    (Creak'
		(CL 	(0.9))
		(CR 	(0.05))
		(SL 	(0.05))
	    )
	)
		
	(OR
	    (Creak'
		(CL 	(0.05))
		(CR 	(0.9))
		(SL 	(0.05))
	    )
	)
		
	(L
	    (Creak'
		(CL 	(0.05))
		(CR 	(0.05))
		(SL 	(0.9))
	    )
	)
    )
)

-- I's observation function for listening to growls
(defdd iListensGrowl
    (TigerLoc'
	(TL 	(Growl'	(GL	(0.85)) 	(GR 	(0.15))))
	(TR 	(Growl'	(GR	(0.85)) 	(GL 	(0.15))))
    )
)

-- I's joint action transition for the listen action
(defdd iListensDD
    (Agentj_actions
	(OL
            (TigerLoc' UNIFORM)
	)
		
	(OR
	    (TigerLoc' UNIFORM)
	)
		
	(L
	    (TigerLoc
		(TL 	(TigerLoc' TL))
		(TR 	(TigerLoc' TR))
	    )
	)
    )
	
)

-- J's joint action transition for the listen action
(defdd jListensDD
    (Agent_actions
	(OL 	(TigerLoc' UNIFORM))
	(OR 	(TigerLoc' UNIFORM))
	(L 		(SAME TigerLoc))
    )
)

-- For the transition and the observation functions, we use DBNs (Dynamic
-- Bayesian Networks). A detailed tutorial will explain how to define 
-- these custom DBNs, but for now, here's the DBN for the open action 
-- taken by agent I
-- I's DBN for open action
(defdbn actionIOpen
    (TigerLoc 	(TigerLoc' 	UNIFORM))
    (Growl 		(Growl' 	UNIFORM))
    (Creak 		(Creak' 	UNIFORM))	
)

-- I's DBN for listen action
(defdbn actionIListen
    (TigerLoc 	(iListensDD))
    (Growl 		(iListensGrowl))
    (Creak 		(iListensCreak))
)

-- J's DBNs
-- J's L0 DBN for opening doors
(defdbn actionOpenAny
    (TigerLoc 	(openDoorDD))
    (Growl_j 	(Growl_j' UNIFORM))
)

-- J's L0 DBN for listening
-- Notice that the Agent_actions var is summed out after multiplying with
-- assumed distribution of I's actions. This is mainly because at L0, agent J
-- does not explicitly model agent I
(defdbn actionL
    (TigerLoc 	(# (Agent_actions) (aIDist * jListensDD)))
    (Growl_j 	(growlObsDD))
)

-- J's level N DBN for opening doors
(defdbn actionJOpen
    (TigerLoc   (TigerLoc' UNIFORM))
    (Growl_j    (Growl_j' UNIFORM))
    (Creak_j    (Creak_j' UNIFORM))
)

-- J's level N DBN for listening

(defdd jListensCreakDD

    (Agent_actions
        (OL
            (Creak_j'
                (CL     (0.9))
                (CR     (0.05))
                (SL     (0.05))
            )
        )

        (OR
            (Creak_j'
                (CL     (0.05))
                (CR     (0.9))
                (SL     (0.05))
            )
        )

        (L
            (Creak_j'
                (CL     (0.05))
                (CR     (0.05))
                (SL     (0.9))
            )
        )
    )
)

(defdbn actionJListens
    (TigerLoc   (jListensDD))
    (Growl_j    (growlObsDD))
    (Creak_j    (jListensCreakDD))

)

-- Define agents
-- Agnet J L0
(defpomdp agentJ
    (S 	
        (TigerLoc)
    )

    (O
        (Growl_j)
    )

    (A 	Agentj_actions)
        
    (dynamics
        (OL 	(actionOpenAny))
        (OR 	(actionOpenAny))
        (L 		(actionL))
    )
       
    (R
        (OL 	(TigerLoc 
                    (TL 	(-100))
                    (TR 	(10))
        ))
        
        (OR		(TigerLoc
                    (TL 	(10))
                    (TR 	(-100))
        ))
            
        (L 		(-1))
    )
        
    (discount 0.9)	
)

-- Agent I L1
(defipomdp agentI
    (S 	
        (TigerLoc)
    )

    (O
        (Growl Creak)
    )

    (A 	Agent_actions)
    (Aj Agentj_actions)
    (Mj agent_j)
    
    (Thetaj Frame_jl1 	
    	(frame1 agentJ)
    )
        
    (dynamics
        (OL 	(actionIOpen))
        (OR 	(actionIOpen))
        (L 	(actionIListen))
    )
       
    (R
        (OL 	
            (TigerLoc 
                (TL 	(-100))
                (TR 	(10))
            )
        )
        
        (OR		
        			
            (TigerLoc
                (TR 	(-100))
                (TL 	(10))
            )
       	)
            
        (L 		(-1))
    )
        
    (discount 0.9)
    (H 4)	
)

-- Initialize models for L2 model var
(initmodelvar agent_il1
    (frames Frame_il2)
    (
	(frame1 	(m0 	(agent_j m0)))
	(frame1		(m1 	(agent_j m1)))
    )
)

-- Agent J L2
(defipomdp agentJl2
    (S 	
        (TigerLoc)
    )

    (O
        (Growl_j Creak_j)
    )

    (A 	Agentj_actions)
    (Aj Agent_actions)
    (Mj agent_il1)
    (Thetaj Frame_il2 	
    	(frame1 agentI)
    )
        
    (dynamics
        (OL 	(actionJOpen))
        (OR 	(actionJOpen))
        (L 		(actionJListens))
    )
       
    (R
        (OL 	
        	(TigerLoc 
                (TL 	(-100))
                (TR 	(10))
        	)
        )
        
        (OR		
        			
            (TigerLoc
                (TR 	(-100))
                (TL 	(10))
            )
       	)
            
        (L 		(-1))
    )
        
    (discount 0.9)
    (H 4)	
)

-- Initial belief of the L2 agent
(defdd initL2agentJ
    (
        (agent_il1
            (m0 (0.5))
            (m1 (0.5))
        )
        * (0.5)
    )
)

-- Solvers
(defpbvisolv ipomdp agentJl2Solver)

-- run the solver
-- To run the solver, the syntax is:
-- (defpol <policy_name> = solve <solver_name> (<list of initial beliefs>)
-- <agent_name> <backups> <depth of belief search>)

(run
    (defpol l2pol = solve agentJl2Solver ((initL2agentJ)) agentJl2 100 3)
    (poltree (initL2agentJ) agentJl2 l2pol 3)
)
```
