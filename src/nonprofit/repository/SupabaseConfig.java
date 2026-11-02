package nonprofit.repository;

public class SupabaseConfig {
    private final String url;
    private final String apiKey;

    public SupabaseConfig(String url, String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
    }

    public static SupabaseConfig fromEnvironment() {
        String url = System.getenv("SUPABASE_URL");
        String key = System.getenv("SUPABASE_ANON_KEY");
        if (url == null || url.trim().isEmpty() || key == null || key.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Set SUPABASE_URL and SUPABASE_ANON_KEY environment variables before starting MissionOS.");
        }
        return new SupabaseConfig(url, key);
    }

    public String getUrl() {
        return url;
    }

    public String getApiKey() {
        return apiKey;
    }
}
