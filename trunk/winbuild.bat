@echo off
REM FidoCadJ build tools for Windows
set REV=20131026

if "%1" == "run" goto :FOR_RUN
if "%1" == "clean" goto :FOR_CLEAN
if "%1" == "compile" goto :FOR_COMPILE
if "%1" == "rebuild" goto :FOR_REBUILD
if "%1" == "force" goto :FOR_FORCE

echo FidoCadJ build tools for Windows. (rev.%REV%)
echo.
echo Usage: winbuild [run ^| clean ^| compile ^| force ^| rebuild]
echo. 
echo        run      Launch application.
echo        clean    Delete all class files under .\bin
echo        compile  Compile FidoMain.java and related sources.
echo                 This option refers to the compiler resolving dependency.
echo        force    Compile source files of all directories. 
echo                 The compiler starts on each source file. Very slow.
echo        rebuild  Clean and compile.
                     
goto :END

:FOR_RUN
  call :run
  goto :end

:FOR_COMPILE
  call :compile
  goto :end

:FOR_CLEAN
  call :clean
  goto :end

:FOR_REBUILD
  call :clean
  call :compile
  goto :end

:FOR_FORCE
  call :force_compile
  goto :end  

:COMPILE
  rem javac  -g -Xlint:unchecked -O -sourcepath src -classpath bin -source 1.5 -target 1.5 ./src/FidoMain.java -d bin
  rem javac  -g -Xlint:unchecked -O -sourcepath src -classpath bin -source 1.5 -target 1.5 ./src/FidoReadApplet.java -d bin
  rem javac  -Xlint:unchecked -g -O -sourcepath src -classpath bin -source 1.5 -target 1.5 ./src/FidoCadApplet.java -d bin
  javac  -g -O -sourcepath src -classpath bin -source 1.5 -target 1.5 ./src/FidoMain.java -d bin
  javac  -g -O -sourcepath src -classpath bin -source 1.5 -target 1.5 ./src/FidoReadApplet.java -d bin
  javac  -g -O -sourcepath src -classpath bin -source 1.5 -target 1.5 ./src/FidoCadApplet.java -d bin
  exit /b


:FORCE_COMPILE
  for /r .\src %%N in (*.java) do (
  	  echo %%N
  	  javac -g -O -sourcepath src -classpath bin -source 1.5 -target 1.5 -d bin %%N
  )
  exit /b
  
:CLEAN
  for /r .\bin %%N in (*.class) do (
  	  echo delete %%N
  	  del %%N
  )
  exit /b

:RUN
  java -classpath ./bin;./jar;./jar/quaqua.jar;./jar/ FidoMain
  exit /b

:END
