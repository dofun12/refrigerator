cmd.exe /c mvn clean install package -DskipTests
mkdir target\dependency
cd target\dependency
jar -xf ../*.jar
cd ..\..