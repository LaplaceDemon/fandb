package io.github.laplacedemon.fandb;

public class DBConfig {
    public static class Builder {
        private String path;
        
        public Builder path(final String path) {
            this.path = path;
            return this;
        }
        
        public DBConfig build() {
            DBConfig dbConfig = new DBConfig();
            dbConfig.path = path;
            return dbConfig;
        }
    }
    
    public static Builder newBuilder() {
        return new Builder();
    }
    
    private String path;

    public String getPath() {
        return path;
    }
}
