@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@echo off
setlocal

set MAVEN_WRAPPER_PROPERTIES=.mvn\wrapper\maven-wrapper.properties
set WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
set DEFAULT_WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar

set "WRAPPER_URL=%DEFAULT_WRAPPER_URL%"
set "WRAPPER_SHA256="

if exist "%MAVEN_WRAPPER_PROPERTIES%" (
  for /F "usebackq tokens=1,* delims==" %%A in ("%MAVEN_WRAPPER_PROPERTIES%") do (
    if /I "%%A"=="wrapperUrl" set "WRAPPER_URL=%%B"
    if /I "%%A"=="wrapperSha256Sum" set "WRAPPER_SHA256=%%B"
  )
)

if not exist "%WRAPPER_JAR%" (
  echo Downloading Maven Wrapper jar from: %WRAPPER_URL%
  where powershell >NUL 2>&1
  if %ERRORLEVEL%==0 (
    powershell -NoProfile -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')"
  ) else (
    echo PowerShell is required to download the Maven Wrapper. >&2
    exit /B 1
  )
  @REM Checksum verification (best-effort)
  if not "%WRAPPER_SHA256%"=="" (
    where certutil >NUL 2>&1
    if %ERRORLEVEL%==0 (
      for /f "tokens=1" %%H in ('certutil -hashfile "%WRAPPER_JAR%" SHA256 ^| find /I /V "hash" ^| find /I /V "CertUtil"') do set "CALC_SUM=%%H"
      if /I not "%CALC_SUM%"=="%WRAPPER_SHA256%" (
        echo SHA-256 checksum verification failed for maven-wrapper.jar >&2
        del /Q "%WRAPPER_JAR%" >NUL 2>&1
        exit /B 1
      )
    )
  )
)

@REM Prepare JAVA
if "%JAVA_HOME%"=="" (
  set "JAVACMD=java"
) else (
  set "JAVACMD=%JAVA_HOME%\bin\java.exe"
)

if not exist "%JAVACMD%" (
  echo Error: JAVA_HOME is not defined correctly, or Java not found on PATH. >&2
  exit /B 1
)

@REM Read jvm.config into JVM_CONFIG_MAVEN_PROPS
set "JVM_CONFIG_MAVEN_PROPS="
if exist ".mvn\jvm.config" (
  for /F "usebackq delims=" %%a in (".mvn\jvm.config") do set JVM_CONFIG_MAVEN_PROPS=!JVM_CONFIG_MAVEN_PROPS! %%a
)

set WRAPPER_MAIN=org.apache.maven.wrapper.MavenWrapperMain
set "MAVEN_PROJECTBASEDIR=%CD%"

"%JAVACMD%" %JVM_CONFIG_MAVEN_PROPS% %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_MAIN% %*
endlocal
