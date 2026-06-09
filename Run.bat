@echo off
cd /d "%~dp0"
set PATH=C:\Program Files (x86)\jdk-26.0.1\bin;%PATH%

javac -d out -encoding UTF-8 src/model/Coordinate.java src/model/CellStatus.java src/model/Ship.java src/model/ShotResult.java src/model/Board.java src/ui/ConsoleRenderer.java src/ui/InputParser.java src/bot/SmartBot.java src/game/Player.java src/game/GameVsBot.java src/logger/GameLogger.java src/admin/AdminMenu.java src/admin/GameReplayer.java src/Main.java src/model/Direction.java

if errorlevel 1 (
    pause
    exit /b 1
)

java -cp out Main
pause