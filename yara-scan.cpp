#include <chrono>
#include <cstdio>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <stdexcept>
#include <string>
#include <vector>

namespace fs = std::filesystem;

struct YaraResult {
    std::string status;
    std::vector<std::string> matches;
    std::string error;
    long long elapsedMs = 0;
};

YaraResult yaraScan(const std::string& filePath) {
    YaraResult result;
    auto start = std::chrono::steady_clock::now();

    try {
        fs::path p = fs::absolute(filePath);
        std::string absolutePath = p.string();
        std::string filename = p.filename().string();
        std::string extension = p.extension().string();
        if (!extension.empty() && extension[0] == '.') {
            extension = extension.substr(1);
        }

        std::string command = "yara -d filepath=\"" + absolutePath +
                              "\" -d filename=\"" + filename +
                              "\" -d extension=\"" + extension +
                              "\" yara_rules/index.yar \"" + absolutePath + "\" 2>&1";

        char buffer[128];
        std::string commandOutput;

        FILE* pipe = popen(command.c_str(), "r");
        if (!pipe) {
            throw std::runtime_error("popen() failed to execute command.");
        }

        while (fgets(buffer, sizeof(buffer), pipe) != nullptr) {
            commandOutput += buffer;
        }

        int exitCode = pclose(pipe);
        result.elapsedMs = std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::steady_clock::now() - start).count();

        if (exitCode == 0) {
            result.status = "scanned";

            std::string line;
            for (char c : commandOutput) {
                if (c == '\n') {
                    if (!line.empty()) {
                        result.matches.push_back(line);
                        line.clear();
                    }
                } else {
                    line += c;
                }
            }
            if (!line.empty()) result.matches.push_back(line);

            result.error = "";
        } else {
            result.status = "error";
            result.error = commandOutput;
        }

    } catch (const std::exception& e) {
        result.elapsedMs = std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::steady_clock::now() - start).count();
        std::cerr << "Error executing yara scan: " << e.what() << std::endl;
        throw;
    }
    return result;
}

int main(int argc, char* argv[]) {
    if (argc < 2) {
        std::cerr << "Uso: ./yara-scan <arquivo>\n";
        return 1;
    }

    YaraResult result = yaraScan(argv[1]);

    std::cout << "status: " << result.status << "\n";
    std::cout << "elapsed_ms: " << result.elapsedMs << "\n";
    std::cout << "matches: " << result.matches.size() << "\n";
    for (const auto& match : result.matches) {
        std::cout << match << "\n";
    }
    if (!result.error.empty()) {
        std::cout << "error: " << result.error << "\n";
    }

    return 0;
}