HMM Decoding: Viterbi Algorithm: 
Programmatically implement the Viterbi algorithm and run it with the HMM given below to compute the most likely weather sequence and
probability for a given observation sequence.
Example observation sequences: 331, 122313, 331123312, etc.

HMM details are as follows:
States:
  Hot, Cold

Transition Probabilities:
           Hot   Cold
    Hot    0.7   0.3
    Cold   0.4   0.6
    
Initial Probabilities (Pi):
  Hot: 0.8    Cold: 0.2
  
Observation Likelihood:
    B1              B2
    P(1|Hot)=0.2    P(1|Cold)=0.5
    P(2|Hot)=0.4    P(2|Cold)=0.4
    P(3|Hot)=0.4    P(3|Cold)=0.1
    
    
Is answered using Python. Kindly, either copy the concerned files to a new project in a suitable IDE like Pycharm, or you could
execute it on the command line.

In order to run the program, first execute "Viterbi.py" with the file containing the Observation list ("Observations.txt") as a 
command line argument.
The program takes observations list delimited with spaces.

Output is displayed on the console
