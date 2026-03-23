@echo off
setlocal EnableExtensions EnableDelayedExpansion

title SPK - Backup GitHub

REM =========================================================
REM  CONFIGURACAO
REM =========================================================
set "PROJETO=C:\ws\SPK"
set "BRANCH=main"

REM =========================================================
REM  CABECALHO
REM =========================================================
echo =========================================================
echo                SPK - BACKUP GIT / GITHUB
echo =========================================================
echo Projeto : %PROJETO%
echo Branch  : %BRANCH%
echo =========================================================
echo.

REM =========================================================
REM  VALIDAR PASTA DO PROJETO
REM =========================================================
if not exist "%PROJETO%" (
    echo ERRO: A pasta do projeto nao foi encontrada.
        echo Caminho esperado: %PROJETO%
            echo.
                pause
                    exit /b 1
                    )

cd /d "%PROJETO%"

REM =========================================================
REM  VALIDAR SE E REPOSITORIO GIT
REM =========================================================
if not exist ".git" (
    echo ERRO: A pasta informada nao contem um repositorio Git.
        echo Verifique se o projeto foi iniciado com git init
            echo ou se esta na pasta correta.
                echo.
                    pause
                        exit /b 1
                        )

REM =========================================================
REM  VALIDAR SE GIT ESTA DISPONIVEL
REM =========================================================
git --version >nul 2>&1
if errorlevel 1 (
    echo ERRO: Git nao encontrado no PATH do Windows.
        echo Instale o Git corretamente ou ajuste a variavel PATH.
            echo.
                pause
                    exit /b 1
                    )

REM =========================================================
REM  EXIBIR STATUS ATUAL
REM =========================================================
echo STATUS ATUAL DO REPOSITORIO:
echo ---------------------------------------------------------
git status --short
echo ---------------------------------------------------------
echo.

REM =========================================================
REM  VERIFICAR SE EXISTEM ALTERACOES
REM =========================================================
git diff --quiet
set "HAS_WORKTREE_CHANGES=%errorlevel%"

git diff --cached --quiet
set "HAS_STAGED_CHANGES=%errorlevel%"

git ls-files --others --exclude-standard > "%TEMP%\spk_git_untracked.txt"
for %%A in ("%TEMP%\spk_git_untracked.txt") do set "UNTRACKED_SIZE=%%~zA"
del "%TEMP%\spk_git_untracked.txt" >nul 2>&1

if "%HAS_WORKTREE_CHANGES%"=="0" if "%HAS_STAGED_CHANGES%"=="0" if "%UNTRACKED_SIZE%"=="0" (
    echo Nenhuma alteracao encontrada para enviar ao GitHub.
        echo.
            pause
                exit /b 0
                )

REM =========================================================
REM  SOLICITAR MENSAGEM DO COMMIT
REM =========================================================
set "MSG="
set /p MSG=Digite a mensagem do commit:

if not defined MSG (
    echo.
        echo ERRO: A mensagem do commit nao pode ficar em branco.
            echo Operacao cancelada.
                echo.
                    pause
                        exit /b 1
                        )

REM =========================================================
REM  ADICIONAR ARQUIVOS
REM =========================================================
echo.
echo [1/4] Adicionando arquivos...
git add .

if errorlevel 1 (
    echo.
        echo ERRO ao executar git add .
            echo.
                pause
                    exit /b 1
                    )

REM =========================================================
REM  COMMIT
REM =========================================================
echo.
echo [2/4] Gerando commit...
git commit -m "%MSG%"

if errorlevel 1 (
    echo.
        echo ERRO ao executar o commit.
            echo Verifique se ha algo para commitar ou se existe algum problema no Git.
                echo.
                    pause
                        exit /b 1
                        )

REM =========================================================
REM  PUSH
REM =========================================================
echo.
echo [3/4] Enviando para o GitHub...
git push origin %BRANCH%

if errorlevel 1 (
    echo.
        echo ERRO ao executar o push para o GitHub.
            echo Verifique conexao, autenticacao, branch e permissoes.
                echo.
                    pause
                        exit /b 1
                        )

REM =========================================================
REM  FINALIZACAO
REM =========================================================
echo.
echo [4/4] Backup concluido com sucesso.
echo.
git log -1 --oneline
echo.
echo =========================================================
echo                PROCESSO FINALIZADO
echo =========================================================
echo.
pause
exit /b 0
