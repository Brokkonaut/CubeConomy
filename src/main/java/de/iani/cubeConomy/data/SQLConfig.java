package de.iani.cubeConomy.data;

import org.bukkit.configuration.ConfigurationSection;

public class SQLConfig
{
    private String host = "localhost";

    private String user = "CHANGETHIS";

    private String password = "CHANGETHIS";

    private String database = "CHANGETHIS";

    private String tableprefix = "cubeconomy";

    public SQLConfig(ConfigurationSection section)
    {
        if (section != null)
        {
            host = section.getString("host", host);
            user = section.getString("user", user);
            password = section.getString("password", password);
            database = section.getString("database", database);
            tableprefix = section.getString("tableprefix", tableprefix);
        }
    }

    public String getHost()
    {
        return host;
    }

    public String getUser()
    {
        return user;
    }

    public String getPassword()
    {
        return password;
    }

    public String getDatabase()
    {
        return database;
    }

    public String getTablePrefix()
    {
        return tableprefix;
    }
}
