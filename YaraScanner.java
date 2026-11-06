import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YaraScanner {

    public static Map<String, Object> yaraScan(File file) throws Exception {
        Map<String, Object> result = new HashMap<>();
        long startedAt = System.nanoTime();

        try {
            String absolutePath = file.getAbsolutePath();
            String name = file.getName();

            String extension = "";
            int i = name.lastIndexOf('.');
            if (i > 0) {
                extension = name.substring(i + 1);
            }

            List<String> command = new ArrayList<>();
            command.add("yara");
            command.add("-d"); command.add("filepath=" + absolutePath);
            command.add("-d"); command.add("filename=" + name);
            command.add("-d"); command.add("extension=" + extension);
            command.add("yara_rules/index.yar");
            command.add(absolutePath);

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> matches = new ArrayList<>();
            String line;
            while ((line = outReader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    matches.add(line);
                }
            }

            BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorBuilder = new StringBuilder();
            while ((line = errReader.readLine()) != null) {
                errorBuilder.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            double elapsedMs = (System.nanoTime() - startedAt) / 1_000_000.0;

            if (exitCode == 0) {
                result.put("status", "scanned");
                result.put("matches", matches);
                result.put("error", null);
                result.put("elapsed_ms", elapsedMs);
            } else {
                result.put("status", "error");
                result.put("matches", null);
                result.put("error", errorBuilder.toString().trim());
                result.put("elapsed_ms", elapsedMs);
            }

        } catch (Exception e) {
            double elapsedMs = (System.nanoTime() - startedAt) / 1_000_000.0;
            System.out.println("Error executing yara scan: " + e.getMessage());
            result.put("status", "error");
            result.put("matches", null);
            result.put("error", e.getMessage());
            result.put("elapsed_ms", elapsedMs);
            throw e;
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Uso: java YaraScanner <arquivo>");
            System.exit(1);
        }

        File file = new File(args[0]);
        System.out.println(new java.util.HashMap<>(yaraScan(file)));
    }
}
