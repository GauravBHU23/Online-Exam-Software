@echo off
start "" "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\javaw.exe" --module-path "D:\Exam Server\java-client\javafx-sdk\lib" --add-modules javafx.controls,javafx.web,javafx.swing -Djava.library.path="D:\Exam Server\java-client\javafx-sdk\bin" -jar "D:\Exam Server\java-client\exam-client.jar"
