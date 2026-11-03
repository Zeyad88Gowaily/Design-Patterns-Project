package nonprofit.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Connection settings for Supabase. Values come from a {@code .env} file in the working
 * directory (KEY=value lines); real environment variables take precedence over the file.
 */
public class SupabaseConfig {
    private final String url;
    private final String apiKey;

    public SupabaseConfig(String url, String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
    }

    public static SupabaseConfig fromEnvironment() {
        return load(Paths.get(".env"), System.getenv());
    }

    static SupabaseConfig load(Path envFile, Map<String, String> environment) {
        Map<String, String> file = readEnvFile(envFile);
        String url = pick(environment, file, "SUPABASE_URL", "NEXT_PUBLIC_SUPABASE_URL");
        String key = pick(environment, file, "SUPABASE_ANON_KEY", "NEXT_PUBLIC_SUPABASE_PUBLISHABLE_KEY",
                "NEXT_PUBLIC_SUPABASE_ANON_KEY");
        if (url == null || key == null) {
            throw new IllegalStateException("Could not find the Supabase URL and key. Put SUPABASE_URL and "
                    + "SUPABASE_ANON_KEY in the .env file in the folder you run the app from "
                    + "or set them as environment variables. "
                    + "Looked for .env in: " + envFile.toAbsolutePath());
        }
        return new SupabaseConfig(url, key);
    }

    /** First non-empty value among the names; environment variables win over the file. */
    private static String pick(Map<String, String> environment, Map<String, String> file, String... names) {
        for (Map<String, String> source : java.util.Arrays.asList(environment, file)) {
            for (String name : names) {
                String value = source.get(name);
                if (value != null && !value.trim().isEmpty()) {
                    return value.trim();
                }
            }
        }
        return null;
    }

    private static Map<String, String> readEnvFile(Path path) {
        Map<String, String> values = new HashMap<String, String>();
        if (!Files.isRegularFile(path)) {
            return values;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read " + path + ": " + ex.getMessage(), ex);
        }
        for (String line : lines) {
            String trimmed = line.trim();
            int eq = trimmed.indexOf('=');
            if (trimmed.isEmpty() || trimmed.startsWith("#") || eq <= 0) {
                continue;
            }
            String value = trimmed.substring(eq + 1).trim();
            if (value.length() >= 2 && (value.startsWith("\"") && value.endsWith("\"")
                    || value.startsWith("'") && value.endsWith("'"))) {
                value = value.substring(1, value.length() - 1);
            }
            values.put(trimmed.substring(0, eq).trim(), value);
        }
        return values;
    }

    public String getUrl() {
        return url;
    }

    public String getApiKey() {
        return apiKey;
    }
}
