# YARA Scanner em múltiplas linguagens

Este projeto contém implementações da mesma função de escaneamento do YARA em Python, JavaScript, Java, C++, e Clojure.

## Requisitos

### 1) Instalar o YARA

No Linux, normalmente é suficiente:

```bash
sudo apt update
sudo apt install yara
```

Se preferir, também pode compilar manualmente a partir do código-fonte do YARA.

### 2) Instalar as linguagens

- Python 3
- Node.js + npm
- Java JDK
- g++ (compilador C++)
- Clojure CLI (`clj` ou `clojure`)

#### Verificando se estão instaladas

```bash
python3 --version
node --version
javac -version
g++ --version
clj -Sdescribe
```

Se o comando `clj` não existir, tente:

```bash
clojure -Sdescribe
```

## Estrutura do projeto

- `yara-scan.py` — função de scan em Python
- `main.py` — executa a análise em Python
- `yara-scan.js` — função de scan em JavaScript
- `main.js` — executa a análise em JavaScript
- `YaraScanner.java` — classe Java com a análise
- `yara-scan.cpp` — código C++ com a análise
- `yara-scan.clj` — função de scan em Clojure
- `main.clj` — executa a análise em Clojure
- `test-files/eicar.com` — arquivo de teste de exemplo
- `yara_rules/` — diretório com as regras YARA
- `yara_rules/index.yar` — índice gerado para incluir todas as regras

## Como executar

### Python

```bash
python3 main.py test-files/eicar.com
```

### JavaScript

```bash
node main.js test-files/eicar.com
```

### Java

```bash
javac YaraScanner.java
java YaraScanner test-files/eicar.com
```

### C++

```bash
g++ -std=c++17 yara-scan.cpp -o yara-scan
./yara-scan test-files/eicar.com
```

### Clojure

Se o comando `clj` estiver disponível:

```bash
clj main.clj test-files/eicar.com
```

Se o comando for `clojure`:

```bash
clojure main.clj test-files/eicar.com
```

## Exemplo de saída esperada

```json
{
  "status": "scanned",
  "matches": [
    "SUSP_Just_EICAR /home/usuario/projeto/test-files/eicar.com"
  ],
  "error": null,
  "elapsed_ms": 894
}
```

## Observações

- O projeto usa uma regra `index.yar` para incluir todas as regras da pasta `yara_rules`.
- O campo `elapsed_ms` mostra quanto tempo a análise levou em milissegundos.
- O arquivo de teste `test-files/eicar.com` foi usado apenas como exemplo de validação.
