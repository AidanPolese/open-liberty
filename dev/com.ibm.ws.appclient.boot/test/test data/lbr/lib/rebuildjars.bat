set JAR=c:\java60\bin\jar

for /r %%i in (*.jar) do (
  if exist %%~ni.mf (
  echo "%%~ni" 
  mkdir "%%~ni"
  cd %%~ni
  %JAR% xvf "%%i"
  cd ..
  del "%%i"
  %JAR% cvmf "%%~ni.mf" "%%i" -C "%%~ni" .  
  del /s/q "%%~ni"
  rmdir /s/q "%%~ni"
  )
  )