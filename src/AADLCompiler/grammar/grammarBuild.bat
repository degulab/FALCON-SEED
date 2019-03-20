@echo off
REM
REM  Licensed under the Apache License, Version 2.0 (the "License");
REM  you may not use this file except in compliance with the License.
REM  You may obtain a copy of the License at
REM
REM      http://www.apache.org/licenses/LICENSE-2.0
REM
REM  Unless required by applicable law or agreed to in writing, software
REM  distributed under the License is distributed on an "AS IS" BASIS,
REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM  See the License for the specific language governing permissions and
REM  limitations under the License.
REM
REM  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
REM  <author> Yasunari Ishizuka (PieCake,Inc.)
REM  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
REM  <author> Yuji Onuki (Statistics Bureau)
REM  <author> Shungo Sakaki (Tokyo University of Technology)
REM  <author> Akira Sasaki (HOSEI UNIVERSITY)
REM  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
REM
REM ANTLR build for AADL grammar
REM created by Y.Ishizuka(PieCake,Inc.)
REM last update : 2008.05.13

echo --- ANTLR build AADL grammar... ---
echo.

java -Xmx200M -cp ..\lib\antlr-3.0.1.jar;..\lib\antlr-2.7.7.jar;..\lib\stringtemplate-3.1b1.jar org.antlr.Tool -o build AADL.g

echo.
echo --- build completed! ---
echo.
pause

