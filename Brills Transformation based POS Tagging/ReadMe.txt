Transformation Based POS Tagging:
Given a POS-tagged training file, HW2_F17_NLP6320_POSTaggedTrainingSet.txt, that has been tagged with POS tags from the Penn Treebank POS
tagset.
Use this POS tagged file to:
  a. Create a unigram model containing the most probable POS tag for each word in the corpus’s vocabulary. Note: compute this probability 
      by considering each word in isolation. Do NOT use any context (i.e. previous words or tags) for compute the most probable POS tag for
      a word.
  b. Brill’s transformation rules: Implement Brill’s transformation-based POS tagging algorithm using ONLY the previous word’s tag to 
      create transformation rules.
  c. Apply model (a) and (b) on the sentence below, and show the difference in error rates.

Sentence: The president wants to control the board 's control
POS Tagged Sentence: The_DT president_NN wants_VBZ to_TO control_VB the_DT board_NN 's_POS control_NN

In order to run the program, first execute "Brills.java" with the following files as a command line arguments:
1. File containing the tagged corpus
2. File containing the test sentence (not tagged)
3. File containing the tagged test sentence.

Output is in "Output.txt"
