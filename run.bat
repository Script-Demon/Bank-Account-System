@echo off
cd /d "%~dp0"
javac Transaction.java BankAccount.java BankSystem.java || (echo Compilation failed. Is a JDK 14+ installed? & pause & exit /b 1)
java BankSystem
pause
