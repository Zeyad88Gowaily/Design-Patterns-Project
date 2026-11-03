package nonprofit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SupabaseConfigTest {
    @Test
    void readsValuesFromEnvFile(@TempDir Path dir) throws Exception {
        Path env = dir.resolve(".env");
        Files.writeString(env, "# comment\nSUPABASE_URL=\"https://p.supabase.co\"\nSUPABASE_ANON_KEY = key123\n");
        SupabaseConfig config = SupabaseConfig.load(env, Collections.<String, String>emptyMap());
        assertEquals("https://p.supabase.co", config.getUrl());
        assertEquals("key123", config.getApiKey());
    }

    @Test
    void environmentVariablesOverrideTheFile(@TempDir Path dir) throws Exception {
        Path env = dir.resolve(".env");
        Files.writeString(env, "SUPABASE_URL=https://file.supabase.co\nSUPABASE_ANON_KEY=filekey\n");
        Map<String, String> real = new HashMap<String, String>();
        real.put("SUPABASE_URL", "https://env.supabase.co");
        SupabaseConfig config = SupabaseConfig.load(env, real);
        assertEquals("https://env.supabase.co", config.getUrl());
        assertEquals("filekey", config.getApiKey());
    }

    @Test
    void missingValuesAreReported(@TempDir Path dir) {
        assertThrows(IllegalStateException.class,
                () -> SupabaseConfig.load(dir.resolve(".env"), Collections.<String, String>emptyMap()));
    }

    @Test
    void acceptsTheNextJsStyleNames(@TempDir Path dir) throws Exception {
        Path env = dir.resolve(".env");
        Files.writeString(env, "NEXT_PUBLIC_SUPABASE_URL=https://n.supabase.co\n"
                + "NEXT_PUBLIC_SUPABASE_PUBLISHABLE_KEY=pubkey\n");
        SupabaseConfig config = SupabaseConfig.load(env, Collections.<String, String>emptyMap());
        assertEquals("https://n.supabase.co", config.getUrl());
        assertEquals("pubkey", config.getApiKey());
    }
}
