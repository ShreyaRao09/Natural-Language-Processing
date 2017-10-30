import sys

#Function to implement Viterbi Algorithm
def Viterbi(obsList):
    N=2
    T=len(obsList)
    a=[[0.8,0.2],[0.7,0.3],[0.4,0.6]]
    b=[[0.2,0.5],[0.4,0.4],[0.4,0.1]]
    viterbi=[[0 for j in range(T+1)] for i in range(N+2)]
    backPointer=[[0 for j in range(T+1)] for i in range(N+1)]

    for s in range(1,N+1):
        viterbi[s][0]=a[0][s-1]*b[obsList[0]-1][s-1]
        backPointer[s-1][0]=0

    for t in range(1,T):
        for s in range(1,N+1):
            maxV=viterbi[1][t-1]*a[1][s-1]*b[obsList[t]-1][s-1]
            maxB=1
            for k in range(2,N+1):
                val= viterbi[k][t-1]*a[k][s-1]*b[obsList[t]-1][s-1]
                if(maxV<val):
                    maxV=val
                    maxB=k
            viterbi[s][t]=maxV
            backPointer[s-1][t]=maxB

    maxV=viterbi[1][T-1]
    maxB=1
    for k in range(2,N+1):
        val=viterbi[k][T-1]
        if(maxV<val):
            maxV=val
            maxB=k
            print(maxV)
    viterbi[N+1][T]=maxV
    backPointer[N][T]=maxB

    q=N
    s=""
    # s+=str(viterbi[q+1][T])+"\n"
    for t in range(T,0,-1):
        q=backPointer[q][t]
        if(q==1):
            s = 'Hot ' + s
        elif(q==2):
            s = 'Cold ' + s
        q-=1
    s ="Sequence is : "+ s + "\nProbability of weather sequence : "+str(viterbi[N + 1][T]) + "\n";
    return s


file=open(sys.argv[1],"r")
obsList=[]
for line in file:
    for o in line.split(" "):
        obsList.append(int(o))

print(Viterbi(obsList))
