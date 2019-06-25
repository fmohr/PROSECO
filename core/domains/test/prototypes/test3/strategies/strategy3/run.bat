echo Strategy >> %1/strategy3.out
echo file:%~dp0%~nx0 >> %1/strategy3.out
echo param1:%1 >> %1/strategy3.out
echo param2:%2 >> %1/strategy3.out
echo param3:%3 >> %1/strategy3.out
echo param4:%4 >> %1/strategy3.out
cd %~dp0
javac --release 8 FilesNoFScore.java
java FilesNoFScore %1 %2 %3 %4