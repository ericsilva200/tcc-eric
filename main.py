import importlib.util
import json
import sys
from pathlib import Path


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Uso: python3 main.py <arquivo>")
        sys.exit(1)

    module_path = Path(__file__).with_name("yara-scan.py")
    spec = importlib.util.spec_from_file_location("yara_scan_module", module_path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)

    result = module.yara_scan(sys.argv[1])
    print(json.dumps(result, ensure_ascii=False, indent=2))
