import os
import re
import subprocess
import time


def yara_scan(file_path):
    start_time = time.perf_counter()
    try:
        absolute_path = os.path.abspath(file_path)
        filename = os.path.basename(absolute_path)

        ext_match = re.search(r'\.([^.]+)$', filename)
        extension = ext_match.group(1) if ext_match else ""

        command = [
            "yara",
            "-d", f"filepath={absolute_path}",
            "-d", f"filename={filename}",
            "-d", f"extension={extension}",
            "yara_rules/index.yar",
            absolute_path,
        ]

        yara_result = subprocess.run(command, capture_output=True, text=True)
        elapsed_ms = (time.perf_counter() - start_time) * 1000

        if yara_result.returncode == 0:
            matches = [line for line in yara_result.stdout.splitlines() if line.strip()]
            return {
                "status": "scanned",
                "matches": matches,
                "error": None,
                "elapsed_ms": round(elapsed_ms, 3),
            }

        return {
            "status": "error",
            "matches": None,
            "error": yara_result.stderr.strip() or "Unknown error",
            "elapsed_ms": round(elapsed_ms, 3),
        }

    except Exception as e:
        elapsed_ms = (time.perf_counter() - start_time) * 1000
        print(f"Error executing yara scan: {str(e)}")
        raise