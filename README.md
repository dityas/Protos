Protos is a level 1 factored IPOMDP solver developed at [THINC Lab @ UGA](http://thinc.cs.uga.edu/). It uses Jesse Hoey's implementation of the Symbolic Perseus POMDP solver and extends it to solve IPOMDPs. 

******

## Online IPOMDP simulator:
Simulates a 2 agent interaction between the I-POMDP agent and a randomly sampled POMDP agent from the I-POMDP frames.

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

Run:

```
java -Xms55g -Xmx60g -cp Protos.jar thinclab.executables.RunSimulations -b 100 
-d /path/to/domain_file/tiger.L1.F3.agnostic.domain -s /path/to/results/dir/ 
-n 5 --ipomdp --sim 10 -y 1 -ssga
```

The output directory will contain:
- policy graph of every solved POMDP frame.
- policy tree of every solved POMDP frame (not all POMDP policies can be compressed into finite policy graphs).
- human readable summary of the simulation
- complete trace of the simulation in json format.