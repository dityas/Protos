Protos is a level 1 factored IPOMDP solver. It uses Jesse Hoey's implementation of the Symbolic Perseus POMDP solver and extends it to solve IPOMDPs.

******

### Usage

#### Online IPOMDP simulator:
Simulates a 2 agent interaction between the I-POMDP agent and a randomly sampled POMDP agent from the I-POMDP frames
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
 -m,--mapomdp                set if the domain is a MAPOMDP domain
 -n <arg>                    look ahead for IPOMDPs / SSGA depth for
                             POMDPs
 -p,--pomdp                  set if the domain is a POMDP domain
 -q,--cyberdec-reactive      use reactive solver
 -r <arg>                    number of symbolic perseus rounds (always 1
                             for IPOMDPs)
 -s,--output <arg>           directory where result files are to be
                             stored. (Should be an existing dir)
 -x,--sim <arg>              run stochastic simulation for given
                             iterations
 -y <arg>                    number of simulation rounds
```
