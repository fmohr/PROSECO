echo Strategy >> %1/test.out
echo file:%~dp0%~nx0 >> %1/test.out
echo param1:%1 >> %1/test.out
echo param2:%2 >> %1/test.out
echo param3:%3 >> %1/test.out
echo param4:%4 >> %1/test.out
echo 0.5 > %3/score