::=============================================================
:: Task 7 - SQL
:: This should be at the same folder as University.jar
:: This is for executing University application
::=============================================================

@ECHO OFF
set file_name=University.jar

:: Check if .jar file available
IF NOT EXIST %~dp0%file_name% (
ECHO Error: University.jar is not found
EXIT /B 2
)


java -jar %~dp0%file_name% %*

