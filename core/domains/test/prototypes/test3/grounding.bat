echo Grounding >> ../../../../processes/%1/test.out
echo file:%~dp0%~nx0  >> ../../../../processes/%1/test.out
echo param1:%1 >> ../../../../processes/%1/test.out
echo param2:%2 >> ../../../../processes/%1/test.out
echo param3:%3 >> ../../../../processes/%1/test.out
cd %~dp0
javac --release 8 Grounding.java
java Grounding %1 %2 %3