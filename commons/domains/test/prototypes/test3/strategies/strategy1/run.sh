echo Strategy >> $1/strategy1.out
echo file:$0 >> $1/strategy1.out
echo param1:$1 >> $1/strategy1.out
echo param2:$2 >> $1/strategy1.out
echo param3:$3 >> $1/strategy1.out
echo param4:$4 >> $1/strategy1.out
cd "${0%/*}"
javac --release 8 ExhaustCPU.java
java ExhaustCPU 30 $2 $3 $4