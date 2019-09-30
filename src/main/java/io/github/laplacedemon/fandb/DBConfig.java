package io.github.laplacedemon.fandb;

public class DBConfig {
    public static class Builder {
        private String path;
        
        public DBConfig build() {
            DBConfig dbConfig = new DBConfig();
            dbConfig.path = path;
            return dbConfig;
        }
    }
    
    private String path;

    public String getPath() {
        return path;
    }
}
